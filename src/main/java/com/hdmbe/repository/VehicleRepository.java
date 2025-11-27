package com.hdmbe.repository;

import com.hdmbe.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByCarNumber(String carNumber); // 중복 체크용

    List<Vehicle> findByCarNumberContainingIgnoreCase(String carNumber);

    List<Vehicle> findByCompanyId(Long companyId);

    List<Vehicle> findByDriverMemberIdContainingIgnoreCase(String driverMemberID);
}
