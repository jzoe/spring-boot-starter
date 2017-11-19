-- Create table
create table QRTZ_TIMED_TASK_PARAM
(
  param_id    INTEGER not null,
  param_key   VARCHAR2(100) not null,
  param_value VARCHAR2(1000) not null,
  param_type  VARCHAR2(100),
  param_desc  VARCHAR2(1000),
  task_name   VARCHAR2(200) not NULL,
  sort_id        INTEGER
);
-- Add comments to the columns
comment on column QRTZ_TIMED_TASK_PARAM.param_id
is '参数主键';
comment on column QRTZ_TIMED_TASK_PARAM.param_key
is '参数代码';
comment on column QRTZ_TIMED_TASK_PARAM.param_value
is '参数值';
comment on column QRTZ_TIMED_TASK_PARAM.param_type
is '参数类型(不填默认为String类型)';
comment on column QRTZ_TIMED_TASK_PARAM.param_desc
is '参数描述';
comment on column QRTZ_TIMED_TASK_PARAM.task_name
is '任务名称';
comment on column QRTZ_TIMED_TASK_PARAM.sort_id
is '参数顺序';

-- Create/Recreate primary, unique and foreign key constraints
alter table QRTZ_TIMED_TASK_PARAM
  add primary key (PARAM_ID)
  using index
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
  initial 64K
  next 1M
  minextents 1
  maxextents unlimited
  );
