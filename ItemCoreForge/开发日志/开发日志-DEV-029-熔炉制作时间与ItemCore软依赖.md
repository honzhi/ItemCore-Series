# 开发日志 - DEV-029 - 熔炉制作时间配置与 ItemCore 软依赖分析

**开发日期**: 2026-05-28  
**开发阶段**: Phase 29  
**开发状态**: ✅ 已完成

---

## 📋 开发目标

1. 为熔炉配方添加 `cook-time` 可配置项
2. 分析项目对 ItemCore 的依赖程度
3. 制定 ItemCore 软依赖改造计划

---

## 🔧 修改内容

### 1. 熔炉配方添加 cook-time 配置

#### 已完成功能

**Forge.java - 新增 cookTime 字段**

```java
public static class Recipe {
    private ItemReference input;
    private ItemReference output;
    private final List<ItemReference> materials = new ArrayList<>();
    private final List<Condition> conditions = new ArrayList<>();
    private double craftTime = 1.0;
    private int cookTime = 200;  // 新增：熔炉烹饪时间（ticks）
    private boolean exactPlacement = false;
    
    public int getCookTime() {
        return cookTime;
    }
    
    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }
}
```

**ForgeLoader.java - 加载 cook-time 配置**

```java
private Forge.Recipe loadRecipe(FileConfiguration config, String path, String recipeId) {
    Forge.Recipe recipe = new Forge.Recipe(recipeId);
    // ... 其他加载逻辑 ...
    recipe.setCookTime(config.getInt(path + ".cook-time", 200));
    return recipe;
}
```

**FurnaceRecipeManager.java - 使用配置的烹饪时间**

```java
private void registerRecipe(Forge forge, Forge.Recipe recipe) {
    // ... 之前的逻辑 ...
    FurnaceRecipe furnaceRecipe = new FurnaceRecipe(key, result, choice, 0, recipe.getCookTime());
    Bukkit.addRecipe(furnaceRecipe);
}
```

**example_furnace.yml - 配置示例**

```yaml
recipes:
  iron_to_steel:
    input:
      source: "vanilla"
      id: "WHEAT"
      amount: 1
    output:
      source: "vanilla"
      id: "BREAD"
      amount: 1
    # 烹饪时间（tick，1 tick = 0.05 秒）
    # 原版熔炉：200 tick = 10 秒
    # 原版熔炉烧沙子：100 tick = 5 秒
    cook-time: 200
```

#### 不开发的功能

经过讨论，**熔炉类型不开发数量选项**，原因如下：

| 功能 | 状态 | 原因 |
|------|------|------|
| 输入数量 | ❌ 不开发 | 原版熔炉只有1个输入槽，每次只能烧1个物品 |
| 输出数量 | ❌ 不开发 | 与原版熔炉逻辑保持一致 |

---

### 2. ItemCore 依赖程度分析

#### 当前依赖情况

**硬依赖位置：**

1. **plugin.yml** - 插件元数据
   ```yaml
   depend: [ItemCore]  # 硬依赖
   ```

2. **ItemCoreForge.java** - 启动检查
   ```java
   if (!checkItemCore()) {
       getLogger().severe("未找到 ItemCore 插件，请确保已安装 ItemCore！");
       getServer().getPluginManager().disablePlugin(this);
       return;
   }
   ```

3. **pom.xml** - Maven 依赖
   ```xml
   <dependency>
       <groupId>com.minemart</groupId>
       <artifactId>ItemCore</artifactId>
       <version>${itemcore.version}</version>
       <scope>system</scope>
       <systemPath>${project.basedir}/../ItemCore/target/itemcore-1.0.0.jar</systemPath>
   </dependency>
   ```

**已实现软依赖的位置：**

**MaterialChecker.java** - 已使用反射调用 ItemCore API

```java
private int countItemCoreItem(Player player, ItemReference ref) {
    try {
        Class<?> apiClass = Class.forName("com.minemart.itemcore.api.ItemCoreAPI");
        Object api = apiClass.getMethod("getInstance").invoke(null);
        ItemStack sampleItem = (ItemStack) apiClass.getMethod("getItemStack", String.class)
            .invoke(api, ref.getId());
        // ...
    } catch (Exception e) {
        return 0;  // ItemCore 不存在时返回 0
    }
}
```

