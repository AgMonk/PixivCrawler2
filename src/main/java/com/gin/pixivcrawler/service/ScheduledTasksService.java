package com.gin.pixivcrawler.service;

import com.gin.pixivcrawler.entity.StatusReport;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 周期任务业务
 *
 * @author bx002
 * @date 2021/2/3 11:28
 */
public interface ScheduledTasksService {

    void downloadUntagged() throws InterruptedException, ExecutionException, TimeoutException;

    /**
     * 状态报告
     *
     * @return com.gin.pixivcrawler.entity.StatusReport
     * @author bx002
     * @date 2021/2/4 17:37
     */
    StatusReport getStatusReport();
}
