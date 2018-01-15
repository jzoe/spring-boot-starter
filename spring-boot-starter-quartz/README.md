[TOC]

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

脚本可在发布包的根目录中获得

Quartz集群的相关表可在Quartz的发布包org.quartz.impl.jdbcjobstore目录下找到相关SQL。

【https://github.com/quartz-scheduler/quartz/blob/master/quartz-core/src/main/resources/org/quartz/impl/jdbcjobstore】

- 以下以Oracle的脚本为例

>  任务配置表（QRTZ_TIMED_TASK）

~~~sql
-- CREATE TABLE
CREATE TABLE QRTZ_TIMED_TASK
(
  TASK_NAME   VARCHAR2(200),
  TASK_DESC   VARCHAR2(500),
  TASK_EXPRES VARCHAR2(100),
  TASK_METHOD VARCHAR2(200),
  TASK_CLASS  VARCHAR2(200),
  TASK_GROUP  VARCHAR2(200) DEFAULT 0,
  STATUS      VARCHAR2(100) DEFAULT 'U',
  CREATE_TIME DATE DEFAULT SYSDATE,
  CREATER     VARCHAR2(200),
  MODIFY_TIME DATE DEFAULT SYSDATE,
  MODIFIER    VARCHAR2(200)
);
-- ADD COMMENTS TO THE COLUMNS
COMMENT ON COLUMN QRTZ_TIMED_TASK.TASK_NAME
IS '任务名称';
COMMENT ON COLUMN QRTZ_TIMED_TASK.TASK_DESC
IS '任务描述';
COMMENT ON COLUMN QRTZ_TIMED_TASK.TASK_EXPRES
IS '任务执行表达式';
COMMENT ON COLUMN QRTZ_TIMED_TASK.TASK_METHOD
IS '任务执行方法';
COMMENT ON COLUMN QRTZ_TIMED_TASK.TASK_CLASS
IS '任务接口路径';
COMMENT ON COLUMN QRTZ_TIMED_TASK.TASK_GROUP
IS '任务分组';
COMMENT ON COLUMN QRTZ_TIMED_TASK.STATUS
IS '任务状态';
COMMENT ON COLUMN QRTZ_TIMED_TASK.CREATE_TIME
IS '创建时间';
COMMENT ON COLUMN QRTZ_TIMED_TASK.CREATER
IS '创建人员';
COMMENT ON COLUMN QRTZ_TIMED_TASK.MODIFY_TIME
IS '修改时间';
COMMENT ON COLUMN QRTZ_TIMED_TASK.MODIFIER
IS '修改人员';
-- CREATE/RECREATE PRIMARY, UNIQUE AND FOREIGN KEY CONSTRAINTS
ALTER TABLE QRTZ_TIMED_TASK
  ADD PRIMARY KEY (TASK_NAME)
  USING INDEX
  PCTFREE 10
  INITRANS 2
  MAXTRANS 255
  STORAGE
  (
  INITIAL 64K
  NEXT 1M
  MINEXTENTS 1
  MAXEXTENTS UNLIMITED
  );
-- CREATE INDEX
CREATE INDEX IDX_TIMED_STATUS ON QRTZ_TIMED_TASK (STATUS)
  PCTFREE 10
  INITRANS 2
  MAXTRANS 255
  STORAGE (
    INITIAL 64K
    NEXT 1M
    MINEXTENTS 1
    MAXEXTENTS UNLIMITED
  );
~~~

> 任务参数配置表（QRTZ_TIMED_TASK_PARAM）

~~~sql
-- CREATE TABLE
CREATE TABLE QRTZ_TIMED_TASK_PARAM
(
  PARAM_KEY   VARCHAR2(100) NOT NULL,
  PARAM_VALUE VARCHAR2(1000) NOT NULL,
  PARAM_TYPE  VARCHAR2(100),
  PARAM_DESC  VARCHAR2(1000),
  TASK_NAME   VARCHAR2(200) NOT NULL,
  SORT_ID     INTEGER DEFAULT 0
);
-- ADD COMMENTS TO THE COLUMNS
COMMENT ON COLUMN QRTZ_TIMED_TASK_PARAM.PARAM_KEY
IS '参数代码';
COMMENT ON COLUMN QRTZ_TIMED_TASK_PARAM.PARAM_VALUE
IS '参数值';
COMMENT ON COLUMN QRTZ_TIMED_TASK_PARAM.PARAM_TYPE
IS '参数类型(不填默认为STRING类型)';
COMMENT ON COLUMN QRTZ_TIMED_TASK_PARAM.PARAM_DESC
IS '参数描述';
COMMENT ON COLUMN QRTZ_TIMED_TASK_PARAM.TASK_NAME
IS '任务名称';
COMMENT ON COLUMN QRTZ_TIMED_TASK_PARAM.SORT_ID
IS '参数顺序';
-- CREATE/RECREATE PRIMARY, UNIQUE AND FOREIGN KEY CONSTRAINTS
ALTER TABLE QRTZ_TIMED_TASK_PARAM
  ADD PRIMARY KEY (PARAM_KEY, TASK_NAME)
  USING INDEX
  PCTFREE 10
  INITRANS 2
  MAXTRANS 255
  STORAGE
  (
  INITIAL 64K
  NEXT 1M
  MINEXTENTS 1
  MAXEXTENTS UNLIMITED
  );
