package com.hdmbe.repository;

import com.hdmbe.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    // 나이스파크용: 차량번호로 찾기
    Optional<Vehicle> findByCarNumber(String carNumber);

    // 에스원용: 운전자 사번으로 찾기
    // (Vehicle 엔티티에 driverMemberId 필드가 있어야 함)
    Optional<Vehicle> findByDriverMemberId(String driverMemberId);
}
