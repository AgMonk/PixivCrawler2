package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.dao.PixivCookieDao;
import com.gin.pixivcrawler.utils.JsonUtil;
import com.gin.pixivcrawler.utils.pixivUtils.PixivPost;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivBookmarks;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivCookie;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bx002
 * @date 2021/2/2 16:58
 */
@RestController
@RequestMapping("test")
public class TestController {
    private final PixivCookieDao pixivCookieDao;

    public TestController(PixivCookieDao pixivCookieDao) {
        this.pixivCookieDao = pixivCookieDao;
    }

    @RequestMapping("1")
    public void test(){
        PixivCookie pixivCookie = pixivCookieDao.selectOne(null);
        PixivBookmarks bookmarks = PixivPost.getBookmarks(pixivCookie.getCookie(), pixivCookie.getUserId(), 0, 10, "未分類");
        JsonUtil.printJson(bookmarks);
    }
}
