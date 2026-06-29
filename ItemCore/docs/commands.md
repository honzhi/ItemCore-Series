# 命令系统

## 主命令 /ic

| 子命令 | 用途 | 权限 |
|--------|------|------|
| `open` | 打开物品库 GUI | `itemcore.command.gui` |
| `gui` | 同上（兼容） | `itemcore.command.gui` |
| `give <玩家> <物品> [数量]` | 给予物品 | `itemcore.command.give` |
| `list [分类]` | 列出物品 | `itemcore.command.list` |
| `reload` | 热重载配置 | `itemcore.command.reload` |
| `info <物品>` | 物品详情 | `itemcore.command.info` |
| `help` | 帮助 | `itemcore.command.help` |

### 示例

```
/ic open
/ic give Is_Lianhua iron_sword 1
/ic reload
/ic list weapons
```

## RPG 命令 /itemcorerpg

| 子命令 | 用途 |
|--------|------|
| `stats [玩家]` | 打开玩家信息 GUI |
| `reload` | 重载配置 |