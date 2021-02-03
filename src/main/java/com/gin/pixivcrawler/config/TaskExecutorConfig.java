package com.gin.pixivcrawler.config;

import com.gin.pixivcrawler.utils.TasksUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author bx002
 * @date 2021/2/2 15:16
 */
@Configuration
public class TaskExecutorConfig {
    @Bean
    public ThreadPoolTaskExecutor detailExecutor(){ return TasksUtil.getExecutor("detail", 10); }
    @Bean
    public ThreadPoolTaskExecutor bookmarksExecutor(){ return TasksUtil.getExecutor("bookmarks", 3); }

}
