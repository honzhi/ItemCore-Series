# 开发日志 - DEV-004 - GUI系统开发

**开发日期**: 2026-05-26  
**开发阶段**: Phase 5  
**开发状态**: ✅ 已完成

---

## 📋 开发目标

实现 GUI 系统，包括：
1. BaseMenu 基础菜单类
2. CustomForgeGUI 自定义布局界面
3. RecipeSelectGUI 配方选择界面
4. GuiListener GUI 监听器
5. 队列显示更新（含剩余时间）

---

## 📁 创建/更新的文件

| 文件路径 | 说明 |
|---------|------|
| `gui/BaseMenu.java` | 基础菜单类 |
| `gui/CustomForgeGUI.java` | 自定义布局界面 |
| `gui/RecipeSelectGUI.java` | 配方选择界面 |
| `gui/GuiListener.java` | GUI 监听器 |
| `command/ForgeCommand.java` | 更新命令系统 |
| `ItemCoreForge.java` | 更新主类注册监听器 |

---

## 🔧 核心实现

### 1. BaseMenu 基础菜单类

**设计模式**: 模板方法模式

**核心功能**：
```java
public abstract class BaseMenu implements InventoryHolder {
    protected final Inventory inventory;
    protected final Map<Integer, Consumer<InventoryClickEvent>> clickHandlers;
    
    // 设置物品
    void setItem(int slot, ItemStack item);
    
    // 设置物品和点击处理器
    void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler);
    
    // 处理点击事件
    void handleClick(InventoryClickEvent event);
    
    // 打开/关闭菜单
    void open();
    void close();
}
```

### 2. CustomForgeGUI 自定义布局界面

**布局渲染流程**：
```
renderLayout()
    ↓
遍历布局配置
    ↓
根据功能类型渲染槽位
    ├── border → 渲染边框
    ├── material_slot → 渲染材料槽位
    ├── output_slot → 渲染输出槽位
    ├── queue_display → 渲染队列显示
    ├── confirm_button → 渲染确认按钮
    └── cancel_button → 渲染取消按钮
```

**队列显示实现**：
```java
private void renderQueueDisplay(int slot, LayoutLoader.SlotConfig config) {
    RecipeQueue queue = plugin.getPlayerQueueManager().getQueue(player, forge.getForgeId());
    CraftTask currentTask = queue.getCurrentTask();
    
    if (currentTask != null) {
        // 显示正在制作的物品
        // 显示剩余时间
        double remainingSeconds = plugin.getCraftScheduler().getRemainingSeconds(currentTask.getTaskId());
        lore.add("剩余时间: " + remainingSeconds + "秒");
        
        // 显示队列中等待的数量
        int waitingCount = queue.getWaitingCount();
    }
}
```

**确认按钮实现**：
```java
private void renderConfirmButton(int slot, LayoutLoader.SlotConfig config) {
    // 检查材料
    boolean hasMaterials = materialChecker.hasMaterials(player, selectedRecipe.getMaterials());
    
    // 检查条件
    boolean conditionsMet = conditionChecker.allConditionsMet(player, selectedRecipe.getConditions());
    
    // 根据检查结果显示不同按钮
    Material buttonMaterial = (hasMaterials && conditionsMet) ? GREEN_WOOL : RED_WOOL;
    
    // 设置点击处理器
    setItem(slot, item, event -> {
        if (hasMaterials && conditionsMet) {
            startCraft();
        }
    });
}
```

### 3. RecipeSelectGUI 配方选择界面

**功能**：
- 显示锻造台所有配方
- 分页显示（每页28个）
- 点击配方打开制作界面

**配方显示**：
```java
private void renderRecipeItem(int slot, String recipeId, Forge.Recipe recipe) {
    // 显示配方输出物品
    // 显示制作时间
    // 显示材料数量
    // 显示条件列表
    // 点击打开制作界面
}
```

### 4. GuiListener GUI 监听器

**事件处理**：
```java
@EventHandler
public void onInventoryClick(InventoryClickEvent event) {
    // 检查是否为 BaseMenu
    // 取消默认行为
    // 调用菜单的点击处理器
}

@EventHandler
public void onInventoryDrag(InventoryDragEvent event) {
    // 取消拖拽行为
}

@EventHandler
public void onInventoryClose(InventoryCloseEvent event) {
    // 清理菜单引用
}
```

---

## 📊 GUI 交互流程

```
玩家输入 /icf 铁匠铺
    ↓
┌─────────────────────────────────────┐
│     RecipeSelectGUI                 │
│  显示配方列表（分页）                 │
└─────────────────────────────────────┘
    ↓ 点击配方
┌─────────────────────────────────────┐
│     CustomForgeGUI                  │
│  - 材料槽位（显示所需材料）           │
│  - 输出槽位（显示产出物品）           │
│  - 队列显示（显示剩余时间）           │
│  - 确认按钮（开始制作）               │
└─────────────────────────────────────┘
    ↓ 点击确认
┌─────────────────────────────────────┐
│     CraftScheduler.enqueueCraft()   │
│  - 检查材料                          │
│  - 检查条件                          │
│  - 消耗材料                          │
│  - 创建任务                          │
│  - 启动异步制作                      │
└─────────────────────────────────────┘
```

---

## ✅ 完成的功能

- [x] BaseMenu 基础菜单类
- [x] CustomForgeGUI 自定义布局界面
- [x] RecipeSelectGUI 配方选择界面
- [x] GuiListener GUI 监听器
- [x] 布局渲染引擎
- [x] 材料槽位显示
- [x] 输出槽位显示
- [x] 队列显示（含剩余时间）
- [x] 确认按钮（条件检查）
- [x] 取消按钮
- [x] 分页功能

---

## 📝 待完善的功能

- [ ] 玩家数据持久化
- [ ] 队列状态保存
- [ ] 制作历史记录

---

## 🎨 GUI 布局示例

**配方选择界面**：
```
┌─────────────────────────────────────────┐
│         铁匠铺 - 选择配方                 │
├─────────────────────────────────────────┤
│   [铁剑]     [钻石剑]    [铁镐]          │
│   3.0秒      5.0秒      2.0秒           │
│                                         │
│   [铁斧]     [铁锹]      [铁锭]          │
│   3.0秒      2.0秒      1.0秒           │
│                                         │
├─────────────────────────────────────────┤
│         [关闭]                          │
└─────────────────────────────────────────┘
```

**制作界面**：
```
┌─────────────────────────────────────────┐
│              铁匠铺                      │
├─────────────────────────────────────────┤
│   [边框] [材料1] [输出] [材料2] [边框]   │
│   [边框] [确认]  [材料3] [材料4] [边框]  │
│   [边框] [材料5] [队列] [材料6] [边框]   │
│   [边框] [边框]  [边框] [边框]  [边框]   │
│   [边框] [队列显示] [队列显示] [队列显示] │
└─────────────────────────────────────────┘
```

---

## 🚀 下一步计划

**Phase 6: 制作流程**（已合并到 Phase 3）
**Phase 7: 命令系统**（已完成）
**Phase 8: 玩家数据持久化**
- 玩家数据存储（YAML）
- 队列状态持久化
- 制作历史记录

---

*开发者: Trae AI*  
*文档版本: 1.0.0*
