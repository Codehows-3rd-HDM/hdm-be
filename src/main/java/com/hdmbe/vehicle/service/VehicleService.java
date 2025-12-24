package com.hdmbe.vehicle.service;

import com.hdmbe.carModel.entity.CarModel;
import com.hdmbe.carModel.repository.CarModelRepository;
import com.hdmbe.company.entity.Company;
import com.hdmbe.company.repository.CompanyRepository;
import com.hdmbe.operationPurpose.entity.OperationPurpose;
import com.hdmbe.operationPurpose.repository.OperationPurposeRepository;
import com.hdmbe.vehicle.dto.VehicleRequestDto;
import com.hdmbe.vehicle.dto.VehicleResponseDto;
import com.hdmbe.vehicle.entity.Vehicle;
import com.hdmbe.vehicle.entity.VehicleOperationPurposeMap;
import com.hdmbe.vehicle.repository.VehicleOperationPurposeMapRepository;
import com.hdmbe.vehicle.repository.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleOperationPurposeMapRepository vehicleOperationPurposeMapRepository;
    private final CompanyRepository companyRepository;
    private final OperationPurposeRepository operationPurposeRepository;
    private final CarModelRepository carModelRepository;

    // 등록
    @Transactional
    public VehicleResponseDto create(VehicleRequestDto dto) {

        // Company 찾기: ID가 있으면 ID로, 없으면 이름으로 찾기
        Company company;
        if (dto.getCompanyId() != null) {
            company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new EntityNotFoundException("업체를 찾을 수 없습니다."));
        } else if (dto.getCompanyNameForCreation() != null && !dto.getCompanyNameForCreation().isEmpty()) {
            // 이름으로 찾아 첫 번째 결과 사용 (중복된 이름이 없다고 가정)
            List<Company> companies = companyRepository.findAll();
            company = companies.stream()
                    .filter(c -> c.getCompanyName().equals(dto.getCompanyNameForCreation()))
                    .findFirst()
                    .orElseThrow(
                            () -> new EntityNotFoundException("업체를 찾을 수 없습니다: " + dto.getCompanyNameForCreation()));
        } else {
            throw new EntityNotFoundException("업체 ID 또는 이름이 필요합니다.");
        }

        // OperationPurpose 찾기: ID가 있으면 ID로, 없으면 이름으로 찾기
        OperationPurpose operationPurpose;
        if (dto.getPurposeId() != null) {
            operationPurpose = operationPurposeRepository.findById(dto.getPurposeId())
                    .orElseThrow(() -> new EntityNotFoundException("운행목적을 찾을 수 없습니다."));
        } else if (dto.getPurposeName() != null && !dto.getPurposeName().isEmpty()) {
            List<OperationPurpose> purposes = operationPurposeRepository.findAll();
            operationPurpose = purposes.stream()
                    .filter(p -> p.getPurposeName().equals(dto.getPurposeName()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("운행목적을 찾을 수 없습니다: " + dto.getPurposeName()));
        } else {
            throw new EntityNotFoundException("운행목적 ID 또는 이름이 필요합니다.");
        }

        // CarModel 찾기
        CarModel carModel;
        if (dto.getChildCategoryId() != null && dto.getFuelType() != null) {
            carModel = carModelRepository.findByCarCategoryIdAndFuelType(dto.getChildCategoryId(), dto.getFuelType())
                    .orElseThrow(() -> new EntityNotFoundException("차종을 찾을 수 없습니다."));
        } else if (dto.getCarCategoryId() != null) {
            carModel = carModelRepository.findById(dto.getCarCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("차종을 찾을 수 없습니다."));
        } else {
            throw new EntityNotFoundException("차종 카테고리 ID와 연료 타입이 필요합니다.");
        }

        Vehicle saved = vehicleRepository.save(
                Vehicle.builder()
                        .carNumber(dto.getCarNumber())
                        .carName(dto.getCarName())
                        .carModel(
                                carModelRepository.findById(carModel.getId())
                                        .orElseThrow(() -> new EntityNotFoundException("차종을 찾을 수 없습니다.")))
                        .driverMemberId(dto.getDriverMemberId())
                        .company(company)
                        .operationDistance(
                                dto.getOperationDistance() != null ? dto.getOperationDistance() : BigDecimal.ZERO)
                        .remark(dto.getRemark())
                        .build());

        // Vehicle과 OperationPurpose 매핑
        VehicleOperationPurposeMap purposeMap = VehicleOperationPurposeMap.builder()
                .vehicle(saved)
                .operationPurpose(operationPurpose)
                .build();
        vehicleOperationPurposeMapRepository.save(purposeMap);

        return VehicleResponseDto.fromEntity(saved, purposeMap);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public Page<VehicleResponseDto> search(
            String carNumber,
            Long purposeId,
            String companyName,
            String driverMemberId,
            String keyword,
            int page,
            int size) {
        System.out.println("[VehicleService] 차량 검색 요청 - carNumber: " + carNumber
                + ", purposeId: " + purposeId + ", companyName: " + companyName
                + ", driverMemberId: " + driverMemberId + ", keyword: " + keyword
                + ", page: " + page + ", size: " + size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<Vehicle> result = vehicleRepository.search(
                carNumber,
                purposeId,
                companyName,
                driverMemberId,
                keyword,
                pageable);

        System.out.println("[VehicleService] 차량 검색 결과 - 총 개수: " + result.getTotalElements()
                + ", 현재 페이지 개수: " + result.getNumberOfElements());

        return result.map(vehicle -> {
            VehicleOperationPurposeMap purposeMap = vehicleOperationPurposeMapRepository
                    .findByVehicleAndEndDateIsNull(vehicle)
                    .orElse(null);

            return VehicleResponseDto.fromEntity(vehicle, purposeMap);
        });
    }

    // 단일 수정
    @Transactional
    public VehicleResponseDto updateSingle(Long id, VehicleRequestDto dto) {
        validateUpdate(dto);
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("차량 id 없음 = " + id));

        // 차량번호
        if (dto.getCarNumber() != null) {
            vehicle.setCarNumber(dto.getCarNumber());
        }
        // 차량 모델명 (Vehicle 자체에 저장)
        if (dto.getCarName() != null) {
            vehicle.setCarName(dto.getCarName());
        }

        // 사원번호
        if (dto.getDriverMemberId() != null) {
            vehicle.setDriverMemberId(dto.getDriverMemberId());
        }

        // 비고
        if (dto.getRemark() != null) {
            vehicle.setRemark(dto.getRemark());
        }

        // 협력사 변경
        if (dto.getCompanyId() != null) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new EntityNotFoundException("협력사 없음"));
            vehicle.setCompany(company);

            if (vehicle.getOperationDistance() == null) {
                vehicle.setOperationDistance(company.getOneWayDistance());
            }
        }
        if (dto.getOperationDistance() != null) {
            vehicle.setOperationDistance(dto.getOperationDistance());
        }
        // 차종 변경
        if (dto.getCarCategoryId() != null && dto.getFuelType() != null) {
            CarModel carModel = carModelRepository.findByCarCategoryIdAndFuelType(
                    dto.getCarCategoryId(), dto.getFuelType())
                    .orElseThrow(() -> new EntityNotFoundException("해당 하위 카테고리 + 연료 타입 조합의 차종이 없습니다."));

            vehicle.setCarModel(carModel);
        }

        // 운행목적 변경
        VehicleOperationPurposeMap newMap = null;

        if (dto.getPurposeId() != null) {

            OperationPurpose purpose =
                    operationPurposeRepository.findById(dto.getPurposeId())
                            .orElseThrow(() -> new EntityNotFoundException("운행목적 없음"));
            // 기존 목적 종료
            vehicleOperationPurposeMapRepository
                    .findByVehicleAndEndDateIsNull(vehicle)
                    .ifPresent(map -> map.setEndDate(LocalDate.now()));
            // 신규 목적 등록
            newMap = vehicleOperationPurposeMapRepository.save(
                    VehicleOperationPurposeMap.builder()
                            .vehicle(vehicle)
                            .operationPurpose(purpose)
                            .build()
            );
        }

        VehicleOperationPurposeMap currentMap =
                newMap != null
                        ? newMap
                        : vehicleOperationPurposeMapRepository
                        .findByVehicleAndEndDateIsNull(vehicle)
                        .orElse(null);

        return VehicleResponseDto.fromEntity(vehicle, currentMap);

    }
    // 전체 수정
    @Transactional
    public List<VehicleResponseDto> updateMultiple(List<VehicleRequestDto> dtoList) {
        return dtoList.stream()
                .peek(this::validateUpdate)
                .map(dto -> updateSingle(dto.getId(), dto))
                .toList();
    }

    // 단일 삭제
    @Transactional
    public void deleteSingle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("차량 id 없음 = " + id));

        // 현재 운행목적 매핑 종료 (이력 관리)
        vehicleOperationPurposeMapRepository
                .findByVehicleAndEndDateIsNull(vehicle)
                .ifPresent(vehicleOperationPurposeMapRepository::delete);

        vehicleRepository.delete(vehicle);
    }

    // 다중 삭제
    @Transactional
    public void deleteMultiple(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        for (Long id : ids) {
            deleteSingle(id);
        }
    }

    // 유효성 검사
    private void validateUpdate(VehicleRequestDto dto) {

        if (dto.getCompanyId() != null && dto.getCompanyId() <= 0) {
            throw new IllegalArgumentException("협력사 id 유효하지 않음");
        }

        if (dto.getCarCategoryId() != null && dto.getCarCategoryId() <= 0) {
            throw new IllegalArgumentException("차종 id 유효하지 않음");
        }

        if (dto.getPurposeId() != null && dto.getPurposeId() <= 0) {
            throw new IllegalArgumentException("운행목적 id 유효하지 않음");
        }

    }
}
