package com.guangruan.asset.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class InventoryItem {
    private Long id;
    private String status;
    private String statusText;
    private String tagStatus;
    private String tagStatusText;
    private String codeFiscal;
    private String nameFiscal;
    private String category;
    private String spec;
    private String brand;
    private String location;
    private LocalDate acquisitionDate;
    private BigDecimal originalValue;
    private BigDecimal netValue;
    private BigDecimal accumulatedDepreciation;
}
