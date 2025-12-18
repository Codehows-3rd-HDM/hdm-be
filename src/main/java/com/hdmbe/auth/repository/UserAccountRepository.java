package com.hdmbe.auth.repository;

import com.hdmbe.auth.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long>
{
    boolean existsByUserName(String userName);

    Optional<UserAccount> findByUserName(String userName);
}
