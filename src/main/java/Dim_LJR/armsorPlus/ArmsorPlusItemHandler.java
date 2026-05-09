package Dim_LJR.armsorPlus;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static Dim_LJR.armsorPlus.Food.FoodItems.*;
import static org.bukkit.Material.*;

/**
 * 处理所有自定义武器/物品的特殊效果。
 */
public class ArmsorPlusItemHandler implements Listener {

    private static final Map<UUID, Long> axeChargeStart = new HashMap<>();
    private static final Map<UUID, Integer> axeChargeTask = new HashMap<>();
    private static final Map<UUID, Integer> scepterCooldown = new HashMap<>();
    private static final Map<UUID, Integer> rainSwordCooldown = new HashMap<>();
    private static final Map<UUID, Integer> flashStepBladeCooldown = new HashMap<>();
    private static final Map<UUID, Boolean> flyingSwordActive = new HashMap<>();
    private static final Map<UUID, Integer> magicStickCooldown = new HashMap<>();
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
                    double xOffset = (RANDOM.nextDouble() - 0.5) * 3;
                    double zOffset = (RANDOM.nextDouble() - 0.5) * 3;
                    Location arrowLoc = target.clone().add(xOffset, 15, zOffset);

                    // 射出箭矢
                    Arrow arrow = target.getWorld().spawn(arrowLoc, Arrow.class);
                    arrow.setVelocity(new Vector(0, -3, 0));
                    arrow.setDamage(12);
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
    // 法杖: 左键发射魔法球 (SmallFireball) 直射攻击
    // ========================================================================

