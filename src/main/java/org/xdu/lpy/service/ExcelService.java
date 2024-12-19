package org.xdu.lpy.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xdu.lpy.exception.BusinessException;
import org.xdu.lpy.mapper.ExcelMetaMapper;
import org.xdu.lpy.model.ExcelMeta;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelService {
    
    private final ExcelMetaMapper excelMetaMapper;
    private final DynamicTableService dynamicTableService;
    
    public void processExcelFile(MultipartFile file) throws IOException {
        // 生成动态表名
        String tableName = "excel_data_" + System.currentTimeMillis();
        List<String> headers = new ArrayList<>();
        List<Map<String, Object>> dataList = new ArrayList<>();
        
        // 使用EasyExcel读取文件
        EasyExcel.read(file.getInputStream(), new AnalysisEventListener<Map<Integer, String>>() {
            @Override
            public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                // 检查表头是否重复
                Set<String> uniqueHeaders = new HashSet<>();
                List<String> duplicateHeaders = new ArrayList<>();
                
                headMap.values().forEach(header -> {
                    if (!uniqueHeaders.add(header)) {
                        duplicateHeaders.add(header);
                    }
                });
                
                if (!duplicateHeaders.isEmpty()) {
                    throw new BusinessException("Excel表头存在重复: " + String.join(", ", duplicateHeaders));
                }
                
                headers.addAll(headMap.values());
            }
            
            @Override
            public void invoke(Map<Integer, String> rowData, AnalysisContext context) {
                Map<String, Object> dataMap = new HashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    dataMap.put(headers.get(i), rowData.get(i));
                }
                dataList.add(dataMap);
            }
            
            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                log.info("Excel解析完成，表头: {}, 数据行数: {}", headers, dataList.size());
            }
        }).sheet().doRead();
        
        // 创建动态表
        dynamicTableService.createTable(tableName, headers);
        
        // 批量插入数据
        if (!dataList.isEmpty()) {
            dynamicTableService.batchInsert(tableName, headers, dataList);
        }
        
        // 保存元数据信息
        ExcelMeta excelMeta = new ExcelMeta();
        excelMeta.setFileName(file.getOriginalFilename());
        excelMeta.setTableName(tableName);
        excelMeta.setHeaders(String.join(",", headers));
        excelMeta.setCreateTime(LocalDateTime.now());
        excelMeta.setUpdateTime(LocalDateTime.now());
        excelMetaMapper.insert(excelMeta);
    }
    
    /**
     * 删除Excel文件及其数据
     */
    public void deleteExcel(Long id) {
        ExcelMeta meta = excelMetaMapper.selectById(id);
        if (meta != null) {
            // 删除数据表
            dynamicTableService.dropTable(meta.getTableName());
            // 删除元数据
            excelMetaMapper.deleteById(id);
        }
    }
} 