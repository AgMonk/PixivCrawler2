package com.gin.pixivcrawler.utils.pixivUtils.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 详情中的Tag对象
 *
 * @author bx002
 * @date 2021/2/2 11:50
 */
@Data
public class PixivTagsInDetail implements Serializable {
    @JsonIgnore
    @JSONField(serialize = false)
    Long authorId;
    @JsonIgnore
    @JSONField(serialize = false)
    Boolean isLocked;
    List<PixivTag> tags;
    @JsonIgnore
    @JSONField(serialize = false)
    Boolean writable;
}
