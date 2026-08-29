package Dim_LJR.armsorPlus.Boss;

import Dim_LJR.armsorPlus.ArmsorItem;
import Dim_LJR.armsorPlus.NamespaceKey;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

import static Dim_LJR.armsorPlus.Item.Materials.IllusionerScrap;

// 影武者 —— 玩家模型 BOSS (真实实体物理本体 + 假玩家视觉)
// 使用真实玩家的皮肤渲染成一个完整玩家模型，玩家可以直接攻击它。
// 物理本体是隐形盔甲架(ModelBoss): setVelocity追击(原生物理/击退), 假玩家每tick同步到盔甲架位置;
// 攻击检测: 盔甲架命中盒优先(原生击退) + PacketEvents 拦截 ServerboundInteractEntity 兜底。
public class PlayerBoss implements Listener {

    private static final double MAX_HEALTH = 300;
    private static final int FOLLOW_RANGE = 40;
    // 使用英文名: 中文名不是合法Minecraft用户名, 在PlayerInfoUpdate/用户列表里无法被正确引用
    private static final String BOSS_NAME = "ShadowWarrior";

    private static boolean bossAlive = false;
    private static FakePlayer body;
    private static World world;
    private static BukkitTask aiTask;
    private static BossBar bossBar;
    private static Player summoner;

    private static final Random RANDOM = new Random();

    // ========================================================================
    // 召唤
    // ========================================================================

    public static void spawnBoss(Player summoner) {
        spawnBoss(summoner, summoner);
    }

    // skinSource: 使用谁的皮肤渲染 (默认召唤者本人)
    public static void spawnBoss(Player summoner, Player skinSource) {
        if (bossAlive) {
            summoner.sendMessage("§c已有一只" + BOSS_NAME + "，请先击败或等待其消失");
            return;
        }
        Location spawnLoc = findSpawnLocation(summoner);
        if (spawnLoc == null) {
            summoner.sendMessage("§c没有足够的空间召唤BOSS");
            return;
        }

        world = summoner.getWorld();
        PlayerBoss.summoner = summoner;
        spawnLoc.getWorld().loadChunk(spawnLoc.getChunk());

        // ---- BossBar (先于FakePlayer生成, 血量由onDamage回调同步) ----
        bossBar = Bukkit.createBossBar("§8■ " + BOSS_NAME, BarColor.PURPLE, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(1.0);

        // ---- 统一创建 (附属插件优先, 本地PE兜底) ----
        body = FakePlayerProvider.createShadowWarrior(world, spawnLoc, skinSource, BOSS_NAME,
                "§8■ " + BOSS_NAME + " §7Lv.60", summoner.getLocation().getYaw(), MAX_HEALTH);
        if (body == null) {
            summoner.sendMessage("§c" + BOSS_NAME + "渲染初始化失败");
            NamespaceKey.Keys.getplugin.getLogger().warning(BOSS_NAME + "渲染初始化失败");
            cleanup();
            return;
        }
        if (!FakePlayerProvider.hasBackend()) {
            summoner.sendMessage("§e未检测到 PacketEvents 与附属插件, " + BOSS_NAME + " 将以隐形状态出现(仅盔甲架本体, 伤害仍有效)");
        }
        Enmity.registerBossBody(body.getBody()); // 供挑拨木棍右键选中

        // ---- 穿戴 (钻石套 + 钻石剑) ----
        body.setEquipment(
                new ItemStack(Material.DIAMOND_HELMET),
                new ItemStack(Material.DIAMOND_CHESTPLATE),
                new ItemStack(Material.DIAMOND_LEGGINGS),
                new ItemStack(Material.DIAMOND_BOOTS),
                new ItemStack(Material.DIAMOND_SWORD));

        // ---- 挂游戏逻辑回调 (算伤害/同步血条/掉落) ----
        body.setOnAttack(p -> body.applyDamage(p, ModelBoss.computeDamage(p), true));
        body.setOnDamage((attacker, remaining) -> {
            bossBar.setProgress(Math.min(1.0, Math.max(0.0, remaining / MAX_HEALTH)));
            bossBar.setTitle("§8■ " + BOSS_NAME + " §7" + Math.round(remaining) + "/" + Math.round(MAX_HEALTH));
            BossMenu.syncBossHealth(BossMenu.BossType.SHADOW_WARRIOR, remaining, MAX_HEALTH);
            BossMenu.updateBossBar(BossMenu.BossType.SHADOW_WARRIOR);
        });
        body.setOnDeath(killer -> PlayerBoss.onDeath());

        BossMenu.registerBoss(BossMenu.BossType.SHADOW_WARRIOR, body.getBody(), MAX_HEALTH, bossBar);

        // ---- 召唤特效 ----
        world.strikeLightningEffect(spawnLoc);
        world.spawnParticle(Particle.SMOKE, spawnLoc, 80, 1, 2, 1, 0.1);
        world.spawnParticle(Particle.ENCHANT, spawnLoc, 60, 1, 2, 1, 0.3);
        world.playSound(spawnLoc, Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.7f);

        String msg = "§8◆ " + BOSS_NAME + " (" + skinSource.getName() + " 的幻影) 在 "
                + spawnLoc.getBlockX() + " " + spawnLoc.getBlockY() + " " + spawnLoc.getBlockZ() + " 处出现了！";
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(msg);
        }

        bossAlive = true;
        startAI();
    }

