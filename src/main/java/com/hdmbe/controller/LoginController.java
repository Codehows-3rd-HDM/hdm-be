package com.hdmbe.controller;

import com.hdmbe.dto.LoginDto;
import com.hdmbe.dto.LoginResponseDto;
import com.hdmbe.entity.UserAccount;
import com.hdmbe.service.JwtService;
import com.hdmbe.service.UserAccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController
{
    private final UserAccountService userAccountService;
    private final JwtService jwtService;

    @PostMapping("/user/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto)
    {
        try {

            // 1. 아이디/비번 검증 (Service)
            UserAccount loginUser = userAccountService.login(loginDto);

            // 2. JWT 발급
            // JwtService 는 String을 받으므로 Enum(.name())으로 변환해서 전달
            String jwtToken = jwtService.generateToken(loginUser.getUserName(), loginUser.getRole().name());

            // 3. 응답 Dto 생성
            LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                                                                .token(jwtToken)
                                                                .userName(loginUser.getUserName())
                                                                .role(loginUser.getRole())
                                                                .build();

            // 4. 전송
            return ResponseEntity.ok(loginResponseDto);
        }
        catch (IllegalArgumentException | IllegalStateException e)
        {
            // 로그인 실패 시 401 에러 반환
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/user/logout")
    public ResponseEntity<?> logout()
    {
        // ✅ JWT는 서버에 저장되는 게 없어서(Stateless), 서버에서 삭제할 세션도 없습니다.
        // 로그아웃은 프론트엔드에서 "토큰을 버리는 행위"로 처리됩니다.
        // 여기서는 "성공" 응답만 주면 됩니다.
        return ResponseEntity.ok("로그아웃 되었습니다. (클라이언트에서 토큰을 삭제하세요)");
    }
}
