package com.aronno.culinarycompass.service;

import com.aronno.culinarycompass.entity.Ingredient;
import com.aronno.culinarycompass.entity.Meal;
import com.aronno.culinarycompass.repository.MealRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

@Service
public class MealService {
    private final MealRepository mealRepository;

    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    public Meal createMeal(Meal meal) {
        meal.setStatus("PENDING");
        calculateTotalCost(meal);
        return mealRepository.save(meal);
    }

    public List<Meal> getUserMeals(Long userId) {
        return mealRepository.findByUserIdAndScheduledDateGreaterThanEqual(userId, LocalDate.now());
    }

    private void calculateTotalCost(Meal meal) {
        BigDecimal total = meal.getIngredients().stream()
                .map(Ingredient::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        meal.setTotalCost(total);
    }

    public Meal approveMeal(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found"));
        meal.setStatus("APPROVED");
        return mealRepository.save(meal);
    }
}
