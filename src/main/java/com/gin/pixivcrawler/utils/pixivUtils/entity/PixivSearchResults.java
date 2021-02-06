package com.gin.pixivcrawler.utils.pixivUtils.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Pixiv搜索结果
 *
 * @author bx002
 * @date 2021/2/2 9:26
 */
@Data
public class PixivSearchResults implements Serializable {
    PixivBookmarks illustManga;
    HashMap<String, HashMap<String, String>> tagTranslation;

}
