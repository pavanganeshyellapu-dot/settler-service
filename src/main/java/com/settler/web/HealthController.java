package com.settler.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {
    private final DataSource ds;
    public HealthController(DataSource ds) { this.ds = ds; }

    @GetMapping("/health/db")
    public Map<String, Object> db() throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("utcNow", OffsetDateTime.now().toString());
        try (Connection c = ds.getConnection();
             ResultSet rs = c.createStatement().executeQuery("select version()")) {
            rs.next();
            m.put("dbVersion", rs.getString(1));
            m.put("status", "UP");
        }
        return m;
    }
}
	