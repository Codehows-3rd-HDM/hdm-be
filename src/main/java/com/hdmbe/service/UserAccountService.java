package com.hdmbe.service;

import com.hdmbe.dto.LoginDto;
import com.hdmbe.dto.UserAccountDto;
import com.hdmbe.entity.UserAccount;
import com.hdmbe.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAccountService
{
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public void saveUserAccount(UserAccountDto userAccountDto)
    {
        UserAccount userAccount = UserAccount.createUserAccount(userAccountDto, passwordEncoder);

        validateDuplicateUserAccount(userAccount);    //validateDuplicateUserAccount 이거를 호출!!!
        userAccountRepository.save(userAccount);
    }

    public void validateDuplicateUserAccount(UserAccount userAccount)
    {
        boolean userNameExists = userAccountRepository.existsByUserName(userAccount.getUserName());
        if (userNameExists)
        {
            throw new IllegalArgumentException("이미 등록된 아이디입니다.");
        }
    }

    @Transactional
    public void updateLastLogin(String userName)
    {
        // 아이디로 회원 찾기
        UserAccount user = userAccountRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        // 2. 시간 갱신
        user.setLastLogin(LocalDateTime.now());
    }
}
