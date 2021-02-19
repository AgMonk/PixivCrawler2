package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.dao.PixivCookieDao;
import com.gin.pixivcrawler.service.PixivIllustDetailService;
import com.gin.pixivcrawler.service.PixivTagService;
import com.gin.pixivcrawler.service.ScheduledTasksService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author bx002
 * @date 2021/2/2 16:58
 */
@SuppressWarnings({"FieldCanBeLocal", "UnnecessaryLocalVariable", "RedundantThrows"})
@RestController
@RequestMapping("test")
public class TestController {
    private final PixivCookieDao pixivCookieDao;
    private final ScheduledTasksService scheduledTasksService;
    private final PixivIllustDetailService pixivIllustDetailService;
    private final PixivTagService pixivTagService;

    public TestController(PixivCookieDao pixivCookieDao, ScheduledTasksService scheduledTasksService, PixivIllustDetailService pixivIllustDetailService, PixivTagService pixivTagService) {
        this.pixivCookieDao = pixivCookieDao;
        this.scheduledTasksService = scheduledTasksService;
        this.pixivIllustDetailService = pixivIllustDetailService;
        this.pixivTagService = pixivTagService;
    }

    @RequestMapping("1")
    public Object test(Long pid) throws InterruptedException, ExecutionException, TimeoutException {
        pixivTagService.countTags();
        return null;
    }


}
