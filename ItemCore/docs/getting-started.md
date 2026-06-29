# 安装与配置

## 安装步骤

1. 下载 `ItemCore.jar`、`ItemCoreRPG.jar`、`ItemCoreMythic.jar`
2. 放入 `plugins/` 目录
3. （可选）安装 PlaceholderAPI
4. 重启服务器
5. 插件首次启动会自动生成配置文件

## 目录结构

```
plugins/ItemCore/
├── config.yml              # 主配置
├── attributes.yml          # 属性全局参数
├── messages.yml            # 消息配置
├── categories.yml          # 分类配置
├── elements.yml            # 元素配置
├── ailments.yml            # 异常配置
├── items/                  # 物品配置目录
│   ├── weapons.yml
│   ├── armors.yml
│   └── ...
├── tooltip/
│   ├── lore.yml            # Lore 布局
│   └── stats.yml           # 属性显示格式
```

## config.yml 主配置

```yaml
language: "zh-CN"        # 语言: zh-CN / en-US
debug-mode: false         # 调试模式

items-folder: "items"     # 物品配置目录
categories-file: "categories.yml"  # 分类文件名

gui:
  name: "物品库"
  size: 54

lore-refresh:
  enabled: false          # 启用自动 Lore 刷新
  interval: 100           # 扫描间隔（tick）
```

## attributes.yml 属性参数

```yaml
crit:
  default_crit_damage: 150    # 默认暴击伤害（%）

adaptive-force:
  attack_conversion: 1.0      # 适应之力 → 攻击
  spell_conversion: 1.0       # 适应之力 → 法强

defense_formulas:
  physical_resist: '{damage} * (1 - {armor} / ({armor} + 100))'
  spell_resist: '{damage} * (1 - {armor} / ({armor} + 100))'

penetration_order: percent_first  # 穿甲顺序
```

## 重载配置

修改配置后运行：

```
/ic reload
```

无需重启服务器。