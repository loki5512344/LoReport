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
    Instant createdAt
) {
    public Report(UUID reporterUuid, String reporterName, UUID targetUuid, String targetName, String reason) {
        this(0, reporterUuid, reporterName, targetUuid, targetName, reason, Instant.now());
    }
}
