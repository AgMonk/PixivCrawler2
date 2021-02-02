package com.gin.pixivcrawler.utils.pixivUtils.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Pixiv标签
 *
 * @author bx002
 * @date 2021/2/2 11:51
 */
@Data
public class PixivTag implements Serializable {
    @JsonIgnore
    @JSONField(serialize = false)
    @TableField(exist = false)
    Boolean deletable;
    @JsonIgnore
    @JSONField(serialize = false)
    @TableField(exist = false)
    Boolean locked;
    @JsonIgnore
    @JSONField(serialize = false)
    @TableField(exist = false)
    HashMap<String, String> translation;
    @JsonIgnore
    @JSONField(serialize = false)
    @TableField(exist = false)
    Long userId;
    @JsonIgnore
    @JSONField(serialize = false)
    @TableField(exist = false)
    String userName;

    String tag;
    String transCustomize;
    String transRaw;

    public void setTranslation(HashMap<String, String> translation) {
        this.translation = translation;
        this.transRaw = translation.get("en");
    }
}
