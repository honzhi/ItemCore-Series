# 开发日志 - DEV-026 - crafting_table 类型实现

**开发日期**: 2026-05-27  
**开发阶段**: Phase 26  
**开发状态**: ✅ 已完成

---

## 📋 开发目标

1. 删除 `default` 类型相关代码和注释
2. 新增 `crafting_table` 类型，实现原版工作台配方功能
3. `crafting_table` 类型特性：
   - 不能使用自定义布局（无视 `layout-file` 配置）
   - 不能使用 GUI 打开（无视 `display-name`, `max-queue-size`, `craft-time` 等 custom 相关配置）
   - 不能用 `/icf gui` 指令打开
   - 直接在原版工作台中添加配方
   - 支持 `exact_placement` 选项（精确摆放位置）
   - 材料可以指定 `slot` 字段（槽位位置）

---

## 🔧 修改内容

### 1. Forge.java - 数据模型扩展

**文件**: `core/Forge.java`

#### 新增字段

```java
public class Forge {
    // 新增字段
    private final boolean exactPlacement;

    // 构造函数新增参数
    public Forge(String forgeId, String displayName, int maxQueueSize, 
                 String type, String layoutFile, boolean exactPlacement) {
        // ...
        this.exactPlacement = exactPlacement;
    }

    // 新增方法
    public boolean isExactPlacement() {
        return exactPlacement;
    }

    public boolean isCustomType() {
        return "custom".equalsIgnoreCase(type);
    }

    public boolean isCraftingTableType() {
        return "crafting_table".equalsIgnoreCase(type);
    }

    // ItemReference 新增 slot 字段
    public static class ItemReference {
        private int slot = -1;

        public int getSlot() { return slot; }
        public void setSlot(int slot) { this.slot = slot; }
        public boolean hasSlot() { return slot >= 0; }
    }
}
```

---

### 2. ForgeLoader.java - 配置加载

**文件**: `config/ForgeLoader.java`

#### 修改内容

```java
private Forge loadForge(File file) {
    // ...
    String type = config.getString("settings.type", "custom");
    String layoutFile = config.getString("settings.layout-file", "Default.yml");
    boolean exactPlacement = config.getBoolean("settings.exact_placement", false);
    
    Forge forge = new Forge(forgeId, displayName, maxQueueSize, type, layoutFile, exactPlacement);
    // ...
}

private Forge.ItemReference loadItemReferenceFromMap(Map<?, ?> map) {
    Forge.ItemReference ref = new Forge.ItemReference();
    // ...
    ref.setSlot(getInt(map, "slot", -1));  // 新增
    return ref;
}
```

---

### 3. CraftingTableRecipeManager.java - 工作台配方管理器（新增）

**文件**: `recipe/CraftingTableRecipeManager.java`

#### 功能列表

| 功能 | 说明 |
|------|------|
| 注册配方 | 将配置中的配方注册到原版工作台 |
| 条件检查 | 制作前检查玩家是否满足条件（等级、权限、经济） |
| 材料检查 | 检查玩家背包是否有足够材料 |
| 精确摆放 | 支持 `exact_placement: true` 时指定材料位置 |
| 事件监听 | 监听 `CraftItemEvent`，处理自定义配方制作 |

#### 核心逻辑

**注册配方**：

```java
public void registerForgeRecipes(Forge forge) {
    if (!forge.isCraftingTableType()) {
        return;
    }

    for (Forge.Recipe recipe : forge.getRecipes().values()) {
        NamespacedKey key = new NamespacedKey(plugin, forge.getForgeId() + "_" + recipe.getRecipeId());
        ItemStack result = resolveItemStack(recipe.getOutput());

        if (forge.isExactPlacement()) {
            registerShapedRecipe(forge, recipe, key, result);  // 有序配方
        } else {
            registerShapelessRecipe(forge, recipe, key, result);  // 无序配方
        }
    }
}
```

**精确摆放配方**：

