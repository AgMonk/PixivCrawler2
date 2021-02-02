package com.gin.pixivcrawler.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivUser;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.stereotype.Repository;

/**
 * @author bx002
 * @date 2021/2/2 13:51
 */
@Repository
@CacheNamespace
public interface PixivUserDao extends BaseMapper<PixivUser> {
}
