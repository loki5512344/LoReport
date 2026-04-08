package dev.loki.lorep.gui;

import dev.loki.lorep.LorepPlugin;
import dev.loki.lorep.database.Report;
import dev.loki.lorep.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ReportGui implements InventoryHolder {

    public static final int PAGE_SIZE = 45; // 5 rows for reports
    public static final int INVENTORY_SIZE = 54; // 6 rows total

    public static final int SLOT_PREV_PAGE = 45;
    public static final int SLOT_CLOSE = 47;
    public static final int SLOT_PAGE_INFO = 49;
    public static final int SLOT_NEXT_PAGE = 53;

    private final LorepPlugin plugin;
    private final int page;
    private Inventory inventory;
    
    public ReportGui(LorepPlugin plugin, int page) {
        this.plugin = plugin;
        this.page = page;
    }
    
    public void open(Player player) {
        String title = plugin.getConfigManager().getMessage("gui-title", "%page%", String.valueOf(page + 1));
        this.inventory = Bukkit.createInventory(this, INVENTORY_SIZE, title);
        
        List<Report> reports = plugin.getDatabaseManager().getReportsPaginated(page, PAGE_SIZE);
        int totalReports = plugin.getDatabaseManager().getTotalReportCount();
        
        // Add report items
        for (int i = 0; i < reports.size(); i++) {
            Report report = reports.get(i);
            int targetReportCount = plugin.getDatabaseManager().getReportCount(report.targetUuid());
            inventory.setItem(i, createReportItem(report, targetReportCount));
        }
        
        // Add navigation buttons
        addNavigationButtons(totalReports);
        
        player.openInventory(inventory);
    }
    
    private ItemStack createReportItem(Report report, int totalReports) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        
        if (meta != null) {
            // Set skull owner for skin
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(report.targetUuid()));
            
            // Display name
            meta.setDisplayName("§c" + report.targetName());
            
            // Lore with report info
            List<String> lore = new ArrayList<>();
            lore.add("§7");
            lore.add("§eОтправитель: §f" + report.reporterName());
            lore.add("§ePричина: §f" + report.reason());
            lore.add("§eВремя: §f" + TimeUtil.formatTimeAgo(report.createdAt().toEpochMilli()));
            lore.add("§7");
            lore.add("§eВсего репортов: §c" + totalReports);
            
            meta.setLore(lore);
            head.setItemMeta(meta);
        }
        
        return head;
    }

    private void addNavigationButtons(int totalReports) {
        int totalPages = (int) Math.ceil((double) totalReports / PAGE_SIZE);

        // Previous page button
        if (page > 0) {
            ItemStack prevButton = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevButton.getItemMeta();
            if (prevMeta != null) {
                prevMeta.setDisplayName("§a← Предыдущая страница");
                prevButton.setItemMeta(prevMeta);
            }
            inventory.setItem(SLOT_PREV_PAGE, prevButton);
        }

        // Page info
        ItemStack pageInfo = new ItemStack(Material.PAPER);
        ItemMeta pageMeta = pageInfo.getItemMeta();
        if (pageMeta != null) {
            pageMeta.setDisplayName("§eСтраница " + (page + 1) + " из " + Math.max(1, totalPages));
            List<String> lore = new ArrayList<>();
            lore.add("§7Всего репортов: " + totalReports);
            pageMeta.setLore(lore);
            pageInfo.setItemMeta(pageMeta);
        }
        inventory.setItem(SLOT_PAGE_INFO, pageInfo);

        // Next page button
        if (page < totalPages - 1) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName("§aСледующая страница →");
                nextButton.setItemMeta(nextMeta);
            }
            inventory.setItem(SLOT_NEXT_PAGE, nextButton);
        }

        // Close button
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName("§cЗакрыть");
            closeButton.setItemMeta(closeMeta);
        }
        inventory.setItem(SLOT_CLOSE, closeButton);
    }
    
    public int getPage() {
        return page;
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
}
