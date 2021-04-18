package com.gin.pixivcrawler.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gin.pixivcrawler.utils.fanboxUtils.entity.FanboxItem;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;

/**
 * @author bx002
 */
@Repository
@CacheNamespace
public interface FanboxItemDao extends BaseMapper<FanboxItem> {
}
