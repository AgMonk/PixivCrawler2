package com.gin.pixivcrawler.utils.fanboxUtils.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author Gin
 */
@Slf4j
@Data
public class FanboxItemsBodyFile implements Serializable {
    String id;
    String name;
    String extension;
    long size;
    String url;
}
