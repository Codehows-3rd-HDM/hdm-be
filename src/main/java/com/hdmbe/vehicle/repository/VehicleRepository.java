package com.hdmbe.vehicle.repository;

import com.hdmbe.vehicle.entity.Vehicle;
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

    @Query("""
        SELECT v FROM Vehicle v
        JOIN v.company c
        JOIN v.carModel cm
        JOIN v.operationPurpose op
        WHERE v.carNumber LIKE %:keyword%
           OR c.companyName LIKE %:keyword%
           OR v.driverMemberId LIKE %:keyword%
           OR v.carName LIKE %:keyword%
           OR op.purposeName LIKE %:keyword%
    """)
    List<Vehicle> searchByKeyword(@Param("keyword") String keyword);
}
