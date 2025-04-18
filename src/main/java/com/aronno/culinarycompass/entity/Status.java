package com.aronno.culinarycompass.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;        // e.g., PENDING, APPROVED, REJECTED
    private String category;    // e.g., MEAL, ORDER, PAYMENT
    private String description;
    private Boolean isActive;
    
    @ManyToOne
    private User updatedBy;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
