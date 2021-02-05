package com.gin.pixivcrawler.entity.taskQuery;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

/**
 * 查询详情队列
 *
 * @author bx002
 * @date 2021/2/5 11:30
 */
@TableName("t_query_detail")
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DetailQuery implements Serializable {
    @TableId
    Long pid;
    Long userId;
    String type;
    Integer priority;
    /**
     * 请求完成后的操作
     */
    String callback;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DetailQuery that = (DetailQuery) o;

        return Objects.equals(pid, that.pid);
    }

    @Override
    public int hashCode() {
        return pid != null ? pid.hashCode() : 0;
    }
}
