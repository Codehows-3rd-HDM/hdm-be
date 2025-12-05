package com.hdmbe.carModel.entity;

import com.hdmbe.carCategory.entity.CarCategory;
import com.hdmbe.constant.FuelType;
import com.hdmbe.commonModule.entity.BaseTimeEntity;
import com.hdmbe.vehicle.entity.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CAR_MODEL")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarModel extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 차종 ID
    @Column(name = "car_model_id", columnDefinition = "BIGINT")
    private Long id;

    // 차종 분류ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CarCategory carCategory;

    // 연료 타입
    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false)
    private FuelType fuelType;

    // 연비
    @Column(name = "custom_efficiency", precision = 6, scale = 2, nullable = false)
    private BigDecimal customEfficiency;
    //(NUMERIC) 타입을 안전한 소수계산을 위해 사용함

    @OneToMany(mappedBy = "carModel", cascade = CascadeType.ALL)
    private List<Vehicle> vehicles = new ArrayList<>();
}
