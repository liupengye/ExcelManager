package org.xdu.lpy.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xdu.lpy.exception.BusinessException;
import org.xdu.lpy.listener.DataListener;
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

    /**
     * 处理Excel文件
     */
    public void processExcelFile(MultipartFile file) throws IOException {

        // 使用EasyExcel读取文件
        EasyExcel.read(file.getInputStream(),
                        new DataListener(file, excelMetaMapper, dynamicTableService))
                .sheet().doRead();

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