```java
private void registerShapedRecipe(Forge forge, Forge.Recipe recipe, NamespacedKey key, ItemStack result) {
    ShapedRecipe shapedRecipe = new ShapedRecipe(key, result);
    
    // 根据 slot 字段生成形状
    Map<Integer, Forge.ItemReference> slotMaterials = new HashMap<>();
    for (Forge.ItemReference material : recipe.getMaterials()) {
        if (material.hasSlot()) {
            slotMaterials.put(material.getSlot(), material);
        }
    }

    // 生成 3x3 形状
    String[] shape = new String[3];
    for (int row = 0; row < 3; row++) {
        StringBuilder rowBuilder = new StringBuilder();
        for (int col = 0; col < 3; col++) {
            int slot = row * 3 + col;
            Forge.ItemReference mat = slotMaterials.get(slot);
            if (mat != null) {
                rowBuilder.append('A' + materialIndex);
            } else {
                rowBuilder.append(' ');
            }
        }
        shape[row] = rowBuilder.toString();
    }

    shapedRecipe.shape(shape);
    Bukkit.addRecipe(shapedRecipe);
}
```

**制作事件处理**：

```java
@EventHandler
public void onCraftItem(CraftItemEvent event) {
    // 检查是否是我们注册的配方
    if (!key.getNamespace().equals(plugin.getName().toLowerCase())) {
        return;
    }

    // 检查条件
    if (!conditionChecker.allConditionsMet(player, forgeRecipe.getConditions())) {
        event.setCancelled(true);
        MessageUtil.sendMessage(player, "conditions-not-met");
        return;
    }

    // 检查材料
    if (!materialChecker.hasMaterials(player, forgeRecipe.getMaterials())) {
        event.setCancelled(true);
        MessageUtil.sendMessage(player, "materials-insufficient");
        return;
    }

    // 手动处理制作（避免原版复制问题）
    event.setCancelled(true);
    materialChecker.consumeMaterials(player, forgeRecipe.getMaterials());
    materialChecker.giveItem(player, forgeRecipe.getOutput());
    MessageUtil.sendMessage(player, "crafting-complete", "result", forgeRecipe.getOutput().getId());
}
```

---

### 4. ItemCoreForge.java - 集成

**文件**: `ItemCoreForge.java`

#### 修改内容

```java
public class ItemCoreForge extends JavaPlugin implements Listener {
    private CraftingTableRecipeManager craftingTableRecipeManager;
    private MaterialChecker materialChecker;

    private void initManagers() {
        materialChecker = new MaterialChecker();
        craftingTableRecipeManager = new CraftingTableRecipeManager(this);
        craftingTableRecipeManager.setMaterialChecker(materialChecker);
    }

    private void loadConfigs() {
        // ... 加载配置
        // 注册工作台配方
        for (Forge forge : forgeLoader.getAllForges()) {
            if (forge.isCraftingTableType()) {
                craftingTableRecipeManager.registerForgeRecipes(forge);
            }
        }
    }

    public void reload() {
        // 重载时重新注册
        craftingTableRecipeManager.unregisterAll();
        for (Forge forge : forgeLoader.getAllForges()) {
            if (forge.isCraftingTableType()) {
                craftingTableRecipeManager.registerForgeRecipes(forge);
            }
        }
    }
}
```

---

### 5. 命令过滤

**文件**: `command/ForgeCommand.java` 和 `command/ForgeTabCompleter.java`

```java
// ForgeCommand - 列表和打开时过滤 crafting_table 类型
private void listForges(CommandSender sender) {
    for (String forgeId : plugin.getForgeLoader().getForgeIds()) {
        Forge forge = plugin.getForgeLoader().getForge(forgeId);
        if (forge != null && !forge.isCraftingTableType()) {  // 过滤
            sender.sendMessage("§7- §e" + forgeId);
        }
    }
}

private void openForge(Player player, String forgeId) {
    // ...
    if (forge.isCraftingTableType()) {  // 不允许用指令打开
        MessageUtil.sendMessage(player, "forge-not-found", "forge", forgeId);
        return;
    }
}
```

---

### 6. MaterialChecker.java - 新增方法

**文件**: `utils/MaterialChecker.java`

