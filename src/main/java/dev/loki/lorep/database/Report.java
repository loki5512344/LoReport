package dev.loki.lorep.database;

import java.time.Instant;
import java.util.UUID;

public record Report(
    int id,
    UUID reporterUuid,
    String reporterName,
    UUID targetUuid,
    String targetName,
    String reason,
    String imageUrl,
    String status,
    Instant createdAt
) {
    public Report(UUID reporterUuid, String reporterName, UUID targetUuid, String targetName, String reason) {
        this(0, reporterUuid, reporterName, targetUuid, targetName, reason, null, "PENDING", Instant.now());
    }

    public Report(UUID reporterUuid, String reporterName, UUID targetUuid, String targetName, String reason, String imageUrl) {
        this(0, reporterUuid, reporterName, targetUuid, targetName, reason, imageUrl, "PENDING", Instant.now());
    }
}
