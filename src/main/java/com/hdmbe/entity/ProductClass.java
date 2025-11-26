package com.hdmbe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PRODUCT_CLASS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductClass extends BaseCreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 분류 ID
    @Column(name = "class_id")
    private Long id;

    // 분류 이름
    @Column(name = "class_name", length = 20, nullable = false)
    private String className;

    // 비고
    @Column(name = "remark", nullable = false)
    private String remark;
}