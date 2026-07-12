package dev.loki.lorep.webhook;

import dev.loki.lorep.database.Report;
import net.jqwik.api.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * **Feature: lorep-report-plugin, Property 5: Webhook Payload Completeness**
 * **Validates: Requirements 3.1**
 */
public class DiscordWebhookTest {
    
    private static final Logger logger = Logger.getLogger("TestLogger");
    
    /**
     * **Property 5: Webhook Payload Completeness**
     * For any report sent to the webhook, the payload SHALL contain reporter name,
     * target name, reason, and formatted timestamp.
     * **Validates: Requirements 3.1**
     */
    @Property(tries = 100)
    void webhookPayloadContainsAllRequiredFields(
            @ForAll("playerNames") String reporterName,
            @ForAll("playerNames") String targetName,
            @ForAll("reasons") String reason
    ) {
        DiscordWebhook webhook = new DiscordWebhook("https://example.com/webhook", logger);
        
        Report report = new Report(
            UUID.randomUUID(),
            reporterName,
            UUID.randomUUID(),
            targetName,
            reason
        );
        
        String payload = webhook.buildPayload(report);
        
        // Verify all required fields are present in payload
        assertTrue(payload.contains(escapeForCheck(reporterName)), 
            "Payload should contain reporter name: " + reporterName);
        assertTrue(payload.contains(escapeForCheck(targetName)), 
            "Payload should contain target name: " + targetName);
        assertTrue(payload.contains(escapeForCheck(reason)), 
            "Payload should contain reason: " + reason);
        
        // Verify timestamp format (dd.MM.yyyy HH:mm:ss) - using Pattern.DOTALL for multiline
        assertTrue(java.util.regex.Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}:\\d{2}", 
            java.util.regex.Pattern.DOTALL).matcher(payload).find(),
            "Payload should contain formatted timestamp");
        
        // Verify it's valid JSON structure
        assertTrue(payload.contains("\"embeds\""), "Payload should have embeds array");
        assertTrue(payload.contains("\"fields\""), "Payload should have fields array");
    }
    
    @Provide
    Arbitrary<String> playerNames() {
        return Arbitraries.strings()
            .withCharRange('a', 'z')
            .withCharRange('A', 'Z')
            .withCharRange('0', '9')
            .withChars('_')
            .ofMinLength(3)
            .ofMaxLength(16);
    }
    
    @Provide
    Arbitrary<String> reasons() {
        return Arbitraries.strings()
            .withCharRange('a', 'z')
            .withCharRange('A', 'Z')
            .withCharRange('0', '9')
            .withChars(' ', '.', ',', '!')
            .ofMinLength(1)
            .ofMaxLength(100);
    }
    
    private String escapeForCheck(String text) {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"");
    }
    
    @Test
    void payloadHasCorrectStructure() {
        DiscordWebhook webhook = new DiscordWebhook("https://example.com/webhook", logger);
        
        Report report = new Report(
            UUID.randomUUID(), "TestReporter",
            UUID.randomUUID(), "TestTarget",
            "Test reason"
        );
        
        String payload = webhook.buildPayload(report);
        
        assertTrue(payload.contains("Новый репорт"));
        assertTrue(payload.contains("Нарушитель"));
        assertTrue(payload.contains("Отправитель"));
        assertTrue(payload.contains("Причина"));
        assertTrue(payload.contains("Время"));
    }
}
