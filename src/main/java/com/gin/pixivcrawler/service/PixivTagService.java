package com.gin.pixivcrawler.service;

import com.gin.pixivcrawler.service.base.BaseService;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTag;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;

/**
 * @author bx002
 * @date 2021/2/2 14:35
 */
public interface PixivTagService extends BaseService<PixivTag> {
    /**
     * 为作品添加tag
     *
     * @param detail 详情
     * @param userId 用户id
     * @author bx002
     * @date 2021/2/3 17:43
     */
    void addTag(PixivIllustDetail detail, Long userId);
}
