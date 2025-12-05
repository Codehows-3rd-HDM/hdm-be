package com.hdmbe.vehicle.repository;

import com.hdmbe.vehicle.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

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
