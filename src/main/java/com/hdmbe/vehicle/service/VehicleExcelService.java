package com.hdmbe.vehicle.service;

import com.hdmbe.carModel.entity.CarModel;
import com.hdmbe.company.entity.Company;
import com.hdmbe.operationPurpose.entity.OperationPurpose;
import com.hdmbe.operationPurpose.repository.OperationPurposeRepository;
import com.hdmbe.vehicle.entity.Vehicle;
import com.hdmbe.vehicle.entity.VehicleOperationPurposeMap;
import com.hdmbe.vehicle.repository.VehicleOperationPurposeMapRepository;
import com.hdmbe.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleExcelService {
    private final VehicleRepository vehicleRepository;
    private final VehicleOperationPurposeMapRepository vehicleOperationPurposeMapRepository;

    @Transactional
    public void createOrUpdate(String carNumber,
                               String carName,
                               String driverMemberId,
                               BigDecimal distance,
                               String remark,
                               Company company,             // (이미 찾아온 객체)
                               CarModel carModel,           // (이미 찾아온 객체)
                               OperationPurpose newPurpose)    // (이미 찾아온 객체)
    {
        // 1. [Vehicle] 기본 정보 저장/업데이트
        Vehicle savedVehicle = vehicleRepository.findByCarNumber(carNumber)
                .map(existing -> {
                    // 업데이트
                    existing.setCarName(carName);
                    existing.setDriverMemberId(driverMemberId);
                    existing.setOperationDistance(distance);
                    existing.setRemark(remark != null ? remark : "");

                    // Company, CarModel은 이력 관리 안 한다면 그냥 set
                    existing.setCompany(company);
                    existing.setCarModel(carModel);

                    return existing; // Dirty Checking
                })
                .orElseGet(() ->
                        // 신규 등록
                        vehicleRepository.save(
                                Vehicle.builder()
                                        .carNumber(carNumber)
                                        .carName(carName)
                                        .driverMemberId(driverMemberId)
                                        .operationDistance(distance)
                                        .remark(remark != null ? remark : "")
                                        .company(company)
                                        .carModel(carModel)
                                        .build()
                        )
                );

        // 2. [OperationPurpose] 이력 관리 로직 적용 (Company랑 똑같음!)
        updatePurposeHistory(savedVehicle, newPurpose);
    }

    // --- 이력 관리 메소드 ---
    private void updatePurposeHistory(Vehicle vehicle, OperationPurpose newPurpose) {
        vehicleOperationPurposeMapRepository.findFirstByVehicleAndEndDateIsNull(vehicle)
                .ifPresentOrElse(
                        currentMap -> {
                            // 값이 바뀌었는지 확인
                            if (!currentMap.getOperationPurpose().getId().equals(newPurpose.getId())) {
                                // A. 기존 이력 종료 (어제 날짜)
                                currentMap.setEndDate(LocalDate.now().minusDays(1));

                                // B. 새 이력 시작
                                VehicleOperationPurposeMap newMap = VehicleOperationPurposeMap.builder()
                                        .vehicle(vehicle)
                                        .operationPurpose(newPurpose)
                                        .endDate(null)
                                        .build();
                                vehicleOperationPurposeMapRepository.save(newMap);
                            }
                        },
                        () -> {
                            // 신규 차량이라 매핑 정보가 아예 없는 경우
                            VehicleOperationPurposeMap newMap = VehicleOperationPurposeMap.builder()
                                    .vehicle(vehicle)
                                    .operationPurpose(newPurpose)
                                    .endDate(null)
                                    .build();
                            vehicleOperationPurposeMapRepository.save(newMap);
                        }
                );
    }
}
