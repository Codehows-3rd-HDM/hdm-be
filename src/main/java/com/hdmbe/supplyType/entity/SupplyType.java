package com.hdmbe.supplyType.entity;

import com.hdmbe.commonModule.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SUPPLY_TYPE")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyType extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 공급 유형 ID
    @Column(name = "supply_type_id", columnDefinition = "BIGINT")
    private Long id;

    // 공급 유형 이름
    @Column(name = "supply_type_name", length = 30, unique = true)
    private String supplyTypeName;

}