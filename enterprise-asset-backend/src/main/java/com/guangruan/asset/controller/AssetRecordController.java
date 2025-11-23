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
import com.guangruan.asset.mapper.AssetRecordMapper;
import com.guangruan.asset.model.AssetRecord;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class AssetRecordController {

    private final AssetRecordMapper assetRecordMapper;

    @GetMapping
    public Map<String, Object> list(@RequestHeader(value = "X-Enterprise-Id", required = false) Long enterpriseId,
                                    @RequestParam(required = false) String type,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "25") int size) {
        long requiredEnterpriseId = requireEnterpriseId(enterpriseId);
        int limit = Math.max(size, 1);
        int offset = (Math.max(page, 1) - 1) * limit;
        List<AssetRecord> list = assetRecordMapper.findRecords(requiredEnterpriseId, type, keyword, offset, limit);
        long total = assetRecordMapper.countRecords(requiredEnterpriseId, type, keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return result;
    }

    private long requireEnterpriseId(Long enterpriseId) {
        return enterpriseId == null ? 1L : enterpriseId;
    }
}