    private static Location findSpawnLocation(Player player) {
        Location base = player.getLocation();
        Vector dir = base.getDirection().multiply(6);
        Location target = base.clone().add(dir);
        target.setY(BossSpawn.groundY(target.getWorld(), target.getBlockX(), target.getBlockZ(), base.getBlockY()));
        return target;
    }

    // ========================================================================
    // AI
    // ========================================================================

    private static void startAI() {
        aiTask = new BukkitRunnable() {
            int attackCooldown = 0;
            int skillCooldown = 0;
            int idleTicks = 0;

            @Override
            public void run() {
                try {
                if (body == null || !body.isValid()) {
                    cleanup();
                    cancel();
                    return;
                }

                BossMenu.updateBossBar(BossMenu.BossType.SHADOW_WARRIOR);

                LivingEntity target = Enmity.getEnemy(body.getBody());
                if (target == null) target = findNearestPlayer();
                if (target == null) {
                    body.setTarget(null); // 失去目标, 盔甲架原地待命
                    if (++idleTicks > 3000) {
                        despawn();
                        cancel();
                    }
                    return;
                }
                idleTicks = 0;

                // ---- 追击: ModelBoss 每tick setVelocity 朝目标推进 ----
                body.setTarget(target);
                double dist = body.getLocation().distance(target.getLocation());

                // ---- 技能循环 ----
                if (attackCooldown > 0) attackCooldown--;
                if (skillCooldown > 0) skillCooldown--;

                if (attackCooldown <= 0 && dist <= 3.5) {
                    meleeAttack(target);
                    attackCooldown = 20 + RANDOM.nextInt(16);
                    return;
                }
                if (skillCooldown <= 0 && dist <= 8) {
                    int r = RANDOM.nextInt(3);
                    if (r == 0) {
                        shadowDash(target);
                    } else if (r == 1) {
                        areaSlash(target);
                    } else {
                        meleeAttack(target);
                    }
                    skillCooldown = 60 + RANDOM.nextInt(30);
                }
                } catch (Throwable t) {
                    NamespaceKey.Keys.getplugin.getLogger().warning("[影武者] AI循环异常(已忽略): " + t);
                }
            }
        }.runTaskTimer(NamespaceKey.Keys.getplugin, 20L, 2L);
    }

    // ========================================================================
    // 攻击方式
    // ========================================================================

    // 让假玩家原地转向面向目标 (仅旋转包, 不移动)。
    private static void faceTarget(LivingEntity target) {
        if (body == null) return;
        body.rotateTo(target);
    }

    private static void meleeAttack(LivingEntity target) {
        if (body == null) return;
        body.rotateTo(target); // 攻击前转向目标, 保证挥砍方向与朝向一致
        body.swing();
        world.playSound(body.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);
        if (target.getLocation().distance(body.getLocation()) <= 3.5 && !target.isDead()) {
            target.damage(12);
            target.setVelocity(ModelBoss.safeKnockback(body.getLocation(), target.getLocation(), 0.8, 0.2));
        }
    }

    // 影遁 —— 瞬间冲向目标并造成范围伤害
    private static void shadowDash(LivingEntity target) {
        if (body == null) return;
        Location start = body.getLocation();
        world.playSound(start, Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1.0f, 0.8f);
        world.spawnParticle(Particle.CLOUD, start, 20, 0.5, 0.5, 0.5, 0.05);

        Vector dir = target.getLocation().toVector().subtract(start.toVector());
        if (dir.lengthSquared() < 1.0E-4) {
            dir = new Vector(0, 0, 1); // 与Boss完全重合时兜底方向, 避免 normalize 产生 NaN
        } else {
            dir = dir.normalize();
        }
        // 冲刺方向即面向目标的方向, 不再沿用站定时的旧朝向
        float yaw = (float) Math.toDegrees(Math.atan2(-dir.getX(), dir.getZ()));
        Location end = target.getLocation().clone().subtract(dir.multiply(1.0));
        end.setY(start.getY());
        end.setYaw(yaw);

        body.teleport(end);
        body.swing();

        for (Entity entity : world.getNearbyEntities(end, 3, 2, 3)) {
            if (entity instanceof Player p && !p.isDead()) {
                p.damage(15);
                p.setVelocity(ModelBoss.safeKnockback(end, p.getLocation(), 1.2, 0.4));
            }
        }
        world.spawnParticle(Particle.EXPLOSION, end.clone().add(0, 1, 0), 2, 0.5, 0.5, 0.5, 0);
        world.playSound(end, Sound.ENTITY_GENERIC_EXPLODE, 0.6f, 0.8f);
    }

