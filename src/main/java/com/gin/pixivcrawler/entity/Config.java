package com.gin.pixivcrawler.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

import static com.gin.pixivcrawler.utils.JsonUtil.prettyJson;

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
    @TableId(type = IdType.AUTO)
    Integer id;
    Long userId;
    Integer queryMaxOfAria2 = 10;
    Integer queryMaxOfDetail = 30;
    String rootPath = "c:/illust";
    /**
     * 下载模式 0 = 直接访问 1 = 10809代理 2 = pixiv.cat代理
     */
    Integer downloadMode = 0;
    /**
     * 文件名模板
     * $uid$  用户id
     * $uname$ 用户名
     * $uac$ 用户账号
     * $pid$ pid
     * $bmc$ 收藏数
     * $title$
     * $tags$
     */
    String filePathTemplate = "/[uid_$uid$][uac_$uac$][uname_$uname$]/[bmc_$bmc$][$pid$][title_$title$][tags_$tags$]";

    @Override
    public String toString() {
        return prettyJson(this);
    }
}
