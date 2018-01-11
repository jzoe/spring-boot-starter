package com.github.quartz.examples.controller;

import com.github.quartz.examples.util.JsonUtil;
import com.quartz.model.entity.QrtzTimedTask;
import com.quartz.utils.ScheduleUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quartz")
public class QuartzController {

    @PostMapping("/addJob")
    public Object addJob(QrtzTimedTask qrtzTimedTask) {
        ScheduleUtil.initJob(qrtzTimedTask);
        return JsonUtil.success();
    }
}
