package com.gin.pixivcrawler.utils.ngaUtils.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * nga响应
 *
 * @author bx002
 * @date 2021/1/14 11:16
 */
@Data
public class NgaRes implements Serializable {
    NgaResBase data;
    List<String> error;
    Long time;
}