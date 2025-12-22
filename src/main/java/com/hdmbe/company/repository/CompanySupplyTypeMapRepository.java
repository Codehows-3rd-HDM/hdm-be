package com.hdmbe.company.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hdmbe.company.entity.Company;
import com.hdmbe.company.entity.CompanySupplyTypeMap;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanySupplyTypeMapRepository
        extends JpaRepository<CompanySupplyTypeMap, Long> {

    Optional<CompanySupplyTypeMap> findByCompanyAndEndDateIsNull(Company company);

    boolean existsBySupplyTypeIdAndEndDateIsNull(Long supplyTypeId);

    void deleteByCompany(Company company);

    @Query("SELECT m FROM CompanySupplyTypeMap m WHERE m.supplyType.id = :supplyTypeId AND m.endDate IS NULL")
    List<CompanySupplyTypeMap> findAllCurrentBySupplyTypeId(@Param("supplyTypeId") Long supplyTypeId);

    // 해당 업체의 '현재 적용 중(EndDate가 null)'인 매핑 조회
    Optional<CompanySupplyTypeMap> findFirstByCompanyAndEndDateIsNull(Company company);
}
