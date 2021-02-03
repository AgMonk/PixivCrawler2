package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.dao.PixivCookieDao;
import com.gin.pixivcrawler.service.ScheduledTasksService;
import com.gin.pixivcrawler.utils.JsonUtil;
import com.gin.pixivcrawler.utils.pixivUtils.PixivPost;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivBookmarks;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivCookie;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author bx002
 * @date 2021/2/2 16:58
 */
@RestController
@RequestMapping("test")
public class TestController {
    private final PixivCookieDao pixivCookieDao;
    private final ScheduledTasksService scheduledTasksService;

    public TestController(PixivCookieDao pixivCookieDao, ScheduledTasksService scheduledTasksService) {
        this.pixivCookieDao = pixivCookieDao;
        this.scheduledTasksService = scheduledTasksService;
    }

    @RequestMapping("1")
    public void test() throws InterruptedException, ExecutionException, TimeoutException {
        scheduledTasksService.downloadUntagged();
    }


}
