package com.anomaly.backend.repository;

import com.anomaly.backend.model.DataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DataSourceRepository extends JpaRepository<DataSource, Long> {

    Optional<DataSource> findBySourceId(String sourceId);

    boolean existsBySourceId(String sourceId);
}
