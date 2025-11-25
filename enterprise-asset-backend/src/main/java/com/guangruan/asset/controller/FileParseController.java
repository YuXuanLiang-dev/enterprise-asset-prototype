package com.guangruan.asset.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import com.guangruan.asset.mapper.AssetRecordMapper;
import com.guangruan.asset.model.AssetRecord;

import org.apache.poi.ss.usermodel.DataFormatter;

@RestController
@RequestMapping("/files")
@Slf4j
@RequiredArgsConstructor
public class FileParseController {

    private final AssetRecordMapper assetRecordMapper;
    private final DataFormatter dataFormatter = new DataFormatter();

    @PostMapping("/parse/{type}")
    public Map<String, Object> parse(@PathVariable String type,
                                     @RequestHeader(value = "X-Enterprise-Id", required = false) Long enterpriseId,
                                     @RequestHeader(value = "X-Operator", required = false) String operator,
                                     @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文件为空");
        }
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "未发现工作表");
            }
            int headerRowIndex = findHeaderRow(sheet);
            List<String> headers = readRow(sheet.getRow(headerRowIndex));
            List<Map<String, Object>> preview = readDataRows(sheet, headerRowIndex + 1, headers, 50);

            Map<String, Object> result = new HashMap<>();
            result.put("type", type);
            result.put("headers", headers);
            result.put("preview", preview);
            result.put("count", preview.size());
            result.put("message", "解析完成");

            if ("fiscal-cards".equalsIgnoreCase(type) && enterpriseId != null) {
                recordAction(enterpriseId, "导入财政资产卡片", file.getOriginalFilename(), operator);
            }
            return result;
        } catch (IOException e) {
            log.error("解析文件失败", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文件解析失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("解析文件失败", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文件解析失败");
        }
    }

    private int findHeaderRow(Sheet sheet) {
        int maxScan = Math.min(sheet.getLastRowNum(), 10);
        for (int i = 0; i <= maxScan; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            List<String> values = readRow(row);
            long nonEmpty = values.stream().filter(v -> v != null && !v.isBlank()).count();
            if (nonEmpty >= 2) {
                return i;
            }
        }
        return 0;
    }

    private List<String> readRow(Row row) {
        List<String> cells = new ArrayList<>();
        if (row == null) {
            return cells;
        }
        int lastCell = row.getLastCellNum();
        for (int i = 0; i < lastCell; i++) {
            Cell cell = row.getCell(i);
            cells.add(cell == null ? null : dataFormatter.formatCellValue(cell).trim());
        }
        return cells;
    }

    private List<Map<String, Object>> readDataRows(Sheet sheet, int startRow, List<String> headers, int limit) {
        List<Map<String, Object>> rows = new ArrayList<>();
        int max = Math.min(sheet.getLastRowNum(), startRow + 500);
        for (int i = startRow; i <= max && rows.size() < limit; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            Map<String, Object> map = new HashMap<>();
            boolean hasValue = false;
            for (int c = 0; c < headers.size(); c++) {
                String header = headers.get(c);
                if (header == null || header.isBlank()) {
                    continue;
                }
                Cell cell = row.getCell(c);
                if (cell != null) {
                    String value = dataFormatter.formatCellValue(cell).trim();
                    if (!value.isBlank()) {
                        hasValue = true;
                        map.put(header, value);
                    }
                }
            }
            if (hasValue) {
                rows.add(map);
            }
        }
        return rows;
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
