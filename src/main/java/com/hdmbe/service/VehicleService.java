package com.hdmbe.service;

import com.hdmbe.dto.VehicleRequestDto;
import com.hdmbe.dto.VehicleResponseDto;
import com.hdmbe.entity.Vehicle;
import com.hdmbe.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    @Transactional
    public VehicleResponseDto create(VehicleRequestDto dto) {

        // 차량번호 중복 방지
        if (vehicleRepository.existsByCarNumber(dto.getCarNumber())) {
            throw new RuntimeException("이미 등록된 차량번호입니다: " + dto.getCarNumber());
        }

        Vehicle saved = vehicleRepository.save(
                Vehicle.builder()
                        .carNumber(dto.getCarNumber())
                        .carName(dto.getCarName())
                        .carModelId(dto.getCarModelId())
                        .driverMemberId(dto.getDriverMemberId())
                        .companyId(dto.getCompanyId())
                        .purposeId(dto.getPurposeId())
                        .operationDistance(dto.getOperationDistance())
                        .remark(dto.getRemark())
                        .build()
        );

        return new VehicleResponseDto(
                saved.getId(),
                saved.getCarNumber(),
                saved.getCarName(),
                saved.getCarModelId(),
                saved.getDriverMemberId(),
                saved.getCompanyId(),
                saved.getPurposeId(),
                saved.getOperationDistance(),
                saved.getRemark()
        );
    }
}
