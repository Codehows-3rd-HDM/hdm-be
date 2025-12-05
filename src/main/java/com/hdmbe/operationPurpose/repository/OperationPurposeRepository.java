package com.hdmbe.operationPurpose.repository;

import com.hdmbe.operationPurpose.entity.OperationPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationPurposeRepository extends JpaRepository<OperationPurpose, Long> {

    List<OperationPurpose> findByPurposeNameContaining(String purposeName);

    List<OperationPurpose> findByDefaultScope(Integer defaultScope);

}
