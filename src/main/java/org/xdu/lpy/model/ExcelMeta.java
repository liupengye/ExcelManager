package org.xdu.lpy.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("excel_meta")
public class ExcelMeta {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String fileName;
    
    private String tableName;
    
    private String headers;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
} 