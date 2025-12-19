# lorep

Плагин системы репортов для Paper 1.21

## Возможности

- `/report [ник] [причина]` — отправить репорт на игрока
- `/report gui` — GUI со списком репортов (головы игроков с информацией)
- `/report stats [ник]` — статистика репортов на игрока
- Discord webhook уведомления о новых репортах
- Поддержка SQLite и PostgreSQL

## Права

| Право | Описание | По умолчанию |
|-------|----------|--------------|
| `lorep.report` | Отправка репортов | true |
| `lorep.admin` | GUI и статистика | op |

## Установка

1. Скачай `lorep-1.0.0.jar` из [Releases](https://git.lokili.xyz/loki/lorep/releases)
2. Положи в папку `plugins/`
3. Перезапусти сервер
4. Настрой `plugins/lorep/config.yml`

## Конфигурация

```yaml
# Discord Webhook URL
webhook-url: "https://discord.com/api/webhooks/..."

# База данных
database:
  type: "sqlite"  # sqlite или postgresql
  
  sqlite:
    file: "reports.db"
  
  postgresql:
    host: "localhost"
    port: 5432
    database: "lorep"
    username: "lorep"
    password: "password"
    pool-size: 10

# Сообщения
messages:
  report-sent: "&aРепорт успешно отправлен!"
  already-reported: "&cВы уже отправляли репорт на этого игрока!"
  # ...
```

## Сборка

```bash
./gradlew shadowJar
```

Jar будет в `build/libs/lorep-1.0.0.jar`

## Требования

- Paper 1.21+
- Java 21

## Автор

loki
