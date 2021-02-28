package com.gin.pixivcrawler.service;

import com.gin.pixivcrawler.entity.StatusReport;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 周期任务业务
 *
 * @author bx002
 * @date 2021/2/3 11:28
 */
public interface ScheduledTasksService {

    HashMap<String, Boolean> getScheduledTasksSwitch();

    void turnSwitch(String key, boolean status);

    void downloadUntagged() throws InterruptedException, ExecutionException, TimeoutException;

    @Async("searchExecutor")
    @Scheduled(cron = "5 0/5 * * * ?")
    void autoSearch();

    /**
     * 状态报告
     *
     * @return com.gin.pixivcrawler.entity.StatusReport
     * @author bx002
     * @date 2021/2/4 17:37
     */
    StatusReport getStatusReport();
}
