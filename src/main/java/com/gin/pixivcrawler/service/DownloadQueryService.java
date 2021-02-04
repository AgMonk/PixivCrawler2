package com.gin.pixivcrawler.service;

import com.gin.pixivcrawler.entity.taskQuery.DownloadQuery;
import com.gin.pixivcrawler.service.base.BaseService;

import java.util.Collection;
import java.util.List;

/**
 * @author bx002
 * @date 2021/2/3 14:33
 */
public interface DownloadQueryService extends BaseService<DownloadQuery> {

    boolean deleteByUrl(Collection<String> url);

    /**
     * 获取排序过的队列
     *
     * @param limit    数量
     * @param urlNotIn
     * @return java.util.List<com.gin.pixivcrawler.entity.taskQuery.DownloadQuery>
     * @author bx002
     * @date 2021/2/4 11:16
     */
    List<DownloadQuery> findSortedList(int limit, Collection<String> urlNotIn);

}
