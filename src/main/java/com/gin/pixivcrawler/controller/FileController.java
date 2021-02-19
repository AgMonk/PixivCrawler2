package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.entity.response.Res;
import com.gin.pixivcrawler.service.PixivFileService;
import com.gin.pixivcrawler.service.PixivIllustDetailService;
import com.gin.pixivcrawler.service.PixivTagService;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import static com.gin.pixivcrawler.entity.ConstantValue.DELIMITER_COMMA;

/**
 * @author bx002
 * @date 2021/2/18 15:05
 */
@RestController
@RequestMapping("file")
public class FileController {
    private final PixivFileService pixivFileService;
    private final PixivIllustDetailService pixivIllustDetailService;
    private final PixivTagService pixivTagService;

    public FileController(PixivFileService pixivFileService, PixivIllustDetailService pixivIllustDetailService, PixivTagService pixivTagService) {
        this.pixivFileService = pixivFileService;
        this.pixivIllustDetailService = pixivIllustDetailService;
        this.pixivTagService = pixivTagService;
    }

    @RequestMapping("getMap")
    public Res<TreeMap<String, String>> getFileMap(@RequestParam(defaultValue = "50") Integer limit) {
        return Res.success(pixivFileService.getFileMap(limit));
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
}