-- CREATE INDEX
CREATE INDEX IDX_TIMED_TASK_PARAM_NAME ON QRTZ_TIMED_TASK_PARAM (TASK_NAME)
  PCTFREE 10
  INITRANS 2
  MAXTRANS 255
  STORAGE (
    INITIAL 64K
    NEXT 1M
    MINEXTENTS 1
    MAXEXTENTS UNLIMITED
  );
~~~

解释：

1. QRTZ_TIMED_TASK.TASK_NAME : 任务名称，任务在调度工厂中的唯一标识，再配合参数表的配置，可实现一个接口，配置多个任务的功能
2. QRTZ_TIMED_TASK.STATUS : 表示任务状态，分为四种状态，U表示有效运行，即此任务在工程启动之后即可正常运行；E表示无效任务，即此任务无效，不会自动运行；D表示此任务要删除，调度工厂删除任务后，任务状态改为E；S表示将启动此任务，任务启动成功后，状态改为U
3. QRTZ_TIMED_TASK.TASK_CLASS : 任务类路径，类必须加入到Spring上下文中，可支持配置接口，实现类，或者普通类。
4. QRTZ_TIMED_TASK_PARAM.SORT_ID : 参数顺序，默认使用LinkedHashMap存储，可通过参数顺序，实现通用接口，不同参数配置而实现一个接口多任务配置

## 3、开发任务接口

任务的开发需按照以下标准开发：

- 任务接口需大写 I 开头，大写 SV 结尾
- 接口实现类需去掉接口的 I 开头，然后在 SV 后加 Impl
- 接口方法参数必须为Map

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
INSERT INTO QRTZ_TIMED_TASK (TASK_NAME, TASK_DESC, TASK_EXPRES, TASK_METHOD, TASK_CLASS, TASK_GROUP, STATUS)
VALUES ('hello1', '测试1', '0/10 * * * * ?', 'hello', 'com.IHelloSV', 'hello', 'U');
INSERT INTO QRTZ_TIMED_TASK (TASK_NAME, TASK_DESC, TASK_EXPRES, TASK_METHOD, TASK_CLASS, TASK_GROUP, STATUS)
VALUES ('hello2', '测试2', '0/10 * * * * ?', 'hello', 'com.IHelloSV', 'hello', 'U');
INSERT INTO QRTZ_TIMED_TASK_PARAM (PARAM_KEY, PARAM_VALUE, PARAM_TYPE, PARAM_DESC, TASK_NAME)
VALUES ('name', 'admin', 'java.lang.String', '测试参数', 'hello2');
~~~

## 5、quartz配置

此项目默认采用quartz单机配置方式。具体配置可在发布包根目录下的quartz.properties中查看。
如需使用集群配置，可在自己项目的classpath下新建quartz.properties文件进行配置。

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

**注：** 当只修改了QRTZ_TIMED_TASK时，只有TASK_EXPRES生效；修改了QRTZ_TIMED_TASK_PARAM配置之后，QRTZ_TIMED_TASK的其他配置都是可以修改的。

## 7、Quartz集群相关表介绍

> qrtz_fired_triggers

触发器与任务关联表,存储与已触发的Trigger相关的状态信息，以及相联Job的执行信息。

> qrtz_simple_triggers

存储简单的Trigger，包括重复次数，间隔，以及已触发的次数

> qrtz_simprop_triggers

> qrtz_cron_triggers

存储CronTrigger，包括Cron表达式和时区信息

> qrtz_blob_triggers

Trigger作为Blob类型存储(用于Quartz用户用JDBC创建他们自己定制的Trigger类型，JobStore 并不知道如何存储实例的时候)

> qrtz_triggers

存储已配置的 Trigger的信息

> qrtz_job_details

存储每一个已配置的Job的详细信息

> qrtz_calendars

以Blob类型存储Quartz的Calendar日历信息， quartz可配置一个日历来指定一个时间范围

> qrtz_paused_trigger_grps

存储已暂停的Trigger组的信息

> qrtz_locks

存储程序的非观锁的信息(假如使用了悲观锁)

> qrtz_scheduler_state

存储少量的有关 Scheduler的状态信息，和别的 Scheduler 实例(假如是用于一个集群中)

