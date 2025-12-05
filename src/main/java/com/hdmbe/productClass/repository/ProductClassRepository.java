package com.hdmbe.productClass.repository;

import com.hdmbe.productClass.entity.ProductClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductClassRepository extends JpaRepository<ProductClass, Long> {

    // 중복 검사용
    boolean existsByClassName(String className);

    List<ProductClass> findByClassNameContaining(String className);
}


