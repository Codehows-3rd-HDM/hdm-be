package com.hdmbe.emissionTarget.entity;

import com.hdmbe.commonModule.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "EMISSION_TARGET")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmissionTarget extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 목표 ID
    @Column(name = "target_id", columnDefinition = "BIGINT")
    private Long id;

    //  연도
    @Column(name = "year", nullable = false)
    private Integer year;

    // 월
    @Column(name = "month")
    private Integer month; // 연간 목표면 NULL

    // 목표구분
    @Column(name = "target_type", length = 10)
    private String targetType;

    // 목표배출량
    @Column(name = "target_emission", precision = 10, scale = 3)
    private BigDecimal targetEmission;
//(NUMERIC) 타입을 안전한 소수계산을 위해 사용함
    // 설정일시
    @Column(name = "set_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime setAt;
}