# Конфигурация

## Основной файл конфигурации

Файл `config.yml` находится в папке `plugins/Loreport/`.

```yaml
# Discord Webhook URL для уведомлений
webhook-url: "https://discord.com/api/webhooks/YOUR_WEBHOOK_URL"

# Настройки базы данных
database:
  # Тип БД: sqlite или postgresql
  type: "sqlite"
  
  # Настройки SQLite
  sqlite:
    file: "reports.db"
  
  # Настройки PostgreSQL
  postgresql:
    host: "localhost"
    port: 5432
    database: "loreport"
    username: "loreport"
    password: "your_password"
    pool-size: 10

# Настройки GUI
gui:
  # Количество репортов на странице
  reports-per-page: 45
  # Автообновление GUI (в тиках, 20 тиков = 1 секунда)
  auto-refresh: 100

# Ограничения
limits:
  # Максимальная длина причины репорта
  max-reason-length: 100
  # Кулдаун между репортами (в секундах)
  report-cooldown: 30

# Сообщения
messages:
  report-sent: "&aРепорт успешно отправлен!"
  already-reported: "&cВы уже отправляли репорт на этого игрока!"
  self-report: "&cВы не можете отправить репорт на себя!"
  player-not-found: "&cИгрок не найден!"
  no-permission: "&cУ вас нет прав на эту команду!"
  usage: "&eИспользование: /report <ник> <причина>"
  no-reports: "&eУ этого игрока нет репортов."
  stats-header: "&6=== Статистика репортов: %player% ==="
  stats-count: "&eВсего репортов: &f%count%"
  stats-last-online: "&eПоследний онлайн: &f%time%"
  stats-report-entry: "&7- %reason% &8(%time% назад)"
  gui-title: "Репорты - Страница %page%"
  cooldown: "&cПодождите %time% секунд перед следующим репортом!"
  reason-too-long: "&cПричина слишком длинная! Максимум %max% символов."
```

## Discord Webhook

### Создание Webhook

1. Откройте настройки канала в Discord
2. Перейдите в "Интеграции" → "Вебхуки"
3. Нажмите "Создать вебхук"
4. Скопируйте URL и вставьте в `webhook-url`

### Формат уведомлений

Плагин отправляет уведомления в следующем формате:
```
🚨 Новый репорт!
Игрок: PlayerName
Нарушитель: ReportedPlayer
Причина: Reason text
Время: 2024-01-01 12:00:00
```

## База данных

### SQLite (рекомендуется для небольших серверов)
- Не требует дополнительной настройки
- Файл БД создается автоматически
- Подходит для серверов до 100 игроков

### PostgreSQL (рекомендуется для больших серверов)
- Требует установки PostgreSQL сервера
- Лучшая производительность
- Подходит для серверов с большим количеством игроков

#### Настройка PostgreSQL

1. Установите PostgreSQL
2. Создайте базу данных:
```sql
CREATE DATABASE loreport;
CREATE USER loreport WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE loreport TO loreport;
```
3. Обновите конфигурацию плагина

## Цветовые коды

Плагин поддерживает стандартные цветовые коды Minecraft:
- `&0` - черный
- `&1` - темно-синий
- `&2` - темно-зеленый
- `&3` - темно-бирюзовый
- `&4` - темно-красный
- `&5` - темно-фиолетовый
- `&6` - золотой
- `&7` - серый
- `&8` - темно-серый
- `&9` - синий
- `&a` - зеленый
- `&b` - бирюзовый
- `&c` - красный
- `&d` - светло-фиолетовый
- `&e` - желтый
- `&f` - белый

А также форматирование:
- `&l` - жирный
- `&m` - зачеркнутый
- `&n` - подчеркнутый
- `&o` - курсив
- `&r` - сброс форматирования