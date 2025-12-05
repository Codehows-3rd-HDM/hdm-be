package com.hdmbe.repository;

import com.hdmbe.constant.FuelType;
import com.hdmbe.entity.CarbonEmissionFactor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarbonEmissionFactorRepository extends JpaRepository<CarbonEmissionFactor, Long> {

    // 연료 타입으로 배출 계수 정보 찾기
    // (단건 조회라고 가정. 만약 날짜별로 다르다면 findTopByFuelTypeOrderByEffectiveDateDesc 등으로 변경 필요)
    CarbonEmissionFactor findByFuelType(FuelType fuelType);
}
