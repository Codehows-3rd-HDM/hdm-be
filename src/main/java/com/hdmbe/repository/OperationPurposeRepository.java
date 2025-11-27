package com.hdmbe.repository;

import com.hdmbe.entity.OperationPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationPurposeRepository extends JpaRepository<OperationPurpose, Long> {

    boolean existsByPurposeName(String purposeName);

    // 목적명 검색
    List<OperationPurpose> findByPurposeNameContaining(String purposeName);

    // 스코프 검색
    List<OperationPurpose> findByDefaultScope(Integer defaultScope);
}

