package com.hdmbe.company.repository;

import com.hdmbe.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findByCompanyNameContaining(String companyName);

    List<Company> findBySupplyCustomerId(Long processId);

    List<Company> findBySupplyTypeId(Long productClassId);

    List<Company> findByAddressContaining(String address);

    @Query("""
        SELECT c FROM Company c
        JOIN c.supplyCustomer s
        JOIN c.supplyType st
        WHERE c.companyName LIKE %:keyword%
           OR c.address LIKE %:keyword%
           OR s.customerName LIKE %:keyword%
           OR st.supplyTypeName LIKE %:keyword%
    """)
    List<Company> searchByKeyword(@Param("keyword") String keyword);

    Optional<Company> findByCompanyName(String companyName);
}

