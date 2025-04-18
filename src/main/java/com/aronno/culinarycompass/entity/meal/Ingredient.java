package com.aronno.culinarycompass.entity.meal;

import com.aronno.culinarycompass.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal price;
    private boolean isSystemIngredient;
    private String description;
    @ManyToOne
    private User addedBy;
}
