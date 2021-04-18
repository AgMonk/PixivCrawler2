package com.gin.pixivcrawler.utils.fanboxUtils.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author bx002
 */
@Data
public class FanboxItemsBodyImage implements Serializable {
    String id;
    /**
     * 后缀
     */
    String extension;
    Integer width;
    Integer height;
    String originalUrl;
    String thumbnailUrl;
}
