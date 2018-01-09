package com.quartz.jdbc.execute;

import com.quartz.jdbc.QuartzRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public abstract class TaskExecuter {

    private static final Logger logger = LoggerFactory.getLogger(TaskExecuter.class);

    private QuartzRepository quartzRepository;

    public TaskExecuter(QuartzRepository quartzRepository) {
        Assert.notNull(quartzRepository, "quartzRepository must not be null");
        this.quartzRepository = quartzRepository;
    }

    public QuartzRepository getQuartzRepository() {
        return quartzRepository;
    }

    public void setQuartzRepository(QuartzRepository quartzRepository) {
        this.quartzRepository = quartzRepository;
    }
}
