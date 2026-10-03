package com.anomaly.backend.service;

import com.anomaly.backend.dto.AnomalyEventDto;
import com.anomaly.backend.dto.DashboardStatsDto;
import com.anomaly.backend.model.AnomalyEvent;
import com.anomaly.backend.repository.AnomalyEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnomalyEventService {

    private final AnomalyEventRepository anomalyEventRepository;

    public Page<AnomalyEventDto> getAll(String severity, String sourceId,
                                         LocalDateTime startDate, LocalDateTime endDate,
                                         Pageable pageable) {
        return anomalyEventRepository
                .findFiltered(severity, sourceId, startDate, endDate, pageable)
                .map(AnomalyEventDto::fromEntity);
    }

    public AnomalyEventDto getById(Long id) {
        AnomalyEvent event = anomalyEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anomaly event not found with id: " + id));
        return AnomalyEventDto.fromEntity(event);
    }

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        stats.setTotalAnomalies(anomalyEventRepository.count());
        stats.setCriticalCount(anomalyEventRepository.countBySeverity("CRITICAL"));
        stats.setHighCount(anomalyEventRepository.countBySeverity("HIGH"));
        stats.setMediumCount(anomalyEventRepository.countBySeverity("MEDIUM"));
        stats.setLowCount(anomalyEventRepository.countBySeverity("LOW"));

        Map<String, Long> bySource = new LinkedHashMap<>();
        for (Object[] row : anomalyEventRepository.countBySourceId()) {
            bySource.put((String) row[0], (Long) row[1]);
        }
        stats.setAnomaliesBySource(bySource);

        List<AnomalyEventDto> recent = anomalyEventRepository
                .findByOrderByTimestampDesc(PageRequest.of(0, 10))
                .getContent()
                .stream()
                .map(AnomalyEventDto::fromEntity)
                .collect(Collectors.toList());
        stats.setRecentAnomalies(recent);

        return stats;
    }
}
