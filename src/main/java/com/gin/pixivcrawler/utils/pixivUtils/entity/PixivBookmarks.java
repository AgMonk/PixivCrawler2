package com.gin.pixivcrawler.utils.pixivUtils.entity;

import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetailInBookmarks;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Pixiv收藏
 *
 * @author bx002
 * @date 2021/2/2 9:27
 */
@Data
public class PixivBookmarks implements Serializable {
    Integer total;
    List<PixivIllustDetailInBookmarks> works;
}
