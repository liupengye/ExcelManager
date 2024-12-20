package org.xdu.lpy.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ConverterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.xdu.lpy.exception.BusinessException;
import org.xdu.lpy.mapper.ExcelMetaMapper;
import org.xdu.lpy.model.ExcelMeta;
import org.xdu.lpy.service.DynamicTableService;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class DataListener implements ReadListener<Map<Integer, String>> {
    private final MultipartFile file;
    private final ExcelMetaMapper excelMetaMapper;
    private final DynamicTableService dynamicTableService;

    // 生成动态表名
    String tableName = "excel_data_" + System.currentTimeMillis();
    List<String> headers = new ArrayList<>();
    List<Map<String, Object>> dataList = new ArrayList<>();

    public DataListener(MultipartFile file, ExcelMetaMapper excelMetaMapper, DynamicTableService dynamicTableService) {
        this.file = file;
        this.excelMetaMapper = excelMetaMapper;
        this.dynamicTableService = dynamicTableService;
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        // 检查表头是否重复
        Set<String> uniqueHeaders = new HashSet<>();
        List<String> duplicateHeaders = new ArrayList<>();
        Map<Integer, String> stringHeadMap = ConverterUtils.convertToStringMap(headMap, context);

        stringHeadMap.values().forEach(map -> {
            if (!uniqueHeaders.add(map)) {
                duplicateHeaders.add(map);
            }
        });

        if (!duplicateHeaders.isEmpty()) {
            throw new BusinessException("Excel表头存在重复: " + String.join(", ", duplicateHeaders));
        }

        headers.addAll(stringHeadMap.values());

        // 创建动态表
        dynamicTableService.createTable(tableName, headers);
    }

    @Override
    public void invoke(Map<Integer, String> integerStringMap, AnalysisContext analysisContext) {
        Map<String, Object> dataMap = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            dataMap.put(headers.get(i), integerStringMap.get(i));
        }
        dataList.add(dataMap);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("Excel解析完成，表头: {}, 数据行数: {}", headers, dataList.size());

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
}
