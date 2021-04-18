package com.gin.pixivcrawler.service;

import com.gin.pixivcrawler.utils.fileUtils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.gin.pixivcrawler.entity.ConstantValue.DELIMITER_PIXIV_NAME;
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

    private final Comparator<String> comparator = (o1, o2) -> {
        Matcher m1 = PIXIV_ILLUST_FULL_NAME.matcher(o1);
        Matcher m2 = PIXIV_ILLUST_FULL_NAME.matcher(o2);
        if (m1.find() && m2.find()) {
            String[] s1 = m1.group().split(DELIMITER_PIXIV_NAME);
            String[] s2 = m2.group().split(DELIMITER_PIXIV_NAME);
            if (s1[0].equals(s2[0])) {
                return Integer.parseInt(s1[1]) - Integer.parseInt(s2[1]);
            }
            if (s1[0].length() != s2[0].length()) {
                return s2[0].length() - s1[0].length();
            }
            return s2[0].compareTo(s1[0]);
        }

        return 0;
    };
    private final TreeMap<String, File> fileMap = new TreeMap<>(comparator);

    private final TreeMap<String, File> filesWithoutDetailMap = new TreeMap<>(comparator);

    private final TreeMap<String, File> fileMapTemp = new TreeMap<>((Comparator.reverseOrder()));

    private final TreeMap<String, File> fileMapTemp2 = new TreeMap<>((Comparator.reverseOrder()));

    @Override
    public TreeMap<String, File> getFilesWithoutDetailMap() {
        return filesWithoutDetailMap;
    }

    @Override
    public TreeMap<String, String> getUnarchivedFileMap(int limit) {
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
        String archivePath = configService.getConfig().getRootPath() + "/待归档/";
        return FileUtils.moveFiles(fileMap, pidCollection, archivePath);
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
        listFileMap();
        listFilesWithoutDetailMap();
    }


    @Scheduled(cron = "0 * * * * ?")
    public void listFileMap() {
        if (fileMapTemp.size() > 0) {
            return;
        }
        log.debug("检索待归档文件...");
        long start = System.currentTimeMillis();
        String rootPath = configService.getConfig().getRootPath();
        FileUtils.listFiles(new File(rootPath + "/未分类"), fileMapTemp);
        FileUtils.listFiles(new File(rootPath + "/搜索下载"), fileMapTemp);
        fileMap.clear();
        copyMap(fileMapTemp, fileMap);
        log.info("待归档文件共有 {} 个 耗时:{}", fileMapTemp.size(), timeCost(start));
        fileMapTemp.clear();
    }

    @Scheduled(cron = "30 * * * * ?")
    public void listFilesWithoutDetailMap() {
        if (fileMapTemp2.size() > 0) {
            return;
        }
        log.debug("检索无详情文件...");
        long start = System.currentTimeMillis();
        String rootPath = configService.getConfig().getRootPath();
        FileUtils.listFiles(new File(rootPath + "/无详情文件"), fileMapTemp2);
        filesWithoutDetailMap.clear();
        copyMap(fileMapTemp2, filesWithoutDetailMap);
        log.info("无详情文件共有 {} 个 耗时:{}", fileMapTemp2.size(), timeCost(start));
        fileMapTemp2.clear();
    }

    private static <T> void copyMap(Map<String, T> source, Map<String, T> dest) {
        for (Map.Entry<String, T> entry : source.entrySet()) {
            String k = entry.getKey();
            T v = entry.getValue();
            dest.put(k, v);
        }
    }
}
