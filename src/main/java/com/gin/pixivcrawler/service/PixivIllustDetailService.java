package com.gin.pixivcrawler.service;

import com.gin.pixivcrawler.service.base.BaseSelectService;
import com.gin.pixivcrawler.service.base.BaseService;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivIllustDetail;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import java.io.Serializable;
import java.util.concurrent.Future;

/**
 * @author bx002
 * @date 2021/2/2 13:17
 */
public interface PixivIllustDetailService extends BaseSelectService<PixivIllustDetail> {
}
