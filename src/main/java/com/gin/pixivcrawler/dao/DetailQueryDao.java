package com.gin.pixivcrawler.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gin.pixivcrawler.entity.taskQuery.DetailQuery;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;

/**
 * @author bx002
 * @date 2021/2/5 11:33
 */
@Repository
@CacheNamespace(flushInterval = 60 * 1000)
public interface DetailQueryDao extends BaseMapper<DetailQuery> {
}
