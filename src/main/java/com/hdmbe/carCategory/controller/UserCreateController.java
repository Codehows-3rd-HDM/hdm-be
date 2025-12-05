package com.hdmbe.controller;

import com.hdmbe.dto.UserAccountDto;
import com.hdmbe.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserCreateController {
    private final UserAccountService userAccountService;

    @PostMapping("/superadmin/create")
    public ResponseEntity<?> userCreate(@Valid @RequestBody UserAccountDto userAccountDto)
    {
        try {
            userAccountService.saveUserAccount(userAccountDto);
            return ResponseEntity.ok("계정생성 완료");
        }
        catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
