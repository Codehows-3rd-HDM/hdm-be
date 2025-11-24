package com.hdmbe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime createdAt;  // 생성시각

    @LastModifiedDate
    @Column(name = "updated_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime updatedAt; // 수정시각
}
