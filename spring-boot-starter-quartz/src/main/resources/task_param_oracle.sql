-- Create table
create table AMS_TIMED_TASK_PARAM_TD
(
  param_id    INTEGER not null,
  param_key   VARCHAR2(100) not null,
  param_value VARCHAR2(1000) not null,
  param_type  VARCHAR2(100),
  param_desc  VARCHAR2(1000),
  task_name   VARCHAR2(200)
);
-- Add comments to the columns
comment on column AMS_TIMED_TASK_PARAM_TD.param_id
is '参数主键';
comment on column AMS_TIMED_TASK_PARAM_TD.param_key
is '参数代码';
comment on column AMS_TIMED_TASK_PARAM_TD.param_value
is '参数值';
comment on column AMS_TIMED_TASK_PARAM_TD.param_type
is '参数类型(不填默认为String类型)';
comment on column AMS_TIMED_TASK_PARAM_TD.param_desc
is '参数描述';
-- Create/Recreate primary, unique and foreign key constraints
alter table AMS_TIMED_TASK_PARAM_TD
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