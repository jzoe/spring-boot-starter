package com.quartz.model.execute;

import com.quartz.model.annotation.Id;
import com.quartz.model.assist.DbType;
import com.quartz.model.entity.QrtzTimedTask;
import com.quartz.model.entity.QrtzTimedTaskParam;
import com.quartz.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ObjectUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.quartz.constant.QuartzConstant.ARGS1;
import static com.quartz.constant.QuartzConstant.ARGS2;

public abstract class TaskExecuter {

    private static final Logger logger = LoggerFactory.getLogger(TaskExecuter.class);

    protected DbType dbType;

    protected String taskTableName;
    protected String taskParamTableName;
    protected String tableId;

    protected JdbcTemplate jdbcTemplate;

    protected String INSERT_TASK_SQL = "INSERT INTO " + taskTableName + "(" + ARGS1 + ") " +
            "VALUES(" + ARGS2 + ")";
    protected String INSERT_TASK_PARAM_SQL = "INSERT INTO " + taskParamTableName + "(" + ARGS1 + ") " +
            "VALUES (" + ARGS2 + ")";
//    protected String INSERT_TASK_SQL = "INSERT INTO " + taskTableName + "(TASK_ID, TASK_NAME, TASK_DESC, TASK_EXPRES, TASK_METHOD, TASK_CLASS, TASK_GROUP, STATUS, CREATE_TIME, CREATER) " +
//            "VALUES(SEQ_TASK.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, sysdate, ?)";
//    protected String INSERT_TASK_PARAM_SQL = "INSERT INTO " + taskParamTableName + "(PARAM_ID, PARAM_KEY, PARAM_VALUE, PARAM_DESC, SORT_ID, TASK_NAME) " +
//            "VALUES (SEQ_TASK_PARAM.NEXTVAL, ?, ?, ?, ?, ?)";
    protected String UPDATE_TASK_SQL = "UPDATE " + taskTableName + " T SET " + ARGS1 + " WHERE T.TASK_NAME = ?";
    protected String UPDATE_TASK_PARAM_SQL = "UPDATE " + taskParamTableName + " T SET " + ARGS1 + " WHERE T.TASK_NAME = ? AND T.PARAM_KEY = ?";

    public void save(final QrtzTimedTask qrtzTimedTask) {
        jdbcTemplate.update(getITaskSQL(qrtzTimedTask));
    }

    public void save(final QrtzTimedTaskParam qrtzTimedTaskParam) {
        jdbcTemplate.update(getITaskParamSQL(qrtzTimedTaskParam));
    }

    public void update(final QrtzTimedTask qrtzTimedTask) {
        jdbcTemplate.update(getUTaskSQL(qrtzTimedTask));
    }

    public void update(final QrtzTimedTaskParam qrtzTimedTaskParam) {
        jdbcTemplate.update(getUTaskParamSQL(qrtzTimedTaskParam));
    }

    protected abstract String getITaskSQL(QrtzTimedTask qrtzTimedTask);
    protected abstract String getITaskParamSQL(QrtzTimedTaskParam qrtzTimedTaskParam);
    protected abstract String getUTaskSQL(QrtzTimedTask qrtzTimedTask);
    protected abstract String getUTaskParamSQL(QrtzTimedTaskParam qrtzTimedTaskParam);

    protected String buildUSQL(Object obj) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        StringBuilder sb = new StringBuilder();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            // 过滤class属性
            if (!key.equals("class")) {
                String columnName = StringUtil.underscoreName(key).toUpperCase();
                Id id = obj.getClass().getDeclaredField(key).getAnnotation(Id.class);
                if (id != null) {
                    this.setTableId(key);
                    continue;
                }
                // 得到property对应的getter方法
                Method getter = property.getReadMethod();
                if (getter == null) {
                    continue;
                }
                Object value = getter.invoke(obj);
                if (!ObjectUtils.isEmpty(value)) {
                    if (value instanceof Date) {
                        value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
                        sb.append(columnName + " = " + "DATE'" + value.toString() + "'");
                    } else {
                        sb.append(columnName + " = '" + value.toString() + "'");
                    }
                    sb.append(",");
                }
            }
        }
        return sb.toString().substring(0, sb.toString().lastIndexOf(","));
    }

    protected Map<String, Object> buildISQL(Object obj) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            // 过滤class属性
            if (!key.equals("class")) {
                String columnName = StringUtil.underscoreName(key).toUpperCase();
                Id id = obj.getClass().getDeclaredField(key).getAnnotation(Id.class);
                if (id != null) {
                    this.setTableId(key);
                    continue;
                }
                // 得到property对应的getter方法
                Method getter = property.getReadMethod();
                if (getter == null) {
                    continue;
                }
                Object value = getter.invoke(obj);
                if (!ObjectUtils.isEmpty(value)) {
                    columns.append(columnName + ",");
                    if (value instanceof Date) {
                        value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
                        values.append("DATE'" + value.toString() + "'");
                    } else {
                        values.append("'" + value.toString() + "'");
                    }
                    values.append(",");
                }
            }
        }
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("COLUMNS", StringUtil.remove(columns.toString(),","));
        maps.put("VALUES", StringUtil.remove(values.toString(), ","));
        return maps;
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

    public String getTableId() {
        return tableId;
    }

    public TaskExecuter setTableId(String tableId) {
        this.tableId = tableId;
        return this;
    }
}
