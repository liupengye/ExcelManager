package org.xdu.lpy.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.xdu.lpy.BaseTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DynamicTableServiceTest extends BaseTest {
    
    @Autowired
    private DynamicTableService dynamicTableService;
    
    @Test
    void testCreateTable() {
        String tableName = "test_table";
        List<String> columns = Arrays.asList("name", "age", "email");
        
        dynamicTableService.createTable(tableName, columns);
        
        // 验证表是否创建成功
        List<Map<String, Object>> tables = jdbcTemplate.queryForList(
            "SHOW TABLES LIKE ?", tableName);
        assertFalse(tables.isEmpty());
    }
    
    @Test
    void testBatchInsert() {
        // 准备测试表
        String tableName = "test_table";
        List<String> columns = Arrays.asList("name", "age");
        dynamicTableService.createTable(tableName, columns);
        
        // 准备测试数据
        List<Map<String, Object>> dataList = Arrays.asList(
            createTestData("张三", "20"),
            createTestData("李四", "25")
        );
        
        dynamicTableService.batchInsert(tableName, columns, dataList);
        
        // 验证数据是否插入成功
        List<Map<String, Object>> result = jdbcTemplate.queryForList(
            "SELECT * FROM " + tableName);
        assertEquals(2, result.size());
    }
    
    @Test
    void testQueryByPage() {
        // 准备测试表和数据
        String tableName = "test_table";
        List<String> columns = Arrays.asList("name", "age");
        dynamicTableService.createTable(tableName, columns);
        
        List<Map<String, Object>> dataList = Arrays.asList(
            createTestData("张三", "20"),
            createTestData("李四", "25"),
            createTestData("王五", "30")
        );
        dynamicTableService.batchInsert(tableName, columns, dataList);
        
        // 测试分页查询
        List<Map<String, Object>> page1 = dynamicTableService.queryByPage(tableName, 1, 2);
        assertEquals(2, page1.size());
        
        List<Map<String, Object>> page2 = dynamicTableService.queryByPage(tableName, 2, 2);
        assertEquals(1, page2.size());
    }
    
    @Test
    void testUpdateCell() {
        // 准备测试表和数据
        String tableName = "test_table";
        List<String> columns = Arrays.asList("name", "age");
        dynamicTableService.createTable(tableName, columns);
        
        List<Map<String, Object>> dataList = Arrays.asList(
            createTestData("张三", "20")
        );
        dynamicTableService.batchInsert(tableName, columns, dataList);
        
        // 获取插入的记录ID
        Long id = jdbcTemplate.queryForObject(
            "SELECT id FROM " + tableName + " LIMIT 1", Long.class);
        
        // 测试更新单元格
        dynamicTableService.updateCell(tableName, id, "age", "21");
        
        // 验证更新结果
        Map<String, Object> updated = jdbcTemplate.queryForMap(
            "SELECT * FROM " + tableName + " WHERE id = ?", id);
        assertEquals("21", updated.get("age"));
    }
    
    private Map<String, Object> createTestData(String name, String age) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("age", age);
        return data;
    }
} 