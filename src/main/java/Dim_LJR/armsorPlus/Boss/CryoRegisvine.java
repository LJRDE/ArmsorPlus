package Dim_LJR.armsorPlus.Boss;

import Dim_LJR.armsorPlus.ArmsorItem;
import Dim_LJR.armsorPlus.NamespaceKey;
import static Dim_LJR.armsorPlus.Food.FoodItems.IceCore;
import static Dim_LJR.armsorPlus.Food.FoodItems.FireCore;
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

import static org.bukkit.Material.*;

/**
 * 雪人王 —— 冰元素BOSS (原神: 雪人王)
 * <p>
 * 核心为雪人，树状结构由盔甲架佩戴方块构成。
 * 对盔甲架的伤害转移至核心，元素反应造成双倍伤害。
 */
public class CryoRegisvine {

    private static final double MAX_HEALTH = 700;
    private static final int ATTACK_RADIUS = 8;
    private static final int FOLLOW_RANGE = 50;

    private static boolean bossAlive = false;
    private static LivingEntity bossEntity;
    private static BukkitTask aiTask;
    private static BossBar bossBar;

    private static ArmorStand coreStand;
    private static final List<ArmorStand> bodyStands = new ArrayList<>();

    private static final Random RANDOM = new Random();

    // ========================================================================
    // 召唤
    // ========================================================================

    public static void spawnBoss(Player summoner) {
        if (bossAlive) {
            summoner.sendMessage("§c已有一只雪人王，请先击败或等待其消失");
            return;
        }

        Location spawnLoc = findSpawnLocation(summoner);
        if (spawnLoc == null) {
            summoner.sendMessage("§c没有足够的空间召唤BOSS");
            return;
        }

        spawnLoc.getWorld().loadChunk(spawnLoc.getChunk());

        // ---- 雪人核心 (存血量/位置) ----
        bossEntity = (LivingEntity) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.SNOW_GOLEM);
        bossEntity.setCustomName("§b■ 雪人王 §7Lv.90");
        bossEntity.setCustomNameVisible(true);
        bossEntity.setRemoveWhenFarAway(false);
        bossEntity.setPersistent(true);
        bossEntity.setAI(false);
        bossEntity.setCollidable(false);
        bossEntity.setSilent(true);
        bossEntity.getEquipment().clear();

        var maxHpAttr = bossEntity.getAttribute(Attribute.MAX_HEALTH);
        if (maxHpAttr != null) maxHpAttr.setBaseValue(MAX_HEALTH);
        bossEntity.setHealth(MAX_HEALTH);

