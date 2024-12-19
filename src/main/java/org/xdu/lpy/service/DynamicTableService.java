package org.xdu.lpy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DynamicTableService {
    
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * 创建动态表
     * @param tableName 表名
     * @param columns 列定义
     */
    public void createTable(String tableName, List<String> columns) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(tableName).append(" (");
        sql.append("id BIGINT PRIMARY KEY AUTO_INCREMENT,");
        
        for (int i = 0; i < columns.size(); i++) {
            sql.append("`").append(columns.get(i)).append("` TEXT");
            if (i < columns.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(")");
        
        jdbcTemplate.execute(sql.toString());
    }
    
    /**
     * 批量插入数据
     */
    public void batchInsert(String tableName, List<String> columns, List<Map<String, Object>> dataList) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName).append(" (");
        sql.append(String.join(",", columns));
        sql.append(") VALUES (");
        sql.append("?,".repeat(columns.size() - 1)).append("?)");
        
        List<Object[]> batchArgs = dataList.stream()
            .map(data -> columns.stream()
                .map(data::get)
                .toArray())
            .toList();
            
        jdbcTemplate.batchUpdate(sql.toString(), batchArgs);
    }
    
    /**
     * 分页查询数据
     */
    public List<Map<String, Object>> queryByPage(String tableName, int page, int size) {
        String sql = "SELECT * FROM " + tableName + " LIMIT ? OFFSET ?";
        return jdbcTemplate.queryForList(sql, size, (page - 1) * size);
    }
    
    /**
     * 更新数据
     */
    public void updateCell(String tableName, Long id, String column, Object value) {
        String sql = "UPDATE " + tableName + " SET " + column + " = ? WHERE id = ?";
        jdbcTemplate.update(sql, value, id);
    }
} 