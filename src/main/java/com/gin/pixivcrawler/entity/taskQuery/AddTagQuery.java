package com.gin.pixivcrawler.entity.taskQuery;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 添加Tag任务队列
 *
 * @author bx002
 * @date 2021/2/4 15:32
 */
@TableName("t_query_add_tag")
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddTagQuery implements Serializable {
    @TableId
    Long pid;
    Long userId;
    String tag;
}
