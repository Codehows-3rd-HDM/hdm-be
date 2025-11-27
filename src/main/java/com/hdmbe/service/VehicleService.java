package com.hdmbe.service;

import com.hdmbe.dto.VehicleRequestDto;
import com.hdmbe.dto.VehicleResponseDto;
import com.hdmbe.dto.VehicleSearchDto;
import com.hdmbe.entity.Vehicle;
import com.hdmbe.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        return VehicleResponseDto.fromEntity(saved);
    }
    @Transactional(readOnly = true)
    public List<VehicleResponseDto> getAll() {
        return vehicleRepository.findAll().stream()
                .map(VehicleResponseDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VehicleResponseDto> search(VehicleSearchDto searchDto) {
        String type = searchDto.getType();
        String keyword = searchDto.getKeyword();

        if (type == null || type.equals("all") || keyword == null || keyword.isEmpty()) {
            return getAll();
        }

        switch (type) {
            case "carNumber":
                return vehicleRepository.findByCarNumberContainingIgnoreCase(keyword)
                        .stream()
                        .map(VehicleResponseDto::fromEntity)
                        .toList();

            case "companyId":
                try {
                    Long companyId = Long.parseLong(keyword);
                    return vehicleRepository.findByCompanyId(companyId)
                            .stream()
                            .map(VehicleResponseDto::fromEntity)
                            .toList();
                } catch (NumberFormatException e) {
                    throw new RuntimeException("회사 ID는 숫자여야 합니다.");
                }

            case "driverMemberId":
                return vehicleRepository.findByDriverMemberIdContainingIgnoreCase(keyword)
                        .stream()
                        .map(VehicleResponseDto::fromEntity)
                        .toList();

            default:
                return getAll();
        }
    }
}
