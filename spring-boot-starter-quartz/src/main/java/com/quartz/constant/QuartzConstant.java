package com.quartz.constant;

public class QuartzConstant {
    public static final String TASK_PREFIX_SUBST = "{0}";
    public static final String PARAM_PREFIX_SUBST = "{1}";
    public static final String TASK_PREFIX = "QRTZ";  // 默认表前缀
    public static final String TASK_NAME_SUFFIX = "_TIMED_TASK";  // 默认任务配置表名
    public static final String TASK_PARAM_NAME_SUFFIX = "_TIMED_TASK_PARAM";   // 默认任务参数表名

    public static final String SELECT_TASK_SQL = "SELECT * FROM " + TASK_PREFIX_SUBST + " T WHERE T.STATUS = 'U'";
    public static final String SELECT_TASK_PARAM_SQL = "SELECT * FROM " + PARAM_PREFIX_SUBST + " T WHERE T.TASK_NAME = ? ORDER BY T.SORT_ID";
}
