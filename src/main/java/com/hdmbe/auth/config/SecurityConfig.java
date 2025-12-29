package com.hdmbe.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
// @EnableMethodSecurity(prePostEnabled = true)   //메서드 보안 활성화 (컨트롤러에 붙은 보안 딱지를 인식해라)
public class SecurityConfig {
    private final AuthEntryPoint authEntryPoint;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain FilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CORS 설정 적용 (프론트엔드 연동 위해 필수)
                //.cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. CSRF 비활성화 (JWT 쓸 땐 필요 없음)
                .csrf(csrf -> csrf.disable())

                // 3. 세션 미사용 (Stateless 설정)
                .sessionManagement
                        ((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. URL별 접근 권한 관리
                .authorizeHttpRequests(auth -> auth
                        // [SUPERADMIN]
                        .requestMatchers("/superadmin/**")
//                        .hasAnyRole("SUPERADMIN")
                        .permitAll()
                        // [SUPERADMIN, ADMIN]
                        .requestMatchers( "/admin/excel/upload/**").hasAnyRole("SUPERADMIN", "ADMIN")
                        // [ALL]
                        .requestMatchers("/login", "/logout", "/admin/**", "/view/company").permitAll()
                        .anyRequest().authenticated())

                // 5. JWT 필터 끼워넣기 (Username...Filter 앞에 실행)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // 6. 예외 처리 (인증 실패 시 처리)
                .exceptionHandling((ex) -> ex.authenticationEntryPoint(authEntryPoint)
                );
        return http.build();
    }

    // 🔐 비밀번호 암호화 빈
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();   //비밀번호 암호화
    }

    // 🔐 인증 관리자 빈
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception
    {
        return authConfig.getAuthenticationManager();
    }

}
