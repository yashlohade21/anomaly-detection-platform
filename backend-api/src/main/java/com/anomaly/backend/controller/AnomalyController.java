package com.anomaly.backend.controller;

import com.anomaly.backend.dto.AnomalyEventDto;
import com.anomaly.backend.service.AnomalyEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/anomalies")
@RequiredArgsConstructor
public class AnomalyController {

    private final AnomalyEventService anomalyEventService;

    @GetMapping
    public ResponseEntity<Page<AnomalyEventDto>> getAll(
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String sourceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AnomalyEventDto> result = anomalyEventService.getAll(severity, sourceId, startDate, endDate,
                PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnomalyEventDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(anomalyEventService.getById(id));
    }
}
