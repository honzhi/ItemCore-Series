package com.minemart.itemcorelevel.storage;

import com.minemart.itemcorelevel.PlayerData;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class MySQLStorage implements DataStorage {

    private final HikariDataSource dataSource;
    private final String tableName;
    private final Logger logger;

    public MySQLStorage(String host, int port, String database, String tablePrefix,
                        String username, String password,
                        int maxPoolSize, int minIdle, int connTimeout,
                        Logger logger) {
        this.logger = logger;
        this.tableName = tablePrefix + "player_data";

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database
            + "?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8&useUnicode=true&serverTimezone=Asia/Shanghai");
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connTimeout);
        config.setPoolName("ItemCoreLevel-Hikari");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        this.dataSource = new HikariDataSource(config);
        createTable();
        logger.info("MySQL 连接成功: " + host + ":" + port + "/" + database);
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS `" + tableName + "` ("
            + "`uuid` VARCHAR(36) NOT NULL PRIMARY KEY,"
            + "`player_name` VARCHAR(32) NOT NULL,"
            + "`level` INT NOT NULL DEFAULT 1,"
            + "`exp` INT NOT NULL DEFAULT 0,"
            + "`last_online` BIGINT NOT NULL,"
            + "`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
            + "`updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
            + "INDEX `idx_player_name` (`player_name`)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            logger.severe("创建数据表失败: " + e.getMessage());
        }
    }

    @Override
    public PlayerData load(UUID uuid) {
        String sql = "SELECT * FROM `" + tableName + "` WHERE uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new PlayerData(
                    uuid,
                    rs.getString("player_name"),
                    rs.getInt("level"),
                    rs.getInt("exp"),
                    rs.getLong("last_online")
                );
            }
        } catch (SQLException e) {
            logger.warning("加载玩家数据失败 [" + uuid + "]: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void save(UUID uuid, PlayerData data) {
        String sql = "INSERT INTO `" + tableName + "` (uuid, player_name, level, exp, last_online) "
            + "VALUES (?, ?, ?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE player_name = VALUES(player_name), "
            + "level = VALUES(level), exp = VALUES(exp), last_online = VALUES(last_online)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, data.getPlayerName());
            ps.setInt(3, data.getLevel());
            ps.setInt(4, data.getExp());
            ps.setLong(5, data.getLastOnline());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.warning("保存玩家数据失败 [" + uuid + "]: " + e.getMessage());
        }
    }

    @Override
    public void saveAll(Map<UUID, PlayerData> dataMap) {
        if (dataMap.isEmpty()) return;
        String sql = "INSERT INTO `" + tableName + "` (uuid, player_name, level, exp, last_online) "
            + "VALUES (?, ?, ?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE player_name = VALUES(player_name), "
            + "level = VALUES(level), exp = VALUES(exp), last_online = VALUES(last_online)";
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Map.Entry<UUID, PlayerData> entry : dataMap.entrySet()) {
                    PlayerData data = entry.getValue();
                    ps.setString(1, entry.getKey().toString());
                    ps.setString(2, data.getPlayerName());
                    ps.setInt(3, data.getLevel());
                    ps.setInt(4, data.getExp());
                    ps.setLong(5, data.getLastOnline());
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.warning("批量保存玩家数据失败: " + e.getMessage());
        }
    }

    @Override
    public boolean hasData(UUID uuid) {
        String sql = "SELECT 1 FROM `" + tableName + "` WHERE uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("MySQL 连接池已关闭");
        }
    }
}