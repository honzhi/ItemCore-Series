# 开发日志 - DEV-003: GUI布局重构与自定义模型数据支持

## 日期
2026-05-29

## 版本
ItemCoreTrinkets v1.0.0

## 开发进度
- 阶段：第四阶段（GUI界面与交互完善）
- 状态：已完成

---

## 今日完成内容

### 1. GUI配置文件重构

按照开发方案中的字符映射布局方式重新设计了 `gui.yml` 配置文件。

#### 旧配置格式（已废弃）
```yaml
layout:
  title: "&6饰品管理"
  size: 27
  slots:
    ring_left: 11
    ring_right: 13
  decorations:
    border:
      enabled: true
      slots: [0,1,2,3,4,5,6,7,8]
```

#### 新配置格式（字符映射布局）
```yaml
settings:
  rows: 5
  title: "&6饰品管理"

layout:
  - 'AAAAAAAAA'    # 第1行：全边框
  - 'ARABHBAEA'    # 第2行：戒指、护身符、头盔显示
  - 'ALXLBXAFA'    # 第3行：手镯、腰带、胸甲显示
  - 'AMXNBXAGH'    # 第4行：护腿、靴子、副手显示
  - 'AAAAAAAAA'    # 第5行：全边框

slots:
  A:
    material: BLACK_STAINED_GLASS_PANE
    function: border
  R:
    material: GOLD_NUGGET
    function: trinket_slot
    trinket-slot-id: ring_left
    type: ring
```

### 2. 新增功能类型

| 功能类型 | 说明 |
|----------|------|
| `border` | 边框装饰，无交互功能 |
| `trinket_slot` | 饰品槽位，显示已装备饰品或默认物品，可交互 |
| `equipment_slot` | 原版装备显示槽位，仅显示，不可交互 |
| `close` | 关闭按钮，点击关闭界面 |

### 3. 新增原版装备显示功能

在GUI中添加了原版装备显示槽位：

| 字符 | 槽位类型 | 说明 |
|------|----------|------|
| E | helmet | 头盔显示槽位 |
| F | chestplate | 胸甲显示槽位 |
| M | leggings | 护腿显示槽位 |
| N | boots | 靴子显示槽位 |
| G | offhand | 副手显示槽位 |

### 4. 自定义模型数据支持

新增 `custom-model-data` 配置项，支持资源包自定义模型：

```yaml
R:
  material: GOLD_NUGGET
  display-name: "&6左手戒指"
  custom-model-data: 5001    # 自定义模型ID
  function: trinket_slot
  trinket-slot-id: ring_left
  type: ring
```

**使用说明**：
- 设置为 `0` 或不设置 → 使用原版材质
- 设置大于 `0` 的值 → 使用资源包中定义的自定义模型

### 5. `material` 字段功能说明

`material` 字段现在表示**未装备时显示的默认物品材质**：

| 状态 | 显示内容 |
|------|----------|
| 未装备饰品 | 显示配置中定义的 `material` 物品 |
| 已装备饰品 | 显示实际装备的饰品物品 |

---

## 代码修改详情

### TrinketMenu.java 修改

1. **新增字符映射布局解析**
   ```java
   private void parseLayout(ConfigurationSection guiConfig) {
       List<String> layoutRows = guiConfig.getStringList("layout");
       ConfigurationSection slotsSection = guiConfig.getConfigurationSection("slots");
       
       int slotIndex = 0;
       for (String row : layoutRows) {
           for (char c : row.toCharArray()) {
               String charKey = String.valueOf(c);
               if (slotsSection.contains(charKey)) {
                   ConfigurationSection slotConfig = slotsSection.getConfigurationSection(charKey);
                   if (slotConfig != null) {
                       slotConfigs.put(slotIndex, new SlotConfig(slotConfig));
                   }
               }
               slotIndex++;
           }
       }
   }
   ```

2. **新增原版装备显示**
   ```java
   private ItemStack getPlayerEquipment(String slotType) {
       return switch (slotType.toLowerCase()) {
           case "helmet" -> player.getInventory().getHelmet();
           case "chestplate" -> player.getInventory().getChestplate();
           case "leggings" -> player.getInventory().getLeggings();
           case "boots" -> player.getInventory().getBoots();
           case "offhand" -> player.getInventory().getItemInOffHand();
           default -> null;
       };
   }
   ```

3. **新增自定义模型数据支持**
   ```java
   // SlotConfig 类新增字段
   int customModelData;
   
   // 构造函数中读取配置
   this.customModelData = section.getInt("custom-model-data", 0);
   
   // 创建物品时设置模型数据
   if (config.customModelData > 0) {
       meta.setCustomModelData(config.customModelData);
   }
   ```

---

## 配置文件注释完善

为 `gui.yml` 添加了详细的中文注释：

```yaml
# ================================================
# 槽位功能定义
# ================================================
# 每个字符对应一个槽位功能
# 字段说明：
#   material:          未装备时显示的物品材质（必填）
#   display-name:      显示名称（支持颜色代码）
#   custom-model-data: 自定义模型数据（可选，用于资源包自定义模型）
#   function:          功能类型（border/trinket_slot/equipment_slot/close）
#   trinket-slot-id:   饰品槽位ID（仅trinket_slot功能需要）
#   type:              饰品类型要求（仅trinket_slot功能需要）
#   slot-type:         原版装备槽位类型（仅equipment_slot功能需要）
# ================================================
```

---

## GUI布局预览

```
┌─────────────────────────────────┐
│  A  A  A  A  A  A  A  A  A      │  第1行：边框
│  A  R  A  B  H  B  A  E  A      │  第2行：戒指、护身符、头盔
│  A  L  X  L  B  X  A  F  A      │  第3行：腰带、手镯、胸甲
│  A  M  X  N  B  X  A  G  H      │  第4行：护腿、靴子、副手
│  A  A  A  A  A  A  A  A  A      │  第5行：边框
└─────────────────────────────────┘

字符含义：
A = 边框    R = 左戒指   B = 右戒指   H = 护身符
X = 手镯    L = 腰带     E = 头盔     F = 胸甲
M = 护腿    N = 靴子     G = 副手
```

---

## 待办事项

- [x] DEV-001: 项目初始化与核心类搭建
- [x] DEV-002: 插件构建与测试
- [x] DEV-003: GUI布局重构与自定义模型数据支持
- [ ] DEV-004: 属性计算与应用机制完善
- [ ] DEV-005: 命令系统、权限与测试

---

## 备注

GUI配置文件已完全重构，支持：
- 字符映射布局方式
- 自定义模型数据（custom-model-data）
- 原版装备显示功能
- 详细中文注释

下一步将完善属性计算与应用机制。