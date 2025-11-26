package com.hdmbe.service;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class JwtService
{
    // 서버와 클라이언트가 주고 받는 토근 ==> HTTP Header 내 Authorization 헤더값에 저장
    // 예) Authorization Bearer <토큰값>
    static final String PREFIX = "Bearer ";
    static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24L;    //86,400,000 시간 => 하루

    // ✅ [수정 1] 랜덤 키(Keys.secretKeyFor) 대신, 고정된 '비밀번호'를 씁니다.
    // (서버 껐다 켜도 로그인이 유지되게 하기 위함)
    // 32글자 이상 아무거나 길게 적으시면 됩니다.
    //static final Key SIGNING_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    static final String SECRET_STRING = "hdm_project_secret_key_must_be_very_long_and_secure";
    static final Key SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    // loginId(ID)를 받아서 JWT 생성
    public String generateToken(String userName, String role)
    {
        return Jwts.builder()
                .setSubject(userName)
                .claim("role", role)    //권한 정보 추가
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token)
    {
        try
        {
            // 토큰을 파싱해서 서명이 위조되거나 만료되지 않았는지 확인
            // 문제가 있으면 알아서 예외(Exception)가 터짐 -> catch로 이동
            Jwts.parserBuilder()
                    .setSigningKey(SIGNING_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true; // 아무 에러 없으면 유효함!
        }
        catch (Exception e) {
            return false; // 에러 나면 유효하지 않음!
        }
    }

    // JWT를 받아서 loginId(ID)를 반환
    public String parseToken(HttpServletRequest request) {
        String token = resolveToken(request); // 헤더에서 토큰 꺼내기 (아래 메서드 활용)

        if (token != null) {
            try {
                return Jwts.parserBuilder()
                        .setSigningKey(SIGNING_KEY)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject(); // ID 반환
            } catch (Exception e) {
                // 토큰이 위조되었거나 만료되면 null 반환
                return null;
            }
        }
        return null;
    }

    // ✅ [추가] 토큰에서 Role(권한)을 꺼내는 기능 (나중에 관리자 페이지 접근 막을 때 씀)
    public String parseRole(HttpServletRequest request) {
        String token = resolveToken(request);

        if (token != null) {
            try {
                return Jwts.parserBuilder()
                        .setSigningKey(SIGNING_KEY)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .get("role", String.class); // Role 반환
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    // (내부 사용용) 헤더에서 Bearer 떼고 순수 토큰만 가져오는 메서드
    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(PREFIX)) {
            return header.replace(PREFIX, "");
        }
        return null;
    }

//    결론: JwtService나 Filter는 고칠 필요 없나요?
//    네, JwtService와 JwtFilter는 고치지 말고 String 그대로 두는 게 좋습니다.
//
//    JWT 토큰은 본질적으로 텍스트 덩어리라서 내부에는 문자열("ADMIN")로 저장되는 게 맞습니다.
//
//    필터에서도 스프링 시큐리티가 권한을 처리할 때 문자열("ROLE_ADMIN")을 주로 사용합니다.
//
//        [최종 정리]
//
//    Entity & DTO: Role (Enum) 사용 ➡ 안전성 확보
//
//    JWT & DB: String 사용 ➡ 호환성 확보
//
//    변환: Controller에서 user.getRole().name()으로 딱 한 번만 변환



    // JWT를 받아서 loginID(ID)를 반환
//    public String parseToken(HttpServletRequest request)
//    {
        // 요청 헤더에서 Authorization 헤더값을 가져옴
        // 예) header = Bearer <토큰값>
//        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (header != null && header.startsWith(PREFIX))
//        {
//            JwtParser parser = Jwts.parserBuilder()
//                                   .setSigningKey(SIGNING_KEY)
//                                   .build();
//
//            String userName = parser.parseClaimsJws(header.replace(PREFIX, ""))
//                                    .getBody()
//                                    .getSubject();
//            if (userName != null)
//            {
//                return userName;
//            }
//        }
//        return null;
//    }
}
