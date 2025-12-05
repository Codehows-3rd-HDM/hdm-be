package com.hdmbe.config;

import com.hdmbe.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter
{
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;  // DB 조회용

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        // 필터 ==> 요청, 응답을 중간에서 가로챈 다음 ==> 필요한 동작을 수행
        // 1. 요청 헤더 (Authorization)에서 JWT 토큰을 꺼냄
        // String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = jwtService.resolveToken(request);

        // 토큰이 있고, 유효하다면? (validationToken 활용)
        if (token != null && jwtService.validateToken(token))
        {
            // 2. 꺼낸 토큰에서 유저 정보 추출
            String username = jwtService.getUserName(token);

            // 3. 추출된 유저 정보로 Authentication 을 만들어서 SecurityContext에 set
            if (username != null)
            {
                // DB에서 최신 회원 정보(권한 포함) 조회
                // (UserDetailsServiceImpl의 loadUserByUsername이 실행됨)
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 인증 객체 생성
                // (userDetails.getAuthorities() 안에 DB에서 가져온 권한이 이미 들어있음!)
                Authentication authToken =
                        new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());

                // 5. 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("인증 완료: {}, 권한: {}", username, userDetails.getAuthorities());
            }
        }
        // 마지막에 다음 필터를 호출
        filterChain.doFilter(request, response);
    }
}
