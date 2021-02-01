package com.gin.pixivcrawler.utils.ngaUtils.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gin.pixivcrawler.utils.TimeUtil.second2String;

/**
 * 回复
 *
 * @author bx002
 * @date 2021/1/14 12:12
 */
@SuppressWarnings("SpellCheckingInspection")
@Data
@TableName("t_replies")
public class Reply implements Serializable {
    private static final Pattern LAST_ALTER_STRING_PATTERN = Pattern.compile("E\\d+");
    @TableId
    String uuid;
    @JSONField(alternateNames = "alterinfo")
    String alterInfo;
    @JSONField(alternateNames = "authorid")
    Long authorId;
    @TableField(exist = false)
    String content;
    @JSONField(alternateNames = "content_length")
    Long contentLength;
    Long fid;
    Long lou;
    Long pid;
    @JSONField(alternateNames = "postdatetimestamp", serialize = false)
    Long postDateTimestamp;
    Long recommend;
    Long score;
    @JSONField(alternateNames = "score_2")
    Long score2;
    String subject;
    Long tid;
    Long type;

    public void setAlterInfo(String alterInfo) {
        this.alterInfo = alterInfo;
        this.uuid = this.pid + this.alterInfo;
    }

    public void setPid(Long pid) {
        this.pid = pid;
        this.uuid = this.pid + this.alterInfo;
    }

    public String getPostDateTime() {
        return second2String(postDateTimestamp);
    }

    public String getLastEditDatetime() {
        Long lastEdit = getLastEdit();
        return lastEdit != null ? second2String(lastEdit) : null;
    }

    @JsonIgnore
    public Long getLastEdit() {
        Matcher matcher = LAST_ALTER_STRING_PATTERN.matcher(this.alterInfo);
        if (matcher.find()) {
            String s = matcher.group().replace("E", "");
            return Long.parseLong(s);
        }
        return postDateTimestamp;
    }
}
