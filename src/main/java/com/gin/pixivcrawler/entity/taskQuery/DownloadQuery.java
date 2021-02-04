package com.gin.pixivcrawler.entity.taskQuery;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 下载文件队列
 *
 * @author bx002
 * @date 2021/2/3 14:22
 */
@TableName("t_query_download")
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DownloadQuery implements Serializable {
    @TableId
    String uuid;
    String path;
    String fileName;
    String url;
    String type;
    Integer priority;
}
