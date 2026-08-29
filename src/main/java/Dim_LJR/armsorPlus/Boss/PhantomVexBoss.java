package Dim_LJR.armsorPlus.Boss;

import Dim_LJR.armsorPlus.ArmsorItem;
import Dim_LJR.armsorPlus.NamespaceKey;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

// 幻翼·恼鬼 —— 幻翼 + 恼鬼 同时降临的组合飞行BOSS (参照熔岩双王的双实体模式)。
// 幻翼(125血)与恼鬼(125血): 二者都隐身; 幻翼俯冲伤害×2、恼鬼挥剑伤害×0.35, 均为穿透伤害。
// 免疫远程伤害/火焰伤害/中毒/凋零; 幻翼是主实体(手动驱动: 盘旋→俯冲→咬击), 恼鬼是身体部位(原版AI贴身挥剑)。
// 双BossBar分别显示二者血量; 幻翼与恼鬼都死亡才击败BOSS。
public class PhantomVexBoss {

    private static final double PHANTOM_HEALTH = 125;
    private static final double VEX_HEALTH = 125;
    private static final int TARGET_RANGE = 150; // 锁定范围: 150格内最近的生存玩家
    private static final int DESPAWN_TICKS = 1200;
    private static final double PHANTOM_BASE_DAMAGE = 6; // 幻翼原版咬击基础伤害 (×2穿透由BossMenu放大)
    private static final double PHANTOM_SPEED = 1.3;     // 盘旋飞行速度 (格/tick)
    private static final double PHANTOM_DIVE_SPEED = 2.2;// 俯冲速度 (格/tick)
    private static final double PHANTOM_HOVER_HEIGHT = 7;// 盘旋点高于目标的高度

    private static boolean bossAlive = false;
    private static Phantom phantom;
    private static Vex vex;
    private static BossBar phantomBar;
    private static BossBar vexBar;
    private static BukkitTask aiTask;
    private static int noTargetTicks;
    private static Location lastDeathLocation;
    private static LivingEntity phantomTarget;   // 幻翼当前追击目标 (原版AI不对玩家生效, 手动驱动)
    private static BukkitTask phantomMoveTask;
    private static int phantomDiveCooldown = 0;  // 距下次俯冲的冷却tick
    private static int phantomDiveTicksLeft = 0; // >0 表示正在俯冲
    private static int phantomBiteCooldown = 0;  // 咬击冷却

    private static final Random RANDOM = new Random();

    public static boolean isAlive() { return bossAlive; }

    public static Location getBossLocation() {
        if (phantom != null && !phantom.isDead()) return phantom.getLocation();
        if (vex != null && !vex.isDead()) return vex.getLocation();
        return null;
    }

    // ========================================================================
    // 召唤
    // ========================================================================

