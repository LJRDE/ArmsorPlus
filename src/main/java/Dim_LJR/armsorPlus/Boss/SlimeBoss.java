package Dim_LJR.armsorPlus.Boss;

import Dim_LJR.armsorPlus.ArmsorItem;
import Dim_LJR.armsorPlus.NamespaceKey;
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

import java.util.*;

// 史莱姆王 —— 持有满级附魔重锤的巨型史莱姆BOSS。
// 巨型史莱姆携带 Density V / Breach IV / Wind Burst III 重锤，
// 通过超高跳跃触发重锤粉碎攻击，对落点周围造成巨额伤害。
// 半血以下分裂小史莱姆并进入激怒状态。
public class SlimeBoss {

    private static final double MAX_HEALTH = 750;
    private static final int ATTACK_RADIUS = 6;
    private static final int FOLLOW_RANGE = 50;
    private static final int SLIME_SIZE = 15;

    private static boolean bossAlive = false;
    private static Slime bossSlime;
    private static BukkitTask aiTask;
    private static BossBar bossBar;
    private static double lastY;
    private static final List<Slime> minions = new ArrayList<>();
    private static final Random RANDOM = new Random();

    // ========================================================================
    // 召唤
    // ========================================================================

    public static void spawnBoss(Player summoner) {
        if (bossAlive) {
            summoner.sendMessage("§c已有一只史莱姆王，请先击败或等待其消失");
            return;
        }

        Location spawnLoc = findSpawnLocation(summoner);
        if (spawnLoc == null) {
            summoner.sendMessage("§c没有足够的空间召唤BOSS");
            return;
        }

        spawnLoc.getWorld().loadChunk(spawnLoc.getChunk());

        // ---- 巨型史莱姆本体 ----
        bossSlime = (Slime) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.SLIME);
        bossSlime.setSize(SLIME_SIZE);
        bossSlime.setCustomName("§a◆ 史莱姆王 §7Lv.90");
        bossSlime.setCustomNameVisible(true);
        bossSlime.setRemoveWhenFarAway(false);
        bossSlime.setPersistent(true);

        var maxHpAttr = bossSlime.getAttribute(Attribute.MAX_HEALTH);
        if (maxHpAttr != null) maxHpAttr.setBaseValue(MAX_HEALTH);
        bossSlime.setHealth(MAX_HEALTH);

        // 手持满级附魔重锤
        ItemStack mace = createBossMace();
        bossSlime.getEquipment().setItemInMainHand(mace);
        bossSlime.getEquipment().setItemInMainHandDropChance(1.0f);

        lastY = spawnLoc.getY();

