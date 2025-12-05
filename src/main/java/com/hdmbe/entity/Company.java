package com.hdmbe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "COMPANY")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends BaseCreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 업체 ID
    @Column(name = "company_id")
    private Long id;

    // 업체명
    @Column(name = "company_name", length = 50, nullable = false, unique = true)
    private String companyName;

    // 편도거리
    @Column(name = "one_way_distance", precision = 10, scale = 2)
    private BigDecimal oneWayDistance;

    // 주소
    @Column(name = "address", nullable = false)
    private String address;

    // 공정 ID
    @Column(name = "process_id", nullable = false)
    private Long processId;

    // 제품 분류 ID
    @Column(name = "class_id", nullable = false)
    private Long classId;
}