package com.aronno.culinarycompass.service;

import com.aronno.culinarycompass.entity.Meal;
import com.aronno.culinarycompass.entity.Status;
import com.aronno.culinarycompass.entity.User;
import com.aronno.culinarycompass.entity.meal.Ingredient;
import com.aronno.culinarycompass.exceptions.ResourceNotFoundException;
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
    private static final String ENTITY_TYPE_MEAL = "MEAL";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String MEAL_CREATED_MESSAGE = "Meal created";
    private static final String MEAL_UPDATED_MESSAGE = "Meal updated";
    
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
            .findByNameAndCategory(STATUS_PENDING, ENTITY_TYPE_MEAL)
            .orElseThrow(() -> new ResourceNotFoundException("Status " + STATUS_PENDING + " not found for category " + ENTITY_TYPE_MEAL));
        
        meal.setStatus(pendingStatus);
        calculateTotalCost(meal);
        Meal savedMeal = mealRepository.save(meal);
        
        statusService.updateEntityStatus(
            ENTITY_TYPE_MEAL,
            savedMeal.getId(),
            pendingStatus,
            currentUser,
            MEAL_CREATED_MESSAGE
        );
        
        return savedMeal;
    }

    private void calculateTotalCost(Meal meal) {
        if (meal.getIngredients() == null || meal.getIngredients().isEmpty()) {
            meal.setTotalCost(BigDecimal.ZERO);
            return;
        }
        
        BigDecimal totalCost = meal.getIngredients().stream()
            .map(Ingredient::getPrice)
            .filter(price -> price != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        meal.setTotalCost(totalCost);
    }

    @Transactional
    public Meal approveMeal(Long mealId, User admin, String comments) {
        Meal meal = mealRepository.findById(mealId)
            .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + mealId));
            
        Status approvedStatus = statusRepository
            .findByNameAndCategory(STATUS_APPROVED, ENTITY_TYPE_MEAL)
            .orElseThrow(() -> new ResourceNotFoundException("Status " + STATUS_APPROVED + " not found for category " + ENTITY_TYPE_MEAL));
            
        meal.setStatus(approvedStatus);
        Meal savedMeal = mealRepository.save(meal);
        
        statusService.updateEntityStatus(
            ENTITY_TYPE_MEAL,
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
            .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + mealId));

        Status rejectedStatus = statusRepository
            .findByNameAndCategory(STATUS_REJECTED, ENTITY_TYPE_MEAL)
            .orElseThrow(() -> new ResourceNotFoundException("Status " + STATUS_REJECTED + " not found for category " + ENTITY_TYPE_MEAL));

        meal.setStatus(rejectedStatus);
        Meal savedMeal = mealRepository.save(meal);

        statusService.updateEntityStatus(
            ENTITY_TYPE_MEAL,
            savedMeal.getId(),
            rejectedStatus,
            admin,
            reason
        );

        return savedMeal;
    }

    public Object getMealStatusHistory(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
            .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + mealId));

        return statusService.getEntityStatusHistory(ENTITY_TYPE_MEAL, meal.getId());
    }

    public List<Meal> getUserScheduledMeals(Long id, String month) {
        LocalDate date = month != null ? LocalDate.parse(month + "-01") : LocalDate.now();
        return mealRepository.findByUserIdAndScheduledDateGreaterThanEqual(id, date);
    }

    public Meal updateMeal(Long mealId, @Valid Meal mealDetails, User currentUser) {
        Meal meal = mealRepository.findById(mealId)
            .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + mealId));

        meal.setName(mealDetails.getName());
        meal.setScheduledDate(mealDetails.getScheduledDate());
        meal.setIngredients(mealDetails.getIngredients());
        calculateTotalCost(meal);

        Meal updatedMeal = mealRepository.save(meal);

        statusService.updateEntityStatus(
            ENTITY_TYPE_MEAL,
            updatedMeal.getId(),
            updatedMeal.getStatus(),
            currentUser,
            MEAL_UPDATED_MESSAGE
        );

        return updatedMeal;
    }

    public boolean isMealOwner(Long mealId, Long userId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + mealId));

        return meal.getUser().getId().equals(userId);
    }

    public void deleteMeal(Long mealId) {
        mealRepository.deleteById(mealId);
    }
}
