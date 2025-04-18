package com.aronno.culinarycompass.controller;

import com.aronno.culinarycompass.entity.Meal;
import com.aronno.culinarycompass.service.MealService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/meals")
public class MealController {
    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    public ResponseEntity<Meal> createMeal(@RequestBody Meal meal) {
        return ResponseEntity.ok(mealService.createMeal(meal));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Meal>> getUserMeals(@PathVariable Long userId) {
        return ResponseEntity.ok(mealService.getUserMeals(userId));
    }

    @PutMapping("/{mealId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Meal> approveMeal(@PathVariable Long mealId) {
        return ResponseEntity.ok(mealService.approveMeal(mealId));
    }
}
