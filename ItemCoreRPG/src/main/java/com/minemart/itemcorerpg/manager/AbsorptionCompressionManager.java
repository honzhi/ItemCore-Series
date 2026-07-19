package com.minemart.itemcorerpg.manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.minemart.itemcorerpg.ItemCoreRPG;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AbsorptionCompressionManager implements Listener {

    private static final String PACKET_CLASS_NAME =
            "net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket";
    private static final String SNAPSHOT_CLASS_NAME = PACKET_CLASS_NAME + "$AttributeSnapshot";
    private static final String ATTRIBUTES_CLASS_NAME =
            "net.minecraft.world.entity.ai.attributes.Attributes";

    private final ItemCoreRPG plugin;
    private ProtocolManager protocolManager;
    private PacketListener packetListener;
    private Constructor<?> packetConstructor;
    private Constructor<?> snapshotConstructor;
    private Method packetEntityIdMethod;
    private Method packetValuesMethod;
    private Method snapshotAttributeMethod;
    private Object maxAbsorptionHolder;
    private boolean packetRewriteEnabled;
    private boolean failureLogged;

    public AbsorptionCompressionManager(ItemCoreRPG plugin) {
        this.plugin = plugin;
    }

    public void start() {
        Plugin protocolLib = Bukkit.getPluginManager().getPlugin("ProtocolLib");
        if (protocolLib == null || !protocolLib.isEnabled()) {
            plugin.getLogger().warning("未检测到 ProtocolLib，金色吸收生命压缩已禁用");
            return;
        }

        try {
            initializeReflection();
        } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
            logFailure("无法初始化 1.21.11 吸收生命属性包", exception);
            return;
        }

        protocolManager = ProtocolLibrary.getProtocolManager();
        packetRewriteEnabled = true;
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
        if (protocolManager == null || !packetRewriteEnabled) {
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
        if (packetRewriteEnabled) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                sendAbsorptionAttribute(player, false);
            }
        }
        packetRewriteEnabled = false;
        protocolManager = null;
    }

    private void initializeReflection() throws ReflectiveOperationException {
        Class<?> packetClass = Class.forName(PACKET_CLASS_NAME);
        Class<?> snapshotClass = Class.forName(SNAPSHOT_CLASS_NAME);
        Class<?> holderClass = Class.forName("net.minecraft.core.Holder");

        packetConstructor = packetClass.getDeclaredConstructor(int.class, List.class);
        packetConstructor.setAccessible(true);
        snapshotConstructor = snapshotClass.getConstructor(
                holderClass, double.class, Collection.class);
        packetEntityIdMethod = packetClass.getMethod("getEntityId");
        packetValuesMethod = packetClass.getMethod("getValues");
        snapshotAttributeMethod = snapshotClass.getMethod("attribute");

        Class<?> attributesClass = Class.forName(ATTRIBUTES_CLASS_NAME);
        Field maxAbsorptionField = attributesClass.getField("MAX_ABSORPTION");
        maxAbsorptionHolder = maxAbsorptionField.get(null);
    }

    private void rewriteAbsorptionAttribute(PacketEvent event) {
        if (!packetRewriteEnabled || !plugin.getConfigManager().isAbsorptionCompressionEnabled()) {
            return;
        }

        try {
            Object packetHandle = event.getPacket().getHandle();
            int entityId = (int) packetEntityIdMethod.invoke(packetHandle);
            if (entityId != event.getPlayer().getEntityId()) {
                return;
            }

            List<?> snapshots = readSnapshots(packetHandle);
            List<Object> rewrittenSnapshots = new ArrayList<>(snapshots.size());
            boolean rewritten = false;
            for (Object snapshot : snapshots) {
                Object attributeHolder = snapshotAttributeMethod.invoke(snapshot);
                if (maxAbsorptionHolder.equals(attributeHolder)) {
                    double displayValue = getDisplayedMaxAbsorption(event.getPlayer(), true);
                    rewrittenSnapshots.add(createSnapshot(attributeHolder, displayValue));
                    rewritten = true;
                } else {
                    rewrittenSnapshots.add(snapshot);
                }
            }

            if (rewritten) {
                Object rewrittenPacket = packetConstructor.newInstance(entityId, rewrittenSnapshots);
                event.setPacket(PacketContainer.fromPacket(rewrittenPacket));
            }
        } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
            packetRewriteEnabled = false;
            logFailure("改写吸收生命属性包失败，功能已自动停用", exception);
        }
    }

    private List<?> readSnapshots(Object packetHandle) throws ReflectiveOperationException {
        return (List<?>) packetValuesMethod.invoke(packetHandle);
    }

    private Object createSnapshot(Object attributeHolder, double value)
            throws ReflectiveOperationException {
        return snapshotConstructor.newInstance(attributeHolder, value, Collections.emptyList());
    }

    private double compressValue(Player player, double actualValue) {
        double maxHealth = player.getMaxHealth();
        if (maxHealth <= 0) {
            return actualValue;
        }
        return Math.max(0, actualValue * plugin.getConfigManager().getHealthScale() / maxHealth);
    }

    private double getDisplayedMaxAbsorption(Player player, boolean compressed) {
        AttributeInstance attribute = player.getAttribute(Attribute.MAX_ABSORPTION);
        if (attribute == null) {
            return 0;
        }
        return compressed ? compressValue(player, attribute.getValue()) : attribute.getValue();
    }

    private void sendAbsorptionAttribute(Player player, boolean compressed) {
        if (!packetRewriteEnabled || protocolManager == null) {
            return;
        }

        try {
            double displayValue = getDisplayedMaxAbsorption(player, compressed);
            Object snapshot = createSnapshot(maxAbsorptionHolder, displayValue);
            Object packetHandle = packetConstructor.newInstance(
                    player.getEntityId(), List.of(snapshot));
            protocolManager.sendServerPacket(
                    player, PacketContainer.fromPacket(packetHandle), false);
        } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
            packetRewriteEnabled = false;
            logFailure("发送吸收生命属性包失败，功能已自动停用", exception);
        }
    }

    private void logFailure(String message, Throwable throwable) {
        if (failureLogged) {
            return;
        }
        failureLogged = true;
        plugin.getLogger().log(java.util.logging.Level.SEVERE, message, throwable);
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
