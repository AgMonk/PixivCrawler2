package com.gin.pixivcrawler.service.queryService;

import com.gin.pixivcrawler.entity.taskQuery.AddTagQuery;
import com.gin.pixivcrawler.service.base.BaseInsertService;
import com.gin.pixivcrawler.service.base.BaseSelectService;

import java.util.Collection;
import java.util.List;

/**
 * @author bx002
 * @date 2021/2/4 15:36
 */
public interface AddTagQueryService extends BaseSelectService<AddTagQuery>, BaseInsertService<AddTagQuery> {

    List<AddTagQuery> findAllNotIn(Collection<Long> pidCollection);

    boolean delete(Long pid);
}
