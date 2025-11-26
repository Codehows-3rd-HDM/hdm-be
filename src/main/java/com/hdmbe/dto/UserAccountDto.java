package com.hdmbe.dto;

import com.hdmbe.constant.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccountDto
{
    @NotBlank(message = "USER ID는 필수 입력 값입니다.")
    private String userName;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    @NotNull(message = "권한 선택은 필수 입력 값입니다.")
    private Role role;
}
