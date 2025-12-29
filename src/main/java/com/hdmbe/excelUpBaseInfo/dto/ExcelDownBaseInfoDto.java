package com.hdmbe.excelUpBaseInfo.dto;

import com.hdmbe.commonModule.constant.FuelType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelDownBaseInfoDto {
    // ✅ [추가] 프론트엔드에서 보낸 순번(idx)을 받기 위해 여기에 꼭 있어야 함!
    private Integer idx;
    private String carNumber;         // 차량번호
    private String employeeName;
    private String driverMemberId;          // 사원번호
    private String companyName;        // 업체명
    private String supplyTypeName;     // 공급 유형 (가공, 소재)
    private String supplyCustomerName; // 공급 고객 (1차, 2차)
    private Integer defaultScope;             // Scope (1, 3) -> 서비스에서 숫자로 변환함
    private String purposeName;       // 운행목적 (납품, 출퇴근)
    private String address;            // 주소
    private BigDecimal distanceInput;
    private String carModelName;      // 차종명 -> Vehicle의 carName에 저장
    private String bigCategory;    // 차종 대분류 (승용차, 상용트럭)
    private String smallCategory;  // 차종 소분류 (중형, 1t급)
    private FuelType fuelType;          // 연료 종류 (디젤, 가솔린)
    private BigDecimal efficiency; // 연비
    private BigDecimal emissionFactor;// 탄소배출계수

    // 생성자 순서를 쿼리 순서와 100% 일치시킴
    public ExcelDownBaseInfoDto(
            int idx,
            String carNumber,
            String employeeName,
            String driverMemberId,
            String companyName,
            String supplyTypeName,
            String supplyCustomerName,
            Integer defaultScope,
            String purposeName,
            String address,
            BigDecimal operationDistance,
            String carName,
            String bigCategory,
            String smallCategory,
            FuelType fuelType,
            BigDecimal efficiency,
            BigDecimal emissionFactor
    ) {
        this.idx = idx;
        this.carNumber = carNumber;
        this.employeeName = employeeName;
        this.driverMemberId = driverMemberId;
        this.companyName = companyName;
        this.supplyTypeName = supplyTypeName;
        this.supplyCustomerName = supplyCustomerName;
        this.defaultScope = defaultScope;
        this.purposeName = purposeName;
        this.address = address;
        this.distanceInput = operationDistance;
        this.carModelName = carName;
        this.bigCategory = bigCategory;
        this.smallCategory = smallCategory;
        this.fuelType = fuelType;
        this.efficiency = efficiency;
        this.emissionFactor = emissionFactor;
    }
}
