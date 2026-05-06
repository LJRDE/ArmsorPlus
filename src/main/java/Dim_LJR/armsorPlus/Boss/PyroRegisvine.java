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

import java.util.Random;
import java.util.UUID;

import static org.bukkit.Material.*;

/**
 * 爆炎树 —— 火元素BOSS (原神: 爆炎树)
 * <p>
 * 本体隐身无AI僵尸，装饰为无AI隐身僵尸佩戴下界岩/岩浆块。
 * 3×3树干向上延伸，5条半圆形枝干环绕，粒子火焰装饰。
 */
public class PyroRegisvine {

    private static final double MAX_HEALTH = 750;
    private static final int ATTACK_RADIUS = 8;
    private static final int FOLLOW_RANGE = 50;

    private static boolean bossAlive = false;
    private static LivingEntity bossEntity;
    private static BukkitTask aiTask;
    private static UUID bossUuid;
    private static BossBar bossBar;

    // 僵尸躯干 (树状结构)
    private static LivingEntity coreStand;
    private static final java.util.List<LivingEntity> bodyStands = new java.util.ArrayList<>();

    private static final Random RANDOM = new Random();

    // ========================================================================
    // 召唤
    // ========================================================================

    public static void spawnBoss(Player summoner) {
        if (bossAlive) {
            summoner.sendMessage("§c已有一只爆炎树，请先击败或等待其消失");
            return;
        }

        Location spawnLoc = findSpawnLocation(summoner);
        if (spawnLoc == null) {
            summoner.sendMessage("§c没有足够的空间召唤BOSS");
            return;
        }

        spawnLoc.getWorld().loadChunk(spawnLoc.getChunk());

        // ---- 隐身本体 (存血量/位置) ----
        bossEntity = (LivingEntity) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ZOMBIE);
        bossEntity.setCustomName("§c■ 爆炎树 §7Lv.90");
        bossEntity.setCustomNameVisible(false);
        bossEntity.setRemoveWhenFarAway(false);
        bossEntity.setPersistent(true);
        bossEntity.setAI(false);
        bossEntity.setCollidable(false);
        bossEntity.setSilent(true);
        bossEntity.setInvulnerable(true);
        bossEntity.getEquipment().clear();
        bossEntity.eject();

