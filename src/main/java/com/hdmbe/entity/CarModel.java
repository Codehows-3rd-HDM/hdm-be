package com.hdmbe.entity;

import com.hdmbe.constant.FuelType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "CAR_MODEL")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarModel extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 차종 ID
    @Column(name = "car_model_id")
    private Long id;

    // 차종 분류ID
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    // 연료 타입
    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false)
    private FuelType fuelType;

    // 연비
    @Column(name = "custom_efficiency", precision = 6, scale = 2, nullable = false)
    private BigDecimal customEfficiency;
    //(NUMERIC) 타입을 안전한 소수계산을 위해 사용함
}
