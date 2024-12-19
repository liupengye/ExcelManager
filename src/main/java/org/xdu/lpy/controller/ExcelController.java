package org.xdu.lpy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xdu.lpy.dto.PageRequest;
import org.xdu.lpy.dto.PageResponse;
import org.xdu.lpy.dto.UpdateCellRequest;
import org.xdu.lpy.mapper.ExcelMetaMapper;
import org.xdu.lpy.model.ExcelMeta;
import org.xdu.lpy.service.DynamicTableService;
import org.xdu.lpy.service.ExcelService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelController {
    
    private final ExcelService excelService;
    private final ExcelMetaMapper excelMetaMapper;
    private final DynamicTableService dynamicTableService;
    
    @PostMapping("/upload")
    public ResponseEntity<Void> uploadExcel(@RequestParam("file") MultipartFile file) throws Exception {
        excelService.processExcelFile(file);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<ExcelMeta>> listExcelFiles() {
        return ResponseEntity.ok(excelMetaMapper.selectList(null));
    }
    
    @GetMapping("/data/{tableId}")
    public ResponseEntity<PageResponse> getTableData(
            @PathVariable Long tableId,
            PageRequest pageRequest) {
        ExcelMeta meta = excelMetaMapper.selectById(tableId);
        if (meta == null) {
            return ResponseEntity.notFound().build();
        }
        PageResponse response = dynamicTableService.queryByPage(
                meta.getTableName(),
                pageRequest.getPage(),
                pageRequest.getSize()
        );
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/data/{tableId}")
    public ResponseEntity<Void> updateCell(
            @PathVariable Long tableId,
            @RequestBody UpdateCellRequest request) {
        ExcelMeta meta = excelMetaMapper.selectById(tableId);
        if (meta == null) {
            return ResponseEntity.notFound().build();
        }
        dynamicTableService.updateCell(
                meta.getTableName(),
                request.getId(),
                request.getColumn(),
                request.getValue()
        );
        return ResponseEntity.ok().build();
    }
} 