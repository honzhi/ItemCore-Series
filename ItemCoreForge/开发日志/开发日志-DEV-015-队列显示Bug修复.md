# 开发日志 - DEV-015 - 队列显示 Bug 修复

**开发日期**: 2026-05-27  
**开发阶段**: Phase 15  
**开发状态**: ✅ 已完成

---

## 📋 开发目标

修复队列显示的 Bug，并简化队列显示逻辑：
1. 修复 QueueDisplayGUI 的槽位映射问题
2. 删除队列中未制作物品的等待时间显示，只显示"等待中"
3. 只有制作中的物品才显示剩余时间

---

## 🔧 问题描述

### Bug 1: QueueDisplayGUI 槽位映射问题

**问题分析**:
- `QueueDisplayGUI` 虽然从布局文件中读取了队列槽位顺序（`queueSlots`），但在实际显示时没有正确使用这些槽位信息
- 代码设计过于复杂，引入了不必要的布局映射逻辑
- `QueueDisplayGUI` 是独立的 6 行 54 槽 GUI，应该从槽位 0 开始顺序显示队列物品

### Bug 2: 等待时间显示过于复杂

**问题分析**:
- 队列中等待的物品显示"预计等待时间"，但计算方式不准确（使用当前 recipe 的制作时间，而不是前面所有任务的制作时间之和）
- 用户反馈不需要显示等待中的物品的预计等待时间
- 应该简化显示：只有制作中的物品显示剩余时间，等待中的物品只显示"等待中"

---

## 🛠 修复内容

### 1. 修复 QueueDisplayGUI

**文件**: `gui/QueueDisplayGUI.java`

#### 修改 render() 方法

```java
// 修复前
private void render() {
    // ...
    // 使用配方 GUI 中的队列槽位顺序显示
    for (int slotIndex = 0; slotIndex < queueSlots.size(); slotIndex++) {
        int guiSlot = slotIndex;  // ❌ 逻辑混乱
        // ...
    }
    // 返回按钮位置计算复杂
    int backSlot = queueSlots.isEmpty() ? 0 : (queueSlots.size() < 54 ? queueSlots.size() : 53);
}

// 修复后
private void render() {
    // ...
    // 从槽位 0 开始顺序显示到槽位 52
    for (int slotIndex = 0; slotIndex < 53; slotIndex++) {
        CraftTask task = null;
        if (slotIndex < allTasks.size()) {
            task = allTasks.get(slotIndex);
        }
        
        if (task != null) {
            Forge.Recipe recipe = forge.getRecipe(task.getRecipeId());
            if (recipe != null) {
                ItemStack item = createQueueItem(recipe, task, slotIndex);
                inventory.setItem(slotIndex, item);
            }
        } else {
            ItemStack item = createEmptySlot(slotIndex);
            inventory.setItem(slotIndex, item);
        }
    }
    
    // 返回按钮固定在槽位 53（最后一个槽位）
    setItem(53, backButton, event -> {
        close();
        RecipeSelectGUI recipeGUI = new RecipeSelectGUI(plugin, player, forge);
        recipeGUI.open();
    });
}
```

#### 修改 createQueueItem() 方法

```java
// 修复前
private ItemStack createQueueItem(Forge.Recipe recipe, CraftTask task, int index, List<CraftTask> allTasks) {
    // ...
    if (index == 0) {
        meta.displayName(MessageUtil.toComponent("&a正在制作"));
    } else {
        meta.displayName(MessageUtil.toComponent("&e等待 #" + (index + 1)));  // ❌ 显示编号
    }
    // ...
    if (index == 0) {
        lore.add(MessageUtil.toComponent("&7剩余时间: &e" + ... + "秒"));
    } else {
        // ❌ 计算并显示预计等待时间
        double waitTime = 0;
        for (int i = 1; i <= index; i++) {
            // ... 复杂的计算逻辑
        }
        lore.add(MessageUtil.toComponent("&7预计等待: &e" + ... + "秒"));
    }
}

// 修复后
private ItemStack createQueueItem(Forge.Recipe recipe, CraftTask task, int index) {
    // ...
    if (index == 0) {
        meta.displayName(MessageUtil.toComponent("&a正在制作"));
    } else {
        meta.displayName(MessageUtil.toComponent("&e等待中"));  // ✅ 统一显示"等待中"
    }
    // ...
    if (index == 0) {
        lore.add(MessageUtil.toComponent("&7剩余时间: &e" + ... + "秒"));
    }
    // ✅ 删除等待中物品的预计等待时间显示
}
```

