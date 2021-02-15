package com.gin.pixivcrawler.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;

/**
 * @author bx002
 * @date 2021/2/2 13:17
 */
@Repository
@CacheNamespace(flushInterval = 60 * 1000)
public interface PixivIllustDetailDao extends BaseMapper<PixivIllustDetail> {
}
