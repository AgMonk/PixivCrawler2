package com.gin.pixivcrawler.utils.fileUtils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail.PIXIV_ILLUST_FULL_NAME;

/**
 * 文件工具类
 *
 * @author bx002
 * @date 2021/2/24 9:00
 */
@Slf4j
public class FileUtils {
    /**
     * 列出目录下的所有文件
     *
     * @param rootDir 根目录
     * @param map     保存文件的map
     * @author bx002
     * @date 2021/2/19 11:55
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
                if (matcher.find()) {
                    map.put(matcher.group(), file);
                }
            }
        }
    }

    /**
     * 移动文件
     *
     * @param fileMap    文件map
     * @param keys       需要移动的文件key
     * @param targetPath 目标路径
     * @return java.util.HashMap<java.lang.String, java.util.List < java.lang.String>>
     * @author bx002
     * @date 2021/2/19 11:54
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
* @param dest 目标文件
     * @return void
     * @author Gin
     * @date 2021/2/27 22:53
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
}
