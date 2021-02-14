package com.gin.pixivcrawler.entity.taskQuery;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gin.pixivcrawler.utils.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 搜索关键字
 *
 * @author Gin
 * @date 2021/2/14 14:52
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("t_search_keyword")
public class SearchKeyword implements Serializable {
    @TableId
    String name;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    String hasAllKeywords;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    String hasNotKeywords;
    /**
     * 含有关键字 OR 连接
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    String hasKeywords;

    public void setHasAllKeywords(String hasAllKeywords) {
        this.hasAllKeywords = StringUtils.isEmpty(hasAllKeywords)?null:hasAllKeywords;
    }
    public void setHasNotKeywords(String hasNotKeywords) {
        this.hasNotKeywords = StringUtils.isEmpty(hasNotKeywords)?null:hasNotKeywords;
    }
    public void setHasKeywords(String hasKeywords) {
        this.hasKeywords =  StringUtils.isEmpty(hasKeywords)?null:hasKeywords;
    }

    public String getKeywords(){
        StringBuilder sb = new StringBuilder();
        if (hasAllKeywords!=null) {
            sb.append(hasAllKeywords).append(" ");
        }
        if (hasKeywords!=null) {
            sb.append(Arrays.stream(hasKeywords.split(" ")).collect(Collectors.joining(" OR ","(",")"))).append(" ");
        }
        if (hasNotKeywords!=null) {
            sb.append("-").append(hasNotKeywords.replace(" ", " -"));
        }
        return sb.toString().trim();
    }
}
