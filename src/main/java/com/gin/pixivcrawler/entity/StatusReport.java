package com.gin.pixivcrawler.entity;

import com.gin.pixivcrawler.entity.taskQuery.AddTagQuery;
import com.gin.pixivcrawler.entity.taskQuery.DetailQuery;
import com.gin.pixivcrawler.entity.taskQuery.DownloadQuery;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

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
    List<DownloadQuery> downloadQuery;
    List<DetailQuery> detailQuery;

    public static StatusReport create() {
        return new StatusReport();
    }
}
