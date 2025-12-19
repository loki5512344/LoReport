package dev.loki.lorep.gui;

import dev.loki.lorep.util.PaginationUtil;
import net.jqwik.api.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * **Feature: lorep-report-plugin, Property 6: Pagination Correctness**
 * **Validates: Requirements 4.4**
 */
public class PaginationTest {
    
    private static final int PAGE_SIZE = 45;
    
    /**
     * **Property 6: Pagination Correctness**
     * For any list of N reports and page size P, requesting page K SHALL return
     * at most P reports starting from index K*P, and the total page count SHALL
     * equal ceil(N/P).
     * **Validates: Requirements 4.4**
     */
    @Property(tries = 100)
    void paginationCorrectness(
            @ForAll("totalReports") int totalReports,
            @ForAll("pageNumbers") int page
    ) {
        int totalPages = PaginationUtil.calculateTotalPages(totalReports, PAGE_SIZE);
        int startIndex = PaginationUtil.calculateStartIndex(page, PAGE_SIZE);
        int itemsOnPage = PaginationUtil.calculateItemsOnPage(page, PAGE_SIZE, totalReports);
        
        // Total pages should equal ceil(N/P)
        int expectedTotalPages = totalReports <= 0 ? 1 : (int) Math.ceil((double) totalReports / PAGE_SIZE);
        assertEquals(expectedTotalPages, totalPages, 
            "Total pages should equal ceil(totalReports/pageSize)");
        
        // Start index should be page * pageSize
        assertEquals(page * PAGE_SIZE, startIndex,
            "Start index should be page * pageSize");
        
        // Items on page should be at most PAGE_SIZE
        assertTrue(itemsOnPage <= PAGE_SIZE,
            "Items on page should not exceed page size");
        
        // Items on page should be non-negative
        assertTrue(itemsOnPage >= 0,
            "Items on page should be non-negative");
        
        // For valid pages, items should match expected
        if (page < totalPages && page >= 0) {
            int expectedItems = Math.min(PAGE_SIZE, totalReports - startIndex);
            assertEquals(Math.max(0, expectedItems), itemsOnPage,
                "Items on valid page should match expected count");
        }
    }
    
    @Property(tries = 100)
    void allItemsCoveredByPages(@ForAll("totalReports") int totalReports) {
        Assume.that(totalReports > 0);
        
        int totalPages = PaginationUtil.calculateTotalPages(totalReports, PAGE_SIZE);
        int totalItemsCovered = 0;
        
        for (int page = 0; page < totalPages; page++) {
            totalItemsCovered += PaginationUtil.calculateItemsOnPage(page, PAGE_SIZE, totalReports);
        }
        
        assertEquals(totalReports, totalItemsCovered,
            "Sum of items across all pages should equal total reports");
    }
    
    @Provide
    Arbitrary<Integer> totalReports() {
        return Arbitraries.integers().between(0, 500);
    }
    
    @Provide
    Arbitrary<Integer> pageNumbers() {
        return Arbitraries.integers().between(0, 20);
    }
    
    @Test
    void emptyReportsHasOnePage() {
        assertEquals(1, PaginationUtil.calculateTotalPages(0, PAGE_SIZE));
        assertEquals(0, PaginationUtil.calculateItemsOnPage(0, PAGE_SIZE, 0));
    }
    
    @Test
    void exactlyOnePageOfReports() {
        assertEquals(1, PaginationUtil.calculateTotalPages(PAGE_SIZE, PAGE_SIZE));
        assertEquals(PAGE_SIZE, PaginationUtil.calculateItemsOnPage(0, PAGE_SIZE, PAGE_SIZE));
    }
    
    @Test
    void lastPageHasRemainingItems() {
        int totalReports = PAGE_SIZE + 10;
        assertEquals(2, PaginationUtil.calculateTotalPages(totalReports, PAGE_SIZE));
        assertEquals(PAGE_SIZE, PaginationUtil.calculateItemsOnPage(0, PAGE_SIZE, totalReports));
        assertEquals(10, PaginationUtil.calculateItemsOnPage(1, PAGE_SIZE, totalReports));
    }
}
