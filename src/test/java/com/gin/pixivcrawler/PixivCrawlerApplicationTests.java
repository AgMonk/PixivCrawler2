package com.gin.pixivcrawler;

import com.gin.pixivcrawler.dao.FanboxCookieDao;
import com.gin.pixivcrawler.service.FanboxItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PixivCrawlerApplicationTests {
    @Autowired
    FanboxCookieDao fanboxCookieDao;
    @Autowired
    FanboxItemService fanboxItemService;

    @Test
    void contextLoads() {
    }

    @Test
    void test1() {
        fanboxItemService.listSupporting(50);

//        FanboxCookie fanboxCookie = fanboxCookieDao.selectById(1);
//        String cookie = fanboxCookie.getCookie();
//        FanboxResponseBody fanboxResponseBody = FanboxPost.listSupporting(cookie, 20);
//        if (fanboxResponseBody == null) {
//            return;
//        }
//        List<FanboxItem> items = fanboxResponseBody.getItems();
//        for (FanboxItem item : items) {
//            if (item.getBody() != null) {
//                System.out.println(item.getId() + " " + item.getTitle());
//            }
//        }
    }
}
