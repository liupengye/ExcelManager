package org.xdu.lpy.dto;

import lombok.Data;

@Data
public class UpdateCellRequest {
    private Long id;
    private String column;
    private String value;
} 