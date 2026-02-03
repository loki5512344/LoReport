package dev.loki.lorep.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgreSQLDatabaseManager implements DatabaseManager {
    
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final int poolSize;
    private final Logger logger;
    private HikariDataSource dataSource;
    
    public PostgreSQLDatabaseManager(String host, int port, String database, 
                                      String username, String password, int poolSize, Logger logger) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.poolSize = poolSize;
        this.logger = logger;
    }
    
    @Override
    public void initialize() {
        try {
            // Explicitly load PostgreSQL driver
            Class.forName("org.postgresql.Driver");
            
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("org.postgresql.Driver");
            config.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + database);
            config.setUsername(username);
            config.setPassword(password);
            config.setMaximumPoolSize(poolSize);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setPoolName("lorep-pool");
            
            dataSource = new HikariDataSource(config);
            createTables();
            logger.info("PostgreSQL database initialized: " + host + ":" + port + "/" + database);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "PostgreSQL driver not found", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize PostgreSQL database", e);
        }
    }
    
    private void createTables() throws SQLException {
        String createTable = """
            CREATE TABLE IF NOT EXISTS reports (
                id SERIAL PRIMARY KEY,
                reporter_uuid TEXT NOT NULL,
                reporter_name TEXT NOT NULL,
                target_uuid TEXT NOT NULL,
                target_name TEXT NOT NULL,
                reason TEXT NOT NULL,
                created_at BIGINT NOT NULL
            )
            """;
        
        String createTargetIndex = "CREATE INDEX IF NOT EXISTS idx_target_uuid ON reports(target_uuid)";
        String createReporterTargetIndex = "CREATE INDEX IF NOT EXISTS idx_reporter_target ON reports(reporter_uuid, target_uuid)";
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTable);
            stmt.execute(createTargetIndex);
            stmt.execute(createReporterTargetIndex);
        }
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("PostgreSQL connection pool closed");
        }
    }
    
    @Override
    public void saveReport(Report report) {
        String sql = "INSERT INTO reports (reporter_uuid, reporter_name, target_uuid, target_name, reason, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, report.reporterUuid().toString());
            stmt.setString(2, report.reporterName());
            stmt.setString(3, report.targetUuid().toString());
            stmt.setString(4, report.targetName());
            stmt.setString(5, report.reason());
            stmt.setLong(6, report.createdAt().toEpochMilli());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to save report", e);
        }
    }
    
    @Override
    public List<Report> getReportsForTarget(UUID targetUuid) {
        String sql = "SELECT * FROM reports WHERE target_uuid = ? ORDER BY created_at DESC";
        List<Report> reports = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, targetUuid.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reports.add(mapReport(rs));
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
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
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
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, targetUuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get report count", e);
        }
        
        return 0;
    }
    
    @Override
    public boolean hasReported(UUID reporterUuid, UUID targetUuid) {
        String sql = "SELECT COUNT(*) FROM reports WHERE reporter_uuid = ? AND target_uuid = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reporterUuid.toString());
            stmt.setString(2, targetUuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
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
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, page * pageSize);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reports.add(mapReport(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get paginated reports", e);
        }
        
        return reports;
    }
    
    @Override
    public int getTotalReportCount() {
        String sql = "SELECT COUNT(*) FROM reports";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get total report count", e);
        }
        
        return 0;
    }
    
    private Report mapReport(ResultSet rs) throws SQLException {
        return new Report(
            rs.getInt("id"),
            UUID.fromString(rs.getString("reporter_uuid")),
            rs.getString("reporter_name"),
            UUID.fromString(rs.getString("target_uuid")),
            rs.getString("target_name"),
            rs.getString("reason"),
            Instant.ofEpochMilli(rs.getLong("created_at"))
        );
    }
}
