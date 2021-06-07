package com.gin.pixivcrawler.utils.fileUtils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail.PIXIV_GIF_FULL_NAME;
import static com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail.PIXIV_ILLUST_FULL_NAME;

/**
 * 文件工具类
 * @author bx002
 */
@Slf4j
public class FileUtils {
    /**
     * 列出目录下的所有文件
     * @param rootDir 根目录
     * @param map     保存文件的map
     */
    public static void listFiles(File rootDir, Map<String, File> map) {
        File[] fs = rootDir.listFiles();
        if (fs == null) {
            return;
        }
//        List<File> files = Arrays.stream(fs).filter(f -> !f.getPath().endsWith(".zip")).collect(Collectors.toList());
        List<File> files = Arrays.asList(fs);
        if (files.size() == 0) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                listFiles(file, map);
            } else {


                Matcher matcher = PIXIV_ILLUST_FULL_NAME.matcher(file.getName());
                Matcher matcherU = PIXIV_GIF_FULL_NAME.matcher(file.getName());
                String key = matcher.find() ? matcher.group()
                        : (matcherU.find() ? matcherU.group() : null);
                if (key != null) {
                        if (!file.getName().endsWith("zip")&&!verifyImage(file)) {
                           moveFile(file,"F:/illust/未分类/损坏文件/","损坏");
                            continue;
                        }


                    if (map.containsKey(key)) {
                        File f2 = map.get(key);
                        if (file.length() == f2.length()) {
                            moveFile(file,"F:/illust/未分类/重复文件/","重复");
                        } else {
                            do {
                                key += "_bak";
                            } while (map.containsKey(key));
                            map.put(key, file);
                        }
                    } else {
                        map.put(key, file);
                    }
                }
            }
        }
    }

    /**
     * 移动文件
     * @param fileMap    文件map
     * @param keys       需要移动的文件key
     * @param targetPath 目标路径
     * @return java.util.HashMap<java.lang.String, java.util.List < java.lang.String>>
     */
    public static HashMap<String, List<String>> moveFiles(Map<String, File> fileMap, Collection<String> keys, String targetPath) {
        targetPath += targetPath.endsWith("/") ? "" : "/";
        List<String> pidList = fileMap.keySet().stream().filter(keys::contains).collect(Collectors.toList());
        List<String> successList = new ArrayList<>();
        List<String> delList = new ArrayList<>();
        List<String> failList = new ArrayList<>();
        File targetDir = new File(targetPath);
        if (targetDir.mkdirs()) {
            log.info("创建目录:{}", targetPath);
        }
        for (String pid : pidList) {
            File file = fileMap.get(pid);
            String newPath = targetPath + file.getName();
            File newFile = new File(newPath);
            if (newFile.exists()) {
//                文件已存在
                if (file.length() == newFile.length()) {
//                    大小相同 删除
                    if (file.delete()) {
                        fileMap.remove(pid);
                        delList.add(pid);
                    } else {
                        log.warn("删除失败 {}", file);
                        failList.add(pid);
                    }
                } else {
//                    大小不同 改名
                    int i = 0;
                    int subIndex = newPath.lastIndexOf(".");
                    String p = newPath.substring(0, subIndex);
                    String s = newPath.substring(subIndex);
                    do {
                        i++;
                        newPath = p + "." + i + s;
                        newFile = new File(newPath);
                    } while (!newFile.exists());

                    if (file.renameTo(newFile)) {
                        fileMap.remove(pid);
                        successList.add(pid);
                    } else {
                        log.warn("重命名失败 {}", file);
                        failList.add(pid);
                    }
                }
            } else {
                if (file.renameTo(newFile)) {
                    fileMap.remove(pid);
                    successList.add(pid);
                } else {
                    log.warn("重命名失败 {}", file);
                    failList.add(pid);
                }
            }
            File parentFile = file.getParentFile();
            File[] listFiles = parentFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                if (parentFile.delete()) {
                    log.info("删除空目录 {}", parentFile.getPath());
                }
            }
        }
        String msg = String.format("成功移动文件 %d 个 删除重复文件 %d 个 操作失败 %d 个", successList.size(), delList.size(), failList.size());
        log.info(msg);

        HashMap<String, List<String>> map = new HashMap<>(3);
        map.put("success", successList);
        map.put("del", delList);
        map.put("fail", failList);
        return map;
    }

    /**
     * 复制文件
     * @param source 源文件
     * @param dest   目标文件
     * @return void
     */
    public static void copyFile(File source, File dest) throws IOException {
        if (source.getPath().equals(dest.getPath())) {
            return;
        }
        File parentFile = dest.getParentFile();
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs()) {
                log.error("创建文件夹失败 {}", parentFile.getPath());
            }
        }

        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            log.info("复制文件 {} >> {}", source, dest);
        } finally {
            assert inputChannel != null;
            inputChannel.close();
            assert outputChannel != null;
            outputChannel.close();
        }
    }

    public static void splitFile2Dirs(File rootDir) {
        TreeMap<String, File> map = new TreeMap<>(String::compareTo);
        listFiles(rootDir, map);
        int i = 0;
        int count = 1;
        int maxCount = 1000;
        for (Map.Entry<String, File> entry : map.entrySet()) {
            String k = entry.getKey();
            File sourceFile = entry.getValue();

            String groupPath = rootDir.getPath() + "/分组" + i;
            moveFile(sourceFile, groupPath, "分组");
            count++;
            if (count == maxCount) {
                i++;
                count = 0;
            }
        }
    }

    /**
     * 验证图片是否损坏
     * @return boolean true 正常 false 损坏
     */
    public static boolean verifyImage(File file) {
        try(FileInputStream fis = new FileInputStream(file);) {
            BufferedImage sourceImg = ImageIO.read(fis);
            if (sourceImg==null) {
                return false;
            }
            int picWidth= sourceImg.getWidth();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void moveFile(File source, String destPath, String reason) {
        File dest = new File(destPath + "/" + source.getName());
        moveFile(source, dest, reason);
    }

    public static void moveFile(File source, File dest, String reason) {
        File parentDir = dest.getParentFile();
        if (!parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new RuntimeException("文件夹创建失败:" + parentDir);
            }
        }
        if (!source.renameTo(dest)) {
            throw new RuntimeException(String.format("文件移动失败 %s -> %s", source, dest));
        } else {
            log.info("[{}] 移动文件成功 {} -> {}", reason, source.getName(), dest);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        File rootDir = new File("F:\\illust\\未分类\\新建文件夹");
        splitFile2Dirs(rootDir);
//        listFiles(rootDir, new HashMap<>());
//        boolean b = verifyImage(new File("F:/illust/未分类/[52781285_p0].jpg"));
//        boolean b = verifyImage(new File("F:/illust/未分类/[43527469_p0].jpg"));
//        System.out.println("b = " + b);
    }
}
