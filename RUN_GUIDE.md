# 本地运行快速教程（零基础）

## 1. 环境准备（macOS，推荐 Homebrew）
```bash
# 安装 JDK 21、Maven、Node.js 18+
brew install openjdk@21 maven node

# 安装 Docker（用于启动 MySQL）
brew install --cask docker

# 可选：安装 IntelliJ IDEA 社区版
brew install --cask intellij-idea-ce
```
> 安装 Docker 后需手动打开「Docker Desktop」并等待启动完成；需要 mysql 客户端可执行 `brew install mysql`。

## 2. 获取项目路径
假设仓库已克隆到 `/Users/xuan/Desktop/enterprise-asset`，后续命令均在该目录下执行。

## 3. 启动数据库（Docker）
```bash
cd /Users/xuan/Desktop/enterprise-asset
docker compose up -d asset-mysql
```
- 端口/账号：`3306`，`root/root`，数据库名 `enterprise_asset_db`  
- 数据文件在 `mysql_data/`，重启容器数据不丢。
- 如使用自有 MySQL，请确保连接串与 `enterprise-asset-backend/src/main/resources/application.yml` 保持一致。

## 4. 启动后端（Spring Boot）
```bash
cd /Users/xuan/Desktop/enterprise-asset/enterprise-asset-backend
./mvnw spring-boot:run
```
- 首次启动会自动执行 `schema.sql` + `data.sql`，创建表并写入演示数据。
- 服务地址 `http://localhost:8080/api`。
- 演示账号：手机号 `17620927807`，密码 `123456`。

## 5. 启动前端（Vue 3 + Vite）
```bash
cd /Users/xuan/Desktop/enterprise-asset/enterprise-salary-platform
npm install          # 首次需要
npm run dev          # 默认 http://localhost:5173
```
- 已配置代理 `/api -> http://localhost:8080`，确保后端运行后再启动前端。
- 生产构建：`npm run build`。

## 6. 验证
- MySQL：`docker ps` 查看 `asset-mysql` 是否运行；或 `mysql -uroot -proot -h127.0.0.1 -P3306` 连接。
- 后端：访问 `http://localhost:8080/api/dashboard` 应返回 JSON。
- 前端：浏览器打开 `http://localhost:5173`，使用演示账号登录后进入看板。

## 7. 常见问题
- 端口占用：如 3306/8080 被占用，可修改 `docker-compose.yml` 或 `enterprise-asset-backend/src/main/resources/application.yml`，前端代理在 `vite.config.ts`。
- JDK 版本：`pom.xml` 要求 `<java.version>21</java.version>`，若本机版本较低可暂时下调后重启。
- Docker 未启动：若报数据库连接失败，请先启动 Docker Desktop 并确保 `asset-mysql` 运行。
