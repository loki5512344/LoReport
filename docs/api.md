# API для разработчиков

## Подключение к проекту

### Maven
```xml
<repository>
    <id>codeberg</id>
    <url>https://codeberg.org/api/packages/loki5512344/maven</url>
</repository>

<dependency>
    <groupId>dev.loki</groupId>
    <artifactId>loreport</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

### Gradle
```kotlin
repositories {
    maven("https://codeberg.org/api/packages/loki5512344/maven")
}

dependencies {
    compileOnly("dev.loki:loreport:1.0.0")
}
```

## Основные классы

### LorepPlugin
Главный класс плагина, предоставляет доступ к основным компонентам.

```java
LorepPlugin plugin = LorepPlugin.getInstance();
DatabaseManager db = plugin.getDatabaseManager();
ConfigManager config = plugin.getConfigManager();
```

### DatabaseManager
Интерфейс для работы с базой данных репортов.

```java
public interface DatabaseManager {
    void addReport(Report report);
    List<Report> getReports(String playerName);
    List<Report> getAllReports();
    int getReportCount(String playerName);
    boolean hasReported(String reporter, String reported);
    void initialize();
    void close();
}
```

### Report
Класс представляющий репорт.

```java
public class Report {
    private final String reporter;
    private final String reported;
    private final String reason;
    private final long timestamp;
    
    // Конструкторы и геттеры
}
```

## События (Events)

### ReportSubmitEvent
Вызывается при отправке нового репорта.

```java
@EventHandler
public void onReportSubmit(ReportSubmitEvent event) {
    Player reporter = event.getReporter();
    String reported = event.getReported();
    String reason = event.getReason();
    
    // Ваша логика
    
    // Отменить репорт
    event.setCancelled(true);
}
```

### ReportViewEvent
Вызывается при просмотре репортов через GUI.

```java
@EventHandler
public void onReportView(ReportViewEvent event) {
    Player viewer = event.getViewer();
    List<Report> reports = event.getReports();
    
    // Ваша логика
}
```

## Примеры использования

### Добавление репорта программно

```java
public void addCustomReport(String reporter, String reported, String reason) {
    LorepPlugin plugin = LorepPlugin.getInstance();
    DatabaseManager db = plugin.getDatabaseManager();
    
    Report report = new Report(reporter, reported, reason, System.currentTimeMillis());
    db.addReport(report);
    
    // Отправить в Discord если настроен
    DiscordWebhook webhook = plugin.getDiscordWebhook();
    if (webhook != null) {
        webhook.sendReport(report);
    }
}
```

### Получение статистики игрока

```java
public void showPlayerStats(Player admin, String targetPlayer) {
    LorepPlugin plugin = LorepPlugin.getInstance();
    DatabaseManager db = plugin.getDatabaseManager();
    
    List<Report> reports = db.getReports(targetPlayer);
    int count = reports.size();
    
    admin.sendMessage("У игрока " + targetPlayer + " " + count + " репортов");
    
    for (Report report : reports) {
        admin.sendMessage("- " + report.getReason() + " (от " + report.getReporter() + ")");
    }
}
```

### Интеграция с системой наказаний

```java
@EventHandler
public void onReportSubmit(ReportSubmitEvent event) {
    String reported = event.getReported();
    
    // Получаем количество репортов
    DatabaseManager db = LorepPlugin.getInstance().getDatabaseManager();
    int reportCount = db.getReportCount(reported);
    
    // Автоматические действия
    if (reportCount >= 5) {
        // Временный бан на 1 час
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
            "tempban " + reported + " 1h Множественные репорты");
    } else if (reportCount >= 3) {
        // Предупреждение
        Player player = Bukkit.getPlayer(reported);
        if (player != null) {
            player.sendMessage("§cВнимание! На вас поступают жалобы от других игроков.");
        }
    }
}
```

### Кастомные команды

```java
public class CustomReportCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        
        Player player = (Player) sender;
        LorepPlugin plugin = LorepPlugin.getInstance();
        
        if (args.length < 2) {
            player.sendMessage("Использование: /customreport <игрок> <причина>");
            return true;
        }
        
        String reported = args[0];
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        
        // Создаем репорт с дополнительной информацией
        Report report = new Report(
            player.getName(), 
            reported, 
            "[CUSTOM] " + reason, 
            System.currentTimeMillis()
        );
        
        plugin.getDatabaseManager().addReport(report);
        player.sendMessage("§aКастомный репорт отправлен!");
        
        return true;
    }
}
```

## Хуки для других плагинов

### PlaceholderAPI

```java
public class LorepPlaceholders extends PlaceholderExpansion {
    
    @Override
    public String getIdentifier() {
        return "loreport";
    }
    
    @Override
    public String getAuthor() {
        return "loki5512344";
    }
    
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) return "";
        
        DatabaseManager db = LorepPlugin.getInstance().getDatabaseManager();
        
        switch (params) {
            case "reports_count":
                return String.valueOf(db.getReportCount(player.getName()));
            case "reports_total":
                return String.valueOf(db.getAllReports().size());
            default:
                return null;
        }
    }
}
```

### DiscordSRV интеграция

```java
@EventHandler
public void onReportSubmit(ReportSubmitEvent event) {
    // Отправляем в Discord через DiscordSRV
    String message = String.format(
        "🚨 **Новый репорт!**\n" +
        "**Отправитель:** %s\n" +
        "**Нарушитель:** %s\n" +
        "**Причина:** %s",
        event.getReporter().getName(),
        event.getReported(),
        event.getReason()
    );
    
    DiscordSRV.getPlugin().getMainTextChannel().sendMessage(message).queue();
}
```

## Лучшие практики

1. **Всегда проверяйте доступность плагина:**
```java
if (!Bukkit.getPluginManager().isPluginEnabled("Loreport")) {
    // Плагин не загружен
    return;
}
```

2. **Используйте асинхронные операции для БД:**
```java
Bukkit.getScheduler().runTaskAsynchronously(yourPlugin, () -> {
    // Работа с базой данных
    List<Report> reports = db.getReports(playerName);
    
    Bukkit.getScheduler().runTask(yourPlugin, () -> {
        // Обновление UI в главном потоке
        updateGUI(reports);
    });
});
```

3. **Обрабатывайте исключения:**
```java
try {
    db.addReport(report);
} catch (Exception e) {
    getLogger().severe("Ошибка при добавлении репорта: " + e.getMessage());
}
```