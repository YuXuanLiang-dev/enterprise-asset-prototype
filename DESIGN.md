# 🏗 System Design & Architecture Document

## 1. 设计哲学 (Design Philosophy)
本系统遵循 **领域驱动设计 (DDD)** 的轻量级实践，将核心业务逻辑与基础设施解耦。设计重点在于**数据一致性**（财务数据）与**用户体验流畅性**（复杂流程交互）。

> **战略目标**: 从单一的资产管理系统 (AMS) 演进为具备财务集成能力的 ERP 核心模块。

## 2. 系统架构 (System Architecture)

### 2.1 逻辑架构 (Modular Monolith)
后端采用模块化单体架构，为未来拆分微服务预留边界：
- **Core Domain**: `AssetContext` (资产全生命周期), `DepreciationContext` (折旧核心)
- **Support Domain**: `AuthContext` (RBAC 权限), `OrgContext` (组织架构)
- **Infrastructure**: `FileService` (Excel 解析/导出), `TaskQueue` (异步任务)

### 2.2 核心交互流程
1.  **认证流**: JWT Token + `X-Enterprise-Id` 租户头，实现逻辑上的多租户隔离。
2.  **折旧流**: 定时任务 (Quartz) -> 触发折旧计算策略 (Strategy Pattern) -> 生成财务凭证草稿 -> 写入 `fiscal_asset_cards`。
3.  **盘点流**: 快照生成 (Snapshot) -> 移动端填报 -> 差异比对 (Diff Logic) -> 生成盘盈/盘亏报告。

## 3. 核心算法与业务逻辑 (Core Logic)

### 3.1 资产折旧算法 (Depreciation Algo)
为满足会计实务要求，系统实现了以下核心算法：
* **年限平均法 (Straight-line):**
    $$\text{月折旧额} = \frac{\text{原值} \times (1 - \text{残值率})}{\text{预计使用月数}}$$
    *Implementation Detail:* 使用 `BigDecimal` 处理浮点运算，最后通过**尾差倒挤法**（Last Period Adjustment）消除精度误差。

### 3.2 状态机模型 (State Machine)
资产状态流转严格受控，防止非法操作：
- `IDLE` (闲置) <-> `IN_USE` (在用)
- `IN_USE` -> `REPAIR` (维修) -> `IDLE`
- `*` -> `SCRAP` (报废) [终态]

## 4. 数据库设计 (Schema Design)

采用 MySQL 8.0，核心表设计如下：

| 表名 | 说明 | 关键设计 |
| :--- | :--- | :--- |
| `assets` | 资产主表 | 包含 `version` 字段 (乐观锁) 防止并发修改 |
| `fiscal_asset_cards` | 财务卡片 | 记录原值、累计折旧、净值；与主表 1:1 关联 |
| `asset_events` | 事件溯源表 | 记录 `event_type` (领用/调拨/折旧), `snapshot` (修改前快照) |
| `inventory_tasks` | 盘点任务 | 定义盘点范围（部门/位置），状态（进行中/已封账） |
| `sys_org` | 组织架构 | 闭包表 (Closure Table) 或 邻接表设计，支持无限层级 |

## 5. AI 辅助开发方法论 (AI-Assisted Methodology)
本项目探索了 **Human-in-the-loop** 的开发模式：
1.  **Architecture Definition**: 人工定义领域模型、接口契约与核心算法逻辑。
2.  **Code Generation**: 使用 Cursor/LLM 生成 CRUD 样板代码与单元测试 (JUnit)。
3.  **Review & Refactor**: 人工进行 Code Review，专注于业务逻辑的边界情况 (Edge Cases) 处理。

## 6. 未来演进路线 (Roadmap to ERP)
- [ ] **Phase 2**: 集成 **Workflow Engine** (如 Flowable)，实现动态审批流配置。
- [ ] **Phase 3**: 引入 **RabbitMQ**，解耦折旧计算与报表生成的高峰负载。
- [ ] **Phase 4**: 财务模块对接，自动生成会计凭证 (Voucher)，向标准 ERP 转型。