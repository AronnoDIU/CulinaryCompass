package com.aronno.culinarycompass.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Data
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDate scheduledDate;
    @ManyToMany
    private Set<Ingredient> ingredients;
    private BigDecimal totalCost;
    @ManyToOne
    private User user;
    private String status; // PENDING, APPROVED, REJECTED
    @ManyToOne
    private Company company;
}
