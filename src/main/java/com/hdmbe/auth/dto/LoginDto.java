package com.hdmbe.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto
{
    @NotBlank(message = "아이디를 입력하세요.")
    private String userName;

    @NotBlank(message = "비밀번호를 입력하세요.")
    private String password;
}
