package com.gin.pixivcrawler.entity.taskQuery;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.UUID;

/**
 * 搜索队列
 *
 * @author Gin
 * @date 2021/2/15 13:37
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("t_query_search")
public class SearchQuery implements Serializable {
    @TableId
    String uuid = UUID.randomUUID().toString();
    Long timestamp = System.currentTimeMillis();
    String name;
    String keyword;
    Long uid;
    Integer page;

    public SearchQuery(String name,String keyword, Long uid, Integer page) {
        this.name = name;
        this.keyword = keyword;
        this.uid = uid;
        this.page = page;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SearchQuery that = (SearchQuery) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
