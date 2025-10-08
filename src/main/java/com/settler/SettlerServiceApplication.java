package com.settler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@SpringBootApplication(scanBasePackages = "com.settler")
@EnableJpaRepositories(basePackages = "com.settler.domain")
@EntityScan(basePackages = "com.settler.domain")
public class SettlerServiceApplication {

    public static void main(String[] args) {
        log.info("🚀 Starting Settler Service Application...");
        SpringApplication.run(SettlerServiceApplication.class, args);
        log.info("✅ Settler Service Started Successfully!");
    }
}
