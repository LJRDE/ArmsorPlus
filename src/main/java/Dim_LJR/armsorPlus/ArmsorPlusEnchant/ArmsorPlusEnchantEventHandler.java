package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import Dim_LJR.armsorPlus.PlayerSettings;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant.addEnchantLore;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;

/**
 * 自定义附魔效果的事件处理 —— 所有附魔的触发逻辑。
 * <p>
 * 包括: 闪避、影避、饥荒、涟漪、血祭、涤魂、寒冻、格挡、凋零、
 * 幸存、复仇、生命提升、蓄爆、双重打击、吸血、弹道、狙击
 */
public class ArmsorPlusEnchantEventHandler implements Listener {

    // ========================================================================
    // 工具方法
    // ========================================================================

    /** 百分率随机: 返回 true 的概率为 x% */
    public boolean percent(int x) {
        return new Random().nextInt(101) <= x;
    }

    /** 将整数转为罗马数字 (I~X) */
    public static String romanNumeral(int num) {
        return switch (num) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(num);
        };
    }

    /** 获取实体的可读名称 */
    private String getEntityName(Entity entity) {
        if (entity instanceof Player) return ((Player) entity).getName();
        return entity.getCustomName() != null ? entity.getCustomName() : entity.getName();
    }

    // ========================================================================
    // 弹道 & 狙击 —— 提升箭矢速度
    // ========================================================================

    @EventHandler
    public void ArrowSpeedHander(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Arrow)) return;
        Arrow arrow = (Arrow) event.getEntity();
        if (!(arrow.getShooter() instanceof LivingEntity)) return;
        LivingEntity shooter = (LivingEntity) arrow.getShooter();

        int speedLevel = ArmsorEnchant.getEnchantLevel(shooter.getActiveItem(), ArrowSpeed);
        if (speedLevel == 0) return;

        Vector velocity = arrow.getVelocity();
        velocity.multiply(speedLevel);

        int snipeLevel = ArmsorEnchant.getEnchantLevel(shooter.getActiveItem(), Sniping);
        if (snipeLevel != 0) {
            velocity.multiply(5L * snipeLevel);
        }
        arrow.setVelocity(velocity);
    }

    // ========================================================================
    // 影避 —— 概率闪避所有伤害 (靴子)
    // ========================================================================

    @EventHandler
    public void ShadowDodgeHandler(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        int level = ArmsorEnchant.getEnchantLevel(equipment.getBoots(), ShadowDodge);
        if (level == 0) return;
        if (!percent(6 * level)) return;

        event.setCancelled(true);
        Particle.PORTAL.builder()
                .location(event.getEntity().getLocation())
                .offset(0.1, 0.1, 0.1)
                .count(96)
                .receivers(32, true)
                .spawn();

        if (event.getEntity() instanceof Player player) {
            PlayerSettings.notify(player, ChatColor.DARK_PURPLE + "你影避了伤害");
        }
    }

    // ========================================================================
    // 蓄爆 —— 概率射出爆炸烟花 (弓/弩)
    // ========================================================================

    @EventHandler
    public void ExplosiveArrowHandler(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack bow = event.getBow();
        if (bow == null) return;

        int level = ArmsorEnchant.getEnchantLevel(bow, ExplosiveArrowKey);
        if (level <= 0) return;

        // 蓄爆与弹道/狙击不共存
        if (ArmsorEnchant.getEnchantLevel(bow, ArrowSpeed) > 0
                || ArmsorEnchant.getEnchantLevel(bow, Sniping) > 0) return;

        event.setCancelled(true);

        // 在玩家前方生成烟花，避免在体内爆炸
        Location spawnLoc = player.getEyeLocation().add(
                player.getLocation().getDirection().multiply(1.5));

        Firework firework = (Firework) player.getWorld().spawnEntity(
                spawnLoc, EntityType.FIREWORK_ROCKET);

        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.RED).withFade(Color.ORANGE)
                .with(FireworkEffect.Type.BURST).withTrail().build());
        meta.setPower(1);
        firework.setFireworkMeta(meta);

        @NotNull Vector velocity = player.getLocation().getDirection()
                .multiply(event.getForce() * 3.0);
        firework.setVelocity(velocity);
        firework.setMetadata("ExplosiveArrow",
                new FixedMetadataValue(getplugin, level));

        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
        PlayerSettings.notify(player, ChatColor.GOLD + "蓄爆效果触发！发射了烟花火箭");
    }

    /** 蓄爆烟花爆炸时造成范围伤害 */
    @EventHandler
    public void ExplosiveArrowExplode(FireworkExplodeEvent event) {
        Firework firework = event.getEntity();
        if (!firework.hasMetadata("ExplosiveArrow")) return;

        int level = firework.getMetadata("ExplosiveArrow").get(0).asInt();
        Location loc = firework.getLocation();

        loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(),
                2.0f, false, false);

        double damage = 30.0 * level;
        for (Entity entity : loc.getWorld().getNearbyEntities(loc, 3, 3, 3)) {
            if (entity instanceof LivingEntity && entity != firework) {
                ((LivingEntity) entity).damage(damage);
            }
        }
    }

    // ========================================================================
    // 复仇 —— 反弹伤害 (胸甲)
    // ========================================================================

    @EventHandler(priority = EventPriority.LOW)
    public void RevengeHandler(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack chestplate = player.getEquipment().getChestplate();
        if (chestplate == null) return;

        int level = ArmsorEnchant.getEnchantLevel(chestplate, RevengeKey);
        if (level <= 0) return;

        if (Math.random() >= 0.2 * level) return;

        Entity damager = event.getDamager();
        if (!(damager instanceof LivingEntity target)) return;

        double revengeDamage = event.getDamage() * 0.5;
        target.damage(revengeDamage, player);

        PlayerSettings.notify(player, ChatColor.RED + "复仇效果反弹了"
                + String.format("%.1f", revengeDamage) + "点伤害！");

        PlayerSettings.notify(target, ChatColor.RED + player.getName() + "的复仇效果反弹了你的攻击！");

        player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR,
                target.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5);
    }

    // ========================================================================
    // 凋零 —— 攻击造成凋零效果 (武器)
    // ========================================================================

    @EventHandler
    public void WitheringHandler(EntityDamageByEntityEvent event) {
        if (event.getEntity().equals(event.getDamager())) return;
        Entity damager = event.getDamager();
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        ItemStack weapon = null;
        if (damager instanceof Player) {
            weapon = ((Player) damager).getInventory().getItemInMainHand();
        } else if (damager instanceof LivingEntity living) {
            weapon = living.getEquipment().getItemInMainHand();
        }

        if (weapon == null || weapon.getType() == Material.AIR) return;

        int level = ArmsorEnchant.getEnchantLevel(weapon, WitheringKey);
        if (level <= 0) return;

        int duration = level * 5 * 20; // level*5 秒 (ticks)
        int effectLevel = Math.min(level, 2);
        target.addPotionEffect(new PotionEffect(
                PotionEffectType.WITHER, duration, effectLevel, false, true));

        String msg = "施加了凋零" + romanNumeral(effectLevel + 1)
                + "（时长：" + (level * 5) + "秒）";
        PlayerSettings.notify(damager, ChatColor.GOLD + "你的武器" + msg);
        PlayerSettings.notify(target, ChatColor.RED + "你被" + getEntityName(damager) + msg);
    }

    // ========================================================================
    // 格挡 —— 按伤害比例减免 (头盔)
    // ========================================================================

    @EventHandler
    public void BlockingHandler(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        int level = ArmsorEnchant.getEnchantLevel(equipment.getHelmet(), BlockingKey);
        if (level <= 0) return;

        double original = event.getDamage();
        double rate;
        if (original > 120) rate = 0.25;   // 超高伤害格挡75%
        else if (original > 80) rate = 0.4; // 高伤害格挡60%
        else rate = 0.6;                      // 普通伤害格挡40%

        event.setDamage(original * rate);

        if (entity instanceof Player player) {
            double blocked = original - event.getDamage();
            PlayerSettings.notifyActionBar(player, ChatColor.BLUE + "格挡效果减免了"
                    + String.format("%.1f", blocked) + "点伤害");
        }
    }

    // ========================================================================
    // 寒冻 —— 攻击造成缓慢效果 (武器)
    // ========================================================================

    @EventHandler
    public void FreezeHandler(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity damager)) return;

        int level = ArmsorEnchant.getEnchantLevel(
                damager.getEquipment().getItemInMainHand(), FreezeKey);
        if (level == 0) return;

        if (event.getEntity() instanceof Player victim) {
            victim.addPotionEffect(new PotionEffect(
                    PotionEffectType.SLOWNESS, 20 * level, 1));
            PlayerSettings.notifyActionBar(victim, ChatColor.AQUA + "你被敌人施加了寒冻");
            PlayerSettings.notifyActionBar(damager, ChatColor.AQUA + "你对敌人施加了寒冻");
        } else if (event.getEntity() instanceof LivingEntity target) {
            target.addPotionEffect(new PotionEffect(
                    PotionEffectType.SLOWNESS, 20 * level, 1));
            PlayerSettings.notifyActionBar(damager, ChatColor.AQUA + "你对敌人施加了寒冻");
        }
    }

    // ========================================================================
    // 涤魂 —— 周期性免疫魔法伤害 (胸甲)
    // ========================================================================

    private static final EntityDamageEvent.DamageCause[] MAGIC_DAMAGE_TYPES = {
            EntityDamageEvent.DamageCause.MAGIC,
            EntityDamageEvent.DamageCause.POISON,
            EntityDamageEvent.DamageCause.WITHER,
            EntityDamageEvent.DamageCause.DRAGON_BREATH
    };

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    private boolean isMagicDamage(EntityDamageEvent.DamageCause cause) {
        for (var type : MAGIC_DAMAGE_TYPES) {
            if (cause == type) return true;
        }
        return false;
    }

    @EventHandler
    public void onMagicDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack chestplate = player.getInventory().getChestplate();
        int level = ArmsorEnchant.getEnchantLevel(chestplate, EffectClear);
        if (level <= 0) return;
        if (!isMagicDamage(event.getCause())) return;

        UUID pid = player.getUniqueId();
        long now = System.currentTimeMillis();
        Long last = cooldowns.get(pid);
        long cd = (6L - level) * 1000; // 冷却时间 (秒)

        if (last == null || (now - last) >= cd) {
            event.setCancelled(true);
            cooldowns.put(pid, now);
            player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.5f);
            player.spawnParticle(Particle.ENCHANT, player.getLocation(), 20, 0.5, 0.5, 0.5);
            PlayerSettings.notifyActionBar(player, "§b[涤魂]魔法伤害免疫 §7(冷却中)");
        } else {
            long remain = cd - (now - last);
            PlayerSettings.notifyActionBar(player, "§c[涤魂]魔法免疫冷却中 §7(" + (remain / 1000) + "秒)");
        }
    }

    // ========================================================================
    // 闪避 —— 概率闪避攻击伤害 (靴子)
    // ========================================================================

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnBeaten(EntityDamageByEntityEvent event) {
        // 玩家闪避玩家
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof Player damager) {
            ItemStack boots = player.getInventory().getBoots();
            int level = ArmsorEnchant.getEnchantLevel(boots, Dodgekey);
            if (level == 0 || !percent(15 * level)) return;

            event.setCancelled(true);
            Particle.ENCHANTED_HIT.builder().location(player.getLocation())
                    .offset(0.1, 0.1, 0.1).count(48).receivers(32, true).spawn();
            PlayerSettings.notifyActionBar(player, ChatColor.GOLD + "你闪避了对方的伤害");
            PlayerSettings.notifyActionBar(damager, ChatColor.GOLD + "对方闪避了你的伤害");
            return;
        }

        // 玩家闪避生物
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof LivingEntity) {
            ItemStack boots = player.getInventory().getBoots();
            int level = ArmsorEnchant.getEnchantLevel(boots, Dodgekey);
            if (level == 0 || !percent(20 * level)) return;

            event.setCancelled(true);
            Particle.PORTAL.builder().location(player.getLocation())
                    .offset(0.1, 0.1, 0.1).count(64).receivers(32, true).spawn();
            PlayerSettings.notifyActionBar(player, ChatColor.GOLD + "你闪避了对方的伤害");
            return;
        }

        // 生物闪避生物
        if (event.getEntity() instanceof LivingEntity entity) {
            EntityEquipment equipment = entity.getEquipment();
            if (equipment == null) return;
            ItemStack boots = equipment.getBoots();
            int level = ArmsorEnchant.getEnchantLevel(boots, Dodgekey);
            if (level == 0 || !percent(20 * level)) return;

            event.setCancelled(true);
            Particle.PORTAL.builder().location(entity.getLocation())
                    .offset(0.1, 0.1, 0.1).count(64).receivers(32, true).spawn();
        }
    }

    // ========================================================================
    // 涟漪 —— 受到伤害时回复生命 (靴子)
    // ========================================================================

    // 涟漪 —— 减缓血量下降，每级减少20%伤害 (靴子, 满级III)
    @EventHandler
    public void RipplesHandler(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        int level = ArmsorEnchant.getEnchantLevel(
                entity.getEquipment().getBoots(), RipplesProtectkey);
        if (level <= 0) return;

        double reduction = 1.0 - (0.2 * level); // 每级减缓20%血量下降
        event.setDamage(event.getDamage() * reduction);

        PlayerSettings.notify(entity, ChatColor.BLUE + "涟漪减缓了" + (int)(level * 20) + "%伤害");
    }

    // ========================================================================
    // 双重打击 —— 概率双倍伤害 (武器)
    // ========================================================================

    @EventHandler
    public void DoubleHit(EntityDamageByEntityEvent event) {
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;

        int level = ArmsorEnchant.getEnchantLevel(
                damager.getEquipment().getItemInMainHand(), DoubleHitkey);
        if (level == 0 || !percent(20 * level)) return;

        event.setDamage(event.getDamage() * 2);

        PlayerSettings.notify(event.getDamager(), "你发动了" + ChatColor.RED + "双重打击"
                + ChatColor.RESET + "对对方造成" + event.getDamage() + "点伤害");
        PlayerSettings.notify(event.getEntity(), "对方发动了" + ChatColor.RED + "双重打击"
                + ChatColor.RESET + "对你造成" + event.getDamage() + "点伤害");
    }

    // ========================================================================
    // 血祭 —— 概率消耗生命造成多倍伤害 (剑)
    // ========================================================================

    @EventHandler
    public void OnDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getEntity() instanceof LivingEntity)
                || !(event.getDamager() instanceof LivingEntity damager)) return;

        int level = ArmsorEnchant.getEnchantLevel(
                damager.getEquipment().getItemInMainHand(), BloodSacrificekey);
        if (level == 0 || !percent(20 * level)) return;

        int rate = new Random().nextInt(level) + 2;
        event.setDamage(event.getDamage() * rate);
        damager.damage(10, damager); // 穿透伤害

        PlayerSettings.notify(event.getDamager(), "你发动了" + ChatColor.RED + "血祭"
                + ChatColor.RESET + "对对方造成" + rate + "倍伤害");
        PlayerSettings.notify(event.getEntity(), "对方发动了" + ChatColor.RED + "血祭"
                + ChatColor.RESET + "对你造成" + rate + "倍伤害");
    }

    // ========================================================================
    // 吸血 —— 攻击时吸取生命 (武器)
    // ========================================================================

    @EventHandler
    public void FeedingEnchant(EntityDamageByEntityEvent event) {
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getEntity() instanceof LivingEntity target)
                || !(event.getDamager() instanceof LivingEntity damager)) return;

        int level = ArmsorEnchant.getEnchantLevel(
                damager.getEquipment().getItemInMainHand(), Feedingkey);
        if (level == 0) return;

        double healAmount = 1.5 * level;

        // 目标扣血
        double targetHp = target.getHealth() - healAmount;
        target.setHealth(Math.max(0, targetHp));

        // 攻击者回血
        double healerHp = Math.min(damager.getHealth() + healAmount, damager.getMaxHealth());
        damager.setHealth(healerHp);

        PlayerSettings.notify(event.getDamager(), ChatColor.RED + "吸血 恢复了" + String.format("%.1f", healAmount) + "点生命值");
    }

    // ========================================================================
    // 饥荒 —— 攻击造成饥饿效果 (武器)
    // ========================================================================

    @EventHandler
    public void FamineHandler(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity damager)) return;

        int level = ArmsorEnchant.getEnchantLevel(
                damager.getEquipment().getItemInMainHand(), Faminekey);
        if (level == 0) return;

        if (event.getEntity() instanceof Player victim) {
            victim.addPotionEffect(new PotionEffect(
                    PotionEffectType.HUNGER, 40 * level, 4 * level));
            PlayerSettings.notify(victim, ChatColor.GREEN + "你被敌人施加了饥荒");
            PlayerSettings.notify(event.getDamager(), ChatColor.GREEN + "你对敌人施加了饥荒");
        } else {
            PlayerSettings.notify(event.getDamager(), ChatColor.GREEN + "你对敌人施加了饥荒");
        }
    }

    // ========================================================================
    // 魔法球 —— 右键抽取随机附魔书
    // ========================================================================

    @EventHandler
    public void IfUSEMagicBallEvent(PlayerInteractEvent event) {
        if (event.getAction().isLeftClick()) return;
        if (event.getItem() == null) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        int amount = ArmsorEnchant.getEnchantLevel(item, MagicBallKey);
        if (amount == 0) return;

        Random r = new Random(System.currentTimeMillis());
        item.setAmount(item.getAmount() - 1);
        event.setCancelled(true);

        int count = 0;
        // 每层保底循环: amount层, 每层独立概率获得附魔书
        for (int i = 0; i < amount; i++) {
            if (percent(10)) {
                player.getInventory().addItem(Sniping_EnchantdeBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.LIGHT_PURPLE + "获得狙击附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Famine_EnchantdeBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.GREEN + "获得饥荒附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Dodge_EnchantdeBook(1, r.nextInt(4) + 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得闪避附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Ripples_EnchantdeBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.BLUE + "获得涟漪附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(BloodSacrifice_EnchantdeBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.DARK_RED + "获得血祭附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(EffectClear_EnchantdeBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.WHITE + "获得涤魂附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Freeze_EnchantedBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.AQUA + "获得寒冻附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Revenge_EnchantedBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.DARK_RED + "获得复仇附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(HealthBoost_EnchantedBook(1, r.nextInt(4) + 1));
                PlayerSettings.notify(player,ChatColor.RED + "获得生命提升附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(ExplosiveArrow_EnchantedBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.YELLOW + "获得蓄爆附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Withering_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.DARK_PURPLE + "获得凋零附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Blocking_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.AQUA + "获得格挡附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Survivor_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得幸存附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(ShadowDodge_EnchantdeBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.DARK_PURPLE + "获得影避附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(ArrowSpeed_EnchantdeBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得弹道附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(DoubleHit_EnchantdeBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得双重打击附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Feeding_EnchantdeBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得吸血附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(QuickThrust_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得疾刺附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(DiamondDrill_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.AQUA + "获得金刚钻附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Blindness_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.DARK_GRAY + "获得失明附魔书");
                count++;
            }
            // 0.3I 新附魔
            if (percent(10)) {
                player.getInventory().addItem(ProtectionPRO_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得保护PRO附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Stun_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.DARK_GREEN + "获得眩晕附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(GolemGuardian_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.GRAY + "获得傀儡守护者附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(CriticalStrike_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.RED + "获得暴击附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Piercing_EnchantedBook(1, 1));
                PlayerSettings.notify(player,ChatColor.DARK_RED + "获得穿甲附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(LavaWalker_EnchantedBook(1, 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得熔岩行者附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(LightningCall_EnchantedBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.YELLOW + "获得唤雷附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Holographic_EnchantedBook(1, 1));
                PlayerSettings.notify(player,ChatColor.AQUA + "获得全息附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Tracking_EnchantedBook(1, 1));
                PlayerSettings.notify(player,ChatColor.GREEN + "获得追踪附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Harvest_EnchantedBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得丰收附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(AutoPlant_EnchantedBook(1, 1));
                PlayerSettings.notify(player,ChatColor.GREEN + "获得自动种植附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(StrongBurst_EnchantedBook(1, 1));
                PlayerSettings.notify(player,ChatColor.DARK_PURPLE + "获得强风暴附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(MultiShot_EnchantedBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.LIGHT_PURPLE + "获得千重射击附魔书");
                count++;
            }
        }

        PlayerSettings.notify(player,"获得数量: " + count);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.spawnParticle(Particle.FIREWORK, player.getLocation(), 30, 0.5, 1, 0.5, 0.2);
    }

    // ========================================================================
    // 幸存 —— 致命伤概率复活 (护腿)
    // ========================================================================

    @EventHandler(priority = EventPriority.HIGHEST)
    public void SurvivorHandler(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack leggings = player.getEquipment().getLeggings();
        int level = ArmsorEnchant.getEnchantLevel(leggings, SurvivorKey);
        if (level <= 0) return;

        // 非致命伤害不触发
        if (player.getHealth() - event.getFinalDamage() > 0) return;

        // 每级10%概率
        if (Math.random() >= level * 0.1) return;

        // 复活
        event.setCancelled(true);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setFireTicks(0);

        PlayerSettings.notify(player,ChatColor.GOLD + "护腿上的幸存效果触发！成功规避死亡");

        if (event instanceof EntityDamageByEntityEvent e) {
            if (e.getDamager() instanceof Player attacker) {
                PlayerSettings.notify(attacker, ChatColor.YELLOW + player.getName()
                        + " 的幸存附魔触发，规避了致命伤害");
            }
        }

        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING,
                player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.5);
        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.8f, 1.2f);

        // 3秒生命恢复
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.REGENERATION, 60, 10, false, false));
    }

    // ========================================================================
    // 金刚钻 —— 概率瞬间挖掉黑曜石 (镐子)
    // ========================================================================

    @EventHandler
    public void DiamondDrillHandler(BlockBreakEvent event) {
        if (event.getBlock().getType() != OBSIDIAN && event.getBlock().getType() != CRYING_OBSIDIAN) return;
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        int level = ArmsorEnchant.getEnchantLevel(tool, DiamondDrillKey);
        if (level == 0) return;
        if (!percent(20 * level)) {
            event.setCancelled(true);
            return;
        }
        PlayerSettings.notifyActionBar(player, ChatColor.AQUA + "金刚钻触发！瞬间挖掉黑曜石");
    }

    // ========================================================================
    // 失明 —— 攻击造成失明效果 (武器)
    // ========================================================================

    @EventHandler
    public void BlindnessHandler(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        int level = ArmsorEnchant.getEnchantLevel(
                damager.getEquipment().getItemInMainHand(), BlindnessKey);
        if (level == 0) return;

        target.addPotionEffect(new PotionEffect(
                PotionEffectType.BLINDNESS, 40 * level, 0, false, true));
        PlayerSettings.notifyActionBar(damager, ChatColor.DARK_GRAY + "你对敌人施加了失明");
        PlayerSettings.notifyActionBar(target, ChatColor.DARK_GRAY + "你被敌人施加了失明");
    }

    // ========================================================================
    // 不灭 —— 防止死亡 (任意装备栏)
    // ========================================================================

    @EventHandler(priority = EventPriority.HIGHEST)
    public void IndestructibleHandler(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.getHealth() - event.getFinalDamage() > 0) return;

        int level = 0;
        for (ItemStack item : player.getInventory().getArmorContents()) {
            level = Math.max(level, ArmsorEnchant.getEnchantLevel(item, IndestructibleKey));
        }
        level = Math.max(level, ArmsorEnchant.getEnchantLevel(player.getInventory().getItemInMainHand(), IndestructibleKey));
        level = Math.max(level, ArmsorEnchant.getEnchantLevel(player.getInventory().getItemInOffHand(), IndestructibleKey));
        if (level == 0) return;

        event.setCancelled(true);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setFireTicks(0);

        PlayerSettings.notify(player, ChatColor.GOLD + "不灭效果触发！成功规避死亡");

        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING,
                player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.5);
        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.8f, 1.2f);
    }

    // ========================================================================
    // 保护PRO —— 每级额外减少6%伤害 (胸甲, 满级V)
    // ========================================================================

    @EventHandler
    public void ProtectionPROHandler(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack chest = player.getEquipment().getChestplate();
        int level = ArmsorEnchant.getEnchantLevel(chest, ProtectionPROKey);
        if (level <= 0) return;

        double reduction = 1.0 - (0.06 * level);
        event.setDamage(event.getDamage() * reduction);
    }

    // ========================================================================
    // 眩晕 —— 攻击造成反胃效果 (剑, 每级0.7秒, 满级V)
    // ========================================================================

    @EventHandler
    public void StunHandler(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        int level = ArmsorEnchant.getEnchantLevel(
                damager.getEquipment().getItemInMainHand(), StunKey);
        if (level <= 0) return;

        int duration = (int)(level * 0.7 * 20);
        target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, duration, 0, false, false));
    }

    // ========================================================================
    // 傀儡守护者 —— 受伤时召唤铁傀儡反击 (胸甲, 每级1只, 冷却300秒, 满级V)
    // ========================================================================

    private final Map<UUID, Long> golemCooldowns = new HashMap<>();

    @EventHandler
    public void GolemGuardianHandler(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;

        ItemStack chest = player.getEquipment().getChestplate();
        int level = ArmsorEnchant.getEnchantLevel(chest, GolemGuardianKey);
        if (level <= 0) return;

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (golemCooldowns.containsKey(uuid) && (now - golemCooldowns.get(uuid)) < 300000) return;
        golemCooldowns.put(uuid, now);

        if (attacker.getLocation().distance(player.getLocation()) > 70) return;

        for (int i = 0; i < level; i++) {
            IronGolem golem = player.getWorld().spawn(player.getLocation(), IronGolem.class);
            golem.setTarget(attacker);
            golem.setPlayerCreated(false);
        }
        player.sendMessage(ChatColor.GRAY + "傀儡守护者: 召唤了" + level + "只铁傀儡");
    }

    // ========================================================================
    // 暴击 —— 概率造成额外伤害 (斧, 15%*level暴击, +25%*level伤害, 满级V)
    // ========================================================================

    @EventHandler
    public void CriticalStrikeHandler(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity damager)) return;

        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        int level = ArmsorEnchant.getEnchantLevel(weapon, CriticalStrikeKey);
        if (level <= 0) return;

        if (!percent(15 * level)) return;

        double bonus = 1.0 + (0.25 * level);
        event.setDamage(event.getDamage() * bonus);
        damager.getWorld().playSound(damager.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.2f);

        if (damager instanceof Player player) {
            PlayerSettings.notify(player, ChatColor.RED + "暴击! 造成" + (int)(25 * level) + "%额外伤害");
        }
    }

    // ========================================================================
    // 穿甲 —— 盾牌进入CD + 5点穿透伤害 (弓/弩)
    // ========================================================================

    @EventHandler
    public void PiercingHandler(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        ItemStack bow = player.getInventory().getItemInMainHand();
        int level = ArmsorEnchant.getEnchantLevel(bow, PiercingKey);
        if (level <= 0) return;

        // 如果目标使用盾牌，使其进入冷却
        if (target instanceof Player targetPlayer && targetPlayer.isBlocking()) {
            targetPlayer.setCooldown(Material.SHIELD, 100);
        }

        // 5点穿透伤害
        target.damage(5, player);
        target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0),
                10, 0.3, 0.3, 0.3, 0.1);
    }

    // ========================================================================
    // 熔岩行者 —— 岩浆行走5格内变为岩浆块，5秒后恢复 (靴子)
    // ========================================================================

    @EventHandler
    public void LavaWalkerHandler(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack boots = player.getEquipment().getBoots();
        int level = ArmsorEnchant.getEnchantLevel(boots, LavaWalkerKey);
        if (level <= 0) return;

        Location loc = player.getLocation();
        World world = loc.getWorld();

        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                for (int y = -1; y <= 0; y++) {
                    Location check = loc.clone().add(x, y, z);
                    if (check.getBlock().getType() == Material.LAVA) {
                        check.getBlock().setType(Material.MAGMA_BLOCK);
                        Bukkit.getScheduler().runTaskLater(getplugin, () -> {
                            if (check.getBlock().getType() == Material.MAGMA_BLOCK) {
                                check.getBlock().setType(Material.LAVA);
                            }
                        }, 100L);
                    }
                }
            }
        }
    }

    // ========================================================================
    // 唤雷 —— 无视天气召唤level道雷 (三叉戟, 满级III)
    // ========================================================================

    @EventHandler
    public void LightningCallHandler(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Trident trident)) return;
        if (!(trident.getShooter() instanceof Player player)) return;

        int level = ArmsorEnchant.getEnchantLevel(player.getInventory().getItemInMainHand(), LightningCallKey);
        if (level <= 0) return;

        Location hitLoc = trident.getLocation();
        World world = hitLoc.getWorld();

        for (int i = 0; i < level; i++) {
            world.strikeLightning(hitLoc);
        }
    }

    // ========================================================================
    // 全息 —— 持盾时扩展到全角度防御 (盾牌, 需右键举盾, 斧子可破盾)
    // ========================================================================

    @EventHandler
    public void HolographicHandler(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // 检查玩家是否正在举盾防御
        if (!player.isBlocking()) return;

        // 查找带有全息附魔的盾牌 (主手或副手)
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        ItemStack shield = mainHand.getType() == Material.SHIELD && ArmsorEnchant.getEnchantLevel(mainHand, HolographicKey) > 0
                ? mainHand : offHand.getType() == Material.SHIELD && ArmsorEnchant.getEnchantLevel(offHand, HolographicKey) > 0
                ? offHand : null;
        if (shield == null) return;

        // 消耗盾牌耐久
        Damageable dmg = (Damageable) shield.getItemMeta();
        dmg.setDamage(dmg.getDamage() + 1);
        if (dmg.getDamage() >= shield.getType().getMaxDurability()) {
            shield.setAmount(0);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
        } else {
            shield.setItemMeta(dmg);
        }

        // 斧子攻击: 盾牌进入冷却, 但不格挡伤害
        if (event.getDamager() instanceof LivingEntity damager) {
            ItemStack weapon = damager.getEquipment().getItemInMainHand();
            if (weapon.getType().name().endsWith("_AXE")) {
                player.setCooldown(Material.SHIELD, 100); // 5秒冷却
                player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0f, 1.0f);
                return;
            }
        }

        // 非斧子: 全角度格挡伤害
        event.setCancelled(true);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation().add(0, 1, 0),
                5, 0.3, 0.3, 0.3, 0);
    }

    // ========================================================================
    // 追踪 —— 箭矢追踪450格内指向目标 (弓)
    // ========================================================================

    private final Map<UUID, UUID> trackingArrows = new HashMap<>();

    @EventHandler
    public void TrackingHandler(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        int level = ArmsorEnchant.getEnchantLevel(event.getBow(), TrackingKey);
        if (level <= 0) return;

        LivingEntity target = null;
        double nearestDist = 450;
        for (Entity entity : player.getNearbyEntities(nearestDist, nearestDist, nearestDist)) {
            if (entity instanceof LivingEntity living && living != player
                    && player.hasLineOfSight(living)) {
                double angle = player.getEyeLocation().getDirection()
                        .angle(living.getLocation().add(0, 1, 0).subtract(player.getEyeLocation()).toVector());
                if (angle < 0.3) {
                    double dist = player.getLocation().distance(living.getLocation());
                    if (dist < nearestDist) {
                        nearestDist = dist;
                        target = living;
                    }
                }
            }
        }

        if (target != null && event.getProjectile() instanceof Arrow arrow) {
            trackingArrows.put(arrow.getUniqueId(), target.getUniqueId());
            Bukkit.getScheduler().runTaskTimer(getplugin, () -> {
                if (!arrow.isValid() || arrow.isDead()) {
                    trackingArrows.remove(arrow.getUniqueId());
                    return;
                }
                LivingEntity t = (LivingEntity) Bukkit.getEntity(trackingArrows.get(arrow.getUniqueId()));
                if (t == null || t.isDead()) {
                    trackingArrows.remove(arrow.getUniqueId());
                    return;
                }
                Vector toTarget = t.getLocation().add(0, 1, 0).subtract(arrow.getLocation()).toVector();
                arrow.setVelocity(toTarget.normalize().multiply(2.0));
            }, 0L, 2L);
        }
    }

    // ========================================================================
    // 丰收 —— 概率多倍收获 (锄头, 30%*level概率level+1倍, 满级III)
    // ========================================================================

    @EventHandler
    public void HarvestHandler(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack hoe = player.getInventory().getItemInMainHand();
        int level = ArmsorEnchant.getEnchantLevel(hoe, HarvestKey);
        if (level <= 0) return;

        Material block = event.getBlock().getType();
        if (!isCrop(block)) return;

        if (!percent(30 * level)) return;

        event.setDropItems(false);
        Collection<ItemStack> drops = event.getBlock().getDrops(hoe);
        for (int i = 0; i <= level; i++) {
            for (ItemStack drop : drops) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop.clone());
            }
        }
    }

    // 检查是否为作物 (包括甜浆果)
    private boolean isCrop(Material mat) {
        return mat == Material.WHEAT || mat == Material.CARROTS || mat == Material.POTATOES
                || mat == Material.BEETROOTS || mat == Material.NETHER_WART
                || mat == Material.SWEET_BERRY_BUSH || mat == Material.COCOA;
    }

    // ========================================================================
    // 自动种植 —— 采集作物时自动补种副手种子 (锄头)
    // ========================================================================

    @EventHandler
    public void AutoPlantHandler(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack hoe = player.getInventory().getItemInMainHand();
        int level = ArmsorEnchant.getEnchantLevel(hoe, AutoPlantKey);
        if (level <= 0) return;

        Material block = event.getBlock().getType();
        if (!isCrop(block)) return;

        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (offHand == null || offHand.getType() == Material.AIR) return;

        Material seedType = offHand.getType();
        Material soil = event.getBlock().getLocation().subtract(0, 1, 0).getBlock().getType();
        if (soil != Material.FARMLAND && soil != Material.SOUL_SAND) return;

        Material cropToPlant = getCropFromSeed(seedType);
        if (cropToPlant == null) return;

        Bukkit.getScheduler().runTaskLater(getplugin, () -> {
            Location loc = event.getBlock().getLocation();
            if (loc.getBlock().getType() == Material.AIR) {
                loc.getBlock().setType(cropToPlant);
                if (player.getGameMode() != GameMode.CREATIVE) {
                    offHand.setAmount(offHand.getAmount() - 1);
                }
            }
        }, 1L);
    }

    private Material getCropFromSeed(Material seed) {
        return switch (seed) {
            case WHEAT_SEEDS -> Material.WHEAT;
            case CARROT -> Material.CARROTS;
            case POTATO -> Material.POTATOES;
            case BEETROOT_SEEDS -> Material.BEETROOTS;
            case SWEET_BERRIES -> Material.SWEET_BERRY_BUSH;
            case COCOA_BEANS -> Material.COCOA;
            default -> null;
        };
    }

    // ========================================================================
    // 强风暴 —— 无需下落即可触发风暴 (重锤)
    // ========================================================================

    @EventHandler
    public void StrongBurstHandler(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (weapon.getType() != Material.MACE) return;
        int level = ArmsorEnchant.getEnchantLevel(weapon, StrongBurstKey);
        if (level <= 0) return;

        if (!(event.getEntity() instanceof LivingEntity target)) return;

        Location loc = target.getLocation();
        loc.getWorld().createExplosion(loc, 2.0f, false, false);
        loc.getWorld().spawnParticle(Particle.GUST, loc, 30, 2, 1, 2, 0.5);
        loc.getWorld().playSound(loc, Sound.ENTITY_WIND_CHARGE_WIND_BURST, 1.0f, 1.0f);
    }

    // ========================================================================
    // 千重射击 —— 射击时多射level支箭 (弩, 满级III)
    // ========================================================================

    @EventHandler
    public void MultiShotHandler(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player player)) return;

        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (weapon.getType() != Material.CROSSBOW) return;
        int level = ArmsorEnchant.getEnchantLevel(weapon, MultiShotKey);
        if (level <= 0) return;

        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection();
        World world = player.getWorld();

        for (int i = 0; i < level; i++) {
            double spread = (Math.random() - 0.5) * 0.3;
            Vector offset = new Vector(-dir.getZ() * spread, (Math.random() - 0.5) * 0.15, dir.getX() * spread);
            Arrow extraArrow = world.spawn(eye, Arrow.class);
            extraArrow.setShooter(player);
            extraArrow.setVelocity(dir.clone().add(offset).normalize().multiply(3.0));
        }
    }

    // ========================================================================
    // 剧毒 —— 攻击造成中毒效果 (武器)
    // ========================================================================

    @EventHandler
    public void PoisonHandler(EntityDamageByEntityEvent event) {
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;

        int level = ArmsorEnchant.getEnchantLevel(
                damager.getEquipment().getItemInMainHand(), PoisonKey);
        if (level <= 0) return;

        if (!(event.getEntity() instanceof LivingEntity target)) return;

        int duration = (level + 1) * 3 * 20; // (level+1)*3 秒 (ticks)
        int effectLevel = Math.min(level - 1, 2); // 最高中毒III
        target.addPotionEffect(new PotionEffect(
                PotionEffectType.POISON, duration, effectLevel, false, true));

        PlayerSettings.notify(event.getDamager(), ChatColor.DARK_GREEN + "你对敌人施加了剧毒"
                + romanNumeral(effectLevel + 1) + "（时长：" + ((level + 1) * 3) + "秒）");
        PlayerSettings.notify(event.getEntity(), ChatColor.DARK_GREEN + "你被施加了剧毒"
                + romanNumeral(effectLevel + 1));
    }

    // ========================================================================
    // 利刃 —— 目标护甲值越低伤害越高 (武器)
    // ========================================================================

    @EventHandler
    public void SharpBladeHandler(EntityDamageByEntityEvent event) {
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;

        int level = ArmsorEnchant.getEnchantLevel(
                damager.getEquipment().getItemInMainHand(), SharpBladeKey);
        if (level <= 0) return;

        if (!(event.getEntity() instanceof LivingEntity target)) return;

        // 计算目标护甲值
        double armor = 0;
        if (target.getEquipment() != null) {
            for (ItemStack armorPiece : target.getEquipment().getArmorContents()) {
                if (armorPiece != null && armorPiece.getType() != Material.AIR) {
                    armor += getArmorValue(armorPiece.getType());
                }
            }
        }

        double multiplier;
        if (armor > 18) {
            multiplier = 1.10; // 仅提升10%
        } else {
            // 护甲值越低伤害越高: (18 - armor) / 18 * level * 8% + 基础1.15
            multiplier = 1.0 + (18.0 - armor) / 18.0 * level * 0.08;
        }

        event.setDamage(event.getDamage() * multiplier);

        if (armor <= 18) {
            PlayerSettings.notify(event.getDamager(), ChatColor.DARK_AQUA + "利刃: 伤害提升"
                    + String.format("%.0f", (multiplier - 1) * 100) + "%");
        }
    }

    /** 根据护甲材质估算基础护甲值 */
    private static double getArmorValue(Material material) {
        String name = material.name().toLowerCase();
        if (name.contains("netherite")) {
            if (name.contains("chestplate")) return 8;
            if (name.contains("leggings")) return 6;
            if (name.contains("helmet") || name.contains("boots")) return 3;
        } else if (name.contains("diamond")) {
            if (name.contains("chestplate")) return 8;
            if (name.contains("leggings")) return 6;
            if (name.contains("helmet") || name.contains("boots")) return 3;
        } else if (name.contains("iron")) {
            if (name.contains("chestplate")) return 6;
            if (name.contains("leggings")) return 5;
            if (name.contains("helmet") || name.contains("boots")) return 2;
        } else if (name.contains("chainmail")) {
            if (name.contains("chestplate")) return 5;
            if (name.contains("leggings")) return 4;
            if (name.contains("helmet") || name.contains("boots")) return 2;
        } else if (name.contains("gold")) {
            if (name.contains("chestplate")) return 5;
            if (name.contains("leggings")) return 3;
            if (name.contains("helmet") || name.contains("boots")) return 2;
        } else if (name.contains("leather")) {
            if (name.contains("chestplate")) return 3;
            if (name.contains("leggings")) return 2;
            if (name.contains("helmet") || name.contains("boots")) return 1;
        } else if (name.contains("turtle")) {
            return 2; // 海龟壳
        }
        return 0;
    }
}
