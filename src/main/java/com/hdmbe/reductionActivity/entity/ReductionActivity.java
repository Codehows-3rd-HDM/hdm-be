package com.hdmbe.reductionActivity.entity;

import com.hdmbe.commonModule.entity.BaseTimeEntity;
import com.hdmbe.reductionActivityPhoto.entity.ReductionActivityPhoto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "REDUCTION_ACTIVITY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReductionActivity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 활동 ID
    @Column(name = "activity_id", columnDefinition = "BIGINT")
    private Long id;

    // 시작일
    @Column(name = "activity_period_start", nullable = false)
    private LocalDate periodStart;

    // 종료일
    @Column(name = "activity_period_end", nullable = false)
    private LocalDate periodEnd;

    // 활동명
    @Column(name = "activity_name", length = 255, nullable = false)
    private String activityName;

    // 활동내용
    @Column(name = "activity_details", nullable = false)
    private String activityDetails;

    // 비용
    @Column(name = "cost_amount", precision = 15, scale = 2)
    private BigDecimal costAmount;

    // 기대효과
    @Column(name = "expected_effect")
    private String expectedEffect;

    @OneToMany(mappedBy = "reductionActivity", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ReductionActivityPhoto> photos = new ArrayList<>();
}