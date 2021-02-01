package com.gin.pixivcrawler.utils.ngaUtils.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 帖子集合
 *
 * @author bx002
 * @date 2021/1/14 11:13
 */
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("AlibabaAvoidStartWithDollarAndUnderLineNaming")
@Data
public abstract class NgaResThreadCollection extends NgaResBase implements Serializable {
    @JSONField(alternateNames = "__T__ROWS")
    Long threadRows;
    @JSONField(alternateNames = "__T__ROWS_PAGE")
    Long threadRowsPage;

    /**
     * 获得帖子列表
     *
     * @return java.util.List<com.gin.pixivcrawler.utils.ngaUtils.entity.Thread>
     * @author bx002
     * @date 2021/1/15 8:59
     */
    public abstract List<NgaThread> getThreads();
}
