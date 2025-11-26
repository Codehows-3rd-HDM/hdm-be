package com.hdmbe.repository;

import com.hdmbe.entity.ProductClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductClassRepository extends JpaRepository<ProductClass, Long> {

    boolean existsByClassName(String className);
    List<ProductClass> findByClassNameContainingIgnoreCase(String className);

}
