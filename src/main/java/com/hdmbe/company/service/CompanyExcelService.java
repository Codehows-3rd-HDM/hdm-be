package com.hdmbe.company.service;

import com.hdmbe.SupplyCustomer.entity.SupplyCustomer;
import com.hdmbe.SupplyCustomer.repository.SupplyCustomerRepository;
import com.hdmbe.company.entity.Company;
import com.hdmbe.company.entity.CompanySupplyCustomerMap;
import com.hdmbe.company.entity.CompanySupplyTypeMap;
import com.hdmbe.company.repository.CompanyRepository;
import com.hdmbe.company.repository.CompanySupplyCustomerMapRepository;
import com.hdmbe.company.repository.CompanySupplyTypeMapRepository;
import com.hdmbe.supplyType.entity.SupplyType;
import com.hdmbe.supplyType.repository.SupplyTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CompanyExcelService {

    private final CompanyRepository companyRepository;
    private final CompanySupplyTypeMapRepository supplyTypeMapRepository;
    private final CompanySupplyCustomerMapRepository supplyCustomerMapRepository;

    @Transactional
    public Company getOrCreate(String companyName,
                               String address,
                               BigDecimal distance,
                               String remark,
                               SupplyType newType,
                               SupplyCustomer newCustomer)
    {
        // 1. [Company] 기본 정보 처리 (기존과 동일하지만, 매핑 정보는 여기서 처리 안 함)
        Company savedCompany = companyRepository.findByCompanyName(companyName)
                .map(existing -> {
                    // [이미 있는 경우] -> 업데이트
                    existing.setAddress(address);
                    existing.setOneWayDistance(distance);
                    existing.setRemark(remark);
                    return existing;
                })
                .orElseGet(() ->
                        // [없는 경우] -> 신규 저장
                        companyRepository.save(
                                Company.builder()
                                        .companyName(companyName)
                                        .address(address)
                                        .oneWayDistance(distance) // 거리 저장
                                        .remark(remark != null ? remark : "") // 비고 없으면 공백
                                        .build()
                        )
                );
        // 2. [SupplyType] 이력 관리 로직 적용
        updateSupplyTypeHistory(savedCompany, newType);

        // 3. [SupplyCustomer] 이력 관리 로직 적용
        updateSupplyCustomerHistory(savedCompany, newCustomer);

        return savedCompany;
    }

    // --- [핵심] 공급유형 이력 관리 메소드 ---
    private void updateSupplyTypeHistory(Company company, SupplyType newType) {
        // [추가] 엑셀이 빈칸이라 null이 넘어왔으면? -> 매핑 정보 업데이트 안 하고 종료
        if (newType == null) {
            return;
        }

        // 1. 현재 활성화된(endDate가 null인) 매핑 정보를 찾음
        supplyTypeMapRepository.findFirstByCompanyAndEndDateIsNull(company)
                .ifPresentOrElse(
                        currentMap -> {
                            // [기존 매핑 존재]
                            // 만약 엑셀에 들어온 값(newType)과 현재 DB값(currentMap)이 다르다면?
                            // 여기서 newType.getId() 할 때 newType이 null이면 에러나는데, 위에서 막아줌!
                            if (!currentMap.getSupplyType().getId().equals(newType.getId())) {

                                // A. 기존 이력을 오늘 날짜로 종료시킴 (이력 박제)
                                currentMap.setEndDate(LocalDate.now().minusDays(1)); // 어제부로 종료 처리 (취향 차이)

                                // B. 새로운 이력을 생성 (오늘부터 시작)
                                CompanySupplyTypeMap newMap = CompanySupplyTypeMap.builder()
                                        .company(company)
                                        .supplyType(newType)
                                        .endDate(null) // 현재 적용 중
                                        .build();
                                supplyTypeMapRepository.save(newMap);
                            }
                            // 값이 같으면? 아무것도 안 함 (현행 유지)
                        },
                        () -> {
                            // [기존 매핑 없음 - 신규 업체인 경우]
                            CompanySupplyTypeMap newMap = CompanySupplyTypeMap.builder()
                                    .company(company)
                                    .supplyType(newType)
                                    .endDate(null)
                                    .build();
                            supplyTypeMapRepository.save(newMap);
                        }
                );
    }

    // --- [핵심] 공급고객 이력 관리 메소드 ---
    private void updateSupplyCustomerHistory(Company company, SupplyCustomer newCustomer) {
        // [추가] null 체크
        if (newCustomer == null) {
            return;
        }
        supplyCustomerMapRepository.findFirstByCompanyAndEndDateIsNull(company)
                .ifPresentOrElse(
                        currentMap -> {
                            // 값이 바뀌었는지 체크
                            if (!currentMap.getSupplyCustomer().getId().equals(newCustomer.getId())) {

                                // A. 기존 이력 종료
                                currentMap.setEndDate(LocalDate.now().minusDays(1));

                                // B. 새 이력 시작
                                CompanySupplyCustomerMap newMap = CompanySupplyCustomerMap.builder()
                                        .company(company)
                                        .supplyCustomer(newCustomer)
                                        .endDate(null)
                                        .build();
                                supplyCustomerMapRepository.save(newMap);
                            }
                        },
                        () -> {
                            // 신규 생성
                            CompanySupplyCustomerMap newMap = CompanySupplyCustomerMap.builder()
                                    .company(company)
                                    .supplyCustomer(newCustomer)
                                    .endDate(null)
                                    .build();
                            supplyCustomerMapRepository.save(newMap);
                        }
                );
    }
}
