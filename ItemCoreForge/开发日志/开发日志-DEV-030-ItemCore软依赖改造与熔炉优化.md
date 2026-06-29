# 开发日志 - DEV-030 - ItemCore 软依赖改造与熔炉优化

**开发日期**: 2026-05-28  
**开发阶段**: Phase 30  
**开发状态**: ✅ 已完成

---

## 📋 开发目标

1. 实施 ItemCore 软依赖改造
2. 移除熔炉配方的条件检测（逻辑不合理）
3. 确认 crafting_table 条件检测逻辑正确

---

## 🔧 修改内容

### 1. ItemCore 软依赖改造

#### 1.1 修改 plugin.yml

```yaml
# 修改前
depend: [ItemCore]

# 修改后
softdepend: [ItemCore]
```

#### 1.2 修改 ItemCoreForge.java - 启动检查

```java
// 修改前 - 强制禁用插件
if (!checkItemCore()) {
    getLogger().severe("未找到 ItemCore 插件，请确保已安装 ItemCore！");
    getServer().getPluginManager().disablePlugin(this);
    return;
}

// 修改后 - 仅警告，插件继续运行
if (!checkItemCore()) {
    getLogger().warning("未检测到 ItemCore 插件，将仅支持 vanilla 物品！");
}
```

#### 1.3 改造后的效果

| 场景 | 行为 |
|------|------|
| **有 ItemCore** | 全部功能正常，支持 `source: "itemcore"` 和 `source: "vanilla"` |
| **无 ItemCore** | 插件正常启动，仅支持 `source: "vanilla"`，使用 ItemCore 物品的配方会自动跳过 |

---

### 2. 移除熔炉条件检测

#### 2.1 问题分析

**原实现问题：**

```java
// 检测熔炉周围5格范围内的所有玩家
for (Entity entity : furnaceBlock.getWorld().getNearbyEntities(
        furnaceBlock.getLocation(), 5, 5, 5)) {
    if (entity instanceof Player) {
        // 问题：无法确定是"哪个玩家"在使用熔炉
    }
}
```

**核心问题：**

| 问题 | 说明 |
|------|------|
| 无法确定操作玩家 | 熔炉事件不关联特定玩家，任何人都可以点燃熔炉 |
| 多人附近逻辑混乱 | 如果有一个玩家满足条件，其他玩家即使不满足也能让熔炉工作 |
| 消息发送错误 | 条件不满足时，消息会发给所有附近的玩家 |

#### 2.2 解决方案

完全移除熔炉配方的条件检测功能，原因：

| 类型 | 事件 | 能否确定玩家 | 是否适合条件检测 |
|------|------|-------------|-----------------|
| **CraftingTable** | `CraftItemEvent` | ✅ 能（点击的玩家） | ✅ 适合 |
| **Furnace** | `FurnaceSmeltEvent` | ❌ 不能 | ❌ 不适合 |

#### 2.3 修改的文件

**FurnaceRecipeManager.java - 移除事件监听器**

```java
// 修改前
public class FurnaceRecipeManager implements Listener {
    private final ConditionChecker conditionChecker = new ConditionChecker();
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        // ... 条件检测逻辑 ...
    }
    
    public void registerEvents() {
        // ...
    }
}

// 修改后
public class FurnaceRecipeManager {
    // 不再实现 Listener 接口
    // 移除 ConditionChecker、事件监听器和 registerEvents 方法
}
```

**FurnaceHandler.java - 移除事件注册**

```java
// 修改前
this.recipeManager.registerEvents();

// 修改后
// 移除该行
```

**清理的 import：**
- `com.minemart.itemcoreforge.utils.ConditionChecker`
- `com.minemart.itemcoreforge.utils.MessageUtil`
- `org.bukkit.Keyed`
- `org.bukkit.event.EventHandler`
- `org.bukkit.event.EventPriority`
- `org.bukkit.event.Listener`
- `org.bukkit.event.inventory.FurnaceSmeltEvent`
- `org.bukkit.entity.Player`
- `org.bukkit.inventory.FurnaceInventory`
- `org.bukkit.inventory.Recipe`
- `org.bukkit.plugin.PluginManager`

---

### 3. CraftingTable 条件检测确认

#### 3.1 正确的实现

```java
@EventHandler(priority = EventPriority.HIGHEST)
public void onCraftItem(CraftItemEvent event) {
    // ✅ 直接从事件获取操作的玩家
    if (!(event.getView().getPlayer() instanceof Player player)) {
        return;
    }
    
    // ... 配方识别逻辑 ...
    
    // ✅ 对"这个玩家"进行条件检查
    if (!conditionChecker.allConditionsMet(player, forgeRecipe.getConditions())) {
        event.setCancelled(true);
        MessageUtil.sendMessage(player, "conditions-not-met");
        return;
    }
    // ...
}
```

#### 3.2 支持的条件类型

```yaml
recipes:
  my_recipe:
    # ... 输入输出配置 ...
    
    conditions:
      - type: "level"        # 玩家等级
        value: 10
      - type: "permission"   # 权限节点
        node: "my.permission"
      - type: "money"        # 经济需求（需要Vault）
        amount: 500
```

#### 3.3 各类型对比

| 特性 | CraftingTable | Furnace |
|------|---------------|---------|
| **事件类型** | `CraftItemEvent`（玩家点击） | `FurnaceSmeltEvent`（熔炉完成） |
| **能否获取玩家** | ✅ `event.getView().getPlayer()` | ❌ 事件不关联特定玩家 |
| **条件检查对象** | 明确的"操作玩家" | 不确定（周围所有玩家） |
| **条件检测状态** | ✅ 保留 | ❌ 已移除 |

---

## 📋 开发流程

1. ✅ 修改 plugin.yml：depend → softdepend
2. ✅ 修改 ItemCoreForge.java：移除强制禁用逻辑
3. ✅ 分析熔炉条件检测逻辑的问题
4. ✅ 移除 FurnaceRecipeManager 的条件检测
5. ✅ 确认 CraftingTable 条件检测逻辑正确
6. ✅ 构建插件验证

---

## 🔗 已修改的文件

- `src/main/resources/plugin.yml`
- `src/main/java/com/minemart/itemcoreforge/ItemCoreForge.java`
- `src/main/java/com/minemart/itemcoreforge/recipe/FurnaceRecipeManager.java`
- `src/main/java/com/minemart/itemcoreforge/type/furnace/FurnaceHandler.java`

---

## 🚀 构建结果

- JAR 位置: `target/ItemCoreForge-1.0.0.jar`
- 构建状态: ✅ 成功

---

## 💡 后续扩展方向

- **添加更多配方类型**：如 `smithing_table`（锻造台）、`stonecutter`（切石机）
- **配方冲突检测**：当多个配方使用相同输入时给出警告
- **调试模式优化**：更详细的配方加载日志

---

*开发者: Trae AI*  
*文档版本: 1.0.0*
