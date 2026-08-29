package Dim_LJR.armsorPlus.Boss;

import Dim_LJR.armsorPlus.ArmsorItem;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import Dim_LJR.armsorPlus.NamespaceKey;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

import static Dim_LJR.armsorPlus.Item.Materials.SteelIngot;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

// 铁骑双雄 —— 双骑士骑乘BOSS。
// 僵尸骑士骑一匹僵尸马(125血), 骷髅射手骑一只毒蛛(125血), 双坐骑抗性IV。
// 双坐骑速度VII, 僵尸长矛基础伤害25+贯穿III+击退X, 骷髅神弓力量85+弹道V。
// 僵尸马支持生物骑乘操纵(isMobControlled() = getFirstPassenger() instanceof Mob),
// 僵尸骑士可稳定冲锋; 蜘蛛不属于AbstractHorse家族, 没有MountPanicGoal恐慌目标,
// 被骷髅骑乘受伤也不会乱跑, pathfinder可直接驱动, 故骷髅改用毒蛛坐骑。
// 击杀两名骑士即击败BOSS; 坐骑是纯坐骑, BOSS结束时随之消失。
public class DesertCamelBoss {

    private static final double MOUNT_HEALTH = 125;
    private static final double ZOMBIE_HEALTH = 150;
    private static final double SKELETON_HEALTH = 100;
    private static final int FOLLOW_RANGE = 40;
    private static final int DESPAWN_TICKS = 1200;
    private static final Random RANDOM = new Random();

    private static boolean bossAlive = false;
    private static ZombieHorse zombieHorse;
    private static Spider spider;
    private static Zombie zombie;
    private static Skeleton skeleton;
    private static BossBar zombieBar;
    private static BossBar skeletonBar;
    private static BukkitTask aiTask;
    private static int noTargetTicks;
    private static boolean zombieWasAlive = false;
    private static boolean skeletonWasAlive = false;
    private static Location lastDeathLocation;

    public static boolean isAlive() { return bossAlive; }

    public static Location getBossLocation() {
        if (zombieHorse != null && !zombieHorse.isDead()) return zombieHorse.getLocation();
        if (spider != null && !spider.isDead()) return spider.getLocation();
        if (zombie != null && !zombie.isDead()) return zombie.getLocation();
        if (skeleton != null && !skeleton.isDead()) return skeleton.getLocation();
        return null;
    }

    // ========================================================================
    // 召唤
    // ========================================================================

