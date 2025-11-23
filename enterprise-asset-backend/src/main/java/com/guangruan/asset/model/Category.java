package com.guangruan.asset.model;

import lombok.Data;

@Data
public class Category {
    private Long id;
    private String name;
    private Boolean isRequired;
    private Integer paramCount;
}
