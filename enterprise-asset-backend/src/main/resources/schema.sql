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
