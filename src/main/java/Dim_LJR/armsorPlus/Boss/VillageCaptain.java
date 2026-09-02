package Dim_LJR.armsorPlus.Boss;

import Dim_LJR.armsorPlus.NamespaceKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

// 村民队长 —— 玩家模型简单BOSS (无技能, 追击+近战)。
// 物理本体是隐形盔甲架(ModelBoss): setVelocity追击(原生物理/击退), 假玩家每tick同步到盔甲架位置;
// 使用玩家账号换肤后的"新皮肤"(已烘焙的签名textures), 原版+离线服均可显示。
// 独立的血量/血条/死亡, 不依赖 BossMenu, 机制保持简单。
public class VillageCaptain implements Listener {

    private static final double MAX_HEALTH = 20;
    private static final int FOLLOW_RANGE = 40;
    private static final String BOSS_NAME = "VillageCaptain";

    private static boolean alive = false;
    private static FakePlayer body;
    private static World world;
    private static BukkitTask aiTask;
    private static BossBar bossBar;

    private static final Random RANDOM = new Random();

    // ========================================================================
    // 召唤
    // ========================================================================

    public static void spawnBoss(Player summoner) {
        world = summoner.getWorld();
        Location spawnLoc = summoner.getLocation();

        // ---- BossBar (先于FakePlayer生成, 血量由onDamage回调同步) ----
        bossBar = Bukkit.createBossBar("§a■ " + BOSS_NAME, BarColor.GREEN, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(1.0);

        // ---- 统一创建 (附属插件优先, 本地PE兜底) ----
        body = FakePlayerProvider.createVillageCaptain(world, spawnLoc, BOSS_NAME,
                "§a■ " + BOSS_NAME + " §7Lv.40", summoner.getLocation().getYaw(), MAX_HEALTH);
        if (body == null) {
            summoner.sendMessage("§c" + BOSS_NAME + "渲染初始化失败");
            NamespaceKey.Keys.getplugin.getLogger().warning(BOSS_NAME + "渲染初始化失败");
            cleanup();
            return;
        }
        body.setAiType(AiType.RUN); // 村民队长: RUN 追击(更快 + 更近停止, 翻 1 格)
        if (!FakePlayerProvider.hasBackend()) {
            summoner.sendMessage("§e未检测到 PacketEvents 与附属插件, " + BOSS_NAME + " 将以隐形状态出现(仅盔甲架本体, 伤害仍有效)");
        }
        Enmity.registerBossBody(body.getBody()); // 供挑拨木棍右键选中

        // ---- 穿戴 (铁剑 + 金胸甲) ----
        body.setEquipment(
                null,
                new ItemStack(Material.GOLDEN_CHESTPLATE),
                null,
                null,
                new ItemStack(Material.IRON_SWORD));

        // ---- 挂游戏逻辑回调 (算伤害/同步血条/掉落) ----
        body.setOnAttack(p -> body.applyDamage(p, ModelBoss.computeDamage(p), true));
        body.setOnDamage((attacker, remaining) -> {
            bossBar.setProgress(Math.min(1.0, Math.max(0.0, remaining / MAX_HEALTH)));
            bossBar.setTitle("§a■ " + BOSS_NAME + " §7" + Math.round(remaining) + "/" + Math.round(MAX_HEALTH));
        });
        body.setOnDeath(killer -> VillageCaptain.onDeath());

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§a◆ " + BOSS_NAME + " 出现了！");
        }

        alive = true;
        startAI();
    }

    // ========================================================================
    // AI: 追击 + 近战 (无技能)
    // ========================================================================

