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
import com.guangruan.asset.mapper.SettingMapper;
import com.guangruan.asset.model.Category;
import com.guangruan.asset.model.ParamItem;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingMapper settingMapper;

    @GetMapping("/categories")
    public Map<String, Object> categories(@RequestHeader(value = "X-Enterprise-Id", required = false) Long enterpriseId,
                                          @RequestParam(required = false) String keyword,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "25") int size) {
        long requiredEnterpriseId = requireEnterpriseId(enterpriseId);
        int limit = Math.max(size, 1);
        int offset = (Math.max(page, 1) - 1) * limit;
        List<Category> list = settingMapper.findCategories(requiredEnterpriseId, keyword, offset, limit);
        long total = settingMapper.countCategories(requiredEnterpriseId, keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return result;
    }

    @GetMapping("/params")
    public List<ParamItem> params(@RequestHeader(value = "X-Enterprise-Id", required = false) Long enterpriseId,
                                  @RequestParam(required = false) String keyword) {
        long requiredEnterpriseId = requireEnterpriseId(enterpriseId);
        return settingMapper.findParams(requiredEnterpriseId, keyword);
    }

    private long requireEnterpriseId(Long enterpriseId) {
        return enterpriseId == null ? 1L : enterpriseId;
    }
}
