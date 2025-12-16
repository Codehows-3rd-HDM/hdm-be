package com.hdmbe.vehicle.entity;

import com.hdmbe.carModel.entity.CarModel;
import com.hdmbe.carbonEmission.entity.CarbonEmissionDailyLog;
import com.hdmbe.carbonEmission.entity.CarbonEmissionMonthlyLog;
import com.hdmbe.company.entity.Company;
import com.hdmbe.commonModule.entity.BaseTimeEntity;
import com.hdmbe.operationPurpose.entity.OperationPurpose;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "VEHICLE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 차량 ID
    @Column(name = "car_id", columnDefinition = "BIGINT")
    private Long id;

    // 차량번호
    @Column(name = "car_number", length = 10, nullable = false, unique = true)
    private String carNumber;

    // 차 이름
    @Column(name = "car_name", length = 20, nullable = false)
    private String carName;

    // 차종 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_model_id", nullable = false)
    private CarModel carModel;

    // 운전자 사번
    @Column(name = "driver_member_id", length = 10)
    private String driverMemberId;

    // 소속 업체 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // 운행목적 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purpose_id", nullable = false)
    private OperationPurpose operationPurpose;

    // 운행거리
    @Column(name = "operation_distance", precision = 10, scale = 2, nullable = false)
    private BigDecimal operationDistance;
    //(NUMERIC) 타입을 안전한 소수계산을 위해 사용함

    // 비고
    @Column(name = "remark", nullable = false)
    private String remark;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CarbonEmissionDailyLog> dailyLogs = new ArrayList<>();

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CarbonEmissionMonthlyLog> monthlyLogs = new ArrayList<>();
}
