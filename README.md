### 项目说明
- 一键生成考卷系统的主要目标是简化和自动化教师或教育机构在出卷过程中的工作，提高出卷效率，并保持试卷质量。
- 采用SpringBoot、Shiro、MyBatis-Plus、Vue3、TypeScript、Element Plus、Vue Router、Pinia、Axios、Vite框架。
<br>


<br>

### 具有如下特点
- 友好的代码结构及注释，便于阅读及二次开发
- 灵活的权限控制，可控制到页面或按钮，满足绝大部分的权限需求
- 提供CrudService接口，对增删改查进行封装，代码更简洁
- 页面交互使用Vue3.x，极大的提高了开发效率
- 完善的学校管理及数据权限，通过注解实现数据权限的控制
- 完善的XSS防范及脚本过滤，彻底杜绝XSS攻击
- 完善的代码生成机制，可在线生成entity、xml、dao、service、vue、sql代码，减少70%以上的开发任务
- 引入quartz定时任务，可动态完成任务的添加、修改、删除、暂停、恢复及日志查看等功能
- 引入Hibernate Validator校验框架，轻松实现后端校验
- 引入云存储服务，已支持：七牛云、阿里云、腾讯云等
- 引入swagger文档支持，方便编写API接口文档

<br>

### 数据权限设计思想
- 用户管理、角色管理、学校管理
- 菜单管理、定时任务、参数管理、字典管理、系统日志，没有数据权限
- 业务功能，按照用户数据权限，查询、操作数据【没有本学校数据权限，也能查询本人数据】

<br> 


**项目结构**
```
ocepgen
├─ocepgen-common     公共模块
│ 
├─ocepgen-admin      管理后台
│    ├─db  数据库SQL脚本
│    │ 
│    ├─modules  模块
│    │    ├─job 定时任务
│    │    ├─log 日志管理
│    │    ├─oss 文件存储
│    │    ├─security 安全模块
│    │    ├─sys 系统管理(核心)
│    │    ├─exam 考卷管理模块
│    │    ├─question 题库管理模块
│    │    └─course 课程管理模块
│    │ 
│    └─resources 
│        ├─mapper   MyBatis文件
│        ├─public  静态资源
│        └─application.yml   全局配置文件
│       
│ 
├─ocepgen-generator  代码生成器
│        └─resources 
│           ├─mapper   MyBatis文件
│           ├─template 代码生成器模板（可增加或修改相应模板）
│           ├─application.yml    全局配置文件
│           └─generator.properties   代码生成器，配置文件
│ 
├─ocepgen-ui        Vue3前端工程
```

<br>

**技术选型：**
- 核心框架：Spring Boot 2.7
- 安全框架：Apache Shiro 1.12
- 持久层框架：MyBatis 3.5
- 定时器：Quartz 2.3
- 数据库连接池：Druid 1.2
- 日志管理：Logback
- 页面交互：Vue3.x

<br>

**软件需求**
- JDK1.8
- Maven3.0+
- MySQL8.0
- Oracle 11g+
- SQLServer 2012+
- PostgreSQL 9.4+
- 达梦8
<br>


**本地部署**
- 通过git下载源码
- idea、eclipse需安装lombok插件，不然会提示找不到entity的get set方法
- 创建数据库ocepgen，数据库编码为UTF-8
- 执行db/mysql.sql文件，初始化数据
- 修改application-dev.yml文件，更新MySQL账号和密码
- 在ocepgen目录下，执行mvn clean install
- Eclipse、IDEA运行AdminApplication.java，则可启动项目【ocepgen-admin】
- ocepgen-admin访问路径：http://localhost:8080/ocepgen-admin
- swagger文档路径：http://localhost:8080/ocepgen-admin/doc.html
- 账号密码：admin/admin
<br>

<br>

### 如何交流、反馈、参与贡献？
- 开发文档：https://www.renren.io/guide/security
- 官方社区：https://www.renren.io/community
- Gitee仓库：https://gitee.com/renrenio/renren-security
- [人人开源](https://www.renren.io)：https://www.renren.io
<br>

