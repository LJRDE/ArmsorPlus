package Dim_LJR.armsorPlus;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// 玩家个人设置管理 —— 持久化到 player_settings.yml。
public class PlayerSettings {

    private boolean enchantNotifications = true;
    private boolean adminMode = false;

    private static final Map<UUID, PlayerSettings> SETTINGS = new HashMap<>();
    private static File dataFile;

    // ========================================================================
    // 存取
    // ========================================================================

    private static PlayerSettings get(UUID uuid) {
        return SETTINGS.computeIfAbsent(uuid, k -> new PlayerSettings());
    }

    // ---- 附魔通知 ----

    public static boolean isNotificationEnabled(UUID uuid) {
        return get(uuid).enchantNotifications;
    }

    public static void setNotificationEnabled(UUID uuid, boolean enabled) {
        get(uuid).enchantNotifications = enabled;
    }

    // 仅当玩家开启了附魔通知时才发送聊天消息
    public static void notify(Entity entity, String message) {
        if (entity instanceof Player p && isNotificationEnabled(p.getUniqueId())) {
            p.sendMessage(message);
        }
    }

    // 仅当玩家开启了附魔通知时才发送ActionBar消息
    public static void notifyActionBar(Entity entity, String message) {
        if (entity instanceof Player p && isNotificationEnabled(p.getUniqueId())) {
            p.sendActionBar(message);
        }
    }

    // ---- 管理员模式 ----

    public static boolean isAdminMode(UUID uuid) {
        return get(uuid).adminMode;
    }

    public static void setAdminMode(UUID uuid, boolean enabled) {
        get(uuid).adminMode = enabled;
    }

    // ========================================================================
    // 持久化
    // ========================================================================

    public static void load(JavaPlugin plugin) {
        dataFile = new File(plugin.getDataFolder(), "player_settings.yml");
        if (!dataFile.exists()) return;

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(dataFile);
        for (String key : yaml.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                PlayerSettings ps = new PlayerSettings();
                ps.enchantNotifications = yaml.getBoolean(key + ".enchantNotifications", true);
                ps.adminMode = yaml.getBoolean(key + ".adminMode", false);
                SETTINGS.put(uuid, ps);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public static void save(JavaPlugin plugin) {
        dataFile = new File(plugin.getDataFolder(), "player_settings.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        for (var entry : SETTINGS.entrySet()) {
            String key = entry.getKey().toString();
            PlayerSettings ps = entry.getValue();
            yaml.set(key + ".enchantNotifications", ps.enchantNotifications);
            yaml.set(key + ".adminMode", ps.adminMode);
        }
        try {
            yaml.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("无法保存玩家设置: " + e.getMessage());
        }
    }
}
