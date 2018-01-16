package com.github.quartz.examples;

import com.github.quartz.schedule.EnableQuartz;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableQuartz
//@PropertySource(value = "classpath:application.yml", ignoreResourceNotFound = true)
public class QuartzApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuartzApplication.class, args);
    }
}
