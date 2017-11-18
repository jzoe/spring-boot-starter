package com.github.quartz.examples.controller;

import com.quartz.entity.QrtzTimedTask;
import com.quartz.utils.ScheduleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quartz")
public class QuartzController {

    @Autowired
    private ScheduleUtil scheduleUtil;

    @PostMapping("/addJob")
    public Object addJob(QrtzTimedTask qrtzTimedTask) {
        return null;
    }
}
