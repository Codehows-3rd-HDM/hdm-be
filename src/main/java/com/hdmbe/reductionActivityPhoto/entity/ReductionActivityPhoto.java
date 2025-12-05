package com.hdmbe.reductionActivityPhoto.entity;

import com.hdmbe.commonModule.entity.BaseTimeEntity;
import com.hdmbe.reductionActivity.entity.ReductionActivity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "REDUCTION_ACTIVITY_PHOTO")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReductionActivityPhoto extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // 사진 ID
    @Column(name = "photo_id", columnDefinition = "BIGINT")
    private Long id;

    // 활동 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private ReductionActivity reductionActivity;

    // 사진 파일 경로
    @Column(name = "photo_url", nullable = false)
    private String photoUrl;

    // 업로드일시
    @Column(name = "uploaded_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime uploadedAt;
}