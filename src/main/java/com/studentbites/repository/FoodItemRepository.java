package com.studentbites.repository;

import com.studentbites.model.FoodCategory;
import com.studentbites.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByCategory(FoodCategory category);

    Optional<FoodItem> findByName(String name);
}
