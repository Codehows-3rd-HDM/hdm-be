package com.hdmbe.company.repository;

import com.hdmbe.company.entity.Company;
import com.hdmbe.company.entity.CompanySupplyCustomerMap;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanySupplyCustomerMapRepository
        extends JpaRepository<CompanySupplyCustomerMap,Long> {

    Optional<CompanySupplyCustomerMap> findByCompanyAndEndDateIsNull(Company company);

    void deleteByCompany(Company company);
}
