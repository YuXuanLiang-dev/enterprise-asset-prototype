package com.guangruan.asset.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AssetRecord {
    private Long id;
    private Long enterpriseId;
    private String type;
    private String assetCode;
    private LocalDateTime actionTime;
    private String operator;
}
