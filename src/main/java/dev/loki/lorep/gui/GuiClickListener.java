package dev.loki.lorep.gui;

import dev.loki.lorep.LorepPlugin;
import dev.loki.lorep.util.PaginationUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class GuiClickListener implements Listener {
    
    private final LorepPlugin plugin;
    
    public GuiClickListener(LorepPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ReportGui gui)) {
            return;
        }
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        int slot = event.getRawSlot();
        
        // Navigation buttons are in bottom row (slots 45-53)
        switch (slot) {
            case 45 -> { // Previous page
                if (gui.getPage() > 0) {
                    new ReportGui(plugin, gui.getPage() - 1).open(player);
                }
            }
            case 47 -> { // Close
                player.closeInventory();
            }
            case 53 -> { // Next page
                int totalReports = plugin.getDatabaseManager().getTotalReportCount();
                int totalPages = PaginationUtil.calculateTotalPages(totalReports, ReportGui.PAGE_SIZE);
                if (gui.getPage() < totalPages - 1) {
                    new ReportGui(plugin, gui.getPage() + 1).open(player);
                }
            }
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof ReportGui) {
            event.setCancelled(true);
        }
    }
}
