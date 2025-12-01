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

    public UserAccount login(LoginDto loginDto)
    {
        UserAccount userAccount = userAccountRepository.findByUserName(loginDto.getUserName())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        if (userAccount.getPassword().equals(loginDto.getPassword()))
        {
            userAccount.setLastLogin(LocalDateTime.now());

            return userAccount;
        }
        else
        {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

    }
}
