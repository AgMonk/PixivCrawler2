package com.gin.pixivcrawler.utils.ngaUtils.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 帖子列表
 *
 * @author bx002
 * @date 2021/1/14 11:13
 */
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("AlibabaAvoidStartWithDollarAndUnderLineNaming")
@Data
public class NgaResThreadList extends NgaResThreadCollection implements Serializable {
    @JSONField(alternateNames = "__T")
    List<NgaThread> threads;
}
