package com.quartz.model;

import com.quartz.model.entity.QrtzTimedTask;
import org.springframework.jdbc.core.JdbcTemplate;

public class TaskExecuter {

    private String taskTableName;
    private String taskParamTableName;

    private JdbcTemplate jdbcTemplate;

    private String INSERT_SQL = "INSERT INTO " + taskTableName + "(TASK_ID, TASK_NAME, TASK_DESC, TASK_EXPRES, TASK_METHOD, TASK_CLASS, TASK_GROUP, STATUS, CREATE_TIME, CREATER)\n" +
            "VALUES(3, 'QuartzTest03', 'quartz测试3', '0/5 * * * * ?', 'test03', 'com.github.quartz.examples.quartz.QuartzTest', 'quartzTest', 'U', sysdate, 'haha')";

    public void addTask(QrtzTimedTask qrtzTimedTask) {
        String sql = "INSERT INTO ";
        jdbcTemplate.execute("");
    }

    public TaskExecuter setTaskTableName(String taskTableName) {
        this.taskTableName = taskTableName;
        return this;
    }

    public TaskExecuter setTaskParamTableName(String taskParamTableName) {
        this.taskParamTableName = taskParamTableName;
        return this;
    }

    public TaskExecuter setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        return this;
    }
}
