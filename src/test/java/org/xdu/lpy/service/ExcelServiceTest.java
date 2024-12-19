package org.xdu.lpy.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.xdu.lpy.BaseTest;
import org.xdu.lpy.mapper.ExcelMetaMapper;
import org.xdu.lpy.model.ExcelMeta;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExcelServiceTest extends BaseTest {
    
    @Autowired
    private ExcelService excelService;
    
    @Autowired
    private ExcelMetaMapper excelMetaMapper;
    
    @Test
    void testProcessExcelFile() throws IOException {
        // 创建测试Excel文件内容
        String content = "姓名,年龄\n张三,20\n李四,25";
        MockMultipartFile file = new MockMultipartFile(
            "test.csv",
            "test.csv",
            "text/csv",
            content.getBytes(StandardCharsets.UTF_8)
        );
        
        // 处理Excel文件
        excelService.processExcelFile(file);
        
        // 验证元数据是否保存
        List<ExcelMeta> metas = excelMetaMapper.selectList(null);
        assertEquals(1, metas.size());
        ExcelMeta meta = metas.get(0);
        assertEquals("test.csv", meta.getFileName());
        assertTrue(meta.getTableName().startsWith("excel_data_"));
        assertEquals("姓名,年龄", meta.getHeaders());
        
        // 验证数据是否正确导入
        List<Map<String, Object>> data = jdbcTemplate.queryForList(
            "SELECT * FROM " + meta.getTableName());
        assertEquals(2, data.size());
    }
} 