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
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.Random;

/**
 * 小僵尸Double —— 小僵尸骑小僵尸的二人组BOSS。
 * 两只小僵尸均穿保护IV下界合金套（不可掉落），一只拿长矛一只拿下界合金剑。
 */
public class BabyZombieDoubleBoss {

    private static final double MAX_HEALTH = 600;
    private static final double ATTACK_DAMAGE = 25;
    private static final int FOLLOW_RANGE = 40;

    private static boolean bossAlive = false;
    private static Zombie riderZombie;
    private static Zombie mountZombie;
    private static BukkitTask aiTask;
    private static BossBar bossBar;
    private static final Random RANDOM = new Random();

    public static void spawnBoss(Player summoner) {
        if (bossAlive) {
            summoner.sendMessage("§c已有一组小僵尸Double，请先击败或等待其消失");
            return;
        }

        Location spawnLoc = summoner.getLocation().clone()
                .add(summoner.getLocation().getDirection().multiply(6));
        spawnLoc.setY(spawnLoc.getWorld().getHighestBlockYAt(spawnLoc) + 1);

        spawnLoc.getWorld().loadChunk(spawnLoc.getChunk());

        // 坐骑（下方的小僵尸）
        mountZombie = (Zombie) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ZOMBIE);
        mountZombie.setBaby(true);
        mountZombie.setCustomName("§c◆ 小僵尸Double §7Lv.75");
        mountZombie.setCustomNameVisible(true);
        mountZombie.setRemoveWhenFarAway(false);
        mountZombie.setPersistent(true);

        var mountHp = mountZombie.getAttribute(Attribute.MAX_HEALTH);
        if (mountHp != null) mountHp.setBaseValue(MAX_HEALTH);
        mountZombie.setHealth(MAX_HEALTH);

        var mountAtk = mountZombie.getAttribute(Attribute.ATTACK_DAMAGE);
        if (mountAtk != null) mountAtk.setBaseValue(ATTACK_DAMAGE);

        var mountFollow = mountZombie.getAttribute(Attribute.FOLLOW_RANGE);
        if (mountFollow != null) mountFollow.setBaseValue(FOLLOW_RANGE);

        equipArmor(mountZombie, Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE,
                Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS);
        // 坐骑用下界合金剑
        mountZombie.getEquipment().setItemInMainHand(createUndroppableItem(
                new ItemStack(Material.NETHERITE_SWORD), "§c小僵尸的合金剑"));
        mountZombie.getEquipment().setItemInMainHandDropChance(0f);

