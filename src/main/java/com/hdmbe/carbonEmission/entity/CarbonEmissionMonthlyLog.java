package com.hdmbe.carbonEmission.entity;

import com.hdmbe.commonModule.entity.BaseTimeEntity;
import com.hdmbe.vehicle.entity.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "CARBON_EMISSION_MONTHLY_LOG")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarbonEmissionMonthlyLog extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 월별 ID
    @Column(name = "monthly_id", columnDefinition = "BIGINT")
    private Long id;

    // 차량 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Vehicle vehicle;

    // 기준 연도
    @Column(name = "year", nullable = false)
    private Integer year;

    // 기준 월
    @Column(name = "month", nullable = false)
    private Integer month;

    // 월별 총 배출량
    @Column(name = "total_emission", precision = 12, scale = 3)
    private BigDecimal totalEmission;
    //(NUMERIC) 타입을 안전한 소수계산을 위해 사용함
}