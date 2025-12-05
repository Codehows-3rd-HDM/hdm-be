package com.hdmbe.carCategory.repository;

import com.hdmbe.carCategory.entity.CarCategory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CarCategoryRepository extends JpaRepository<CarCategory, Long> {
}
