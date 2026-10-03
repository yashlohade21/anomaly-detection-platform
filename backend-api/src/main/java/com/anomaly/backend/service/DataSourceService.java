package com.anomaly.backend.service;

import com.anomaly.backend.model.DataSource;
import com.anomaly.backend.repository.DataSourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DataSourceService {

    private final DataSourceRepository dataSourceRepository;

    public List<DataSource> getAll() {
        return dataSourceRepository.findAll();
    }

    public DataSource getById(Long id) {
        return dataSourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DataSource not found with id: " + id));
    }

    public DataSource getBySourceId(String sourceId) {
        return dataSourceRepository.findBySourceId(sourceId)
                .orElseThrow(() -> new RuntimeException("DataSource not found with sourceId: " + sourceId));
    }

    public DataSource create(DataSource dataSource) {
        if (dataSourceRepository.existsBySourceId(dataSource.getSourceId())) {
            throw new RuntimeException("DataSource already exists with sourceId: " + dataSource.getSourceId());
        }
        return dataSourceRepository.save(dataSource);
    }

    public DataSource update(Long id, DataSource updated) {
        DataSource existing = getById(id);
        existing.setName(updated.getName());
        existing.setType(updated.getType());
        existing.setHostname(updated.getHostname());
        existing.setRegion(updated.getRegion());
        existing.setStatus(updated.getStatus());
        return dataSourceRepository.save(existing);
    }

    public void delete(Long id) {
        DataSource existing = getById(id);
        dataSourceRepository.delete(existing);
    }
}
