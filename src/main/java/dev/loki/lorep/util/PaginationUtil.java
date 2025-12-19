package dev.loki.lorep.util;

public class PaginationUtil {
    
    public static int calculateTotalPages(int totalItems, int pageSize) {
        if (totalItems <= 0) return 1;
        return (int) Math.ceil((double) totalItems / pageSize);
    }
    
    public static int calculateStartIndex(int page, int pageSize) {
        return page * pageSize;
    }
    
    public static int calculateItemsOnPage(int page, int pageSize, int totalItems) {
        int startIndex = calculateStartIndex(page, pageSize);
        int remaining = totalItems - startIndex;
        return Math.min(pageSize, Math.max(0, remaining));
    }
}
