package com.hdmbe.company.repository;

import com.hdmbe.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findByCompanyNameContaining(String companyName);

    List<Company> findByProcessId(Long processId);

    List<Company> findByProductClassId(Long productClassId);

    List<Company> findByAddressContaining(String address);

    @Query("""
        SELECT c FROM Company c
        JOIN c.process p
        JOIN c.productClass pc
        WHERE c.companyName LIKE %:keyword%
           OR c.address LIKE %:keyword%
           OR p.processName LIKE %:keyword%
           OR pc.className LIKE %:keyword%
    """)
    List<Company> searchByKeyword(@Param("keyword") String keyword);
}

