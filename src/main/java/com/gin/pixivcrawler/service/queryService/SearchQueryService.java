package com.gin.pixivcrawler.service.queryService;

import com.gin.pixivcrawler.entity.SearchKeyword;
import com.gin.pixivcrawler.entity.taskQuery.SearchQuery;

import java.util.Collection;
import java.util.List;

/**
 * @author Gin
 * @date 2021/2/15 13:47
 */
public interface SearchQueryService {
    /**
     * 查询所有搜索队列
     * @return java.util.List<com.gin.pixivcrawler.entity.taskQuery.SearchQuery>
     * @author Gin
     * @date 2021/2/15 13:49
     */
    SearchQuery findOne();
    /**
     * 添加搜索队列
     * @param searchKeywords 搜索关键字
     * @param uid 用户id
     * @param page 页数
     * @author Gin
     * @date 2021/2/15 13:49
     */
    void saveList(Collection<SearchKeyword> searchKeywords, long uid, int page);

    List<SearchQuery> findAll();

    void delById(String uuid);
}
