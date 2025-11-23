package com.guangruan.asset.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.guangruan.asset.mapper.DepartmentMapper;
import com.guangruan.asset.model.Department;
import com.guangruan.asset.model.Personnel;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentMapper departmentMapper;

    @GetMapping("/tree")
    public List<Map<String, Object>> tree(@RequestHeader(value = "X-Enterprise-Id", required = false) Long enterpriseId) {
        long requiredEnterpriseId = requireEnterpriseId(enterpriseId);
        List<Department> departments = departmentMapper.findAll(requiredEnterpriseId);
        Map<Long, Map<String, Object>> nodeMap = new LinkedHashMap<>();
        departments.forEach(dept -> {
            Map<String, Object> node = new HashMap<>();
            node.put("id", dept.getId());
            node.put("label", dept.getName());
            node.put("children", new ArrayList<Map<String, Object>>());
            nodeMap.put(dept.getId(), node);
        });

        List<Map<String, Object>> roots = new ArrayList<>();
        for (Department dept : departments) {
            Map<String, Object> node = nodeMap.get(dept.getId());
            if (dept.getParentId() == null) {
                roots.add(node);
            } else {
                Map<String, Object> parent = nodeMap.get(dept.getParentId());
                if (parent != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get("children");
                    children.add(node);
                } else {
                    roots.add(node);
                }
            }
        }
        return roots;
    }

    @GetMapping("/personnel")
    public Map<String, Object> personnel(@RequestHeader(value = "X-Enterprise-Id", required = false) Long enterpriseId,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "25") int size) {
        long requiredEnterpriseId = requireEnterpriseId(enterpriseId);
        int limit = Math.max(size, 1);
        int offset = (Math.max(page, 1) - 1) * limit;
        List<Personnel> list = departmentMapper.findPersonnel(requiredEnterpriseId, keyword, offset, limit);
        long total = departmentMapper.countPersonnel(requiredEnterpriseId, keyword);

        Map<Long, Department> deptMap = departmentMapper.findAll(requiredEnterpriseId).stream()
                .collect(Collectors.toMap(Department::getId, d -> d));

        List<Map<String, Object>> rows = list.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("name", item.getName());
            map.put("deptId", item.getDeptId());
            map.put("deptName", buildDeptName(item.getDeptId(), deptMap));
            map.put("isLeader", item.getIsLeader());
            return map;
        }).toList();

        Map<String, Object> result = new HashMap<>();
        result.put("list", rows);
        result.put("total", total);
        return result;
    }

    private long requireEnterpriseId(Long enterpriseId) {
        return enterpriseId == null ? 1L : enterpriseId;
    }

    private String buildDeptName(Long deptId, Map<Long, Department> deptMap) {
        List<String> names = new ArrayList<>();
        Long current = deptId;
        while (current != null && deptMap.containsKey(current)) {
            Department d = deptMap.get(current);
            names.add(d.getName());
            current = d.getParentId();
        }
        if (names.isEmpty()) {
            return "";
        }
        // reverse order: parent -> child
        List<String> reversed = new ArrayList<>();
        for (int i = names.size() - 1; i >= 0; i--) {
            reversed.add(names.get(i));
        }
        return reversed.stream().filter(Objects::nonNull).collect(Collectors.joining("/"));
    }
}
