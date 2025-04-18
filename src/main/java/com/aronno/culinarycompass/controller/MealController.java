package com.aronno.culinarycompass.controller;

import com.aronno.culinarycompass.entity.Meal;
import com.aronno.culinarycompass.entity.User;
import com.aronno.culinarycompass.service.MealService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealController {
    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Meal> createMeal(
            @Valid @RequestBody Meal meal,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(mealService.createMeal(meal, currentUser));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('CUSTOMER') and #userId == authentication.principal.id")
    public ResponseEntity<List<Meal>> getUserMeals(@PathVariable Long userId) {
        return ResponseEntity.ok(mealService.getUserMeals(userId));
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Meal>> getCompanyMeals(@PathVariable Long companyId) {
        return ResponseEntity.ok(mealService.getMealsByCompany(companyId));
    }

    @PutMapping("/{mealId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Meal> approveMeal(
            @PathVariable Long mealId,
            @RequestParam(required = false) String comments,
            @AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(mealService.approveMeal(mealId, admin, comments));
    }

    @PutMapping("/{mealId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Meal> rejectMeal(
            @PathVariable Long mealId,
            @RequestParam String reason,
            @AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(mealService.rejectMeal(mealId, admin, reason));
    }

    @GetMapping("/{mealId}/status-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<?> getMealStatusHistory(@PathVariable Long mealId) {
        return ResponseEntity.ok(mealService.getMealStatusHistory(mealId));
    }

    @GetMapping("/scheduled")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Meal>> getUserScheduledMeals(
            @RequestParam(required = false) String month,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(mealService.getUserScheduledMeals(currentUser.getId(), month));
    }

    @PutMapping("/{mealId}")
    @PreAuthorize("hasRole('CUSTOMER') and @mealService.isMealOwner(#mealId, authentication.principal.id)")
    public ResponseEntity<Meal> updateMeal(
            @PathVariable Long mealId,
            @Valid @RequestBody Meal mealDetails,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(mealService.updateMeal(mealId, mealDetails, currentUser));
    }

    @DeleteMapping("/{mealId}")
    @PreAuthorize("hasRole('CUSTOMER') and @mealService.isMealOwner(#mealId, authentication.principal.id)")
    public ResponseEntity<?> deleteMeal(@PathVariable Long mealId) {
        mealService.deleteMeal(mealId);
        return ResponseEntity.ok().build();
    }
}
