package com.hdmbe.company.repository;

import com.hdmbe.company.entity.Company;
import com.hdmbe.company.entity.CompanySupplyTypeMap;
import com.hdmbe.vehicle.entity.Vehicle;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanySupplyTypeMapRepository
        extends JpaRepository<CompanySupplyTypeMap,Long> {

    static boolean existsBySupplyTypeIdAndEndDateIsNull(Long id) {
        return CompanySupplyTypeMapRepository.existsBySupplyTypeIdAndEndDateIsNull(id);
    }

    Optional<CompanySupplyTypeMap> findByCompanyAndEndDateIsNull(Company company);

}
