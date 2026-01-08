package com.hdmbe.excelUpNiceS1.entity;

import com.hdmbe.vehicle.entity.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "S1_EMPLOYEE_LOG")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class S1Log {
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

    // 출근 날짜
    @Column(name = "access_date", nullable = false, columnDefinition = "DATE")
    private LocalDate accessDate;

}