    private static void startAI() {
        aiTask = new BukkitRunnable() {
            int attackCooldown = 0;
            int idleTicks = 0;

            @Override
            public void run() {
                try {
                    if (body == null || !body.isValid()) {
                        cleanup();
                        cancel();
                        return;
                    }
                    // 盔甲架被传送门带到其他世界 → 直接消失
                    if (!body.getLocation().getWorld().equals(world)) {
                        despawn();
                        cancel();
                        return;
                    }
                    LivingEntity target = Enmity.getEnemy(body.getBody());
                    if (target == null) target = findNearestTarget();
                    if (target == null) {
                        body.setTarget(null);
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

                    // ---- 近战 ----
                    if (attackCooldown > 0) attackCooldown--;
                    if (attackCooldown <= 0 && dist <= 3.5) {
                        body.rotateTo(target);
                        body.swing();
                        world.playSound(body.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);
                        if (!target.isDead()) {
                            // 以盔甲架本体作为伤害来源, 让被击中的生物能收到"是谁打的它"
                            target.damage(8, body.getBody());
                            // 每次命中都强制生物反击玩家模型本体(不检查原目标):
                            // 僵尸猪灵等中立怪被攻击后原生仇恨会优先指向附近的玩家, 不强制会被"抢走"反击目标
                            if (target instanceof Mob mob) {
                                mob.setTarget(body.getBody());
                            }
                            target.setVelocity(ModelBoss.safeKnockback(
                                    body.getLocation(), target.getLocation(), 0.7, 0.2));
                        }
                        attackCooldown = 25 + RANDOM.nextInt(25);
                    }
                } catch (Throwable t) {
                    NamespaceKey.Keys.getplugin.getLogger().warning("[" + BOSS_NAME + "] AI循环异常(已忽略): " + t);
                }
            }
        }.runTaskTimer(NamespaceKey.Keys.getplugin, 20L, 2L);
    }

    private static void faceTarget(Player target) {
        if (body == null) return;
        body.rotateTo(target);
    }

    // 寻找目标: 优先攻击附近敌对生物(怪物), 无怪物时攻击最近的战斗玩家。
    private static LivingEntity findNearestTarget() {
        if (body == null || !body.isValid()) return null;
        Location bodyLoc = body.getLocation();
        LivingEntity monster = null;
        LivingEntity player = null;
        double monsterDist = Double.MAX_VALUE;
        double playerDist = Double.MAX_VALUE;
        for (Entity entity : bodyLoc.getWorld().getNearbyEntities(bodyLoc, FOLLOW_RANGE, 10, FOLLOW_RANGE)) {
            if (entity.isDead() || !entity.isValid() || !entity.getWorld().equals(world)) continue;
            double dist = entity.getLocation().distance(bodyLoc);
            if (entity instanceof Monster) {
                if (dist < monsterDist) {
                    monsterDist = dist;
                    monster = (LivingEntity) entity;
                }
            } else if (entity instanceof Player p && BossTargets.isCombatPlayer(p)) {
                if (dist < playerDist) {
                    playerDist = dist;
                    player = p;
                }
            }
        }
        return monster != null ? monster : player;
    }

    // ========================================================================
    // 死亡 & 消失
    // ========================================================================

    private static void onDeath() {
        if (body == null) return;
        Location loc = body.getLocation();
        body.remove();

        world.spawnParticle(Particle.CLOUD, loc, 80, 2, 2, 2, 0.3);
        world.spawnParticle(Particle.ENCHANT, loc, 40, 1, 1, 1, 0.2);
        world.playSound(loc, Sound.ENTITY_WITHER_DEATH, 1.0f, 0.6f);

        // 村民队长掉落: 绿宝石 + 村民刷怪蛋 + 经验瓶
        world.dropItemNaturally(loc, new ItemStack(Material.EMERALD, RANDOM.nextInt(3) + 2));
        world.dropItemNaturally(loc, new ItemStack(Material.VILLAGER_SPAWN_EGG, 1));
        world.dropItemNaturally(loc, new ItemStack(Material.EXPERIENCE_BOTTLE, 6));

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§a◆ " + BOSS_NAME + " 已被击败！");
        }
        cleanup();
    }

    private static void despawn() {
        if (body == null) return;
        body.remove();
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§a" + BOSS_NAME + " 因失去目标而消失了");
        }
        cleanup();
    }

    private static void cleanup() {
        if (aiTask != null) { aiTask.cancel(); aiTask = null; }
        if (body != null) {
            Enmity.unregisterBossBody(body.getBody());
            body.remove();
            body = null;
        }
        if (bossBar != null) { bossBar.removeAll(); bossBar = null; }
        alive = false;
        world = null;
    }

    public static boolean isAlive() { return alive; }

    public static Location getBossLocation() {
        return body != null ? body.getLocation() : null;
    }

    // ========================================================================
    // 玩家上线补齐 (让后加入的玩家也能看到村民队长)
    // ========================================================================

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!alive || body == null || world == null) return;
        Player p = event.getPlayer();
        if (p.getWorld().equals(world)) {
            Bukkit.getScheduler().runTaskLater(NamespaceKey.Keys.getplugin,
                    () -> { if (alive && body != null && p.isOnline()) body.spawnTo(p); }, 20L);
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        if (!alive || body == null || world == null) return;
        Player p = event.getPlayer();
        if (p.getWorld().equals(world)) {
            Bukkit.getScheduler().runTaskLater(NamespaceKey.Keys.getplugin,
                    () -> { if (alive && body != null && p.isOnline()) body.spawnTo(p); }, 20L);
        } else {
            body.despawnFrom(p);
        }
    }
}
