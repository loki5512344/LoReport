package dev.loki.lorep;

import dev.loki.lorep.config.ConfigManager;
import dev.loki.lorep.database.DatabaseManager;
import dev.loki.lorep.webhook.DiscordWebhook;
import org.bukkit.plugin.java.JavaPlugin;

public final class LorepPlugin extends JavaPlugin {
    
    private static LorepPlugin instance;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private DiscordWebhook discordWebhook;
    
    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("lorep plugin enabling...");
        
        // Initialize managers (will be implemented later)
        
        getLogger().info("lorep plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("lorep plugin disabled!");
    }
    
    public static LorepPlugin getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    public DiscordWebhook getDiscordWebhook() {
        return discordWebhook;
    }
    
    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }
    
    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
    
    public void setDiscordWebhook(DiscordWebhook discordWebhook) {
        this.discordWebhook = discordWebhook;
    }
}
