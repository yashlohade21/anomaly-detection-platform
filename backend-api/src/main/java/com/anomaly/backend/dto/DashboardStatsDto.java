package com.anomaly.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {

    private long totalAnomalies;
    private long criticalCount;
    private long highCount;
    private long mediumCount;
    private long lowCount;
    private Map<String, Long> anomaliesBySource;
    private List<AnomalyEventDto> recentAnomalies;
}
