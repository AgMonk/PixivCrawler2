package com.gin.pixivcrawler.utils.fanboxUtils;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bx002
 */
@Data
@TableName("t_fanbox_cookie")
public class FanboxCookie implements Serializable {
    @TableId(type = IdType.AUTO)
    Long id;
    String cookie;
}
