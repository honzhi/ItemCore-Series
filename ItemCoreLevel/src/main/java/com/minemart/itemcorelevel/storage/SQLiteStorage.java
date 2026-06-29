package com.minemart.itemcorelevel.storage;

import com.minemart.itemcorelevel.PlayerData;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class SQLiteStorage implements DataStorage {

    private final File dataFile;
    private final Logger logger;
    private Connection connection;

    public SQLiteStorage(File dataFolder, Logger logger) {
        this.dataFile = new File(dataFolder, "playerdata.db");
        this.logger = logger;
        init();
    }

    private void init() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFile.getAbsolutePath());
            createTable();
            logger.info("SQLite 已连接: " + dataFile.getName());
        } catch (Exception e) {
            logger.severe("SQLite 连接失败: " + e.getMessage());
        }
    }

    private void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS player_data ("
            + "uuid TEXT PRIMARY KEY,"
            + "player_name TEXT NOT NULL,"
            + "level INTEGER NOT NULL DEFAULT 1,"
            + "exp INTEGER NOT NULL DEFAULT 0,"
            + "last_online INTEGER NOT NULL"
            + ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public PlayerData load(UUID uuid) {
        String sql = "SELECT * FROM player_data WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
            logger.warning("SQLite 加载失败 [" + uuid + "]: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void save(UUID uuid, PlayerData data) {
        String sql = "INSERT INTO player_data (uuid, player_name, level, exp, last_online) "
            + "VALUES (?, ?, ?, ?, ?) "
            + "ON CONFLICT(uuid) DO UPDATE SET player_name=excluded.player_name, "
            + "level=excluded.level, exp=excluded.exp, last_online=excluded.last_online";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, data.getPlayerName());
            ps.setInt(3, data.getLevel());
            ps.setInt(4, data.getExp());
            ps.setLong(5, data.getLastOnline());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.warning("SQLite 保存失败 [" + uuid + "]: " + e.getMessage());
        }
    }

    @Override
    public void saveAll(Map<UUID, PlayerData> dataMap) {
        if (dataMap.isEmpty()) return;
        String sql = "INSERT INTO player_data (uuid, player_name, level, exp, last_online) "
            + "VALUES (?, ?, ?, ?, ?) "
            + "ON CONFLICT(uuid) DO UPDATE SET player_name=excluded.player_name, "
            + "level=excluded.level, exp=excluded.exp, last_online=excluded.last_online";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.warning("SQLite 批量保存失败: " + e.getMessage());
        }
    }

    @Override
    public boolean hasData(UUID uuid) {
        String sql = "SELECT 1 FROM player_data WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void shutdown() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("SQLite 连接已关闭");
            } catch (SQLException e) {
                logger.warning("SQLite 关闭失败: " + e.getMessage());
            }
        }
    }
}