package com.gin.pixivcrawler.utils.pixivUtils.entity.details;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gin.pixivcrawler.utils.timeUtils.TimeUnit;
import com.gin.pixivcrawler.utils.timeUtils.TimeUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 收藏中的详情
 *
 * @author bx002
 * @date 2021/2/2 17:43
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PixivIllustDetailInBookmarks extends PixivDetailBase {
    @JSONField(name = "tags", serialize = false)
    @JsonIgnore
    List<String> tagsList;
    @JSONField(serialize = false)
    @JsonIgnore
    String updateDate;
    @JSONField(serialize = false)
    @JsonIgnore
    Long updateSeconds;

    String url;

    public String getUpdateDateTime() {
        return TimeUtils.formatTime(updateSeconds, TimeUnit.SECONDS);
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
        this.updateSeconds = getEpochSecond(updateDate);
    }

    public String getTagString() {
        return String.join(",", tagsList);
    }
}
