package com.hdmbe.excelUpBaseInfo.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ExcelUpBaseInfoDto {

    // 1. [기초] 운행 목적 & 배출 계수
    private String purposeName;       // 운행목적 (납품, 출퇴근)
    private String scope;             // Scope (1, 3) -> 서비스에서 숫자로 변환함
    private String fuelName;          // 연료 종류 (디젤, 가솔린)
    private BigDecimal emissionFactor;// 탄소배출계수

    // 2. [중간] 업체 정보
    private String companyName;        // 업체명
    private String address;            // 주소
    private String supplyTypeName;     // 공급 유형 (가공, 소재)
    private String supplyCustomerName; // 공급 고객 (1차, 2차)
    //private BigDecimal oneWayDistance; // 편도 거리

    // 3. [중간] 차종 스펙
    private String bigCategory;    // 차종 대분류 (승용차, 상용트럭)
    private String smallCategory;  // 차종 소분류 (중형, 1t급)
    private BigDecimal efficiency; // 연비

    // 4. [최종] 차량 정보
    private String carNumber;         // 차량번호
    private String carModelName;      // 차종명 -> Vehicle의 carName에 저장
    private String driverMemberId;          // 사원번호
    //private  String employeeName;
    //private BigDecimal operationDistance; // 운행거리 (보통 편도랑 같거나 다를 수 있음)

    // 5. 거리 (일단 프론트에서 거리 하나로 받은 다음에 operationDistance, oneWayDistance 나눠서 저장)
    private BigDecimal distanceInput;
}
