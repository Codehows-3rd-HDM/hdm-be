package com.hdmbe.SupplyCustomer.entity;

import com.hdmbe.company.entity.Company;
import com.hdmbe.commonModule.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Supply_Customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyCustomer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 분류 ID
    @Column(name = "customer_id", columnDefinition = "BIGINT")
    private Long id;

    // 분류 이름
    @Column(name = "Customer_name", length = 20, nullable = false)
    private String customerName;

    // 비고
    @Column(name = "remark")
    private String remark;

}