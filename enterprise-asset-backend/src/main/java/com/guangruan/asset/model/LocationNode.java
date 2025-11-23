package com.guangruan.asset.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class LocationNode {
    private Long id;
    private String name;
    private Long parentId;
    private Integer level;
    private String parentName;
    private String countText;
    private List<LocationNode> children = new ArrayList<>();
}
