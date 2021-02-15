package com.gin.pixivcrawler.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gin.pixivcrawler.entity.taskQuery.AddTagQuery;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;

/**
 * @author bx002
 * @date 2021/2/4 15:36
 */
@Repository
@CacheNamespace(flushInterval = 60 * 1000)
public interface AddTagQueryDao extends BaseMapper<AddTagQuery> {
}
