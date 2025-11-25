package com.hdmbe.repository;

import com.hdmbe.entity.OperationPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationPurposeRepository extends JpaRepository<OperationPurpose, Long> {

    boolean existsByPurposeName(String purposeName);
}
