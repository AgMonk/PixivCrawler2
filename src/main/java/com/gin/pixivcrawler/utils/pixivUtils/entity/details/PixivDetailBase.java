package com.gin.pixivcrawler.utils.pixivUtils.entity.details;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gin.pixivcrawler.utils.TimeUtil;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashMap;

import static com.gin.pixivcrawler.utils.TimeUtil.ZONE_ID;

/**
 * 详情基础类
 *
 * @author bx002
 * @date 2021/2/2 17:43
 */
@Data
public class PixivDetailBase implements Serializable {

    /**
     * 收藏数据
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    HashMap<String, String> bookmarkData;
    Integer bookmarked;
    /**
     * 作品id
     */
    @TableId
    @JSONField(alternateNames = {"id", "illustId"})
    Long id;
    /**
     * 作品宽度
     */
    @TableField(exist = false)
    Integer width;
    /**
     * 作品高度
     */
    @TableField(exist = false)
    Integer height;
    /**
     * 作品标题
     */
    @JSONField(alternateNames = {"illustTitle", "title"})
    String illustTitle;
    /**
     * 作品类型
     */
    Integer illustType;

    /**
     * 创建时间
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    @JsonIgnore
    String createDate;
    /**
     * 创建时间(秒)
     */
    @JsonIgnore
    @JSONField(serialize = false)
    Long createSeconds;
    /**
     * 作者用户id
     */
    Long userId;
    /**
     * 作者昵称
     */
    @TableField(exist = false)
    String userName;
    /**
     * 作品页数
     */
    Integer pageCount;

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
        this.createSeconds = getEpochSecond(createDate);
    }

    public void setBookmarkData(HashMap<String, String> bookmarkData) {
        this.bookmarkData = bookmarkData;
        if (bookmarkData != null) {
            this.bookmarked = 1;
        }
    }

    public String getCreateDateTime() {
        return TimeUtil.second2String(createSeconds);
    }


    static long getEpochSecond(String date) {
        return ZonedDateTime.parse(date).withZoneSameInstant(ZONE_ID).toInstant().getEpochSecond();
    }
}