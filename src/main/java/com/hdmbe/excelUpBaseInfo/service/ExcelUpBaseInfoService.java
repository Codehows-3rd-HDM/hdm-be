package com.hdmbe.excelUpBaseInfo.service;


import com.hdmbe.SupplyCustomer.entity.SupplyCustomer;
import com.hdmbe.SupplyCustomer.repository.SupplyCustomerRepository;
import com.hdmbe.carModel.entity.CarModel;
import com.hdmbe.carModel.service.CarModelExcelService;
import com.hdmbe.carbonEmission.service.CarbonEmissionFactorService;
import com.hdmbe.company.entity.Company;
import com.hdmbe.company.repository.CompanyRepository;
import com.hdmbe.company.service.CompanyExcelService;
import com.hdmbe.excelUpBaseInfo.dto.ExcelUpBaseInfoDto;
import com.hdmbe.operationPurpose.entity.OperationPurpose;
import com.hdmbe.operationPurpose.service.OperationPurposeService;
import com.hdmbe.supplyType.entity.SupplyType;
import com.hdmbe.supplyType.repository.SupplyTypeRepository;
import com.hdmbe.vehicle.service.VehicleExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExcelUpBaseInfoService {
    // 우리가 만든 어벤져스 서비스들 총출동
    private final OperationPurposeService purposeService;
    private final CarbonEmissionFactorService factorService;
    private final CompanyExcelService companyExcelService;
    private final CarModelExcelService carModelExcelService;
    private final VehicleExcelService vehicleExcelService;
    private final CompanyRepository companyRepository;
    private final SupplyTypeRepository supplyTypeRepository;
    private final SupplyCustomerRepository supplyCustomerRepository;

    @Transactional
    public void uploadMasterData(List<ExcelUpBaseInfoDto> dtoList) {

        validateFuelConsistency(dtoList);

        for (ExcelUpBaseInfoDto dto : dtoList) {
            try {
                // 1. [기초] 운행 목적 & 배출 계수 확보
                OperationPurpose purpose = purposeService.getOrCreate(dto.getPurposeName(), dto.getScope());

                if (dto.getFuelName() != null && !dto.getFuelName().trim().isEmpty()
                        && dto.getEmissionFactor() != null) {

                    factorService.getOrCreate(dto.getFuelName(), dto.getEmissionFactor());
                }

                // 거리 계산 로직 (서비스 호출 전에 미리 결정!)
                BigDecimal finalCompanyDistance;

                // 현대정밀이면 0, 아니면 엑셀값 그대로
                if ("현대정밀".equals(dto.getCompanyName())) {
                    finalCompanyDistance = BigDecimal.ZERO;
                } else {
                    finalCompanyDistance = dto.getDistanceInput();
                }

                // [수정 1] 엑셀에 적힌 글자("용접")로 실제 유형 Entity("용접"객체)를 찾아옴
                // 공급유형: 값이 있을 때만 조회, 없으면 null
                SupplyType typeEntity = null;
                if (dto.getSupplyTypeName() != null && !dto.getSupplyTypeName().trim().isEmpty()) {
                    String typeName = dto.getSupplyTypeName().trim();

                    typeEntity = supplyTypeRepository.findBySupplyTypeName(typeName)
                            .orElseGet(() -> {
                                // DB에 없으면 에러 내지 말고, 여기서 바로 만들어서 저장!
                                SupplyType newType = SupplyType.builder()
                                        .supplyTypeName(typeName)
                                        // .remark("") // 필요한 필드가 더 있으면 여기서 채우세요
                                        .build();
                                return supplyTypeRepository.save(newType);
                            });
                }
                // [수정 2] 엑셀에 적힌 글자("현대")로 실제 고객 Entity("현대"객체)를 찾아옴
                // 공급고객: 값이 있을 때만 조회, 없으면 null
                SupplyCustomer customerEntity = null;
                if (dto.getSupplyCustomerName() != null && !dto.getSupplyCustomerName().trim().isEmpty()) {
                    String customerName = dto.getSupplyCustomerName().trim();

                    customerEntity = supplyCustomerRepository.findByCustomerName(customerName)
                            .orElseGet(() -> {
                                // DB에 없으면 바로 생성!
                                SupplyCustomer newCustomer = SupplyCustomer.builder()
                                        .customerName(customerName)
                                        // .remark("")
                                        .build();
                                return supplyCustomerRepository.save(newCustomer);
                            });
                }

                // 2. [중간] 업체 저장 (부모인 유형, 고객은 안에서 처리함)
                Company company = companyExcelService.getOrCreate(
                        dto.getCompanyName(),
                        dto.getAddress(),
                        finalCompanyDistance,
                        "",
                        typeEntity,      // null 가능
                        customerEntity   // null 가능
                );

                // 3. [중간] 차종 스펙 저장 (부모인 카테고리는 안에서 처리함)
                CarModel carModel = carModelExcelService.getOrCreate(
                        dto.getBigCategory(),
                        dto.getSmallCategory(),
                        dto.getFuelName(),
                        dto.getEfficiency()

                );

                // 4. [최종] 차량 저장 (주인공)
                vehicleExcelService.createOrUpdate(
                        dto.getCarNumber(),    // 차량번호
                        dto.getCarModelName(), // 차종명 (소나타) -> DTO에 필드 있는지 확인!
                        dto.getDriverMemberId(),     // 사원번호
                        dto.getDistanceInput(), // 운행거리
                        "",                    // 비고
                        company,               // 위에서 만든 업체
                        carModel,              // 위에서 만든 차종
                        purpose,                // 위에서 만든 목적
                        dto.getCalcBaseDate()   //차량등록일
                );

            } catch (Exception e) {
                // 한 줄 에러 나도 멈추지 말고 로그 찍고 계속 갈지, 멈출지 결정
                // (일단은 에러 터뜨려서 트랜잭션 롤백 시키는 게 안전함)
                throw new RuntimeException("엑셀 업로드 중 오류 발생 (차량번호: " + dto.getCarNumber() + ") - " + e.getMessage(), e);
            }
        }
    }

    // [검증 메서드] 연료별 배출계수 일관성 체크
    private void validateFuelConsistency(List<ExcelUpBaseInfoDto> dtoList) {
        // Key: "연료명", Value: "배출계수"
        Map<String, BigDecimal> fuelMap = new HashMap<>();

        for (int i = 0; i < dtoList.size(); i++) {
            ExcelUpBaseInfoDto dto = dtoList.get(i);

            // 연료나 배출계수가 비어있으면 패스
            if (dto.getFuelName() == null || dto.getEmissionFactor() == null) continue;

            String fuelName = dto.getFuelName().trim();
            BigDecimal currentFactor = dto.getEmissionFactor();

            if (fuelMap.containsKey(fuelName)) {
                // 이미 이 연료가 등장했었다면, 값 비교
                BigDecimal firstFactor = fuelMap.get(fuelName);

                if (firstFactor.compareTo(currentFactor) != 0) {
                    throw new IllegalArgumentException(
                            String.format("데이터 불일치 오류! \n" +
                                            "연료 [%s]의 배출계수가 통일되지 않았습니다.\n" +
                                            "기존 값: %s vs 현재 값: %s (행 번호: %d)\n" +
                                            "※ 같은 연료라면 모든 행의 배출계수가 동일해야 합니다.",
                                    fuelName, firstFactor, currentFactor, i + 1)
                    );
                }
            } else {
                // 처음 본 연료면 등록
                fuelMap.put(fuelName, currentFactor);
            }
        }
    }

}
