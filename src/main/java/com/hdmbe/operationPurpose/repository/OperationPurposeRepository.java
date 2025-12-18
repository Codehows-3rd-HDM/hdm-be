package com.hdmbe.operationPurpose.repository;

import com.hdmbe.operationPurpose.entity.OperationPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OperationPurposeRepository extends JpaRepository<OperationPurpose, Long> {

    List<OperationPurpose> findByPurposeNameContaining(String purposeName);

    List<OperationPurpose> findByDefaultScope(Integer defaultScope);

    Optional<OperationPurpose> findByPurposeName(String purposeName);
}
