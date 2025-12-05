package com.hdmbe.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint
{
    @Override
    public void commence(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException authException) throws IOException, ServletException
    {
        // 401 에러 코드 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // SC_UNAUTHORIZED ==> 401 에러(인증불가)

        // JSON 형식 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // JSON 모양으로 예쁘게 출력
        PrintWriter out = response.getWriter();
        String jsonResponse = "{\"message\": \"인증에 실패했습니다.\", \"details\": \"" + authException.getMessage() + "\"}";
        out.println(jsonResponse);
    }
}
