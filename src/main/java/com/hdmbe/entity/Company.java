package com.hdmbe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "COMPANY")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 업체 ID
    @Column(name = "company_id", columnDefinition = "BIGINT")
    private Long id;

    // 업체명
    @Column(name = "company_name", length = 50, nullable = false, unique = true)
    private String companyName;

    // 업체명
    @Column(name = "one_way_distance", precision = 10, scale = 2)
    private BigDecimal oneWayDistance;

    // 주소
    @Column(name = "address", nullable = false)
    private String address;

    // 공정 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", nullable = false)
    private ProcessEntity process;

    // 제품 분류 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ProductClass productClass;

    // 비고
    @Column(name = "remark", nullable = false)
    private String remark;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Vehicle> vehicles = new ArrayList<>();
}