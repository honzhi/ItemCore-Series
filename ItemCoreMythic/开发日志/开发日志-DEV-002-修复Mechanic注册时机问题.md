# 开发日志 DEV-002 - 修复 Mechanic 注册时机问题

## 概述

本次修复解决 MythicMobs Mechanic/Condition/Placeholder 注册时机问题，避免第一次启动失效、/reload 后恢复的问题。

## 问题分析

原问题：
- ItemCoreMythic `onEnable()` 时立即注册 Mechanic
- 此时 MythicMobs 可能尚未完全加载
- 导致注册失效
- `/reload` 后 MythicMobs 已加载，恢复正常

## 修改内容

### ItemCoreMythic.java 主类修改

**关键变更**:
1. 移除 `onEnable()` 中的 `registerMechanics()` 直接调用
2. 新增 `registerAll()` 方法，统一注册 Mechanic/Condition/Placeholder
3. 新增 `mechanicsRegistered` 标志，防止重复注册
4. 拆分为 `registerMechanics()`、`registerConditions()`、`registerPlaceholders()` 三个子方法
5. 每个注册操作添加日志输出，便于调试

### MMLoadListener.java 监听器修改

**关键变更**:
1. 监听 `MythicReloadedEvent`（MM 完全加载后触发）
2. 事件触发时调用 `plugin.registerAll()`

## 注册流程

**新流程（正确）**:
```
服务器启动
    ↓
ItemCoreMythic onEnable()
    ↓
检查依赖、初始化组件、注册监听器
    ↓
等待...
    ↓
MythicMobs 完全加载 → MythicReloadedEvent
    ↓
MMLoadListener 接收事件
    ↓
registerAll()
    ↓
registerMechanics()     ← 安全
registerConditions()   ← 安全
registerPlaceholders() ← 安全
```

## 修改文件清单

| 路径 | 说明 |
|------|------|
| `ItemCoreMythic.java` | 主类，修改注册逻辑 |
| `MMLoadListener.java` | 监听器，接收 MythicReloadedEvent |

## 相关开发方案

- [开发方案-DEV-002-SkillMetadataManager与YAMLLoader.md](../开发方案/开发方案-DEV-002-SkillMetadataManager与YAMLLoader.md) - 下一阶段开发计划
