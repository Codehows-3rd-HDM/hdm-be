package com.hdmbe.company.repository;

import com.hdmbe.company.entity.Company;
import com.hdmbe.company.entity.CompanySupplyCustomerMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanySupplyCustomerMapRepository extends JpaRepository<CompanySupplyCustomerMap, Long> {

    @Query("SELECT m FROM CompanySupplyCustomerMap m WHERE m.company.id = :companyId AND m.endDate IS NULL")
    Optional<CompanySupplyCustomerMap> findCurrentByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT m FROM CompanySupplyCustomerMap m WHERE m.company.id = :companyId AND m.endDate IS NULL")
    List<CompanySupplyCustomerMap> findAllCurrentByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT m FROM CompanySupplyCustomerMap m WHERE m.supplyCustomer.id = :supplyCustomerId AND m.endDate IS NULL")
    List<CompanySupplyCustomerMap> findAllCurrentBySupplyCustomerId(@Param("supplyCustomerId") Long supplyCustomerId);

    // 해당 업체의 '현재 적용 중(EndDate가 null)'인 매핑 조회
    Optional<CompanySupplyCustomerMap> findFirstByCompanyAndEndDateIsNull(Company company);
}