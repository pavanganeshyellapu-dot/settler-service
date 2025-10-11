package com.settler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SettlerServiceApplication {

    public static void main(String[] args) {
        log.info("🚀 Starting Settler Service Application...");
        SpringApplication.run(SettlerServiceApplication.class, args);
        log.info("✅ Settler Service Started Successfully!");
    }
}
