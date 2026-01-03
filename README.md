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

## 命令行跑通（无 IDE，可选）

> 说明：本项目原始结构为传统 Web 工程（非 Maven）。仓库已补充 `pom.xml`，方便在命令行直接构建与用 Jetty 启动。
> Java 源码文件为 **GBK** 编码，`pom.xml` 已按 GBK 配置编译编码。

1. 启动 MySQL（推荐 Docker）：

```bash
docker rm -f hotelssm-mysql >/dev/null 2>&1 || true

docker run --name hotelssm-mysql \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=db_hotel_ssm \
  -p 3306:3306 \
  -d mysql:5.7 \
  --character-set-server=utf8 --collation-server=utf8_general_ci
```

然后导入脚本（显式指定 UTF-8，避免中文数据“å…”乱码）：

```bash
docker exec -i hotelssm-mysql mysql --default-character-set=utf8 -uroot -p123456 db_hotel_ssm < db_hotel_ssm.sql
```

2. 构建并启动：

```bash
mvn -DskipTests package
mvn -DskipTests jetty:run
```

3. 访问：
   - 前台：`http://localhost:8080/home/index`
   - 后台：`http://localhost:8080/system/login`

详细步骤见：`docs/运行与部署指南.md`

## 文档入口

- 运行与部署指南：`docs/运行与部署指南.md`
- 项目结构说明：`docs/项目结构说明.md`
- 开发差距与待办清单：`docs/开发差距与待办清单.md`
- 协作开发规则：`docs/协作开发规则.md`
