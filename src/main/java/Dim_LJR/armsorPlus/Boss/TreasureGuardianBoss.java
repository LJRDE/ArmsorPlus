package Dim_LJR.armsorPlus.Boss;

import Dim_LJR.armsorPlus.ArmsorItem;
import Dim_LJR.armsorPlus.NamespaceKey;
import org.bukkit.*;

import static Dim_LJR.armsorPlus.Item.Materials.MoonShard;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Random;

// 宝藏守护者 —— BOSS版本
// 骷髅战士，手持重剑，拥有冰冻能力和极高移速。
// BOSS版本血量250，公海版本血量不变(60)。
public class TreasureGuardianBoss {

    private static final double MAX_HEALTH = 250;
    private static final int FOLLOW_RANGE = 40;

    private static boolean bossAlive = false;
    private static Skeleton bossEntity;
    private static BukkitTask aiTask;
    private static BossBar bossBar;

    private static final Random RANDOM = new Random();

    // ========================================================================
    // 召唤
    // ========================================================================

    public static void spawnBoss(Player summoner) {
        if (bossAlive) {
            summoner.sendMessage("§c已有一只宝藏守护者，请先击败或等待其消失");
            return;
        }

        Location spawnLoc = findSpawnLocation(summoner);
        if (spawnLoc == null) {
            summoner.sendMessage("§c没有足够的空间召唤BOSS");
            return;
        }

        spawnLoc.getWorld().loadChunk(spawnLoc.getChunk());

        // ---- 骷髅本体 ----
        bossEntity = spawnLoc.getWorld().spawn(spawnLoc, Skeleton.class, skel -> {
            EntityEquipment equip = skel.getEquipment();
            if (equip != null) {
                equip.setItem(EquipmentSlot.HAND, GuardianSword(1));
                equip.setItem(EquipmentSlot.HEAD, GuardianHelmet(1));
                equip.setItem(EquipmentSlot.CHEST, GuardianChestplate(1));
                equip.setItem(EquipmentSlot.LEGS, GuardianLeggings(1));
                equip.setItem(EquipmentSlot.FEET, GuardianBoots(1));
                equip.setHelmetDropChance(0.1F);
                equip.setChestplateDropChance(0);
                equip.setLeggingsDropChance(0);
                equip.setBootsDropChance(0);
                equip.setItemInMainHandDropChance(0);
            }
            skel.setCustomName("§6■ 宝藏守护者 §7Lv.50");
            skel.setCustomNameVisible(true);
            skel.setRemoveWhenFarAway(false);
            skel.setPersistent(true);
            var maxHpAttr = skel.getAttribute(Attribute.MAX_HEALTH);
            if (maxHpAttr != null) maxHpAttr.setBaseValue(MAX_HEALTH);
            skel.setHealth(MAX_HEALTH);
            skel.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 2, false, false));
        });

        // ---- BossBar ----
        bossBar = Bukkit.createBossBar("§6■ 宝藏守护者", BarColor.YELLOW, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(1.0);

        BossMenu.registerBoss(BossMenu.BossType.TREASURE_GUARDIAN, bossEntity, MAX_HEALTH, bossBar);

        // ---- 召唤特效 ----
        Location bossLoc = bossEntity.getLocation();
        bossLoc.getWorld().strikeLightningEffect(bossLoc);
        bossLoc.getWorld().spawnParticle(Particle.ENCHANT, bossLoc, 60, 1, 2, 1, 0.3);
        bossLoc.getWorld().playSound(bossLoc, Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.8f);

        String msg = "§6◆ 宝藏守护者在 " + bossLoc.getBlockX() + " " + bossLoc.getBlockY() + " " + bossLoc.getBlockZ() + " 处出现了！";
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(msg);
        }

        bossAlive = true;
        startAI();
    }

    private static Location findSpawnLocation(Player player) {
        Location base = player.getLocation();
        Vector dir = base.getDirection().multiply(6);
        Location target = base.clone().add(dir);
        target.setY(BossSpawn.groundY(target.getWorld(), target.getBlockX(), target.getBlockZ(), base.getBlockY()));
        return target;
    }

    // ========================================================================
    // AI
    // ========================================================================

    private static void startAI() {
        aiTask = new BukkitRunnable() {
            int attackCooldown = 0;
            int tick = 0;

            @Override
            public void run() {
                tick++;

                if (bossEntity == null || bossEntity.isDead() || !bossEntity.isValid()) {
                    onDeath();
                    cancel();
                    return;
                }

                // 薄封装: 同步原生血量, 原版骷髅AI负责追击/近战
                BossMenu.syncBossHealth(BossMenu.BossType.TREASURE_GUARDIAN,
                        bossEntity.getHealth(),
                        bossEntity.getAttribute(Attribute.MAX_HEALTH).getValue());
                BossMenu.updateBossBar(BossMenu.BossType.TREASURE_GUARDIAN);

                LivingEntity target = Enmity.getEnemy(bossEntity);
                if (target == null) target = findNearestPlayer();
                if (target == null) {
                    if (tick > 600) {
                        despawn();
                        cancel();
                    }
                    return;
                }
                tick = 0;
                bossEntity.setTarget(target); // 原生AI索敌

                // 周期性技能触发 (冰冻斩/冰霜领域/冲撞)
                if (attackCooldown > 0) {
                    attackCooldown--;
                    return;
                }
                int r = RANDOM.nextInt(4);
                switch (r) {
                    case 0 -> iceSlash(target);
                    case 1 -> freezeAura(target);
                    case 2 -> chargeAttack(target);
                    case 3 -> iceSlash(target);
                }
                attackCooldown = 4 + RANDOM.nextInt(4);
            }
        }.runTaskTimer(NamespaceKey.Keys.getplugin, 20L, 10L);
    }

    // ========================================================================
    // 攻击方式
    // ========================================================================

    private static void iceSlash(LivingEntity target) {
        if (bossEntity == null || bossEntity.isDead()) return;
        Location bossLoc = bossEntity.getLocation();
        World world = bossLoc.getWorld();

        world.spawnParticle(Particle.SWEEP_ATTACK, bossLoc.clone().add(0, 1, 0), 8, 0.5, 0.5, 0.5, 0);
        world.playSound(bossLoc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);

        for (Entity entity : world.getNearbyEntities(bossLoc, 3, 2, 3)) {
            if (entity instanceof Player p && !p.isDead()) {
                p.damage(15, bossEntity);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));
                p.setFreezeTicks(40);
            }
        }
    }

    private static void freezeAura(LivingEntity target) {
        if (bossEntity == null || bossEntity.isDead()) return;
        Location bossLoc = bossEntity.getLocation();
        World world = bossLoc.getWorld();

        world.spawnParticle(Particle.SNOWFLAKE, bossLoc, 50, 4, 2, 4, 0.1);
        world.playSound(bossLoc, Sound.BLOCK_GLASS_BREAK, 0.8f, 0.5f);

        for (Entity entity : world.getNearbyEntities(bossLoc, 8, 4, 8)) {
            if (entity instanceof Player p && !p.isDead()) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 2));
                p.setFreezeTicks(60);
                p.sendActionBar("§b❄ 守护者的冰霜领域减缓了你");
            }
        }
    }

    private static void chargeAttack(LivingEntity target) {
        if (bossEntity == null || bossEntity.isDead()) return;
        World world = bossEntity.getWorld();

        Vector dir = target.getLocation().toVector().subtract(bossEntity.getLocation().toVector()).normalize();
        bossEntity.setVelocity(dir.multiply(1.5));

        world.spawnParticle(Particle.CLOUD, bossEntity.getLocation(), 20, 0.5, 0.5, 0.5, 0.05);
        world.playSound(bossEntity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1.0f, 1.0f);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (bossEntity == null || bossEntity.isDead()) return;
                Location loc = bossEntity.getLocation();
                for (Entity entity : world.getNearbyEntities(loc, 2.5, 2, 2.5)) {
                    if (entity instanceof Player p && !p.isDead()) {
                        p.damage(20, bossEntity);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 1));
                    }
                }
                world.spawnParticle(Particle.EXPLOSION, loc, 1, 0.3, 0.3, 0.3, 0);
                world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 0.8f);
            }
        }.runTaskLater(NamespaceKey.Keys.getplugin, 8L);
    }

    // ========================================================================
    // 死亡 & 消失
    // ========================================================================

    public static void onDeath() {
        if (bossEntity == null) return;

        Location loc = bossEntity.getLocation();
        World world = loc.getWorld();

        world.spawnParticle(Particle.CLOUD, loc, 80, 2, 2, 2, 0.3);
        world.spawnParticle(Particle.ENCHANT, loc, 40, 1, 1, 1, 0.2);
        world.playSound(loc, Sound.ENTITY_WITHER_DEATH, 1.0f, 0.6f);

        world.dropItemNaturally(loc, new ItemStack(Material.DIAMOND, RANDOM.nextInt(3) + 1));
        world.dropItemNaturally(loc, ArmsorItem.MagicBallCreateI(RANDOM.nextInt(2) + 1));
        world.dropItemNaturally(loc, MoonShard(RANDOM.nextInt(2) + 1)); // 月之碎片 1~2 (吞云斩月刀材料)

        if (RANDOM.nextBoolean()) {
            world.dropItemNaturally(loc, ArmsorItem.Freeze_EnchantedBook(1, RANDOM.nextInt(2) + 1));
        }

        world.dropItemNaturally(loc, new ItemStack(Material.EXPERIENCE_BOTTLE, 6));

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§6◆ 宝藏守护者已被击败！");
        }

        cleanup();
    }

    private static void despawn() {
        if (bossEntity == null) return;
        Location loc = bossEntity.getLocation();
        loc.getWorld().spawnParticle(Particle.SMOKE, loc, 40, 2, 2, 2, 0.1);
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§e宝藏守护者因失去目标而消失了");
        }
        cleanup();
    }

    private static void cleanup() {
        if (aiTask != null) { aiTask.cancel(); aiTask = null; }

        if (bossEntity != null && !bossEntity.isDead()) { bossEntity.remove(); }

        BossMenu.unregisterBoss(BossMenu.BossType.TREASURE_GUARDIAN);

        if (bossBar != null) { bossBar.removeAll(); bossBar = null; }

        bossAlive = false;
        bossEntity = null;
    }

    // ========================================================================
    // 装备 (与公海版本相同)
    // ========================================================================

    private static ItemStack GuardianSword(int amount) {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "守卫之剑");
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new org.bukkit.attribute.AttributeModifier(
                        new NamespacedKey(NamespaceKey.Keys.getplugin, "GuardianBoss_SwordDamage"),
                        15, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER,
                        org.bukkit.inventory.EquipmentSlotGroup.HAND));
        item.setAmount(amount);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack GuardianHelmet(int amount) {
        ItemStack item = new ItemStack(Material.CARVED_PUMPKIN);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "守卫者的头套");
        meta.addAttributeModifier(Attribute.ARMOR,
                new org.bukkit.attribute.AttributeModifier(
                        new NamespacedKey(NamespaceKey.Keys.getplugin, "GuardianBoss_HelmetArmor"),
                        8, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER,
                        org.bukkit.inventory.EquipmentSlotGroup.HEAD));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
                new org.bukkit.attribute.AttributeModifier(
                        new NamespacedKey(NamespaceKey.Keys.getplugin, "GuardianBoss_HelmetToughness"),
                        12, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER,
                        org.bukkit.inventory.EquipmentSlotGroup.HEAD));
        item.setAmount(amount);
        meta.addEnchant(org.bukkit.enchantments.Enchantment.PROTECTION, 10, true);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack GuardianChestplate(int amount) {
        ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + " ");
        meta.addAttributeModifier(Attribute.ARMOR,
                new org.bukkit.attribute.AttributeModifier(
                        new NamespacedKey(NamespaceKey.Keys.getplugin, "GuardianBoss_ChestplateArmor"),
                        8, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER,
                        org.bukkit.inventory.EquipmentSlotGroup.CHEST));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
                new org.bukkit.attribute.AttributeModifier(
                        new NamespacedKey(NamespaceKey.Keys.getplugin, "GuardianBoss_ChestplateToughness"),
                        8, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER,
                        org.bukkit.inventory.EquipmentSlotGroup.CHEST));
        item.setAmount(amount);
        meta.addEnchant(org.bukkit.enchantments.Enchantment.PROTECTION, 4, true);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack GuardianLeggings(int amount) {
        ItemStack item = new ItemStack(Material.DIAMOND_LEGGINGS);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + " ");
        meta.addAttributeModifier(Attribute.ARMOR,
                new org.bukkit.attribute.AttributeModifier(
                        new NamespacedKey(NamespaceKey.Keys.getplugin, "GuardianBoss_LeggingsArmor"),
                        8, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER,
                        org.bukkit.inventory.EquipmentSlotGroup.LEGS));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
                new org.bukkit.attribute.AttributeModifier(
                        new NamespacedKey(NamespaceKey.Keys.getplugin, "GuardianBoss_LeggingsToughness"),
                        8, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER,
                        org.bukkit.inventory.EquipmentSlotGroup.LEGS));
        item.setAmount(amount);
        meta.addEnchant(org.bukkit.enchantments.Enchantment.PROTECTION, 4, true);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack GuardianBoots(int amount) {
        ItemStack item = new ItemStack(Material.DIAMOND_BOOTS);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + " ");
        meta.addAttributeModifier(Attribute.ARMOR,
                new org.bukkit.attribute.AttributeModifier(
                        new NamespacedKey(NamespaceKey.Keys.getplugin, "GuardianBoss_BootsArmor"),
                        8, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER,
                        org.bukkit.inventory.EquipmentSlotGroup.FEET));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
                new org.bukkit.attribute.AttributeModifier(
                        new NamespacedKey(NamespaceKey.Keys.getplugin, "GuardianBoss_BootsToughness"),
                        8, org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER,
                        org.bukkit.inventory.EquipmentSlotGroup.FEET));
        item.setAmount(amount);
        meta.addEnchant(org.bukkit.enchantments.Enchantment.PROTECTION, 4, true);
        item.setItemMeta(meta);
        return item;
    }

    // ========================================================================
    // 工具
    // ========================================================================

    private static Player findNearestPlayer() {
        if (bossEntity == null || bossEntity.isDead()) return null;
        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Entity entity : bossEntity.getNearbyEntities(FOLLOW_RANGE, 10, FOLLOW_RANGE)) {
            if (entity instanceof Player p && BossTargets.isCombatPlayer(p)) {
                double dist = p.getLocation().distance(bossEntity.getLocation());
                if (dist < nearestDistance) {
                    nearestDistance = dist;
                    nearest = p;
                }
            }
        }
        return nearest;
    }


    public static boolean isAlive() { return bossAlive; }

    public static Location getBossLocation() {
        return bossEntity != null ? bossEntity.getLocation() : null;
    }
}
