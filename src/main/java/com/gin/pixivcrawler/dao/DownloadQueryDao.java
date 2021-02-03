package com.gin.pixivcrawler.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gin.pixivcrawler.entity.taskQuery.DownloadQuery;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;

/**
 * @author bx002
 * @date 2021/2/3 14:31
 */
@Repository
@CacheNamespace
public interface DownloadQueryDao extends BaseMapper<DownloadQuery> {
}
