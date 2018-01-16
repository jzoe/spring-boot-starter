[TOC]

# spring-boot-starter-quartz

针对公司业务要求，针对springboot，对quartz做了简单的封装，将其做成SpringBoot的一个Starter模块。主要功能有：

1. 任务配置动态更新，增加，停止，立即执行
2. 任务立即执行
3. quartz集群节点是否开启实现可配置化

后续功能继续增加中。。。

以下是在项目中具体使用方法:

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

## 2、开启Quartz自动配置

目前提供了@EnableQuartz，@EnableQuartzBean，@EnableQuartzDataBase三个开启自动配置注解。

@EnableQuartz：开启所有的自动配置

@EnableQuartzBean：开启quartz远程调用客户端配置

@EnableQuartzDataBase：开启quartz远程调用客户端及quartz相关基础Bean的配置

## 3、任务配置表

脚本可在发布包的根目录中获得

Quartz集群的相关表可在Quartz的发布包org.quartz.impl.jdbcjobstore目录下找到相关SQL。

Quartz官方源码地址：【https://github.com/quartz-scheduler/quartz/blob/master/quartz-core/src/main/resources/org/quartz/impl/jdbcjobstore】

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

## 4、开发任务接口

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

## 5、任务配置

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

## 6、quartz配置

此项目默认采用quartz单机配置方式。具体配置可在发布包根目录下的quartz.properties中查看。
如需使用集群配置，可在自己项目的classpath下新建quartz.properties文件进行配置。

## 7、任务动态更新

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

## 8、Quartz集群相关表介绍

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

## 9、quartz时间表达式

时间格式：s>m>h>d>m>w(?)>y(?)  对应：秒>分>小时>日>月>周>年

- Cron表达式的符号、格式

| 特殊字符 | 含义                                       |
| :--: | :--------------------------------------- |
|  ＊   | 匹配所有的值。如：＊在分钟的字段域里表示 每分钟                 |
|  ?   | 只在日期域和星期域中使用。它被用来指定“非明确的值”               |
|  -   | 指定一个范围。如：“10-12”在小时域意味着“在10点到12点之间”      |
|  ,   | 指定几个可选值。如：“MON,WED,FRI”在星期域里表示“星期一、星期三、星期五” |
|  /   | 指定增量。如：“0/15”在秒域表示在每分钟的第0秒开始，每15秒执行一次。符号“*”在“/”前面等价于0在“/”前面 |
|  L   | 表示day-of-month和day-of-week域，但在两个字段中的意思不同，例如day-of-month域中表示一个月的最后一天。如果在day-of-week域表示“7”或者“SAT”，如果在day-of-week域中前面加上数字，表示一个月的最后几天，例如“6L”就表示一个月的最后一个星期五 |
|  W   | 只允许日期域出现。这个字符用于指定日期的最近工作日。例如：如果你在日期域中写“15W”，表示：这个月15号最近的工作日。所以，如果15号是周六，则任务会在14号触发。如果15刚好是周日，则任务会在周一也就是16号触发。如果是在日期域填写“1W”即使1号是周六，那么任务也只会在下周一，也就是3号触发，“W”字符指定的最近工作日是不能跨月份的。字符“W”只能配合一个单独的数值使用，不能够是一个数字段，如：1-15W是错误的 |
|  LW  | L和W可以在日期域中联合使用，LW表示这个月最后一周的工作日           |
|  #   | 只允许在星期域中出现。这个字符用于指定本月的某某天。例如：“6#3”表示本月第三周的星期五（6表示星期五，3表示第三周）。“2#1”表示本月第一周的星期一。“4#5”表示第五周的星期三 |
|  C   | 允许在日期域和星期域出现。这个字符依靠一个指定的“日历”。也就是说这个表达式的值依赖于相关的“日历”的计算结果，如果没有“日历”关联，则等价于所有包含的“日历”。如：日期域是“5C”表示关联“日历”中第一天，或者这个月开始的第一天的后5天。星期域是“1C”表示关联“日历”中第一天，或者星期的第一天的后1天，也就是周日的后一天（周一） |

- Cron表达式特殊字符意义对应表

| 字段    | 允许值          | 允许的特殊字符         |
| ----- | ------------ | --------------- |
| 秒     | 0-59         | , - * /         |
| 分     | 0-59         | , - * /         |
| 小时    | 0-23         | , - * /         |
| 月内日期  | 1-31         | , - * ? / L W C |
| 月     | 1-12或JAN-DEC | , - * /         |
| 周内日期  | 1-7或SUN-SAT  | , - * ? / L C # |
| 年（可选） | 留空，1970-2099 | , - * /         |

时间表达式在线生成器：http://cron.qqe2.com/