package com.hdmbe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "NICEPARK_VEHICLE_LOG")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class NiceparkLog extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 로그 ID
    @Column(name = "log_id", columnDefinition = "BIGINT")
    private Long id;

    // 차량번호
    @Column(name = "car_number", length = 10, nullable = false)
    private String carNumber;

    // 출입일시
    @Column(name = "access_time", nullable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime accessTime;
}