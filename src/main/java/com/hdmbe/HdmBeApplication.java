package com.hdmbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HdmBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HdmBeApplication.class, args);
    }

}
