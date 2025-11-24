package com.hdmbe.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "category_id")
    private Long id;

    // 차량 분류 이름
    @Column(name = "category_name", length = 20, nullable = false)
    private String categoryName;

    // 상위 카테고리 ID
    @Column(name = "parent_id")
    private Long parentId;
}