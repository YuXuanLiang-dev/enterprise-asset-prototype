package com.guangruan.asset.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class FiscalAssetCard {
    private Long id;
    private Long enterpriseId;
    private String code;
    private String name;
    private String category;
    private String spec;
    private String brand;
    private Integer quantity;
    private String useDept;
    private String userName;
    private String managerDept;
    private String managerName;
    private BigDecimal originalValue;
    private LocalDate acquisitionDate;
    private BigDecimal accumulatedDepreciation;
    private LocalDate postingDate;
    private String voucherNo;
    private Integer depreciationMonths;
    private String remarks;
}
