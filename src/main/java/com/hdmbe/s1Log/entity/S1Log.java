package com.hdmbe.s1Log.entity;

import com.hdmbe.commonModule.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "S1_EMPLOYEE_LOG")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class S1Log extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 로그 ID
    @Column(name = "log_id", columnDefinition = "BIGINT")
    private Long id;

    // 사원번호
    @Column(name = "member_id", length = 10, nullable = false)
    private String memberId;

    // 직원명
    @Column(name = "employee_name", length = 10, nullable = false)
    private String employeeName;

    // 출입일시
    @Column(name = "access_time", nullable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime accessTime;
}