```java
public void giveItem(Player player, Forge.ItemReference materialRef) {
    // 给予单个物品
}

public ItemStack resolveItem(Forge.ItemReference materialRef) {
    // 解析物品为 ItemStack
}
```

---

### 7. 配置文件更新

**文件**: `forges/example_template.yml`

```yaml
# 锻造台设置
settings:
  max-queue-size: 6
  
  # 锻造台类型
  # custom: 使用自定义布局文件（需要 GUI 界面）
  # crafting_table: 原版工作台配方（直接在原版工作台中使用）
  # furnace: 熔炉类型（暂未开发）
  # brewing_stand: 酿造台类型（暂未开发）
  type: custom
  
  # 使用的布局文件（仅 type 为 custom 时生效）
  layout-file: "Default.yml"
  
  # 是否使用精确放置（仅 type 为 crafting_table 时生效）
  exact_placement: false
```

---

## 📊 配置文件对比

### custom 类型

```yaml
forge-id: "example_template"
display-name: "&6示例锻造台"
settings:
  type: custom
  max-queue-size: 6
  layout-file: "Default.yml"
```

**特性**：
- 可以用 `/icf gui example_template` 打开
- 有 GUI 界面
- 支持队列系统
- 支持制作时间

### crafting_table 类型

```yaml
forge-id: "example_crafting_table"
settings:
  type: crafting_table
  exact_placement: true
```

**特性**：
- 无视 `display-name`, `max-queue-size`, `layout-file`, `craft-time` 等配置
- 不能用 `/icf gui` 指令打开
- 不会在 `/icf list` 中显示
- 直接在原版工作台中添加配方
- 支持条件检查（等级、权限、经济）

---

## 📋 开发流程

1. ✅ 扩展 Forge 数据模型，添加 `exactPlacement` 字段和 `slot` 字段
2. ✅ 修改 ForgeLoader，加载新配置
3. ✅ 创建 CraftingTableRecipeManager，实现配方注册和事件监听
4. ✅ 修改 ItemCoreForge，集成新功能
5. ✅ 修改命令系统，过滤 crafting_table 类型
6. ✅ 更新配置模板注释

---

## 🔗 相关文件

- `src/main/java/com/minemart/itemcoreforge/core/Forge.java`
- `src/main/java/com/minemart/itemcoreforge/config/ForgeLoader.java`
- `src/main/java/com/minemart/itemcoreforge/recipe/CraftingTableRecipeManager.java`（新增）
- `src/main/java/com/minemart/itemcoreforge/ItemCoreForge.java`
- `src/main/java/com/minemart/itemcoreforge/command/ForgeCommand.java`
- `src/main/java/com/minemart/itemcoreforge/command/ForgeTabCompleter.java`
- `src/main/java/com/minemart/itemcoreforge/utils/MaterialChecker.java`
- `src/main/resources/forges/example_template.yml`

---

## 🚀 使用示例

### 1. 创建 crafting_table 类型锻造台

```yaml
# plugins/ItemCoreForge/forges/my_recipes.yml
forge-id: "my_recipes"
settings:
  type: crafting_table
  exact_placement: true

recipes:
  custom_sword:
    output:
      source: "itemcore"
      id: "legendary_sword"
      amount: 1
      check-durability: false
    materials:
      - source: "itemcore"
        id: "flame_ingot"
        amount: 2
        slot: 1
        check-durability: false
      - source: "itemcore"
        id: "flame_ingot"
        amount: 1
        slot: 4
        check-durability: false
      - source: "vanilla"
        id: "STICK"
        amount: 1
        slot: 7
        check-durability: false
```

### 2. 重载配置

```
/icf reload
```

### 3. 使用配方

1. 打开原版工作台
2. 按照 `slot` 指定的位置摆放材料
3. 自动检查条件（等级、权限等）
4. 制作完成

---

## 💡 后续扩展方向

- `furnace` 类型：熔炉配方
- `brewing_stand` 类型：酿造台配方
- `anvil` 类型：铁砧配方
- `smithing_table` 类型：锻造台配方

---

*开发者: Trae AI*  
*文档版本: 1.0.0*
