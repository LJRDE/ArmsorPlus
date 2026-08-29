package Dim_LJR.armsorPlus.Boss;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// 挑拨木棍 (EnmityTool) 的仇怨注册表。
// 双向记录: 右键 A 再右键 B 后, A 视 B 为敌、B 视 A 为敌, 持续到一方死亡/消失。
// 原版生物(含原生实体Boss): 全局 tick 强制 setTarget 维持追打。
// 插件Boss(假玩家/盔甲架本体): 由各自 AI 循环通过 getEnemy() 覆盖默认玩家索敌。
public final class Enmity {

    private static final Map<UUID, UUID> pairs = new ConcurrentHashMap<>();      // 自己 → 敌人 (双向各存一份)
    private static final Set<UUID> bossBodies = ConcurrentHashMap.newKeySet();   // 假玩家Boss的盔甲架本体UUID

    private Enmity() {}

    // 全局强制执行任务 (原版生物互相追打 + 清理死对)。在插件 onEnable 调用一次。
    public static void startTask(JavaPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(plugin, 20L, 10L);
    }

    // A 与 B 结成死敌 (双向)。同一实体不成对。
    public static void setEnmity(LivingEntity a, LivingEntity b) {
        if (a == null || b == null || a.getUniqueId().equals(b.getUniqueId())) return;
        UUID au = a.getUniqueId(), bu = b.getUniqueId();
        pairs.put(au, bu);
        pairs.put(bu, au);
    }

    // 返回 self 当前存活的敌人; 无敌人/敌人已死失效则清理并返回 null。
    public static LivingEntity getEnemy(LivingEntity self) {
        if (self == null || self.isDead() || !self.isValid()) return null;
        UUID enemyId = pairs.get(self.getUniqueId());
        if (enemyId == null) return null;
        LivingEntity enemy = lookup(enemyId);
        if (enemy == null || enemy.isDead() || !enemy.isValid()) {
            removePair(self.getUniqueId());
            return null;
        }
        return enemy;
    }

    // 多候选: 任一候选有敌人即返回 (Boss 含多个实体时用, 右键任意一个都能生效)。
    public static LivingEntity getEnemy(LivingEntity... selves) {
        for (LivingEntity self : selves) {
            if (self == null) continue;
            LivingEntity e = getEnemy(self);
            if (e != null) return e;
        }
        return null;
    }

    // ---- 假玩家Boss本体注册 (让挑拨木棍可右键选中假玩家) ----
    public static void registerBossBody(Entity body) {
        if (body != null) bossBodies.add(body.getUniqueId());
    }

    public static void unregisterBossBody(Entity body) {
        if (body == null) return;
        bossBodies.remove(body.getUniqueId());
        removePair(body.getUniqueId());
    }

    public static boolean isBossBody(Entity e) {
        return e != null && bossBodies.contains(e.getUniqueId());
    }

    // 每tick段强制执行: 原版生物互相追打; 插件Boss的盔甲架本体不是 Mob, 由各自AI循环处理, 此处不冲突。
    private static void tick() {
        for (Map.Entry<UUID, UUID> entry : new ArrayList<>(pairs.entrySet())) {
            UUID selfId = entry.getKey();
            UUID enemyId = entry.getValue();
            LivingEntity self = lookup(selfId);
            LivingEntity enemy = lookup(enemyId);
            if (self == null || enemy == null || self.isDead() || enemy.isDead()
                    || !self.isValid() || !enemy.isValid()) {
                removePair(selfId);
                continue;
            }
            if (self instanceof Mob mob) mob.setTarget(enemy);
        }
    }

    private static LivingEntity lookup(UUID id) {
        for (var world : Bukkit.getWorlds()) {
            Entity e = world.getEntity(id);
            if (e instanceof LivingEntity le) return le;
        }
        return null;
    }

    private static void removePair(UUID selfId) {
        UUID enemyId = pairs.remove(selfId);
        if (enemyId != null) pairs.remove(enemyId);
    }
}
