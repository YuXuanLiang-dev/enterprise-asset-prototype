package com.guangruan.asset.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.guangruan.asset.mapper.LocationMapper;
import com.guangruan.asset.model.LocationNode;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationMapper locationMapper;

    @GetMapping
    public List<LocationNode> list(@RequestHeader(value = "X-Enterprise-Id", required = false) Long enterpriseId,
                                   @RequestParam(required = false) String keyword) {
        long requiredEnterpriseId = requireEnterpriseId(enterpriseId);
        List<LocationNode> nodes = locationMapper.findAll(requiredEnterpriseId, keyword);
        Map<Long, LocationNode> map = new LinkedHashMap<>();
        nodes.forEach(n -> map.put(n.getId(), n));
        List<LocationNode> roots = new ArrayList<>();
        for (LocationNode node : nodes) {
            if (node.getParentId() == null) {
                roots.add(node);
            } else {
                LocationNode parent = map.get(node.getParentId());
                if (parent != null) {
                    node.setParentName(parent.getName());
                    parent.getChildren().add(node);
                } else {
                    roots.add(node);
                }
            }
        }
        // 反向填充 parentName 对于第二层以下
        for (LocationNode node : nodes) {
            if (node.getParentName() == null && node.getParentId() != null) {
                LocationNode parent = map.get(node.getParentId());
                if (parent != null) {
                    node.setParentName(parent.getName());
                }
            }
        }
        roots.forEach(this::fillCountText);
        return roots.stream().filter(Objects::nonNull).toList();
    }

    private long requireEnterpriseId(Long enterpriseId) {
        return enterpriseId == null ? 1L : enterpriseId;
    }

    private void fillCountText(LocationNode node) {
        if (node.getChildren() == null) {
            node.setCountText(null);
            return;
        }
        node.getChildren().forEach(this::fillCountText);
        int childCount = node.getChildren().size();
        long leafCount = node.getChildren().stream().mapToLong(child -> {
            if (child.getChildren() == null || child.getChildren().isEmpty()) {
                return 1;
            }
            return child.getChildren().size();
        }).sum();
        if (childCount > 0) {
            node.setCountText("(" + childCount + " 个下级，" + leafCount + " 个末级)");
        }
    }
}
