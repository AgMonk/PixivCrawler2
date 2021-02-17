package com.gin.pixivcrawler.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 运行配置
 *
 * @author Gin
 * @date 2021/2/17 14:27
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("t_config")
public class Config implements Serializable {
    @TableId(type= IdType.AUTO)
    Integer id;
    Long userId;
    Integer queryMaxOfAria2 = 10;
    Integer queryMaxOfDetail = 30;
    String rootPath = "c:/illust";
}
