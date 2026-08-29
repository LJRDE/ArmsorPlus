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
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

// 熔岩双王 —— 烈焰人 + 岩浆史莱姆王 同时降临的组合BOSS (参照铁骑双雄的双实体模式)。
// 烈焰人(200血): 火焰弹/接触伤害均为原版10倍, 火焰弹命中额外15点爆炸; 血量降至75%/50%/25%各召唤2只烈焰人小弟(伤害减半x5, 攻速相同, 火焰弹命中额外5点爆炸)。
// 岩浆史莱姆王(750血): 无护甲纯坦克, 半血激怒分裂小史莱姆。
// 双BossBar分别显示二者血量; 烈焰人与岩浆史莱姆王都死亡才击败BOSS。
public class LavaDuoBoss {

    private static final double BLAZE_HEALTH = 200;
    private static final double MAGMA_HEALTH = 750;
    private static final int SLIME_SIZE = 15;
    private static final int FOLLOW_RANGE = 50;
    private static final int DESPAWN_TICKS = 1200;
    private static final double FIREBALL_SPEED = 2.5; // 原版小型火焰弹约1.8格/tick, 提升弹速
    private static final double MINION_HEALTH = 100; // 烈焰人小弟血量 (伤害为主烈焰人一半)

    // 火焰弹弹种标签: "explosive"=爆炸弹(目标有抗火) / "fire"=仅火焰伤害的火焰弹(目标无抗火)
    public static final NamespacedKey FIREBALL_KEY =
            new NamespacedKey(NamespaceKey.Keys.getplugin, "lava_duo_fireball");

    private static boolean bossAlive = false;
    private static Blaze blaze;
    private static MagmaCube magmaCube;
    private static BossBar blazeBar;
    private static BossBar magmaBar;
    private static BukkitTask aiTask;
    private static int noTargetTicks;
    private static final List<MagmaCube> minions = new ArrayList<>();
    private static final List<Blaze> blazeMinions = new ArrayList<>();
    private static boolean minion75 = false;
    private static boolean minion50 = false;
    private static boolean minion25 = false;
    private static boolean blazeWasAlive = false;
    private static boolean magmaWasAlive = false;
    private static Location lastDeathLocation;

    private static final Random RANDOM = new Random();

    public static boolean isAlive() { return bossAlive; }

    public static Location getBossLocation() {
        if (blaze != null && !blaze.isDead()) return blaze.getLocation();
        if (magmaCube != null && !magmaCube.isDead()) return magmaCube.getLocation();
        return null;
    }

    // ========================================================================
    // 召唤
    // ========================================================================

