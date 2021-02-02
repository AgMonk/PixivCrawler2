package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.service.PixivIllustDetailService;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

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
    public PixivIllustDetail getDetail(Long pid) throws ExecutionException, InterruptedException, TimeoutException {
        return pixivIllustDetailService.findOne(pid);
    }

    @RequestMapping("getDetails")
    public List<PixivIllustDetail> getDetailList(String pidString) {
        return pixivIllustDetailService.findList(Arrays.stream(pidString.split(",")).map(Long::parseLong).collect(Collectors.toList()));
    }
}
