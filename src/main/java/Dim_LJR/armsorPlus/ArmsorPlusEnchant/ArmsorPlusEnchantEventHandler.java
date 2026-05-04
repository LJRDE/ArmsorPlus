package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
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
            player.sendMessage(ChatColor.DARK_PURPLE + "你影避了伤害");
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

        // 100%触发 (原为Math.random() < 1, 就是必触发)
        event.setCancelled(true);

        Firework firework = (Firework) player.getWorld().spawnEntity(
                player.getLocation(), EntityType.FIREWORK_ROCKET);

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
        player.sendMessage(ChatColor.GOLD + "蓄爆效果触发！发射了烟花火箭");
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

        player.sendMessage(ChatColor.RED + "复仇效果反弹了"
                + String.format("%.1f", revengeDamage) + "点伤害！");

        if (target instanceof Player attacker) {
            attacker.sendMessage(ChatColor.RED + player.getName() + "的复仇效果反弹了你的攻击！");
        }

        player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR,
                target.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5);
    }

    // ========================================================================
    // 凋零 —— 攻击造成凋零效果 (武器)
    // ========================================================================

    @EventHandler
    public void WitheringHandler(EntityDamageByEntityEvent event) {
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
        if (damager instanceof Player) {
            ((Player) damager).sendMessage(ChatColor.GOLD + "你的武器" + msg);
        }
        if (target instanceof Player) {
            target.sendMessage(ChatColor.RED + "你被" + getEntityName(damager) + msg);
        }
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
            player.sendActionBar(ChatColor.BLUE + "格挡效果减免了"
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
            victim.sendActionBar(ChatColor.AQUA + "你被敌人施加了寒冻");
            if (damager instanceof Player) {
                ((Player) damager).sendActionBar(ChatColor.AQUA + "你对敌人施加了寒冻");
            }
        } else if (event.getEntity() instanceof LivingEntity target) {
            target.addPotionEffect(new PotionEffect(
                    PotionEffectType.SLOWNESS, 20 * level, 1));
            if (damager instanceof Player) {
                ((Player) damager).sendActionBar(ChatColor.AQUA + "你对敌人施加了寒冻");
            }
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
            player.sendActionBar("§b[涤魂]魔法伤害免疫 §7(冷却中)");
        } else {
            long remain = cd - (now - last);
            player.sendActionBar("§c[涤魂]魔法免疫冷却中 §7(" + (remain / 1000) + "秒)");
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
            player.sendActionBar(ChatColor.GOLD + "你闪避了对方的伤害");
            damager.sendActionBar(ChatColor.GOLD + "对方闪避了你的伤害");
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
            player.sendActionBar(ChatColor.GOLD + "你闪避了对方的伤害");
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

    @EventHandler
    public void RipplesHandler(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        int level = ArmsorEnchant.getEnchantLevel(
                entity.getEquipment().getBoots(), RipplesProtectkey);
        if (level <= 0) return;

        double maxHp = entity.getMaxHealth();
        double newHp = entity.getHealth() + level - event.getDamage();

        if (newHp >= maxHp) {
            entity.setHealth(maxHp);
            event.setDamage(0);
        } else if (level > event.getDamage()) {
            event.setDamage(0);
            entity.setHealth(newHp);
        } else {
            event.setDamage(event.getDamage() - level);
        }

        if (entity instanceof Player) {
            entity.sendMessage(ChatColor.BLUE + "涟漪恢复了" + level + "点生命值");
        }
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

        if (event.getDamager() instanceof Player) {
            event.getDamager().sendMessage("你发动了" + ChatColor.RED + "双重打击"
                    + ChatColor.RESET + "对对方造成" + event.getDamage() + "点伤害");
        }
        if (event.getEntity() instanceof Player) {
            event.getEntity().sendMessage("对方发动了" + ChatColor.RED + "双重打击"
                    + ChatColor.RESET + "对你造成" + event.getDamage() + "点伤害");
        }
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
        damager.damage(15, damager);

        if (event.getDamager() instanceof Player) {
            event.getDamager().sendMessage("你发动了" + ChatColor.RED + "血祭"
                    + ChatColor.RESET + "对对方造成" + rate + "倍伤害");
        }
        if (event.getEntity() instanceof Player) {
            event.getEntity().sendMessage("对方发动了" + ChatColor.RED + "血祭"
                    + ChatColor.RESET + "对你造成" + rate + "倍伤害");
        }
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
        if (level == 0 || !percent(20 * level)) return;

        // 目标扣血
        double targetHp = target.getHealth() - 2;
        target.setHealth(Math.max(0, targetHp));

        // 攻击者回血
        double healerHp = Math.min(damager.getHealth() + 2, damager.getMaxHealth());
        damager.setHealth(healerHp);

        if (event.getDamager() instanceof Player) {
            event.getDamager().sendMessage(ChatColor.RED + "你对敌人施加了吸血");
        }
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
            victim.sendMessage(ChatColor.GREEN + "你被敌人施加了饥荒");
            if (event.getDamager() instanceof Player) {
                event.getDamager().sendMessage(ChatColor.GREEN + "你对敌人施加了饥荒");
            }
        } else {
            if (event.getDamager() instanceof Player) {
                event.getDamager().sendMessage(ChatColor.GREEN + "你对敌人施加了饥荒");
            }
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
                player.sendMessage(ChatColor.LIGHT_PURPLE + "获得狙击附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Famine_EnchantdeBook(1, r.nextInt(3) + 1));
                player.sendMessage(ChatColor.GREEN + "获得饥荒附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Dodge_EnchantdeBook(1, r.nextInt(4) + 1));
                player.sendMessage(ChatColor.GOLD + "获得闪避附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Ripples_EnchantdeBook(1, r.nextInt(3) + 1));
                player.sendMessage(ChatColor.BLUE + "获得涟漪附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(BloodSacrifice_EnchantdeBook(1, r.nextInt(3) + 1));
                player.sendMessage(ChatColor.DARK_RED + "获得血祭附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(EffectClear_EnchantdeBook(1, r.nextInt(5) + 1));
                player.sendMessage(ChatColor.WHITE + "获得涤魂附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Freeze_EnchantedBook(1, r.nextInt(3) + 1));
                player.sendMessage(ChatColor.AQUA + "获得寒冻附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Revenge_EnchantedBook(1, r.nextInt(3) + 1));
                player.sendMessage(ChatColor.DARK_RED + "获得复仇附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(HealthBoost_EnchantedBook(1, r.nextInt(4) + 1));
                player.sendMessage(ChatColor.RED + "获得生命提升附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(ExplosiveArrow_EnchantedBook(1, r.nextInt(3) + 1));
                player.sendMessage(ChatColor.YELLOW + "获得蓄爆附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Withering_EnchantedBook(1, r.nextInt(5) + 1));
                player.sendMessage(ChatColor.DARK_PURPLE + "获得凋零附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Blocking_EnchantedBook(1, r.nextInt(5) + 1));
                player.sendMessage(ChatColor.AQUA + "获得格挡附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Survivor_EnchantedBook(1, r.nextInt(5) + 1));
                player.sendMessage(ChatColor.GOLD + "获得幸存附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(ShadowDodge_EnchantdeBook(1, r.nextInt(5) + 1));
                player.sendMessage(ChatColor.DARK_PURPLE + "获得影避附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(ArrowSpeed_EnchantdeBook(1, r.nextInt(5) + 1));
                player.sendMessage(ChatColor.GOLD + "获得弹道附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(DoubleHit_EnchantdeBook(1, r.nextInt(5) + 1));
                player.sendMessage(ChatColor.GOLD + "获得双重打击附魔书");
                count++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Feeding_EnchantdeBook(1, r.nextInt(5) + 1));
                player.sendMessage(ChatColor.GOLD + "获得吸血附魔书");
                count++;
            }
        }

        player.sendMessage("获得数量: " + count);
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

        player.sendMessage(ChatColor.GOLD + "护腿上的幸存效果触发！成功规避死亡");

        if (event instanceof EntityDamageByEntityEvent e) {
            if (e.getDamager() instanceof Player attacker) {
                attacker.sendMessage(ChatColor.YELLOW + player.getName()
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
}
