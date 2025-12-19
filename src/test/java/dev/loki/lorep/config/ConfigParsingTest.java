package dev.loki.lorep.config;

import net.jqwik.api.*;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * **Feature: lorep-report-plugin, Property 9: Configuration Parsing**
 * **Validates: Requirements 7.1, 7.3, 7.4**
 */
public class ConfigParsingTest {
    
    private final Yaml yaml = new Yaml();
    
    /**
     * Property 9: Configuration Parsing
     * For any valid config.yml content, parsing SHALL correctly extract webhook URL and database settings.
     */
    @Property(tries = 100)
    void configurationParsingExtractsValues(
            @ForAll("webhookUrls") String webhookUrl,
            @ForAll("databaseTypes") String dbType,
            @ForAll("sqliteFiles") String sqliteFile,
            @ForAll("hosts") String pgHost,
            @ForAll("ports") int pgPort
    ) {
        String configYaml = buildConfigYaml(webhookUrl, dbType, sqliteFile, pgHost, pgPort);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> config = yaml.load(configYaml);
        
        // Verify webhook URL is correctly parsed
        assertEquals(webhookUrl, config.get("webhook-url"));
        
        // Verify database settings
        @SuppressWarnings("unchecked")
        Map<String, Object> database = (Map<String, Object>) config.get("database");
        assertNotNull(database);
        assertEquals(dbType, database.get("type"));
        
        // Verify SQLite settings
        @SuppressWarnings("unchecked")
        Map<String, Object> sqlite = (Map<String, Object>) database.get("sqlite");
        assertNotNull(sqlite);
        assertEquals(sqliteFile, sqlite.get("file"));
        
        // Verify PostgreSQL settings
        @SuppressWarnings("unchecked")
        Map<String, Object> postgresql = (Map<String, Object>) database.get("postgresql");
        assertNotNull(postgresql);
        assertEquals(pgHost, postgresql.get("host"));
        assertEquals(pgPort, postgresql.get("port"));
    }
    
    @Provide
    Arbitrary<String> webhookUrls() {
        return Arbitraries.of(
                "",
                "https://discord.com/api/webhooks/123/abc",
                "https://discord.com/api/webhooks/999999/xyz123"
        );
    }
    
    @Provide
    Arbitrary<String> databaseTypes() {
        return Arbitraries.of("sqlite", "postgresql");
    }
    
    @Provide
    Arbitrary<String> sqliteFiles() {
        return Arbitraries.of("reports.db", "data.db", "lorep.sqlite");
    }
    
    @Provide
    Arbitrary<String> hosts() {
        return Arbitraries.of("localhost", "127.0.0.1", "db.example.com");
    }
    
    @Provide
    Arbitrary<Integer> ports() {
        return Arbitraries.integers().between(1, 65535);
    }
    
    private String buildConfigYaml(String webhookUrl, String dbType, String sqliteFile, String pgHost, int pgPort) {
        return String.format("""
                webhook-url: "%s"
                database:
                  type: "%s"
                  sqlite:
                    file: "%s"
                  postgresql:
                    host: "%s"
                    port: %d
                    database: "lorep"
                    username: "lorep"
                    password: "password"
                    pool-size: 10
                messages:
                  report-sent: "Report sent!"
                """, webhookUrl, dbType, sqliteFile, pgHost, pgPort);
    }
    
    @Test
    void defaultConfigHasValidStructure() {
        String defaultConfig = """
                webhook-url: ""
                database:
                  type: "sqlite"
                  sqlite:
                    file: "reports.db"
                  postgresql:
                    host: "localhost"
                    port: 5432
                    database: "lorep"
                    username: "lorep"
                    password: "password"
                    pool-size: 10
                messages:
                  report-sent: "&aРепорт успешно отправлен!"
                """;
        
        @SuppressWarnings("unchecked")
        Map<String, Object> config = yaml.load(defaultConfig);
        
        assertNotNull(config.get("webhook-url"));
        assertNotNull(config.get("database"));
        assertNotNull(config.get("messages"));
    }
}
