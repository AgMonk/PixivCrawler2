package com.gin.pixivcrawler.utils.pixivUtils.entity.details;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gin.pixivcrawler.utils.TimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.stream.Collectors;

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
        return TimeUtil.second2String(updateSeconds);
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
        this.updateSeconds = getEpochSecond(updateDate);
    }

    public String getTagString() {
        return String.join(",", tagsList);
    }
}