### 2. 修改 CustomForgeGUI

**文件**: `gui/CustomForgeGUI.java`

#### 修改 renderQueueDisplay() 方法

```java
// 修复前
if (queueIndex == 0) {
    meta.displayName(MessageUtil.toComponent("&a正在制作"));
} else {
    meta.displayName(MessageUtil.toComponent("&e等待中 " + (queueIndex + 1)));  // ❌ 显示编号
}

if (queueIndex == 0) {
    lore.add(MessageUtil.toComponent("&7剩余时间: &e" + ... + "秒"));
} else {
    lore.add(MessageUtil.toComponent("&7预计等待: &e" + (queueIndex * recipe.getCraftTime()) + "秒"));  // ❌ 计算不准确
}

// 修复后
if (queueIndex == 0) {
    meta.displayName(MessageUtil.toComponent("&a正在制作"));
} else {
    meta.displayName(MessageUtil.toComponent("&e等待中"));  // ✅ 统一显示"等待中"
}

if (queueIndex == 0) {
    lore.add(MessageUtil.toComponent("&7剩余时间: &e" + ... + "秒"));
}
// ✅ 删除等待中物品的预计等待时间显示
```

---

## ✅ 修复效果

### QueueDisplayGUI 显示效果

| 槽位 | 状态 | 显示内容 |
|------|------|----------|
| 0 | 正在制作 | `&a正在制作` + 物品信息 + `&7剩余时间: X.X秒` |
| 1 | 等待中 | `&e等待中` + 物品信息 |
| 2 | 等待中 | `&e等待中` + 物品信息 |
| ... | ... | ... |
| 52 | 空槽位 | `&7空槽位` |
| 53 | 返回按钮 | `&a返回配方列表` |

### CustomForgeGUI 队列显示效果

| 队列索引 | 状态 | 显示内容 |
|---------|------|----------|
| 0 | 正在制作 | `&a正在制作` + 物品信息 + `&7剩余时间: X.X秒` |
| 1 | 等待中 | `&e等待中` + 物品信息 |
| 2 | 等待中 | `&e等待中` + 物品信息 |
| ... | ... | ... |

---

## 📊 修改总结

### 修改的文件

| 文件 | 修改内容 |
|------|----------|
| `gui/QueueDisplayGUI.java` | 修复槽位映射，简化显示逻辑，删除预计等待时间 |
| `gui/CustomForgeGUI.java` | 删除预计等待时间显示，统一显示"等待中" |

### 简化的设计

1. **QueueDisplayGUI**: 独立的 6 行 54 槽 GUI
   - 槽位 0-52: 显示队列物品（共 53 个槽位）
   - 槽位 53: 固定显示返回按钮
   - 从左到右、从上到下顺序显示

2. **显示规则**:
   - 第一个物品（index == 0）: 显示"正在制作" + 剩余时间
   - 其他物品（index > 0）: 只显示"等待中"，不显示预计等待时间

---

## 🔗 相关文件

- `src/main/java/com/minemart/itemcoreforge/gui/QueueDisplayGUI.java`
- `src/main/java/com/minemart/itemcoreforge/gui/CustomForgeGUI.java`

---

## 🚀 验证步骤

1. 启动服务器，加载 ItemCoreForge 插件
2. 打开锻造台，选择配方开始制作
3. 在制作进行中时，再添加几个配方到队列
4. 打开队列显示界面，验证：
   - 第一个物品显示"正在制作"和剩余时间
   - 其他物品显示"等待中"，不显示预计等待时间
   - 返回按钮在右下角（槽位 53）

---

*开发者: Trae AI*  
*文档版本: 1.0.0*
