package com.gin.pixivcrawler.utils.ngaUtils.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 帖子map
 *
 * @author bx002
 * @date 2021/1/14 11:13
 */
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("AlibabaAvoidStartWithDollarAndUnderLineNaming")
@Data
public class NgaResThreadMap extends NgaResThreadCollection implements Serializable {
    @JSONField(alternateNames = "__T", serialize = false)
    @JsonIgnore
    HashMap<String, NgaThread> threadsMap;

    @Override
    public List<NgaThread> getThreads() {
        return new ArrayList<>(threadsMap.values());
    }
}
