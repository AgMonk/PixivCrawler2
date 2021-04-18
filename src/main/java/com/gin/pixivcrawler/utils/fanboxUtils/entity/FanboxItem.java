package com.gin.pixivcrawler.utils.fanboxUtils.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author bx002
 */
@TableName("t_fanbox_item")
@Data
public class FanboxItem {
    Long id;
    String title;
    /**
     * 赞助费
     */
    Integer feeRequired;
    /**
     * 发表时间
     */
    String publishedDatetime;
    /**
     * 上传时间
     */
    String updatedDatetime;
    String type;
    FanboxItemsBody body;
    String creatorId;

}