    @EventHandler
    public void onMagicStickLeftClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, MagicStickKey) == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        int cd = magicStickCooldown.getOrDefault(uuid, 0);
        //if (cd > 0) {
        //    return; // 冷却中,静默阻止
        //}

        // 发射魔法球 (SmallFireball直射)
        SmallFireball fireball = player.launchProjectile(SmallFireball.class);
        fireball.setVelocity(player.getEyeLocation().getDirection().multiply(2.0));
        fireball.setMetadata("MagicStick", new FixedMetadataValue(getplugin, true));

        // 飞行粒子追踪
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                tick++;
                if (!fireball.isValid() || fireball.isDead() || tick > 80) {
                    if (fireball.isValid()) fireball.remove();
                    cancel();
                    return;
                }
                Location loc = fireball.getLocation();
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 3, 0.15, 0.15, 0.15, 0.02);
                loc.getWorld().spawnParticle(Particle.SMOKE, loc, 1, 0.1, 0.1, 0.1, 0.01);
            }
        }.runTaskTimer(getplugin, 0L, 1L);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.5f, 1.5f);

        // 0.5秒冷却 (10 ticks)
        magicStickCooldown.put(uuid, 10);
        new BukkitRunnable() {
            int remaining = 10;
            @Override
            public void run() {
                remaining--;
                magicStickCooldown.put(uuid, Math.max(0, remaining));
                if (remaining <= 0) cancel();
            }
        }.runTaskTimer(getplugin, 1L, 1L);
    }

    @EventHandler
    public void onMagicStickHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof SmallFireball fireball)) return;
        if (!fireball.hasMetadata("MagicStick")) return;

        if (event.getEntity() instanceof LivingEntity target) {
            event.setDamage(20);
            target.getWorld().spawnParticle(Particle.EXPLOSION, target.getLocation().add(0, 1, 0),
                    2, 0.3, 0.3, 0.3, 0);
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 1.0f);
            target.setFireTicks(60);
        }
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
        if (item == null) return;
        if (item.getType() != TRIDENT && ArmsorEnchant.getEnchantLevel(item, FlameHalberdKey) == 0) return;

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

        event.getInventory().setResult(RejuvenationPowder(result.getAmount(), tier));
    }

    // ========================================================================
    // 雨御前: 右键3秒隐身+无敌+周围生物缓慢255/挖掘疲劳3秒 (冷却15s)
    // ========================================================================

    @EventHandler
    public void onRainSwordUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, RainSwordKey) == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        int cd = rainSwordCooldown.getOrDefault(uuid, 0);
        if (cd > 0) {
            player.sendActionBar("§c雨御前冷却中... " + cd / 20 + "秒");
            return;
        }

        // 3秒隐身+无敌
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0, false, false));
        player.setInvulnerable(true);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.05);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.0f, 1.5f);

        // 给周围所有生物施加缓慢255 + 挖掘疲劳 3秒
        for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
            if (entity instanceof LivingEntity living && living != player) {
                living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 254, false, false));
                living.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 60, 2, false, false));
            }
        }
        player.getWorld().spawnParticle(Particle.SNOWFLAKE, player.getLocation(), 50, 5, 3, 5, 0.1);
        player.sendActionBar("§b雨御前！3秒隐身 + 冰霜领域");

        // 3秒后取消无敌
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setInvulnerable(false);
                player.sendActionBar("§c隐身效果已结束");
            }
        }.runTaskLater(getplugin, 60L);

        rainSwordCooldown.put(uuid, 300); // 15秒冷却
        new BukkitRunnable() {
            int remaining = 300;
            @Override
            public void run() {
                remaining--;
                rainSwordCooldown.put(uuid, Math.max(0, remaining));
                if (remaining <= 0) cancel();
            }
        }.runTaskTimer(getplugin, 1L, 1L);
    }

    // ========================================================================
    // 飞天御剑: 右键起飞/收起，按W沿视角方向飞行
    // ========================================================================

    @EventHandler
    public void onFlyingSwordUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, FlyingSwordKey) == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Boolean active = flyingSwordActive.getOrDefault(uuid, false);
        if (active) {
            // 停止飞行
            flyingSwordActive.put(uuid, false);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.setFlySpeed(0.1f);
            player.sendActionBar("§6飞天御剑 已收起");
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.8f, 1.0f);
        } else {
            // 开启飞行 (按W沿视角方向前进，松开即停)
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setFlySpeed(0.4f); // 8m/s, W按下时沿视角方向移动
            flyingSwordActive.put(uuid, true);
            player.sendActionBar("§6飞天御剑 已起航！");
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5f, 2.0f);

            // 脚下生成飞剑显示 + 检测是否仍手持飞天御剑
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!flyingSwordActive.getOrDefault(uuid, false) || !player.isOnline()) {
                        if (player.isOnline()) {
                            player.setFlying(false);
                            player.setAllowFlight(false);
                            player.setFlySpeed(0.1f);
                        }
                        cancel();
                        return;
                    }
                    // 切物品自动取消飞行
                    ItemStack currentItem = player.getInventory().getItemInMainHand();
                    if (ArmsorEnchant.getEnchantLevel(currentItem, FlyingSwordKey) == 0) {
                        flyingSwordActive.put(uuid, false);
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        player.setFlySpeed(0.1f);
                        player.sendActionBar("§c已收起飞天御剑");
                        cancel();
                        return;
                    }
                    if (!player.isFlying()) {
                        player.setFlying(true);
                    }

                    Location foot = player.getLocation().subtract(0, 0.5, 0);
                    player.getWorld().spawnParticle(Particle.END_ROD, foot, 1, 0, 0, 0, 0);
                    player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, foot, 1, 0.5, 0, 0.5, 0);
                }
            }.runTaskTimer(getplugin, 0L, 2L);
        }
    }

    // ========================================================================
    // 瞬步刃: 右键向前瞬移，指向目标则瞬移到身后并造成伤害
    // ========================================================================

    @EventHandler
    public void onFlashStepBladeUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        // 仅右键触发
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, FlashStepBladeKey) == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        int cd = flashStepBladeCooldown.getOrDefault(uuid, 0);
        if (cd > 0) {
            player.sendActionBar("§c瞬步刃冷却中... " + cd / 20 + "秒");
            return;
        }

        // 检查是否指向目标
        LivingEntity target = null;
        for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
            if (entity instanceof LivingEntity living && living != player
                    && living.hasLineOfSight(player)) {
                Location eyeLoc = player.getEyeLocation();
                Vector dir = eyeLoc.getDirection();
                Vector toTarget = living.getLocation().add(0, 1, 0).subtract(eyeLoc).toVector();
                double angle = dir.angle(toTarget);
                if (angle < 0.3) { // ~17度锥形范围
                    target = living;
                    break;
                }
            }
        }

        if (target != null) {
            // 瞬移到目标身后并造成伤害
            Vector behind = target.getLocation().getDirection().normalize().multiply(-2);
            Location behindTarget = target.getLocation().add(behind).add(0, 0.5, 0);
            Location safeLoc = findSafeTeleportLocation(behindTarget);
            if (safeLoc != null) {
                player.teleport(safeLoc);
            }
            target.damage(15, player);
            target.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, target.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0);
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.2f);
            player.sendActionBar("§5瞬步！已闪至目标身后");
        } else {
            // 纯粹向前瞬移
            Vector direction = player.getEyeLocation().getDirection().normalize().multiply(5);
            Location teleportTo = player.getLocation().add(direction);
            Location safeLoc = findSafeTeleportLocation(teleportTo);
            if (safeLoc != null) {
                player.teleport(safeLoc);
            }
            player.sendActionBar("§5瞬步！");
        }

        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.5f);

        flashStepBladeCooldown.put(uuid, 40); // 2秒冷却
        new BukkitRunnable() {
            int remaining = 40;
            @Override
            public void run() {
                remaining--;
                flashStepBladeCooldown.put(uuid, Math.max(0, remaining));
                if (remaining <= 0) cancel();
            }
        }.runTaskTimer(getplugin, 1L, 1L);
    }

    /**
     * 更新武器Lore中的剩余次数显示
     */
    private static void updateUsesLore(ItemStack item, int uses) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return;
        java.util.List<String> lore = meta.getLore();
        for (int i = 0; i < lore.size(); i++) {
            if (ChatColor.stripColor(lore.get(i)).contains("剩余次数")) {
                lore.set(i, ChatColor.YELLOW + "剩余次数: " + uses);
                break;
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    /**
     * 寻找安全传送位置: 向上搜索5格寻找双脚和头部均可通行的位置
     */
    private static Location findSafeTeleportLocation(Location base) {
        for (int yOffset = 0; yOffset <= 5; yOffset++) {
            Location check = base.clone().add(0, yOffset, 0);
            if (check.getBlock().isPassable() && check.clone().add(0, 1, 0).getBlock().isPassable()) {
                return check;
            }
        }
        return null;
    }

    // ========================================================================
    // 寒冰剑: 攻击时对敌方造成缓慢II 3秒
    // ========================================================================

    @EventHandler
    public void onIceSwordAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, IceSwordKey) == 0) return;

        if (event.getEntity() instanceof LivingEntity target) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1, false, false));
            target.getWorld().spawnParticle(Particle.SNOWFLAKE,
                    target.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
        }
    }

    // ========================================================================
    // 盘丝弓: 攻击时在敌方周围生成蜘蛛网30秒, PDC计数9次后损坏
    // ========================================================================

    @EventHandler
    public void onWebBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack bow = event.getBow();
        if (bow == null || ArmsorEnchant.getEnchantLevel(bow, WebBowKey) == 0) return;

        // PDC使用次数管理
        int uses = ArmsorEnchant.getEnchantLevel(bow, WebBowUsesKey);
        if (uses == 0) uses = 9;
        uses--;
        final int finalUses = uses;
        bow.editMeta(meta -> meta.getPersistentDataContainer()
                .set(WebBowUsesKey, PersistentDataType.INTEGER, finalUses));
        updateUsesLore(bow, finalUses);
        if (finalUses <= 0) {
            bow.setAmount(0);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onWebBowHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player player)) return;
        if (ArmsorEnchant.getEnchantLevel(player.getInventory().getItemInMainHand(), WebBowKey) == 0) return;

        Location loc = event.getEntity().getLocation();
        World world = loc.getWorld();
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                Location webLoc = loc.clone().add(x, 0, z);
                if (webLoc.getBlock().getType() == Material.AIR) {
                    webLoc.getBlock().setType(Material.COBWEB);
                    Bukkit.getScheduler().runTaskLater(getplugin, () -> {
                        if (webLoc.getBlock().getType() == Material.COBWEB) {
                            webLoc.getBlock().setType(Material.AIR);
                        }
                    }, 600L); // 30秒后恢复
                }
            }
        }
        world.spawnParticle(Particle.ITEM_COBWEB, loc.add(0, 1, 0), 20, 1, 1, 1, 0.1);
        world.playSound(loc, Sound.BLOCK_SLIME_BLOCK_PLACE, 0.5f, 0.8f);
    }

    // ========================================================================
    // 爆炸弓: 攻击时在敌方周围爆炸, PDC计数9次后损坏
    // ========================================================================

    @EventHandler
    public void onExplosionBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack bow = event.getBow();
        if (bow == null || ArmsorEnchant.getEnchantLevel(bow, ExplosionBowKey) == 0) return;

        // PDC使用次数管理
        int uses = ArmsorEnchant.getEnchantLevel(bow, ExplosionBowUsesKey);
        if (uses == 0) uses = 9;
        uses--;
        final int finalUses = uses;
        bow.editMeta(meta -> meta.getPersistentDataContainer()
                .set(ExplosionBowUsesKey, PersistentDataType.INTEGER, finalUses));
        updateUsesLore(bow, finalUses);
        if (finalUses <= 0) {
            bow.setAmount(0);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onExplosionBowHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player player)) return;
        if (ArmsorEnchant.getEnchantLevel(player.getInventory().getItemInMainHand(), ExplosionBowKey) == 0) return;

        Location loc = event.getEntity().getLocation();
        loc.getWorld().createExplosion(loc, 2.0f, false, false);
        loc.getWorld().spawnParticle(Particle.EXPLOSION, loc.add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0);
    }
}
