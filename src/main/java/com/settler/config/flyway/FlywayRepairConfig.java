package com.settler.config.flyway;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FlywayRepairConfig {

    private final Flyway flyway;

    @PostConstruct
    public void repairAndMigrate() {
        try {
            log.info("🧹 Running Flyway repair before migration...");
            flyway.repair();
            log.info("✅ Flyway repair completed successfully.");

            log.info("🚀 Running Flyway migration...");
            flyway.migrate();
            log.info("✅ Flyway migration completed successfully.");
        } catch (Exception e) {
            log.error("❌ Flyway repair/migration failed: {}", e.getMessage(), e);
            throw e;
        }
    }
}
