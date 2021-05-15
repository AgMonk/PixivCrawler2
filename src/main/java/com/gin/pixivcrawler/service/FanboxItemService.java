package com.gin.pixivcrawler.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gin.pixivcrawler.utils.fanboxUtils.entity.FanboxItem;

import java.util.List;

/**
 * @author bx002
 */
public interface FanboxItemService extends IService<FanboxItem> {
    /**
     * 列出赞助作者的作品
     * @param limit 数量
     * @return void
     */
    List<FanboxItem> listSupporting(int limit);

    /**
     * 查询一个作品
     * @param id  id
     * @return 作品
     */
    FanboxItem findItem(long id);
}
