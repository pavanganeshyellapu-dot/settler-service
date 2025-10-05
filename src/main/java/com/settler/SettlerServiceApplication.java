package com.settler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SettlerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SettlerServiceApplication.class, args);
        System.out.println("🚀 Settler Service Started Successfully!");
    }
}
