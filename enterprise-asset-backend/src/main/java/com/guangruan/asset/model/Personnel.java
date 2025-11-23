package com.guangruan.asset.model;

import lombok.Data;

@Data
public class Personnel {
    private Long id;
    private String name;
    private Long deptId;
    private Boolean isLeader;
}
