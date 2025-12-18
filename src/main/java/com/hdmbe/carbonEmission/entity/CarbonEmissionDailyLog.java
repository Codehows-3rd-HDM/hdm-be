package com.hdmbe.carbonEmission.entity;

import com.hdmbe.commonModule.entity.BaseTimeEntity;
import com.hdmbe.vehicle.entity.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;


@Entity
@Table(name = "CARBON_EMISSION_DAILY_LOG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarbonEmissionDailyLog extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 로그 ID
    @Column(name = "emission_log_id", columnDefinition = "BIGINT")
    private Long id;


//    @Column(name = "car_id", nullable = false)
//    private Long carId;

    // ✅ [수정] 객체(Entity)와 직접 연결!
    @ManyToOne(fetch = FetchType.LAZY) // 필요할 때만 조회 (성능 최적화)
    @JoinColumn(name = "car_id", nullable = false) // 실제 DB 컬럼명 지정
    private Vehicle vehicle;

    // 운행일자
    @Column(name = "operation_date", nullable = false)
    private LocalDate operationDate;

    // 일별배출량
    @Column(name = "daily_calculated_emission", precision = 10, scale = 3)
    private BigDecimal dailyEmission;
    //(NUMERIC) 타입을 안전한 소수계산을 위해 사용함
}
