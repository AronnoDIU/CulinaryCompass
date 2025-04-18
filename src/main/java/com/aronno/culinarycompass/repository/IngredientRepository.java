package com.aronno.culinarycompass.repository;

import com.aronno.culinarycompass.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}
