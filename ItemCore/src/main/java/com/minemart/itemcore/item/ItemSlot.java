package com.minemart.itemcore.item;

import org.bukkit.inventory.EquipmentSlot;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ItemSlot {

    MAIN_HAND("main_hand", EquipmentSlot.HAND),
    OFF_HAND("off_hand", EquipmentSlot.OFF_HAND),
    HEAD("head", EquipmentSlot.HEAD),
    CHEST("chest", EquipmentSlot.CHEST),
    LEGS("legs", EquipmentSlot.LEGS),
    FEET("feet", EquipmentSlot.FEET),
    ANY("any", null),
    TRINKETS("trinkets", null);

    private final String configKey;
    private final EquipmentSlot equipmentSlot;

    ItemSlot(String configKey, EquipmentSlot equipmentSlot) {
        this.configKey = configKey;
        this.equipmentSlot = equipmentSlot;
    }

    public String getConfigKey() {
        return configKey;
    }

    public EquipmentSlot getEquipmentSlot() {
        return equipmentSlot;
    }

    public static ItemSlot fromConfigKey(String key) {
        if (key == null) {
            return MAIN_HAND;
        }
        for (ItemSlot slot : values()) {
            if (slot.configKey.equalsIgnoreCase(key)) {
                return slot;
            }
        }
        return MAIN_HAND;
    }

    public static List<EquipmentSlot> toEquipmentSlots(List<ItemSlot> slots) {
        return slots.stream()
            .filter(s -> s.equipmentSlot != null)
            .map(s -> s.equipmentSlot)
            .collect(Collectors.toList());
    }

    public static List<ItemSlot> defaultSlots() {
        return Arrays.asList(MAIN_HAND);
    }
}
