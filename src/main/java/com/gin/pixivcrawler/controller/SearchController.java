package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.dao.PixivCookieDao;
import com.gin.pixivcrawler.entity.response.Res;
import com.gin.pixivcrawler.entity.taskQuery.SearchKeyword;
import com.gin.pixivcrawler.service.PixivSearchService;
import com.gin.pixivcrawler.service.PixivTagService;
import com.gin.pixivcrawler.utils.pixivUtils.PixivPost;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivCookie;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivSearchResults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 搜索
 *
 * @author Gin
 * @date 2021/2/14 16:40
 */
@RequestMapping("search")
@RestController
public class SearchController {
    private final PixivSearchService pixivSearchService;

    public SearchController(PixivSearchService pixivSearchService) {
        this.pixivSearchService = pixivSearchService;
    }

    @RequestMapping("search")
    public Res<PixivSearchResults> search(SearchKeyword keyword) {
        return Res.success(pixivSearchService.search(keyword, 57680761, 1));
    }

    @RequestMapping("saveKeyword")
    public Res<Void> save(SearchKeyword keyword) {
        pixivSearchService.saveKeyword(keyword);
        return Res.success();
    }
}
