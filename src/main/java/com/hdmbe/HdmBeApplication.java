package com.hdmbe;

import com.hdmbe.auth.dto.UserAccountDto;
import com.hdmbe.auth.service.UserAccountService;
import com.hdmbe.commonModule.constant.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HdmBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HdmBeApplication.class, args);

    }

    // 애플리케이션 시작 시 실행되는 더미 데이터 생성
    @Bean
    public CommandLineRunner initDummyUsers(UserAccountService userAccountService) {
        return args -> {
            try {
                UserAccountDto admin = new UserAccountDto();
                admin.setUserName("hdm2518");
                admin.setPassword("hdrd0104");
                admin.setRole(Role.SUPERADMIN);
                userAccountService.saveUserAccount(admin);

                UserAccountDto admin1 = new UserAccountDto();
                admin1.setUserName("1");
                admin1.setPassword("1");
                admin1.setRole(Role.SUPERADMIN);
                userAccountService.saveUserAccount(admin1);

                System.out.println("✅ 더미 계정 생성 완료");
            } catch (Exception e) {
                System.out.println("⚠️ 더미 계정 생성 중 오류: " + e.getMessage());
            }
        };
    }


}
