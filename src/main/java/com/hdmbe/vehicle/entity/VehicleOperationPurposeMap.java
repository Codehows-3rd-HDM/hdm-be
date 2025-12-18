package com.hdmbe.vehicle.entity;

import com.hdmbe.commonModule.entity.BaseTimeEntity;
import com.hdmbe.operationPurpose.entity.OperationPurpose;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "VEHICLE_OPERATION_PURPOSE_MAP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleOperationPurposeMap extends BaseTimeEntity {

    // 매핑 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "map_id", columnDefinition = "BIGINT")
    private Long id;

    // 차량
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Vehicle vehicle;

    // 운행 목적
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purpose_id", nullable = false)
    private OperationPurpose operationPurpose;

    // 적용 종료일 (NULL = 현재 적용 중)
    @Column(name = "end_date")
    private LocalDate endDate;
}
