package com.anomaly.backend.controller;

import com.anomaly.backend.dto.DashboardStatsDto;
import com.anomaly.backend.service.AnomalyEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AnomalyEventService anomalyEventService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getStats() {
        return ResponseEntity.ok(anomalyEventService.getDashboardStats());
    }
}
