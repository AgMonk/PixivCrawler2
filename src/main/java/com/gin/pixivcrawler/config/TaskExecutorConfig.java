package com.gin.pixivcrawler.config;

import com.gin.pixivcrawler.utils.TasksUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author bx002
 * @date 2021/2/2 15:16
 */
@Configuration
public class TaskExecutorConfig {
    @Bean
    public ThreadPoolTaskExecutor detailExecutor() {
        return TasksUtil.getExecutor("detail", 5);
    }

    @Bean
    public ThreadPoolTaskExecutor bookmarksExecutor() {
        return TasksUtil.getExecutor("bookmarks", 3);
    }

    @Bean
    public ThreadPoolTaskExecutor tagExecutor() {
        return TasksUtil.getExecutor("tag", 1);
    }

    @Bean
    public ThreadPoolTaskExecutor searchExecutor() {
        return TasksUtil.getExecutor("search", 1);
    }

    @Bean
    public ThreadPoolTaskExecutor mainExecutor() {
        return TasksUtil.getExecutor("main", 1);
    }

    @Bean
    public TaskScheduler scheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        taskScheduler.setThreadNamePrefix("scheduler-");
        taskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //调度器shutdown被调用时等待当前被调度的任务完成
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        //等待时长
        taskScheduler.setAwaitTerminationSeconds(60);
        return taskScheduler;
    }
}
