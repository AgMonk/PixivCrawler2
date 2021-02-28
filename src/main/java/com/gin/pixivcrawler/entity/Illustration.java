package com.gin.pixivcrawler.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * 详情
 *
 * @author bx002
 * @date 2021/2/28 10:47
 */
@Data
public class Illustration implements Serializable {
    @JSONField(alternateNames = {"illustTitle", "title"})
    String illustTitle;
    String tag;
    String fileName;
    Integer pageCount;
    String urlSuffix;
    String urlPrefix;
    String userName;
    Long userId;
    Integer bookmarkCount;
    Integer illustType;
    Long id;

    public void setFileName(String fileName) {
        this.fileName = fileName;
        urlSuffix = fileName.substring(fileName.lastIndexOf("."));
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix.replace("https://i.pximg.net/img-original/img/", "");
        this.urlPrefix += this.urlPrefix.endsWith("/") ? "" : "/";
    }

    public static void main(String[] args) {

    }
}
