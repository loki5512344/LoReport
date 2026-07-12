package dev.loki.lorep.gui;

import dev.loki.lorep.LorepPlugin;
import dev.loki.lorep.database.Report;
import dev.loki.lorep.util.PaginationUtil;
import org.bukkit.Bukkit;
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

        String action = gui.slotActions.get(event.getRawSlot());

        if (action == null) {
            return;
        }

        switch (action) {
            case "prev_page" -> handlePrevPage(player, gui);
            case "next_page" -> handleNextPage(player, gui);
            case "close" -> handleClose(player, gui);
            case "teleport" -> handleTeleport(player, gui);
            case "screenshot" -> handleScreenshot(player, gui);
            default -> handleDefaultAction(player, gui, action);
        }
    }

    private void handlePrevPage(Player player, ReportGui gui) {
        if (gui.page > 0) {
            new ReportGui(plugin, gui.page - 1).open(player);
        }
    }

    private void handleNextPage(Player player, ReportGui gui) {
        int totalReports = plugin.getDatabaseManager().getTotalReportCount();
        int pageSize = plugin.getConfig().getInt("gui.reports-per-page", 45);
        int totalPages = PaginationUtil.calculateTotalPages(totalReports, pageSize);
        if (gui.page < totalPages - 1) {
            new ReportGui(plugin, gui.page + 1).open(player);
        }
    }

    private void handleClose(Player player, ReportGui gui) {
        if (gui.detailView) {
            new ReportGui(plugin, gui.page).open(player);
        } else {
            player.closeInventory();
        }
    }

    private void handleTeleport(Player player, ReportGui gui) {
        Report report = gui.currentReport;
        if (report == null) {
            return;
        }
        Player target = Bukkit.getPlayer(report.targetUuid());
        if (target != null && target.isOnline()) {
            player.teleport(target.getLocation());
                        player.sendMessage(plugin.getConfigManager().getMessage("teleported-to", "%player%", report.targetName()));
                    } else {
                        player.sendMessage(plugin.getConfigManager().getMessage("player-offline", "%player%", report.targetName()));
        }
    }

    private void handleScreenshot(Player player, ReportGui gui) {
        Report report = gui.currentReport;
        if (report != null && report.imageUrl() != null && !report.imageUrl().isEmpty()) {
            player.sendMessage(plugin.getConfigManager().getMessage("screenshot-link", "%url%", report.imageUrl()));
        }
    }

    private void handleDefaultAction(Player player, ReportGui gui, String action) {
        if (action.startsWith("status_set:")) {
            handleStatusSet(player, gui, action.substring("status_set:".length()));
        } else if (action.startsWith("report_detail:")) {
            handleReportDetail(player, gui, action.substring("report_detail:".length()));
        }
    }

    private void handleStatusSet(Player player, ReportGui gui, String status) {
        Report report = gui.currentReport;
        if (report == null) {
            return;
        }
        plugin.getDatabaseManager().updateReportStatus(report.id(), status);
        Report updated = plugin.getDatabaseManager().getReportById(report.id());
        if (updated != null && plugin.getDiscordWebhook() != null) {
            plugin.getDiscordWebhook().sendStatusUpdate(updated);
        }
        if (updated != null) {
            new ReportGui(plugin, gui.page).openDetail(player, updated);
        }
    }

    private void handleReportDetail(Player player, ReportGui gui, String idStr) {
        try {
            int reportId = Integer.parseInt(idStr);
            Report report = plugin.getDatabaseManager().getReportById(reportId);
            if (report != null) {
                new ReportGui(plugin, gui.page).openDetail(player, report);
            }
        } catch (NumberFormatException ignored) {
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof ReportGui) {
            event.setCancelled(true);
        }
    }
}
