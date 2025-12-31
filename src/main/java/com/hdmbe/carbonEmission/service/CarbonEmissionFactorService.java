package com.hdmbe.carbonEmission.service;

import com.hdmbe.carbonEmission.entity.CarbonEmissionFactor;
import com.hdmbe.carbonEmission.repository.CarbonEmissionFactorRepository;
import com.hdmbe.commonModule.constant.FuelType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import com.hdmbe.carbonEmission.dto.CarbonEmissionFactorResponse;
import com.hdmbe.carbonEmission.dto.CarbonEmissionFactorUpdateRequest;

import java.util.List;

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

    // 조회
    public List<CarbonEmissionFactorResponse> getAll() {
        return carbonEmissionFactorRepository.findAll()
                .stream()
                .map(CarbonEmissionFactorResponse::fromEntity)
                .toList();
    }

    // 수정
    public CarbonEmissionFactorResponse update(Long id, CarbonEmissionFactorUpdateRequest dto) {
        CarbonEmissionFactor entity = carbonEmissionFactorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 배출계수"));

        entity.setEmissionFactor(dto.getEmissionFactor());
        entity.setRemark(dto.getRemark());

        carbonEmissionFactorRepository.save(entity);

        return CarbonEmissionFactorResponse.fromEntity(entity);
    }
}
