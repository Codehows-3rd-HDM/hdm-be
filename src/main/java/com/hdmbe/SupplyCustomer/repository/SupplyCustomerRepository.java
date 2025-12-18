package com.hdmbe.SupplyCustomer.repository;

import com.hdmbe.SupplyCustomer.entity.SupplyCustomer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface SupplyCustomerRepository extends JpaRepository<SupplyCustomer, Long> {

    // 중복 검사용
    boolean existsByCustomerName(String customerName);

    List<SupplyCustomer> findByCustomerNameContaining(String customerName);

    @Query("""
        SELECT sc
        FROM SupplyCustomer sc
        WHERE
            (:customerName IS NULL OR sc.customerName LIKE %:customerName%)
    """)
    Page<SupplyCustomer> search(
            @Param("customerName") String customerName,
            Pageable pageable
    );
}


