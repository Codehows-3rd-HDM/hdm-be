package com.hdmbe.auth.dto;

import com.hdmbe.commonModule.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto
{
    private String token;
    private String userName;
    private Role role;
}
