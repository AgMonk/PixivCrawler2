package com.gin.pixivcrawler.service;

import com.gin.pixivcrawler.service.base.BaseSelectService;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;

import java.util.concurrent.Future;

/**
 * @author bx002
 * @date 2021/2/2 13:17
 */
public interface PixivIllustDetailService extends BaseSelectService<PixivIllustDetail> {
    /**
     * 获取详情
     *
     * @param pid pid
     * @return java.util.concurrent.Future<com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail>
     * @author bx002
     * @date 2021/2/3 11:08
     */
    Future<PixivIllustDetail> getDetail(long pid);
}
