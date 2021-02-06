package com.gin.pixivcrawler.utils.pixivUtils.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bx002
 * @date 2021/2/2 16:56
 */
@TableName("t_pixiv_cookie")
@Data
public class PixivCookie implements Serializable {
    @TableId
    Long userId;
    String cookie;
    String tt;
}
