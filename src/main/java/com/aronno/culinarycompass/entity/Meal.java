package com.aronno.culinarycompass.entity;

import com.aronno.culinarycompass.entity.meal.Ingredient;
import com.aronno.culinarycompass.entity.status.History;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

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

    @ManyToOne
    private Company company;

    @ManyToOne
    private Status status;

    @OneToMany(mappedBy = "entityId")
    @Where(clause = "entityType = 'MEAL'")
    @OrderBy("createdAt DESC")
    private Set<History> statusHistory;
}
