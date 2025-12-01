package com.hdmbe.service;

import com.hdmbe.dto.VehicleRequestDto;
import com.hdmbe.dto.VehicleResponseDto;
import com.hdmbe.dto.VehicleSearchDto;
import com.hdmbe.entity.CarModel;
import com.hdmbe.entity.Company;
import com.hdmbe.entity.OperationPurpose;
import com.hdmbe.entity.Vehicle;
import com.hdmbe.repository.CarModelRepository;
import com.hdmbe.repository.CompanyRepository;
import com.hdmbe.repository.OperationPurposeRepository;
import com.hdmbe.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CarModelRepository carModelRepository;
    private final CompanyRepository companyRepository;
    private final OperationPurposeRepository operationPurposeRepository;

    // 등록
    @Transactional
    public VehicleResponseDto create(VehicleRequestDto dto) {

        if (vehicleRepository.existsByCarNumber(dto.getCarNumber())) {
            throw new RuntimeException("이미 등록된 차량번호입니다: " + dto.getCarNumber());
        }

        CarModel carModel = carModelRepository.findById(dto.getCarModelId())
                .orElseThrow(() -> new RuntimeException("차종이 존재하지 않습니다: " + dto.getCarModelId()));

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("회사 정보가 존재하지 않습니다: " + dto.getCompanyId()));

        OperationPurpose purpose = operationPurposeRepository.findById(dto.getPurposeId())
                .orElseThrow(() -> new RuntimeException("운행목적이 존재하지 않습니다: " + dto.getPurposeId()));

        Vehicle saved = vehicleRepository.save(
                Vehicle.builder()
                        .carNumber(dto.getCarNumber())
                        .carName(dto.getCarName())
                        .carModel(carModel)
                        .driverMemberId(dto.getDriverMemberId())
                        .company(company)
                        .operationPurpose(purpose)
                        .operationDistance(dto.getOperationDistance())
                        .remark(dto.getRemark())
                        .build()
        );

        return VehicleResponseDto.fromEntity(saved);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public List<VehicleResponseDto> getAll() {
        return vehicleRepository.findAll().stream()
                .map(VehicleResponseDto::fromEntity)
                .toList();
    }

    // 검색
    @Transactional(readOnly = true)
    public List<VehicleResponseDto> search(VehicleSearchDto dto) {

        return vehicleRepository.findAll().stream()
                .filter(v -> dto.getCarNumber() == null
                        || v.getCarNumber().contains(dto.getCarNumber()))

                .filter(v -> dto.getCompanyId() == null
                        || (v.getCompany() != null
                        && v.getCompany().getId().equals(dto.getCompanyId())))

                .filter(v -> dto.getDriverMemberId() == null
                        || (v.getDriverMemberId() != null
                        && v.getDriverMemberId().contains(dto.getDriverMemberId())))

                .map(VehicleResponseDto::fromEntity)
                .toList();
    }
}
