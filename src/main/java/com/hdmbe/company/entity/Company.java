package com.hdmbe.company.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.hdmbe.SupplyCustomer.entity.SupplyCustomer;
import com.hdmbe.commonModule.entity.BaseTimeEntity;
import com.hdmbe.supplyType.entity.SupplyType;
import com.hdmbe.vehicle.entity.Vehicle;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "COMPANY")
@Getter
@Setter
@NoArgsConstructor
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

    // 편도거리
    @Column(name = "one_way_distance", precision = 10, scale = 2)
    private BigDecimal oneWayDistance;

    // 주소
    @Column(name = "address", nullable = false)
    private String address;

    // 비고
    @Column(name = "remark")
    private String remark;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Vehicle> vehicles = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CompanySupplyTypeMap> supplyTypeMaps = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CompanySupplyCustomerMap> supplyCustomerMaps = new ArrayList<>();

    // 현재 유효한 공급유형 조회
    public SupplyType getCurrentSupplyType() {
        return supplyTypeMaps.stream()
                .filter(map -> map.getEndDate() == null || map.getEndDate().isAfter(java.time.LocalDate.now()))
                .findFirst()
                .map(CompanySupplyTypeMap::getSupplyType)
                .orElse(null);
    }

    // 현재 유효한 공급고객 조회
    public SupplyCustomer getCurrentSupplyCustomer() {
        return supplyCustomerMaps.stream()
                .filter(map -> map.getEndDate() == null || map.getEndDate().isAfter(java.time.LocalDate.now()))
                .findFirst()
                .map(CompanySupplyCustomerMap::getSupplyCustomer)
                .orElse(null);
    }
}
