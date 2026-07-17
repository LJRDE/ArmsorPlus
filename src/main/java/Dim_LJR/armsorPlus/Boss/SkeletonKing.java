package Dim_LJR.armsorPlus.Boss;

import Dim_LJR.armsorPlus.ArmsorItem;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Random;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;

public class SkeletonKing {
    private static final double MAX_HEALTH = 600;
    private static final int DESPAWN_TICKS = 1200; // 60s no target -> despawn
    private static final Random RANDOM = new Random();
    private static boolean bossAlive;
    private static LivingEntity bossEntity;
    private static BukkitTask aiTask;
    private static BossBar bossBar;
    private static int tickCounter;
    private static int noTargetTicks;

    public static boolean isAlive() { return bossAlive && bossEntity != null && !bossEntity.isDead(); }
    public static Location getBossLocation() { return bossEntity != null ? bossEntity.getLocation() : null; }

    public static void spawnBoss(Player summoner) {
        if (bossAlive) { summoner.sendMessage("§c骷髅王已在战斗中"); return; }
        Location spawnLoc = findSpawnLocation(summoner);
        if (spawnLoc == null) { summoner.sendMessage("§c没有足够空间"); return; }
        spawnLoc.getWorld().loadChunk(spawnLoc.getChunk());

        bossEntity = (LivingEntity) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.SKELETON);
        bossEntity.setCustomName("§8■ 骷髅王 §7Lv.85");
        bossEntity.setCustomNameVisible(true);
        bossEntity.setRemoveWhenFarAway(false);
        bossEntity.setPersistent(true);

        var maxHp = bossEntity.getAttribute(Attribute.MAX_HEALTH);
        if (maxHp != null) maxHp.setBaseValue(MAX_HEALTH);
        bossEntity.setHealth(MAX_HEALTH);

        // 自定义弓: Power X, 不掉落
        ItemStack bow = new ItemStack(BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.addEnchant(Enchantment.POWER, 10, true);
        bowMeta.setUnbreakable(true);
        bow.setItemMeta(bowMeta);
        bossEntity.getEquipment().setItemInMainHand(bow);
        bossEntity.getEquipment().setItemInMainHandDropChance(0f);

        bossEntity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, 3, false, false));
        bossEntity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 0, false, false));

        bossBar = Bukkit.createBossBar("§8■ 骷髅王", BarColor.WHITE, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(1.0);
        BossMenu.registerBoss(BossMenu.BossType.SKELETON_KING, bossEntity, MAX_HEALTH, bossBar);

        Location loc = bossEntity.getLocation();
        loc.getWorld().strikeLightningEffect(loc);
        loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1f, 0.5f);
        Bukkit.broadcastMessage("§8◆ 骷髅王在 " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " 降临！");

        bossAlive = true;
        tickCounter = 0;
        noTargetTicks = 0;
        startAI();
    }

    private static void startAI() {
        aiTask = new BukkitRunnable() {
            @Override
            public void run() {
                // 实体无效(区块卸载等) → 异常清理
                if (bossEntity == null || !bossEntity.isValid()) {
                    if (bossAlive) {
                        Bukkit.broadcastMessage("§8骷髅王异常消失...");
                        cleanup();
                    }
                    cancel();
                    return;
                }

                // 实体自然死亡 → 触发死亡掉落
                if (bossEntity.isDead()) {
                    if (bossAlive) {
                        onDeath();
                    }
                    cancel();
                    return;
                }

                tickCounter++;

                // 从实体原生血量更新BossBar
                double currentHp = bossEntity.getHealth();
                double maxHp = bossEntity.getAttribute(Attribute.MAX_HEALTH).getValue();
                bossBar.setProgress(Math.max(0, currentHp / maxHp));
                bossBar.setTitle("§8■ 骷髅王 §7" + Math.round(currentHp) + "/" + Math.round(maxHp));
                BossMenu.syncBossHealth(BossMenu.BossType.SKELETON_KING, currentHp, maxHp);
                BossMenu.updateBossBar(BossMenu.BossType.SKELETON_KING);

                // ---- 寻找附近玩家 ----
                boolean hasTarget = false;
                Location bl = bossEntity.getLocation();
                for (Player p : bl.getWorld().getPlayers()) {
                    double dist = p.getLocation().distance(bl);
                    if (dist <= 50) { hasTarget = true; break; }
                }

                if (!hasTarget) {
                    noTargetTicks++;
                    if (noTargetTicks >= DESPAWN_TICKS) {
                        Bukkit.broadcastMessage("§8骷髅王因无人应战而消失...");
                        despawn();
                        return;
                    }
                } else {
                    noTargetTicks = 0;
                }

                // ---- 箭雨: 每200tick (10秒) ----
                if (tickCounter % 200 == 0) {
                    for (Player p : bl.getWorld().getPlayers()) {
                        if (p.getLocation().distance(bl) > 20) continue;
                        for (int i = 0; i < 5; i++) {
                            Location arrowLoc = p.getLocation().add(
                                    RANDOM.nextDouble()*6-3, 8+RANDOM.nextDouble()*5, RANDOM.nextDouble()*6-3);
                            Arrow arrow = bl.getWorld().spawn(arrowLoc, Arrow.class);
                            arrow.setVelocity(new Vector(0, -2, 0));
                            arrow.setDamage(6);
                            arrow.setShooter(bossEntity);
                            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                        }
                    }
                    bl.getWorld().playSound(bl, Sound.ENTITY_ARROW_SHOOT, 0.5f, 1.5f);
                }
            }
        }.runTaskTimer(getplugin, 10L, 10L);
    }

    private static void despawn() {
        if (bossEntity != null) {
            Location loc = bossEntity.getLocation();
            loc.getWorld().spawnParticle(Particle.SMOKE, loc, 40, 2, 2, 2, 0.1);
            bossEntity.remove();
        }
        cleanup();
    }

    private static void cleanup() {
        if (aiTask != null) { aiTask.cancel(); aiTask = null; }
        bossAlive = false;
        bossEntity = null;
        if (bossBar != null) { bossBar.setVisible(false); bossBar.removeAll(); bossBar = null; }
        BossMenu.unregisterBoss(BossMenu.BossType.SKELETON_KING);
    }

    private static Location findSpawnLocation(Player summoner) {
        Location base = summoner.getLocation();
        for (int r = 5; r <= 20; r += 5) {
            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI / 4;
                Location loc = base.clone().add(Math.cos(angle)*r, 0, Math.sin(angle)*r);
                loc.setY(base.getWorld().getHighestBlockYAt(loc) + 1);
                if (loc.getBlock().isEmpty() && loc.clone().add(0,1,0).getBlock().isEmpty())
                    return loc;
            }
        }
        return null;
    }

    public static void onDeath() {
        if (!bossAlive) return;
        if (aiTask != null) aiTask.cancel();
        Location loc = bossEntity.getLocation();
        loc.getWorld().strikeLightningEffect(loc);
        loc.getWorld().createExplosion(loc, 0f, false, false);
        loc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 50, 1, 1, 1, 0.1);
        loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_DEATH, 1f, 0.5f);

        if (RANDOM.nextDouble() < 0.5)
            loc.getWorld().dropItemNaturally(loc, ArmsorItem.SkeletonScepter(1));
        loc.getWorld().dropItemNaturally(loc, new ItemStack(BONE, 8+RANDOM.nextInt(8)));
        loc.getWorld().dropItemNaturally(loc, new ItemStack(ARROW, 16+RANDOM.nextInt(16)));

        cleanup();
    }
}
