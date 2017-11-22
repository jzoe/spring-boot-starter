package com.quartz.model;

import com.quartz.model.assist.DbType;
import com.quartz.model.entity.QrtzTimedTask;
import com.quartz.model.entity.QrtzTimedTaskParam;
import com.quartz.utils.StringUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TaskExecuter {

    private DbType dbType;

    private String taskTableName;
    private String taskParamTableName;

    private JdbcTemplate jdbcTemplate;

    private String INSERT_TASK_SQL = "INSERT INTO " + taskTableName + "(TASK_ID, TASK_NAME, TASK_DESC, TASK_EXPRES, TASK_METHOD, TASK_CLASS, TASK_GROUP, STATUS, CREATE_TIME, CREATER) " +
            "VALUES(SEQ_TASK.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, sysdate, ?)";
    private String INSERT_TASK_PARAM_SQL = "INSERT INTO " + taskParamTableName + "(PARAM_ID, PARAM_KEY, PARAM_VALUE, PARAM_DESC, SORT_ID, TASK_NAME) " +
            "VALUES (SEQ_TASK_PARAM.NEXTVAL, ?, ?, ?, ?, ?)";
    private String UPDATE_TASK_SQL = "UPDATE " + taskTableName + " T SET {} WHERE T.TASK_NAME = ?";
    private String UPDATE_TASK_PARAM_SQL = "UPDATE " + taskParamTableName + " T SET {} WHERE T.TASK_NAME = ? AND T.PARAM_KEY = ?";

    public void save(final QrtzTimedTask qrtzTimedTask) {
        jdbcTemplate.update(INSERT_TASK_SQL, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, qrtzTimedTask.getTaskName());
                ps.setString(2, qrtzTimedTask.getTaskDesc() == null ? "" : qrtzTimedTask.getTaskDesc());
                ps.setString(3, qrtzTimedTask.getTaskExpres());
                ps.setString(4, qrtzTimedTask.getTaskMethod());
                ps.setString(5, qrtzTimedTask.getTaskClass());
                ps.setString(6, qrtzTimedTask.getTaskGroup() == null ? "" : qrtzTimedTask.getTaskGroup());
                ps.setString(7, "U");
                ps.setString(8, qrtzTimedTask.getCreater() == null ? "" : qrtzTimedTask.getCreater());
            }
        });
    }

    public void save(final QrtzTimedTaskParam qrtzTimedTaskParam) {
        jdbcTemplate.update(INSERT_TASK_PARAM_SQL, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, qrtzTimedTaskParam.getParamKey());
                ps.setString(2, qrtzTimedTaskParam.getParamValue());
                ps.setString(3, qrtzTimedTaskParam.getParamDesc() == null ? "" : qrtzTimedTaskParam.getParamDesc());
                ps.setLong(4, qrtzTimedTaskParam.getSortId());
                ps.setString(5, qrtzTimedTaskParam.getTaskName());
            }
        });
    }

    private Map<String, Object> build(Object obj) {
        Class clazz = obj.getClass();
        Field[] fields = clazz.getFields();
        Map<String, Object> maps = new HashMap<String, Object>();
        for (Field field : fields) {
            String fieldName = field.getName();
            String columnName = StringUtil.underscoreName(fieldName);
            Method method = ReflectionUtils.findMethod(obj.getClass(), StringUtil.getGetter(fieldName));
            Object value = ReflectionUtils.invokeMethod(method, obj);
            if (value instanceof Date) {
                value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
                if(value.toString().endsWith("00:00:00")){
                    value = value.toString().substring(0, value.toString().lastIndexOf(" "));
                }
            }
            maps.put(columnName, value);
        }
        return maps;
    }

    private String build(Map<String, Object> maps) {
        StringBuilder sb = new StringBuilder();
        int i = maps.values().size();
        for (String column : maps.keySet()) {
            i--;
            Object value = maps.get(column);
            if (value != null) {
                sb.append(column + " = " + value.toString());
                if (i > 1) {
                    sb.append(",");
                }
            }
        }
        return sb.toString();
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

    public TaskExecuter setDbType(DbType dbType) {
        this.dbType = dbType;
        return this;
    }
}
