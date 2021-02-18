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
     *
     * @return com.gin.pixivcrawler.entity.Config
     * @author Gin
     * @date 2021/2/17 15:09
     */
    Config getConfig();

    /**
     * 更新配置
     *
     * @param config 配置
     * @return void
     * @author bx002
     * @date 2021/2/18 9:12
     */
    void updateConfig(Config config);
}
