package com.hdmbe.excelUpBaseInfo.service;

import com.hdmbe.SupplyCustomer.repository.SupplyCustomerRepository;
import com.hdmbe.carModel.service.CarModelExcelService;
import com.hdmbe.carbonEmission.service.CarbonEmissionFactorService;
import com.hdmbe.company.repository.CompanyRepository;
import com.hdmbe.company.service.CompanyExcelService;
import com.hdmbe.excelUpBaseInfo.dto.BaseInfoCheckDto;
import com.hdmbe.excelUpBaseInfo.dto.ExcelUpBaseInfoDto;
import com.hdmbe.operationPurpose.entity.OperationPurpose;
import com.hdmbe.operationPurpose.service.OperationPurposeService;
import com.hdmbe.supplyType.repository.SupplyTypeRepository;
import com.hdmbe.vehicle.entity.Vehicle;
import com.hdmbe.vehicle.entity.VehicleOperationPurposeMap;
import com.hdmbe.vehicle.repository.VehicleOperationPurposeMapRepository;
import com.hdmbe.vehicle.repository.VehicleRepository;
import com.hdmbe.vehicle.service.VehicleExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BaseInfoCheckService {

   private final VehicleRepository vehicleRepository;
   private final VehicleOperationPurposeMapRepository mapRepository;

    @Transactional(readOnly = true)
    public List<BaseInfoCheckDto> checkDataStatus(List<ExcelUpBaseInfoDto> dtoList)
    {
        List<BaseInfoCheckDto> results = new ArrayList<>();

        for (ExcelUpBaseInfoDto dto : dtoList)
        {
            // 1. 차량번호로 DB 조회
            Optional<Vehicle> vehicleOpt = vehicleRepository.findByCarNumber(dto.getCarNumber());

            if (vehicleOpt.isEmpty())
            {
                // [신규차량]
                results.add(BaseInfoCheckDto.builder()
                                .idx(dto.getIdx())
                                .carNumber(dto.getCarNumber())
                                .status("NEW")
                                .message("신규 등록 차량")
                                .build());
            }
            else
            {
                // [수정차량]
                Vehicle v = vehicleOpt.get();
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
                        changes.add("업체명");
                    }
                    // (4) 주소
                    if (!isSame(v.getCompany().getAddress(), dto.getAddress())) {
                        changes.add("주소");
                    }

                    // ⚠️ [주의] 공급유형/고객은 이력 관리(Map)라서 1:1 비교가 힘들 수 있음.
                    // 만약 Company 엔티티에 현재 유형 이름을 가져오는 메소드가 없다면 일단 주석 처리하거나
                    // v.getCompany().getSupplyTypes() 리스트를 뒤져야 함.
                    // 여기서는 일단 '이름' 필드가 있다고 가정하고 작성 (없으면 빨간줄 뜹니다. 확인 필요!)
                    /*
                    if (!isSame(v.getCompany().getCurrentSupplyTypeName(), dto.getSupplyTypeName())) {
                        changes.add("공급유형");
                    }
                    if (!isSame(v.getCompany().getCurrentCustomerName(), dto.getSupplyCustomerName())) {
                        changes.add("공급고객");
                    }
                    */
                } else {
                    // DB에는 업체 연결이 안 되어있는데 엑셀엔 업체명이 있다? -> 변경됨
                    if (dto.getCompanyName() != null && !dto.getCompanyName().isEmpty()) {
                        changes.add("업체 정보(신규 연결)");
                    }
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
                // 5. 배출계수 (CarbonEmissionFactor) - 보통 연료따라감
                // =========================================================
                // 배출계수는 보통 FuelType에 매핑되거나 별도 서비스로 가져와야 해서
                // Vehicle 엔티티에서 바로 꺼내기 힘들 수 있음. 여기선 생략하거나 로직 추가 필요.


                // [결과 저장]
                if (!changes.isEmpty()) {
                    String msg = String.join(", ", changes) + " 정보 불일치";
                    results.add(BaseInfoCheckDto.builder()
                            .idx(dto.getIdx())
                            .carNumber(dto.getCarNumber())
                            .status("MODIFIED")
                            .message(msg)
                            .build());
                } else {
                    results.add(BaseInfoCheckDto.builder()
                            .idx(dto.getIdx())
                            .carNumber(dto.getCarNumber())
                            .status("UNCHANGED")
                            .message("변경 사항 없음")
                            .build());
                }
            }
        }
        return results;
    }

    // [비교 함수] Enum이든, 숫자든, null이든 다 문자로 바꿔서 비교하는 함수
    private boolean isSame(Object dbValue, Object excelValue) {
        // 1. 둘 다 null이면 같다
        if (dbValue == null && excelValue == null) return true;

        // 2. 둘 중 하나만 null이면 다르다
        if (dbValue == null || excelValue == null) return false;

        // 3. String으로 변환 (Enum은 이름으로, 숫자는 문자열로)
        String s1 = dbValue.toString().trim();
        String s2 = excelValue.toString().trim();

        // 4. 문자열 비교
        return s1.equals(s2);
    }
}
