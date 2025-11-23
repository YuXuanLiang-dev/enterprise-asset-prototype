package com.guangruan.asset.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.guangruan.asset.mapper.FiscalAssetCardMapper;
import com.guangruan.asset.mapper.AssetRecordMapper;
import com.guangruan.asset.model.FiscalAssetCard;
import com.guangruan.asset.model.AssetRecord;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/fiscal-cards")
@RequiredArgsConstructor
public class FiscalAssetCardController {

    private final FiscalAssetCardMapper fiscalAssetCardMapper;
    private final AssetRecordMapper assetRecordMapper;

    @GetMapping
    public Map<String, Object> list(@RequestHeader(value = "X-Enterprise-Id", required = false) Long enterpriseId,
                                    @RequestParam(required = false) String category,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "25") int size) {
        long requiredEnterpriseId = requireEnterpriseId(enterpriseId);
        int limit = Math.max(size, 1);
        int offset = (Math.max(page, 1) - 1) * limit;
        List<FiscalAssetCard> list = fiscalAssetCardMapper.findCards(requiredEnterpriseId, category, keyword, offset, limit);
        long total = fiscalAssetCardMapper.countCards(requiredEnterpriseId, category, keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return result;
    }

    @PutMapping("/{id}")
    public FiscalAssetCard update(@PathVariable Long id,
                                  @RequestHeader(value = "X-Enterprise-Id", required = false) Long enterpriseId,
                                  @RequestHeader(value = "X-Operator", required = false) String operator,
                                  @RequestBody FiscalAssetCard card) {
        long requiredEnterpriseId = requireEnterpriseId(enterpriseId);
        card.setId(id);
        card.setEnterpriseId(requiredEnterpriseId);
        int changed = fiscalAssetCardMapper.updateCard(card);
        if (changed == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "未找到资产卡片");
        }
        recordAction(requiredEnterpriseId, "编辑财政资产卡片", card.getCode(), operator);
        return card;
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
        fiscalAssetCardMapper.deleteCards(requiredEnterpriseId, ids);
        recordAction(requiredEnterpriseId, "删除财政资产卡片", "批量删除" + ids.size(), operator);
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
        record.setActionTime(java.time.LocalDateTime.now());
        record.setOperator(operator == null || operator.isBlank() ? "系统" : operator);
        assetRecordMapper.insertRecord(record);
    }
}
