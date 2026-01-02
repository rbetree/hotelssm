# 酒店服务管理系统（SSM）

本仓库为作者的 J2EE 课程设计项目，基于 **Spring + Spring MVC + MyBatis**（SSM）实现，采用 **JSP + Tomcat** 的 B/S 架构。

## 技术栈

- JDK：1.8+
- Web 容器：Tomcat 8.5/9
- 数据库：MySQL 5.7（其他版本可自行适配）
- 核心框架：Spring / Spring MVC / MyBatis
- 视图：JSP（`WebContent/WEB-INF/views/`）

## 项目结构（传统 Web 工程结构，非 Maven）

- Java 源码：`src/`
- 配置文件：`src/config/`
- Web 资源：`WebContent/`
  - `WebContent/WEB-INF/web.xml`：Web 入口配置
  - `WebContent/WEB-INF/views/`：JSP 页面
  - `WebContent/resources/`：静态资源

更详细说明见：`docs/项目结构说明.md`

## 快速启动（本地）

1. 导入数据库脚本：`db_hotel_ssm.sql`
2. 修改数据库连接：`src/config/db.properties`
3. 使用 IDEA/Eclipse 将本项目作为 Web 工程导入，并配置 Tomcat 运行。
4. 浏览器访问：
   - 前台入口：`/home/index`
   - 后台入口：`/system/login`

详细步骤见：`docs/运行与部署指南.md`

## 文档入口

- 运行与部署指南：`docs/运行与部署指南.md`
- 项目结构说明：`docs/项目结构说明.md`
- 开发差距与待办清单：`docs/开发差距与待办清单.md`
