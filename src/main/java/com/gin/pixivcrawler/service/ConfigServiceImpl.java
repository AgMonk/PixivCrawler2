package com.gin.pixivcrawler.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gin.pixivcrawler.dao.ConfigDao;
import com.gin.pixivcrawler.entity.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Gin
 * @date 2021/2/17 14:46
 */
@Service
@Slf4j
public class ConfigServiceImpl extends ServiceImpl<ConfigDao, Config> implements ConfigService {
    private Config currentConfig;

    @Override
    public Config getConfig() {
        if (this.currentConfig == null) {
            this.currentConfig = getById(1);
            log.info("载入配置 UID:{} 根目录:{} Aria2队列长度:{} 详情队列长度:{} 下载模式:{}"
                    , this.currentConfig.getUserId()
                    , this.currentConfig.getRootPath()
                    , this.currentConfig.getQueryMaxOfAria2()
                    , this.currentConfig.getQueryMaxOfDetail()
                    , this.currentConfig.getDownloadMode()
            );
        }
        return this.currentConfig;
    }

    @Override
    public void updateConfig(Config config) {
        log.info("更新配置 {}", config);
        updateById(config);
    }

}