#### 依赖程度统计

| 文件 | 直接引用 ItemCore 类 | 反射调用 | 状态 |
|------|---------------------|----------|------|
| MaterialChecker.java | ❌ 无 | ✅ 有 | ✅ 已软依赖 |
| CraftingTableRecipeManager.java | ❌ 无 | ❌ 无 | ✅ 无依赖 |
| FurnaceRecipeManager.java | ❌ 无 | ❌ 无 | ✅ 无依赖 |
| 其他文件 | ❌ 无 | ❌ 无 | ✅ 无依赖 |

**结论**：项目中**没有任何文件直接 `import` ItemCore 的类**，所有对 ItemCore 的调用都已通过反射实现。只需要修改启动检查和 plugin.yml 即可完成软依赖改造。

---

## 📊 ItemCore 软依赖改造计划

### 方案概述

将 ItemCore 从硬依赖改为软依赖，使 ItemCoreForge 可以独立运行：

- **有 ItemCore 时**：支持 `source: "itemcore"` 的物品
- **无 ItemCore 时**：仅支持 `source: "vanilla"` 的物品，插件正常启动

### 具体修改

#### 1. 修改 plugin.yml

```yaml
# 修改前
depend: [ItemCore]

# 修改后
softdepend: [ItemCore]
```

#### 2. 修改 ItemCoreForge.java

```java
@Override
public void onEnable() {
    instance = this;
    
    // 取消强制检查，改为警告
    if (!checkItemCore()) {
        getLogger().warning("未检测到 ItemCore 插件，将仅支持 vanilla 物品！");
    }
    
    initManagers();
    // ... 后续逻辑不变
}
```

#### 3. 修改 pom.xml（可选）

如果不需要编译时类型检查，可以将 ItemCore 依赖改为 `provided` 或直接移除（因为已使用反射）。

---

## 📋 开发流程

1. ✅ 分析熔炉配方配置需求
2. ✅ 在 Forge.Recipe 中添加 cookTime 字段
3. ✅ 在 ForgeLoader 中加载 cook-time 配置
4. ✅ 修改 FurnaceRecipeManager 使用配置的烹饪时间
5. ✅ 更新 example_furnace.yml 添加 cook-time 配置和注释
6. ✅ 确认不开发熔炉数量选项
7. ✅ 分析项目对 ItemCore 的依赖程度
8. ✅ 制定 ItemCore 软依赖改造计划

---

## 🔗 相关文件

### 已修改
- `src/main/java/com/minemart/itemcoreforge/core/Forge.java`
- `src/main/java/com/minemart/itemcoreforge/config/ForgeLoader.java`
- `src/main/java/com/minemart/itemcoreforge/recipe/FurnaceRecipeManager.java`
- `src/main/resources/forges/example_furnace.yml`

### 待修改（软依赖改造）
- `src/main/resources/plugin.yml`
- `src/main/java/com/minemart/itemcoreforge/ItemCoreForge.java`
- `pom.xml`（可选）

---

## 🚀 使用示例

### 1. 配置自定义烹饪时间

```yaml
forge-id: "fast_furnace"
settings:
  type: furnace

recipes:
  fast_sand:
    input:
      source: "vanilla"
      id: "SAND"
      amount: 1
    output:
      source: "vanilla"
      id: "GLASS"
      amount: 1
    cook-time: 50  # 2.5秒完成（原版是10秒）
```

### 2. 不使用 ItemCore 的纯原版配方

```yaml
# 即使没有 ItemCore 也能正常工作
recipes:
  iron_to_gold:
    input:
      source: "vanilla"
      id: "IRON_INGOT"
      amount: 1
    output:
      source: "vanilla"
      id: "GOLD_INGOT"
      amount: 1
    cook-time: 200
```

---

## 💡 后续扩展方向

- **ItemCore 软依赖改造**：修改 plugin.yml 和启动检查
- **熔炉配方的燃料自定义**：支持自定义燃料类型和燃烧时间
- **高炉/烟熏炉支持**：扩展 furnace 类型支持更多熔炉变体
- **配方冲突检测**：当多个配方使用相同输入时给出警告

---

*开发者: Trae AI*  
*文档版本: 1.0.0*
