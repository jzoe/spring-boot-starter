DROP TABLE IF EXISTS QRTZ_TIMED_TASK;
DROP TABLE IF EXISTS QRTZ_TIMED_TASK_PARAM;

CREATE TABLE QRTZ_TIMED_TASK
(
  TASK_NAME   VARCHAR(200),
  TASK_DESC   VARCHAR(500),
  TASK_EXPRES VARCHAR(100),
  TASK_METHOD VARCHAR(200),
  TASK_CLASS  VARCHAR(200),
  TASK_GROUP  VARCHAR(200) DEFAULT 0,
  STATUS      VARCHAR(100) DEFAULT 'U',
  CREATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CREATER     VARCHAR(200),
  MODIFY_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  MODIFIER    VARCHAR(200),
  PRIMARY KEY(TASK_NAME)
);
CREATE TABLE QRTZ_TIMED_TASK_PARAM
(
  PARAM_KEY   VARCHAR(100) NOT NULL,
  PARAM_VALUE VARCHAR(1000) NOT NULL,
  PARAM_TYPE  VARCHAR(100),
  PARAM_DESC  VARCHAR(1000),
  TASK_NAME   VARCHAR(200) NOT NULL,
  SORT_ID     INTEGER DEFAULT 0,
  PRIMARY KEY(PARAM_KEY, TASK_NAME)
);