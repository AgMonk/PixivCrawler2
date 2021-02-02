package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.service.PixivIllustDetailService;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivIllustDetail;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bx002
 * @date 2021/2/2 13:36
 */
@RestController
@RequestMapping("detail")
public class PixivIllustDetailController {
    private final PixivIllustDetailService pixivIllustDetailService;

    public PixivIllustDetailController(PixivIllustDetailService pixivIllustDetailService) {
        this.pixivIllustDetailService = pixivIllustDetailService;
    }

    @RequestMapping("getDetail")
    public PixivIllustDetail getDetail(Long pid) {
        return pixivIllustDetailService.findOne(pid);
    }
}
