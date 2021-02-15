package com.gin.pixivcrawler.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gin.pixivcrawler.entity.SearchKeyword;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;

/**
 * @author Gin
 * @date 2021/2/14 15:02
 */
@Repository
@CacheNamespace(flushInterval = 60 * 1000)
public interface SearchKeywordDao extends BaseMapper<SearchKeyword> {
}
