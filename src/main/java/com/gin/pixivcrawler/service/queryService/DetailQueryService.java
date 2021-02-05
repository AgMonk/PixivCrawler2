package com.gin.pixivcrawler.service.queryService;

import com.gin.pixivcrawler.entity.taskQuery.DetailQuery;
import com.gin.pixivcrawler.service.base.BaseDeleteService;
import com.gin.pixivcrawler.service.base.BaseInsertService;
import com.gin.pixivcrawler.service.base.BaseSelectService;

import java.util.Collection;
import java.util.List;

/**
 * @author bx002
 * @date 2021/2/5 11:34
 */
public interface DetailQueryService extends BaseInsertService<DetailQuery>, BaseSelectService<DetailQuery>, BaseDeleteService<DetailQuery> {

    /**
     * 获取排序过的队列
     *
     * @param limit    数量
     * @param pidNotIn 排除的pid
     * @return java.util.List<com.gin.pixivcrawler.entity.taskQuery.DownloadQuery>
     * @author bx002
     * @date 2021/2/4 11:16
     */
    List<DetailQuery> findSortedList(int limit, Collection<Long> pidNotIn);
}
