package com.gin.pixivcrawler.utils.ngaUtils.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.gin.pixivcrawler.utils.TimeUtil.DATE_FORMATTER;
import static com.gin.pixivcrawler.utils.TimeUtil.second2String;

/**
 * nga主题
 *
 * @author bx002
 * @date 2021/1/14 10:44
 */
@SuppressWarnings("SpellCheckingInspection")
@Data
@TableName("t_nga_thread")
@Accessors(chain = true)
public class NgaThread implements Serializable {

    private static final String urlPattern = "http.+?]";
    private static final Pattern URL_PATTERN = Pattern.compile(urlPattern);
    private static final List<String> REPLACE_PATTERN =
            Arrays.asList("\\[img].+?\\[/img]", "<br/>"
                    , "\\[b]", "\\[/b]"
                    , "\\[del]", "\\[/del]"
                    , "\\[quote]", "\\[/quote]"
                    , "\\[size.+?]", "\\[/size]"
                    , "\\[align.+?]", "\\[/align]"
                    , "\\[color.+?]", "\\[/color]"
                    , "\\[s:.+?]");

    @TableField(exist = false)
    String author;

    @JSONField(alternateNames = "authorid")
    String authorId;
    Long fid;
    @JSONField(alternateNames = "lastmodify", serialize = false)
    @JsonIgnore
    Long lastModify;
    /**
     * 最后回复时间
     */
    @JSONField(alternateNames = "lastpost", serialize = false)
    @JsonIgnore
    Long lastPost;
    /**
     * 最后回复用户
     */
    @JSONField(alternateNames = "lastposter")
    String lastPoster;
    /**
     * 主题发布时间
     */
    @JSONField(alternateNames = "postdate", serialize = false)
    @JsonIgnore
    Long postDate;
    /**
     * 上次修改
     */
    @JsonIgnore
    Long lastEdit;
    /**
     * 上次查询
     */
    @JsonIgnore
    Long lastCheck;
    /**
     * 推荐值
     */
    Long recommend;
    /**
     * 回复数量
     */
    Long replies;
    /**
     * 标题
     */
    String subject;
    /**
     * 标题字体
     */
    @JSONField(alternateNames = "titlefont")
    String titleFont;
    /**
     * 标题字体2
     */
    @JSONField(alternateNames = "topic_misc")
    String topicMisc;
    @TableId
    Long tid;
    Long type;

    String content;
    String contentType;
    @TableField(exist = false)
    Long contentLength;
    @TableField(exist = false)
    Integer count;

    public void setTitleFont(String titleFont) {
        this.titleFont = titleFont;
        this.topicMisc = titleFont;
    }

    public void setTopicMisc(String topicMisc) {
        this.titleFont = topicMisc;
        this.topicMisc = topicMisc;
    }

    public NgaThread setContent(String content) {
        content = StringEscapeUtils.unescapeHtml(content);
        for (String s : REPLACE_PATTERN) {
            content = content.replaceAll(s, "");
        }
        this.content = content.substring(0, Math.min(10000, content.length()));
        return this;
    }

    public String getSubject() {
        return StringEscapeUtils.unescapeHtml(subject);
    }

    public String getLastModifyTime() {
        return lastModify != null ? second2String(lastModify, DATE_FORMATTER) : null;
    }

    public String getLastPostTime() {
        return lastPost != null ? second2String(lastPost, DATE_FORMATTER) : null;
    }

    public String getLastEditTime() {
        return lastEdit != null ? second2String(lastEdit, DATE_FORMATTER) : null;
    }

    public String getPostDateTime() {
        return postDate != null ? second2String(postDate, DATE_FORMATTER) : null;
    }


}
