package com.gin.pixivcrawler.utils.pixivUtils.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

/**
 * Pixiv标签
 *
 * @author bx002
 * @date 2021/2/2 11:51
 */
@Data
@TableName("t_pixiv_tags")
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
    @TableId
    String tag;
    @TableField(insertStrategy = FieldStrategy.IGNORED)
    String transCustomize;
    @TableField(insertStrategy = FieldStrategy.IGNORED)
    String transRaw;

    public void setTranslation(HashMap<String, String> translation) {
        this.translation = translation;
        this.transRaw = translation.get("en");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PixivTag pixivTag = (PixivTag) o;

        return Objects.equals(tag, pixivTag.tag);
    }

    @Override
    public int hashCode() {
        return tag != null ? tag.hashCode() : 0;
    }
}
