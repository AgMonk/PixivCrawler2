package com.gin.pixivcrawler.utils.pixivUtils.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * PixivUrls
 *
 * @author bx002
 * @date 2021/2/2 10:09
 */
@Data
public class PixivUrls implements Serializable {
    String mini;
    String original;
    String regular;
    String small;
    String thumb;

}
