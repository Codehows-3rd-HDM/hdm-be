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
}
