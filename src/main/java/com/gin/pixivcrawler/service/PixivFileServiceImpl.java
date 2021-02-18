package com.gin.pixivcrawler.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

import static com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail.PIXIV_ILLUST_FULL_NAME;
import static com.gin.pixivcrawler.utils.requestUtils.RequestBase.timeCost;

/**
 * @author bx002
 * @date 2021/2/18 11:53
 */
@Service
@Slf4j
public class PixivFileServiceImpl implements PixivFileService {
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
    private final ConfigService configService;

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
