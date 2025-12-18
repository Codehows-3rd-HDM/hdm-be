package com.hdmbe.company.entity;
import com.hdmbe.commonModule.entity.BaseTimeEntity;
import com.hdmbe.SupplyCustomer.entity.SupplyCustomer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "COMPANY_SUPPLY_CUSTOMER_MAP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanySupplyCustomerMap extends BaseTimeEntity {

    // 매핑 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "map_id", columnDefinition = "BIGINT")
    private Long id;

    // 협력사
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // 공급 고객
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supply_customer_id", nullable = false)
    private SupplyCustomer supplyCustomer;

    // 적용 종료일 (NULL = 현재 적용 중)
    @Column(name = "end_date")
    private LocalDate endDate;
}
