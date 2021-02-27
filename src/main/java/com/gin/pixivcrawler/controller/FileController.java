package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.entity.response.Res;
import com.gin.pixivcrawler.service.*;
import com.gin.pixivcrawler.utils.SpringContextUtil;
import com.gin.pixivcrawler.utils.fileUtils.FileUtils;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.TokenList;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.gin.pixivcrawler.entity.ConstantValue.DELIMITER_COMMA;
import static com.gin.pixivcrawler.entity.ConstantValue.DELIMITER_PIXIV_NAME;

/**
 * @author bx002
 * @date 2021/2/18 15:05
 */
@RestController
@RequestMapping("file")
@Slf4j
public class FileController {
    private final PixivFileService pixivFileService;
    private final PixivIllustDetailService pixivIllustDetailService;
    private final PixivTagService pixivTagService;
    private final ConfigService configService;

    public FileController(PixivFileService pixivFileService, PixivIllustDetailService pixivIllustDetailService, PixivTagService pixivTagService, ConfigService configService) {
        this.pixivFileService = pixivFileService;
        this.pixivIllustDetailService = pixivIllustDetailService;
        this.pixivTagService = pixivTagService;
        this.configService = configService;
    }

    @RequestMapping("getMap")
    public Res<TreeMap<String, String>> getFileMap(@RequestParam(defaultValue = "50") Integer limit) {
        TreeMap<String, String> map = pixivFileService.getUnarchivedFileMap(limit);
        TreeMap<String, String> hashMap = new TreeMap<>();
        map.forEach((k,v)->{
            if (!v.endsWith("zip")) {
                hashMap.put(k,v);
            }
        });
        return Res.success(hashMap);
    }

    @RequestMapping("getTranslation")
    public Res<HashMap<Long, String>> getTranslation(String pidString) {
        String[] pidArray = pidString.split(DELIMITER_COMMA);
        List<PixivIllustDetail> details = pixivIllustDetailService.findList(Arrays.asList(pidArray));
        HashMap<Long, String> map = new HashMap<>(details.size());
        for (PixivIllustDetail detail : details) {
            map.put(detail.getId(), pixivTagService.translate(detail.getTagString(), DELIMITER_COMMA));
        }
        return Res.success(map);
    }

    @RequestMapping("archive")
    public Res<HashMap<String, List<String>>> archive(String pidString) {
        String[] pidArray = pidString.split(DELIMITER_COMMA);
        return Res.success(pixivFileService.archive(Arrays.asList(pidArray)));
    }

    @RequestMapping("del")
    public Res<HashMap<String, List<String>>> del(String pidString) {
        String[] pidArray = pidString.split(DELIMITER_COMMA);
        return Res.success(pixivFileService.del(Arrays.asList(pidArray)));
    }
    @RequestMapping("copy")
    public Res<Void> copy(String keyword){
        TreeMap<String, String> fileMap = pixivFileService.getUnarchivedFileMap(99999);
        log.info("待检索文件 {}个",fileMap.size());
        Collection<Serializable> pidList = fileMap.keySet().stream()
                .map(key -> Long.parseLong(key.split(DELIMITER_PIXIV_NAME)[0]))
                .distinct()
                .collect(Collectors.toList());
        List<String> list = new ArrayList<>();
        PixivIllustDetailServiceImpl detailService = SpringContextUtil.getBean(PixivIllustDetailServiceImpl.class);
        detailService.listByIds(pidList).forEach(detail->{
            if (pixivTagService.translate(detail.getTagString(),DELIMITER_COMMA).contains(keyword)) {
                list.add(String.valueOf(detail.getId()));
            }
        });
        log.info("符合关键字的作品 {}个",list.size());
         fileMap.keySet().stream()
                .filter(key -> list.contains(key.split(DELIMITER_PIXIV_NAME)[0]))
                .map(key -> fileMap.get(key).replace("/img", configService.getConfig().getRootPath()))
                 .forEach(path->{
                     File source = new File(path);
                     String fileName = path.substring(path.lastIndexOf("/") + 1);
                     File dest = new File(configService.getConfig().getRootPath()+"/"+keyword+"/"+fileName);
                     try {
                         FileUtils.copyFile(source,dest);
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 });
         log.info("复制完毕");
        return Res.success();
    }
}
