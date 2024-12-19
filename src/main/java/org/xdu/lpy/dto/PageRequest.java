package org.xdu.lpy.dto;

import lombok.Data;

@Data
public class PageRequest {
    private Integer page = 1;
    private Integer size = 20;
} 