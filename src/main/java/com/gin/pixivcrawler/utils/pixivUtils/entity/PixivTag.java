package com.gin.pixivcrawler.utils.pixivUtils.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gin.pixivcrawler.utils.StringUtils;
import lombok.Data;
import org.nlpcn.commons.lang.jianfan.JianFan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    @TableField(insertStrategy = FieldStrategy.IGNORED, updateStrategy = FieldStrategy.IGNORED)
    String transCustomize;
    @TableField(insertStrategy = FieldStrategy.IGNORED)
    String transRaw;
    @TableField(exist = false)
    List<String> recommendTranslations;

    public void addRecTrans(String s) {
        if (StringUtils.isEmpty(s)) {
            return;
        }
        s = replace(s);
        recommendTranslations = recommendTranslations == null ? new ArrayList<>() : recommendTranslations;
        if (!recommendTranslations.contains(s)) {
            recommendTranslations.add(s);
        }
    }

    public void setTransCustomize(String transCustomize) {
        if (StringUtils.isEmpty(transCustomize)) {
            transCustomize = null;
        }
        this.transCustomize = transCustomize;
    }

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

        return tag.equalsIgnoreCase(pixivTag.tag);
    }

    @Override
    public int hashCode() {
        return tag != null ? tag.hashCode() : 0;
    }

    public static String replace(String s) {
        return JianFan.f2j(s)
                .replace("（", "(")
                .replace("）", ")")
                .replace(" ", "")
                ;
    }
}
