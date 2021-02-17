package com.gin.pixivcrawler.service;

import com.gin.pixivcrawler.entity.Config;

/**
 * 运行配置业务
 *
 * @author Gin
 * @date 2021/2/17 14:45
 */
public interface ConfigService {
    /**
     * 获取当前配置
     * @return com.gin.pixivcrawler.entity.Config
     * @author Gin
     * @date 2021/2/17 15:09
     */
    Config getConfig();
}
