package dev.loki.lorep.database;

import java.util.List;
import java.util.UUID;

public interface DatabaseManager {
    
    void initialize();
    
    void close();
    
    void saveReport(Report report);
    
    List<Report> getReportsForTarget(UUID targetUuid);
    
    List<Report> getAllReports();
    
    int getReportCount(UUID targetUuid);
    
    boolean hasReported(UUID reporterUuid, UUID targetUuid);
    
    List<Report> getReportsPaginated(int page, int pageSize);

    int getTotalReportCount();

    void updateReportStatus(int reportId, String status);

    void updateReportImage(int reportId, String imageUrl);

    Report getReportById(int id);
}
