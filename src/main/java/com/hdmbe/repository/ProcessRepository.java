package com.hdmbe.repository;

import com.hdmbe.entity.ProcessEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessRepository extends JpaRepository<ProcessEntity, Long> {
}