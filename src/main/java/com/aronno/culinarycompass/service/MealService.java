package com.aronno.culinarycompass.service;

import com.aronno.culinarycompass.entity.Meal;
import com.aronno.culinarycompass.entity.Status;
import com.aronno.culinarycompass.entity.User;
import com.aronno.culinarycompass.entity.meal.Ingredient;
import com.aronno.culinarycompass.repository.MealRepository;
import com.aronno.culinarycompass.repository.StatusRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

@Service
public class MealService {
    private final MealRepository mealRepository;
    private final StatusService statusService;
    private final StatusRepository statusRepository;

    public MealService(MealRepository mealRepository, 
                      StatusService statusService,
                      StatusRepository statusRepository) {
        this.mealRepository = mealRepository;
        this.statusService = statusService;
        this.statusRepository = statusRepository;
    }

    @Transactional
    public Meal createMeal(Meal meal, User currentUser) {
        Status pendingStatus = statusRepository
            .findByNameAndCategory("PENDING", "MEAL")
            .orElseThrow(() -> new RuntimeException("Status not found"));
        
        meal.setStatus(pendingStatus);
        calculateTotalCost(meal);
        Meal savedMeal = mealRepository.save(meal);
        
        statusService.updateEntityStatus(
            "MEAL",
            savedMeal.getId(),
            pendingStatus,
            currentUser,
            "Meal created"
        );
        
        return savedMeal;
    }

    private void calculateTotalCost(Meal meal) {
        BigDecimal totalCost = BigDecimal.ZERO;
        for (Ingredient ingredient : meal.getIngredients()) {
            totalCost = totalCost.add(ingredient.getPrice());
        }
        meal.setTotalCost(totalCost);
    }

    @Transactional
    public Meal approveMeal(Long mealId, User admin, String comments) {
        Meal meal = mealRepository.findById(mealId)
            .orElseThrow(() -> new RuntimeException("Meal not found"));
            
        Status approvedStatus = statusRepository
            .findByNameAndCategory("APPROVED", "MEAL")
            .orElseThrow(() -> new RuntimeException("Status not found"));
            
        meal.setStatus(approvedStatus);
        Meal savedMeal = mealRepository.save(meal);
        
        statusService.updateEntityStatus(
            "MEAL",
            savedMeal.getId(),
            approvedStatus,
            admin,
            comments
        );
        
        return savedMeal;
    }

    public List<Meal> getUserMeals(Long userId) {
        return mealRepository.findByUserId(userId);
    }

    public List<Meal> getMealsByCompany(Long companyId) {
        return mealRepository.findByCompanyId(companyId);
    }

    public Meal rejectMeal(Long mealId, User admin, String reason) {
        Meal meal = mealRepository.findById(mealId)
            .orElseThrow(() -> new RuntimeException("Meal not found"));

        Status rejectedStatus = statusRepository
            .findByNameAndCategory("REJECTED", "MEAL")
            .orElseThrow(() -> new RuntimeException("Status not found"));

        meal.setStatus(rejectedStatus);
        Meal savedMeal = mealRepository.save(meal);

        statusService.updateEntityStatus(
            "MEAL",
            savedMeal.getId(),
            rejectedStatus,
            admin,
            reason
        );

        return savedMeal;
    }

    public Object getMealStatusHistory(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
            .orElseThrow(() -> new RuntimeException("Meal not found"));

        return statusService.getEntityStatusHistory("MEAL", meal.getId());
    }

    public List<Meal> getUserScheduledMeals(Long id, String month) {
        LocalDate date = month != null ? LocalDate.parse(month + "-01") : LocalDate.now();
        return mealRepository.findByUserIdAndScheduledDateGreaterThanEqual(id, date);
    }

    public Meal updateMeal(Long mealId, @Valid Meal mealDetails, User currentUser) {
        Meal meal = mealRepository.findById(mealId)
            .orElseThrow(() -> new RuntimeException("Meal not found"));

        meal.setName(mealDetails.getName());
        meal.setScheduledDate(mealDetails.getScheduledDate());
        meal.setIngredients(mealDetails.getIngredients());
        calculateTotalCost(meal);

        Meal updatedMeal = mealRepository.save(meal);

        statusService.updateEntityStatus(
            "MEAL",
            updatedMeal.getId(),
            updatedMeal.getStatus(),
            currentUser,
            "Meal updated"
        );

        return updatedMeal;
    }

    public boolean isMealOwner(Long mealId, Long userId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found"));

        return meal.getUser().getId().equals(userId);
    }

    public void deleteMeal(Long mealId) {
        mealRepository.deleteById(mealId);
    }
}
