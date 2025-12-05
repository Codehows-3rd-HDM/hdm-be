package com.hdmbe.productClass.entity;

import com.hdmbe.company.entity.Company;
import com.hdmbe.commonModule.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PRODUCT_CLASS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductClass extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 분류 ID
    @Column(name = "class_id", columnDefinition = "BIGINT")
    private Long id;

    // 분류 이름
    @Column(name = "class_name", length = 20, nullable = false)
    private String className;

    // 비고
    @Column(name = "remark", nullable = false, unique = true)
    private String remark;

    @OneToMany(mappedBy = "productClass", cascade = CascadeType.ALL)
    private List<Company> companies = new ArrayList<>();
}