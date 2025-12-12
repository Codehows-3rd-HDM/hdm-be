package com.hdmbe.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hdmbe.SupplyCustomer.service.SupplyCustomerService;
import com.hdmbe.carCategory.service.CarCategoryService;
import com.hdmbe.company.service.CompanyService;
import com.hdmbe.operationPurpose.service.OperationPurposeService;
import com.hdmbe.supplyType.service.SupplyTypeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/options")
@RequiredArgsConstructor
public class RegistrationOptionsController {

    private final OperationPurposeService operationPurposeService;
    private final CompanyService companyService;
    private final CarCategoryService carCategoryService;
    private final SupplyTypeService supplyTypeService;
    private final SupplyCustomerService supplyCustomerService;

    /**
     * 기준정보 등록 페이지의 모든 드롭박스 옵션을 조회합니다. 응답: { "PURPOSE_OPTIONS": ["납품", "출퇴근",
     * ...], "COMPANY_OPTIONS": ["Volvo KOREA", ...], "CAT_LARGE_OPTIONS":
     * ["승용차", "상용트럭"], "CAT_SMALL_OPTIONS": ["대형", "중형", ...], "FUEL_OPTIONS":
     * ["가솔린", "디젤", ...], "SUPPLY_CUSTOMER_OPTIONS": ["1000", "2000", ...],
     * "SCOPE_OPTIONS": ["1", "3"], "SUPPLY_TYPE_OPTIONS": ["가공", "단조", ...],
     * "REGION_OPTIONS": ["서울특별시", "부산광역시", ...] }
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRegistrationOptions() {
        Map<String, Object> options = new HashMap<>();

        // fetch car categories once
        List<com.hdmbe.carCategory.dto.CarCategoryResponseDto> carCategories = carCategoryService.getAll();

        // 1. 운행목적 (PURPOSE_OPTIONS)
        options.put("PURPOSE_OPTIONS",
                operationPurposeService.getAll().stream()
                        .map(dto -> dto.getPurposeName())
                        .collect(Collectors.toList())
        );

        // 2. 협력사/업체 (COMPANY_OPTIONS)
        options.put("COMPANY_OPTIONS",
                companyService.getAll().stream()
                        .map(dto -> dto.getCompanyName())
                        .collect(Collectors.toList())
        );

        // 3. 차종 대분류 (CAT_LARGE_OPTIONS) - 부모 카테고리
        List<String> catLarge = carCategories.stream()
                .filter(dto -> dto.getParentId() == null)
                .map(dto -> dto.getCategoryName())
                .distinct()
                .collect(Collectors.toList());
        options.put("CAT_LARGE_OPTIONS", catLarge);

        // 4. 차종 소분류 (CAT_SMALL_OPTIONS) - 모든 자식 카테고리
        List<String> catSmall = carCategories.stream()
                .filter(dto -> dto.getParentId() != null)
                .map(dto -> dto.getCategoryName())
                .distinct()
                .collect(Collectors.toList());
        options.put("CAT_SMALL_OPTIONS", catSmall);

        // Build a parent->children map for frontend convenience
        Map<String, List<String>> carCategoryMap = new HashMap<>();
        // map parent id to name
        Map<Long, String> parentNames = carCategories.stream()
                .filter(dto -> dto.getParentId() == null)
                .collect(Collectors.toMap(dto -> dto.getId(), dto -> dto.getCategoryName()));

        carCategories.stream()
                .filter(dto -> dto.getParentId() != null)
                .forEach(dto -> {
                    Long pid = dto.getParentId();
                    String parentName = parentNames.get(pid);
                    if (parentName == null) {
                        return;
                    }
                    carCategoryMap.computeIfAbsent(parentName, k -> new java.util.ArrayList<>()).add(dto.getCategoryName());
                });

        options.put("CAR_CATEGORY_MAP", carCategoryMap);

        // 5. 연료 종류 (FUEL_OPTIONS) - FuelType Enum의 모든 값
        options.put("FUEL_OPTIONS", List.of(
                "가솔린", "디젤", "LPG", "CNG", "전기", "수소", "중유", "등유", "도시가스"
        ));

        // 6. 공급 고객 (SUPPLY_CUSTOMER_OPTIONS)
        options.put("SUPPLY_CUSTOMER_OPTIONS",
                supplyCustomerService.getAll().stream()
                        .map(dto -> dto.getCustomerName())
                        .collect(Collectors.toList())
        );

        // 7. Scope (SCOPE_OPTIONS)
        options.put("SCOPE_OPTIONS", List.of("1", "3", "4"));

        // 8. 공급 유형 (SUPPLY_TYPE_OPTIONS)
        options.put("SUPPLY_TYPE_OPTIONS",
                supplyTypeService.getAll().stream()
                        .map(dto -> dto.getSupplyTypeName())
                        .collect(Collectors.toList())
        );

        // 9. 지역 (REGION_OPTIONS)
        options.put("REGION_OPTIONS", List.of(
                "강원특별자치도", "경기도", "경상남도", "경상북도", "광주광역시", "대구광역시",
                "대전광역시", "부산광역시", "서울특별시", "세종특별자치시", "울산광역시",
                "인천광역시", "전라남도", "전북특별자치도", "제주특별자치도", "충청남도", "충청북도"
        ));

        return ResponseEntity.ok(options);
    }
}
