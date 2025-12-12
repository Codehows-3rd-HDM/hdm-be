package com.hdmbe.carCategory.repository;

import com.hdmbe.carCategory.entity.CarCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CarCategoryRepository extends JpaRepository<CarCategory, Long> {
    Optional<CarCategory> findByCategoryName(String categoryName);
}