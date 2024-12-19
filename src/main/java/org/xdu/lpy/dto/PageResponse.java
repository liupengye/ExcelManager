package org.xdu.lpy.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class PageResponse {
    private List<Map<String, Object>> records;
    private long total;
    private int page;
    private int size;
} 