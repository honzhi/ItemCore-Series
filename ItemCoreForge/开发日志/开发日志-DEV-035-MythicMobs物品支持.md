# ================================================
# 开发日志 - DEV-035
# MythicMobs 物品支持
# ================================================

## 更新日期
2026-05-29

## 更新内容

### 1. 功能添加

添加对 MythicMobs 插件物品的完整支持，允许在锻造台中使用 MythicMobs 物品作为材料或输出。

### 2. 新增的物品来源

| 来源 | 格式 | 示例 |
|------|------|------|
| `mythicmobs` | `mythicmobs:item_id*amount` | `mythicmobs:SkeletonKingSword*1` |

### 3. 代码修改

| 文件 | 修改内容 |
|------|----------|
| `ItemReference.java` | 添加 `mythicmobs` 来源解析，支持两种 API 调用方式，添加 `isMythicMobs()` 方法 |
| `MaterialChecker.java` | 添加对 MythicMobs 物品的计数和检查逻辑 |
| `CraftScheduler.java` | 修改领取逻辑，使用 `ItemReference.toItemStack()` 统一处理所有来源 |
| `CraftingQueueManager.java` | 修改领取逻辑，使用 `ItemReference.toItemStack()` 统一处理所有来源 |
| `ItemCoreForge.java` | 添加 MythicMobs 插件检测和启动日志 |
| `plugin.yml` | 添加 `MythicMobs` 到 `softdepend` |
| `example_template.yml` | 添加 MythicMobs 物品配置示例 |
| `RecipeSelectGUI.java` | 添加 null 检查，防止物品 meta 为空导致报错 |
| `CustomForgeGUI.java` | 添加 null 检查，防止队列显示时 meta 为空导致报错 |

### 4. MythicMobs API 调用方式

支持两种 MythicMobs API 调用方式，以兼容不同版本：

**方式1：直接获取 ItemStack（旧版 API）**
```java
ItemStack item = mythicMobs.getItemManager().getItemStack("item_id");
```

**方式2：通过 Optional 和生成（新版 API）**
```java
Optional<MythicItem> opt = mythicMobs.getItemManager().getItem("item_id");
if (opt.isPresent()) {
    AbstractItemStack aItem = opt.get().generateItemStack();
    ItemStack item = aItem.build();  // 或 aItem.asBukkit()
}
```

### 5. 占位物品提示

当 MythicMobs 物品不存在时，会显示红色屏障方块作为占位符，提示具体原因：
- 未找到 MythicMobs 物品: xxx
- 获取 MythicMobs 物品失败: 错误信息

### 6. 配置示例

```yaml
recipes:
  # MythicMobs 物品配方
  mythic_sword:
    output:
      source: "mythicmobs"
      id: "SkeletonKingSword"
      amount: 1
      check-durability: false
    
    materials:
      - source: "mythicmobs"
        id: "MythicIngot"
        amount: 3
      - source: "vanilla"
        id: "STICK"
        amount: 1
    
    craft-time: 10.0
    success-rate: 100
    consume-on-fail: true
```

### 7. 软依赖处理

- 即使没有安装 MythicMobs 插件，ItemCoreForge 也能正常启动
- 无 MythicMobs 时，仅支持 vanilla 和 itemcore 来源的物品
- 启动时会在日志中提示 MythicMobs 是否检测到

## 兼容性

- 完全向后兼容，现有配方无需修改
- 支持的 MythicMobs API 版本：5.x 及兼容版本
