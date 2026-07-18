package com.minemart.itemcorerpg.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.WrappedAttribute;
import com.minemart.itemcorerpg.ItemCoreRPG;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AbsorptionCompressionManager implements Listener {

    private final ItemCoreRPG plugin;
    private ProtocolManager protocolManager;
    private PacketListener packetListener;

    public AbsorptionCompressionManager(ItemCoreRPG plugin) {
        this.plugin = plugin;
    }

    public void start() {
        Plugin protocolLib = Bukkit.getPluginManager().getPlugin("ProtocolLib");
        if (protocolLib == null || !protocolLib.isEnabled()) {
            if (plugin.getConfigManager().isAbsorptionCompressionEnabled()) {
                plugin.getLogger().warning("未检测到 ProtocolLib，金色吸收生命压缩已禁用");
            }
            return;
        }

        protocolManager = ProtocolLibrary.getProtocolManager();
        packetListener = new PacketAdapter(
                plugin, ListenerPriority.NORMAL, PacketType.Play.Server.UPDATE_ATTRIBUTES) {
            @Override
            public void onPacketSending(PacketEvent event) {
                rewriteAbsorptionAttribute(event);
            }
        };
        protocolManager.addPacketListener(packetListener);
        refreshAll();
    }

    public void refreshAll() {
        if (protocolManager == null) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendAbsorptionAttribute(player, plugin.getConfigManager().isAbsorptionCompressionEnabled());
        }
    }

    public void shutdown() {
        if (protocolManager == null) {
            return;
        }
        if (packetListener != null) {
            protocolManager.removePacketListener(packetListener);
            packetListener = null;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendAbsorptionAttribute(player, false);
        }
        protocolManager = null;
    }

    private void rewriteAbsorptionAttribute(PacketEvent event) {
        if (!plugin.getConfigManager().isAbsorptionCompressionEnabled()) {
            return;
        }

        PacketContainer originalPacket = event.getPacket();
        if (originalPacket.getIntegers().size() == 0
                || originalPacket.getIntegers().read(0) != event.getPlayer().getEntityId()) {
            return;
        }

        PacketContainer packet = originalPacket.deepClone();
        List<WrappedAttribute> attributes = packet.getAttributeCollectionModifier().read(0);
        if (attributes == null || attributes.isEmpty()) {
            return;
        }

        List<WrappedAttribute> rewrittenAttributes = new ArrayList<>(attributes.size());
        boolean rewritten = false;
        for (WrappedAttribute attribute : attributes) {
            if (isMaxAbsorption(attribute)) {
                double displayValue = compressValue(event.getPlayer(), attribute.getFinalValue());
                rewrittenAttributes.add(WrappedAttribute.newBuilder(attribute)
                        .packet(packet)
                        .baseValue(displayValue)
                        .modifiers(Collections.emptyList())
                        .build());
                rewritten = true;
            } else {
                rewrittenAttributes.add(attribute);
            }
        }

        if (rewritten) {
            packet.getAttributeCollectionModifier().write(0, rewrittenAttributes);
            event.setPacket(packet);
        }
    }

    private boolean isMaxAbsorption(WrappedAttribute attribute) {
        String attributeKey = attribute.getAttributeKey();
        return attributeKey != null && attributeKey.toLowerCase().endsWith("max_absorption");
    }

    private double compressValue(Player player, double actualValue) {
        double maxHealth = player.getMaxHealth();
        if (maxHealth <= 0) {
            return actualValue;
        }
        return Math.max(0, actualValue * plugin.getConfigManager().getHealthScale() / maxHealth);
    }

    private void sendAbsorptionAttribute(Player player, boolean compressed) {
        AttributeInstance attribute = player.getAttribute(Attribute.MAX_ABSORPTION);
        if (attribute == null) {
            return;
        }

        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.UPDATE_ATTRIBUTES);
        packet.getIntegers().write(0, player.getEntityId());
        double value = compressed ? compressValue(player, attribute.getValue()) : attribute.getValue();
        WrappedAttribute wrappedAttribute = WrappedAttribute.newBuilder()
                .packet(packet)
                .attributeKey(Attribute.MAX_ABSORPTION.getKey().toString())
                .baseValue(value)
                .build();
        packet.getAttributeCollectionModifier().write(0, List.of(wrappedAttribute));
        protocolManager.sendServerPacket(player, packet, false);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (protocolManager != null && event.getPlayer().isOnline()) {
                sendAbsorptionAttribute(
                        event.getPlayer(), plugin.getConfigManager().isAbsorptionCompressionEnabled());
            }
        });
    }
}