        var maxHpAttr = bossEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHpAttr != null) maxHpAttr.setBaseValue(MAX_HEALTH);
        bossEntity.setHealth(MAX_HEALTH);

        bossEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, -1, 1, false, false));
        bossEntity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 0, false, false));
        bossEntity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, 4, false, false));

        bossUuid = bossEntity.getUniqueId();

        // ---- BossBar ----
        bossBar = Bukkit.createBossBar("§c■ 爆炎树", BarColor.RED, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(1.0);

        BossMenu.registerBoss(BossMenu.BossType.PYRO, bossEntity, MAX_HEALTH, bossBar);

        // ---- 僵尸树状结构 ----
        spawnTree(spawnLoc);

        // ---- 召唤特效 ----
        Location bossLoc = bossEntity.getLocation();
        bossLoc.getWorld().strikeLightningEffect(bossLoc);
        bossLoc.getWorld().spawnParticle(Particle.LAVA, bossLoc, 100, 2, 2, 2, 1);
        bossLoc.getWorld().spawnParticle(Particle.FLAME, bossLoc, 80, 2, 2, 2, 0.3);
        bossLoc.getWorld().playSound(bossLoc, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.5f);

        String msg = "§c◆ 爆炎树在 " + bossLoc.getBlockX() + " " + bossLoc.getBlockY() + " " + bossLoc.getBlockZ() + " 处降临了！";
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(msg);
        }

        bossAlive = true;
        startAI();
    }

    private static Location findSpawnLocation(Player player) {
        Location base = player.getLocation();
        Vector dir = base.getDirection().multiply(8);
        Location target = base.clone().add(dir);
        target.setY(target.getWorld().getHighestBlockYAt(target) + 1);

        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                Location check = target.clone().add(x, 0, z);
                if (!check.getBlock().isPassable()) return null;
                for (int y = 1; y <= 6; y++) {
                    Location up = check.clone().add(0, y, 0);
                    if (!up.getBlock().isPassable()) return null;
                }
            }
        }
        return target;
    }

    // ========================================================================
    // 僵尸树状结构
    // ========================================================================

    /**
     * 生成由无AI隐身僵尸+带头方块组成的火树。
     * 树干3×3剖面向上收窄，5条半圆枝干向外伸展。
     */
    private static void spawnTree(Location center) {
        // ========== 树干 (3×3 逐渐收窄) ==========

        // 第1层-底部 (3×3 满铺, NETHERRACK)
        spawnTrunk(center, -1, -1.0, -1, NETHERRACK);
        spawnTrunk(center, -1, -1.0,  0, NETHERRACK);
        spawnTrunk(center, -1, -1.0,  1, NETHERRACK);
        spawnTrunk(center,  0, -1.0, -1, NETHERRACK);
        spawnTrunk(center,  0, -1.0,  0, NETHERRACK);
        spawnTrunk(center,  0, -1.0,  1, NETHERRACK);
        spawnTrunk(center,  1, -1.0, -1, NETHERRACK);
        spawnTrunk(center,  1, -1.0,  0, NETHERRACK);
        spawnTrunk(center,  1, -1.0,  1, NETHERRACK);

        // 第2层 (3×3 满铺, NETHERRACK)
        spawnTrunk(center, -1,  0.2, -1, NETHERRACK);
        spawnTrunk(center, -1,  0.2,  0, NETHERRACK);
        spawnTrunk(center, -1,  0.2,  1, NETHERRACK);
        spawnTrunk(center,  0,  0.2, -1, NETHERRACK);
        spawnTrunk(center,  0,  0.2,  0, NETHERRACK);
        spawnTrunk(center,  0,  0.2,  1, NETHERRACK);
        spawnTrunk(center,  1,  0.2, -1, NETHERRACK);
        spawnTrunk(center,  1,  0.2,  0, NETHERRACK);
        spawnTrunk(center,  1,  0.2,  1, NETHERRACK);

        // 第3层 (3×3 满铺, MAGMA_BLOCK)
        spawnTrunk(center, -1,  1.4, -1, MAGMA_BLOCK);
        spawnTrunk(center, -1,  1.4,  0, MAGMA_BLOCK);
        spawnTrunk(center, -1,  1.4,  1, MAGMA_BLOCK);
        spawnTrunk(center,  0,  1.4, -1, MAGMA_BLOCK);
        spawnTrunk(center,  0,  1.4,  0, MAGMA_BLOCK);
        spawnTrunk(center,  0,  1.4,  1, MAGMA_BLOCK);
        spawnTrunk(center,  1,  1.4, -1, MAGMA_BLOCK);
        spawnTrunk(center,  1,  1.4,  0, MAGMA_BLOCK);
        spawnTrunk(center,  1,  1.4,  1, MAGMA_BLOCK);

        // 第4层 (3×3 边角, MAGMA_BLOCK→SHROOMLIGHT 过渡)
        spawnTrunk(center, -0.5, 2.6, -1, MAGMA_BLOCK);
        spawnTrunk(center, -0.5, 2.6,  1, MAGMA_BLOCK);
        spawnTrunk(center,  0.5, 2.6, -1, MAGMA_BLOCK);
        spawnTrunk(center,  0.5, 2.6,  1, MAGMA_BLOCK);

        // 第5层 (单点核心, SHROOMLIGHT)
        coreStand = spawnTreeZombie(center, 0, 3.6, 0, SHROOMLIGHT);
        BossMenu.registerBodyStand(BossMenu.BossType.PYRO, coreStand.getUniqueId());

        // ========== 5条半圆形枝干 ==========

        // 枝干1 (+X 方向, y≈1.2)
        spawnBranch(center,  1.5, -0.3,  0,   NETHERRACK);
        spawnBranch(center,  2.2, -0.3,  0.5, MAGMA_BLOCK);
        spawnBranch(center,  2.2, -0.3, -0.5, MAGMA_BLOCK);
        spawnBranch(center,  2.8, -0.3,  0,   SHROOMLIGHT);

        // 枝干2 (-X 方向, y≈1.8)
        spawnBranch(center, -1.5,  0.3,  0,   NETHERRACK);
        spawnBranch(center, -2.2,  0.3,  0.5, MAGMA_BLOCK);
        spawnBranch(center, -2.2,  0.3, -0.5, MAGMA_BLOCK);
        spawnBranch(center, -2.8,  0.3,  0,   SHROOMLIGHT);

        // 枝干3 (+Z 方向, y≈2.4)
        spawnBranch(center,  0,   0.9,  1.5, NETHERRACK);
        spawnBranch(center,  0.5, 0.9,  2.2, MAGMA_BLOCK);
        spawnBranch(center, -0.5, 0.9,  2.2, MAGMA_BLOCK);
        spawnBranch(center,  0,   0.9,  2.8, SHROOMLIGHT);

        // 枝干4 (-Z 方向, y≈3.0)
        spawnBranch(center,  0,   1.5, -1.5, NETHERRACK);
        spawnBranch(center,  0.5, 1.5, -2.2, MAGMA_BLOCK);
        spawnBranch(center, -0.5, 1.5, -2.2, MAGMA_BLOCK);
        spawnBranch(center,  0,   1.5, -2.8, SHROOMLIGHT);

        // 枝干5 (花冠散开, y≈3.8)
        spawnBranch(center,  1.2, 2.3,  1.2, MAGMA_BLOCK);
        spawnBranch(center, -1.2, 2.3,  1.2, MAGMA_BLOCK);
        spawnBranch(center,  1.2, 2.3, -1.2, MAGMA_BLOCK);
        spawnBranch(center, -1.2, 2.3, -1.2, SHROOMLIGHT);
    }

    /** 树干节点 */
    private static void spawnTrunk(Location center, double x, double y, double z, Material head) {
        spawnBodyZombie(center, x, y, z, head);
    }

    /** 枝干节点 */
    private static void spawnBranch(Location center, double x, double y, double z, Material head) {
        spawnBodyZombie(center, x, y, z, head);
    }

    private static void spawnBodyZombie(Location center, double x, double y, double z, Material head) {
        LivingEntity zombie = spawnTreeZombie(center, x, y, z, head);
        bodyStands.add(zombie);
        BossMenu.registerBodyStand(BossMenu.BossType.PYRO, zombie.getUniqueId());
    }

    /** 生成无AI隐身僵尸，佩戴指定方块作为头盔 */
    private static LivingEntity spawnTreeZombie(Location center, double x, double y, double z, Material head) {
        Location loc = center.clone().add(x, y, z);
        LivingEntity zombie = (LivingEntity) center.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
        zombie.setAI(false);
        zombie.setSilent(true);
        zombie.setCollidable(false);
        zombie.setRemoveWhenFarAway(false);
        zombie.setPersistent(true);
        zombie.setInvisible(true);
        zombie.getEquipment().clear();
        zombie.getEquipment().setHelmet(new ItemStack(head), true);
        zombie.eject();
        return zombie;
    }

    // ========================================================================
    // 火焰弹雨 — 从天而降的火焰弹攻击
    // ========================================================================

    private static void fireballRainAttack(Player target) {
        Location targetLoc = target.getLocation();
        World world = targetLoc.getWorld();

        world.playSound(targetLoc, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 0.5f);
        target.sendActionBar("§c❄ 火焰弹来了，快躲开！");

        new BukkitRunnable() {
            int wave = 0;

            @Override
            public void run() {
                if (bossEntity == null || bossEntity.isDead()) {
                    cancel();
                    return;
                }
                wave++;
                if (wave > 5) {
                    cancel();
                    return;
                }

                for (int i = 0; i < 6; i++) {
                    double xOffset = (RANDOM.nextDouble() - 0.5) * 7;
                    double zOffset = (RANDOM.nextDouble() - 0.5) * 7;

                    Location from = targetLoc.clone().add(xOffset, 14, zOffset);
                    world.spawnParticle(Particle.FLAME, from, 8, 0.4, 0.4, 0.4, 0.03);
                    world.spawnParticle(Particle.SMOKE, from, 4, 0.3, 0.3, 0.3, 0.01);

                    Location hitLoc = targetLoc.clone().add(xOffset, 0, zOffset);
                    for (Entity entity : world.getNearbyEntities(hitLoc, 1.8, 3, 1.8)) {
                        if (entity instanceof Player p && !p.isDead() && isTarget(p)) {
                            p.damage(10, bossEntity);
                            p.setFireTicks(80);
                        }
                    }

                    world.spawnParticle(Particle.LAVA, hitLoc, 15, 0.5, 0.3, 0.5, 0.5);
                    world.playSound(hitLoc, Sound.BLOCK_FIRE_EXTINGUISH, 0.6f, 1.0f);
                }
            }
        }.runTaskTimer(NamespaceKey.Keys.getplugin, 0L, 12L);
    }

    // ========================================================================
    // 横扫 — 前方6.5m喷射 + 旋转1080°
    // ========================================================================

    private static void sweepAttack(Player target) {
        Location bossLoc = bossEntity.getLocation();
        World world = bossLoc.getWorld();

        lookAtTarget(bossEntity, target.getLocation());

        // Phase 1: 前方6.5m喷射
        Vector dir = bossLoc.getDirection();
        for (double d = 0; d < 6.5; d += 0.5) {
            Location point = bossLoc.clone().add(dir.clone().multiply(d));
            world.spawnParticle(Particle.FLAME, point, 2, 0.2, 0.2, 0.2, 0.02);
            world.spawnParticle(Particle.SMOKE, point, 1, 0.1, 0.1, 0.1, 0);
        }
        world.playSound(bossLoc, Sound.ENTITY_BLAZE_SHOOT, 1.5f, 0.8f);

        for (Entity entity : world.getNearbyEntities(bossLoc, 6.5, 2, 6.5)) {
            if (entity instanceof Player p && !p.isDead() && isTarget(p)) {
                Vector toEntity = p.getLocation().toVector().subtract(bossLoc.toVector());
                double dist = toEntity.length();
                double angle = dir.angle(toEntity);
                if (angle < Math.toRadians(25) && dist <= 6.5) {
                    p.damage(12, bossEntity);
                    p.setFireTicks(60);
                }
            }
        }

        // Phase 2: 旋转1080° (3圈, 约3秒 = 60 ticks, 18°/tick)
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (bossEntity == null || bossEntity.isDead()) {
                    cancel();
                    return;
                }
                tick++;
                if (tick > 60) {
                    cancel();
                    return;
                }

                Location loc = bossEntity.getLocation();
                loc.setYaw(loc.getYaw() + 18);
                bossEntity.teleport(loc);

                Vector v = loc.getDirection();
                for (double d = 0; d < 6.5; d += 0.8) {
                    Location point = loc.clone().add(v.clone().multiply(d));
                    world.spawnParticle(Particle.FLAME, point, 1, 0.15, 0.15, 0.15, 0.01);
                    world.spawnParticle(Particle.SMOKE, point, 1, 0.1, 0.1, 0.1, 0);
                }

                for (Entity entity : world.getNearbyEntities(loc, 6.5, 2, 6.5)) {
                    if (entity instanceof Player p && !p.isDead() && isTarget(p)) {
                        Vector toEntity = p.getLocation().toVector().subtract(loc.toVector());
                        double dist = toEntity.length();
                        double angle = v.angle(toEntity);
                        if (angle < Math.toRadians(20) && dist <= 6.5) {
                            p.damage(8, bossEntity);
                            p.setFireTicks(40);
                        }
                    }
                }

                world.spawnParticle(Particle.SWEEP_ATTACK, loc.clone().add(0, 0.5, 0), 1, 0.5, 0, 0.5, 0);
                if (tick % 4 == 0) {
                    world.playSound(loc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.4f, 0.8f);
                }
            }
        }.runTaskTimer(NamespaceKey.Keys.getplugin, 0L, 1L);
    }

    // ========================================================================
    // 核心暴露
    // ========================================================================

    public static void exposeCore() {
        if (coreStand == null || coreStand.isDead() || bossEntity == null || bossEntity.isDead()) return;

        BossMenu.upgradeBodyToCore(BossMenu.BossType.PYRO, coreStand.getUniqueId());
        coreStand.setGlowing(true);

        Location loc = coreStand.getLocation();
        bossEntity.getWorld().spawnParticle(Particle.ENCHANT, loc, 60, 0.5, 0.5, 0.5, 0.5);
        bossEntity.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.5f);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getWorld().equals(bossEntity.getWorld())
                    && online.getLocation().distance(bossEntity.getLocation()) <= 150) {
                online.sendMessage("§e✦ 爆炎树的核心暴露了！攻击核心造成大量伤害！");
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                hideCore();
            }
        }.runTaskLater(NamespaceKey.Keys.getplugin, 100L);
    }

    private static void hideCore() {
        if (coreStand == null || coreStand.isDead()) return;
        BossMenu.downgradeCoreToBody(BossMenu.BossType.PYRO, coreStand.getUniqueId());
        coreStand.setGlowing(false);
        if (bossEntity != null && !bossEntity.isDead()) {
            bossEntity.getWorld().playSound(bossEntity.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.0f);
        }
    }

    // ========================================================================
    // AI
    // ========================================================================

    private static void startAI() {
        aiTask = new BukkitRunnable() {
            int attackCooldown = 0;
            int attacksSinceCore = 0;
            boolean phase2 = false;
            int tick = 0;

            @Override
            public void run() {
                tick++;

                if (bossEntity == null || bossEntity.isDead() || !bossEntity.isValid()) {
                    onDeath();
                    cancel();
                    return;
                }

                BossMenu.updateBossBar(BossMenu.BossType.PYRO);

                double hpPercent = BossMenu.getBossHealth(BossMenu.BossType.PYRO) / MAX_HEALTH;
                phase2 = hpPercent < 0.5;

                if (phase2) {
                    bossBar.setColor(BarColor.WHITE);
                }

                // 粒子效果装饰 — 火焰升腾
                Location bossLoc = bossEntity.getLocation();
                bossLoc.getWorld().spawnParticle(Particle.FLAME,
                        bossLoc.clone().add(0, 1, 0), 3, 1.5, 0.5, 1.5, 0.02);

                Player target = findNearestPlayer();
                if (target == null) {
                    if (tick > 600) {
                        despawn();
                        cancel();
                    }
                    return;
                }
                tick = 0;

                lookAtTarget(bossEntity, target.getLocation());

                if (attackCooldown > 0) {
                    attackCooldown--;
                    return;
                }

                int r = RANDOM.nextInt(phase2 ? 6 : 5);
                int attack = (!phase2 && r >= 3) ? r + 1 : r;
                switch (attack) {
                    case 0 -> fireBallAttack(target);
                    case 1 -> flameAura();
                    case 2 -> flameBreath(target);
                    case 3 -> magmaExplosion(target);
                    case 4 -> fireballRainAttack(target);
                    case 5 -> sweepAttack(target);
                }

                attacksSinceCore++;
                if (attacksSinceCore >= 3) {
                    exposeCore();
                    attacksSinceCore = 0;
                    attackCooldown = phase2 ? 5 : 8;
                } else {
                    attackCooldown = phase2 ? 5 : 8;
                }
            }
        }.runTaskTimer(NamespaceKey.Keys.getplugin, 20L, 10L);
    }

    // ========================================================================
    // 攻击方式
    // ========================================================================

    private static void fireBallAttack(Player target) {
        Location bossLoc = bossEntity.getEyeLocation();
        Location targetLoc = target.getLocation().add(0, 1, 0);
        World world = bossLoc.getWorld();

        Vector dir = targetLoc.toVector().subtract(bossLoc.toVector()).normalize();
        double distance = bossLoc.distance(targetLoc);
        for (double d = 0; d < distance; d += 0.8) {
            Location point = bossLoc.clone().add(dir.clone().multiply(d));
            world.spawnParticle(Particle.FLAME, point, 1, 0, 0, 0, 0);
            world.spawnParticle(Particle.SMOKE, point, 1, 0, 0, 0, 0);
        }
        world.playSound(bossLoc, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.2f);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (bossEntity == null || bossEntity.isDead()) return;
                world.createExplosion(targetLoc, 2.0f, false, false);
                world.spawnParticle(Particle.LAVA, targetLoc, 40, 1, 1, 1, 0.5);

                for (Entity entity : world.getNearbyEntities(targetLoc, 2.5, 2.5, 2.5)) {
                    if (entity instanceof Player p && !p.isDead() && isTarget(p)) {
                        p.damage(12, bossEntity);
                        p.setFireTicks(80);
                        p.sendActionBar("§c❄ 你受到了火球灼烧");
                    }
                }
            }
        }.runTaskLater(NamespaceKey.Keys.getplugin, 15L);
    }

    private static void flameAura() {
        Location bossLoc = bossEntity.getLocation();
        World world = bossLoc.getWorld();

        world.spawnParticle(Particle.FLAME, bossLoc, 80, ATTACK_RADIUS, 2, ATTACK_RADIUS, 0.3);
        world.spawnParticle(Particle.SMOKE, bossLoc, 40, ATTACK_RADIUS, 2, ATTACK_RADIUS, 0.1);
        world.playSound(bossLoc, Sound.ENTITY_BLAZE_HURT, 1.5f, 0.8f);

        for (Entity entity : world.getNearbyEntities(bossLoc, ATTACK_RADIUS, 4, ATTACK_RADIUS)) {
            if (entity instanceof Player p && !p.isDead() && isTarget(p)) {
                p.damage(15, bossEntity);
                p.setFireTicks(120);
                p.sendActionBar("§c❄ 烈焰领域正在灼烧你");
            }
        }
    }

    private static void flameBreath(Player target) {
        Location bossLoc = bossEntity.getLocation();
        World world = bossLoc.getWorld();
        Vector bossDir = bossLoc.getDirection();

        for (double r = 0; r < 5; r += 0.5) {
            for (double angle = -45; angle <= 45; angle += 15) {
                double rad = Math.toRadians(angle);
                double x = r * Math.sin(rad);
                double z = r * Math.cos(rad);
                Location point = bossLoc.clone().add(bossDir.clone().multiply(z)).add(x, 0.5, 0);
                world.spawnParticle(Particle.FLAME, point, 1, 0.3, 0.3, 0.3, 0.02);
                world.spawnParticle(Particle.LARGE_SMOKE, point, 1, 0.2, 0.2, 0.2, 0.01);
            }
        }
        world.playSound(bossLoc, Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 1.2f);

        for (Entity entity : world.getNearbyEntities(bossLoc, 5, 3, 5)) {
            if (!(entity instanceof Player p) || p.isDead() || !isTarget(p)) continue;
            Vector toEntity = entity.getLocation().toVector().subtract(bossLoc.toVector());
            double angle = bossDir.angle(toEntity);
            if (angle < Math.toRadians(50)) {
                p.damage(20, bossEntity);
                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 120, 2));
                p.setFireTicks(120);
                p.sendActionBar("§c❄ 熔岩吐息！");
            }
        }
    }

    private static void magmaExplosion(Player target) {
        Location targetLoc = target.getLocation();
        World world = targetLoc.getWorld();

        world.spawnParticle(Particle.LAVA, targetLoc.add(0, 0.5, 0), 40, 1.5, 0.5, 1.5, 0.5);
        world.playSound(targetLoc, Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 0.5f);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (bossEntity == null || bossEntity.isDead()) return;
                world.createExplosion(targetLoc, 4.0f, false, false);
                world.spawnParticle(Particle.LAVA, targetLoc, 80, 2, 1, 2, 1);
                world.spawnParticle(Particle.SMOKE, targetLoc, 40, 2, 1, 2, 0.2);
                world.playSound(targetLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);

                for (Entity entity : world.getNearbyEntities(targetLoc, 3, 3, 3)) {
                    if (entity instanceof Player p && !p.isDead() && isTarget(p)) {
                        p.damage(25, bossEntity);
                        p.setFireTicks(160);
                    }
                }
            }
        }.runTaskLater(NamespaceKey.Keys.getplugin, 20L);
    }

    // ========================================================================
    // 死亡 & 消失
    // ========================================================================

    public static void onDeath() {
        if (bossEntity == null) return;

        Location loc = bossEntity.getLocation();
        World world = loc.getWorld();

        world.spawnParticle(Particle.LAVA, loc, 120, 3, 2, 3, 1);
        world.spawnParticle(Particle.FLAME, loc, 80, 2, 2, 2, 0.5);
        world.spawnParticle(Particle.LARGE_SMOKE, loc, 40, 2, 2, 2, 0.1);
        world.playSound(loc, Sound.ENTITY_WITHER_DEATH, 1.0f, 0.5f);

        world.dropItemNaturally(loc, new ItemStack(Material.MAGMA_BLOCK, 16));
        world.dropItemNaturally(loc, new ItemStack(Material.FIRE_CHARGE, 8));
        world.dropItemNaturally(loc, ArmsorItem.MagicBallCreateI(RANDOM.nextInt(2) + 1));

        if (RANDOM.nextBoolean()) {
            world.dropItemNaturally(loc, ArmsorItem.BloodSacrifice_EnchantdeBook(1, RANDOM.nextInt(3) + 1));
        }
        if (RANDOM.nextInt(100) < 30) {
            world.dropItemNaturally(loc, ArmsorItem.Revenge_EnchantedBook(1, RANDOM.nextInt(3) + 1));
        }

        world.dropItemNaturally(loc, new ItemStack(Material.EXPERIENCE_BOTTLE, 12));

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§c◆ 爆炎树已被击败！");
        }

        cleanup();
    }

    private static void despawn() {
        if (bossEntity == null) return;
        Location loc = bossEntity.getLocation();
        loc.getWorld().spawnParticle(Particle.SMOKE, loc, 40, 2, 2, 2, 0.1);
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§e爆炎树因失去目标而消失了");
        }
        cleanup();
    }

    private static void cleanup() {
        if (aiTask != null) { aiTask.cancel(); aiTask = null; }

        for (LivingEntity entity : bodyStands) {
            if (entity != null && !entity.isDead()) entity.remove();
        }
        bodyStands.clear();
        if (coreStand != null && !coreStand.isDead()) { coreStand.remove(); coreStand = null; }

        if (bossEntity != null && !bossEntity.isDead()) { bossEntity.remove(); }

        BossMenu.unregisterBoss(BossMenu.BossType.PYRO);

        if (bossBar != null) { bossBar.removeAll(); bossBar = null; }

        bossAlive = false;
        bossEntity = null;
        bossUuid = null;
    }

    // ========================================================================
    // 工具
    // ========================================================================

    private static Player findNearestPlayer() {
        if (bossEntity == null || bossEntity.isDead()) return null;
        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Entity entity : bossEntity.getNearbyEntities(FOLLOW_RANGE, 10, FOLLOW_RANGE)) {
            if (entity instanceof Player p && !p.isDead() && !p.isInvulnerable()) {
                double dist = p.getLocation().distance(bossEntity.getLocation());
                if (dist < nearestDistance) {
                    nearestDistance = dist;
                    nearest = p;
                }
            }
        }
        return nearest;
    }

    private static boolean isTarget(Player player) {
        return player.getLocation().distance(bossEntity.getLocation()) <= FOLLOW_RANGE * 1.5;
    }

    private static void lookAtTarget(Entity entity, Location target) {
        Location loc = entity.getLocation().clone();
        loc.setDirection(target.toVector().subtract(loc.toVector()));
        entity.teleport(loc);
    }

    public static boolean isAlive() { return bossAlive; }

    public static Location getBossLocation() {
        return bossEntity != null ? bossEntity.getLocation() : null;
    }
}
