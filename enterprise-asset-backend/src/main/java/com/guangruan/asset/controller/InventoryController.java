package com.guangruan.asset.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.guangruan.asset.mapper.InventoryMapper;
import com.guangruan.asset.model.InventoryItem;
import com.guangruan.asset.model.InventoryStats;
import com.guangruan.asset.model.StatusStat;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryMapper inventoryMapper;

    @GetMapping
    public Map<String, Object> list(@RequestHeader(value = "X-Enterprise-Id", required = false) Long enterpriseId,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "25") int size) {
        long requiredEnterpriseId = requireEnterpriseId(enterpriseId);
        int limit = Math.max(size, 1);
        int offset = (Math.max(page, 1) - 1) * limit;
        List<InventoryItem> list = inventoryMapper.findItems(requiredEnterpriseId, status, keyword, offset, limit);
        long total = inventoryMapper.countItems(requiredEnterpriseId, status, keyword);

        InventoryStats stats = buildStats(requiredEnterpriseId);

        Map<String, Object> result = new HashMap<>();
        result.put("stats", stats);
        result.put("list", list);
        result.put("total", total);
        return result;
    }

    private InventoryStats buildStats(Long enterpriseId) {
        List<StatusStat> statusStats = inventoryMapper.countByStatus(enterpriseId);
        InventoryStats stats = new InventoryStats();
        for (StatusStat stat : statusStats) {
            switch (stat.getStatus()) {
                case "verified" -> stats.setVerified(stat.getCount());
                case "surplus" -> stats.setSurplus(stat.getCount());
                case "loss" -> stats.setLoss(stat.getCount());
                case "pending" -> stats.setPending(stat.getCount());
                default -> {
                }
            }
        }
        // 演示：待贴标等同于已盘实，未实现贴标字段时默认 0
        stats.setPendingTag(stats.getVerified());
        stats.setTagged(0L);
        stats.setNoTag(0L);
        return stats;
    }

    private long requireEnterpriseId(Long enterpriseId) {
        return enterpriseId == null ? 1L : enterpriseId;
    }
}
