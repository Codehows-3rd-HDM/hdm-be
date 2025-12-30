package com.hdmbe.vehicle.repository;

import com.hdmbe.company.entity.Company;
import com.hdmbe.vehicle.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // 나이스파크용: 차량번호로 찾기
    // Optional<Vehicle> findByCarNumber(String carNumber); => findByCarNumberAndCalcBaseDate 로 대체
    // [신규/핵심] 엑셀 업로드 시 "차량번호 + 기준일"로 정확히 그 이력을 찾기 위함
    // Entity에서 복합 유니크 키를 걸었으므로, 얘는 무조건 1개 아니면 0개임 (Optional 안전)
    Optional<Vehicle> findByCarNumberAndCalcBaseDate(String carNumber, LocalDate calcBaseDate);

    // 에스원용: 운전자 사번으로 찾기
    // (Vehicle 엔티티에 driverMemberId 필드가 있어야 함)
    // [수정 1] 반환 타입 변경 (Optional -> List)
    // 한 사원이 여러 차량 이력(과거, 현재)을 가질 수 있으므로 List 여야 함
    List<Vehicle> findByDriverMemberId(String driverMemberId);

    List<Vehicle> findByCompany(Company company);

    // 단순 검색용 (여러 개 나와도 됨 -> List 유지)
    List<Vehicle> findByCarNumberContaining(String carNumber);

    List<Vehicle> findByDriverMemberIdContaining(String driverMemberId);

    boolean existsByCompany(Company company);

    @Query("""
        SELECT v
        FROM Vehicle v
        LEFT JOIN VehicleOperationPurposeMap vop
            ON vop.vehicle = v
            AND vop.endDate IS NULL
        LEFT JOIN vop.operationPurpose op
        JOIN v.company c
        JOIN v.carModel cm
        JOIN cm.carCategory cc
        LEFT JOIN cc.parentCategory pcc
        WHERE
            (:carNumber IS NULL OR v.carNumber LIKE %:carNumber%)
        AND (:operationPurposeId IS NULL OR op.id = :operationPurposeId)
        AND (:companyName IS NULL OR c.companyName LIKE %:companyName%)
        AND (:driverMemberId IS NULL OR v.driverMemberId LIKE %:driverMemberId%)
        AND (
            :keyword IS NULL OR
            v.carNumber LIKE %:keyword%
            OR c.companyName LIKE %:keyword%
            OR v.driverMemberId LIKE %:keyword%
            OR cc.categoryName LIKE %:keyword%
            OR pcc.categoryName LIKE %:keyword%
            OR v.carName LIKE %:keyword%
            OR op.purposeName LIKE %:keyword%
        )
    """)
    Page<Vehicle> search(
            @Param("carNumber") String carNumber,
            @Param("operationPurposeId") Long operationPurposeId,
            @Param("companyName") String companyName,
            @Param("driverMemberId") String driverMemberId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 운행목적명 정렬용 쿼리
    @Query("""
        SELECT v
        FROM Vehicle v
        LEFT JOIN VehicleOperationPurposeMap vop
            ON vop.vehicle = v
            AND vop.endDate IS NULL
        LEFT JOIN vop.operationPurpose op
        JOIN v.company c
        JOIN v.carModel cm
        JOIN cm.carCategory cc
        LEFT JOIN cc.parentCategory pcc
        WHERE
            (:carNumber IS NULL OR v.carNumber LIKE %:carNumber%)
        AND (:operationPurposeId IS NULL OR op.id = :operationPurposeId)
        AND (:companyName IS NULL OR c.companyName LIKE %:companyName%)
        AND (:driverMemberId IS NULL OR v.driverMemberId LIKE %:driverMemberId%)
        AND (
            :keyword IS NULL OR
            v.carNumber LIKE %:keyword%
            OR c.companyName LIKE %:keyword%
            OR v.driverMemberId LIKE %:keyword%
            OR cc.categoryName LIKE %:keyword%
            OR pcc.categoryName LIKE %:keyword%
            OR v.carName LIKE %:keyword%
            OR op.purposeName LIKE %:keyword%
        )
    """)
    Page<Vehicle> searchOrderByOperationPurpose(
            @Param("carNumber") String carNumber,
            @Param("operationPurposeId") Long operationPurposeId,
            @Param("companyName") String companyName,
            @Param("driverMemberId") String driverMemberId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // Scope 정렬용 쿼리
    @Query("""
        SELECT v
        FROM Vehicle v
        LEFT JOIN VehicleOperationPurposeMap vop
            ON vop.vehicle = v
            AND vop.endDate IS NULL
        LEFT JOIN vop.operationPurpose op
        JOIN v.company c
        JOIN v.carModel cm
        JOIN cm.carCategory cc
        LEFT JOIN cc.parentCategory pcc
        WHERE
            (:carNumber IS NULL OR v.carNumber LIKE %:carNumber%)
        AND (:operationPurposeId IS NULL OR op.id = :operationPurposeId)
        AND (:companyName IS NULL OR c.companyName LIKE %:companyName%)
        AND (:driverMemberId IS NULL OR v.driverMemberId LIKE %:driverMemberId%)
        AND (
            :keyword IS NULL OR
            v.carNumber LIKE %:keyword%
            OR c.companyName LIKE %:keyword%
            OR v.driverMemberId LIKE %:keyword%
            OR cc.categoryName LIKE %:keyword%
            OR pcc.categoryName LIKE %:keyword%
            OR v.carName LIKE %:keyword%
            OR op.purposeName LIKE %:keyword%
        )
    """)
    Page<Vehicle> searchOrderByScope(
            @Param("carNumber") String carNumber,
            @Param("operationPurposeId") Long operationPurposeId,
            @Param("companyName") String companyName,
            @Param("driverMemberId") String driverMemberId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 사번 목록만 가볍게 조회하는 쿼리 (최적화)
    // 설명: Vehicle 전체를 가져오는 게 아니라 'driverMemberId' 문자열만 가져옴!
    @Query("SELECT v FROM Vehicle v WHERE v.driverMemberId IS NOT NULL")
    List<Vehicle> findAllDriverMemberIds();

    // 차량 번호만 가볍게 조회하는 쿼리 (최적화)
    // 설명: Vehicle 전체를 가져오는 게 아니라 'carNumber' 문자열만 가져옴!
    @Query("SELECT v.carNumber FROM Vehicle v WHERE v.carNumber IS NOT NULL")
    List<String> findAllCarNumbers();

    // VehicleRepository.java
    // [수정 2] 반환 타입 변경 (Optional -> List)
    // 특정 차 번호를 가진 모든 이력(과거~현재)을 다 가져옴
    @Query("SELECT v FROM Vehicle v "
            + "LEFT JOIN FETCH v.company c "
            + // 업체 정보 미리 로딩
            "LEFT JOIN FETCH v.carModel cm "
            + // 차종 정보 미리 로딩
            "LEFT JOIN FETCH cm.carCategory cc "
            + // 카테고리까지 미리 로딩
            "WHERE v.carNumber = :carNumber")
    List<Vehicle> findByCarNumberWithAll(@Param("carNumber") String carNumber);

    // ✅ [추가] 차량번호로 조회하되, 기준일(calcBaseDate)이 가장 '최신'인 것 1개만 가져오기
    // OrderByCalcBaseDateDesc -> 날짜 내림차순(최신순) 정렬
    // Top -> 그 중 맨 위에 거 1개
    Optional<Vehicle> findTopByCarNumberOrderByCalcBaseDateDesc(String carNumber);
}
