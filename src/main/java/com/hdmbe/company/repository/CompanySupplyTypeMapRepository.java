package com.hdmbe.company.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hdmbe.company.entity.CompanySupplyTypeMap;

@Repository
public interface CompanySupplyTypeMapRepository extends JpaRepository<CompanySupplyTypeMap, Long> {

    @Query("SELECT m FROM CompanySupplyTypeMap m WHERE m.company.id = :companyId AND m.endDate IS NULL")
    Optional<CompanySupplyTypeMap> findCurrentByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT m FROM CompanySupplyTypeMap m WHERE m.company.id = :companyId AND m.endDate IS NULL")
    List<CompanySupplyTypeMap> findAllCurrentByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT m FROM CompanySupplyTypeMap m WHERE m.supplyType.id = :supplyTypeId AND m.endDate IS NULL")
    List<CompanySupplyTypeMap> findAllCurrentBySupplyTypeId(@Param("supplyTypeId") Long supplyTypeId);
}
