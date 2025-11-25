package com.hdmbe.repository;

import com.hdmbe.entity.ProductClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductClassRepository extends JpaRepository<ProductClass, Long> {

    boolean existsByClassName(String className);
}
