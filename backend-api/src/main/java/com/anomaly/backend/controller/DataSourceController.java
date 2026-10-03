package com.anomaly.backend.controller;

import com.anomaly.backend.model.DataSource;
import com.anomaly.backend.service.DataSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sources")
@RequiredArgsConstructor
public class DataSourceController {

    private final DataSourceService dataSourceService;

    @GetMapping
    public ResponseEntity<List<DataSource>> getAll() {
        return ResponseEntity.ok(dataSourceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataSource> getById(@PathVariable Long id) {
        return ResponseEntity.ok(dataSourceService.getById(id));
    }

    @PostMapping
    public ResponseEntity<DataSource> create(@RequestBody DataSource dataSource) {
        return ResponseEntity.ok(dataSourceService.create(dataSource));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataSource> update(@PathVariable Long id, @RequestBody DataSource dataSource) {
        return ResponseEntity.ok(dataSourceService.update(id, dataSource));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dataSourceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
