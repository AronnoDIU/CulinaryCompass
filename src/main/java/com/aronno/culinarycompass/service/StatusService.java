package com.aronno.culinarycompass.service;

import com.aronno.culinarycompass.entity.Status;
import com.aronno.culinarycompass.entity.User;
import com.aronno.culinarycompass.entity.status.History;
import com.aronno.culinarycompass.repository.StatusRepository;
import com.aronno.culinarycompass.repository.status.HistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StatusService {
    private final StatusRepository statusRepository;
    private final HistoryRepository historyRepository;

    public StatusService(StatusRepository statusRepository, 
                        HistoryRepository historyRepository) {
        this.statusRepository = statusRepository;
        this.historyRepository = historyRepository;
    }

    public Status createStatus(Status status) {
        return statusRepository.save(status);
    }

    public List<Status> getStatusesByCategory(String category) {
        return statusRepository.findByCategory(category);
    }

    @Transactional
    public void updateEntityStatus(String entityType, Long entityId,
                                   Status newStatus, User updatedBy, String comments) {
        History history = new History();
        history.setEntityType(entityType);
        history.setEntityId(entityId);
        history.setStatus(newStatus);
        history.setUpdatedBy(updatedBy);
        history.setComments(comments);
        
        historyRepository.save(history);
    }

    public List<History> getEntityStatusHistory(String entityType, Long entityId) {
        return historyRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            entityType, entityId);
    }
}
