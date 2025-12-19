package dev.loki.lorep.config;

import dev.loki.lorep.LorepPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final LorepPlugin plugin;
    private FileConfiguration config;
    
    public ConfigManager(LorepPlugin plugin) {
        this.plugin = plugin;
        reload();
    }
    
    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }
    
    public String getWebhookUrl() {
        return config.getString("webhook-url", "");
    }
    
    public String getDatabaseType() {
        return config.getString("database.type", "sqlite");
    }
    
    public String getSqliteFile() {
        return config.getString("database.sqlite.file", "reports.db");
    }
    
    public String getPostgresHost() {
        return config.getString("database.postgresql.host", "localhost");
    }
    
    public int getPostgresPort() {
        return config.getInt("database.postgresql.port", 5432);
    }
    
    public String getPostgresDatabase() {
        return config.getString("database.postgresql.database", "lorep");
    }
    
    public String getPostgresUsername() {
        return config.getString("database.postgresql.username", "lorep");
    }
    
    public String getPostgresPassword() {
        return config.getString("database.postgresql.password", "password");
    }
    
    public int getPostgresPoolSize() {
        return config.getInt("database.postgresql.pool-size", 10);
    }
    
    public String getMessage(String key) {
        String message = config.getString("messages." + key, "&cMessage not found: " + key);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public String getMessage(String key, String... replacements) {
        String message = getMessage(key);
        for (int i = 0; i < replacements.length - 1; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return message;
    }
}