        // 骑士（上方的小僵尸）
        riderZombie = (Zombie) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ZOMBIE);
        riderZombie.setBaby(true);
        riderZombie.setCustomNameVisible(false);
        riderZombie.setRemoveWhenFarAway(false);
        riderZombie.setPersistent(true);

        var riderHp = riderZombie.getAttribute(Attribute.MAX_HEALTH);
        if (riderHp != null) riderHp.setBaseValue(MAX_HEALTH);
        riderZombie.setHealth(MAX_HEALTH);

        var riderAtk = riderZombie.getAttribute(Attribute.ATTACK_DAMAGE);
        if (riderAtk != null) riderAtk.setBaseValue(ATTACK_DAMAGE);

        var riderFollow = riderZombie.getAttribute(Attribute.FOLLOW_RANGE);
        if (riderFollow != null) riderFollow.setBaseValue(FOLLOW_RANGE);

        equipArmor(riderZombie, Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE,
                Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS);
        // 骑士用长矛
        ItemStack spear = ArmsorItem.FlameHalberd(1);
        riderZombie.getEquipment().setItemInMainHand(spear);
        riderZombie.getEquipment().setItemInMainHandDropChance(0f);

        // 建立骑乘关系
        mountZombie.addPassenger(riderZombie);

        bossBar = Bukkit.createBossBar("§c◆ 小僵尸Double", BarColor.RED, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(1.0);

        BossMenu.registerBoss(BossMenu.BossType.BABY_ZOMBIE_DOUBLE, mountZombie, MAX_HEALTH * 2, bossBar);
        BossMenu.registerBodyStand(BossMenu.BossType.BABY_ZOMBIE_DOUBLE, mountZombie.getUniqueId());
        BossMenu.registerBodyStand(BossMenu.BossType.BABY_ZOMBIE_DOUBLE, riderZombie.getUniqueId());

        Location bossLoc = mountZombie.getLocation();
        bossLoc.getWorld().strikeLightningEffect(bossLoc);
        bossLoc.getWorld().playSound(bossLoc, Sound.ENTITY_WITHER_SPAWN, 0.8f, 0.6f);

        String msg = "§c◆ 小僵尸Double 在 " + bossLoc.getBlockX() + " " + bossLoc.getBlockY() + " " + bossLoc.getBlockZ() + " 处出现了！";
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(msg);
        }

        bossAlive = true;
        startAI();
    }

    private static void equipArmor(Zombie zombie, Material helmet, Material chest, Material legs, Material boots) {
        EntityEquipment equip = zombie.getEquipment();
        equip.setHelmet(createUndroppableArmor(new ItemStack(helmet)));
        equip.setChestplate(createUndroppableArmor(new ItemStack(chest)));
        equip.setLeggings(createUndroppableArmor(new ItemStack(legs)));
        equip.setBoots(createUndroppableArmor(new ItemStack(boots)));
        equip.setHelmetDropChance(0f);
        equip.setChestplateDropChance(0f);
        equip.setLeggingsDropChance(0f);
        equip.setBootsDropChance(0f);
    }

    private static ItemStack createUndroppableArmor(ItemStack armor) {
        armor.addEnchantment(Enchantment.PROTECTION, 4);
        armor.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
        return armor;
    }

    private static ItemStack createUndroppableItem(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList("§7小僵尸的武器"));
        item.setItemMeta(meta);
        return item;
    }

    private static void startAI() {
        aiTask = new BukkitRunnable() {
            int tick = 0;
            int attackCooldown = 0;

            @Override
            public void run() {
                tick++;

                boolean mountDead = mountZombie == null || mountZombie.isDead() || !mountZombie.isValid();
                boolean riderDead = riderZombie == null || riderZombie.isDead() || !riderZombie.isValid();

                if (mountDead && riderDead) {
                    onDeath();
                    cancel();
                    return;
                }

                // 如果坐骑死了但骑士还活着，骑士下来继续战斗
                if (mountDead && !riderDead) {
                    onDeath();
                    cancel();
                    return;
                }

                BossMenu.updateBossBar(BossMenu.BossType.BABY_ZOMBIE_DOUBLE);

                LivingEntity active = mountZombie;
                if (active == null || active.isDead()) active = riderZombie;

                Player target = findNearestPlayer();
                if (target == null) {
                    if (tick > 600) {
                        despawn();
                        cancel();
                    }
                    return;
                }
                tick = 0;

                if (attackCooldown > 0) {
                    attackCooldown--;
                    return;
                }

                double dist = active.getLocation().distance(target.getLocation());
                if (dist <= 4) {
                    target.damage(ATTACK_DAMAGE, active);
                    if (RANDOM.nextBoolean() && riderZombie != null && !riderZombie.isDead()) {
                        target.damage(ATTACK_DAMAGE * 0.5, riderZombie);
                    }
                    attackCooldown = 10;
                }
            }
        }.runTaskTimer(NamespaceKey.Keys.getplugin, 20L, 10L);
    }

    public static void onDeath() {
        Location loc = mountZombie != null ? mountZombie.getLocation()
                : (riderZombie != null ? riderZombie.getLocation() : null);
        if (loc == null) return;

        World world = loc.getWorld();
        world.spawnParticle(Particle.EXPLOSION, loc, 5, 1, 1, 1, 0.1);
        world.playSound(loc, Sound.ENTITY_WITHER_DEATH, 0.8f, 0.8f);

        world.dropItemNaturally(loc, ArmsorItem.MagicBallCreateII(RANDOM.nextInt(3) + 1));
        world.dropItemNaturally(loc, new ItemStack(Material.EXPERIENCE_BOTTLE, 32));
        if (RANDOM.nextInt(100) < 15) {
            world.dropItemNaturally(loc, ArmsorItem.QuickThrust_EnchantedBook(1, RANDOM.nextInt(3) + 2));
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§c◆ 小僵尸Double 被击败了！");
        }

        cleanup();
    }

    private static void despawn() {
        Location loc = mountZombie != null ? mountZombie.getLocation()
                : (riderZombie != null ? riderZombie.getLocation() : null);
        if (loc != null) {
            loc.getWorld().spawnParticle(Particle.SMOKE, loc, 20, 1, 1, 1, 0.1);
        }
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§e小僵尸Double因失去目标而消失了");
        }
        cleanup();
    }

    private static void cleanup() {
        if (aiTask != null) { aiTask.cancel(); aiTask = null; }
        BossMenu.unregisterBoss(BossMenu.BossType.BABY_ZOMBIE_DOUBLE);
        if (bossBar != null) { bossBar.removeAll(); bossBar = null; }
        if (mountZombie != null && !mountZombie.isDead()) { mountZombie.remove(); }
        if (riderZombie != null && !riderZombie.isDead()) { riderZombie.remove(); }
        bossAlive = false;
        mountZombie = null;
        riderZombie = null;
    }

    private static Player findNearestPlayer() {
        LivingEntity active = mountZombie;
        if (active == null || active.isDead()) active = riderZombie;
        if (active == null || active.isDead()) return null;

        Player nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (Entity entity : active.getNearbyEntities(FOLLOW_RANGE, 10, FOLLOW_RANGE)) {
            if (entity instanceof Player p && !p.isDead() && !p.isInvulnerable()) {
                double dist = p.getLocation().distance(active.getLocation());
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
        if (mountZombie != null && !mountZombie.isDead()) return mountZombie.getLocation();
        if (riderZombie != null && !riderZombie.isDead()) return riderZombie.getLocation();
        return null;
    }
}
