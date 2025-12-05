package com.hdmbe.process.entity;

import com.hdmbe.company.entity.Company;
import com.hdmbe.commonModule.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PROCESS")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 공정 ID
    @Column(name = "process_id", columnDefinition = "BIGINT")
    private Long id;

    // 공정 이름
    @Column(name = "process_name", length = 30, unique = true)
    private String processName;

    // 업체명
    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL)
    private List<Company> companies = new ArrayList<>();
}