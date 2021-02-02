package com.gin.pixivcrawler.utils.pixivUtils.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gin.pixivcrawler.utils.TimeUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.gin.pixivcrawler.utils.TimeUtil.ZONE_ID;

/**
 * Pixiv作品详情
 *
 * @author bx002
 * @date 2021/2/2 9:25
 */
@NoArgsConstructor
@Data
@TableName("t_illust_detail")
public class PixivIllustDetail implements Serializable {
    public final static String DOMAIN = "https://i.pximg.net";


    /**
     * 插画
     */
    private final static int ILLUST_TYPE_ILLUSTRATION = 0;
    /**
     * 漫画
     */
    private final static int ILLUST_TYPE_MANGA = 1;
    /**
     * 动图
     */
    private final static int ILLUST_TYPE_GIF = 2;
    public static final String IMG = "/img/";


    /**
     * 收藏数
     */
    Integer bookmarkCount;
    /**
     * 收藏数据
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    HashMap<String, String> bookmarkData;

    Integer bookmarked;
    /**
     * 创建时间
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    String createDate;
    /**
     * 创建时间(秒)
     */
    @JsonIgnore
    @JSONField(serialize = false)
    Long createSeconds;
    /**
     * 作品高度
     */
    @TableField(exist = false)
    Integer height;
    /**
     * 作品id
     */
    @TableId
    @JSONField(alternateNames = {"id", "illustId"})
    Long id;
    /**
     * 作品标题
     */
    @JSONField(alternateNames = {"illustTitle", "title"})
    String illustTitle;
    /**
     * 作品类型
     */
    Integer illustType;
    /**
     * 赞数
     */
    @TableField(exist = false)
    Integer likeCount;
    /**
     * 上传时间
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    @JsonIgnore
    String uploadDate;
    /**
     * 作品页数
     */
    Integer pageCount;
    /**
     * 详情中的tags
     */
    @JsonIgnore
    @JSONField(serialize = false, name = "tags")
    @TableField(exist = false)
    PixivTagsInDetail tagsInDetail;

    String tagString;
    @TableField(exist = false)
    String tagTransString;
    /**
     * 上传时间(秒)
     */
    @JsonIgnore
    @JSONField(serialize = false)
    Long uploadSeconds;
    /**
     * urls
     */
    @TableField(exist = false)
    @JsonIgnore
    @JSONField(serialize = false)
    PixivUrls urls;
    String urlPrefix;
    String urlSuffix;

    /**
     * 作者用户名
     */
    @TableField(exist = false)
    String userAccount;
    /**
     * 作者用户id
     */
    Long userId;
    /**
     * 作者昵称
     */
    @TableField(exist = false)
    String userName;
    /**
     * 浏览数
     */
    @TableField(exist = false)
    Long viewCount;
    /**
     * 作品宽度
     */
    @TableField(exist = false)
    Integer width;
    /**
     * 检查时间
     */

    @JsonIgnore
    @JSONField(serialize = false)
    Long checkSeconds;

    public void setBookmarkData(HashMap<String, String> bookmarkData) {
        this.bookmarkData = bookmarkData;
        if (bookmarkData != null) {
            this.bookmarked = 1;
        }
    }

    public void setTagsInDetail(PixivTagsInDetail tagsInDetail) {
        this.tagsInDetail = tagsInDetail;
        this.tagString = tagsInDetail.getTags().stream().map(PixivTag::getTag).collect(Collectors.joining(","));
        this.tagTransString = tagsInDetail.getTags().stream().map(PixivTag::getTransRaw).collect(Collectors.joining(","));
    }

    @JSONField(serialize = false)
    public List<String> getUrlList() {
        if (illustType == ILLUST_TYPE_GIF) {
            return Collections.singletonList(DOMAIN + "/img-zip-ugoira" + IMG + urlPrefix + id + "_ugoira1920x1080.zip");
        }
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < pageCount; i++) {
            list.add(DOMAIN + "/img-original" + IMG + urlPrefix + id + "_p" + i + urlSuffix);
        }
        return list;
    }

    public void setUrls(PixivUrls urls) {
        this.urls = urls;
        String original = urls.getOriginal().replace(DOMAIN, "");
        int pidIndex = original.indexOf(String.valueOf(id));
        int imgIndex = original.indexOf(IMG);
        urlPrefix = original.substring(imgIndex + IMG.length(), pidIndex);
        if (!original.contains("ugoira")) {
            urlSuffix = original.substring(original.lastIndexOf('.'));
        }
        this.checkSeconds = System.currentTimeMillis() / 1000;
    }

    public String getCreateDateTime() {
        return TimeUtil.second2String(createSeconds);
    }

    public String getCheckDateTime() {
        return checkSeconds != null ? TimeUtil.second2String(checkSeconds) : null;
    }

    public String getUploadDateTime() {
        return TimeUtil.second2String(uploadSeconds);
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
        this.createSeconds = getEpochSecond(createDate);
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
        this.uploadSeconds = getEpochSecond(uploadDate);
    }

    private static long getEpochSecond(String date) {
        return ZonedDateTime.parse(date).withZoneSameInstant(ZONE_ID).toInstant().getEpochSecond();
    }
}
