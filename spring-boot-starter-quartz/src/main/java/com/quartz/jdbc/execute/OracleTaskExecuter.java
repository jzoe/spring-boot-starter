package com.quartz.jdbc.execute;

import com.quartz.jdbc.QuartzRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OracleTaskExecuter extends TaskExecuter {

    private static final Logger logger = LoggerFactory.getLogger(OracleTaskExecuter.class);

    public OracleTaskExecuter(QuartzRepository quartzRepository) {
        super(quartzRepository);
    }
}
