package com.gin.pixivcrawler.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gin.pixivcrawler.entity.Config;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;

/**
 * @author Gin
 * @date 2021/2/17 14:44
 */
@Repository
@CacheNamespace(flushInterval = 60 * 1000)
public interface ConfigDao extends BaseMapper<Config> {
}
