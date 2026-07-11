package dev.loki.lorep.command;

import dev.loki.lorep.LorepPlugin;
import dev.loki.lorep.database.Report;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReportCommand implements CommandExecutor, TabCompleter {
    
    private final LorepPlugin plugin;
    
    public ReportCommand(LorepPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cЭта команда только для игроков!");
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(plugin.getConfigManager().getMessage("usage"));
            return true;
        }
        
        // Handle subcommands for admins
        if (args[0].equalsIgnoreCase("gui")) {
            return handleGui(player);
        }
        
        if (args[0].equalsIgnoreCase("stats") && args.length >= 2) {
            return handleStats(player, args[1]);
        }
        
        // Regular report command
        if (!player.hasPermission("lorep.report")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage(plugin.getConfigManager().getMessage("usage"));
            return true;
        }
        
        String targetName = args[0];
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        // Validate reason
        if (reason.trim().isEmpty()) {
            player.sendMessage(plugin.getConfigManager().getMessage("usage"));
            return true;
        }

        if (reason.length() > 500) {
            player.sendMessage("§cПричина слишком длинная! Максимум 500 символов.");
            return true;
        }

        return handleReport(player, targetName, reason);
    }
    
    private boolean handleReport(Player reporter, String targetName, String reason) {
        // Check if target exists
        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            reporter.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
            return true;
        }
        
        // Check self-report
        if (target.getUniqueId().equals(reporter.getUniqueId())) {
            reporter.sendMessage(plugin.getConfigManager().getMessage("self-report"));
            return true;
        }
        
        // Check duplicate report
        if (plugin.getDatabaseManager().hasReported(reporter.getUniqueId(), target.getUniqueId())) {
            reporter.sendMessage(plugin.getConfigManager().getMessage("already-reported"));
            return true;
        }
        
        // Create and save report
        Report report = new Report(
            reporter.getUniqueId(),
            reporter.getName(),
            target.getUniqueId(),
            target.getName() != null ? target.getName() : targetName,
            reason
        );
        
        plugin.getDatabaseManager().saveReport(report);
        
        // Send to Discord webhook
        if (plugin.getDiscordWebhook() != null) {
            plugin.getDiscordWebhook().sendReport(report);
        }
        
        reporter.sendMessage(plugin.getConfigManager().getMessage("report-sent"));
        return true;
    }

    private boolean handleGui(Player player) {
        if (!player.hasPermission("lorep.admin")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }
        
        new dev.loki.lorep.gui.ReportGui(plugin, 0).open(player);
        return true;
    }
    
    private boolean handleStats(Player player, String targetName) {
        new ReportStatsCommand(plugin).execute(player, targetName);
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // First argument: player names or subcommands
            String input = args[0].toLowerCase();
            
            if (sender.hasPermission("lorep.admin")) {
                if ("gui".startsWith(input)) {
                    completions.add("gui");
                }
                if ("stats".startsWith(input)) {
                    completions.add("stats");
                }
            }
            
            // Add online player names
            completions.addAll(
                Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .collect(Collectors.toList())
            );
        } else if (args.length == 2 && args[0].equalsIgnoreCase("stats")) {
            // Second argument for stats: player names
            String input = args[1].toLowerCase();
            completions.addAll(
                Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .collect(Collectors.toList())
            );
        }
        
        return completions;
    }
}
