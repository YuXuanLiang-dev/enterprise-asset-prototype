package com.guangruan.asset.controller;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.guangruan.asset.mapper.AssetMapper;
import com.guangruan.asset.model.CategoryStat;
import com.guangruan.asset.model.StatusStat;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AssetMapper assetMapper;
    private static final DecimalFormat PERCENT = new DecimalFormat("0.00%");

    static {
        PERCENT.setRoundingMode(RoundingMode.HALF_UP);
    }

    @GetMapping
    public Map<String, Object> dashboard(@RequestHeader(value = "X-Enterprise-Id", required = false) Long enterpriseId) {
        long requiredEnterpriseId = requireEnterpriseId(enterpriseId);
        List<StatusStat> statusStats = assetMapper.countByStatus(requiredEnterpriseId);
        long total = statusStats.stream().mapToLong(StatusStat::getCount).sum();
        List<Map<String, Object>> overviewDetail = statusStats.stream()
                .map(stat -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("value", stat.getCount());
                    item.put("name", stat.getStatusText());
                    String percent = total == 0 ? "0.00%" : PERCENT.format((double) stat.getCount() / total);
                    item.put("percent", percent);
                    return item;
                })
                .collect(Collectors.toList());

        List<CategoryStat> categoryStats = assetMapper.countByCategory(requiredEnterpriseId);
        List<String> categories = categoryStats.stream().map(CategoryStat::getCategory).toList();
        List<Long> values = categoryStats.stream().map(CategoryStat::getCount).toList();

        List<CategoryStat> valueByStatus = assetMapper.sumValueByStatus(requiredEnterpriseId);
        java.math.BigDecimal totalValue = valueByStatus.stream()
                .map(stat -> stat.getTotalValue() == null ? java.math.BigDecimal.ZERO : stat.getTotalValue())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        List<Map<String, Object>> valueDetail = valueByStatus.stream().map(stat -> {
            Map<String, Object> item = new HashMap<>();
            java.math.BigDecimal val = stat.getTotalValue() == null ? java.math.BigDecimal.ZERO : stat.getTotalValue();
            item.put("name", stat.getStatusText());
            item.put("value", val);
            String percent = totalValue.compareTo(java.math.BigDecimal.ZERO) == 0
                    ? "0.00%"
                    : PERCENT.format(val.doubleValue() / totalValue.doubleValue());
            item.put("percent", percent);
            return item;
        }).toList();

        List<CategoryStat> locationStats = assetMapper.sumValueByLocation(requiredEnterpriseId);
        List<String> locations = locationStats.stream().map(CategoryStat::getCategory).toList();
        List<java.math.BigDecimal> locationValues = locationStats.stream()
                .map(stat -> stat.getTotalValue() == null ? java.math.BigDecimal.ZERO : stat.getTotalValue())
                .toList();

        Map<String, Object> assetOverview = new HashMap<>();
        assetOverview.put("total", total);
        assetOverview.put("detail", overviewDetail);

        Map<String, Object> assetValue = new HashMap<>();
        assetValue.put("totalValue", totalValue);
        assetValue.put("detail", valueDetail);

        Map<String, Object> assetStats = new HashMap<>();
        assetStats.put("categories", categories);
        assetStats.put("values", values);

        Map<String, Object> locationValueStats = new HashMap<>();
        locationValueStats.put("locations", locations);
        locationValueStats.put("values", locationValues);

        Map<String, Object> response = new HashMap<>();
        response.put("assetOverview", assetOverview);
        response.put("assetValueOverview", assetValue);
        response.put("assetStats", assetStats);
        response.put("locationValueStats", locationValueStats);
        return response;
    }

    private long requireEnterpriseId(Long enterpriseId) {
        return enterpriseId == null ? 1L : enterpriseId;
    }
}
