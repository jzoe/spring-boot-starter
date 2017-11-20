package com.quartz.model;

import com.quartz.model.entity.QrtzTimedTask;
import com.quartz.model.entity.QrtzTimedTaskParam;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TaskExecuter {

    private String taskTableName;
    private String taskParamTableName;

    private JdbcTemplate jdbcTemplate;

    private String INSERT_TASK_SQL = "INSERT INTO " + taskTableName + "(TASK_ID, TASK_NAME, TASK_DESC, TASK_EXPRES, TASK_METHOD, TASK_CLASS, TASK_GROUP, STATUS, CREATE_TIME, CREATER) " +
            "VALUES(SEQ_TASK.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, sysdate, ?)";
    private String INSERT_TASK_PARAM_SQL = "INSERT INTO " + taskParamTableName + "(PARAM_ID, PARAM_KEY, PARAM_VALUE, PARAM_DESC, SORT_ID, TASK_NAME) " +
            "VALUES (SEQ_TASK_PARAM.NEXTVAL, ?, ?, ?, ?, ?)";
    private String UPDATE_TASK_SQL = "UPDATE " + taskTableName + " T SET {} WHERE T.TASK_NAME = ?";
    private String UPDATE_TASK_PARAM_SQL = "UPDATE " + taskParamTableName + " T SET {} WHERE T.TASK_NAME = ? AND T.PARAM_KEY = ?";

    public void addTask(QrtzTimedTask qrtzTimedTask) {
        jdbcTemplate.update(INSERT_TASK_SQL, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, qrtzTimedTask.getTaskName());
                ps.setString(2, qrtzTimedTask.getTaskDesc());
                ps.setString(3, qrtzTimedTask.getTaskExpres());
                ps.setString(4, qrtzTimedTask.getTaskMethod());
                ps.setString(5, qrtzTimedTask.getTaskClass());
                ps.setString(6, qrtzTimedTask.getTaskGroup());
                ps.setString(7, "U");
                ps.setString(8, qrtzTimedTask.getCreater());
            }
        });
    }

    public void addTaskParam(QrtzTimedTaskParam qrtzTimedTaskParam) {
        jdbcTemplate.update(INSERT_TASK_PARAM_SQL, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, qrtzTimedTaskParam.getParamKey());
                ps.setString(2, qrtzTimedTaskParam.getParamValue());
                ps.setString(3, qrtzTimedTaskParam.getParamDesc());
                ps.setLong(4, qrtzTimedTaskParam.getSortId());
                ps.setString(5, qrtzTimedTaskParam.getTaskName());
            }
        });
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