        bossEntity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 0, false, false));
        bossEntity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, 4, false, false));

        // ---- BossBar ----
        bossBar = Bukkit.createBossBar("§b■ 雪人王", BarColor.BLUE, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(1.0);

        BossMenu.registerBoss(BossMenu.BossType.CRYO, bossEntity, MAX_HEALTH, bossBar);

        // ---- 盔甲架树状结构 ----
        spawnTree(spawnLoc);

        // ---- 召唤特效 ----
        Location bossLoc = bossEntity.getLocation();
        bossLoc.getWorld().strikeLightningEffect(bossLoc);
        bossLoc.getWorld().spawnParticle(Particle.SNOWFLAKE, bossLoc, 100, 2, 2, 2, 0.5);
        bossLoc.getWorld().playSound(bossLoc, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.5f);

        String msg = "§b◆ 雪人王在 " + bossLoc.getBlockX() + " " + bossLoc.getBlockY() + " " + bossLoc.getBlockZ() + " 处降临了！";
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
    // 盔甲架树状结构 (浮空小方块)
    // ========================================================================

    private static void spawnTree(Location center) {
        // ========== 树干 (3×3 逐渐收窄) ==========

        // 第1层-底部 (3×3 满铺, SNOW_BLOCK)
        spawnTrunk(center, -1, -1.0, -1, SNOW_BLOCK);
        spawnTrunk(center, -1, -1.0,  0, SNOW_BLOCK);
        spawnTrunk(center, -1, -1.0,  1, SNOW_BLOCK);
        spawnTrunk(center,  0, -1.0, -1, SNOW_BLOCK);
        spawnTrunk(center,  0, -1.0,  0, SNOW_BLOCK);
        spawnTrunk(center,  0, -1.0,  1, SNOW_BLOCK);
        spawnTrunk(center,  1, -1.0, -1, SNOW_BLOCK);
        spawnTrunk(center,  1, -1.0,  0, SNOW_BLOCK);
        spawnTrunk(center,  1, -1.0,  1, SNOW_BLOCK);

        // 第2层 (3×3 满铺, PACKED_ICE)
        spawnTrunk(center, -1,  0.2, -1, PACKED_ICE);
        spawnTrunk(center, -1,  0.2,  0, PACKED_ICE);
        spawnTrunk(center, -1,  0.2,  1, PACKED_ICE);
        spawnTrunk(center,  0,  0.2, -1, PACKED_ICE);
        spawnTrunk(center,  0,  0.2,  0, PACKED_ICE);
        spawnTrunk(center,  0,  0.2,  1, PACKED_ICE);
        spawnTrunk(center,  1,  0.2, -1, PACKED_ICE);
        spawnTrunk(center,  1,  0.2,  0, PACKED_ICE);
        spawnTrunk(center,  1,  0.2,  1, PACKED_ICE);

        // 第3层 (3×3 满铺, PACKED_ICE)
        spawnTrunk(center, -1,  1.4, -1, PACKED_ICE);
        spawnTrunk(center, -1,  1.4,  0, PACKED_ICE);
        spawnTrunk(center, -1,  1.4,  1, PACKED_ICE);
        spawnTrunk(center,  0,  1.4, -1, PACKED_ICE);
        spawnTrunk(center,  0,  1.4,  0, PACKED_ICE);
        spawnTrunk(center,  0,  1.4,  1, PACKED_ICE);
        spawnTrunk(center,  1,  1.4, -1, PACKED_ICE);
        spawnTrunk(center,  1,  1.4,  0, PACKED_ICE);
        spawnTrunk(center,  1,  1.4,  1, PACKED_ICE);

        // 第4层 (3×3 边角, BLUE_ICE)
        spawnTrunk(center, -0.5, 2.6, -1, BLUE_ICE);
        spawnTrunk(center, -0.5, 2.6,  1, BLUE_ICE);
        spawnTrunk(center,  0.5, 2.6, -1, BLUE_ICE);
        spawnTrunk(center,  0.5, 2.6,  1, BLUE_ICE);

        // 第5层 (单点核心盔甲架, BLUE_ICE)
        coreStand = spawnTreeArmorStand(center, 0, 3.6, 0, BLUE_ICE);
        BossMenu.registerBodyStand(BossMenu.BossType.CRYO, coreStand.getUniqueId());

        // ========== 5条半圆形枝干 ==========

        // 枝干1 (+X 方向, y≈1.2)
        spawnBranch(center,  1.5, -0.3,  0,   SNOW_BLOCK);
        spawnBranch(center,  2.2, -0.3,  0.5, PACKED_ICE);
        spawnBranch(center,  2.2, -0.3, -0.5, PACKED_ICE);
        spawnBranch(center,  2.8, -0.3,  0,   BLUE_ICE);

        // 枝干2 (-X 方向, y≈1.8)
        spawnBranch(center, -1.5,  0.3,  0,   SNOW_BLOCK);
        spawnBranch(center, -2.2,  0.3,  0.5, PACKED_ICE);
        spawnBranch(center, -2.2,  0.3, -0.5, PACKED_ICE);
        spawnBranch(center, -2.8,  0.3,  0,   BLUE_ICE);

        // 枝干3 (+Z 方向, y≈2.4)
        spawnBranch(center,  0,   0.9,  1.5, SNOW_BLOCK);
        spawnBranch(center,  0.5, 0.9,  2.2, PACKED_ICE);
        spawnBranch(center, -0.5, 0.9,  2.2, PACKED_ICE);
        spawnBranch(center,  0,   0.9,  2.8, BLUE_ICE);

        // 枝干4 (-Z 方向, y≈3.0)
        spawnBranch(center,  0,   1.5, -1.5, SNOW_BLOCK);
        spawnBranch(center,  0.5, 1.5, -2.2, PACKED_ICE);
        spawnBranch(center, -0.5, 1.5, -2.2, PACKED_ICE);
        spawnBranch(center,  0,   1.5, -2.8, BLUE_ICE);

        // 枝干5 (花冠散开, y≈3.8)
        spawnBranch(center,  1.2, 2.3,  1.2, PACKED_ICE);
        spawnBranch(center, -1.2, 2.3,  1.2, PACKED_ICE);
        spawnBranch(center,  1.2, 2.3, -1.2, PACKED_ICE);
        spawnBranch(center, -1.2, 2.3, -1.2, BLUE_ICE);
    }

    private static void spawnTrunk(Location center, double x, double y, double z, Material head) {
        spawnBodyArmorStand(center, x, y, z, head);
    }

    private static void spawnBranch(Location center, double x, double y, double z, Material head) {
        spawnBodyArmorStand(center, x, y, z, head);
    }

    private static void spawnBodyArmorStand(Location center, double x, double y, double z, Material head) {
        ArmorStand stand = spawnTreeArmorStand(center, x, y, z, head);
        bodyStands.add(stand);
        BossMenu.registerBodyStand(BossMenu.BossType.CRYO, stand.getUniqueId());
    }

    /** 生成浮空盔甲架，佩戴指定方块作为头盔 */
    private static ArmorStand spawnTreeArmorStand(Location center, double x, double y, double z, Material head) {
        Location loc = center.clone().add(x, y, z);
        ArmorStand stand = center.getWorld().spawn(loc, ArmorStand.class);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setSmall(true);
        stand.setRemoveWhenFarAway(false);
        stand.setPersistent(true);
        stand.getEquipment().setHelmet(new ItemStack(head), true);
        return stand;
    }

    // ========================================================================
    // 冰雹 — 从天而降的冰锥攻击
    // ========================================================================

    private static void hailAttack(Player target) {
        Location targetLoc = target.getLocation();
        World world = targetLoc.getWorld();

        world.playSound(targetLoc, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 0.5f);
        target.sendActionBar("§b❄ 冰雹来了，快躲开！");

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
                    world.spawnParticle(Particle.SNOWFLAKE, from, 8, 0.4, 0.4, 0.4, 0.02);

                    Location hitLoc = targetLoc.clone().add(xOffset, 0, zOffset);
                    for (Entity entity : world.getNearbyEntities(hitLoc, 1.8, 3, 1.8)) {
                        if (entity instanceof Player p && !p.isDead() && isTarget(p)) {
                            p.damage(10, bossEntity);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));
                            p.setFreezeTicks(40);
                        }
                    }

                    world.spawnParticle(Particle.ITEM_SNOWBALL, hitLoc, 15, 0.5, 0.3, 0.5, 0.3);
                    world.playSound(hitLoc, Sound.BLOCK_SNOW_BREAK, 0.6f, 1.0f);
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
            world.spawnParticle(Particle.SNOWFLAKE, point, 2, 0.2, 0.2, 0.2, 0);
            world.spawnParticle(Particle.CRIT, point, 1, 0.1, 0.1, 0.1, 0);
        }
        world.playSound(bossLoc, Sound.ENTITY_BLAZE_SHOOT, 1.5f, 0.6f);

        for (Entity entity : world.getNearbyEntities(bossLoc, 6.5, 2, 6.5)) {
            if (entity instanceof Player p && !p.isDead() && isTarget(p)) {
                Vector toEntity = p.getLocation().toVector().subtract(bossLoc.toVector());
                double dist = toEntity.length();
                double angle = dir.angle(toEntity);
                if (angle < Math.toRadians(25) && dist <= 6.5) {
                    p.damage(12, bossEntity);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 1));
                    p.setFreezeTicks(30);
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
                    world.spawnParticle(Particle.SNOWFLAKE, point, 1, 0.15, 0.15, 0.15, 0);
                }

                for (Entity entity : world.getNearbyEntities(loc, 6.5, 2, 6.5)) {
                    if (entity instanceof Player p && !p.isDead() && isTarget(p)) {
                        Vector toEntity = p.getLocation().toVector().subtract(loc.toVector());
                        double dist = toEntity.length();
                        double angle = v.angle(toEntity);
                        if (angle < Math.toRadians(20) && dist <= 6.5) {
                            p.damage(8, bossEntity);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 0));
                            p.setFreezeTicks(20);
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

        BossMenu.upgradeBodyToCore(BossMenu.BossType.CRYO, coreStand.getUniqueId());
        coreStand.setGlowing(true);

        Location loc = coreStand.getLocation();
        bossEntity.getWorld().spawnParticle(Particle.ENCHANT, loc, 60, 0.5, 0.5, 0.5, 0.5);
        bossEntity.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.5f);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getWorld().equals(bossEntity.getWorld())
                    && online.getLocation().distance(bossEntity.getLocation()) <= 150) {
                online.sendMessage("§e✦ 雪人王的核心暴露了！攻击核心造成伤害！");
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
        BossMenu.downgradeCoreToBody(BossMenu.BossType.CRYO, coreStand.getUniqueId());
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

                BossMenu.updateBossBar(BossMenu.BossType.CRYO);

                double hpPercent = BossMenu.getBossHealth(BossMenu.BossType.CRYO) / MAX_HEALTH;
                phase2 = hpPercent < 0.5;

                if (phase2) {
                    bossBar.setColor(BarColor.WHITE);
                }

                // 粒子效果装饰 — 雪花飘落
                Location bossLoc = bossEntity.getLocation();
                bossLoc.getWorld().spawnParticle(Particle.SNOWFLAKE,
                        bossLoc.clone().add(0, 5, 0), 5, 2, 0.5, 2, 0.05);

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
                    case 0 -> iceShardAttack(target);
                    case 1 -> frostAura();
                    case 2 -> frostBreath(target);
                    case 3 -> iceExplosion(target);
                    case 4 -> hailAttack(target);
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

    private static void iceShardAttack(Player target) {
        Location bossLoc = bossEntity.getEyeLocation();
        Location targetLoc = target.getLocation().add(0, 1, 0);
        World world = bossLoc.getWorld();

        Vector dir = targetLoc.toVector().subtract(bossLoc.toVector()).normalize();
        double distance = bossLoc.distance(targetLoc);
        for (double d = 0; d < distance; d += 0.8) {
            Location point = bossLoc.clone().add(dir.clone().multiply(d));
            world.spawnParticle(Particle.SNOWFLAKE, point, 1, 0, 0, 0, 0);
        }
        world.spawnParticle(Particle.CRIT, targetLoc, 15, 0.5, 0.5, 0.5, 0.2);
        world.playSound(bossLoc, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.5f);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (bossEntity == null || bossEntity.isDead()) return;
                for (Entity entity : world.getNearbyEntities(targetLoc, 2.5, 2.5, 2.5)) {
                    if (entity instanceof Player p && !p.isDead() && isTarget(p)) {
                        p.damage(12, bossEntity);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 2));
                        p.sendActionBar("§b❄ 你受到了冰锥伤害");
                    }
                }
                world.spawnParticle(Particle.ITEM_SNOWBALL, targetLoc, 30, 1, 1, 1, 0.2);
                world.playSound(targetLoc, Sound.BLOCK_GLASS_BREAK, 0.8f, 0.5f);
            }
        }.runTaskLater(NamespaceKey.Keys.getplugin, 15L);
    }

    private static void frostAura() {
        Location bossLoc = bossEntity.getLocation();
        World world = bossLoc.getWorld();

        world.spawnParticle(Particle.SNOWFLAKE, bossLoc, 80, ATTACK_RADIUS, 2, ATTACK_RADIUS, 0.2);
        world.playSound(bossLoc, Sound.ENTITY_BLAZE_HURT, 1.5f, 0.5f);

        for (Entity entity : world.getNearbyEntities(bossLoc, ATTACK_RADIUS, 4, ATTACK_RADIUS)) {
            if (entity instanceof Player p && !p.isDead() && isTarget(p)) {
                p.damage(15, bossEntity);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 3));
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0));
                p.sendActionBar("§b❄ 寒冰领域把你冻住了");
            }
        }
    }

    private static void frostBreath(Player target) {
        Location bossLoc = bossEntity.getLocation();
        World world = bossLoc.getWorld();
        Vector bossDir = bossLoc.getDirection();

        for (double r = 0; r < 5; r += 0.5) {
            for (double angle = -45; angle <= 45; angle += 15) {
                double rad = Math.toRadians(angle);
                double x = r * Math.sin(rad);
                double z = r * Math.cos(rad);
                Location point = bossLoc.clone().add(bossDir.clone().multiply(z)).add(x, 1, 0);
                world.spawnParticle(Particle.CLOUD, point, 1, 0.3, 0.3, 0.3, 0.02);
            }
        }
        world.playSound(bossLoc, Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 0.8f);

        for (Entity entity : world.getNearbyEntities(bossLoc, 5, 3, 5)) {
            if (!(entity instanceof Player p) || p.isDead() || !isTarget(p)) continue;
            Vector toEntity = entity.getLocation().toVector().subtract(bossLoc.toVector());
            double angle = bossDir.angle(toEntity);
            if (angle < Math.toRadians(50)) {
                p.damage(20, bossEntity);
                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 120, 2));
                p.setFreezeTicks(140);
                p.sendActionBar("§b❄ 极寒吐息！");
            }
        }
    }

    private static void iceExplosion(Player target) {
        Location targetLoc = target.getLocation();
        World world = targetLoc.getWorld();

        world.spawnParticle(Particle.SNOWFLAKE, targetLoc.add(0, 0.5, 0), 40, 1.5, 0.5, 1.5, 0.3);
        world.playSound(targetLoc, Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 0.3f);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (bossEntity == null || bossEntity.isDead()) return;
                world.createExplosion(targetLoc, 3.0f, false, false);
                world.spawnParticle(Particle.ITEM_SNOWBALL, targetLoc, 60, 2, 1, 2, 0.5);
                world.playSound(targetLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);

                for (Entity entity : world.getNearbyEntities(targetLoc, 3, 3, 3)) {
                    if (entity instanceof Player p && !p.isDead() && isTarget(p)) {
                        p.damage(25, bossEntity);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 3));
                        p.setFreezeTicks(100);
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

        world.spawnParticle(Particle.SNOWFLAKE, loc, 120, 3, 2, 3, 0.8);
        world.spawnParticle(Particle.CLOUD, loc, 60, 2, 2, 2, 0.3);
        world.playSound(loc, Sound.ENTITY_WITHER_DEATH, 1.0f, 0.5f);

        world.dropItemNaturally(loc, IceCore(1 + RANDOM.nextInt(2)));
        world.dropItemNaturally(loc, new ItemStack(Material.PACKED_ICE, 16));
        world.dropItemNaturally(loc, new ItemStack(Material.BLUE_ICE, 4));
        world.dropItemNaturally(loc, ArmsorItem.MagicBallCreateI(RANDOM.nextInt(2) + 1));

        if (RANDOM.nextBoolean()) {
            world.dropItemNaturally(loc, ArmsorItem.Freeze_EnchantedBook(1, RANDOM.nextInt(3) + 1));
        }
        if (RANDOM.nextInt(100) < 30) {
            world.dropItemNaturally(loc, ArmsorItem.Ripples_EnchantdeBook(1, RANDOM.nextInt(3) + 1));
        }

        world.dropItemNaturally(loc, new ItemStack(Material.EXPERIENCE_BOTTLE, 12));

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§b◆ 雪人王已被击败！");
        }

        cleanup();
    }

    private static void despawn() {
        if (bossEntity == null) return;
        Location loc = bossEntity.getLocation();
        loc.getWorld().spawnParticle(Particle.SMOKE, loc, 40, 2, 2, 2, 0.1);
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§e雪人王因失去目标而消失了");
        }
        cleanup();
    }

    private static void cleanup() {
        if (aiTask != null) { aiTask.cancel(); aiTask = null; }

        for (ArmorStand stand : bodyStands) {
            if (stand != null && !stand.isDead()) stand.remove();
        }
        bodyStands.clear();
        if (coreStand != null && !coreStand.isDead()) { coreStand.remove(); coreStand = null; }

        if (bossEntity != null && !bossEntity.isDead()) { bossEntity.remove(); }

        BossMenu.unregisterBoss(BossMenu.BossType.CRYO);

        if (bossBar != null) { bossBar.removeAll(); bossBar = null; }

        bossAlive = false;
        bossEntity = null;
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
