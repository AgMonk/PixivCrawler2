package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.entity.Config;
import com.gin.pixivcrawler.entity.response.Res;
import com.gin.pixivcrawler.service.ConfigService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 配置
 *
 * @author bx002
 * @date 2021/2/18 9:12
 */
@RequestMapping("config")
@RestController
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @RequestMapping("get")
    public Res<Config> getConfig() {
        return Res.success(configService.getConfig());
    }

    @RequestMapping("update")
    public Res<Void> update(@RequestBody Config config) {
        configService.updateConfig(config);
        return Res.success();
    }
}