    public static void spawnBoss(Player summoner) {
        if (bossAlive) { summoner.sendMessage("§c铁骑双雄已在战斗中"); return; }
        Location spawnLoc = findSpawnLocation(summoner);
        if (spawnLoc == null) { summoner.sendMessage("§c没有足够空间"); return; }
        spawnLoc.getWorld().loadChunk(spawnLoc.getChunk());

        // ---- 僵尸骑士的战马 (125血, 速度VII, 抗性IV, 已驯服) ----
        zombieHorse = spawnLoc.getWorld().spawn(spawnLoc, ZombieHorse.class, h -> {
            h.setCustomName("§2铁骑·僵尸战马");
            h.setCustomNameVisible(true);
            h.setRemoveWhenFarAway(false);
            h.setPersistent(true);
            h.setTamed(true);
            var maxHp = h.getAttribute(Attribute.MAX_HEALTH);
            if (maxHp != null) maxHp.setBaseValue(MOUNT_HEALTH);
            h.setHealth(MOUNT_HEALTH);
            // 基础移速0.2 + 速度VII(×2.4) ≈ 0.48格/tick ≈ 20.6格/秒, 极速冲锋
            var speed = h.getAttribute(Attribute.MOVEMENT_SPEED);
            if (speed != null) speed.setBaseValue(0.2);
            h.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 6, false, false));     // 速度VII
            h.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, 3, false, false)); // 抗性IV
        });

        // ---- 骷髅射手的坐骑毒蛛 (125血, 速度VII, 抗性IV) ----
        // 蜘蛛没有MountPanicGoal, 被骷髅骑乘受伤不会恐慌, pathfinder可直接驱动
        spider = spawnLoc.getWorld().spawn(spawnLoc, Spider.class, s -> {
            s.setCustomName("§7铁骑·毒蛛");
            s.setCustomNameVisible(true);
            s.setRemoveWhenFarAway(false);
            s.setPersistent(true);
            var maxHp = s.getAttribute(Attribute.MAX_HEALTH);
            if (maxHp != null) maxHp.setBaseValue(MOUNT_HEALTH);
            s.setHealth(MOUNT_HEALTH);
            // 蜘蛛基础移速0.3, 统一压到0.2 + 速度VII(×2.4) ≈ 0.48格/tick ≈ 20.6格/秒
            var speed = s.getAttribute(Attribute.MOVEMENT_SPEED);
            if (speed != null) speed.setBaseValue(0.2);
            s.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 6, false, false));     // 速度VII
            s.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, 3, false, false)); // 抗性IV
        });

        // ---- 僵尸骑士 (全套下界合金 + 长矛 + 盾) ----
        zombie = spawnLoc.getWorld().spawn(spawnLoc, Zombie.class, z -> {
            z.setCustomName("§2僵尸骑士");
            z.setCustomNameVisible(true);
            z.setRemoveWhenFarAway(false);
            z.setPersistent(true);
            var maxHp = z.getAttribute(Attribute.MAX_HEALTH);
            if (maxHp != null) maxHp.setBaseValue(ZOMBIE_HEALTH);
            z.setHealth(ZOMBIE_HEALTH);
            equipNetheriteArmor(z);
            EntityEquipment equip = z.getEquipment();
            equip.setItemInMainHand(createSpear());
            equip.setItemInOffHand(createShield());
            equip.setItemInMainHandDropChance(0f);
            equip.setItemInOffHandDropChance(0f);
        });

        // ---- 骷髅射手 (全套下界合金 + 神弓) ----
        skeleton = spawnLoc.getWorld().spawn(spawnLoc, Skeleton.class, s -> {
            s.setCustomName("§7骷髅射手");
            s.setCustomNameVisible(true);
            s.setRemoveWhenFarAway(false);
            s.setPersistent(true);
            var maxHp = s.getAttribute(Attribute.MAX_HEALTH);
            if (maxHp != null) maxHp.setBaseValue(SKELETON_HEALTH);
            s.setHealth(SKELETON_HEALTH);
            equipNetheriteArmor(s);
            EntityEquipment equip = s.getEquipment();
            equip.setItemInMainHand(createBow());
            equip.setItemInMainHandDropChance(0f);
        });

        // 僵尸骑僵尸马, 骷髅骑毒蛛
        zombieHorse.addPassenger(zombie);
        spider.addPassenger(skeleton);

        // 注册同队实体: 骷髅与僵尸之间不互相伤害 (战马/毒蛛已作为主实体/部位注册)
        BossMenu.registerFriendlyEntity(BossMenu.BossType.DESERT_CAMEL, zombie);
        BossMenu.registerFriendlyEntity(BossMenu.BossType.DESERT_CAMEL, skeleton);

        // ---- BossBar (僵尸 & 骷髅) ----
        zombieBar = Bukkit.createBossBar("§2◆ 铁骑双雄·僵尸骑士", BarColor.GREEN, BarStyle.SOLID);
        zombieBar.setVisible(true);
        zombieBar.setProgress(1.0);

        skeletonBar = Bukkit.createBossBar("§7◆ 铁骑双雄·骷髅射手", BarColor.WHITE, BarStyle.SOLID);
        skeletonBar.setVisible(true);
        skeletonBar.setProgress(1.0);

        // ---- 注册战马(隐藏血条): 提供环境伤害保护 + 原版伤害 ----
        BossBar hiddenBar = Bukkit.createBossBar(" ", BarColor.WHITE, BarStyle.SOLID);
        BossMenu.registerBoss(BossMenu.BossType.DESERT_CAMEL, zombieHorse, MOUNT_HEALTH, hiddenBar);
        hiddenBar.setVisible(false);
        BossMenu.registerBodyStand(BossMenu.BossType.DESERT_CAMEL, spider.getUniqueId());

        // ---- 召唤特效 ----
        Location loc = zombieHorse.getLocation();
        loc.getWorld().strikeLightningEffect(loc);
        loc.getWorld().spawnParticle(Particle.CLOUD, loc, 60, 1, 2, 1, 0.3);
        loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.7f);
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§2◆ 铁骑双雄在 " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " 处出现了！");
        }

        bossAlive = true;
        noTargetTicks = 0;
        zombieWasAlive = true;
        skeletonWasAlive = true;
        lastDeathLocation = null;
        startAI();
    }

    // ========================================================================
    // AI
    // ========================================================================

    private static void startAI() {
        aiTask = new BukkitRunnable() {
            @Override
            public void run() {
                boolean zombieHorseAlive = zombieHorse != null && !zombieHorse.isDead() && zombieHorse.isValid();
                boolean spiderAlive = spider != null && !spider.isDead() && spider.isValid();
                boolean zombieAlive = zombie != null && !zombie.isDead() && zombie.isValid();
                boolean skeletonAlive = skeleton != null && !skeleton.isDead() && skeleton.isValid();

                // 记录骑士死亡位置: 后一个死的覆盖前一个, 掉落物随后死亡者位置生成
                if (zombie != null && zombieWasAlive && !zombieAlive) lastDeathLocation = zombie.getLocation();
                if (skeleton != null && skeletonWasAlive && !skeletonAlive) lastDeathLocation = skeleton.getLocation();
                zombieWasAlive = zombieAlive;
                skeletonWasAlive = skeletonAlive;

                // 两名骑士全部死亡 → BOSS被击败 (坐骑是纯坐骑)
                if (!zombieAlive && !skeletonAlive) {
                    onDeath();
                    cancel();
                    return;
                }

                // 更新两条BossBar
                if (zombieAlive) {
                    updateBar(zombieBar, zombie, ZOMBIE_HEALTH, "§2◆ 铁骑双雄·僵尸骑士");
                } else {
                    hideBar(zombieBar);
                }
                if (skeletonAlive) {
                    updateBar(skeletonBar, skeleton, SKELETON_HEALTH, "§7◆ 铁骑双雄·骷髅射手");
                } else {
                    hideBar(skeletonBar);
                }

                // 坐骑血量同步到BossMenu (供环境伤害保护读取)
                double mountHp = (zombieHorseAlive ? zombieHorse.getHealth() : 0)
                        + (spiderAlive ? spider.getHealth() : 0);
                BossMenu.syncBossHealth(BossMenu.BossType.DESERT_CAMEL, mountHp, MOUNT_HEALTH * 2);

                // 索敌 (仇怨目标优先)
                LivingEntity target = Enmity.getEnemy(zombie, skeleton, zombieHorse, spider);
                if (target == null) target = findNearestPlayer();
                if (target == null) {
                    noTargetTicks++;
                    if (noTargetTicks >= DESPAWN_TICKS) {
                        Bukkit.broadcastMessage("§2铁骑双雄因无人应战而消失...");
                        despawn();
                        cancel();
                    }
                    return;
                }
                noTargetTicks = 0;

                if (zombieAlive) zombie.setTarget(target);
                if (skeletonAlive) skeleton.setTarget(target);
                if (zombieHorseAlive) zombieHorse.getPathfinder().moveTo(target.getLocation(), 1.0);
                if (spiderAlive) spider.getPathfinder().moveTo(target.getLocation(), 1.0);
            }
        }.runTaskTimer(getplugin, 20L, 10L);
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
    // 死亡 & 消失
    // ========================================================================

    public static void onDeath() {
        if (!bossAlive) return; // 防止重复调用
        if (aiTask != null) aiTask.cancel();

        Location loc = lastDeathLocation != null ? lastDeathLocation
                : (zombieHorse != null ? zombieHorse.getLocation()
                : (spider != null ? spider.getLocation()
                : (zombie != null ? zombie.getLocation()
                : (skeleton != null ? skeleton.getLocation() : null))));
        if (loc == null) { cleanup(); return; }
        World world = loc.getWorld();

        world.strikeLightningEffect(loc);
        world.createExplosion(loc, 0f, false, false);
        world.spawnParticle(Particle.ENCHANT, loc, 60, 2, 2, 2, 0.2);
        world.playSound(loc, Sound.ENTITY_WITHER_DEATH, 1.0f, 0.6f);

        // 掉落: 钢锭 1~3, 稀罕的魔法球 1~2, 经验瓶, 15%狙击附魔书
        world.dropItemNaturally(loc, SteelIngot(1 + RANDOM.nextInt(3)));
        world.dropItemNaturally(loc, ArmsorItem.MagicBallCreateII(1 + RANDOM.nextInt(2)));
        world.dropItemNaturally(loc, new ItemStack(Material.EXPERIENCE_BOTTLE, 16));
        if (RANDOM.nextInt(100) < 15) {
            world.dropItemNaturally(loc, ArmsorItem.Sniping_EnchantdeBook(1, 1 + RANDOM.nextInt(3)));
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§2◆ 铁骑双雄已被击败！");
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

        BossMenu.unregisterBoss(BossMenu.BossType.DESERT_CAMEL);

        if (zombieBar != null) { zombieBar.removeAll(); zombieBar.setVisible(false); zombieBar = null; }
        if (skeletonBar != null) { skeletonBar.removeAll(); skeletonBar.setVisible(false); skeletonBar = null; }

        if (zombieHorse != null && !zombieHorse.isDead()) zombieHorse.remove();
        if (spider != null && !spider.isDead()) spider.remove();
        if (zombie != null && !zombie.isDead()) zombie.remove();
        if (skeleton != null && !skeleton.isDead()) skeleton.remove();

        zombieHorse = null;
        spider = null;
        zombie = null;
        skeleton = null;
        bossAlive = false;
        noTargetTicks = 0;
        zombieWasAlive = false;
        skeletonWasAlive = false;
        lastDeathLocation = null;
    }

    // ========================================================================
    // 装备
    // ========================================================================

    public static ItemStack createSpear() {
        // 1.21.11 新增 Spear 武器系列, 用下界合金长矛作为僵尸主武器
        ItemStack spear = new ItemStack(Material.NETHERITE_SPEAR);
        ItemMeta meta = spear.getItemMeta();
        meta.setDisplayName("§6平原之星");
        meta.addEnchant(Enchantment.SHARPNESS, 70, true);
        meta.addEnchant(Enchantment.KNOCKBACK, 7, true);
        meta.addEnchant(Enchantment.FIRE_ASPECT, 5, true);
        meta.setUnbreakable(true);
        // 基础伤害25: 移除原版长矛自带伤害修饰符, 再写入固定25
        meta.removeAttributeModifier(Attribute.ATTACK_DAMAGE);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(new NamespacedKey(getplugin, "iron_cavalry_spear_damage"),
                        25.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        spear.setItemMeta(meta);
        ArmsorEnchant.addEnchant(spear, NamespaceKey.Keys.PierceKey, 3);                       // 贯穿III
        ArmsorEnchant.addEnchantLore(spear, ChatColor.DARK_PURPLE + "贯穿", 3, NamespaceKey.Keys.PierceKey);
        return spear;
    }

    public static ItemStack createShield() {
        ItemStack shield = new ItemStack(Material.SHIELD);
        ItemMeta meta = shield.getItemMeta();
        meta.setDisplayName("§7盾");
        meta.addEnchant(Enchantment.UNBREAKING, 5, true);
        shield.setItemMeta(meta);
        return shield;
    }

    public static ItemStack createBow() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        meta.setDisplayName("§f平原之耀");
        meta.addEnchant(Enchantment.POWER, 255, true);
        meta.addEnchant(Enchantment.PUNCH, 3, true);
        meta.addEnchant(Enchantment.FLAME, 1, true);
        meta.setUnbreakable(true);
        bow.setItemMeta(meta);
        return bow;
    }

    public static void equipNetheriteArmor(LivingEntity entity) {
        EntityEquipment equip = entity.getEquipment();
        equip.setHelmet(armorPiece(Material.NETHERITE_HELMET));
        equip.setChestplate(armorPiece(Material.NETHERITE_CHESTPLATE));
        equip.setLeggings(armorPiece(Material.NETHERITE_LEGGINGS));
        equip.setBoots(armorPiece(Material.NETHERITE_BOOTS));
        equip.setHelmetDropChance(0f);
        equip.setChestplateDropChance(0f);
        equip.setLeggingsDropChance(0f);
        equip.setBootsDropChance(0f);
    }

    private static ItemStack armorPiece(Material material) {
        ItemStack piece = new ItemStack(material);
        ItemMeta meta = piece.getItemMeta();
        meta.addEnchant(Enchantment.PROTECTION, 4, true);
        meta.addEnchant(Enchantment.UNBREAKING, 3, true);
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        piece.setItemMeta(meta);
        return piece;
    }

    // ========================================================================
    // 工具
    // ========================================================================

    private static Player findNearestPlayer() {
        LivingEntity active = zombieHorse;
        if (active == null || active.isDead()) active = spider;
        if (active == null || active.isDead()) active = zombie;
        if (active == null || active.isDead()) active = skeleton;
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

    private static Location findSpawnLocation(Player summoner) {
        return BossSpawn.ringSpawn(summoner, 5);
    }
}
