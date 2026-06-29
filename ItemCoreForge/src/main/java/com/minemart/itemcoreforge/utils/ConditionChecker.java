package com.minemart.itemcoreforge.utils;

import com.minemart.itemcoreforge.core.Forge;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ConditionChecker {

    public static class CheckResult {
        private final boolean success;
        private final String failReason;
        private final String conditionType;

        public CheckResult(boolean success, String failReason, String conditionType) {
            this.success = success;
            this.failReason = failReason;
            this.conditionType = conditionType;
        }

        public static CheckResult success() {
            return new CheckResult(true, null, null);
        }

        public static CheckResult fail(String reason, String type) {
            return new CheckResult(false, reason, type);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getFailReason() {
            return failReason;
        }

        public String getConditionType() {
            return conditionType;
        }
    }

    public CheckResult checkCondition(Player player, Forge.Condition condition) {
        if (player == null || condition == null) {
            return CheckResult.fail("无效的玩家或条件", "unknown");
        }

        return switch (condition.getType().toLowerCase()) {
            case "level" -> checkLevelCondition(player, condition);
            case "permission" -> checkPermissionCondition(player, condition);
            case "money" -> checkMoneyCondition(player, condition);
            default -> CheckResult.fail("未知的条件类型: " + condition.getType(), condition.getType());
        };
    }

    public List<CheckResult> checkAllConditions(Player player, List<Forge.Condition> conditions) {
        List<CheckResult> results = new ArrayList<>();
        if (conditions != null) {
            for (Forge.Condition condition : conditions) {
                results.add(checkCondition(player, condition));
            }
        }
        return results;
    }

    public boolean allConditionsMet(Player player, List<Forge.Condition> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }
        
        for (Forge.Condition condition : conditions) {
            CheckResult result = checkCondition(player, condition);
            if (!result.isSuccess()) {
                return false;
            }
        }
        return true;
    }

    private CheckResult checkLevelCondition(Player player, Forge.Condition condition) {
        int requiredLevel = condition.getValue();
        int playerLevel = player.getLevel();
        
        if (playerLevel >= requiredLevel) {
            return CheckResult.success();
        } else {
            return CheckResult.fail(
                "需要等级 " + requiredLevel + "，当前等级 " + playerLevel,
                "level"
            );
        }
    }

    private CheckResult checkPermissionCondition(Player player, Forge.Condition condition) {
        String node = condition.getNode();
        
        if (node == null || node.isEmpty()) {
            return CheckResult.success();
        }
        
        if (player.hasPermission(node)) {
            return CheckResult.success();
        } else {
            return CheckResult.fail("缺少权限: " + node, "permission");
        }
    }

    private CheckResult checkMoneyCondition(Player player, Forge.Condition condition) {
        double requiredMoney = condition.getAmount();
        
        if (requiredMoney <= 0) {
            return CheckResult.success();
        }
        
        try {
            Class<?> economyClass = Class.forName("net.milkbowl.vault.economy.Economy");
            Object economy = player.getServer().getServicesManager()
                .getRegistration(economyClass).getProvider();
            
            double balance = (double) economyClass.getMethod("getBalance", Player.class)
                .invoke(economy, player);
            
            if (balance >= requiredMoney) {
                return CheckResult.success();
            } else {
                return CheckResult.fail(
                    "需要 " + requiredMoney + " 金币，当前余额 " + balance,
                    "money"
                );
            }
        } catch (Exception e) {
            return CheckResult.fail("经济系统未安装或配置错误", "money");
        }
    }

    public String getConditionDescription(Forge.Condition condition) {
        return switch (condition.getType().toLowerCase()) {
            case "level" -> "等级: " + condition.getValue();
            case "permission" -> "权限: " + condition.getNode();
            case "money" -> "金币: " + condition.getAmount();
            default -> "未知条件";
        };
    }
}
