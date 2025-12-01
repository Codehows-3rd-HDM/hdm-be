package com.hdmbe.repository;

import com.hdmbe.entity.CarCategory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CarCategoryRepository extends JpaRepository<CarCategory, Long> {
}
