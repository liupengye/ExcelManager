package org.xdu.lpy.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.xdu.lpy.exception.BusinessException;
import org.xdu.lpy.mapper.ExcelMetaMapper;
import org.xdu.lpy.model.ExcelMeta;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExportService {
    
    private final ExcelMetaMapper excelMetaMapper;
    private final DynamicTableService dynamicTableService;
    
    public void exportExcel(Long id, OutputStream outputStream) {
        // 获取Excel元数据
        ExcelMeta meta = excelMetaMapper.selectById(id);
        if (meta == null) {
            throw new BusinessException("文件不存在");
        }
        
        // 获取表头
        List<String> headers = List.of(meta.getHeaders().split(","));
        
        // 获取所有数据
        List<Map<String, Object>> rawData = dynamicTableService.getAllData(meta.getTableName());
        
        // 转换数据格式
        List<List<String>> rows = new ArrayList<>();
        for (Map<String, Object> row : rawData) {
            List<String> rowData = new ArrayList<>();
            for (String header : headers) {
                Object value = row.get(header);
                rowData.add(value != null ? value.toString() : "");
            }
            rows.add(rowData);
        }
        
        // 写入Excel
        EasyExcel.write(outputStream)
                .head(headers.stream()
                        .map(header -> List.of(header))
                        .toList())
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()) // 自动列宽
                .sheet("Sheet1")
                .doWrite(rows);
    }
} 