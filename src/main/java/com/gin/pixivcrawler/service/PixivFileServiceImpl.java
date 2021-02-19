package com.gin.pixivcrawler.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail.PIXIV_ILLUST_FULL_NAME;
import static com.gin.pixivcrawler.utils.requestUtils.RequestBase.timeCost;

/**
 * @author bx002
 * @date 2021/2/18 11:53
 */
@Service
@Slf4j
public class PixivFileServiceImpl implements PixivFileService {
    private final ConfigService configService;


    private final TreeMap<String, File> fileMap = new TreeMap<>((o1, o2) -> {
        Matcher m1 = PIXIV_ILLUST_FULL_NAME.matcher(o1);
        Matcher m2 = PIXIV_ILLUST_FULL_NAME.matcher(o2);
        if (m1.find() && m2.find()) {
            String[] s1 = m1.group().split("_p");
            String[] s2 = m2.group().split("_p");
            if (s1[0].equals(s2[0])) {
                return Integer.parseInt(s1[1]) - Integer.parseInt(s2[1]);
            }
            if (s1[0].length() != s2[0].length()) {
                return s2[0].length() - s1[0].length();
            }
            return s2[0].compareTo(s1[0]);
        }

        return 0;
    });
    private final TreeMap<String, File> fileMapTemp = new TreeMap<>((Comparator.reverseOrder()));

    @Override
    public TreeMap<String, String> getFileMap(int limit) {
        int i = 0;
        if (this.fileMap.size() == 0) {
            listFileMap();
        }
        TreeMap<String, String> map = new TreeMap<>();
        for (Map.Entry<String, File> entry : this.fileMap.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue().getPath()
                    .replace("\\", "/")
                    .replace(configService.getConfig().getRootPath(), "/img");
            map.put(k, v);
            i++;
            if (i == limit) {
                break;
            }
        }
        return map;
    }

    @Override
    public HashMap<String, List<String>> archive(Collection<String> pidCollection) {
        List<String> pidList = fileMap.keySet().stream().filter(pidCollection::contains).collect(Collectors.toList());

        List<String> successList = new ArrayList<>();
        List<String> delList = new ArrayList<>();
        List<String> failList = new ArrayList<>();
        String archivePath = configService.getConfig().getRootPath() + "/待归档/";
        if (new File(archivePath).mkdirs()) {
            log.info("创建目录:{}", archivePath);
        }
        for (String pid : pidList) {
            File file = fileMap.get(pid);
            String newPath = archivePath + file.getName();
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
        }
        String msg = String.format("成功移动文件 %s 个 删除重复文件 %s 个 操作失败 %s 个", successList.size(), delList.size(), failList.size());
        log.info(msg);
        HashMap<String, List<String>> map = new HashMap<>();
        map.put("success", successList);
        map.put("del", delList);
        map.put("fail", failList);
        return map;
    }

    @Override
    public HashMap<String, List<String>> del(Collection<String> pidCollection) {
        List<String> delList = new ArrayList<>();
        List<String> failList = new ArrayList<>();
        HashMap<String, List<String>> map = new HashMap<>();
        map.put("del", delList);
        map.put("fail", failList);
        List<String> pidList = fileMap.keySet().stream().filter(pidCollection::contains).collect(Collectors.toList());
        for (String pid : pidList) {
            File file = fileMap.get(pid);
            if (file.delete()) {
                delList.add(pid);
                fileMap.remove(pid);
            } else {
                failList.add(pid);
            }
        }
        String msg = String.format("删除文件 %s 个 操作失败 %s 个", delList.size(), failList.size());
        log.info(msg);
        return map;
    }

    public PixivFileServiceImpl(ConfigService configService) {
        this.configService = configService;
    }


    @Scheduled(cron = "0 * * * * ?")
    public void listFileMap() {
        if (fileMapTemp.size() > 0) {
            return;
        }
        log.debug("检索根目录文件...");
        long start = System.currentTimeMillis();
        listFiles(new File(configService.getConfig().getRootPath()), fileMapTemp);
        fileMap.clear();
        fileMap.putAll(fileMapTemp);
        fileMapTemp.clear();
        log.info("根目录下共有文件 {} 个 耗时:{}", fileMap.size(), timeCost(start));
    }


    private static void listFiles(File rootDir, TreeMap<String, File> map) {
        File[] files = rootDir.listFiles();
        if (files == null || files.length == 0) {
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
}
