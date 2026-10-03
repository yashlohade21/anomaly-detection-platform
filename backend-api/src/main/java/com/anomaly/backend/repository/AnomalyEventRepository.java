package com.anomaly.backend.repository;

import com.anomaly.backend.model.AnomalyEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnomalyEventRepository extends JpaRepository<AnomalyEvent, Long> {

    Page<AnomalyEvent> findByOrderByTimestampDesc(Pageable pageable);

    Page<AnomalyEvent> findBySeverity(String severity, Pageable pageable);

    Page<AnomalyEvent> findBySourceId(String sourceId, Pageable pageable);

    long countBySeverity(String severity);

    @Query("SELECT a FROM AnomalyEvent a WHERE " +
            "(:severity IS NULL OR a.severity = :severity) AND " +
            "(:sourceId IS NULL OR a.sourceId = :sourceId) AND " +
            "(:startDate IS NULL OR a.timestamp >= :startDate) AND " +
            "(:endDate IS NULL OR a.timestamp <= :endDate) " +
            "ORDER BY a.timestamp DESC")
    Page<AnomalyEvent> findFiltered(
            @Param("severity") String severity,
            @Param("sourceId") String sourceId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT a.sourceId, COUNT(a) FROM AnomalyEvent a GROUP BY a.sourceId")
    List<Object[]> countBySourceId();
}
