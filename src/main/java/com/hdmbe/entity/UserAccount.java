package com.hdmbe.entity;

import com.hdmbe.constant.Role;
import com.hdmbe.dto.UserAccountDto;
import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "user_id")
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

    public static UserAccount createUserAccount(UserAccountDto dto)
    {
        return  UserAccount
                .builder()
                .userName(dto.getUserName())
                .password(dto.getPassword())
                .role(dto.getRole())
                .build();
    }
}
