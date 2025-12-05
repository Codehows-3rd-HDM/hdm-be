package com.hdmbe.process.repository;

import com.hdmbe.operationPurpose.entity.OperationPurpose;
import com.hdmbe.process.entity.ProcessEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessRepository extends JpaRepository<ProcessEntity, Long> {

    List<ProcessEntity> findByProcessNameContaining(String processName);

}