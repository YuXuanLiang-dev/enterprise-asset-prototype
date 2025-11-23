package com.guangruan.asset.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.guangruan.asset.mapper.AssetMapper;
import com.guangruan.asset.mapper.AssetRecordMapper;
import com.guangruan.asset.model.Asset;
import com.guangruan.asset.model.AssetRecord;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetMapper assetMapper;
    private final AssetRecordMapper assetRecordMapper;

    @GetMapping
    public Map<String, Object> list(@RequestHeader(value = "X-Enterprise-Id", required = false) Long enterpriseId,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "25") int size) {
        long requiredEnterpriseId = requireEnterpriseId(enterpriseId);
        int limit = Math.max(size, 1);
        int offset = (Math.max(page, 1) - 1) * limit;
        List<Asset> list = assetMapper.findAssets(requiredEnterpriseId, status, keyword, offset, limit);
        long total = assetMapper.countAssets(requiredEnterpriseId, status, keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return result;
    }

    @PostMapping
    public Asset create(@RequestHeader(value = "X-Enterprise-Id", required = false) Long enterpriseId,
                        @RequestHeader(value = "X-Operator", required = false) String operator,
                        @RequestBody Asset asset) {
        long requiredEnterpriseId = requireEnterpriseId(enterpriseId);
        asset.setEnterpriseId(requiredEnterpriseId);
        assetMapper.insertAsset(asset);
        recordAction(requiredEnterpriseId, "资产入库", asset.getCode(), operator);
        return asset;
    }

    @PutMapping("/{id}")
    public Asset update(@PathVariable Long id,
                        @RequestHeader(value = "X-Enterprise-Id", required = false) Long enterpriseId,
                        @RequestHeader(value = "X-Operator", required = false) String operator,
                        @RequestBody Asset asset) {
        long requiredEnterpriseId = requireEnterpriseId(enterpriseId);
        asset.setId(id);
        asset.setEnterpriseId(requiredEnterpriseId);
        int changed = assetMapper.updateAsset(asset);
        if (changed == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "未找到资产");
        }
        recordAction(requiredEnterpriseId, "编辑资产", asset.getCode(), operator);
        return asset;
    }

    @DeleteMapping
    public Map<String, Object> delete(@RequestHeader(value = "X-Enterprise-Id", required = false) Long enterpriseId,
                                      @RequestHeader(value = "X-Operator", required = false) String operator,
                                      @RequestBody Map<String, List<Long>> payload) {
        long requiredEnterpriseId = requireEnterpriseId(enterpriseId);
        List<Long> ids = payload.get("ids");
        if (ids == null || ids.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "缺少要删除的ID");
        }
        assetMapper.deleteAssets(requiredEnterpriseId, ids);
        recordAction(requiredEnterpriseId, "删除资产", "批量删除" + ids.size(), operator);
        Map<String, Object> resp = new HashMap<>();
        resp.put("deleted", ids.size());
        return resp;
    }

    private long requireEnterpriseId(Long enterpriseId) {
        return enterpriseId == null ? 1L : enterpriseId;
    }

    private void recordAction(Long enterpriseId, String type, String assetCode, String operator) {
        AssetRecord record = new AssetRecord();
        record.setEnterpriseId(enterpriseId);
        record.setType(type);
        record.setAssetCode(assetCode);
        record.setActionTime(LocalDateTime.now());
        record.setOperator((operator == null || operator.isBlank()) ? "系统" : operator);
        assetRecordMapper.insertRecord(record);
    }
}
