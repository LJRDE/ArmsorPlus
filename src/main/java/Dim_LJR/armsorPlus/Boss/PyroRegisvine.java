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

/**
 * 爆炎树 —— 火元素BOSS (原神: 爆炎树)
 * <p>
 * 本体隐身无AI，由盔甲架戴下界岩/岩浆块模仿形态。
 * 攻击任意盔甲架造成20%HP伤害，核心盔甲架一击必杀。
 * 雷电/冰冻伤害触发元素反应：伤害翻倍+50%暴露核心。
 */
public class PyroRegisvine {

    private static final double MAX_HEALTH = 300;
    private static final int ATTACK_RADIUS = 8;
    private static final int FOLLOW_RANGE = 50;

    private static boolean bossAlive = false;
    private static LivingEntity bossEntity;
    private static BukkitTask aiTask;
    private static UUID bossUuid;
    private static BossBar bossBar;

    // 盔甲架
    private static ArmorStand coreStand;
    private static final java.util.List<ArmorStand> bodyStands = new java.util.ArrayList<>();

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

        var maxHpAttr = bossEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHpAttr != null) maxHpAttr.setBaseValue(MAX_HEALTH);
        bossEntity.setHealth(MAX_HEALTH);

        // 隐身效果
        bossEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, -1, 1, false, false));
        // 抗火 (防止白天自燃) + 抗性
        bossEntity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 0, false, false));
        bossEntity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, 4, false, false));

        bossUuid = bossEntity.getUniqueId();

        // ---- BossBar ----
        bossBar = Bukkit.createBossBar("§c■ 爆炎树", BarColor.RED, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(1.0);

        BossMenu.registerBoss(BossMenu.BossType.PYRO, bossEntity, MAX_HEALTH, bossBar);

        // ---- 盔甲架 ----
        spawnArmorStands(spawnLoc);

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

        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                Location check = target.clone().add(x, 0, z);
                if (!check.getBlock().isPassable()) return null;
                for (int y = 1; y <= 4; y++) {
                    Location up = check.clone().add(0, y, 0);
                    if (!up.getBlock().isPassable()) return null;
                }
            }
        }
        return target;
    }

    // ========================================================================
    // 盔甲架
    // ========================================================================

    private static void spawnArmorStands(Location center) {
        // 底部3个 (NETHERRACK)
        spawnBodyStand(center, 0, 0.5, 1.8, Material.NETHERRACK);
        spawnBodyStand(center, 1.56, 0.5, -0.9, Material.NETHERRACK);
        spawnBodyStand(center, -1.56, 0.5, -0.9, Material.NETHERRACK);

        // 中部3个 (MAGMA_BLOCK)
        spawnBodyStand(center, 0, 1.8, 1.2, Material.MAGMA_BLOCK);
        spawnBodyStand(center, 1.04, 1.8, -0.6, Material.MAGMA_BLOCK);
        spawnBodyStand(center, -1.04, 1.8, -0.6, Material.MAGMA_BLOCK);

        // 核心盔甲架 (MAGMA_BLOCK) — 默认先注册为身体
        coreStand = spawnStand(center, 0, 3.2, 0, Material.MAGMA_BLOCK);
        BossMenu.registerBodyStand(BossMenu.BossType.PYRO, coreStand.getUniqueId());
    }

    private static void spawnBodyStand(Location center, double x, double y, double z, Material head) {
        ArmorStand stand = spawnStand(center, x, y, z, head);
        bodyStands.add(stand);
        BossMenu.registerBodyStand(BossMenu.BossType.PYRO, stand.getUniqueId());
    }

    private static ArmorStand spawnStand(Location center, double x, double y, double z, Material head) {
        Location loc = center.clone().add(x, y, z);
        ArmorStand stand = (ArmorStand) center.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        stand.setVisible(true);
        stand.setSmall(true);
        stand.setBasePlate(false);
        stand.setArms(false);
        stand.setGravity(false);
        stand.setCanPickupItems(false);
        stand.setRemoveWhenFarAway(false);
        stand.setPersistent(true);
        stand.setInvulnerable(false);
        stand.getEquipment().setHelmet(new ItemStack(head), true);
        return stand;
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
                online.sendMessage("§e✦ 爆炎树的核心暴露了！攻击核心造成100%伤害！");
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

                int attack = RANDOM.nextInt(phase2 ? 4 : 3);
                switch (attack) {
                    case 0 -> fireBallAttack(target);
                    case 1 -> flameAura();
                    case 2 -> flameBreath(target);
                    case 3 -> magmaExplosion(target);
                }

                attacksSinceCore++;
                if (attacksSinceCore >= 3) {
                    exposeCore();
                    attacksSinceCore = 0;
                    attackCooldown = phase2 ? 4 : 6;
                } else {
                    attackCooldown = phase2 ? 3 : 6;
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
                        p.damage(6, bossEntity);
                        p.setFireTicks(60);
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
                p.damage(8, bossEntity);
                p.setFireTicks(100);
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
                p.damage(10, bossEntity);
                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 2));
                p.setFireTicks(80);
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
                        p.damage(12, bossEntity);
                        p.setFireTicks(120);
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

        world.dropItemNaturally(loc, new ItemStack(Material.EXPERIENCE_BOTTLE, 48));

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

        for (ArmorStand stand : bodyStands) {
            if (stand != null && !stand.isDead()) stand.remove();
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
