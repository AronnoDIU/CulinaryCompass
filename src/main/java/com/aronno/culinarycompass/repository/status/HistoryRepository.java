package com.aronno.culinarycompass.repository.status;

import com.aronno.culinarycompass.entity.status.History;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);
}