    public static void spawnBoss(Player summoner) {
        if (bossAlive) {
            summoner.sendMessage("§c幻翼·恼鬼已在战斗中，请先击败或等待其消失");
            return;
        }

        Location spawnLoc = findSpawnLocation(summoner);
        if (spawnLoc == null) {
            summoner.sendMessage("§c没有足够的空间召唤BOSS");
            return;
        }
        spawnLoc.getWorld().loadChunk(spawnLoc.getChunk());

        // ---- 幻翼 (125血, 隐身, 俯冲攻击) ----
        phantom = (Phantom) spawnLoc.getWorld().spawnEntity(spawnLoc.clone().add(0, 15, 0), EntityType.PHANTOM);
        phantom.setCustomName("§b◆ 幻翼");
        phantom.setCustomNameVisible(true);
        phantom.setRemoveWhenFarAway(false);
        phantom.setPersistent(true);
        var phantomHp = phantom.getAttribute(Attribute.MAX_HEALTH);
        if (phantomHp != null) phantomHp.setBaseValue(PHANTOM_HEALTH);
        phantom.setHealth(PHANTOM_HEALTH);
        phantom.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));

        // ---- 恼鬼 (125血, 隐身, 贴身挥剑) ----
        vex = (Vex) spawnLoc.getWorld().spawnEntity(spawnLoc.clone().add(0, 4, 0), EntityType.VEX);
        vex.setCustomName("§d◆ 恼鬼");
        vex.setCustomNameVisible(true);
        vex.setRemoveWhenFarAway(false);
        vex.setPersistent(true);
        var vexHp = vex.getAttribute(Attribute.MAX_HEALTH);
        if (vexHp != null) vexHp.setBaseValue(VEX_HEALTH);
        vex.setHealth(VEX_HEALTH);
        vex.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));

        // ---- 双BossBar ----
        phantomBar = Bukkit.createBossBar("§b◆ 幻翼", BarColor.BLUE, BarStyle.SOLID);
        phantomBar.setVisible(true);
        phantomBar.setProgress(1.0);

        vexBar = Bukkit.createBossBar("§d◆ 恼鬼", BarColor.PURPLE, BarStyle.SOLID);
        vexBar.setVisible(true);
        vexBar.setProgress(1.0);

        // ---- 注册幻翼为主实体(隐藏血条): 环境伤害保护 + 伤害识别; 恼鬼为身体部位 ----
        BossBar hiddenBar = Bukkit.createBossBar(" ", BarColor.WHITE, BarStyle.SOLID);
        BossMenu.registerBoss(BossMenu.BossType.PHANTOM_VEX, phantom, PHANTOM_HEALTH, hiddenBar);
        hiddenBar.setVisible(false);
        BossMenu.registerBodyStand(BossMenu.BossType.PHANTOM_VEX, vex.getUniqueId());

        // ---- 召唤特效 (不产生粒子, 保持隐身隐蔽) ----
        Location loc = phantom.getLocation();
        loc.getWorld().strikeLightningEffect(loc);
        loc.getWorld().playSound(loc, Sound.ENTITY_PHANTOM_AMBIENT, 1.0f, 0.6f);

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§b◆ 幻翼与恼鬼在 " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " 处降临了！");
        }

        bossAlive = true;
        noTargetTicks = 0;
        lastDeathLocation = null;
        startAI();
        startPhantomMove();
    }

    private static Location findSpawnLocation(Player summoner) {
        return BossSpawn.ringSpawn(summoner, 8);
    }

    // ========================================================================
    // AI
    // ========================================================================

    private static void startAI() {
        aiTask = new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                tick++;
                boolean phantomAlive = phantom != null && !phantom.isDead() && phantom.isValid();
                boolean vexAlive = vex != null && !vex.isDead() && vex.isValid();

                // 记录死亡位置: 后死的覆盖先死的, 掉落物随后死亡者位置生成
                if (phantom != null && phantomAlive) lastDeathLocation = phantom.getLocation();
                if (vex != null && vexAlive) lastDeathLocation = vex.getLocation();

                // 幻翼与恼鬼全部死亡 → BOSS被击败
                if (!phantomAlive && !vexAlive) {
                    onDeath();
                    cancel();
                    return;
                }

                // 异常: 任一生效实体缺失 → 清理
                if (phantom == null || vex == null) {
                    cleanup();
                    cancel();
                    return;
                }

                // 更新双BossBar
                if (phantomAlive) {
                    updateBar(phantomBar, phantom, PHANTOM_HEALTH, "§b◆ 幻翼");
                } else {
                    hideBar(phantomBar);
                }
                if (vexAlive) {
                    updateBar(vexBar, vex, VEX_HEALTH, "§d◆ 恼鬼");
                } else {
                    hideBar(vexBar);
                }

                // 同步幻翼血量到BossMenu (环境伤害保护识别)
                if (phantomAlive) {
                    BossMenu.syncBossHealth(BossMenu.BossType.PHANTOM_VEX, phantom.getHealth(), PHANTOM_HEALTH);
                }

                // 索敌: 锁定150格内最近的生存玩家; 幻翼靠手动飞行/俯冲, 恼鬼用原版AI; 伤害放大在BossMenu统一处理
                LivingEntity target = findNearestPlayer();
                if (target == null) {
                    noTargetTicks++;
                    phantomTarget = null;
                    if (noTargetTicks >= DESPAWN_TICKS) {
                        Bukkit.broadcastMessage("§e幻翼与恼鬼因无人应战而消失...");
                        despawn();
                        cancel();
                    }
                    return;
                }
                noTargetTicks = 0;

                if (phantomAlive) phantomTarget = target; // 幻翼由移动任务手动驱动盘旋/俯冲
                if (vexAlive) vex.setTarget(target);      // 恼鬼用原版AI
            }
        }.runTaskTimer(NamespaceKey.Keys.getplugin, 20L, 10L);
    }

    // ========================================================================
    // 幻翼手动飞行/俯冲
    // ========================================================================
    // 原版PhantomAttackPlayerGoal不对玩家生效(只对非玩家目标有效), 导致幻翼不会俯冲玩家;
    // 因此用1tick任务手动驱动: 盘旋在目标上方 → 定期俯冲 → 贴近目标身体时咬击。
    // 咬击通过 target.damage() 产生实体伤害事件, 由BossMenu按"幻翼×2穿透"统一放大。
    private static void startPhantomMove() {
        phantomMoveTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (phantom == null || phantom.isDead() || !phantom.isValid()) return;
                if (phantomTarget == null || phantomTarget.isDead()) return;
                phantomStrike(phantomTarget);
            }
        }.runTaskTimer(NamespaceKey.Keys.getplugin, 0L, 1L);
    }

    private static void phantomStrike(LivingEntity target) {
        if (phantom == null || phantom.isDead()) return;
        World world = phantom.getWorld();
        Location pl = phantom.getLocation();
        Location body = target.getLocation().clone().add(0, target.getEyeHeight() * 0.5, 0);
        Location hover = body.clone().add(0, PHANTOM_HOVER_HEIGHT, 0);
        double distBody = pl.distance(body);

        if (phantomDiveTicksLeft > 0) {
            // 俯冲中: 直冲目标身体
            phantom.setVelocity(dirToward(pl.toVector(), body.toVector(), PHANTOM_DIVE_SPEED));
            phantom.lookAt(body);
            phantomDiveTicksLeft--;
            // 命中: 贴近目标身体时咬击 (伤害×2穿透由BossMenu放大)
            if (distBody < 2.5 && phantomBiteCooldown <= 0) {
                target.damage(PHANTOM_BASE_DAMAGE, phantom);
                phantomBiteCooldown = 30;
                world.playSound(pl, Sound.ENTITY_PHANTOM_BITE, 1.2f, 1.0f);
                world.spawnParticle(Particle.SOUL, body, 24, 0.6, 0.6, 0.6, 0.06);
                phantomDiveTicksLeft = 0; // 结束俯冲, 回到盘旋
            }
        } else {
            // 盘旋: 飞向目标上方盘旋点, 到位后开始俯冲
            phantom.setVelocity(dirToward(pl.toVector(), hover.toVector(), PHANTOM_SPEED));
            phantom.lookAt(hover);
            double distHover = pl.distance(hover);
            if (distHover < 3.5 && phantomDiveCooldown <= 0 && pl.getY() > body.getY() + 2) {
                phantomDiveTicksLeft = 14;
                phantomDiveCooldown = 50; // 每次俯冲间隔2.5秒
            }
        }

        if (phantomDiveCooldown > 0) phantomDiveCooldown--;
        if (phantomBiteCooldown > 0) phantomBiteCooldown--;
    }

    // 从from指向to、长度为speed的方向向量 (两点重合时返回零向量)
    private static Vector dirToward(Vector from, Vector to, double speed) {
        Vector d = to.clone().subtract(from);
        if (d.lengthSquared() < 0.0001) return new Vector(0, 0, 0);
        return d.normalize().multiply(speed);
    }

    private static void updateBar(BossBar bar, LivingEntity entity, double maxHp, String baseTitle) {
        if (bar == null) return;
        double hp = Math.max(0, entity.getHealth());
        bar.setProgress(hp / maxHp);
        bar.setTitle(baseTitle + " §7" + Math.round(hp) + "/" + Math.round(maxHp));
        bar.setVisible(true);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().equals(entity.getWorld()) && p.getLocation().distance(entity.getLocation()) <= 150) {
                if (!bar.getPlayers().contains(p)) bar.addPlayer(p);
            } else {
                bar.removePlayer(p);
            }
        }
    }

    private static void hideBar(BossBar bar) {
        if (bar == null) return;
        bar.setVisible(false);
        bar.removeAll();
    }

    // ========================================================================
    // 死亡 & 消失
    // ========================================================================

    public static void onDeath() {
        if (!bossAlive) return; // 防止重复调用
        if (aiTask != null) aiTask.cancel();

        Location loc = lastDeathLocation != null ? lastDeathLocation : getBossLocation();
        if (loc == null) { cleanup(); return; }
        World world = loc.getWorld();

        world.strikeLightningEffect(loc);
        world.spawnParticle(Particle.SOUL, loc, 100, 3, 2, 3, 1);
        world.spawnParticle(Particle.EXPLOSION, loc, 5, 1, 1, 1, 0.1);
        world.playSound(loc, Sound.ENTITY_PHANTOM_DEATH, 1.0f, 0.5f);

        // 掉落物
        world.dropItemNaturally(loc, new ItemStack(Material.PHANTOM_MEMBRANE, 8));
        world.dropItemNaturally(loc, new ItemStack(Material.EXPERIENCE_BOTTLE, 32));
        world.dropItemNaturally(loc, ArmsorItem.MagicBallCreateI(1 + RANDOM.nextInt(2)));

        if (RANDOM.nextBoolean()) {
            world.dropItemNaturally(loc, ArmsorItem.Dodge_EnchantdeBook(1, 1 + RANDOM.nextInt(3)));
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§b◆ 幻翼与恼鬼已被击败！");
        }

        cleanup();
    }

    private static void despawn() {
        Location loc = getBossLocation();
        if (loc != null) {
            loc.getWorld().spawnParticle(Particle.SMOKE, loc, 40, 2, 2, 2, 0.1);
        }
        cleanup();
    }

    private static void cleanup() {
        if (aiTask != null) { aiTask.cancel(); aiTask = null; }
        if (phantomMoveTask != null) { phantomMoveTask.cancel(); phantomMoveTask = null; }

        BossMenu.unregisterBoss(BossMenu.BossType.PHANTOM_VEX);

        if (phantomBar != null) { phantomBar.removeAll(); phantomBar.setVisible(false); phantomBar = null; }
        if (vexBar != null) { vexBar.removeAll(); vexBar.setVisible(false); vexBar = null; }

        if (phantom != null && !phantom.isDead()) phantom.remove();
        if (vex != null && !vex.isDead()) vex.remove();

        phantom = null;
        vex = null;
        bossAlive = false;
        noTargetTicks = 0;
        lastDeathLocation = null;
        phantomTarget = null;
        phantomDiveCooldown = 0;
        phantomDiveTicksLeft = 0;
        phantomBiteCooldown = 0;
    }

    // ========================================================================
    // 工具
    // ========================================================================

    // 锁定150格内, 距离最近的生存模式存活玩家
    private static Player findNearestPlayer() {
        LivingEntity active = phantom;
        if (active == null || active.isDead()) active = vex;
        if (active == null || active.isDead()) return null;

        Player nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isDead() || p.isInvulnerable()) continue;
            if (p.getGameMode() != GameMode.SURVIVAL) continue;
            if (!p.getWorld().equals(active.getWorld())) continue;
            double dist = p.getLocation().distance(active.getLocation());
            if (dist <= TARGET_RANGE && dist < nearestDist) {
                nearestDist = dist;
                nearest = p;
            }
        }
        return nearest;
    }
}
