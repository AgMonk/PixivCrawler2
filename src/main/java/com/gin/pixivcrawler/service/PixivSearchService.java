package com.gin.pixivcrawler.service;

import com.gin.pixivcrawler.entity.taskQuery.SearchKeyword;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivSearchResults;

/**
 * 搜索业务
 *
 * @author Gin
 * @date 2021/2/14 13:54
 */
public interface PixivSearchService {

    /**
     * 搜索
     * @param keyword 关键字
* @param uid 用户id
* @param page 页码
     * @return com.gin.pixivcrawler.utils.pixivUtils.entity.PixivSearchResults
     * @author Gin
     * @date 2021/2/14 17:19
     */
    PixivSearchResults search(SearchKeyword keyword,long uid,int page);

    /**
     * 保存关键字
     * @param keyword 关键字
     * @author Gin
     * @date 2021/2/14 17:44
     */
    void saveKeyword(SearchKeyword keyword);
}
