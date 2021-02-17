package com.gin.pixivcrawler.utils.pixivUtils.entity.details;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gin.pixivcrawler.entity.ConstantValue;
import com.gin.pixivcrawler.utils.TimeUtil;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTag;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivTagsInDetail;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivUrls;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Pixiv作品详情
 *
 * @author bx002
 * @date 2021/2/2 9:25
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@Accessors(chain = true)
@TableName("t_illust_detail")
public class PixivIllustDetail extends PixivDetailBase implements Serializable {
    public final static Pattern PIXIV_ILLUST_FULL_NAME = Pattern.compile("\\d+_p\\d+");

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
     * 赞数
     */
    @TableField(exist = false)
    Integer likeCount;

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
     * 上传时间
     */
    @TableField(exist = false)
    @JSONField(serialize = false)
    @JsonIgnore
    String uploadDate;
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
     * 浏览数
     */
    @TableField(exist = false)
    Long viewCount;
    /**
     * 检查时间
     */
    @JsonIgnore
    @JSONField(serialize = false)
    Long checkSeconds;

    public void setTagsInDetail(PixivTagsInDetail tagsInDetail) {
        this.tagsInDetail = tagsInDetail;
        this.tagString = tagsInDetail.getTags().stream().map(PixivTag::getTag).collect(Collectors.joining(ConstantValue.DELIMITER_COMMA));
        this.tagTransString = tagsInDetail.getTags().stream().map(PixivTag::getTransRaw).collect(Collectors.joining(ConstantValue.DELIMITER_COMMA));
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

    public String getUploadDateTime() {
        return TimeUtil.second2String(uploadSeconds);
    }


    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
        this.uploadSeconds = getEpochSecond(uploadDate);
    }

    public String getCheckDateTime() {
        return checkSeconds != null ? TimeUtil.second2String(checkSeconds) : null;
    }

}
