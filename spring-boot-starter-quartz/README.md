# spring-boot-starter-quartz

针对公司业务要求，对quartz做了简单的封装，使任务定义简单化，支持相同任务实现，
做不同任务执行，同时支持表配置传参功能，动态更新任务，新增任务，删除任务。以下是在项目中具体使用方法:

## 1、导入spring-boot-starter-quartz包

- maven

~~~xml
<dependency>
  	<groupId>com.github.quartz</groupId>
    <artifactId>spring-boot-starter-quartz</artifactId>
    <version>1.0</version>
</dependency>
~~~

- gradle

~~~groovy
compile('com.github.quartz:spring-boot-starter-quartz:1.0')
~~~

## 2、新建任务配置表：

脚本可在发布包的根目录中获得（task_oracle.sql）,Quartz集群的相关表
可在Quartz的发布包org.quartz.impl.jdbcjobstore目录下找到相关SQL。

- 任务配置表（QRTZ_TIMED_TASK）

~~~sql
-- Create table
create table QRTZ_TIMED_TASK
(
  task_name   VARCHAR2(200),
  task_desc   VARCHAR2(500),
  task_expres VARCHAR2(100),
  task_method VARCHAR2(200),
  task_class  VARCHAR2(200),
  task_group  VARCHAR2(200) default 0,
  status      VARCHAR2(100) default 'U',
  create_time DATE default sysdate,
  creater     VARCHAR2(200)
);
-- Add comments to the columns
comment on column QRTZ_TIMED_TASK.task_name
is '任务名称';
comment on column QRTZ_TIMED_TASK.task_desc
is '任务描述';
comment on column QRTZ_TIMED_TASK.task_expres
is '任务执行表达式';
comment on column QRTZ_TIMED_TASK.task_method
is '任务执行方法';
comment on column QRTZ_TIMED_TASK.task_class
is '任务接口路径';
comment on column QRTZ_TIMED_TASK.task_group
is '任务分组';
comment on column QRTZ_TIMED_TASK.status
is '任务状态';
comment on column QRTZ_TIMED_TASK.create_time
is '创建时间';
comment on column QRTZ_TIMED_TASK.creater
is '创建人员';
-- Create/Recreate primary, unique and foreign key constraints
alter table QRTZ_TIMED_TASK
  add primary key (TASK_NAME)
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
~~~

- 任务参数配置表（QRTZ_TIMED_TASK_PARAM）

~~~sql
-- Create table
create table QRTZ_TIMED_TASK_PARAM
(
  param_key   VARCHAR2(100) not null,
  param_value VARCHAR2(1000) not null,
  param_type  VARCHAR2(100),
  param_desc  VARCHAR2(1000),
  task_name   VARCHAR2(200),
  sort_id     INTEGER
);
-- Add comments to the columns
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
is '排序';
-- Create/Recreate primary, unique and foreign key constraints
alter table QRTZ_TIMED_TASK_PARAM
  add primary key (param_key,task_name)
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
~~~

## 3、开发任务接口

任务的开发需按照以下标准开发：

- 任务接口需大写 I 开头，大写 SV 结尾
- 接口实现类需去掉接口的 I 开头，然后在 SV 后加 Impl
- 接口如需参数，必须为Map
- 接口无返回值

如：

~~~java
public interface IHelloSV {
    void hello();
  	void hello(Map param);
}
public class HelloSVImpl implements IHelloSV {
    public void hello() {}
  	public void hello(Map param) {}
}
~~~

## 4、任务配置

在配置任务时，按照以下要求配置：

- 优先配置接口类
- 如需配置接口参数，在参数表中指定参数名，参数值，并指定task_name
- 参数配置时，参数类型可为空，默认使用Map\<String, String\>  接收，当指定参数类型时，
    使用Map\<String, Object\> 接收，在代码中可使用强制类型转换
- 参数类型只支持基本类型，配置时使用全路径 

如：

~~~sql
INSERT INTO QRTZ_TIMED_TASK (TASK_NAME, TASK_DESC, TASK_EXPRES, TASK_METHOD, TASK_CLASS, TASK_GROUP, STATUS, CREATE_TIME, CREATER)
VALUES ('hello1', '测试1', '0/10 * * * * ?', 'hello', 'com.IHelloSV', 'hello', 'U', sysdate, null);
INSERT INTO QRTZ_TIMED_TASK (TASK_NAME, TASK_DESC, TASK_EXPRES, TASK_METHOD, TASK_CLASS, TASK_GROUP, STATUS, CREATE_TIME, CREATER)
VALUES ('hello2', '测试2', '0/10 * * * * ?', 'hello', 'com.IHelloSV', 'hello', 'U', sysdate, null);
INSERT INTO QRTZ_TIMED_TASK_PARAM (PARAM_KEY, PARAM_VALUE, PARAM_TYPE, PARAM_DESC, TASK_NAME)
VALUES ('name', 'admin', null, '测试参数', 'hello2');
~~~

## 5、quartz配置

此项目默认采用quartz集群方式。具体配置可在发布包根目录下的quartz.properties中查看。
如需自定义配置，可在自己项目的classpath下新建quartz.properties文件进行配置。

## 6、任务动态更新

- 新增任务

在QRTZ_TIMED_TASK表中新增任务配置之后，将STATUS字段改为S即可。

- 删除任务

将QRTZ_TIMED_TASK表要删除的数据的STATUS字段改为D即可。

- 修改任务

任务只支持修改以下几项：

QRTZ_TIMED_TASK.TASK_EXPRES,
QRTZ_TIMED_TASK_PARAM.PARAM_KEY,
QRTZ_TIMED_TASK_PARAM.PARAM_VALUE,
QRTZ_TIMED_TASK_PARAM.PARAM_TYPE,
QRTZ_TIMED_TASK_PARAM.PARAM_DESC,

其中，当只修改了QRTZ_TIMED_TASK时，只有TASK_EXPRES生效；修改了QRTZ_TIMED_TASK_PARAM配置之后，QRTZ_TIMED_TASK的其他配置都是可以修改的。
