package com.gin.pixivcrawler;

import com.gin.pixivcrawler.dao.FanboxCookieDao;
import com.gin.pixivcrawler.utils.fanboxUtils.FanboxCookie;
import com.gin.pixivcrawler.utils.fanboxUtils.FanboxPost;
import com.gin.pixivcrawler.utils.fanboxUtils.entity.FanboxItem;
import com.gin.pixivcrawler.utils.fanboxUtils.entity.FanboxResponseBody;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class PixivCrawlerApplicationTests {
    @Autowired
    FanboxCookieDao fanboxCookieDao;

    @Test
    void contextLoads() {
    }

    @Test
    void test1() {
        FanboxCookie fanboxCookie = fanboxCookieDao.selectById(1);
        String cookie = fanboxCookie.getCookie();
        FanboxResponseBody fanboxResponseBody = FanboxPost.listSupporting(cookie, 20);
        if (fanboxResponseBody == null) {
            return;
        }
        List<FanboxItem> items = fanboxResponseBody.getItems();
        for (FanboxItem item : items) {
            System.out.println(item.getId() + " " + item.getTitle());
        }
    }
}
