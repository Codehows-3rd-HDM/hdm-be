package com.hdmbe.repository;

import com.hdmbe.entity.ProductClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductClassRepository extends JpaRepository<ProductClass, Long> {

    // 중복 검사용
    boolean existsByClassName(String className);

}
