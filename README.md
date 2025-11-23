# 🚀 Enterprise Asset Management (EAM) Platform

![Java](https://img.shields.io/badge/Java-21%2B-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green) ![Vue](https://img.shields.io/badge/Vue-3.0-blue) ![Status](https://img.shields.io/badge/Status-Evolutionary%20Prototype-purple)

> 基于真实企业痛点（Real-world Pain Points）构建的演化型高保真原型。
> 融合了 **固定资产全生命周期管理** 与 **财务折旧自动化引擎**。

## 📖 项目简介 (Introduction)
本项目不仅仅是一个 CRUD 系统，而是基于我在**税友集团 (Servyou Group)** 实习期间对企业资产管理痛点的深度洞察而设计的。它旨在解决传统 Excel 管理模式下的**数据孤岛、折旧计算误差、盘点效率低下**等核心问题。

作为**演化型原型 (Evolutionary Prototype)**，本项目采用了 **AI-Assisted Agile Development** 模式，在两周内完成了从需求分析到核心业务闭环的构建，并预留了向 ERP（企业资源计划）系统演进的接口。

## ✨ 核心亮点 (Key Highlights)

### 1. 财务级折旧引擎 (Depreciation Engine)
- **算法实现**: 内置 **年限平均法 (Straight-line)** 与 **双倍余额递减法** 核心算法。
- **精度控制**: 采用 `BigDecimal` 确保金额计算精度，自动处理尾差倒挤，符合会计准则。

### 2. 复杂业务闭环
- **全生命周期**: 采购 -> 入库 -> 领用/借用 -> 调拨 -> 维修 -> 报废/处置。
- **审计追踪**: 每一笔资产变动均通过 `Asset_Log` 记录快照，支持全链路回溯。

### 3. 数字化盘点 (Digital Inventory)
- 支持生成资产唯一二维码。
- **离线/在线双模**: 模拟手持终端（PDA）逻辑，支持断网盘点暂存与联网同步。

### 4. 高性能与可视化
- **ECharts 驾驶舱**: 实时计算资产分布、价值构成与地点热力图。
- **异步处理**: 针对大批量 Excel 导入与月结折旧计算，设计了基于任务队列的异步处理机制（架构设计层面）。

## 🛠 技术栈 (Tech Stack)

| 领域 | 技术选型 | 说明 |
| :--- | :--- | :--- |
| **Backend** | Java 21, Spring Boot 3.2 | 核心业务逻辑, Modular Monolith 架构 |
| **ORM** | MyBatis + MyBatis Plus | 数据持久化, 动态 SQL |
| **Database** | MySQL 8.0 | 事务支持, JSON 字段扩展 |
| **Frontend** | Vue 3, TypeScript, Vite | 组合式 API, 极速构建 |
| **UI/Viz** | Element Plus, ECharts | 企业级组件库, 数据可视化 |
| **DevOps** | Docker, Maven | 容器化部署, 依赖管理 |
| **AI Ops** | Cursor, LLM Optimization | **AI 辅助编码与单元测试生成** |

## 📸 系统演示 (Screenshots)
- Dashboard 驾驶舱：资产状态分布、分类数量柱状图、地点价值 Top10  
  `img/截屏2025-11-23 15.52.34.png`
- 资产管理列表：筛选、分页与操作入口  
  `img/截屏2025-11-23 15.52.45.png`
- 财政资产卡片：列表、筛选与编辑弹窗  
  `img/截屏2025-11-23 15.52.58.png`
- 资产盘点核实：盘实/盘盈/盘亏/待盘统计与列表  
  `img/截屏2025-11-23 15.53.06.png`
- 资产设置：部门人员与地点树管理  
  `img/截屏2025-11-23 15.54.09.png`

## 🚀 快速开始 (Quick Start)

### 环境要求
- JDK 21+
- Node.js ≥ 18
- MySQL 8.0 (or Docker)

### 1. 启动基础设施
```bash
docker compose up -d asset-mysql
# 默认端口: 3306, 账号/密码: root/root
