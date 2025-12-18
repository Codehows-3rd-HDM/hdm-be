package com.hdmbe.auth.service;

import com.hdmbe.auth.repository.UserAccountRepository;
import com.hdmbe.auth.entity.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException
    {
        // 실제 해당 username(ID)을 가지는 유저가 DB에 존재하는지 확인
        // + 해당 유저정보를 UserDetails 타입으로 반환하는 메서드

        UserAccount user = userAccountRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디입니다."));

        // 대소문자 검사
        if (!user.getUserName().equals(userName))
        {
            throw new UsernameNotFoundException("아이디가 일치하지 않습니다. (대소문자 확인)");
        }

        // 3. UserDetails 변환해서 리턴 (비번 검사는 시큐리티가 알아서 함)
        return User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
