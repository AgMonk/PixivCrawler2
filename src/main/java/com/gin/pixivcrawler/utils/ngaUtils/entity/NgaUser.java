package com.gin.pixivcrawler.utils.ngaUtils.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * NGA用户信息
 *
 * @author bx002
 * @date 2021/1/14 14:02
 */
@Data
@TableName("t_nga_user")
public class NgaUser implements Serializable {
    @TableId
    Long uid;
    String username;
}
