package com.hdmbe.vehicle.repository;

import com.hdmbe.company.entity.Company;
import com.hdmbe.vehicle.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByCarNumberContaining(String carNumber);

    List<Vehicle> findByDriverMemberIdContaining(String driverMemberId);

    boolean existsByCompany(Company company);

    @Query("""
        SELECT DISTINCT v
        FROM Vehicle v
        LEFT JOIN VehicleOperationPurposeMap vop
            ON vop.vehicle = v
            AND vop.endDate IS NULL
        LEFT JOIN vop.operationPurpose op
        JOIN v.company c
        JOIN v.carModel cm
        JOIN cm.carCategory cc
        LEFT JOIN cc.parentCategory pcc
        WHERE
            (:carNumber IS NULL OR v.carNumber LIKE %:carNumber%)
        AND (:operationPurposeId IS NULL OR op.id = :operationPurposeId)
        AND (:companyName IS NULL OR c.companyName LIKE %:companyName%)
        AND (:driverMemberId IS NULL OR v.driverMemberId LIKE %:driverMemberId%)
        AND (
            :keyword IS NULL OR
            v.carNumber LIKE %:keyword%
            OR c.companyName LIKE %:keyword%
            OR v.driverMemberId LIKE %:keyword%
            OR cc.categoryName LIKE %:keyword%
            OR pcc.categoryName LIKE %:keyword%
            OR v.carName LIKE %:keyword%
        )
    """)
    Page<Vehicle> search(
            @Param("carNumber") String carNumber,
            @Param("operationPurposeId") Long operationPurposeId,
            @Param("companyName") String companyName,
            @Param("driverMemberId") String driverMemberId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
