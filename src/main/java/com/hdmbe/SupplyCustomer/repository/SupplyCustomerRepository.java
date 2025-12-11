package com.hdmbe.SupplyCustomer.repository;

import com.hdmbe.SupplyCustomer.entity.SupplyCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplyCustomerRepository extends JpaRepository<SupplyCustomer, Long> {

    // 중복 검사용
    boolean existsByCustomerName(String customerName);

    List<SupplyCustomer> findByCustomerNameContaining(String customerName);
}


