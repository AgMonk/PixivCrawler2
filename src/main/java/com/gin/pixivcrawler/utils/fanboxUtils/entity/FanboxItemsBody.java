package com.gin.pixivcrawler.utils.fanboxUtils.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author bx002
 */
@Data
public class FanboxItemsBody implements Serializable {
    String text;
    List<FanboxItemsBodyImage> images;
}
