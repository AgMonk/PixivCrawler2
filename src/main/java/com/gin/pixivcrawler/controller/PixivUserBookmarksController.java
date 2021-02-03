package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.service.PixivUserBookmarksService;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivBookmarks;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * 收藏
 *
 * @author bx002
 * @date 2021/2/3 11:10
 */
@RestController
@RequestMapping("bookmarks")
public class PixivUserBookmarksController {
    private final PixivUserBookmarksService pixivUserBookmarksService;

    public PixivUserBookmarksController(PixivUserBookmarksService pixivUserBookmarksService) {
        this.pixivUserBookmarksService = pixivUserBookmarksService;
    }

    @RequestMapping("get")
    public PixivBookmarks get(
            @RequestParam(defaultValue = "57680761")
            Long userId,
            @RequestParam(defaultValue = "未分類")
            String tag,
            @RequestParam(defaultValue = "0")
            Integer offset,
            @RequestParam(defaultValue = "5")
            Integer limit
    ) throws ExecutionException, InterruptedException {
        return pixivUserBookmarksService.get(userId,tag,offset,limit).get();
    }
}
