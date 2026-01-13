package com.hdmbe.excelUpBaseInfo.service;

import com.hdmbe.carbonEmission.entity.CarbonEmissionFactor;
import com.hdmbe.carbonEmission.repository.CarbonEmissionFactorRepository;
import com.hdmbe.commonModule.constant.FuelType;
import com.hdmbe.company.entity.CompanySupplyCustomerMap;
import com.hdmbe.company.entity.CompanySupplyTypeMap;
import com.hdmbe.company.repository.CompanySupplyCustomerMapRepository;
import com.hdmbe.company.repository.CompanySupplyTypeMapRepository;
import com.hdmbe.excelUpBaseInfo.dto.BaseInfoCheckDto;
import com.hdmbe.excelUpBaseInfo.dto.ExcelUpBaseInfoDto;
import com.hdmbe.operationPurpose.entity.OperationPurpose;
import com.hdmbe.vehicle.entity.Vehicle;
import com.hdmbe.vehicle.entity.VehicleOperationPurposeMap;
import com.hdmbe.vehicle.repository.VehicleOperationPurposeMapRepository;
import com.hdmbe.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BaseInfoCheckService {

   private final VehicleRepository vehicleRepository;
   private final VehicleOperationPurposeMapRepository mapRepository;
   private final CompanySupplyTypeMapRepository supplyTypeMapRepository;
   private final CompanySupplyCustomerMapRepository supplyCustomerMapRepository;
   private final CarbonEmissionFactorRepository carbonEmissionFactorRepository;


    @Transactional(readOnly = true)
    public List<BaseInfoCheckDto> checkDataStatus(List<ExcelUpBaseInfoDto> dtoList)
    {
        validateConsistency(dtoList);

        List<BaseInfoCheckDto> results = new ArrayList<>();

        // 날짜 포맷터 준비
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate defaultDate = LocalDate.of(1900, 1, 1);

        // [최적화] DB에 있는 모든 글로벌 배출계수를 미리 가져옴 (반복문 안에서 매번 조회하면 느리니까)
        // Key: FuelType(GASOLINE), Value: 2.13(배출계수)
        Map<FuelType, BigDecimal> globalFactorMap = carbonEmissionFactorRepository.findAll().stream()
                .collect(Collectors.toMap(CarbonEmissionFactor::getFuelType, CarbonEmissionFactor::getEmissionFactor));

        for (ExcelUpBaseInfoDto dto : dtoList)
        {
            // [수정 1] 날짜 변환 로직 (VehicleExcelService와 동일하게!)
            // 엑셀에 날짜 없으면 1900-01-01로 간주
            LocalDate targetDate = (dto.getCalcBaseDate() == null || dto.getCalcBaseDate().trim().isEmpty())
                    ? defaultDate
                    : LocalDate.parse(dto.getCalcBaseDate().trim(), formatter);

            // 최신 날짜로 찾기
            Optional<Vehicle> vehicleOpt;

            if (targetDate.equals(defaultDate)) {
                // 1. 엑셀에 날짜가 없다? -> DB에서 해당 차량의 "가장 최신 이력"을 가져옴!
                vehicleOpt = vehicleRepository.findTopByCarNumberOrderByCalcBaseDateDesc(dto.getCarNumber());
            } else {
                // 2. 엑셀에 날짜가 있다? -> 그 날짜에 해당하는 이력을 정확히 찾음
                vehicleOpt = vehicleRepository.findByCarNumberAndCalcBaseDate(dto.getCarNumber(), targetDate);
            }

            if (vehicleOpt.isEmpty())
            {
                // [신규차량]
                results.add(BaseInfoCheckDto.builder()
                                .idx(dto.getIdx())
                                .carNumber(dto.getCarNumber())
                                .rowStatus("NEW")
                                .message("신규 등록 차량")
                                .build());
            }
            else
            {
                // [수정차량]
                Vehicle v = vehicleOpt.get();

                // DB Entity(v)를 -> DTO(dbData)로 변환해서 프론트로 보낼 준비
                ExcelUpBaseInfoDto dbData = convertEntityToDto(v);

                List<String> changes = new ArrayList<>();

                // =========================================================
                // 1. 차량(Vehicle) 자체 정보 비교
                // =========================================================
                // (1) 사원번호
                if (!isSame(v.getDriverMemberId(), dto.getDriverMemberId())) {
                    changes.add("사원번호");
                }

                // (2) 운행거리 비교 (BigDecimal은 compareTo 필수)
                BigDecimal dbDist = v.getOperationDistance() != null ? v.getOperationDistance() : BigDecimal.ZERO;
                BigDecimal excelDist = dto.getDistanceInput() != null ? dto.getDistanceInput() : BigDecimal.ZERO;
                if (dbDist.compareTo(excelDist) != 0)
                {
                    changes.add("편도거리 (" + dbDist + " -> " + excelDist + ")");
                }

                // =========================================================
                // 2. 업체(Company) 관련 정보 비교
                // =========================================================
                // v.getCompany()가 null일 수도 있으니 안전하게 접근
                if (v.getCompany() != null) {
                    // (3) 업체명
                    if (!isSame(v.getCompany().getCompanyName(), dto.getCompanyName())) {
                        changes.add("협력사명");
                    }
                    // (4) 주소
                    if (!isSame(v.getCompany().getAddress(), dto.getAddress())) {
                        changes.add("주소");
                    }
                } else {
                    // DB에는 업체 연결이 안 되어있는데 엑셀엔 업체명이 있다? -> 변경됨
                    if (dto.getCompanyName() != null && !dto.getCompanyName().isEmpty()) {
                        changes.add("업체 정보(신규 연결)");
                    }
                }

                // =========================================================
                // 2-1. 공급유형 비교 (Map 기반)
                // =========================================================
                String dbSupplyTypeName = "";

                Optional<CompanySupplyTypeMap> supplyTypeMapOpt =
                        supplyTypeMapRepository.findByCompanyAndEndDateIsNull(v.getCompany());

                if (supplyTypeMapOpt.isPresent()) {
                    dbSupplyTypeName =
                            supplyTypeMapOpt.get()
                                    .getSupplyType()
                                    .getSupplyTypeName();
                }

                if (!isSame(dbSupplyTypeName, dto.getSupplyTypeName())) {
                    changes.add("공급유형");
                }

                // =========================================================
                // 2-2. 공급고객 비교 (Map 기반)
                // =========================================================
                String dbCustomerName = "";

                Optional<CompanySupplyCustomerMap> customerMapOpt =
                        supplyCustomerMapRepository.findByCompanyAndEndDateIsNull(v.getCompany());

                if (customerMapOpt.isPresent()) {
                    dbCustomerName =
                            customerMapOpt.get()
                                    .getSupplyCustomer()
                                    .getCustomerName();
                }

                if (!isSame(dbCustomerName, dto.getSupplyCustomerName())) {
                    changes.add("공급고객");
                }

                // =========================================================
                // 3. 차종(CarModel) 관련 정보 비교 (여기에 연료, 연비 다 있음!)
                // =========================================================
                if (v.getCarModel() != null) {
                    // (5) 차종명 (소나타)
                    // v.getCompany().getCarName() (X) -> v.getCarModel().getCarModelName() (O)
                    if (!isSame(v.getCarName(), dto.getCarModelName())) {
                        changes.add("차종명");
                    }

                    // (6) 대분류 (승용차) - 부모 카테고리 추적
                    String dbBigCategory = "";

                    // 1. CarModel은 위에서 체크했으니, CarCategory가 있는지 먼저 확인! (이게 안전장치)
                    if (v.getCarModel().getCarCategory() != null) {
                        // 2. 그 다음 부모가 있는지 확인
                        if (v.getCarModel().getCarCategory().getParentCategory() != null) {
                            dbBigCategory = v.getCarModel().getCarCategory().getParentCategory().getCategoryName();
                        }
                    }

                    if (!isSame(dbBigCategory, dto.getBigCategory())) {
                        changes.add("차종 대분류");
                    }

                    // (7) 소분류 (중형) - 현재 카테고리
                    String dbSmallCategory = "";

                    if (v.getCarModel().getCarCategory() != null) {
                        dbSmallCategory = v.getCarModel().getCarCategory().getCategoryName();
                    }

                    if (!isSame(dbSmallCategory, dto.getSmallCategory())) {
                        changes.add("차종 소분류");
                    }

                    // (8) 연료 종류 (Enum -> String 변환 자동 처리)
                    // v.getCarModel().getFuelType()이 Enum이라도 isSame 함수가 알아서 처리함
                    if (!isSame(v.getCarModel().getFuelType(), dto.getFuelName())) {
                        changes.add("연료 종류");
                    }

                    // (9) 연비 (숫자 -> 문자열 변환 자동 처리)
                    if (!isSame(v.getCarModel().getCustomEfficiency(), dto.getEfficiency())) {
                        changes.add("연비");
                    }
                }

                // =========================================================
                // 4. 운행 목적 & Scope 비교 (수정됨)
                // =========================================================

                String dbPurposeName = "";
                String dbScope = ""; // 비교를 위해 String으로 초기화

                // 1. 매핑 정보 가져오기
                Optional<VehicleOperationPurposeMap> mapOpt = mapRepository.findByVehicleAndEndDateIsNull(v);

                if (mapOpt.isPresent()) {
                    VehicleOperationPurposeMap map = mapOpt.get();
                    OperationPurpose op = map.getOperationPurpose();

                    if (op != null) {
                        dbPurposeName = op.getPurposeName();

                        // [수정 포인트] Integer 타입을 꺼내서 String으로 변환 저장
                        // (null일 수도 있으니 안전하게 처리)
                        if (op.getDefaultScope() != null) {
                            dbScope = String.valueOf(op.getDefaultScope());
                        }
                    }
                }

                // 비교 로직 (isSame이 "1"과 "1"을 비교하게 됨 -> OK!)
                if (!isSame(dbPurposeName, dto.getPurposeName())) {
                    changes.add("운행 목적");
                }

                if (!isSame(dbScope, dto.getScope())) {
                    changes.add("Scope");
                }

                // =========================================================
                // 5. 배출계수 비교 (연료별)
                // =========================================================
                if (dto.getFuelName() != null && !dto.getFuelName().trim().isEmpty() && dto.getEmissionFactor() != null) {
                    try {
                        // 1. 엑셀에 적힌 연료(휘발유)를 Enum으로 변환
                        FuelType excelFuelType = FuelType.valueOf(dto.getFuelName().trim().toUpperCase());

                        // 2. DB에 저장된 그 연료의 현재 기준값 가져오기
                        BigDecimal currentDbFactor = globalFactorMap.get(excelFuelType);

                        // 3. 비교 (DB값 vs 엑셀값)
                        if (currentDbFactor != null) {
                            // 값이 다르면 "변경됨"으로 처리!
                            if (currentDbFactor.compareTo(dto.getEmissionFactor()) != 0) {
                                changes.add("배출계수(" + currentDbFactor + " -> " + dto.getEmissionFactor() + ")");
                            }
                        } else {
                            // DB에 없는 연료값이면 신규나 마찬가지 -> 변경됨으로 처리
                            changes.add("배출계수(신규설정)");
                        }

                    } catch (Exception e) {
                        // 연료명이 오타이거나 Enum에 없으면 무시 (어차피 위에서 연료 종류 비교할 때 걸림)
                    }
                }


                // [결과 저장]
                if (!changes.isEmpty()) {
                    String msg = String.join(", ", changes) + " 수정";
                    results.add(BaseInfoCheckDto.builder()
                            .idx(dto.getIdx())
                            .carNumber(dto.getCarNumber())
                            .rowStatus("UPDATED")
                            .message(msg)
                            .dbData(dbData)
                            .build());
                } else {
                    results.add(BaseInfoCheckDto.builder()
                            .idx(dto.getIdx())
                            .carNumber(dto.getCarNumber())
                            .rowStatus("UNCHANGED")
                            .message("변경 사항 없음")
                            .dbData(dbData)
                            .build());
                }
            }
        }
        return results;
    }

    // [추가] 연료별 배출계수 일관성 검증 메서드
    private void validateConsistency(List<ExcelUpBaseInfoDto> dtoList) {
        // Key: "연료명", Value: "배출계수"
        Map<String, BigDecimal> consistencyMap = new HashMap<>();

        for (int i = 0; i < dtoList.size(); i++) {
            ExcelUpBaseInfoDto dto = dtoList.get(i);

            // 연료나 배출계수가 비어있으면 패스 (Null 체크는 다른데서 하거나 무시)
            if (dto.getFuelName() == null || dto.getEmissionFactor() == null) continue;

            String fuelName = dto.getFuelName().trim();
            BigDecimal currentFactor = dto.getEmissionFactor();

            if (consistencyMap.containsKey(fuelName)) {
                // 이미 이 연료가 등장했었다면, 처음에 나온 값과 똑같은지 비교
                BigDecimal firstFactor = consistencyMap.get(fuelName);

                // 값이 다르면 에러! (compareTo != 0)
                if (firstFactor.compareTo(currentFactor) != 0) {
                    throw new IllegalArgumentException(
                            String.format("데이터 불일치 오류! \n" +
                                            "연료 [%s]의 배출계수가 통일되지 않았습니다.\n" +
                                            "기준 값: %s vs 현재 값: %s (행 번호: %d)\n" +
                                            "※ 같은 연료라면 엑셀 내 모든 행의 배출계수가 동일해야 합니다.",
                                    fuelName, firstFactor, currentFactor, i + 1)
                    );
                }
            } else {
                // 처음 본 연료면 등록 (이게 이 연료의 기준값이 됨)
                consistencyMap.put(fuelName, currentFactor);
            }
        }
    }

    // [최종 수정] 수식, 소수점 오차, 공백까지 모두 무시하는 강력한 비교 함수
    private boolean isSame(Object dbValue, Object excelValue) {
        // 1. DB값과 엑셀값을 안전하게 문자열로 변환 (null이면 ""로 변환)
        String s1 = (dbValue == null) ? "" : dbValue.toString().trim();
        String s2 = (excelValue == null) ? "" : excelValue.toString().trim();

        // 2. 이제 둘 다 null일 걱정 없음. 바로 비교.
        // DB가 null이고 엑셀이 ""여도, 여기서 둘 다 ""가 되어서 true(같음)가 됨.
        if (s1.equals(s2)) {
            return true;
        }

        // 3. 숫자 비교 (소수점 무시)
        if (isNumeric(s1) && isNumeric(s2)) {
            try {
                BigDecimal b1 = new BigDecimal(s1);
                BigDecimal b2 = new BigDecimal(s2);

                // 값 비교
                if (b1.compareTo(b2) == 0) return true;

                // 미세 오차 허용
                BigDecimal diff = b1.subtract(b2).abs();
                if (diff.compareTo(new BigDecimal("0.000001")) < 0) return true;

            } catch (Exception e) {
                // 변환 실패 시 무시
            }
        }

        return false;
    }

    // 숫자 판별 헬퍼
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    // [추가] Entity -> DTO 변환 헬퍼 메서드
    private ExcelUpBaseInfoDto convertEntityToDto(Vehicle v) {
        ExcelUpBaseInfoDto.ExcelUpBaseInfoDtoBuilder builder = ExcelUpBaseInfoDto.builder()
                .carNumber(v.getCarNumber())
                .driverMemberId(v.getDriverMemberId())
                .distanceInput(v.getOperationDistance());

        // [추가] 날짜도 DTO에 담아서 프론트에 보여주면 좋음 (1900년이면 빈값 처리 등)
        if (v.getCalcBaseDate() != null) {
            if (v.getCalcBaseDate().getYear() == 1900) {
                builder.calcBaseDate(""); // 1900년은 화면에 안 보여줌
            } else {
                builder.calcBaseDate(v.getCalcBaseDate().toString());
            }
        }

        // 1. 업체 정보
        if (v.getCompany() != null) {
            builder.companyName(v.getCompany().getCompanyName());
            builder.address(v.getCompany().getAddress());

            // 1-1. 공급유형 조회
            Optional<CompanySupplyTypeMap> supplyTypeMapOpt =
                    supplyTypeMapRepository.findByCompanyAndEndDateIsNull(v.getCompany());
            supplyTypeMapOpt.ifPresent(map ->
                    builder.supplyTypeName(map.getSupplyType().getSupplyTypeName())
            );

            // 1-2. 공급고객 조회
            Optional<CompanySupplyCustomerMap> customerMapOpt =
                    supplyCustomerMapRepository.findByCompanyAndEndDateIsNull(v.getCompany());
            customerMapOpt.ifPresent(map ->
                    builder.supplyCustomerName(map.getSupplyCustomer().getCustomerName())
            );
        }

        // 2. 차종 정보
        if (v.getCarModel() != null) {
            builder.carModelName(v.getCarModel().getCarCategory().getCategoryName());
            builder.fuelName(v.getCarModel().getFuelType() != null ? v.getCarModel().getFuelType().toString() : ""); // Enum -> String
            builder.efficiency(v.getCarModel().getCustomEfficiency());

            // 카테고리 정보
            if (v.getCarModel().getCarCategory() != null) {
                builder.smallCategory(v.getCarModel().getCarCategory().getCategoryName());
                if (v.getCarModel().getCarCategory().getParentCategory() != null) {
                    builder.bigCategory(v.getCarModel().getCarCategory().getParentCategory().getCategoryName());
                }
            }
        }

        // 3. 운행 목적 및 Scope 매핑 정보
        Optional<VehicleOperationPurposeMap> mapOpt = mapRepository.findByVehicleAndEndDateIsNull(v);
        if (mapOpt.isPresent()) {
            OperationPurpose op = mapOpt.get().getOperationPurpose();
            if (op != null) {
                builder.purposeName(op.getPurposeName());
                if (op.getDefaultScope() != null) {
                    builder.scope(String.valueOf(op.getDefaultScope()));
                }
            }
        }

        return builder.build();
    }
}
