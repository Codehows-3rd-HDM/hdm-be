package com.hdmbe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PROCESS")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessEntity extends BaseCreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 공정 ID
    @Column(name = "process_id")
    private Long id;

    // 공정 이름
    @Column(name = "process_name", length = 30, unique = true)
    private String processName;

    // 비고
    @Column(name = "remark", nullable = false)
    private String remark;
}