package com.hdmbe.auth.entity;

import com.hdmbe.auth.dto.UserAccountDto;
import com.hdmbe.commonModule.constant.Role;
import com.hdmbe.commonModule.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "USER_ACCOUNT")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount extends BaseTimeEntity {
    @Id
    @Column(name = "user_id", columnDefinition = "BIGINT")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "last_login", columnDefinition = "DATETIME(0)")
    private LocalDateTime lastLogin;

    public static UserAccount createUserAccount(UserAccountDto dto, PasswordEncoder passwordEncoder)
    {
        return  UserAccount
                .builder()
                .userName(dto.getUserName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .build();
    }
}
