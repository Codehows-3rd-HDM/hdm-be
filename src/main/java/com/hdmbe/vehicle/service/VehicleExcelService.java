package com.hdmbe.vehicle.service;

import com.hdmbe.carModel.entity.CarModel;
import com.hdmbe.company.entity.Company;
import com.hdmbe.operationPurpose.entity.OperationPurpose;
import com.hdmbe.vehicle.entity.Vehicle;
import com.hdmbe.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleExcelService {
    private final VehicleRepository vehicleRepository;

    @Transactional
    public void createOrUpdate(String carNumber,
                               String carName,
                               String driverMemberId,
                               BigDecimal distance,
                               String remark,
                               Company company,             // (이미 찾아온 객체)
                               CarModel carModel,           // (이미 찾아온 객체)
                               OperationPurpose purpose)    // (이미 찾아온 객체)
    {
        // 차량번호로 조회 (중복확인)
        Optional<Vehicle> existingVehicle = vehicleRepository.findByCarNumber(carNumber);

        if(existingVehicle.isPresent()) {
            // 이미 있으면 업데이트
            Vehicle vehicle = existingVehicle.get();
            vehicle.setCarName(carName);
            vehicle.setDriverMemberId(driverMemberId);
            vehicle.setOperationDistance(distance);
            vehicle.setRemark(remark != null ? remark : "");

            //연결 정보 최신화
            vehicle.setCompany(company);
            vehicle.setCarModel(carModel);
            vehicle.setOperationPurpose(purpose);
        }
        else
        {
            // 없으면 새로 등록
            vehicleRepository.save(
                    Vehicle.builder()
                            .carNumber(carNumber)
                            .carName(carName) // 형님 전략대로 여기에 "소나타" 저장!
                            .driverMemberId(driverMemberId)
                            .operationDistance(distance)
                            .remark(remark != null ? remark : "")
                            .company(company)       // 연결
                            .carModel(carModel)     // 연결
                            .operationPurpose(purpose) // 연결
                            .build()
            );
        }
    }
}
