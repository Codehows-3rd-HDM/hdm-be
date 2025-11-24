package com.hdmbe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "OPERATION_PURPOSE")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationPurpose extends BaseCreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 목적 ID
    @Column(name = "purpose_id")
    private Long id;

    // 목적 이름
    @Column(name = "purpose_name", length = 20, unique = true)
    private String purposeName;

    // 기준 Scope
    @Column(name = "default_scope")
    private Integer defaultScope;
}