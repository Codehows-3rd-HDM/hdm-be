package com.hdmbe.company.repository;

import com.hdmbe.company.entity.Company;
import com.hdmbe.company.entity.CompanySupplyTypeMap;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanySupplyTypeMapRepository
        extends JpaRepository<CompanySupplyTypeMap,Long> {
    Optional<CompanySupplyTypeMap> findByCompanyAndEndDateIsNull(Company company);
}
