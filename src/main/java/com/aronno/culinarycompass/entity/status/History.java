package com.aronno.culinarycompass.entity.status;

import com.aronno.culinarycompass.entity.Status;
import com.aronno.culinarycompass.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Status status;
    
    private String entityType;  // e.g., "Meal", "Order"
    private Long entityId;      // ID of the related entity
    
    @ManyToOne
    private User updatedBy;
    
    private LocalDateTime createdAt;
    private String comments;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
