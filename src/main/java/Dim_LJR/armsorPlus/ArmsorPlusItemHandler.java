package Dim_LJR.armsorPlus;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import Dim_LJR.armsorPlus.Boss.BossMenu;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;

/**
 * 处理所有自定义武器/物品的特殊效果。
 */
public class ArmsorPlusItemHandler implements Listener {

    private static final Map<UUID, Long> axeChargeStart = new HashMap<>();
    private static final Map<UUID, Integer> axeChargeTask = new HashMap<>();
    private static final Map<UUID, Integer> scepterCooldown = new HashMap<>();
    private static final Random RANDOM = new Random();

    // ========================================================================
    // 匕首: 保证额外5点伤害 (已通过属性修饰符实现，此处处理真实伤害)
    // ========================================================================

    @EventHandler
    public void onDaggerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, DaggerKey) == 0) return;

        // 匕首固定额外5点"真实"伤害
        if (event.getEntity() instanceof LivingEntity target) {
            target.damage(5);
            target.getWorld().spawnParticle(Particle.SWEEP_ATTACK,
                    target.getLocation().add(0, 1, 0), 3, 0.3, 0.3, 0.3, 0);
        }
    }

    // ========================================================================
    // 飞斧: 右键蓄力3秒后飞出
    // ========================================================================

    @EventHandler
    public void onThrowingAxeClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, ThrowingAxeKey) == 0) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // 取消正在进行的蓄力
        if (axeChargeTask.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(axeChargeTask.remove(uuid));
            axeChargeStart.remove(uuid);
            player.sendActionBar("§c飞斧蓄力已取消");
            event.setCancelled(true);
            return;
        }

        // 开始蓄力
        event.setCancelled(true);
        axeChargeStart.put(uuid, System.currentTimeMillis());
        player.sendActionBar("§e飞斧蓄力中... 请保持右键");
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 4, false, false));

        int taskId = new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (!player.isOnline() || player.isDead()) {
                    axeChargeStart.remove(uuid);
                    axeChargeTask.remove(uuid);
                    cancel();
                    return;
                }

                ItemStack currentItem = player.getInventory().getItemInMainHand();
                if (ArmsorEnchant.getEnchantLevel(currentItem, ThrowingAxeKey) == 0) {
                    axeChargeStart.remove(uuid);
                    axeChargeTask.remove(uuid);
                    player.sendActionBar("§c请保持手持飞斧");
                    cancel();
                    return;
                }

                tick++;
                if (tick >= 30) { // 3秒 (30 ticks)
                    throwAxe(player);
                    axeChargeStart.remove(uuid);
                    axeChargeTask.remove(uuid);
                    cancel();
                } else if (tick % 10 == 0) {
                    player.sendActionBar("§e飞斧蓄力 §6" + (tick / 10 + 1) + "§e/§63");
                }
            }
        }.runTaskTimer(getplugin, 0L, 2L).getTaskId();

        axeChargeTask.put(uuid, taskId);
    }

    private void throwAxe(Player player) {
        ItemStack axe = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(axe, ThrowingAxeKey) == 0) return;

        // 创建投掷物 (雪球用于检测路径)
        Location eye = player.getEyeLocation();
        Vector direction = eye.getDirection();
        Snowball projectile = player.launchProjectile(Snowball.class, direction);
        projectile.setMetadata("ThrowingAxe", new FixedMetadataValue(getplugin, true));
        projectile.setVelocity(direction.multiply(2.5));

        player.sendActionBar("§6飞斧已掷出！");
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.2f);

        // 消耗耐久
        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE) {
            Damageable meta = (Damageable) axe.getItemMeta();
            meta.setDamage(meta.getDamage() + 1);
            if (meta.getDamage() >= axe.getType().getMaxDurability()) {
                axe.setAmount(0);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            } else {
                axe.setItemMeta(meta);
            }
        }

        // 追踪投掷物
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                tick++;
                if (!projectile.isValid() || projectile.isDead() || tick > 40) {
                    projectile.remove();
                    cancel();
                    return;
                }

                Location loc = projectile.getLocation();
                loc.getWorld().spawnParticle(Particle.CRIT, loc, 3, 0.2, 0.2, 0.2, 0);
                loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, loc, 1, 0.3, 0, 0.3, 0);

                for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1.5, 1.5, 1.5)) {
                    if (entity instanceof LivingEntity target && target != player) {
                        target.damage(20, player);
                        loc.getWorld().spawnParticle(Particle.EXPLOSION, loc, 1, 0, 0, 0, 0);
                        loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.0f);
                        projectile.remove();
                        cancel();
                        return;
                    }
                }
            }
        }.runTaskTimer(getplugin, 0L, 1L);
    }

    // ========================================================================
    // 骷髅权杖: 降下箭雨
    // ========================================================================

    @EventHandler
    public void onScepterUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, SkeletonScepterKey) == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        int cd = scepterCooldown.getOrDefault(uuid, 0);
        if (cd > 0) {
            player.sendActionBar("§c权杖冷却中... " + cd / 20 + "秒");
            return;
        }

        Location target = player.getTargetBlock(null, 30).getLocation().add(0.5, 1, 0.5);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 0.5f);

        new BukkitRunnable() {
            int wave = 0;

            @Override
            public void run() {
                wave++;
                if (wave > 4) {
                    cancel();
                    return;
                }

                for (int i = 0; i < 8; i++) {
                    double xOffset = (RANDOM.nextDouble() - 0.5) * 8;
                    double zOffset = (RANDOM.nextDouble() - 0.5) * 8;
                    Location arrowLoc = target.clone().add(xOffset, 15, zOffset);

                    // 射出箭矢
                    Arrow arrow = target.getWorld().spawn(arrowLoc, Arrow.class);
                    arrow.setVelocity(new Vector(0, -3, 0));
                    arrow.setDamage(8);
                    arrow.setShooter(player);
                    arrow.setMetadata("ScepterArrow", new FixedMetadataValue(getplugin, true));

                    target.getWorld().spawnParticle(Particle.CRIT, arrowLoc, 5, 0.3, 0.3, 0.3, 0);
                }
                target.getWorld().playSound(target, Sound.ENTITY_ARROW_SHOOT, 0.5f, 0.8f);
            }
        }.runTaskTimer(getplugin, 0L, 8L);

        scepterCooldown.put(uuid, 200); // 10秒冷却
        new BukkitRunnable() {
            int remaining = 200;
            @Override
            public void run() {
                remaining--;
                scepterCooldown.put(uuid, Math.max(0, remaining));
                if (remaining <= 0) cancel();
            }
        }.runTaskTimer(getplugin, 1L, 1L);
    }

    // ========================================================================
    // 寒冰弓: 射出时额外发射2支寒冰箭 + 冰块粒子
    // ========================================================================

    @EventHandler
    public void onFrostBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack bow = event.getBow();
        if (bow == null || ArmsorEnchant.getEnchantLevel(bow, FrostBowKey) == 0) return;

        Location eye = player.getEyeLocation();
        Vector direction = eye.getDirection();
        World world = player.getWorld();

        // 在玩家前方生成两束冰粒子路径
        world.spawnParticle(Particle.SNOWFLAKE, eye.add(direction.clone().multiply(0.5)), 20, 0.3, 0.3, 0.3, 0.05);

        // 额外发射2支寒冰箭 (延迟几tick发射避免碰撞)
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) return;
                for (int i = -1; i <= 1; i += 2) {
                    Vector offset = new Vector(-direction.getZ() * i * 0.15, 0, direction.getX() * i * 0.15);
                    Arrow iceArrow = player.launchProjectile(Arrow.class,
                            direction.clone().add(offset).normalize().multiply(3.0));
                    iceArrow.setMetadata("FrostArrow", new FixedMetadataValue(getplugin, true));
                    iceArrow.setDamage(25);
                    iceArrow.setColor(Color.AQUA);

                    world.spawnParticle(Particle.SNOWFLAKE, iceArrow.getLocation(), 5, 0.1, 0.1, 0.1, 0);
                }
            }
        }.runTaskLater(getplugin, 2L);

        // 追踪所有刚射出的寒冰箭的视觉效果
        Arrow mainArrow = (Arrow) event.getProjectile();
        mainArrow.setMetadata("FrostArrow", new FixedMetadataValue(getplugin, true));
        mainArrow.setDamage(25);
        mainArrow.setColor(Color.AQUA);
    }

    @EventHandler
    public void onFrostArrowHit(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Projectile proj && proj.getShooter() instanceof Player) {
            if (proj.hasMetadata("FrostArrow") && event.getEntity() instanceof LivingEntity target) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 2));
                target.setFreezeTicks(60);
                target.getWorld().spawnParticle(Particle.ITEM_SNOWBALL,
                        target.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.3);
                target.getWorld().playSound(target.getLocation(), Sound.BLOCK_GLASS_BREAK, 0.5f, 0.5f);
            }
        }
    }

    // ========================================================================
    // 火焰戟: 攻击额外30火焰伤害 + 投掷时3×3灼烧
    // ========================================================================

    @EventHandler
    public void onFlameHalberdAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, FlameHalberdKey) == 0) return;

        // 额外30点火焰伤害 (如果是三叉戟近战攻击)
        if (event.getEntity() instanceof LivingEntity target) {
            target.setFireTicks(100);
            target.getWorld().spawnParticle(Particle.FLAME,
                    target.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_BLAZE_HURT, 1.0f, 1.0f);
            event.setDamage(event.getDamage() + 30);
        }
    }

    @EventHandler
    public void onFlameHalberdLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, FlameHalberdKey) == 0) return;

        Projectile proj = event.getEntity();
        proj.setMetadata("FlameHalberd", new FixedMetadataValue(getplugin, true));

        // 追踪投掷物，沿途3×3灼烧
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                tick++;
                if (!proj.isValid() || proj.isDead() || tick > 100) {
                    cancel();
                    return;
                }

                Location loc = proj.getLocation();
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 10, 0.3, 0.3, 0.3, 0.02);
                loc.getWorld().spawnParticle(Particle.SMOKE, loc, 5, 0.3, 0.3, 0.3, 0.01);

                for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1.5, 1.5, 1.5)) {
                    if (entity instanceof LivingEntity target && target != player) {
                        target.setFireTicks(60);
                    }
                }
            }
        }.runTaskTimer(getplugin, 0L, 1L);
    }

    @EventHandler
    public void onFlameHalberdHit(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Trident trident && trident.hasMetadata("FlameHalberd")) {
            if (event.getEntity() instanceof LivingEntity target) {
                target.setFireTicks(100);
                target.getWorld().createExplosion(target.getLocation(), 1.5f, false, false);
                target.getWorld().spawnParticle(Particle.LAVA,
                        target.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 1);
            }
        }
    }

    // ========================================================================
    // 疾刺附魔: 右键三叉戟时速度提升 (10%*level)
    // ========================================================================

    @EventHandler
    public void onQuickThrustUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItem();
        if (item == null || item.getType() != TRIDENT) return;

        int level = ArmsorEnchant.getEnchantLevel(item, QuickThrustKey);
        if (level == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        double speedBoost = level * 0.1; // 10% * level
        int duration = 60 + level * 20; // 基础60 ticks + 每级20 ticks

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration,
                (int) (speedBoost / 0.2) - 1, false, false));
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 10, 0.3, 0.1, 0.3, 0.05);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 0.5f, 1.8f);
        player.sendActionBar("§e⚡ 疾刺！速度提升 " + (level * 10) + "%");
    }

    // ========================================================================
    // 食物/药品: 右键使用
    // ========================================================================

    @EventHandler
    public void onRejuvenationUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, RejuvenationPowderKey) == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        int tier = ArmsorEnchant.getEnchantLevel(item, RejuvenationPowderKey);

        // 消耗物品
        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE) {
            item.setAmount(item.getAmount() - 1);
        }

        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0),
                15, 0.5, 0.5, 0.5, 0.1);

        switch (tier) {
            case 4: // 仙品
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    if (effect.getType().getEffectCategory() == PotionEffectType.Category.HARMFUL) {
                        player.removePotionEffect(effect.getType());
                    }
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2400, 9)); // X = 9
                player.sendMessage("§6✨ 仙品回春散！生命恢复 X 120秒！");
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
                break;
            case 3: // 极品
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    if (effect.getType().getEffectCategory() == PotionEffectType.Category.HARMFUL) {
                        player.removePotionEffect(effect.getType());
                    }
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 800, 9));
                player.sendMessage("§e✨ 极品回春散！生命恢复 X 40秒！");
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.8f);
                break;
            case 2: // 上品
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 4));
                player.sendMessage("§a上品回春散！生命恢复 V 6秒！");
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 1.2f);
                break;
            default: // 普通
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 4));
                player.sendMessage("§7回春散 生命恢复 V 3秒");
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 0.8f, 1.0f);
                break;
        }
    }

    @EventHandler
    public void onBandageUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, HemostaticBandageKey) == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE) {
            item.setAmount(item.getAmount() - 1);
        }

        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double newHealth = Math.min(maxHealth, player.getHealth() + 6);
        player.setHealth(newHealth);

        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0),
                10, 0.4, 0.4, 0.4, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_BUNDLE_REMOVE_ONE, 0.8f, 1.0f);
        player.sendActionBar("§c❤ 已使用止血绷带");
    }

    // ========================================================================
    // 回春散合成时随机品质
    // ========================================================================

    @EventHandler
    public void onCraftRejuvenation(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result == null) return;
        if (ArmsorEnchant.getEnchantLevel(result, RejuvenationPowderKey) == 0) return;

        double roll = RANDOM.nextDouble();
        int tier;
        if (roll < 0.001)       tier = 3; // 0.1% 仙品
        else if (roll < 0.011)  tier = 2; // 1% 极品
        else if (roll < 0.111)  tier = 1; // 10% 上品
        else                    tier = 0; // 普通

        event.getInventory().setResult(ArmsorItem.RejuvenationPowder(result.getAmount(), tier));
    }

    @EventHandler
    public void onBiscuitUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, CompressedBiscuitKey) == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE) {
            item.setAmount(item.getAmount() - 1);
        }

        // 瞬间恢复3块面包的饱食度
        int foodLevel = Math.min(20, player.getFoodLevel() + 15);
        player.setFoodLevel(foodLevel);
        float saturation = Math.min((float) foodLevel, player.getSaturation() + 18);
        player.setSaturation(saturation);

        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation().add(0, 1, 0),
                5, 0.3, 0.3, 0.3, 0.05);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 0.8f, 1.5f);
        player.sendActionBar("§6压缩饼干 饱食度已恢复");
    }
}
