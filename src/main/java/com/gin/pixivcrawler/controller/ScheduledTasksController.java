package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.entity.response.Res;
import com.gin.pixivcrawler.service.ScheduledTasksService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 周期任务
 *
 * @author bx002
 * @date 2021/2/28 9:01
 */
@RestController
@RequestMapping("scheduledTasks")
public class ScheduledTasksController {
    private final ScheduledTasksService scheduledTasksService;

    public ScheduledTasksController(ScheduledTasksService scheduledTasksService) {
        this.scheduledTasksService = scheduledTasksService;
    }

    @RequestMapping("turn")
    public Res<Void> turn(String key, Boolean status) {
        scheduledTasksService.turnSwitch(key, status);
        return Res.success(String.format("%s 开关设置为 %s", key, status));
    }
}
