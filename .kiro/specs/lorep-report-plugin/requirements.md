# Requirements Document

## Introduction

Плагин "lorep" для Paper 1.21 — система репортов игроков с интеграцией Discord webhook и базой данных. Позволяет игрокам отправлять жалобы на других игроков, а администраторам — управлять и просматривать репорты через GUI и команды.

## Glossary

- **Report_System**: Основная система обработки репортов плагина lorep
- **Player**: Обычный игрок сервера с базовыми правами
- **Admin**: Администратор сервера с расширенными правами
- **Reporter**: Игрок, отправляющий репорт
- **Target**: Игрок, на которого отправлен репорт
- **Webhook**: Discord webhook для отправки уведомлений
- **GUI**: Графический интерфейс в виде инвентаря Minecraft
- **Cooldown**: Ограничение на повторную отправку репорта на того же игрока

## Requirements

### Requirement 1: Отправка репортов игроками

**User Story:** As a Player, I want to report other players for rule violations, so that administrators can review and take action.

#### Acceptance Criteria

1. WHEN a Player executes `/report [nickname] [reason]` command THEN the Report_System SHALL create a new report record with reporter name, target name, reason, and timestamp
2. WHEN a Player attempts to report a Target they have already reported THEN the Report_System SHALL reject the report and display a cooldown message
3. WHEN a Player attempts to report themselves THEN the Report_System SHALL reject the report and display an error message
4. WHEN a Player attempts to report a non-existent player THEN the Report_System SHALL reject the report and display a player not found message
5. WHEN a report is successfully created THEN the Report_System SHALL send a notification to the configured Discord webhook

### Requirement 2: Хранение данных репортов

**User Story:** As a system administrator, I want reports to be stored persistently, so that report history is preserved across server restarts.

#### Acceptance Criteria

1. WHEN a new report is created THEN the Report_System SHALL store the report in the database with reporter UUID, target UUID, reason, and creation timestamp
2. WHEN the server starts THEN the Report_System SHALL load existing reports from the database
3. WHEN querying reports THEN the Report_System SHALL return reports sorted by creation timestamp in descending order

### Requirement 3: Discord интеграция

**User Story:** As an Admin, I want to receive Discord notifications about new reports, so that I can respond quickly to player issues.

#### Acceptance Criteria

1. WHEN a new report is created THEN the Report_System SHALL send an embed message to the Discord webhook containing reporter name, target name, reason, and timestamp
2. WHEN the Discord webhook URL is not configured THEN the Report_System SHALL log a warning and continue without sending notifications
3. WHEN the Discord webhook request fails THEN the Report_System SHALL log the error and continue normal operation

### Requirement 4: GUI для администраторов

**User Story:** As an Admin, I want to view all reports in a graphical interface, so that I can easily browse and manage player reports.

#### Acceptance Criteria

1. WHEN an Admin executes `/report gui` command THEN the Report_System SHALL open an inventory GUI displaying report entries
2. WHEN displaying a report entry THEN the Report_System SHALL show a player head with the Target's skin
3. WHEN an Admin hovers over a report entry THEN the Report_System SHALL display target nickname, reporter nickname, reason, time since report creation, and total report count for the target
4. WHEN there are more reports than fit on one page THEN the Report_System SHALL provide pagination controls

### Requirement 5: Статистика репортов

**User Story:** As an Admin, I want to view report statistics for specific players, so that I can identify problematic players.

#### Acceptance Criteria

1. WHEN an Admin executes `/report stats [nickname]` command THEN the Report_System SHALL display total report count for the specified player
2. WHEN displaying stats THEN the Report_System SHALL show the player's last online timestamp
3. WHEN displaying stats THEN the Report_System SHALL list recent reports with reasons and timestamps
4. WHEN the specified player has no reports THEN the Report_System SHALL display a message indicating zero reports

### Requirement 6: Система прав

**User Story:** As a server owner, I want granular permission control, so that I can configure who can use which features.

#### Acceptance Criteria

1. WHEN a Player without `lorep.report` permission attempts to use `/report` command THEN the Report_System SHALL deny access and display a no permission message
2. WHEN a user without `lorep.admin` permission attempts to use `/report gui` command THEN the Report_System SHALL deny access and display a no permission message
3. WHEN a user without `lorep.admin` permission attempts to use `/report stats` command THEN the Report_System SHALL deny access and display a no permission message

### Requirement 7: Конфигурация плагина

**User Story:** As a server owner, I want to configure plugin settings, so that I can customize the plugin behavior for my server.

#### Acceptance Criteria

1. WHEN the plugin loads THEN the Report_System SHALL read configuration from config.yml file
2. WHEN config.yml does not exist THEN the Report_System SHALL create a default configuration file
3. WHEN configuration includes database settings THEN the Report_System SHALL use those settings for database connection
4. WHEN configuration includes webhook URL THEN the Report_System SHALL use that URL for Discord notifications
