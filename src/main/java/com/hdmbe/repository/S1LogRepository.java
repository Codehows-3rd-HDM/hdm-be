package com.hdmbe.repository;

import com.hdmbe.entity.S1Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface S1LogRepository extends JpaRepository<S1Log, Long>
{
    // 삭제 메서드 필요
}
