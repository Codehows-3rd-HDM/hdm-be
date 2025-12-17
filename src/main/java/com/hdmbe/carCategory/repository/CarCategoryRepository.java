package com.hdmbe.carCategory.repository;

import com.hdmbe.carCategory.entity.CarCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;


public interface CarCategoryRepository extends JpaRepository<CarCategory, Long> {
    Optional<CarCategory> findByCategoryName(String categoryName);

    Optional<CarCategory> findByCategoryNameAndParentCategoryIsNull(String categoryName);

    Optional<CarCategory> findByCategoryNameAndParentCategory(String categoryName, CarCategory parentCategory);

    CarCategory parentCategory(CarCategory parentCategory);
}