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

/**
 * 僵尸巨人 —— 原版巨人添加僵尸AI。
 * 1500 HP，80 攻击伤害，缓慢但致命的巨型BOSS。
 */
public class ZombieGiantBoss {

    private static final double MAX_HEALTH = 1500;
    private static final double ATTACK_DAMAGE = 80;
    private static final int FOLLOW_RANGE = 40;

    private static boolean bossAlive = false;
    private static Giant bossGiant;
    private static BukkitTask aiTask;
    private static BossBar bossBar;
    private static final Random RANDOM = new Random();

    public static void spawnBoss(Player summoner) {
        if (bossAlive) {
            summoner.sendMessage("§c已有一只僵尸巨人，请先击败或等待其消失");
            return;
        }

        Location spawnLoc = summoner.getLocation().clone()
                .add(summoner.getLocation().getDirection().multiply(8));
        spawnLoc.setY(spawnLoc.getWorld().getHighestBlockYAt(spawnLoc) + 1);

        spawnLoc.getWorld().loadChunk(spawnLoc.getChunk());

        bossGiant = (Giant) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.GIANT);
        bossGiant.setCustomName("§c◆ 僵尸巨人 §7Lv.100");
        bossGiant.setCustomNameVisible(true);
        bossGiant.setRemoveWhenFarAway(false);
        bossGiant.setPersistent(true);

        var maxHp = bossGiant.getAttribute(Attribute.MAX_HEALTH);
        if (maxHp != null) maxHp.setBaseValue(MAX_HEALTH);
        bossGiant.setHealth(MAX_HEALTH);

        var atkDmg = bossGiant.getAttribute(Attribute.ATTACK_DAMAGE);
        if (atkDmg != null) atkDmg.setBaseValue(ATTACK_DAMAGE);

        var follow = bossGiant.getAttribute(Attribute.FOLLOW_RANGE);
        if (follow != null) follow.setBaseValue(FOLLOW_RANGE);

        bossBar = Bukkit.createBossBar("§c◆ 僵尸巨人", BarColor.RED, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(1.0);

        BossMenu.registerBoss(BossMenu.BossType.ZOMBIE_GIANT, bossGiant, MAX_HEALTH, bossBar);
        BossMenu.registerBodyStand(BossMenu.BossType.ZOMBIE_GIANT, bossGiant.getUniqueId());

        Location bossLoc = bossGiant.getLocation();
        bossLoc.getWorld().strikeLightningEffect(bossLoc);
        bossLoc.getWorld().playSound(bossLoc, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.3f);

        String msg = "§c◆ 僵尸巨人 在 " + bossLoc.getBlockX() + " " + bossLoc.getBlockY() + " " + bossLoc.getBlockZ() + " 处苏醒了！";
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(msg);
        }

        bossAlive = true;
        startAI();
    }

    private static void startAI() {
        aiTask = new BukkitRunnable() {
            int attackCooldown = 0;
            int tick = 0;

            @Override
            public void run() {
                tick++;

                if (bossGiant == null || bossGiant.isDead() || !bossGiant.isValid()) {
                    onDeath();
                    cancel();
                    return;
                }

                BossMenu.updateBossBar(BossMenu.BossType.ZOMBIE_GIANT);

                Player target = findNearestPlayer();
                if (target == null) {
                    if (tick > 600) {
                        despawn();
                        cancel();
                    }
                    return;
                }
                tick = 0;

                moveToward(target);

                if (attackCooldown > 0) {
                    attackCooldown--;
                    return;
                }

                if (target.getLocation().distance(bossGiant.getLocation()) <= 6) {
                    attackTarget(target);
                    attackCooldown = 15;
                }
            }
        }.runTaskTimer(NamespaceKey.Keys.getplugin, 20L, 10L);
    }

    private static void attackTarget(Player target) {
        target.damage(ATTACK_DAMAGE, bossGiant);
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 2));
        target.setVelocity(target.getLocation().toVector()
                .subtract(bossGiant.getLocation().toVector())
                .setY(0.4).normalize().multiply(2.5));
        target.sendActionBar("§c💀 僵尸巨人的重击！");
        bossGiant.getWorld().playSound(bossGiant.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1.5f, 0.5f);
    }

    private static void moveToward(Player target) {
        if (bossGiant == null || bossGiant.isDead()) return;
        Location loc = bossGiant.getLocation();
        Location targetLoc = target.getLocation();
        Vector dir = targetLoc.toVector().subtract(loc.toVector());
        dir.setY(0);
        if (dir.length() > 4) {
            dir.normalize().multiply(0.25);
            bossGiant.setVelocity(dir);
        }
    }

    public static void onDeath() {
        if (bossGiant == null) return;
        Location loc = bossGiant.getLocation();
        World world = loc.getWorld();

        world.spawnParticle(Particle.EXPLOSION, loc, 10, 2, 2, 2, 0.2);
        world.playSound(loc, Sound.ENTITY_WITHER_DEATH, 1.0f, 0.3f);

        world.dropItemNaturally(loc, new ItemStack(Material.ROTTEN_FLESH, 64));
        world.dropItemNaturally(loc, new ItemStack(Material.BONE, 32));
        world.dropItemNaturally(loc, ArmsorItem.MagicBallCreateIII(RANDOM.nextInt(3) + 1));
        if (RANDOM.nextInt(100) < 30) {
            world.dropItemNaturally(loc, ArmsorItem.MagicBallCreateIV(1));
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§c◆ 僵尸巨人被击败了！");
        }

        cleanup();
    }

    private static void despawn() {
        if (bossGiant == null) return;
        Location loc = bossGiant.getLocation();
        loc.getWorld().spawnParticle(Particle.SMOKE, loc, 40, 2, 2, 2, 0.1);
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§e僵尸巨人因失去目标而消失了");
        }
        cleanup();
    }

    private static void cleanup() {
        if (aiTask != null) { aiTask.cancel(); aiTask = null; }
        BossMenu.unregisterBoss(BossMenu.BossType.ZOMBIE_GIANT);
        if (bossBar != null) { bossBar.removeAll(); bossBar = null; }
        if (bossGiant != null && !bossGiant.isDead()) { bossGiant.remove(); }
        bossAlive = false;
        bossGiant = null;
    }

    private static Player findNearestPlayer() {
        if (bossGiant == null || bossGiant.isDead()) return null;
        Player nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (Entity entity : bossGiant.getNearbyEntities(FOLLOW_RANGE, 20, FOLLOW_RANGE)) {
            if (entity instanceof Player p && !p.isDead() && !p.isInvulnerable()) {
                double dist = p.getLocation().distance(bossGiant.getLocation());
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = p;
                }
            }
        }
        return nearest;
    }

    public static boolean isAlive() { return bossAlive; }

    public static Location getBossLocation() {
        return bossGiant != null ? bossGiant.getLocation() : null;
    }
}
