-- Create table
create table QRTZ_TIMED_TASK_TD
(
  task_id     INTEGER not null,
  task_name   VARCHAR2(200),
  task_desc   VARCHAR2(500),
  task_expres VARCHAR2(100),
  task_method VARCHAR2(200),
  task_class  VARCHAR2(200),
  task_group  VARCHAR2(200) default 0,
  status      VARCHAR2(100) default 'U',
  create_time DATE default sysdate,
  creater     VARCHAR2(200),
  ext1        VARCHAR2(500),
  ext2        VARCHAR2(500),
  ext3        TIMESTAMP(6),
  ext4        TIMESTAMP(6)
);
-- Add comments to the columns
comment on column QRTZ_TIMED_TASK_TD.task_id
is '任务编码';
comment on column QRTZ_TIMED_TASK_TD.task_name
is '任务名称';
comment on column QRTZ_TIMED_TASK_TD.task_desc
is '任务描述';
comment on column QRTZ_TIMED_TASK_TD.task_expres
is '任务执行表达式';
comment on column QRTZ_TIMED_TASK_TD.task_method
is '任务执行方法';
comment on column QRTZ_TIMED_TASK_TD.task_class
is '任务接口路径';
comment on column QRTZ_TIMED_TASK_TD.task_group
is '任务分组';
comment on column QRTZ_TIMED_TASK_TD.status
is '任务状态';
comment on column QRTZ_TIMED_TASK_TD.create_time
is '创建时间';
comment on column QRTZ_TIMED_TASK_TD.creater
is '创建人员';
comment on column QRTZ_TIMED_TASK_TD.ext1
is '扩展字段1';
comment on column QRTZ_TIMED_TASK_TD.ext2
is '扩展字段2';
comment on column QRTZ_TIMED_TASK_TD.ext3
is '扩展字段3';
comment on column QRTZ_TIMED_TASK_TD.ext4
is '扩展字段4';
-- Create/Recreate primary, unique and foreign key constraints
alter table QRTZ_TIMED_TASK_TD
  add primary key (TASK_ID)
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
alter table QRTZ_TIMED_TASK_TD
  add unique (TASK_NAME)
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
