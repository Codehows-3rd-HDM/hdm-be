package com.hdmbe.operationPurpose.repository;

import com.hdmbe.operationPurpose.entity.OperationPurpose;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OperationPurposeRepository extends JpaRepository<OperationPurpose, Long> {

    List<OperationPurpose> findByPurposeNameContaining(String purposeName);

    List<OperationPurpose> findByDefaultScope(Integer defaultScope);

    @Query("""
        SELECT op
        FROM OperationPurpose op
        WHERE
            (:purposeName IS NULL OR op.purposeName LIKE %:purposeName%)
        AND (:scope IS NULL OR op.defaultScope = :scope)
        AND (
            :keyword IS NULL OR
            op.purposeName LIKE %:keyword%
            OR CAST(op.defaultScope AS string) LIKE %:keyword%
        )
    """)
    Page<OperationPurpose> search(
            @Param("purposeName") String purposeName,
            @Param("scope") Integer scope,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    Optional<OperationPurpose> findByPurposeNameAndDefaultScope(String purposeName, Integer defaultScope);
}
