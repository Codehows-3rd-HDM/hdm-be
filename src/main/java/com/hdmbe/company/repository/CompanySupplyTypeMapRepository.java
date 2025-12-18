package com.hdmbe.company.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hdmbe.company.entity.Company;
import com.hdmbe.company.entity.CompanySupplyTypeMap;

public interface CompanySupplyTypeMapRepository
        extends JpaRepository<CompanySupplyTypeMap, Long> {

    static boolean existsBySupplyTypeIdAndEndDateIsNull(Long id) {
        return CompanySupplyTypeMapRepository.existsBySupplyTypeIdAndEndDateIsNull(id);
    }

    Optional<CompanySupplyTypeMap> findByCompanyAndEndDateIsNull(Company company);

}
