package dev.loki.lorep.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteDatabaseManager implements DatabaseManager {
    
    private final File dataFolder;
    private final String fileName;
    private final Logger logger;
    private Connection connection;
    
    public SQLiteDatabaseManager(File dataFolder, String fileName, Logger logger) {
        this.dataFolder = dataFolder;
        this.fileName = fileName;
        this.logger = logger;
    }
    
    @Override
    public void initialize() {
        try {
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            
            File dbFile = new File(dataFolder, fileName);
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            connection = DriverManager.getConnection(url);
            
            createTables();
            logger.info("SQLite database initialized: " + dbFile.getAbsolutePath());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to initialize SQLite database", e);
        }
    }
    
    private void createTables() throws SQLException {
        String createTable = """
            CREATE TABLE IF NOT EXISTS reports (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                reporter_uuid TEXT NOT NULL,
                reporter_name TEXT NOT NULL,
                target_uuid TEXT NOT NULL,
                target_name TEXT NOT NULL,
                reason TEXT NOT NULL,
                image_url TEXT,
                status TEXT NOT NULL DEFAULT 'PENDING',
                created_at INTEGER NOT NULL
            )
            """;
        
        String createTargetIndex = "CREATE INDEX IF NOT EXISTS idx_target_uuid ON reports(target_uuid)";
        String createReporterTargetIndex = "CREATE INDEX IF NOT EXISTS idx_reporter_target ON reports(reporter_uuid, target_uuid)";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTable);
            stmt.execute(createTargetIndex);
            stmt.execute(createReporterTargetIndex);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("SQLite database connection closed");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Failed to close SQLite connection", e);
        }
    }
    
    @Override
    public void saveReport(Report report) {
        String sql = "INSERT INTO reports (reporter_uuid, reporter_name, target_uuid, "
            + "target_name, reason, image_url, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, report.reporterUuid().toString());
            stmt.setString(2, report.reporterName());
            stmt.setString(3, report.targetUuid().toString());
            stmt.setString(4, report.targetName());
            stmt.setString(5, report.reason());
            if (report.imageUrl() != null) {
                stmt.setString(6, report.imageUrl());
            } else {
                stmt.setNull(6, java.sql.Types.VARCHAR);
            }
            stmt.setString(7, report.status());
            stmt.setLong(8, report.createdAt().toEpochMilli());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to save report", e);
        }
    }
    
    @Override
    public List<Report> getReportsForTarget(UUID targetUuid) {
        String sql = "SELECT * FROM reports WHERE target_uuid = ? ORDER BY created_at DESC";
        List<Report> reports = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);) {
            stmt.setString(1, targetUuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapReport(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get reports for target", e);
        }

        return reports;
    }
    
    @Override
    public List<Report> getAllReports() {
        String sql = "SELECT * FROM reports ORDER BY created_at DESC";
        List<Report> reports = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                reports.add(mapReport(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get all reports", e);
        }

        return reports;
    }
    
    @Override
    public int getReportCount(UUID targetUuid) {
        String sql = "SELECT COUNT(*) FROM reports WHERE target_uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, targetUuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get report count", e);
        }

        return 0;
    }
    
    @Override
    public boolean hasReported(UUID reporterUuid, UUID targetUuid) {
        String sql = "SELECT COUNT(*) FROM reports WHERE reporter_uuid = ? AND target_uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, reporterUuid.toString());
            stmt.setString(2, targetUuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to check if reported", e);
        }

        return false;
    }
    
    @Override
    public List<Report> getReportsPaginated(int page, int pageSize) {
        String sql = "SELECT * FROM reports ORDER BY created_at DESC LIMIT ? OFFSET ?";
        List<Report> reports = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, page * pageSize);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapReport(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get paginated reports", e);
        }

        return reports;
    }
    
    @Override
    public int getTotalReportCount() {
        String sql = "SELECT COUNT(*) FROM reports";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get total report count", e);
        }

        return 0;
    }
    
    private Report mapReport(ResultSet rs) throws SQLException {
        String imageUrl = rs.getString("image_url");
        if (rs.wasNull()) {
            imageUrl = null;
        }
        return new Report(
            rs.getInt("id"),
            UUID.fromString(rs.getString("reporter_uuid")),
            rs.getString("reporter_name"),
            UUID.fromString(rs.getString("target_uuid")),
            rs.getString("target_name"),
            rs.getString("reason"),
            imageUrl,
            rs.getString("status"),
            Instant.ofEpochMilli(rs.getLong("created_at"))
        );
    }

    @Override
    public void updateReportStatus(int reportId, String status) {
        String sql = "UPDATE reports SET status = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, reportId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update report status", e);
        }
    }

    @Override
    public void updateReportImage(int reportId, String imageUrl) {
        String sql = "UPDATE reports SET image_url = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (imageUrl != null) {
                stmt.setString(1, imageUrl);
            } else {
                stmt.setNull(1, java.sql.Types.VARCHAR);
            }
            stmt.setInt(2, reportId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update report image", e);
        }
    }

    @Override
    public Report getReportById(int id) {
        String sql = "SELECT * FROM reports WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapReport(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get report by id", e);
        }

        return null;
    }
}
