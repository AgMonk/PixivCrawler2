package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.entity.response.Res;
import com.gin.pixivcrawler.entity.SearchKeyword;
import com.gin.pixivcrawler.entity.taskQuery.SearchQuery;
import com.gin.pixivcrawler.service.PixivSearchService;
import com.gin.pixivcrawler.service.ScheduledTasksService;
import com.gin.pixivcrawler.service.ScheduledTasksServiceImpl;
import com.gin.pixivcrawler.service.queryService.SearchQueryService;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivSearchResults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private final ScheduledTasksService scheduledTasksService;
    private final SearchQueryService searchQueryService;

    public SearchController(PixivSearchService pixivSearchService, ScheduledTasksService scheduledTasksService, SearchQueryService searchQueryService) {
        this.pixivSearchService = pixivSearchService;
        this.scheduledTasksService = scheduledTasksService;
        this.searchQueryService = searchQueryService;
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

    @RequestMapping("findAllKeywords")
    public Res<List<SearchKeyword>> findAllKeywords(){
        return  Res.success(pixivSearchService.findAll());
    }

    @RequestMapping("autoSearch")
    public Res<Void> autoSearch(){
        scheduledTasksService.autoSearch();
        return Res.success();
    }
}
