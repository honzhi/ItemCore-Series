package com.minemart.itemcoreforge.core;

public class CraftResult {

    public enum ResultType {
        SUCCESS,
        FAILURE,
        CANCELLED,
        QUEUE_FULL,
        MATERIALS_INSUFFICIENT,
        CONDITIONS_NOT_MET
    }

    private final ResultType type;
    private final String message;
    private final QueueItem item;
    private final Forge.Recipe recipe;

    private CraftResult(ResultType type, String message, QueueItem item, Forge.Recipe recipe) {
        this.type = type;
        this.message = message;
        this.item = item;
        this.recipe = recipe;
    }

    public static CraftResult success(QueueItem item) {
        return new CraftResult(ResultType.SUCCESS, "制作成功", item, null);
    }

    public static CraftResult failure(String reason) {
        return new CraftResult(ResultType.FAILURE, reason, null, null);
    }

    public static CraftResult cancelled(QueueItem item) {
        return new CraftResult(ResultType.CANCELLED, "制作已取消", item, null);
    }

    public static CraftResult queueFull() {
        return new CraftResult(ResultType.QUEUE_FULL, "制作队列已满", null, null);
    }

    public static CraftResult materialsInsufficient(String detail) {
        return new CraftResult(ResultType.MATERIALS_INSUFFICIENT, "材料不足: " + detail, null, null);
    }

    public static CraftResult conditionsNotMet(String detail) {
        return new CraftResult(ResultType.CONDITIONS_NOT_MET, "条件不满足: " + detail, null, null);
    }

    public ResultType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public QueueItem getItem() {
        return item;
    }

    public Forge.Recipe getRecipe() {
        return recipe;
    }

    public boolean isSuccess() {
        return type == ResultType.SUCCESS;
    }

    public boolean isFailure() {
        return type == ResultType.FAILURE;
    }

    public boolean isCancelled() {
        return type == ResultType.CANCELLED;
    }
}