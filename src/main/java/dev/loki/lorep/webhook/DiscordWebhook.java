package dev.loki.lorep.webhook;

import dev.loki.lorep.database.Report;
import okhttp3.*;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiscordWebhook {
    
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
        .ofPattern("dd.MM.yyyy HH:mm:ss")
        .withZone(ZoneId.systemDefault());
    
    private final String webhookUrl;
    private final Logger logger;
    private final OkHttpClient client;
    
    public DiscordWebhook(String webhookUrl, Logger logger) {
        this.webhookUrl = webhookUrl;
        this.logger = logger;
        this.client = new OkHttpClient();
    }
    
    public void sendReport(Report report) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            logger.warning("Discord webhook URL is not configured");
            return;
        }
        
        String json = buildPayload(report);
        
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
            .url(webhookUrl)
            .post(body)
            .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logger.log(Level.WARNING, "Failed to send Discord webhook", e);
            }
            
            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    logger.warning("Discord webhook returned: " + response.code());
                }
                response.close();
            }
        });
    }
    
    public String buildPayload(Report report) {
        String timestamp = FORMATTER.format(report.createdAt());
        
        return String.format("""
            {
                "embeds": [{
                    "title": "📢 Новый репорт",
                    "color": 16711680,
                    "fields": [
                        {"name": "👤 Нарушитель", "value": "%s", "inline": true},
                        {"name": "📝 Отправитель", "value": "%s", "inline": true},
                        {"name": "📋 Причина", "value": "%s", "inline": false},
                        {"name": "🕐 Время", "value": "%s", "inline": false}
                    ]
                }]
            }
            """,
            escapeJson(report.targetName()),
            escapeJson(report.reporterName()),
            escapeJson(report.reason()),
            timestamp
        );
    }
    
    private String escapeJson(String text) {
        if (text == null) return "";
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
