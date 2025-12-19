# Implementation Plan

- [x] 1. Настройка проекта и Git



  - [x] 1.1 Создать структуру Gradle проекта

    - Создать build.gradle.kts с зависимостями Paper API, HikariCP, OkHttp
    - Создать settings.gradle.kts
    - Настроить Java 21 и shadowJar для сборки
    - _Requirements: 7.1_

  - [x] 1.2 Настроить Git и .gitignore

    - Создать .gitignore для Java/Gradle проекта
    - Инициализировать git репозиторий
    - Добавить remote https://git.lokili.xyz/loki/lorep.git
    - Сделать initial commit и push
    - _Requirements: N/A_
  - [x] 1.3 Создать plugin.yml и основной класс


    - Создать plugin.yml с командами и правами
    - Создать LorepPlugin.java с onEnable/onDisable
    - _Requirements: 6.1, 6.2, 6.3_

- [x] 2. Реализация конфигурации



  - [x] 2.1 Создать ConfigManager и config.yml

    - Создать default config.yml с настройками БД и webhook
    - Реализовать ConfigManager для чтения конфигурации
    - _Requirements: 7.1, 7.2, 7.3, 7.4_
  - [x] 2.2 Написать property test для парсинга конфигурации


    - **Property 9: Configuration Parsing**
    - **Validates: Requirements 7.1, 7.3, 7.4**

- [x] 3. Реализация базы данных


  - [x] 3.1 Создать модель Report и интерфейс DatabaseManager


    - Создать Report record
    - Создать интерфейс DatabaseManager
    - _Requirements: 2.1_

  - [x] 3.2 Реализовать SQLiteDatabaseManager

    - Реализовать все методы интерфейса для SQLite
    - Создать таблицу и индексы
    - _Requirements: 2.1, 2.2, 2.3_

  - [x] 3.3 Реализовать PostgreSQLDatabaseManager

    - Реализовать все методы с HikariCP connection pool
    - Создать таблицу и индексы для PostgreSQL
    - _Requirements: 2.1, 2.2, 2.3_
  - [x] 3.4 Написать property tests для DatabaseManager


    - **Property 1: Report Creation Integrity**
    - **Property 2: Duplicate Report Prevention**
    - **Property 3: Report Persistence Round-Trip**
    - **Property 4: Report Ordering**
    - **Property 7: Report Count Accuracy**
    - **Validates: Requirements 1.1, 1.2, 2.1, 2.2, 2.3, 5.1**

- [x] 4. Checkpoint - Проверка тестов БД



  - Ensure all tests pass, ask the user if questions arise.

- [x] 5. Реализация Discord Webhook


  - [x] 5.1 Создать DiscordWebhook сервис


    - Реализовать отправку embed сообщений через OkHttp
    - Обработать ошибки и отсутствие URL
    - _Requirements: 3.1, 3.2, 3.3_

  - [x] 5.2 Написать property test для webhook payload

    - **Property 5: Webhook Payload Completeness**
    - **Validates: Requirements 3.1**

- [x] 6. Реализация команд


  - [x] 6.1 Создать ReportCommand (/report [ник] [причина])


    - Валидация аргументов и прав
    - Проверка на самого себя и существование игрока
    - Проверка на дубликат репорта
    - Сохранение в БД и отправка в webhook
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 6.1_
  - [x] 6.2 Создать ReportStatsCommand (/report stats [ник])


    - Показ количества репортов
    - Показ последнего онлайна
    - Список последних репортов
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 6.3_

  - [x] 6.3 Написать property test для проверки прав

    - **Property 8: Permission Enforcement**
    - **Validates: Requirements 6.1, 6.2, 6.3**

- [ ] 7. Реализация GUI
  - [ ] 7.1 Создать ReportGui с пагинацией
    - Создать инвентарь 54 слота
    - Отображение голов игроков с их скинами
    - Lore с информацией о репорте
    - Кнопки навигации по страницам
    - _Requirements: 4.1, 4.2, 4.3, 4.4_
  - [ ] 7.2 Создать GuiClickListener
    - Обработка кликов по страницам
    - Отмена выноса предметов
    - _Requirements: 4.4_
  - [ ] 7.3 Создать ReportGuiCommand (/report gui)
    - Проверка прав и открытие GUI
    - _Requirements: 4.1, 6.2_
  - [ ] 7.4 Написать property test для пагинации
    - **Property 6: Pagination Correctness**
    - **Validates: Requirements 4.4**

- [ ] 8. Утилиты и сообщения
  - [ ] 8.1 Создать TimeUtil для форматирования времени
    - Форматирование "X минут/часов/дней назад"
    - _Requirements: 4.3, 5.2_
  - [ ] 8.2 Создать MessageUtil для цветных сообщений
    - Парсинг цветовых кодов
    - Отправка сообщений игрокам
    - _Requirements: 1.2, 1.3, 1.4, 5.4, 6.1, 6.2, 6.3_

- [ ] 9. Интеграция и финализация
  - [ ] 9.1 Интегрировать все компоненты в LorepPlugin
    - Инициализация всех менеджеров в onEnable
    - Регистрация команд и листенеров
    - Корректное закрытие в onDisable
    - _Requirements: 2.2, 7.1_
  - [ ] 9.2 Финальный push в Git
    - Commit всех изменений
    - Push в репозиторий
    - _Requirements: N/A_

- [ ] 10. Final Checkpoint - Финальная проверка
  - Ensure all tests pass, ask the user if questions arise.
