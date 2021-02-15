package com.gin.pixivcrawler.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gin.pixivcrawler.entity.taskQuery.SearchQuery;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;

/**
 * @author Gin
 * @date 2021/2/15 13:46
 */
@Repository
@CacheNamespace(flushInterval = 60 * 1000)
public interface SearchQueryDao extends BaseMapper<SearchQuery> {
}
