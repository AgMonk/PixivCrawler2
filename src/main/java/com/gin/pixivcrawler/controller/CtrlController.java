package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.entity.response.Res;
import com.gin.pixivcrawler.service.ScheduledTasksService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 控制接口
 *
 * @author bx002
 * @date 2021/2/4 17:41
 */
@RestController
@RequestMapping("ctrl")
public class CtrlController {
    private final ScheduledTasksService scheduledTasksService;

    public CtrlController(ScheduledTasksService scheduledTasksService) {
        this.scheduledTasksService = scheduledTasksService;
    }

    @RequestMapping("downloadUntagged")
    public Res<Void> downloadUntagged() throws InterruptedException, ExecutionException, TimeoutException {
        scheduledTasksService.downloadUntagged();

        return Res.success();
    }

}
