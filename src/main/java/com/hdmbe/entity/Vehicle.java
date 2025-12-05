
    // 운전자 사번
    @Column(name = "driver_member_id", length = 10)
    private String driverMemberId;

    // 소속 업체 ID
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    // 운행목적 ID
    @Column(name = "purpose_id", nullable = false)
    private Long purposeId;

    // 운행거리
    @Column(name = "operation_distance", precision = 10, scale = 2, nullable = false)
    private BigDecimal operationDistance;
//(NUMERIC) 타입을 안전한 소수계산을 위해 사용함

    // 비고
    @Column(name = "remark", nullable = false)
    private String remark;
}