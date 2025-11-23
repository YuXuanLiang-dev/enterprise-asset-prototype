package com.guangruan.asset.model;

import lombok.Data;

@Data
public class CategoryStat {
    private String category;
    private Long count;
    private java.math.BigDecimal totalValue;
    // 复用该类承载按状态分组时的状态名称
    private String statusText;
}
