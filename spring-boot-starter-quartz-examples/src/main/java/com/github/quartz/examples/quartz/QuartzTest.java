package com.github.quartz.examples.quartz;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.stereotype.Component;/** * Created by chenmin on 17/11/19. */@Componentpublic class QuartzTest {    private static final Logger logger = LoggerFactory.getLogger(QuartzTest.class);    public void test01() {        logger.info("QuartzTest.test01");    }    public void test02() {        logger.info("QuartzTest.test02");    }    public void test03() {        logger.info("QuartzTest.test03");    }    public void test04() {        logger.info("QuartzTest.test04");    }}