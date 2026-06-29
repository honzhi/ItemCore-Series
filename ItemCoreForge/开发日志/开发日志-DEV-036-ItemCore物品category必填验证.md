# ================================================
# 开发日志 - DEV-036
# ItemCore 物品 category 必填验证
# ================================================

## 更新日期
2026-05-29

## 更新内容

### 1. 问题背景

ItemCore 物品的完整 ID 格式为 `category/item_id`，缺少 category 可能导致：
- 找到错误的物品（不同 category 有相同 id）
- 无法找到物品
- 潜在的配置错误

### 2. 修改内容

添加 ItemCore 物品的 category 验证，当使用 `source: "itemcore"` 时必须指定 `category`。

### 3. 代码修改

| 文件 | 修改内容 |
|------|----------|
| `ItemReference.java` | 在 `resolveItemCoreItem()` 方法中添加 category 验证，缺少时返回占位物品 |
| `MaterialChecker.java` | 在 `countItemCoreItem()` 方法中添加 category 验证，缺少时直接返回 0 |

### 4. 验证逻辑

**获取物品时的验证**：
```java
if (category.isEmpty()) {
    return getPlaceholderItem("ItemCore 物品必须指定 category");
}
```

**计数时的验证**：
```java
if (ref.getCategory().isEmpty()) {
    return 0;  // 无法匹配物品，返回 0
}
```

### 5. 占位物品提示

当缺少 category 时，会显示红色屏障方块作为占位符，提示：
- `ItemCore 物品必须指定 category`

### 6. 正确配置示例

```yaml
recipes:
  # 正确配置：有 category
  custom_sword:
    output:
      source: "itemcore"
      category: "weapons"  # 必填！
      id: "custom_sword"
      amount: 1
    
    materials:
      - source: "itemcore"
        category: "materials"  # 必填！
        id: "custom_ingot"
        amount: 3
```

### 7. 错误配置示例

```yaml
recipes:
  # 错误配置：缺少 category
  custom_sword:
    output:
      source: "itemcore"
      id: "custom_sword"  # ❌ 缺少 category
      amount: 1
```

### 8. 兼容性

- 之前有正确填写 category 的配方继续正常工作
- 缺少 category 的旧配方会显示占位物品，不会导致插件崩溃
- 建议所有使用 itemcore 来源的配置都补充 category 字段
