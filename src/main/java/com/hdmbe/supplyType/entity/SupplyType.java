package com.hdmbe.supplyType.entity;

import com.hdmbe.company.entity.Company;
import com.hdmbe.commonModule.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SUPPLY_TYPE")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyType extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 공정 ID
    @Column(name = "supply_type_id", columnDefinition = "BIGINT")
    private Long id;

    // 공정 이름
    @Column(name = "supply_type_name", length = 30, unique = true)
    private String supplyTypeName;

    // 업체명
    @OneToMany(mappedBy = "supplyType", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Company> companies = new ArrayList<>();
}