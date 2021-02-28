package com.gin.pixivcrawler.utils.pixivUtils.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Pixiv错误
 *
 * @author bx002
 * @date 2021/2/28 11:43
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PixivErrorException extends Exception implements Serializable {
    Boolean error = true;
    String message;
}
