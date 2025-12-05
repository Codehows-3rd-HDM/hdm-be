package com.hdmbe.auth.controller;

import com.hdmbe.commonModule.constant.Role;
import com.hdmbe.auth.dto.LoginDto;
import com.hdmbe.auth.dto.LoginResponseDto;
import com.hdmbe.auth.service.JwtService;
import com.hdmbe.auth.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController
{
    private final UserAccountService userAccountService;    // 회원정보 관리자
    private final JwtService jwtService;    // 토큰 발급기
    private final AuthenticationManager authenticationManager;    // 검사관

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, BindingResult bindingResult)
    {
        // 1. 유저의 ID, PW 정보를 기반으로 UsernamePasswordAuthenticationToken 생성
        // 2. 생성된 UsernamePasswordAuthenticationToken 을 authenticationManager 에게 전달
        // 3. authenticationManager 는 궁극적으로 UserDetailsService 의 loadUserByUsername 을 호출
        // 4. 조회된 유저 정보(UserDetail)와 UsernamePasswordAuthenticationToken 을 비교해 인증 처리
        // 5. 최종 반환된 Authentication(인증된 유저 정보) 를 기반으로 JWT TOKEN 발급
        // 6. 컨트롤러는 응답 헤더(Authorization) 에 Bearer <JWT TOKEN VALUE> 형태로 응답

        // [400 Bad Request] 유효성 검사 실패 (아이디/비번 공백 등)
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        try {

        // (1) 스프링 시큐리티를 통한 인증 (ID/PW 검사)
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginDto.getUserName(), loginDto.getPassword());

        // (2) 여기서 DB 조회 및 비번 검사가 자동으로 일어남
        Authentication authentication = authenticationManager.authenticate(token);

        // (3) lastlogin(마지막 로그인 시간) 요청
        String username = authentication.getName();
        userAccountService.updateLastLogin(username);

        // ✅ 인증된 사용자의 권한을 확인합니다.
//        boolean isAdmin = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        // ✅ 1. [수정됨] 권한 꺼내기 (없으면 에러)
        String roleStr = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("권한이 없는 계정입니다. 관리자에게 문의하세요."));   // 없으면 예외 던짐

        // "ROLE_" 접두사 떼기
        String finalRoleName = roleStr.startsWith("ROLE_") ? roleStr.substring(5) : roleStr;

        // Enum 변환
        Role roleEnum = Role.valueOf(finalRoleName);

        // ✅ 2. JWT 토큰을 발급합니다.
        String jwtToken = jwtService.generateToken(authentication.getName());

        // ✅ 3. 응답에 포함할 DTO를 생성합니다.
        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .token(jwtToken)
                .userName(authentication.getName())
                .role(roleEnum)
                .build();

        return ResponseEntity.ok(loginResponseDto);

        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            // [401 Unauthorized] 아이디가 없거나 비밀번호가 틀림
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 일치하지 않습니다.");

        } catch (LockedException | DisabledException | AccountExpiredException e) {
            // [403 Forbidden] 계정이 잠겼거나 비활성화됨 (관리자 승인 대기 등)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("계정이 잠겨있거나 사용할 수 없는 상태입니다.");

        } catch (IllegalStateException e) {
            // [409 Conflict] 권한 데이터 누락 등 논리적 충돌
            return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 처리 중 데이터 충돌이 발생했습니다: " + e.getMessage());

        } catch (Exception e) {
            // [500 Internal Server Error] 알 수 없는 서버 에러
            e.printStackTrace(); // 서버 로그에 에러 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다. 관리자에게 문의하세요.");
        }

//        try {
//
//            // 1. 아이디/비번 검증 (Service)
//            UserAccount loginUser = userAccountService.login(loginDto);
//
//            // 2. JWT 발급
//            // JwtService 는 String을 받으므로 Enum(.name())으로 변환해서 전달
//            String jwtToken = jwtService.generateToken(loginUser.getUserName(), loginUser.getRole().name());
//
//            // 3. 응답 Dto 생성
//            LoginResponseDto loginResponseDto = LoginResponseDto.builder()
//                                                                .token(jwtToken)
//                                                                .userName(loginUser.getUserName())
//                                                                .role(loginUser.getRole())
//                                                                .build();
//
//            // 4. 전송
//            return ResponseEntity.ok(loginResponseDto);
//        }
//        catch (IllegalArgumentException | IllegalStateException e)
//        {
//            // 로그인 실패 시 401 에러 반환
//          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout()
    {
        // ✅ JWT는 서버에 저장되는 게 없어서(Stateless), 서버에서 삭제할 세션도 없습니다.
        // 로그아웃은 프론트엔드에서 "토큰을 버리는 행위"로 처리됩니다.
        // 여기서는 "성공" 응답만 주면 됩니다.
        return ResponseEntity.ok("로그아웃 되었습니다. (클라이언트에서 토큰을 삭제하세요)");
    }
}
