package com.hdmbe.carCategory.repository;

import com.hdmbe.carCategory.entity.CarCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CarCategoryRepository extends JpaRepository<CarCategory, Long> {

    // 카테고리 이름으로 조회 (선택 사항)
    Optional<CarCategory> findByCategoryName(String categoryName);

    // 상위 카테고리가 없는 루트 카테고리만 조회 (필요 시)
    List<CarCategory> findByParentCategoryIsNull();

}