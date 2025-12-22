package com.hdmbe.vehicle.repository;

import com.hdmbe.company.entity.Company;
import com.hdmbe.vehicle.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // 나이스파크용: 차량번호로 찾기
    Optional<Vehicle> findByCarNumber(String carNumber);

    // 에스원용: 운전자 사번으로 찾기
    // (Vehicle 엔티티에 driverMemberId 필드가 있어야 함)
    Optional<Vehicle> findByDriverMemberId(String driverMemberId);

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

    // 사번 목록만 가볍게 조회하는 쿼리 (최적화)
    // 설명: Vehicle 전체를 가져오는 게 아니라 'driverMemberId' 문자열만 가져옴!
    @Query("SELECT v.driverMemberId FROM Vehicle v WHERE v.driverMemberId IS NOT NULL")
    List<String> findAllDriverMemberIds();

    // 차량 번호만 가볍게 조회하는 쿼리 (최적화)
    // 설명: Vehicle 전체를 가져오는 게 아니라 'carNumber' 문자열만 가져옴!
    @Query("SELECT v.carNumber FROM Vehicle v WHERE v.carNumber IS NOT NULL")
    List<String> findAllCarNumbers();
}

