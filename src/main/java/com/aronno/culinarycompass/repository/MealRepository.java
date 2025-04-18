package com.aronno.culinarycompass.repository;

import com.aronno.culinarycompass.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByUserIdAndScheduledDateGreaterThanEqual(Long userId, LocalDate date);
    List<Meal> findByCompanyId(Long companyId);

    List<Meal> findByUserId(Long userId);
}
