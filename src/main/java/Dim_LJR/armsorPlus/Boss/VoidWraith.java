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

import static Dim_LJR.armsorPlus.Item.Materials.EndCore;
import static org.bukkit.Material.*;

/**
 * 虚空幽魂 —— 末地主题BOSS (凋灵骷髅)
 * <p>
 * 盔甲架构建末地传送门风格结构，双阶段战斗，每15-25秒传送。
 * 元素反应: 火焰+雷电 → 50%概率暴露核心。
 */
public class VoidWraith {

    private static final double MAX_HEALTH = 800;
    private static final int ATTACK_RADIUS = 10;
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
            summoner.sendMessage("§5已有一只虚空幽魂，请先击败或等待其消失");
            return;
        }

        Location spawnLoc = findSpawnLocation(summoner);
        if (spawnLoc == null) {
            summoner.sendMessage("§c没有足够的空间召唤BOSS");
            return;
        }

        spawnLoc.getWorld().loadChunk(spawnLoc.getChunk());

        // ---- 核心实体 (凋灵骷髅) ----
        bossEntity = (LivingEntity) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.WITHER_SKELETON);
        bossEntity.setCustomName("§5■ 虚空幽魂 §7Lv.95");
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
        bossEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, -1, 0, false, false));

        // ---- BossBar ----
        bossBar = Bukkit.createBossBar("§5■ 虚空幽魂", BarColor.PURPLE, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(1.0);

        BossMenu.registerBoss(BossMenu.BossType.VOID_WRAITH, bossEntity, MAX_HEALTH, bossBar);

        // ---- 盔甲架结构 ----
        spawnStructure(spawnLoc);

        // ---- 召唤特效 ----
        Location bossLoc = bossEntity.getLocation();
        bossLoc.getWorld().strikeLightningEffect(bossLoc);
        bossLoc.getWorld().spawnParticle(Particle.PORTAL, bossLoc, 200, 3, 3, 3, 1.0);
        bossLoc.getWorld().spawnParticle(Particle.DRAGON_BREATH, bossLoc, 100, 2, 1, 2, 0.1);
        bossLoc.getWorld().playSound(bossLoc, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.3f);

        String msg = "§5◆ 虚空幽魂在 " + bossLoc.getBlockX() + " " + bossLoc.getBlockY() + " " + bossLoc.getBlockZ() + " 处降临了！";
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
    // 盔甲架结构 (末地传送门风格塔)
    // ========================================================================

    private static void spawnStructure(Location center) {
        // 第1层 底部 (3x3 OBSIDIAN)
        spawnBody(center, -1, -1.0, -1, OBSIDIAN);
        spawnBody(center, -1, -1.0,  0, OBSIDIAN);
        spawnBody(center, -1, -1.0,  1, OBSIDIAN);
        spawnBody(center,  0, -1.0, -1, OBSIDIAN);
        spawnBody(center,  0, -1.0,  0, OBSIDIAN);
        spawnBody(center,  0, -1.0,  1, OBSIDIAN);
        spawnBody(center,  1, -1.0, -1, OBSIDIAN);
        spawnBody(center,  1, -1.0,  0, OBSIDIAN);
        spawnBody(center,  1, -1.0,  1, OBSIDIAN);

        // 第2层 (3x3 CRYING_OBSIDIAN)
        spawnBody(center, -1,  0.2, -1, CRYING_OBSIDIAN);
        spawnBody(center, -1,  0.2,  0, CRYING_OBSIDIAN);
        spawnBody(center, -1,  0.2,  1, CRYING_OBSIDIAN);
        spawnBody(center,  0,  0.2, -1, CRYING_OBSIDIAN);
        spawnBody(center,  0,  0.2,  0, CRYING_OBSIDIAN);
        spawnBody(center,  0,  0.2,  1, CRYING_OBSIDIAN);
        spawnBody(center,  1,  0.2, -1, CRYING_OBSIDIAN);
        spawnBody(center,  1,  0.2,  0, CRYING_OBSIDIAN);
        spawnBody(center,  1,  0.2,  1, CRYING_OBSIDIAN);

        // 第3层 (3x3 PURPUR_BLOCK)
        spawnBody(center, -1,  1.4, -1, PURPUR_BLOCK);
        spawnBody(center, -1,  1.4,  0, PURPUR_BLOCK);
        spawnBody(center, -1,  1.4,  1, PURPUR_BLOCK);
        spawnBody(center,  0,  1.4, -1, PURPUR_BLOCK);
        spawnBody(center,  0,  1.4,  0, PURPUR_BLOCK);
        spawnBody(center,  0,  1.4,  1, PURPUR_BLOCK);
        spawnBody(center,  1,  1.4, -1, PURPUR_BLOCK);
        spawnBody(center,  1,  1.4,  0, PURPUR_BLOCK);
        spawnBody(center,  1,  1.4,  1, PURPUR_BLOCK);

        // 第4层 四角立柱 (PURPUR_PILLAR)
        spawnBody(center, -1, 2.6, -1, PURPUR_PILLAR);
        spawnBody(center, -1, 2.6,  1, PURPUR_PILLAR);
        spawnBody(center,  1, 2.6, -1, PURPUR_PILLAR);
        spawnBody(center,  1, 2.6,  1, PURPUR_PILLAR);

        // 核心 (END_ROD)
        coreStand = spawnStructureStand(center, 0, 3.6, 0, END_ROD);
        BossMenu.registerBodyStand(BossMenu.BossType.VOID_WRAITH, coreStand.getUniqueId());

        // 外围装饰: 4个SHULKER_BOX在中间高度
        spawnBody(center, -1.5, 0.8,  0,   PURPUR_BLOCK);
        spawnBody(center,  1.5, 0.8,  0,   PURPUR_BLOCK);
        spawnBody(center,  0,   0.8, -1.5, PURPUR_BLOCK);
        spawnBody(center,  0,   0.8,  1.5, PURPUR_BLOCK);
    }

    private static void spawnBody(Location center, double x, double y, double z, Material head) {
        ArmorStand stand = spawnStructureStand(center, x, y, z, head);
        bodyStands.add(stand);
        BossMenu.registerBodyStand(BossMenu.BossType.VOID_WRAITH, stand.getUniqueId());
    }

    private static ArmorStand spawnStructureStand(Location center, double x, double y, double z, Material head) {
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
    // 攻击方式
    // ========================================================================

    /** 虚空弹: 直线弹射物,  blindness + damage */
    private static void voidBolt(Player target) {
        Location bossLoc = bossEntity.getEyeLocation();
        Location targetLoc = target.getLocation().add(0, 1, 0);
        World world = bossLoc.getWorld();

        Vector dir = targetLoc.toVector().subtract(bossLoc.toVector()).normalize();
        double distance = bossLoc.distance(targetLoc);

        world.playSound(bossLoc, Sound.ENTITY_SHULKER_SHOOT, 1.0f, 0.8f);

        new BukkitRunnable() {
            double traveled = 0;
            Location current = bossLoc.clone();

            @Override
            public void run() {
                if (bossEntity == null || bossEntity.isDead()) { cancel(); return; }
                traveled += 0.8;
                if (traveled > distance) {
                    // 命中判定
                    for (Entity e : world.getNearbyEntities(targetLoc, 2.5, 2.5, 2.5)) {
                        if (e instanceof Player p && !p.isDead() && isTarget(p)) {
                            p.damage(14, bossEntity);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                            p.sendActionBar("§5你被虚空之力击中！");
                        }
                    }
                    world.spawnParticle(Particle.DRAGON_BREATH, targetLoc, 30, 1, 1, 1, 0.2);
                    world.playSound(targetLoc, Sound.ENTITY_SHULKER_HURT, 0.8f, 0.5f);
                    cancel();
                    return;
                }
                current = bossLoc.clone().add(dir.clone().multiply(traveled));
                world.spawnParticle(Particle.PORTAL, current, 5, 0.2, 0.2, 0.2, 0.1);
                world.spawnParticle(Particle.WITCH, current, 3, 0.1, 0.1, 0.1, 0.01);
            }
        }.runTaskTimer(NamespaceKey.Keys.getplugin, 0L, 1L);
    }

    /** 末影脉冲: AOE击退 + 伤害 */
    private static void enderPulse() {
        Location bossLoc = bossEntity.getLocation();
        World world = bossLoc.getWorld();

        world.spawnParticle(Particle.PORTAL, bossLoc.clone().add(0, 1, 0), 100, ATTACK_RADIUS, 2, ATTACK_RADIUS, 0.3);
        world.spawnParticle(Particle.FLASH, bossLoc.clone().add(0, 1, 0), 1, 0, 0, 0, 0);
        world.playSound(bossLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 0.3f);

        for (Entity entity : world.getNearbyEntities(bossLoc, ATTACK_RADIUS, 4, ATTACK_RADIUS)) {
            if (entity instanceof Player p && !p.isDead() && isTarget(p)) {
                p.damage(12, bossEntity);
                Vector knockback = p.getLocation().toVector().subtract(bossLoc.toVector()).normalize().multiply(2.5);
                knockback.setY(0.8);
                p.setVelocity(knockback);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0));
                p.sendActionBar("§5末影脉冲将你震飞！");
            }
        }
    }

    /** 虚空裂隙: 延迟爆炸 + 传送BOSS */
    private static void voidRift(Player target) {
        Location targetLoc = target.getLocation();
        World world = targetLoc.getWorld();

        world.spawnParticle(Particle.DRAGON_BREATH, targetLoc.clone().add(0, 0.5, 0), 20, 1, 0.5, 1, 0.05);
        world.playSound(targetLoc, Sound.BLOCK_PORTAL_AMBIENT, 1.0f, 0.5f);
        target.sendActionBar("§5§l⚠ 脚下出现了虚空裂隙！");

        new BukkitRunnable() {
            @Override
            public void run() {
                if (bossEntity == null || bossEntity.isDead()) { cancel(); return; }
                world.createExplosion(targetLoc, 4.0f, false, false);
                world.spawnParticle(Particle.PORTAL, targetLoc, 80, 2, 1, 2, 0.5);
                world.spawnParticle(Particle.DRAGON_BREATH, targetLoc, 40, 1.5, 0.5, 1.5, 0.1);
                world.playSound(targetLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);

                for (Entity entity : world.getNearbyEntities(targetLoc, 4, 3, 4)) {
                    if (entity instanceof Player p && !p.isDead() && isTarget(p)) {
                        p.damage(18, bossEntity);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
                    }
                }

                // 爆炸后传送BOSS
                teleportToRandom();
            }
        }.runTaskLater(NamespaceKey.Keys.getplugin, 30L);
    }

    /** 暗影分身: 召唤1-2个凋灵骷髅小兵 */
    private static void shadowClone() {
        Location bossLoc = bossEntity.getLocation();
        World world = bossLoc.getWorld();
        int count = 1 + RANDOM.nextInt(2);

        world.playSound(bossLoc, Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.8f);
        world.spawnParticle(Particle.PORTAL, bossLoc, 40, 2, 1, 2, 0.5);

        for (int i = 0; i < count; i++) {
            double angle = RANDOM.nextDouble() * Math.PI * 2;
            double r = 2 + RANDOM.nextDouble() * 2;
            Location spawnLoc = bossLoc.clone().add(Math.cos(angle) * r, 0, Math.sin(angle) * r);
            spawnLoc.setY(world.getHighestBlockYAt(spawnLoc) + 1);

            WitherSkeleton clone = world.spawn(spawnLoc, WitherSkeleton.class);
            clone.setCustomName("§5虚空残影");
            clone.setCustomNameVisible(true);
            clone.setRemoveWhenFarAway(false);
            clone.setPersistent(true);
            clone.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, -1, 0, false, false));
            clone.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 0, false, false));

            // 消失倒计时 (20秒)
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (clone != null && !clone.isDead()) {
                        clone.getWorld().spawnParticle(Particle.PORTAL, clone.getLocation(), 20, 1, 1, 1, 0.5);
                        clone.remove();
                    }
                }
            }.runTaskLater(NamespaceKey.Keys.getplugin, 400L);
        }
    }

    /** 终末裁决: 龙息AOE (P2) */
    private static void endJudgment() {
        Location bossLoc = bossEntity.getLocation();
        World world = bossLoc.getWorld();

        for (double r = 0; r < 6; r += 0.5) {
            for (double angle = 0; angle < 360; angle += 30) {
                double rad = Math.toRadians(angle);
                double x = r * Math.cos(rad);
                double z = r * Math.sin(rad);
                world.spawnParticle(Particle.DRAGON_BREATH,
                        bossLoc.clone().add(x, 0.5, z), 1, 0.2, 0.2, 0.2, 0.02);
            }
        }
        world.playSound(bossLoc, Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.5f, 0.5f);
        world.playSound(bossLoc, Sound.ENTITY_WITHER_SHOOT, 1.5f, 0.8f);

        for (Entity entity : world.getNearbyEntities(bossLoc, 7, 4, 7)) {
            if (entity instanceof Player p && !p.isDead() && isTarget(p)) {
                p.damage(22, bossEntity);
                p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1));
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
                p.sendActionBar("§5§l终末裁决！虚空侵蚀了你！");
            }
        }
    }

    /** 虚空吸取: 拉近目标 + 吸取生命 (P2) */
    private static void voidDrain(Player target) {
        Location bossLoc = bossEntity.getLocation();
        Location targetLoc = target.getLocation();
        World world = bossLoc.getWorld();

        world.spawnParticle(Particle.PORTAL, targetLoc.clone().add(0, 1, 0), 50, 1, 1, 1, 0.5);
        world.playSound(bossLoc, Sound.ENTITY_ENDERMAN_SCREAM, 1.0f, 0.5f);
        target.sendActionBar("§5§l虚空之力正在吸取你的生命！");

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (bossEntity == null || bossEntity.isDead()) { cancel(); return; }
                tick++;
                if (tick > 40) { cancel(); return; }

                // 每tick将目标拉向BOSS
                if (target.isDead() || !isTarget(target)) { cancel(); return; }
                Vector pull = bossLoc.toVector().subtract(target.getLocation().toVector()).normalize().multiply(0.4);
                pull.setY(0.1);
                target.setVelocity(pull);

                // 连线粒子
                Location mid = target.getLocation().add(bossLoc).multiply(0.5).add(0, 1, 0);
                world.spawnParticle(Particle.PORTAL, mid, 3, 0.1, 0.1, 0.1, 0.1);
                world.spawnParticle(Particle.DRAGON_BREATH, target.getLocation().add(0, 1, 0), 3, 0.3, 0.3, 0.3, 0.02);

                if (tick % 10 == 0) {
                    target.damage(6, bossEntity);
                    if (bossEntity.getHealth() < MAX_HEALTH) {
                        bossEntity.setHealth(Math.min(MAX_HEALTH, bossEntity.getHealth() + 3));
                    }
                }
            }
        }.runTaskTimer(NamespaceKey.Keys.getplugin, 0L, 1L);
    }

    // ========================================================================
    // 传送
    // ========================================================================

    private static void teleportToRandom() {
        if (bossEntity == null || bossEntity.isDead()) return;
        Player target = findNearestPlayer();
        if (target == null) return;

        Location targetLoc = target.getLocation();
        World world = targetLoc.getWorld();

        // 传送到目标附近随机位置
        double angle = RANDOM.nextDouble() * Math.PI * 2;
        double r = 8 + RANDOM.nextDouble() * 6;
        Location dest = targetLoc.clone().add(Math.cos(angle) * r, 0, Math.sin(angle) * r);
        dest.setY(world.getHighestBlockYAt(dest) + 1);

        // 传送特效
        Location oldLoc = bossEntity.getLocation();
        world.spawnParticle(Particle.PORTAL, oldLoc, 60, 1, 1, 1, 0.5);
        world.playSound(oldLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.6f);

        bossEntity.teleport(dest);

        world.spawnParticle(Particle.PORTAL, dest, 60, 1, 1, 1, 0.5);
        world.playSound(dest, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
    }

    // ========================================================================
    // 核心暴露
    // ========================================================================

    public static void exposeCore() {
        if (coreStand == null || coreStand.isDead() || bossEntity == null || bossEntity.isDead()) return;

        BossMenu.upgradeBodyToCore(BossMenu.BossType.VOID_WRAITH, coreStand.getUniqueId());
        coreStand.setGlowing(true);

        Location loc = coreStand.getLocation();
        bossEntity.getWorld().spawnParticle(Particle.ENCHANT, loc, 80, 0.5, 0.5, 0.5, 0.8);
        bossEntity.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 40, 0.5, 0.5, 0.5, 0.1);
        bossEntity.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.5f);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getWorld().equals(bossEntity.getWorld())
                    && online.getLocation().distance(bossEntity.getLocation()) <= 150) {
                online.sendMessage("§5✦ 虚空幽魂的核心暴露了！攻击核心造成大量伤害！");
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
        BossMenu.downgradeCoreToBody(BossMenu.BossType.VOID_WRAITH, coreStand.getUniqueId());
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
            int teleportCooldown = 0;

            @Override
            public void run() {
                tick++;

                if (bossEntity == null || bossEntity.isDead() || !bossEntity.isValid()) {
                    onDeath();
                    cancel();
                    return;
                }

                BossMenu.updateBossBar(BossMenu.BossType.VOID_WRAITH);

                double hpPercent = BossMenu.getBossHealth(BossMenu.BossType.VOID_WRAITH) / MAX_HEALTH;
                phase2 = hpPercent < 0.5;

                // 粒子装饰: 传送门粒子环绕
                Location bossLoc = bossEntity.getLocation();
                bossLoc.getWorld().spawnParticle(Particle.PORTAL,
                        bossLoc.clone().add(0, 2, 0), 3, 1.5, 1.5, 1.5, 0.1);
                if (phase2) {
                    bossLoc.getWorld().spawnParticle(Particle.DRAGON_BREATH,
                            bossLoc.clone().add(0, 1, 0), 2, 1, 0.5, 1, 0.02);
                }

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

                // 传送冷却
                if (teleportCooldown > 0) {
                    teleportCooldown--;
                } else {
                    teleportToRandom();
                    teleportCooldown = 150 + RANDOM.nextInt(100); // 7.5-12.5秒
                }

                if (attackCooldown > 0) {
                    attackCooldown--;
                    return;
                }

                int attack = RANDOM.nextInt(phase2 ? 6 : 4);
                switch (attack) {
                    case 0 -> voidBolt(target);
                    case 1 -> enderPulse();
                    case 2 -> voidRift(target);
                    case 3 -> shadowClone();
                    case 4 -> endJudgment();
                    case 5 -> voidDrain(target);
                }

                attacksSinceCore++;
                if (attacksSinceCore >= 3) {
                    exposeCore();
                    attacksSinceCore = 0;
                    attackCooldown = phase2 ? 6 : 10;
                } else {
                    attackCooldown = phase2 ? 6 : 10;
                }
            }
        }.runTaskTimer(NamespaceKey.Keys.getplugin, 20L, 10L);
    }

    // ========================================================================
    // 死亡 & 消失
    // ========================================================================

    public static void onDeath() {
        if (bossEntity == null) return;

        Location loc = bossEntity.getLocation();
        World world = loc.getWorld();

        world.spawnParticle(Particle.PORTAL, loc, 150, 3, 2, 3, 1.0);
        world.spawnParticle(Particle.DRAGON_BREATH, loc, 80, 2, 2, 2, 0.3);
        world.spawnParticle(Particle.FLASH, loc.clone().add(0, 1, 0), 1, 0, 0, 0, 0);
        world.playSound(loc, Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0f, 0.5f);
        world.playSound(loc, Sound.ENTITY_WITHER_DEATH, 1.0f, 0.8f);

        world.dropItemNaturally(loc, EndCore(1 + RANDOM.nextInt(2)));
        world.dropItemNaturally(loc, new ItemStack(Material.ENDER_PEARL, 16 + RANDOM.nextInt(17)));
        world.dropItemNaturally(loc, new ItemStack(Material.PURPUR_BLOCK, 8 + RANDOM.nextInt(9)));
        world.dropItemNaturally(loc, ArmsorItem.MagicBallCreateIII(RANDOM.nextInt(3) + 1));
        world.dropItemNaturally(loc, new ItemStack(Material.EXPERIENCE_BOTTLE, 32));

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§5◆ 虚空幽魂已被击败！虚空之力消散了！");
        }

        cleanup();
    }

    private static void despawn() {
        if (bossEntity == null) return;
        Location loc = bossEntity.getLocation();
        loc.getWorld().spawnParticle(Particle.SMOKE, loc, 40, 2, 2, 2, 0.1);
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§e虚空幽魂因失去目标而消失了");
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

        BossMenu.unregisterBoss(BossMenu.BossType.VOID_WRAITH);

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
