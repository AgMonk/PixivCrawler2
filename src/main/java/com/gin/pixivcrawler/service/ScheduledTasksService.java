package com.gin.pixivcrawler.service;

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
}
