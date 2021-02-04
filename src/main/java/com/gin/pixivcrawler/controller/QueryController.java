package com.gin.pixivcrawler.controller;

import com.gin.pixivcrawler.entity.StatusReport;
import com.gin.pixivcrawler.entity.response.Res;
import com.gin.pixivcrawler.service.ScheduledTasksService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 查询接口
 *
 * @author bx002
 * @date 2021/2/4 17:27
 */
@RestController
@RequestMapping("query")
public class QueryController {
    private final ScheduledTasksService scheduledTasksService;

    public QueryController(ScheduledTasksService scheduledTasksService) {
        this.scheduledTasksService = scheduledTasksService;
    }

    @RequestMapping("status")
    public Res<StatusReport> getStatusReport() {
        return Res.success(scheduledTasksService.getStatusReport());
    }
}
