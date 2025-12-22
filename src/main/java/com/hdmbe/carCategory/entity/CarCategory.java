package com.hdmbe.carCategory.entity;

import com.hdmbe.commonModule.entity.BaseTimeEntity;
import com.hdmbe.carModel.entity.CarModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CAR_CATEGORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 분류 ID
    @Column(name = "category_id", columnDefinition = "BIGINT")
    private Long id;

    // 차량 분류 이름
    @Column(name = "category_name", length = 20, nullable = false)
    private String categoryName;

    // 상위 카테고리 ID
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private CarCategory parentCategory;
    // 차종
    @OneToMany(mappedBy = "carCategory", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CarModel> carModels = new ArrayList<>();
}