    public static void spawnBoss(Player summoner) {
        if (bossAlive) {
            summoner.sendMessage("§c熔岩双王已在战斗中，请先击败或等待其消失");
            return;
        }

        Location spawnLoc = findSpawnLocation(summoner);
        if (spawnLoc == null) {
            summoner.sendMessage("§c没有足够的空间召唤BOSS");
            return;
        }
        spawnLoc.getWorld().loadChunk(spawnLoc.getChunk());

        // ---- 烈焰人 (200血, 原生AI发射火焰弹) ----
        blaze = (Blaze) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.BLAZE);
        blaze.setCustomName("§e烈焰人");
        blaze.setCustomNameVisible(true);
        blaze.setRemoveWhenFarAway(false);
        blaze.setPersistent(true);
        var blazeHp = blaze.getAttribute(Attribute.MAX_HEALTH);
        if (blazeHp != null) blazeHp.setBaseValue(BLAZE_HEALTH);
        blaze.setHealth(BLAZE_HEALTH);
        blaze.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, 3, false, false));

        // ---- 岩浆史莱姆王 (750血, 巨型岩浆史莱姆, 无护甲无武器) ----
        magmaCube = (MagmaCube) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.MAGMA_CUBE);
        magmaCube.setSize(SLIME_SIZE);
        magmaCube.setCustomName("§c岩浆史莱姆王");
        magmaCube.setCustomNameVisible(true);
        magmaCube.setRemoveWhenFarAway(false);
        magmaCube.setPersistent(true);
        var magmaHp = magmaCube.getAttribute(Attribute.MAX_HEALTH);
        if (magmaHp != null) magmaHp.setBaseValue(MAGMA_HEALTH);
        magmaCube.setHealth(MAGMA_HEALTH);

        // 取消岩浆史莱姆的所有护甲/装备
        magmaCube.getEquipment().clear();

        // 手持史莱姆王同款重锤 (纯外观, 不掉落)
        magmaCube.getEquipment().setItemInMainHand(SlimeBoss.createBossMace());
        magmaCube.getEquipment().setItemInMainHandDropChance(0f);

        // ---- 双BossBar ----
        blazeBar = Bukkit.createBossBar("§e◆ 烈焰人", BarColor.YELLOW, BarStyle.SOLID);
        blazeBar.setVisible(true);
        blazeBar.setProgress(1.0);

        magmaBar = Bukkit.createBossBar("§c◆ 岩浆史莱姆王", BarColor.RED, BarStyle.SOLID);
        magmaBar.setVisible(true);
        magmaBar.setProgress(1.0);

        // ---- 注册烈焰人为主实体(隐藏血条): 环境伤害保护 + 火焰弹/接触伤害识别 ----
        BossBar hiddenBar = Bukkit.createBossBar(" ", BarColor.WHITE, BarStyle.SOLID);
        BossMenu.registerBoss(BossMenu.BossType.LAVA_DUO, blaze, BLAZE_HEALTH, hiddenBar);
        hiddenBar.setVisible(false);
        BossMenu.registerBodyStand(BossMenu.BossType.LAVA_DUO, magmaCube.getUniqueId());

        // ---- 召唤特效 ----
        Location loc = blaze.getLocation();
        loc.getWorld().strikeLightningEffect(loc);
        loc.getWorld().spawnParticle(Particle.LAVA, loc, 100, 3, 2, 3, 1);
        loc.getWorld().spawnParticle(Particle.FLAME, loc, 80, 3, 2, 3, 0.3);
        loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.6f);

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§c◆ 熔岩双王在 " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " 处降临了！");
        }

        bossAlive = true;
        noTargetTicks = 0;
        blazeWasAlive = true;
        magmaWasAlive = true;
        lastDeathLocation = null;
        blazeMinions.clear();
        minion75 = false;
        minion50 = false;
        minion25 = false;
        startAI();
    }

    private static Location findSpawnLocation(Player summoner) {
        return BossSpawn.ringSpawn(summoner, 6);
    }

    // ========================================================================
    // AI
    // ========================================================================

    private static void startAI() {
        aiTask = new BukkitRunnable() {
            int tick = 0;
            boolean phase2 = false;

            @Override
            public void run() {
                tick++;
                boolean blazeAlive = blaze != null && !blaze.isDead() && blaze.isValid();
                boolean magmaAlive = magmaCube != null && !magmaCube.isDead() && magmaCube.isValid();

                // 记录死亡位置: 后死的覆盖先死的, 掉落物随后死亡者位置生成
                if (blaze != null && blazeWasAlive && !blazeAlive) lastDeathLocation = blaze.getLocation();
                if (magmaCube != null && magmaWasAlive && !magmaAlive) lastDeathLocation = magmaCube.getLocation();
                blazeWasAlive = blazeAlive;
                magmaWasAlive = magmaAlive;

                // 烈焰人与岩浆史莱姆王全部死亡 → BOSS被击败
                if (!blazeAlive && !magmaAlive) {
                    onDeath();
                    cancel();
                    return;
                }

                // 异常: 任一生效实体缺失 → 清理
                if (blaze == null || magmaCube == null) {
                    cleanup();
                    cancel();
                    return;
                }

                // 更新双BossBar
                if (blazeAlive) {
                    updateBar(blazeBar, blaze, BLAZE_HEALTH, "§e◆ 烈焰人");
                } else {
                    hideBar(blazeBar);
                }
                if (magmaAlive) {
                    updateBar(magmaBar, magmaCube, MAGMA_HEALTH, "§c◆ 岩浆史莱姆王");
                } else {
                    hideBar(magmaBar);
                }

                // 同步烈焰人血量到BossMenu (环境伤害保护识别)
                if (blazeAlive) {
                    BossMenu.syncBossHealth(BossMenu.BossType.LAVA_DUO, blaze.getHealth(), BLAZE_HEALTH);
                }

                // 岩浆史莱姆王: 无护甲纯坦克, 保留半血激怒分裂
                if (magmaAlive) {
                    if (magmaCube.getHealth() / MAGMA_HEALTH < 0.5 && !phase2) {
                        phase2 = true;
                        magmaBar.setColor(BarColor.YELLOW);
                        enrage();
                    }

                    Location ml = magmaCube.getLocation();
                    ml.getWorld().spawnParticle(Particle.LAVA, ml.clone().add(0, 0.5, 0), 2, 0.5, 0.3, 0.5, 0);
                }

                // 烈焰人: 粒子装饰
                if (blazeAlive) {
                    Location bl = blaze.getLocation();
                    bl.getWorld().spawnParticle(Particle.FLAME, bl.clone().add(0, 1, 0), 3, 1.5, 0.5, 1.5, 0.02);
                }

                // 索敌 (仇怨目标优先; 原版AI负责追击/接触伤害)
                LivingEntity target = Enmity.getEnemy(blaze, magmaCube);
                if (target == null) target = findNearestPlayer();
                if (target == null) {
                    noTargetTicks++;
                    if (noTargetTicks >= DESPAWN_TICKS) {
                        Bukkit.broadcastMessage("§c熔岩双王因无人应战而消失...");
                        despawn();
                        cancel();
                    }
                    return;
                }
                noTargetTicks = 0;

                if (blazeAlive) {
                    blaze.setTarget(target);
                    checkBlazeMinionSpawn();
                    // 提升攻击速度: AI循环周期10tick, 每次循环连发2枚精确直射火焰弹
                    blazeRapidFire(blaze, target);
                    // 烈焰人小弟: 攻速与主烈焰人相同, 每10tick一次连发
                    blazeMinions.removeIf(m -> m.isDead() || !m.isValid());
                    for (Blaze m : blazeMinions) {
                        m.setTarget(target);
                        blazeRapidFire(m, target);
                    }
                }
                if (magmaAlive) magmaCube.setTarget(target);
            }
        }.runTaskTimer(NamespaceKey.Keys.getplugin, 20L, 10L);
    }

    private static void updateBar(BossBar bar, LivingEntity entity, double maxHp, String baseTitle) {
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
    // 烈焰人技能: 快速连发火焰弹
    // ========================================================================

    private static void blazeRapidFire(Blaze shooter, LivingEntity target) {
        if (shooter == null || shooter.isDead()) return;

        Location eye = shooter.getEyeLocation();
        Location targetLoc = target.getLocation().add(0, target.getEyeHeight() * 0.6, 0);
        double dist = eye.distance(targetLoc);
        if (dist < 0.5) return;

        // 弹道修正: 按火焰弹飞行时间预测目标移动位置, 带提前量命中
        targetLoc.add(target.getVelocity().multiply(dist / FIREBALL_SPEED));

        Vector base = targetLoc.toVector().subtract(eye.toVector()).normalize();

        // 自适应弹种: 目标有抗火 → 爆炸弹(无视抗火); 目标无抗火 → 仅火焰伤害的火焰弹
        boolean explosive = target.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE);

        // 连发2枚火焰弹, 精确直射 + 弹速提升; 弹种标签供BossMenu按命中方式结算
        for (int i = 0; i < 2; i++) {
            SmallFireball fb = shooter.launchProjectile(SmallFireball.class, base);
            fb.setVelocity(base.clone().multiply(FIREBALL_SPEED));
            fb.getPersistentDataContainer().set(FIREBALL_KEY, PersistentDataType.STRING,
                    explosive ? "explosive" : "fire");
        }
        eye.getWorld().playSound(eye, Sound.ENTITY_BLAZE_SHOOT, 0.8f, 1.2f);
        eye.getWorld().spawnParticle(Particle.FLAME, eye, 8, 0.2, 0.2, 0.2, 0.05);
    }

    // ========================================================================
    // 烈焰人小弟: 血量降至75%/50%/25%各召唤2只
    // ========================================================================

    private static void checkBlazeMinionSpawn() {
        if (blaze == null || blaze.isDead()) return;
        double ratio = blaze.getHealth() / BLAZE_HEALTH;
        if (ratio < 0.75 && !minion75) { minion75 = true; summonBlazeMinions(); }
        if (ratio < 0.50 && !minion50) { minion50 = true; summonBlazeMinions(); }
        if (ratio < 0.25 && !minion25) { minion25 = true; summonBlazeMinions(); }
    }

    // 召唤2只烈焰人小弟: 伤害为主烈焰人的一半(x5), 攻速相同(每10tick连发), 火焰弹命中额外5点爆炸
    private static void summonBlazeMinions() {
        if (blaze == null || blaze.isDead()) return;
        Location base = blaze.getLocation();
        World world = base.getWorld();

        for (int i = 0; i < 2; i++) {
            double angle = i * Math.PI; // 左右两侧
            Location loc = base.clone().add(Math.cos(angle) * 3, 0, Math.sin(angle) * 3);
            loc.setY(BossSpawn.groundY(world, loc.getBlockX(), loc.getBlockZ(), base.getBlockY()));

            Blaze m = (Blaze) world.spawnEntity(loc, EntityType.BLAZE);
            m.setCustomName("§c烈焰人小弟");
            m.setCustomNameVisible(true);
            m.setRemoveWhenFarAway(false);
            m.setPersistent(true);
            var hp = m.getAttribute(Attribute.MAX_HEALTH);
            if (hp != null) hp.setBaseValue(MINION_HEALTH);
            m.setHealth(MINION_HEALTH);
            m.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, 3, false, false));
            m.setTarget(blaze.getTarget());
            blazeMinions.add(m);
            // 同队实体: 小弟不与岩浆怪/烈焰人互相伤害
            BossMenu.registerFriendlyEntity(BossMenu.BossType.LAVA_DUO, m);

            world.spawnParticle(Particle.FLAME, loc, 30, 1, 1, 1, 0.1);
            world.playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 0.8f);
        }
        world.playSound(base, Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.7f);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().equals(world) && p.getLocation().distance(base) <= 150) {
                p.sendMessage("§e🔥 烈焰人召唤了2只烈焰人小弟！");
            }
        }
    }

    // 供BossMenu判别: 玩家是否被烈焰人小弟的火焰弹/本体命中 (伤害减半)
    public static boolean isMinion(UUID id) {
        for (Blaze m : blazeMinions) {
            if (m != null && m.isValid() && m.getUniqueId().equals(id)) return true;
        }
        return false;
    }

    // ========================================================================
    // 岩浆史莱姆王技能
    // ========================================================================

    private static void enrage() {
        if (magmaCube == null || magmaCube.isDead()) return;

        Location loc = magmaCube.getLocation();
        loc.getWorld().spawnParticle(Particle.ENCHANT, loc, 100, 2, 2, 2, 0.5);
        loc.getWorld().spawnParticle(Particle.LAVA, loc, 80, 2, 2, 2, 1);
        loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.8f);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getWorld().equals(loc.getWorld())
                    && online.getLocation().distance(loc) <= 150) {
                online.sendMessage("§c⚡ 岩浆史莱姆王进入了激怒状态！攻击更加频繁！");
            }
        }

        splitMinions();
    }

    private static void splitMinions() {
        if (magmaCube == null || magmaCube.isDead()) return;

        Location loc = magmaCube.getLocation();
        World world = loc.getWorld();

        minions.removeIf(s -> s.isDead() || !s.isValid());
        if (minions.size() >= 6) return;

        for (int i = 0; i < 3; i++) {
            double xOff = (RANDOM.nextDouble() - 0.5) * 5;
            double zOff = (RANDOM.nextDouble() - 0.5) * 5;
            Location spawnLoc = loc.clone().add(xOff, 0, zOff);

            MagmaCube small = (MagmaCube) world.spawnEntity(spawnLoc, EntityType.MAGMA_CUBE);
            small.setSize(3 + RANDOM.nextInt(3));
            small.setCustomName("§c小岩浆史莱姆");
            small.setCustomNameVisible(true);
            small.setRemoveWhenFarAway(true);

            var hp = small.getAttribute(Attribute.MAX_HEALTH);
            if (hp != null) hp.setBaseValue(30);
            small.setHealth(30);

            minions.add(small);

            world.spawnParticle(Particle.LAVA, spawnLoc, 10, 0.5, 0.3, 0.5, 1);
        }
        world.playSound(loc, Sound.ENTITY_MAGMA_CUBE_SQUISH, 0.8f, 1.2f);
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
        world.createExplosion(loc, 0f, false, false);
        world.spawnParticle(Particle.LAVA, loc, 100, 3, 2, 3, 1);
        world.spawnParticle(Particle.FLAME, loc, 80, 2, 2, 2, 0.5);
        world.playSound(loc, Sound.ENTITY_WITHER_DEATH, 1.0f, 0.5f);

        // 掉落物
        world.dropItemNaturally(loc, new ItemStack(Material.MAGMA_CREAM, 24));
        world.dropItemNaturally(loc, new ItemStack(Material.BLAZE_ROD, 4 + RANDOM.nextInt(4)));
        world.dropItemNaturally(loc, new ItemStack(Material.MAGMA_BLOCK, 8));
        world.dropItemNaturally(loc, new ItemStack(Material.EXPERIENCE_BOTTLE, 48));
        world.dropItemNaturally(loc, ArmsorItem.MagicBallCreateI(1 + RANDOM.nextInt(2)));

        if (RANDOM.nextBoolean()) {
            world.dropItemNaturally(loc, ArmsorItem.Dodge_EnchantdeBook(1, 1 + RANDOM.nextInt(3)));
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§c◆ 熔岩双王已被击败！");
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

        BossMenu.unregisterBoss(BossMenu.BossType.LAVA_DUO);

        if (blazeBar != null) { blazeBar.removeAll(); blazeBar.setVisible(false); blazeBar = null; }
        if (magmaBar != null) { magmaBar.removeAll(); magmaBar.setVisible(false); magmaBar = null; }

        for (MagmaCube s : minions) {
            if (s != null && !s.isDead()) s.remove();
        }
        minions.clear();

        for (Blaze m : blazeMinions) {
            if (m != null && !m.isDead()) m.remove();
        }
        blazeMinions.clear();
        minion75 = false;
        minion50 = false;
        minion25 = false;

        if (blaze != null && !blaze.isDead()) blaze.remove();
        if (magmaCube != null && !magmaCube.isDead()) magmaCube.remove();

        blaze = null;
        magmaCube = null;
        bossAlive = false;
        noTargetTicks = 0;
        blazeWasAlive = false;
        magmaWasAlive = false;
        lastDeathLocation = null;
    }

    // ========================================================================
    // 工具
    // ========================================================================

    private static Player findNearestPlayer() {
        LivingEntity active = blaze;
        if (active == null || active.isDead()) active = magmaCube;
        if (active == null || active.isDead()) return null;

        Player nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (Entity entity : active.getNearbyEntities(FOLLOW_RANGE, 10, FOLLOW_RANGE)) {
            if (entity instanceof Player p && BossTargets.isCombatPlayer(p)) {
                double dist = p.getLocation().distance(active.getLocation());
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = p;
                }
            }
        }
        return nearest;
    }

    private static boolean isTarget(Player player) {
        LivingEntity active = blaze;
        if (active == null || active.isDead()) active = magmaCube;
        if (active == null || active.isDead()) return false;
        return player.getLocation().distance(active.getLocation()) <= FOLLOW_RANGE * 1.5;
    }
}
