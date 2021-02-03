package com.gin.pixivcrawler.service;

import com.gin.pixivcrawler.service.base.BaseSelectService;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivBookmarks;

import java.util.concurrent.Future;

/**
 * @author bx002
 * @date 2021/2/3 11:03
 */
public interface PixivUserBookmarksService {
    /**
     * 获取收藏作品
     * @param userId 用户id
* @param tag 标签
* @param offset offset
* @param limit limit
     * @return java.util.concurrent.Future<com.gin.pixivcrawler.utils.pixivUtils.entity.PixivBookmarks>
     * @author bx002
     * @date 2021/2/3 11:06
     */
     Future<PixivBookmarks> get(long userId, String tag, int offset, int limit);
}
