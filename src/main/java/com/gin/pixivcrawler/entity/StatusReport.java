package com.gin.pixivcrawler.entity;

import com.gin.pixivcrawler.entity.taskQuery.AddTagQuery;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collection;

/**
 * 状态报告
 *
 * @author bx002
 * @date 2021/2/4 17:35
 */
@Data
@Accessors(chain = true)
public class StatusReport implements Serializable {
    int untaggedTotalCount;
    Collection<AddTagQuery> addTagQuery;

    public static StatusReport create() {
        return new StatusReport();
    }
}
