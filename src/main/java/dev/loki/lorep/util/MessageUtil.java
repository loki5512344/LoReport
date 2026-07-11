package dev.loki.lorep.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {

    private MessageUtil() {}

    private static final String PREFIX = "§8[§clorep§8] §r";
    
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public static void send(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + colorize(message));
    }
    
    public static void sendRaw(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }
    
    public static void sendNoPrefix(Player player, String message) {
        player.sendMessage(colorize(message));
    }
    
    public static String format(String message, String... replacements) {
        String result = message;
        for (int i = 0; i < replacements.length - 1; i += 2) {
            result = result.replace(replacements[i], replacements[i + 1]);
        }
        return colorize(result);
    }
}