        // ---- BossBar ----
        bossBar = Bukkit.createBossBar("§a◆ 史莱姆王", BarColor.GREEN, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(1.0);

        BossMenu.registerBoss(BossMenu.BossType.SLIME, bossSlime, MAX_HEALTH, bossBar);

        // ---- 召唤特效 ----
        Location bossLoc = bossSlime.getLocation();
        bossLoc.getWorld().strikeLightningEffect(bossLoc);
        bossLoc.getWorld().spawnParticle(Particle.ITEM_SLIME, bossLoc, 100, 2, 1, 2, 0.5);
        bossLoc.getWorld().playSound(bossLoc, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.5f);

        String msg = "§a◆ 史莱姆王在 " + bossLoc.getBlockX() + " " + bossLoc.getBlockY() + " " + bossLoc.getBlockZ() + " 处降临了！";
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
        target.setY(BossSpawn.groundY(target.getWorld(), target.getBlockX(), target.getBlockZ(), base.getBlockY()));

        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                Location check = target.clone().add(x, 0, z);
                if (!check.getBlock().isPassable()) return null;
                for (int y = 1; y <= 8; y++) {
                    Location up = check.clone().add(0, y, 0);
                    if (!up.getBlock().isPassable()) return null;
                }
            }
        }
        return target;
    }

    // ========================================================================
    // BOSS重锤
    // ========================================================================

    public static ItemStack createBossMace() {
        ItemStack mace = new ItemStack(Material.MACE);
        ItemMeta meta = mace.getItemMeta();
        meta.setDisplayName("§c★ 史莱姆王的重锤");
        meta.setLore(Arrays.asList(
                "§7史莱姆王的巨型武器",
                "§7蕴含了史莱姆一族千年的力量"
        ));
        mace.setItemMeta(meta);

        mace.addEnchantment(Enchantment.DENSITY, 5);
        mace.addEnchantment(Enchantment.BREACH, 4);
        mace.addEnchantment(Enchantment.WIND_BURST, 3);
        mace.addEnchantment(Enchantment.UNBREAKING, 3);
        mace.addEnchantment(Enchantment.MENDING, 1);

        return mace;
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

                if (bossSlime == null || bossSlime.isDead() || !bossSlime.isValid()) {
                    onDeath();
                    cancel();
                    return;
                }

                // 薄封装: 同步实体原生血量, 由原版史莱姆AI负责移动和接触伤害
                BossMenu.syncBossHealth(BossMenu.BossType.SLIME,
                        bossSlime.getHealth(),
                        bossSlime.getAttribute(Attribute.MAX_HEALTH).getValue());
                BossMenu.updateBossBar(BossMenu.BossType.SLIME);

                // 阶段检测: 原生血量 50% 激怒 + 分裂小史莱姆
                if (bossSlime.getHealth() / MAX_HEALTH < 0.5 && !phase2) {
                    phase2 = true;
                    bossBar.setColor(BarColor.YELLOW);
                    enrage();
                }

                // 重锤粉碎: 原生跳跃/落地高度差触发范围伤害
                double currentY = bossSlime.getLocation().getY();
                double fallDist = lastY - currentY;
                if (fallDist > 3 && bossSlime.isOnGround()) {
                    maceSmashLanding(fallDist);
                }
                lastY = currentY;

                // 粒子装饰
                Location bossLoc = bossSlime.getLocation();
                bossLoc.getWorld().spawnParticle(Particle.ITEM_SLIME,
                        bossLoc.clone().add(0, 0.5, 0), 2, 0.5, 0.3, 0.5, 0);

                // 仇怨目标优先 (原版史莱姆AI负责追击/接触伤害); 无敌人且无人应战才消失
                LivingEntity enemy = Enmity.getEnemy(bossSlime);
                if (enemy != null) {
                    bossSlime.setTarget(enemy);
                } else if (findNearestPlayer() == null && tick > 600) {
                    despawn();
                    cancel();
                }
            }
        }.runTaskTimer(NamespaceKey.Keys.getplugin, 20L, 10L);
    }

    // ========================================================================
    // 攻击技能
    // ========================================================================

    // 重锤落地 — 从高处落下时触发范围伤害
    private static void maceSmashLanding(double fallDist) {
        Location loc = bossSlime.getLocation();
        World world = loc.getWorld();

        double damage = 15 + fallDist * 5; // 基础15 + 每格5伤害
        damage = Math.min(damage, 80); // 上限80

        world.spawnParticle(Particle.EXPLOSION, loc.clone().add(0, 1, 0), 3, 1, 0.5, 1, 0);
        world.spawnParticle(Particle.ITEM_SLIME, loc, 50, 3, 1, 3, 0.3);
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.8f);
        world.playSound(loc, Sound.ENTITY_SLIME_JUMP, 1.0f, 1.5f);

        for (Entity entity : world.getNearbyEntities(loc, 5, 3, 5)) {
            if (entity instanceof Player p && !p.isDead() && isTarget(p)) {
                p.damage(damage, bossSlime);
                // Wind Burst效果: 击飞玩家
                p.setVelocity(new Vector(0, 1.5, 0));
                p.sendActionBar("§c💥 重锤粉碎！");
            }
        }
    }

    // ========================================================================
    // 激怒 & 分裂
    // ========================================================================

    private static void enrage() {
        if (bossSlime == null || bossSlime.isDead()) return;

        Location loc = bossSlime.getLocation();
        loc.getWorld().spawnParticle(Particle.ENCHANT, loc, 100, 2, 2, 2, 0.5);
        loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.8f);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getWorld().equals(loc.getWorld())
                    && online.getLocation().distance(loc) <= 150) {
                online.sendMessage("§c⚡ 史莱姆王进入了激怒状态！攻击更加频繁！");
            }
        }

        splitMinions();
    }

    private static void splitMinions() {
        if (bossSlime == null || bossSlime.isDead()) return;

        Location loc = bossSlime.getLocation();
        World world = loc.getWorld();

        // 清理旧小兵
        minions.removeIf(s -> s.isDead() || !s.isValid());
        if (minions.size() >= 6) return;

        for (int i = 0; i < 3; i++) {
            double xOff = (RANDOM.nextDouble() - 0.5) * 5;
            double zOff = (RANDOM.nextDouble() - 0.5) * 5;
            Location spawnLoc = loc.clone().add(xOff, 0, zOff);

            Slime small = (Slime) world.spawnEntity(spawnLoc, EntityType.SLIME);
            small.setSize(3 + RANDOM.nextInt(3));
            small.setCustomName("§a小史莱姆");
            small.setCustomNameVisible(true);
            small.setRemoveWhenFarAway(true);

            var hp = small.getAttribute(Attribute.MAX_HEALTH);
            if (hp != null) hp.setBaseValue(30);
            small.setHealth(30);

            minions.add(small);

            world.spawnParticle(Particle.ITEM_SLIME, spawnLoc, 10, 0.5, 0.3, 0.5, 0.1);
        }
        world.playSound(loc, Sound.ENTITY_SLIME_SQUISH, 0.8f, 1.2f);
    }

    // ========================================================================
    // 移动
    // ========================================================================

    // ========================================================================
    // 死亡 & 消失
    // ========================================================================

    public static void onDeath() {
        if (bossSlime == null) return;

        Location loc = bossSlime.getLocation();
        World world = loc.getWorld();

        world.spawnParticle(Particle.ITEM_SLIME, loc, 150, 3, 2, 3, 0.8);
        world.spawnParticle(Particle.EXPLOSION, loc, 5, 1, 1, 1, 0.1);
        world.playSound(loc, Sound.ENTITY_WITHER_DEATH, 1.0f, 0.5f);

        // 掉落物
        world.dropItemNaturally(loc, new ItemStack(Material.SLIME_BALL, 32));
        world.dropItemNaturally(loc, new ItemStack(Material.SLIME_BLOCK, 8));
        world.dropItemNaturally(loc, ArmsorItem.MagicBallCreateI(RANDOM.nextInt(3) + 2));

        // 概率掉落BOSS重锤
        if (RANDOM.nextInt(100) < 20) {
            world.dropItemNaturally(loc, createBossMace());
        }
        // 概率掉落附魔书
        if (RANDOM.nextBoolean()) {
            world.dropItemNaturally(loc, ArmsorItem.Dodge_EnchantdeBook(1, RANDOM.nextInt(3) + 1));
        }

        world.dropItemNaturally(loc, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§a◆ 史莱姆王已被击败！");
        }

        cleanup();
    }

    private static void despawn() {
        if (bossSlime == null) return;
        Location loc = bossSlime.getLocation();
        loc.getWorld().spawnParticle(Particle.SMOKE, loc, 40, 2, 2, 2, 0.1);
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§e史莱姆王因失去目标而消失了");
        }
        cleanup();
    }

    private static void cleanup() {
        if (aiTask != null) { aiTask.cancel(); aiTask = null; }

        for (Slime s : minions) {
            if (s != null && !s.isDead()) s.remove();
        }
        minions.clear();

        BossMenu.unregisterBoss(BossMenu.BossType.SLIME);

        if (bossBar != null) { bossBar.removeAll(); bossBar = null; }

        if (bossSlime != null && !bossSlime.isDead()) { bossSlime.remove(); }

        bossAlive = false;
        bossSlime = null;
    }

    // ========================================================================
    // 工具
    // ========================================================================

    private static Player findNearestPlayer() {
        if (bossSlime == null || bossSlime.isDead()) return null;
        Player nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (Entity entity : bossSlime.getNearbyEntities(FOLLOW_RANGE, 10, FOLLOW_RANGE)) {
            if (entity instanceof Player p && BossTargets.isCombatPlayer(p)) {
                double dist = p.getLocation().distance(bossSlime.getLocation());
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = p;
                }
            }
        }
        return nearest;
    }

    private static boolean isTarget(Player player) {
        return bossSlime != null && !bossSlime.isDead()
                && player.getLocation().distance(bossSlime.getLocation()) <= FOLLOW_RANGE * 1.5;
    }

    public static boolean isAlive() { return bossAlive; }

    public static Location getBossLocation() {
        return bossSlime != null ? bossSlime.getLocation() : null;
    }
}
