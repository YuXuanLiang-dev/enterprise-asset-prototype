package com.guangruan.asset.model;

import lombok.Data;

@Data
public class InventoryStats {
    private Long pending = 0L;
    private Long verified = 0L;
    private Long surplus = 0L;
    private Long loss = 0L;
    private Long pendingTag = 0L;
    private Long tagged = 0L;
    private Long noTag = 0L;
}