    // 幻影斩 —— 攻击周围所有玩家
    private static void areaSlash(LivingEntity target) {
        if (body == null) return;
        Location loc = body.getLocation();
        body.rotateTo(target); // 攻击前转向目标
        body.swing();
        world.spawnParticle(Particle.SWEEP_ATTACK, loc.clone().add(0, 1, 0), 8, 0.5, 0.5, 0.5, 0);
        world.playSound(loc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.2f);

        for (Entity entity : world.getNearbyEntities(loc, 4, 3, 4)) {
            if (entity instanceof Player p && !p.isDead()) {
                p.damage(10);
                p.setVelocity(ModelBoss.safeKnockback(loc, p.getLocation(), 0.9, 0.3));
            }
        }
    }

    // ========================================================================
    // 玩家上线补齐 (让后加入的玩家也能看到影武者)
    // ========================================================================

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!bossAlive || body == null || world == null) return;
        Player p = event.getPlayer();
        if (p.getWorld().equals(world)) {
            // 延迟到客户端真正进入 PLAY 状态后再发包, 否则 PlayerInfoUpdate 会被当作状态错误踢出
            Bukkit.getScheduler().runTaskLater(NamespaceKey.Keys.getplugin,
                    () -> { if (bossAlive && body != null && p.isOnline()) body.spawnTo(p); }, 20L);
        }
    }

    // 玩家切换世界: 进入Boss世界时补齐渲染, 离开时清理
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        if (!bossAlive || body == null || world == null) return;
        Player p = event.getPlayer();
        if (p.getWorld().equals(world)) {
            Bukkit.getScheduler().runTaskLater(NamespaceKey.Keys.getplugin,
                    () -> { if (bossAlive && body != null && p.isOnline()) body.spawnTo(p); }, 20L);
        } else {
            body.despawnFrom(p);
        }
    }

    // ========================================================================
    // 死亡 & 消失
    // ========================================================================

    public static void onDeath() {
        if (body == null) return;

        Location loc = body.getLocation();
        body.remove();

        world.spawnParticle(Particle.CLOUD, loc, 80, 2, 2, 2, 0.3);
        world.spawnParticle(Particle.ENCHANT, loc, 40, 1, 1, 1, 0.2);
        world.playSound(loc, Sound.ENTITY_WITHER_DEATH, 1.0f, 0.6f);

        world.dropItemNaturally(loc, new ItemStack(Material.DIAMOND, RANDOM.nextInt(3) + 2));
        world.dropItemNaturally(loc, IllusionerScrap(RANDOM.nextInt(3) + 1)); // 幻术师残片 (影武者材料)
        if (RANDOM.nextBoolean()) {
            world.dropItemNaturally(loc, ArmsorItem.MagicBallCreateI(RANDOM.nextInt(2) + 1));
        }
        world.dropItemNaturally(loc, new ItemStack(Material.EXPERIENCE_BOTTLE, 8));

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§8◆ " + BOSS_NAME + " 已被击败！");
        }

        cleanup();
    }

    private static void despawn() {
        if (body == null) return;
        Location loc = body.getLocation();
        body.remove();
        world.spawnParticle(Particle.SMOKE, loc, 40, 2, 2, 2, 0.1);
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§8" + BOSS_NAME + " 因失去目标而消失了");
        }
        cleanup();
    }

    // 强制击杀影武者 (供 /ArmsorPlus bossremove 指令调用; 走原版死亡流程, 含掉落结算)
    public static boolean killBoss() {
        if (!bossAlive || body == null) return false;
        onDeath();
        return true;
    }

    private static void cleanup() {
        if (aiTask != null) { aiTask.cancel(); aiTask = null; }

        if (body != null) {
            Enmity.unregisterBossBody(body.getBody());
            body.remove();
            body = null;
        }

        BossMenu.unregisterBoss(BossMenu.BossType.SHADOW_WARRIOR);
        if (bossBar != null) { bossBar.removeAll(); bossBar = null; }

        bossAlive = false;
        world = null;
        summoner = null;
    }

    // ========================================================================
    // 工具
    // ========================================================================

    private static Player findNearestPlayer() {
        if (body == null || !body.isValid()) return null;
        Location bodyLoc = body.getLocation();
        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Entity entity : bodyLoc.getWorld().getNearbyEntities(bodyLoc, FOLLOW_RANGE, 10, FOLLOW_RANGE)) {
            if (entity instanceof Player p && BossTargets.isCombatPlayer(p)
                    && p.getWorld().equals(world)) {
                double dist = p.getLocation().distance(bodyLoc);
                if (dist < nearestDistance) {
                    nearestDistance = dist;
                    nearest = p;
                }
            }
        }
        return nearest;
    }

    public static boolean isAlive() { return bossAlive; }

    public static Location getBossLocation() {
        return body != null ? body.getLocation() : null;
    }
}
