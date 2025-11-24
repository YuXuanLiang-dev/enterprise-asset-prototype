-- Enterprise Asset Platform - 初始数据库脚本
-- 作用：创建 enterprise_asset_db 库、建表并写入演示数据
-- 用法：mysql -uroot -proot < database_init.sql

CREATE DATABASE IF NOT EXISTS enterprise_asset_db
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE enterprise_asset_db;

-- 企业信息
CREATE TABLE IF NOT EXISTS enterprises (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 基础用户
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL,
    name VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户可访问的企业
CREATE TABLE IF NOT EXISTS user_enterprises (
    user_id BIGINT NOT NULL,
    enterprise_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, enterprise_id),
    INDEX idx_ue_user (user_id),
    INDEX idx_ue_ent (enterprise_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 部门与人员
CREATE TABLE IF NOT EXISTS departments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    enterprise_id BIGINT NOT NULL,
    name VARCHAR(128) NOT NULL,
    parent_id BIGINT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_dept_parent (parent_id),
    INDEX idx_dept_ent (enterprise_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS personnel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    enterprise_id BIGINT NOT NULL,
    name VARCHAR(128) NOT NULL,
    dept_id BIGINT NOT NULL,
    is_leader TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_personnel_dept (dept_id),
    INDEX idx_personnel_ent (enterprise_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 存放地点
CREATE TABLE IF NOT EXISTS locations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    enterprise_id BIGINT NOT NULL,
    name VARCHAR(128) NOT NULL,
    parent_id BIGINT DEFAULT NULL,
    level INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_location_parent (parent_id),
    INDEX idx_location_ent (enterprise_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 分类与参数
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    enterprise_id BIGINT NOT NULL,
    name VARCHAR(128) NOT NULL,
    is_required TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category_ent (enterprise_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS params (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    enterprise_id BIGINT NOT NULL,
    name VARCHAR(128) NOT NULL,
    type VARCHAR(32) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_params_ent (enterprise_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS category_params (
    enterprise_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    param_id BIGINT NOT NULL,
    is_required TINYINT(1) DEFAULT 0,
    PRIMARY KEY (enterprise_id, category_id, param_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 资产主表（资产列表/盘点用）
CREATE TABLE IF NOT EXISTS assets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    enterprise_id BIGINT NOT NULL,
    code VARCHAR(64) NOT NULL UNIQUE,
    status VARCHAR(32) NOT NULL,
    status_text VARCHAR(32) NOT NULL,
    name VARCHAR(256) NOT NULL,
    category VARCHAR(128) NOT NULL,
    spec VARCHAR(128),
    brand VARCHAR(128),
    location VARCHAR(128),
    price DECIMAL(14,2) DEFAULT 0,
    purchase_date DATE,
    original_value DECIMAL(14,2) DEFAULT 0,
    acquisition_date DATE,
    accumulated_depreciation DECIMAL(14,2) DEFAULT 0,
    posting_date DATE,
    voucher_no VARCHAR(64),
    depreciation_months INT,
    remarks VARCHAR(255),
    use_dept VARCHAR(128),
    user_name VARCHAR(128),
    manager_dept VARCHAR(128),
    manager_name VARCHAR(128),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_assets_ent (enterprise_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 财政资产卡片
CREATE TABLE IF NOT EXISTS fiscal_asset_cards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    enterprise_id BIGINT NOT NULL,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(256) NOT NULL,
    category VARCHAR(128),
    spec VARCHAR(128),
    brand VARCHAR(128),
    quantity INT DEFAULT 1,
    use_dept VARCHAR(128),
    user_name VARCHAR(128),
    manager_dept VARCHAR(128),
    manager_name VARCHAR(128),
    original_value DECIMAL(14,2) DEFAULT 0,
    acquisition_date DATE,
    accumulated_depreciation DECIMAL(14,2) DEFAULT 0,
    posting_date DATE,
    voucher_no VARCHAR(64),
    depreciation_months INT,
    remarks VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_fiscal_ent (enterprise_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 资产操作记录
CREATE TABLE IF NOT EXISTS asset_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    enterprise_id BIGINT NOT NULL,
    type VARCHAR(64) NOT NULL,
    asset_code VARCHAR(64) NOT NULL,
    action_time DATETIME NOT NULL,
    operator VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_record_ent (enterprise_id),
    INDEX idx_record_asset (asset_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 盘点/核实数据
CREATE TABLE IF NOT EXISTS inventory_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    enterprise_id BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL,
    status_text VARCHAR(32) NOT NULL,
    tag_status VARCHAR(32) NOT NULL DEFAULT 'pending_tag',
    tag_status_text VARCHAR(32) NOT NULL DEFAULT '待贴标',
    code_fiscal VARCHAR(64) NOT NULL,
    name_fiscal VARCHAR(256) NOT NULL,
    category VARCHAR(128),
    spec VARCHAR(128),
    brand VARCHAR(128),
    location VARCHAR(128),
    acquisition_date DATE,
    original_value DECIMAL(14,2) DEFAULT 0,
    net_value DECIMAL(14,2) DEFAULT 0,
    accumulated_depreciation DECIMAL(14,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_inventory_ent (enterprise_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 演示数据
INSERT IGNORE INTO enterprises (id, code, name) VALUES
(1, 'SLZX', '石楼中学'),
(2, 'PYZX', '番禺中学');

INSERT IGNORE INTO users (id, phone, password, name) VALUES
(1, '17620927807', '123456', '梁宇煊'),
(2, '13800000000', '123456', '演示管理员');

INSERT IGNORE INTO user_enterprises (user_id, enterprise_id) VALUES
(1, 1),
(1, 2),
(2, 1);

INSERT IGNORE INTO departments (id, enterprise_id, name, parent_id) VALUES
(1, 1, '石楼中学', NULL),
(2, 1, '电教', 1),
(3, 1, '团委', 1),
(4, 1, '后勤', 1),
(11, 2, '番禺中学', NULL),
(12, 2, '教学部', 11),
(13, 2, '总务处', 11);

INSERT IGNORE INTO personnel (enterprise_id, name, dept_id, is_leader) VALUES
(1, '李晖', 2, 0),
(1, '任山峰（负责人）', 2, 1),
(1, '王云辉', 3, 0),
(1, '武丽红', 3, 0),
(1, '张汉平', 4, 0),
(1, '崔秋旺', 4, 0),
(1, '黄江', 4, 0),
(1, '王青平', 4, 0),
(2, '陈晓', 12, 1),
(2, '何静', 13, 0);

INSERT IGNORE INTO locations (id, enterprise_id, name, parent_id, level) VALUES
(1, 1, '综合楼', NULL, 1),
(2, 1, '综合楼一层', 1, 2),
(3, 1, '图书阅览室', 2, 3),
(4, 1, '105 收发室', 2, 3),
(5, 1, '104 保洁室', 2, 3),
(6, 1, '综合楼二层', 1, 2),
(7, 1, '214 政教处', 6, 3),
(8, 1, '211 副校长室', 6, 3),
(21, 2, '博学楼', NULL, 1),
(22, 2, 'A座一层', 21, 2),
(23, 2, 'A101 教研室', 22, 3),
(24, 2, 'A102 会议室', 22, 3);

INSERT IGNORE INTO categories (id, enterprise_id, name, is_required) VALUES
(1, 1, '危险化学品安全设备', 0),
(2, 1, '体操设备', 0),
(3, 1, '其他安全生产设备', 0),
(4, 1, '民用锅炉', 0),
(5, 1, 'A3黑白打印机', 0),
(6, 1, '其他体育设备设施', 0),
(7, 1, '其他化学药品和中药专用设备', 0),
(101, 2, '电脑及配件', 0),
(102, 2, '会议设备', 0),
(103, 2, '后勤物资', 0);

INSERT IGNORE INTO params (id, enterprise_id, name, type) VALUES
(1, 1, '保修期', 'date'),
(2, 1, '材质', 'text'),
(3, 1, '尺寸', 'text'),
(101, 2, '采购批次', 'text'),
(102, 2, '设备序列号', 'text'),
(103, 2, '折旧到期', 'date');

INSERT IGNORE INTO category_params (enterprise_id, category_id, param_id, is_required) VALUES
(1, 1, 2, 0),
(1, 1, 3, 0),
(1, 2, 3, 0),
(1, 5, 1, 1),
(2, 101, 101, 0),
(2, 101, 102, 1),
(2, 102, 103, 0);

-- 资产主表
INSERT IGNORE INTO assets (enterprise_id, code, status, status_text, name, category, spec, brand, location, price, purchase_date, original_value, acquisition_date, accumulated_depreciation, posting_date, voucher_no, depreciation_months, remarks)
VALUES
(1, 'ZY2019000029', 'idle', '闲置', '篮球奖杯', '其他体育设备设施', '-', '无', '综合楼一层', 120.00, '2025-01-01', 120.00, '2025-01-01', 0.00, '2025-01-05', '202501', 180, ''),
(1, 'ZY2019000028', 'idle', '闲置', '联想台式电脑主机', '计算机设备', 'ThinkCentre M720q', '联想', '综合楼二层', 4500.00, '2024-12-01', 4500.00, '2024-12-01', 3300.00, '2024-12-05', '202412', 180, ''),
(1, 'ZY2019000027', 'in_use', '在用', '文件柜', '其他台、桌类', '1.8m', '国产', '综合楼一层', 800.00, '2023-05-01', 800.00, '2023-05-01', 160.00, '2023-05-10', '202305', 120, ''),
(1, 'ZY2019000026', 'in_use', '在用', '美术桌', '其他台、桌类', '100*200cm', '国产', '图书阅览室', 1800.00, '2022-03-12', 1800.00, '2022-03-12', 450.00, '2022-03-20', '202203', 180, ''),
(1, 'ZY2019000025', 'idle', '闲置', '篮球架', '其他体育设备设施', '户外款', '国产', '综合楼', 2600.00, '2021-09-01', 2600.00, '2021-09-01', 1300.00, '2021-09-10', '202109', 120, ''),
(1, 'ZY2019000024', 'in_use', '在用', 'A3黑白打印机', 'A3黑白打印机', '激光', '惠普', '综合楼二层', 5200.00, '2020-06-01', 5200.00, '2020-06-01', 2600.00, '2020-06-05', '202006', 120, ''),
(1, 'ZY2019000023', 'idle', '闲置', '安全监控主机', '其他安全生产设备', '-', '海康', '211 副校长室', 3600.00, '2024-03-01', 3600.00, '2024-03-01', 600.00, '2024-03-05', '202403', 120, ''),
(1, 'ZY2019000022', 'scrapped', '报废', '老旧桌椅', '其他台、桌类', '-', '国产', '综合楼一层', 0.00, '2016-02-01', 150.00, '2016-02-01', 150.00, '2016-02-10', '201602', 120, ''),
(1, 'ZY2019000021', 'repair', '维修', '投影仪', '其他电子设备', 'Epson 1000', '爱普生', '综合楼二层', 3200.00, '2024-11-01', 3200.00, '2024-11-01', 150.00, '2024-11-06', '202411', 120, ''),
(1, 'ZY2019000020', 'idle', '闲置', '化学试剂柜', '其他化学药品和中药专用设备', '大号', '国产', '综合楼二层', 2200.00, '2022-11-01', 2200.00, '2022-11-01', 700.00, '2022-11-05', '202211', 120, ''),
(1, 'ZY2019000019', 'in_use', '在用', '办公桌', '其他台、桌类', '1.6m', '国产', '214 政教处', 1200.00, '2023-08-01', 1200.00, '2023-08-01', 240.00, '2023-08-05', '202308', 120, ''),
(1, 'ZY2019000018', 'idle', '闲置', '资料柜', '其他柜类', '2m', '国产', '综合楼', 900.00, '2020-01-01', 900.00, '2020-01-01', 400.00, '2020-01-05', '202001', 120, ''),
(2, 'PY20240001', 'in_use', '在用', '多媒体讲台', '会议设备', '带音响', '小米', 'A101 教研室', 3800.00, '2024-03-10', 3800.00, '2024-03-10', 600.00, '2024-03-15', '202403', 120, ''),
(2, 'PY20240002', 'idle', '闲置', '笔记本电脑', '电脑及配件', 'ThinkBook 14', '联想', 'A102 会议室', 5200.00, '2024-01-05', 5200.00, '2024-01-05', 900.00, '2024-01-10', '202401', 120, ''),
(2, 'PY20230001', 'scrapped', '报废', '老旧黑板', '后勤物资', '2m', '国产', 'A座一层', 0.00, '2015-06-01', 300.00, '2015-06-01', 300.00, '2015-06-15', '201506', 60, '');

-- 财政资产卡片
INSERT IGNORE INTO fiscal_asset_cards (enterprise_id, code, name, category, spec, brand, quantity, use_dept, user_name, manager_dept, manager_name, original_value, acquisition_date, accumulated_depreciation, posting_date, voucher_no, depreciation_months, remarks)
VALUES
(1, '141126767138451525000024', '陶艺室教学课桌', '其他台、桌类', '100*200cm', '国产', 1, '美术组', '张三', '后勤部', '李四', 1800.00, '2025-07-01', 0.00, '2025-07-01', '202501', 180, '-'),
(1, '141126767138451525000023', '联想台式电脑主机', '计算机设备', 'ThinkCentre M720q', '联想', 1, '信息中心', '王五', '后勤部', '李四', 4500.00, '2024-12-01', 3300.00, '2024-12-01', '202412', 120, '-'),
(1, '141126767138451525000022', '篮球奖杯', '其他体育设备设施', '-', '无', 1, '体育组', '赵六', '体育组', '赵六', 150.00, '2019-09-01', 150.00, '2019-09-01', '201909', 60, '-'),
(1, '141126767138451525000021', '文件柜', '其他柜类', '1.8m', '国产', 1, '教务处', '刘七', '教务处', '刘七', 800.00, '2023-05-01', 160.00, '2023-05-01', '202305', 120, '-'),
(2, '2401010001', '教学电脑', '电脑及配件', 'ThinkBook 14', '联想', 1, '教学部', '陈晓', '教学部', '陈晓', 5200.00, '2024-01-05', 900.00, '2024-01-05', '202401', 120, ''),
(2, '2401010002', '投影幕布', '会议设备', '120寸', '海信', 1, '会议中心', '何静', '总务处', '何静', 1500.00, '2024-02-01', 150.00, '2024-02-01', '202402', 60, '');

-- 资产操作记录
INSERT IGNORE INTO asset_records (enterprise_id, type, asset_code, action_time, operator) VALUES
(1, '入库', 'ZC2025082800158', '2025-09-03 11:11:52', '陈红红'),
(1, '领用', 'JJ2019001552', '2025-09-03 11:11:52', '陈红红'),
(1, '编辑记录', 'JJ2019001551', '2025-09-03 11:11:52', '陈红红'),
(1, '编辑记录', 'JJ2019001550', '2025-09-03 11:11:52', '陈红红'),
(1, '编辑记录', 'JJ2019001549', '2025-09-03 11:11:52', '陈红红'),
(2, '入库', 'PY20240002', '2024-01-10 10:00:00', '陈晓'),
(2, '领用', 'PY20240001', '2024-03-18 09:30:00', '何静');

-- 盘点/核实数据
INSERT IGNORE INTO inventory_items (enterprise_id, status, status_text, tag_status, tag_status_text, code_fiscal, name_fiscal, category, spec, brand, location, acquisition_date, original_value, net_value, accumulated_depreciation)
VALUES
(1, 'verified', '盘实', 'tagged', '已贴标', 'ZY2019000029', '篮球奖杯', '其他体育设备设施', '-', '无', '综合楼仓库', '2019-09-01', 150.00, 0.00, 150.00),
(1, 'surplus', '盘盈', 'pending_tag', '待贴标', 'ZY2019000028', '联想台式电脑主机', '计算机设备', 'ThinkCentre M720q', '联想', '综合楼仓库', '2024-12-01', 4500.00, 1200.00, 3300.00),
(1, 'loss', '盘亏', 'no_tag', '无需贴标', 'ZY2019000027', '文件柜', '其他柜类', '1.8m', '国产', '综合楼仓库', '2023-05-01', 800.00, 640.00, 160.00),
(1, 'verified', '盘实', 'tagged', '已贴标', 'ZY2019000026', '美术桌', '其他台、桌类', '100*200cm', '国产', '综合楼仓库', '2022-03-12', 1800.00, 1200.00, 600.00),
(1, 'surplus', '盘盈', 'tagged', '已贴标', 'ZY2019000025', '篮球架', '其他体育设备设施', '户外款', '国产', '综合楼仓库', '2021-09-01', 2600.00, 1300.00, 1300.00),
(1, 'verified', '盘实', 'tagged', '已贴标', 'ZY2019000024', 'A3黑白打印机', 'A3黑白打印机', '激光', '惠普', '综合楼仓库', '2020-06-01', 5200.00, 2600.00, 2600.00),
(1, 'pending', '待盘', 'pending_tag', '待贴标', 'ZY2019000023', '安全监控主机', '其他安全生产设备', '-', '海康', '综合楼仓库', '2024-03-01', 3600.00, 3200.00, 400.00),
(1, 'pending', '待盘', 'pending_tag', '待贴标', 'ZY2019000022', '老旧桌椅', '其他台、桌类', '-', '国产', '综合楼仓库', '2016-02-01', 150.00, 0.00, 150.00),
(1, 'pending', '待盘', 'pending_tag', '待贴标', 'ZY2019000021', '投影仪', '其他电子设备', 'Epson 1000', '爱普生', '综合楼仓库', '2024-11-01', 3200.00, 3050.00, 150.00),
(1, 'pending', '待盘', 'pending_tag', '待贴标', 'ZY2019000020', '化学试剂柜', '其他化学药品和中药专用设备', '大号', '国产', '综合楼仓库', '2022-11-01', 2200.00, 1500.00, 700.00),
(2, 'verified', '盘实', 'tagged', '已贴标', 'PY20240001', '多媒体讲台', '会议设备', '带音响', '小米', 'A101 教研室', '2024-03-10', 3800.00, 3200.00, 600.00),
(2, 'pending', '待盘', 'pending_tag', '待贴标', 'PY20240002', '笔记本电脑', '电脑及配件', 'ThinkBook 14', '联想', 'A102 会议室', '2024-01-05', 5200.00, 4300.00, 900.00),
(2, 'loss', '盘亏', 'no_tag', '无需贴标', 'PY20230001', '老旧黑板', '后勤物资', '2m', '国产', 'A座一层', '2015-06-01', 300.00, 0.00, 300.00);
