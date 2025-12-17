package com.hdmbe.vehicle.service;

import com.hdmbe.vehicle.dto.VehicleResponseDto;

import com.hdmbe.vehicle.entity.VehicleOperationPurposeMap;
import com.hdmbe.vehicle.repository.VehicleOperationPurposeMapRepository;
import com.hdmbe.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleOperationPurposeMapRepository vehicleOperationPurposeMapRepository;


    // 등록
//    @Transactional
//    public VehicleResponseDto create(VehicleRequestDto dto) {
//
//        // Company 찾기: ID가 있으면 ID로, 없으면 이름으로 찾기
//        Company company;
//        if (dto.getCompanyId() != null) {
//            company = companyRepository.findById(dto.getCompanyId())
//                    .orElseThrow(() -> new EntityNotFoundException("업체를 찾을 수 없습니다."));
//        } else if (dto.getCompanyNameForCreation() != null && !dto.getCompanyNameForCreation().isEmpty()) {
//            // 이름으로 찾아 첫 번째 결과 사용 (중복된 이름이 없다고 가정)
//            List<Company> companies = companyRepository.findAll();
//            company = companies.stream()
//                    .filter(c -> c.getCompanyName().equals(dto.getCompanyNameForCreation()))
//                    .findFirst()
//                    .orElseThrow(() -> new EntityNotFoundException("업체를 찾을 수 없습니다: " + dto.getCompanyNameForCreation()));
//        } else {
//            throw new EntityNotFoundException("업체 ID 또는 이름이 필요합니다.");
//        }
//
//        // OperationPurpose 찾기: ID가 있으면 ID로, 없으면 이름으로 찾기
//        OperationPurpose operationPurpose;
//        if (dto.getOperationPurposeId() != null) {
//            operationPurpose = operationPurposeRepository.findById(dto.getOperationPurposeId())
//                    .orElseThrow(() -> new EntityNotFoundException("운행목적을 찾을 수 없습니다."));
//        } else if (dto.getPurposeName() != null && !dto.getPurposeName().isEmpty()) {
//            List<OperationPurpose> purposes = operationPurposeRepository.findAll();
//            operationPurpose = purposes.stream()
//                    .filter(p -> p.getPurposeName().equals(dto.getPurposeName()))
//                    .findFirst()
//                    .orElseThrow(() -> new EntityNotFoundException("운행목적을 찾을 수 없습니다: " + dto.getPurposeName()));
//        } else {
//            throw new EntityNotFoundException("운행목적 ID 또는 이름이 필요합니다.");
//        }
//
//        // CarModel 찾기
//        CarModel carModel;
//        if (dto.getChildCategoryId() != null && dto.getFuelType() != null) {
//            carModel = carModelRepository.findByCarCategoryIdAndFuelType(dto.getChildCategoryId(), dto.getFuelType())
//                    .orElseThrow(() -> new EntityNotFoundException("차종을 찾을 수 없습니다."));
//        } else if (dto.getCarModelId() != null) {
//            carModel = carModelRepository.findById(dto.getCarModelId())
//                    .orElseThrow(() -> new EntityNotFoundException("차종을 찾을 수 없습니다."));
//        } else {
//            throw new EntityNotFoundException("차종 ID 또는 카테고리 ID와 연료 타입이 필요합니다.");
//        }
//
//        Vehicle saved = vehicleRepository.save(
//                Vehicle.builder()
//                        .carNumber(dto.getCarNumber())
//                        .carName(dto.getCarName())
//                        .carModel(carModel)
//                        .driverMemberId(dto.getDriverMemberId())
//                        .company(company)
////                        .operationPurpose(operationPurpose)
//                        .operationDistance(dto.getOperationDistance())
//                        .remark(dto.getRemark())
//                        .build()
//        );
//
//        return VehicleResponseDto.fromEntity(saved);
//    }
//
//    // 전체 조회
    @Transactional(readOnly = true)
    public Page<VehicleResponseDto> search(
            String carNumber,
            Long purposeId,
            String companyName,
            String driverMemberId,
            String keyword,
            int page,
            int size
    ) {
        Pageable pageable =
                PageRequest.of(page, size, Sort.by("id").ascending());

        return vehicleRepository.search(
                        carNumber,
                        purposeId,
                        companyName,
                        driverMemberId,
                        keyword,
                        pageable
                )
                .map(vehicle -> {
                    VehicleOperationPurposeMap purposeMap =
                            vehicleOperationPurposeMapRepository
                                    .findByVehicleAndEndDateIsNull(vehicle)
                                    .orElse(null);

                    return VehicleResponseDto.fromEntity(vehicle, purposeMap);
                });
    }

}
