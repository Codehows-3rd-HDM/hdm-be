package com.hdmbe.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "VEHICLE")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle extends BaseCreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 차량 ID
    @Column(name = "car_id")
    private Long id;

    // 차량번호
    @Column(name = "car_number", length = 10, nullable = false, unique = true)
    private String carNumber;

    // 차 이름
    @Column(name = "car_name", length = 20, nullable = false)
    private String carName;

    // 차종 ID
    @Column(name = "car_model_id", nullable = false)
    private Long carModelId;

    // 운전자 사번
    @Column(name = "driver_member_id", length = 10)
    private String driverMemberId;

    // 소속 업체 ID
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    // 운행목적 ID
    @Column(name = "purpose_id", nullable = false)
    private Long purposeId;

    // 운행거리
    @Column(name = "operation_distance", precision = 10, scale = 2, nullable = false)
    private BigDecimal operationDistance;
//(NUMERIC) 타입을 안전한 소수계산을 위해 사용함

    // 비고
    @Column(name = "remark", nullable = false)
    private String remark;
}