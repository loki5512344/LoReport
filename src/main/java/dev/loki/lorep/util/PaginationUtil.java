package dev.loki.lorep.util;

public class PaginationUtil {
    
    public static int calculateTotalPages(int totalItems, int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be positive");
        }
        if (totalItems <= 0) return 1;
        return (int) Math.ceil((double) totalItems / pageSize);
    }
    
    public static int calculateStartIndex(int page, int pageSize) {
        if (page < 0 || pageSize <= 0) {
            return 0;
        }
        // Check for overflow
        if (page > Integer.MAX_VALUE / pageSize) {
            return Integer.MAX_VALUE;
        }
        return page * pageSize;
    }
    
    public static int calculateItemsOnPage(int page, int pageSize, int totalItems) {
        int startIndex = calculateStartIndex(page, pageSize);
        int remaining = totalItems - startIndex;
        return Math.min(pageSize, Math.max(0, remaining));
    }
}
