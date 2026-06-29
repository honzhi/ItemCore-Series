# DEV-005: 元素伤害显示对接 IC ElementConfig

**日期**: 2026-06-02
**类型**: 功能完善
**影响范围**: ConfigManager.java, CombatListener.java

---

## 概述

让 RPG 的伤害飘字系统从 ItemCore 的 ElementConfig 读取元素图标和颜色，而非硬编码或维护 RPG 自己的元素配置副本。

---

## 改动详情

### 1. CombatListener.getDamageType()

优先返回元素类型，有元素时返回元素 ID（如 `liuhuo`），否则按伤害类型返回 `spell`/`physical`：

```java
ElementType element = event.getElement();
if (element != null && element != ElementType.NONE) {
    return element.getId().toLowerCase();
}
```

返回的元素 ID 会作为 `damageType` 传给 `formatDamage()`。

### 2. ConfigManager.getIcon() / getColor()

**之前**：只查 RPG 本地 config.yml 的 `damage_indicators.elements.<id>` 段（配置不存在时返回默认图标"❤"），导致元素伤害显示错误。

**现在**：先判断是否为元素类型（非 physical/spell/crit），是则调用 `ItemCoreAPI.getElementIcon(id.toUpperCase())` 从 IC 读取，IC 返回 null 时再 fallback 到 RPG 的本地配置。

| 场景 | 之前 icon | 现在 icon |
|------|-----------|-----------|
| type=liuhuo | "❤" (误) | "🔥" (elements.yml) |
| type=hanshuang | "❤" (误) | "❄" (elements.yml) |
| type=leizhe | "❤" (误) | "⚡" (elements.yml) |
| type=physical | "⚔" | "⚔" (不变) |
| type=spell | "✨" | "✨" (不变) |

---

## 删除

- RPG config.yml 中的 `damage_indicators.elements` 配置段已移除，元素显示数据由 IC 统一管理

---

## 变更文件

| 文件 | 变更 |
|------|------|
| `listener/CombatListener.java` | getDamageType 优先返回元素类型 |
| `config/ConfigManager.java` | getIcon/getColor 优先从 IC 的 ElementConfig 读取（调用 ItemCoreAPI） |

---

## 注意事项

- 依赖 ItemCore DEV-057 提供的 `ItemCoreAPI.getElementIcon()` / `getElementColor()`
- ItemCore 必须是软/硬依赖，否则运行时会抛出 ClassNotFoundException
- 元素的 id 在传递过程中做了大小写转换：RPG → `liuhuo`（低），IC API → `LIUHUO`（高）