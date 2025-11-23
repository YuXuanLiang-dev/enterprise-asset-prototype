package com.guangruan.asset.model;

import lombok.Data;

@Data
public class Department {
    private Long id;
    private String name;
    private Long parentId;
}
