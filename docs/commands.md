# Команды и права

## Команды

### `/report <игрок> <причина>`
Отправить репорт на игрока.

**Примеры:**
```
/report Griefer123 Гриферство на спавне
/report Cheater456 Использует читы
```

### `/report gui`
Открыть GUI со списком всех репортов (только для администраторов).

### `/report stats <игрок>`
Показать статистику репортов на конкретного игрока (только для администраторов).

**Пример:**
```
/report stats Griefer123
```

## Права доступа

| Право | Описание | По умолчанию |
|-------|----------|--------------|
| `lorep.report` | Позволяет отправлять репорты | `true` |
| `lorep.admin` | Доступ к GUI и статистике | `op` |

## Настройка прав

### LuckPerms
```
/lp group moderator permission set lorep.admin true
/lp group default permission set lorep.report true
```

### PermissionsEx
```
/pex group moderator add lorep.admin
/pex group default add lorep.report
```

### GroupManager
```yaml
groups:
  moderator:
    permissions:
    - lorep.admin
  default:
    permissions:
    - lorep.report
```