package com.hdmbe.carbonEmission.service;

import com.hdmbe.carbonEmission.entity.CarbonEmissionFactor;
import com.hdmbe.carbonEmission.repository.CarbonEmissionFactorRepository;
import com.hdmbe.commonModule.constant.FuelType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CarbonEmissionFactorService {
    private final CarbonEmissionFactorRepository carbonEmissionFactorRepository;

    @Transactional
    public CarbonEmissionFactor getOrCreate(String fuelName, BigDecimal factorValue) {

        FuelType fuelType = FuelType.valueOf(fuelName);

        return  carbonEmissionFactorRepository.findByFuelType(fuelType)
                .map(existing -> {
                    // 이미 있으면 계수 업데이트 (최신화)
                    existing.setEmissionFactor(factorValue);
                    return existing;
                })
                .orElseGet(() -> carbonEmissionFactorRepository.save(
                        CarbonEmissionFactor.builder()
                                .fuelType(fuelType)
                                .emissionFactor(factorValue)
                                .unitType("kgCO2/L")
                                .build()
                ));
    }
}
