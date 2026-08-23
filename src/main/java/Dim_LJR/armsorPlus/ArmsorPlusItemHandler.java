package Dim_LJR.armsorPlus;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorPlusEnchantEventHandler;
import org.bukkit.*;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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

import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static Dim_LJR.armsorPlus.Food.FoodItems.*;
import static org.bukkit.Material.*;

// 处理所有自定义武器/物品的特殊效果。
public class ArmsorPlusItemHandler implements Listener {

    private static final Map<UUID, Long> axeChargeStart = new HashMap<>();
    private static final Map<UUID, Integer> axeChargeTask = new HashMap<>();
    private static final Map<UUID, Integer> scepterCooldown = new HashMap<>();
    private static final Map<UUID, Integer> rainSwordCooldown = new HashMap<>();
    private static final Map<UUID, Integer> flashStepBladeCooldown = new HashMap<>();
    private static final Map<UUID, Integer> cloudMoonBladeCooldown = new HashMap<>();
    private static final Map<UUID, Boolean> flyingSwordActive = new HashMap<>();
    private static final Random RANDOM = new Random();

    // 玩家登出时清理冷却/状态, 防止 Map 泄漏
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Integer task = axeChargeTask.remove(uuid);
        if (task != null) Bukkit.getScheduler().cancelTask(task); // 取消飞斧蓄力任务
        axeChargeStart.remove(uuid);
        scepterCooldown.remove(uuid);
        rainSwordCooldown.remove(uuid);
        flashStepBladeCooldown.remove(uuid);
        cloudMoonBladeCooldown.remove(uuid);
        flyingSwordActive.remove(uuid);
    }

    // ========================================================================
    // 匕首: 保证额外5点伤害 (已通过属性修饰符实现，此处处理真实伤害)
    // ========================================================================

    @EventHandler
    public void onDaggerAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (!ArmsorPlusEnchantEventHandler.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, DaggerKey) == 0) return;

        // 匕首固定额外5点"真实"伤害 (计入玩家击杀, 前后检查目标是否死亡)
        if (event.getEntity() instanceof LivingEntity target) {
            if (target.isDead()) return;
            target.damage(5, player);
            if (target.isDead()) return;
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
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
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

        // 消耗耐久 (meta 可能为 null, 加保护)
        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE) {
            ItemMeta axeMeta = axe.getItemMeta();
            if (axeMeta instanceof Damageable meta) {
                meta.setDamage(meta.getDamage() + 1);
                if (meta.getDamage() >= axe.getType().getMaxDurability()) {
                    axe.setAmount(0);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                } else {
                    axe.setItemMeta(meta);
                }
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
                    if (entity instanceof LivingEntity target && target != player && !target.isDead()) {
                        target.damage(20, player);
                        if (target.isDead()) { // 目标已被飞斧击杀, 直接结束
                            projectile.remove();
                            cancel();
                            return;
                        }
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
            player.sendActionBar("§c权杖冷却中... " + (cd + 19) / 20 + "秒"); // 向上取整, 避免显示0秒
            return;
        }

        // 瞄准天空/虚空时 getTargetBlock 可能返回 null, 回退为面前方向10格
        Block targetBlock = player.getTargetBlock(null, 30);
        Location target = (targetBlock != null ? targetBlock.getLocation()
                : player.getLocation().add(player.getEyeLocation().getDirection().multiply(10)))
                .add(0.5, 1, 0.5);
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
                if (remaining <= 0) {
                    scepterCooldown.remove(uuid); // 冷却结束移除条目, 避免 Map 泄漏
                    cancel();
                } else {
                    scepterCooldown.put(uuid, remaining);
                }
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
        // 伤害溯源: 只有真实近战攻击才附加火焰伤害, 荆棘(THORNS)反弹伤害的 damager 是持戟玩家,
        // 但并非玩家主动挥戟, 必须排除, 否则荆棘会造成额外火焰伤害
        if (!ArmsorPlusEnchantEventHandler.isDirectMeleeAttack(event)) return;
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
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null) return;
        // 疾刺对三叉戟/长矛生效 (1.21+ 新增 SPEAR 类型)
        Material type = item.getType();
        if (type != TRIDENT && !type.name().endsWith("_SPEAR")) return;

        int level = ArmsorEnchant.getEnchantLevel(item, QuickThrustKey);
        if (level == 0) return;

        // 不取消事件: 保留原版右键功能 (三叉戟/火焰戟仍可投掷), 疾刺只附加速度提升
        Player player = event.getPlayer();
        int duration = 60 + level * 20; // 基础60 ticks + 每级20 ticks

        // 疾刺: 移动速度提升 level*10% (ADD_SCALAR 真实百分比修饰符)
        org.bukkit.attribute.AttributeInstance speedAttr = player.getAttribute(org.bukkit.attribute.Attribute.MOVEMENT_SPEED);
        if (speedAttr != null) {
            org.bukkit.attribute.AttributeModifier speedMod = new org.bukkit.attribute.AttributeModifier(
                    new NamespacedKey(getplugin, "ArmsorPlus_QuickThrustSpeed"),
                    0.1 * level, org.bukkit.attribute.AttributeModifier.Operation.ADD_SCALAR,
                    org.bukkit.inventory.EquipmentSlotGroup.HAND);
            speedAttr.addTransientModifier(speedMod);
            Bukkit.getScheduler().runTaskLater(getplugin, () -> speedAttr.removeModifier(speedMod), duration);
        }

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
    // 雨御前: 右键3秒隐身+无敌+周围生物缓慢255/挖掘疲劳3秒 (冷却10s)
    // ========================================================================

    @EventHandler
    public void onRainSwordUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, RainSwordKey) == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        int cd = rainSwordCooldown.getOrDefault(uuid, 0);
        if (cd > 0) {
            player.sendActionBar("§c雨御前冷却中... " + (cd + 19) / 20 + "秒");
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

        rainSwordCooldown.put(uuid, 200); // 10秒冷却
        new BukkitRunnable() {
            int remaining = 200;
            @Override
            public void run() {
                remaining--;
                if (remaining <= 0) {
                    rainSwordCooldown.remove(uuid); // 冷却结束移除条目, 避免 Map 泄漏
                    cancel();
                } else {
                    rainSwordCooldown.put(uuid, remaining);
                }
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
            flyingSwordActive.remove(uuid); // 移除状态条目, 避免 Map 泄漏
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
                    if (!flyingSwordActive.getOrDefault(uuid, false) || !player.isOnline() || player.isDead()) {
                        if (player.isOnline() && !player.isDead()) {
                            player.setFlying(false);
                            player.setAllowFlight(false);
                            player.setFlySpeed(0.1f);
                        }
                        flyingSwordActive.remove(uuid);
                        cancel();
                        return;
                    }
                    // 切物品自动取消飞行
                    ItemStack currentItem = player.getInventory().getItemInMainHand();
                    if (ArmsorEnchant.getEnchantLevel(currentItem, FlyingSwordKey) == 0) {
                        flyingSwordActive.remove(uuid); // 移除状态条目
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
            player.sendActionBar("§c瞬步刃冷却中... " + (cd + 19) / 20 + "秒");
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
            if (target.isDead()) return; // 目标已死亡则跳过
            target.damage(15, player);
            if (target.isDead()) return; // 目标被击杀则停止后续特效
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
                if (remaining <= 0) {
                    flashStepBladeCooldown.remove(uuid); // 冷却结束移除条目, 避免 Map 泄漏
                    cancel();
                } else {
                    flashStepBladeCooldown.put(uuid, remaining);
                }
            }
        }.runTaskTimer(getplugin, 1L, 1L);
    }

    // ========================================================================
    // 吞云斩月刀: 右键向前突刺(最远3格), 指向生物则突刺至其面前并造成[基础9+锋利x2]伤害
    // 突刺伤害走真实近战事件(target.damage), 经 MergedDamageHandler 自动触发血祭/双重打击等
    // ========================================================================

    @EventHandler
    public void onCloudMoonBladeUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, CloudMoonBladeKey) == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (cloudMoonBladeCooldown.getOrDefault(uuid, 0) > 0) return; // 0.2秒冷却太短, 不刷提示

        // 检测前方生物 (最远3格)
        LivingEntity target = null;
        double targetDist = 0;
        for (Entity entity : player.getNearbyEntities(3, 3, 3)) {
            if (entity instanceof LivingEntity living && living != player && living.hasLineOfSight(player)) {
                Location eyeLoc = player.getEyeLocation();
                Vector dir = eyeLoc.getDirection();
                Vector toTarget = living.getLocation().add(0, 1, 0).subtract(eyeLoc).toVector();
                double dist = toTarget.length();
                if (dist > 0.5 && dist <= 3.0 && dir.angle(toTarget) < 0.35) { // ~20度锥形
                    target = living;
                    targetDist = dist;
                    break;
                }
            }
        }

        // 计算突刺终点
        Location start = player.getLocation().clone();
        Location end = start.clone();
        if (target != null) {
            // 指向生物: 突刺至目标前方1格 (目标过近则原地突刺)
            if (targetDist >= 1.0) {
                Vector forward = player.getEyeLocation().getDirection().setY(0).normalize();
                end = target.getLocation().clone().subtract(forward.multiply(targetDist - 1.0));
            }
        } else {
            // 未指向生物: 向前突刺最远3格
            Vector forward = player.getEyeLocation().getDirection();
            forward.setY(0).normalize().multiply(3);
            end = start.clone().add(forward);
        }
        Location safeEnd = findSafeTeleportLocation(end);
        if (safeEnd != null) end = safeEnd;

        final int sharp = item.getEnchantmentLevel(Enchantment.SHARPNESS);
        final double dashDamage = 9.0 + sharp * 2.0;
        final LivingEntity finalTarget = (target != null && !target.isDead()) ? target : null;

        // 长矛突刺动画: 手臂挥击 + 4 tick 分步前冲 + 云迹粒子
        player.swingMainHand();
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 0.9f);

        final Vector stepVec = end.clone().subtract(start).toVector().multiply(1.0 / 4);
        new BukkitRunnable() {
            int tick = 0;
            Location lastLoc = start;
            @Override
            public void run() {
                tick++;
                Location loc = start.clone().add(stepVec.clone().multiply(tick));
                // 中途撞墙则停在上一格并提前结算
                if (tick < 4 && (!loc.getBlock().isPassable() || !loc.clone().add(0, 1, 0).getBlock().isPassable())) {
                    player.teleport(lastLoc);
                    finish();
                    cancel();
                    return;
                }
                lastLoc = loc;
                player.teleport(loc);
                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 4, 0.25, 0.25, 0.25, 0.02);
                if (tick >= 4) {
                    finish();
                    cancel();
                }
            }
            // 突刺到位后结算伤害 (真实近战事件, 经 MergedDamageHandler 触发血祭/双重打击)
            private void finish() {
                if (finalTarget != null && !finalTarget.isDead()) {
                    finalTarget.damage(dashDamage, player);
                    if (!finalTarget.isDead()) {
                        finalTarget.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR,
                                finalTarget.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0);
                    }
                }
                player.sendActionBar("§f吞云斩月 · 突刺!");
            }
        }.runTaskTimer(getplugin, 1L, 1L);

        cloudMoonBladeCooldown.put(uuid, 4); // 0.2秒冷却
        new BukkitRunnable() {
            int remaining = 4;
            @Override
            public void run() {
                remaining--;
                if (remaining <= 0) {
                    cloudMoonBladeCooldown.remove(uuid); // 冷却结束移除条目, 避免 Map 泄漏
                    cancel();
                } else {
                    cloudMoonBladeCooldown.put(uuid, remaining);
                }
            }
        }.runTaskTimer(getplugin, 1L, 1L);
    }

    // 更新武器Lore中的剩余次数显示
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

    // 寻找安全传送位置: 向上搜索5格寻找双脚和头部均可通行的位置
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
        if (!ArmsorPlusEnchantEventHandler.isDirectMeleeAttack(event)) return;
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
        // 给箭打标记, 命中时不再依赖主手判断
        event.getProjectile().setMetadata("WebBow", new FixedMetadataValue(getplugin, true));

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
        if (!arrow.hasMetadata("WebBow")) return;
        if (!(arrow.getShooter() instanceof Player player)) return;

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
        // 给箭打标记, 命中时不再依赖主手判断
        event.getProjectile().setMetadata("ExplosionBow", new FixedMetadataValue(getplugin, true));

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
        if (!arrow.hasMetadata("ExplosionBow")) return;
        if (!(arrow.getShooter() instanceof Player player)) return;

        Location loc = event.getEntity().getLocation();
        loc.getWorld().createExplosion(loc, 2.0f, false, false);
        loc.getWorld().spawnParticle(Particle.EXPLOSION, loc.add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0);
    }

    // ========================================================================
    // 鱼骨系列武器特效
    // ========================================================================

    // 检查玩家是否露天（用于雨天判定）
    private boolean isExposedToRain(Player player) {
        if (!player.getWorld().hasStorm()) return false;
        Location loc = player.getLocation();
        return loc.getWorld().getHighestBlockYAt(loc) <= loc.getBlockY();
    }

    // 检查玩家是否处于雨天环境
    private boolean isInRain(Player player) {
        return player.getWorld().hasStorm() && isExposedToRain(player);
    }

    // ---- 海骨剑: 水中+10%伤害 +10%移速 ----

    @EventHandler
    public void onSeaBoneSwordAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!ArmsorPlusEnchantEventHandler.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SeaBoneSwordKey) == 0) return;
        if (!player.isInWater() && !isInRain(player)) return;

        event.setDamage(event.getDamage() * 1.10);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0, false, false));
    }

    // ---- 海骨刀: 水中/雨天+16%伤害 ----

    @EventHandler
    public void onSeaBoneKnifeAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!ArmsorPlusEnchantEventHandler.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SeaBoneKnifeKey) == 0) return;
        if (!player.isInWater() && !isInRain(player)) return;

        event.setDamage(event.getDamage() * 1.16);
    }

    // ---- 灵骨剑: 光灵3s + 水中/雨天15%穿透6点 ----

    @EventHandler
    public void onSpiritBoneSwordAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (!ArmsorPlusEnchantEventHandler.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SpiritBoneSwordKey) == 0) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 60, 0, false, false));
        if ((player.isInWater() || isInRain(player)) && RANDOM.nextDouble() < 0.15) {
            if (target.isDead()) return; // 前: 目标已死亡则跳过
            target.damage(6.0, player);
            if (target.isDead()) return; // 后: 目标被击杀则停止特效
            target.getWorld().spawnParticle(Particle.SOUL,
                    target.getLocation().add(0, 1, 0), 15, 0.3, 0.3, 0.3, 0.05);
        }
    }

    // ---- 灵骨刀: 光灵3s + 水中/雨天15%双倍伤害 ----

    @EventHandler
    public void onSpiritBoneKnifeAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!ArmsorPlusEnchantEventHandler.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SpiritBoneKnifeKey) == 0) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 60, 0, false, false));
        if ((player.isInWater() || isInRain(player)) && RANDOM.nextDouble() < 0.15) {
            event.setDamage(event.getDamage() * 2.0);
            target.getWorld().spawnParticle(Particle.SOUL,
                    target.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.05);
        }
    }

    // ---- 海刺剑: 水中/雨天+40%伤害 ----

    @EventHandler
    public void onSeaSpineSwordAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!ArmsorPlusEnchantEventHandler.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SeaSpineSwordKey) == 0) return;
        if (!player.isInWater() && !isInRain(player)) return;

        event.setDamage(event.getDamage() * 1.40);
    }

    // ---- 海刺刀: 水中/雨天-40%受伤 ----

    @EventHandler
    public void onSeaSpineKnifeDefend(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SeaSpineKnifeKey) == 0
                && ArmsorEnchant.getEnchantLevel(player.getInventory().getItemInOffHand(), SeaSpineKnifeKey) == 0) return;
        if (!player.isInWater() && !isInRain(player)) return;

        event.setDamage(event.getDamage() * 0.60);
    }

    // ---- 海刺剑/海刺刀 水下呼吸被动 ----

    @EventHandler
    public void onSeaSpineWaterBreathing(org.bukkit.event.player.PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        boolean hasSeaSpine = ArmsorEnchant.getEnchantLevel(main, SeaSpineSwordKey) > 0
                || ArmsorEnchant.getEnchantLevel(main, SeaSpineKnifeKey) > 0
                || ArmsorEnchant.getEnchantLevel(off, SeaSpineSwordKey) > 0
                || ArmsorEnchant.getEnchantLevel(off, SeaSpineKnifeKey) > 0;
        if (!hasSeaSpine) return;
        if (!player.isInWater()) return;

        PotionEffect existing = player.getPotionEffect(PotionEffectType.WATER_BREATHING);
        if (existing == null || existing.getDuration() < 100) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 300, 0, false, false));
        }
    }

    // ---- 灵刺剑: 必穿透1点 + 水中/雨天额外穿透2点 ----

    @EventHandler
    public void onSpiritSpineSwordAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (!ArmsorPlusEnchantEventHandler.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SpiritSpineSwordKey) == 0) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        if (target.isDead()) return; // 前: 目标已死亡则跳过
        target.damage(1.0, player);
        if (target.isDead()) return; // 后: 目标被第一段伤害击杀则停止
        if (player.isInWater() || isInRain(player)) {
            target.damage(2.0, player);
            if (target.isDead()) return; // 后: 目标被第二段伤害击杀则停止
        }
        target.getWorld().spawnParticle(Particle.CRIT,
                target.getLocation().add(0, 1, 0), 5, 0.2, 0.2, 0.2, 0);
    }

    // ---- 灵刺刀: 水中/雨天伤害+5 ----

    @EventHandler
    public void onSpiritSpineKnifeAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!ArmsorPlusEnchantEventHandler.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SpiritSpineKnifeKey) == 0) return;
        if (!player.isInWater() && !isInRain(player)) return;

        event.setDamage(event.getDamage() + 5.0);
    }

    // ---- 海哭剑/海哭刀 被动药水效果 ----

    @EventHandler
    public void onSeaCryPassive(org.bukkit.event.player.PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        boolean hasSeaCry = ArmsorEnchant.getEnchantLevel(main, SeaCrySwordKey) > 0
                || ArmsorEnchant.getEnchantLevel(main, SeaCryKnifeKey) > 0
                || ArmsorEnchant.getEnchantLevel(off, SeaCrySwordKey) > 0
                || ArmsorEnchant.getEnchantLevel(off, SeaCryKnifeKey) > 0;
        if (!hasSeaCry) return;

        int duration = 300;
        PotionEffect waterBreathing = player.getPotionEffect(PotionEffectType.WATER_BREATHING);
        if (waterBreathing == null || waterBreathing.getDuration() < 100) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, duration, 0, false, false));
        }
        PotionEffect dolphinsGrace = player.getPotionEffect(PotionEffectType.DOLPHINS_GRACE);
        if (dolphinsGrace == null || dolphinsGrace.getDuration() < 100) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, duration, 0, false, false));
        }
        PotionEffect conduitPower = player.getPotionEffect(PotionEffectType.CONDUIT_POWER);
        if (conduitPower == null || conduitPower.getDuration() < 100) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, duration, 0, false, false));
        }
    }

    // ---- 海哭剑: 发光+15%挖掘疲劳+伤害20%(水中40%) ----

    @EventHandler
    public void onSeaCrySwordAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!ArmsorPlusEnchantEventHandler.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SeaCrySwordKey) == 0) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0, false, false));
        if (RANDOM.nextDouble() < 0.15) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 600, 2, false, false));
        }
        if (player.isInWater() || isInRain(player)) {
            event.setDamage(event.getDamage() * 1.40);
        } else {
            event.setDamage(event.getDamage() * 1.20);
        }
    }

    // ---- 海哭刀: 发光+15%挖掘疲劳+伤害45%(水中/雨天90%) ----

    @EventHandler
    public void onSeaCryKnifeAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!ArmsorPlusEnchantEventHandler.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SeaCryKnifeKey) == 0) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0, false, false));
        if (RANDOM.nextDouble() < 0.15) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 600, 2, false, false));
        }
        if (player.isInWater() || isInRain(player)) {
            event.setDamage(event.getDamage() * 1.90);
        } else {
            event.setDamage(event.getDamage() * 1.45);
        }
    }

    // ---- 鱼骨武器升级配方: 附魔后仍可通过PDC识别原料 ----

    // 当 ExactChoice 无法匹配附魔后的武器时，手动检测合成矩阵并设置结果。
    @EventHandler
    public void onFishBoneUpgradeCraft(PrepareItemCraftEvent event) {
        ItemStack[] m = event.getInventory().getMatrix();
        if (m.length < 9) return;

        // 如果 ExactChoice 已匹配(干净武器)，无需处理
        if (event.getInventory().getResult() != null) return;

        ItemStack center = m[4];
        if (center == null || center.getType() != Material.IRON_SWORD) return;

        ItemStack result = tryBuildUpgrade(m, center);
        if (result != null) {
            event.getInventory().setResult(result);
        }
    }

    // 尝试匹配鱼骨升级配方，返回结果物品或 null
    private ItemStack tryBuildUpgrade(ItemStack[] m, ItemStack center) {
        // 鱼刺剑: 4骨块(角)+4海晶沙粒(边)+鱼骨剑(中心)
        if (hasKey(center, FishBoneSwordKey)
                && isCorners(m, BONE_BLOCK) && isEdges(m, PRISMARINE_SHARD))
            return FishSpineSword(1);
        // 鱼刺刀
        if (hasKey(center, FishBoneKnifeKey)
                && isCorners(m, BONE_BLOCK) && isEdges(m, PRISMARINE_SHARD))
            return FishSpineKnife(1);
        // 海骨剑: 4海绵(角)+4海晶灯(边)+鱼刺剑(中心)
        if (hasKey(center, FishSpineSwordKey)
                && isCorners(m, SPONGE) && isEdges(m, SEA_LANTERN))
            return SeaBoneSword(1);
        // 海骨刀
        if (hasKey(center, FishSpineKnifeKey)
                && isCorners(m, SPONGE) && isEdges(m, SEA_LANTERN))
            return SeaBoneKnife(1);
        // 灵骨剑: 8灵魂沙围海骨剑
        if (hasKey(center, SeaBoneSwordKey) && isAllSurrounding(m, SOUL_SAND))
            return SpiritBoneSword(1);
        // 灵骨刀
        if (hasKey(center, SeaBoneKnifeKey) && isAllSurrounding(m, SOUL_SAND))
            return SpiritBoneKnife(1);
        // 海刺剑: 4鳞甲(角)+4海洋之心(边)+灵骨剑(中心)
        if (hasKey(center, SpiritBoneSwordKey)
                && isCorners(m, TURTLE_SCUTE) && isEdges(m, HEART_OF_THE_SEA))
            return SeaSpineSword(1);
        // 海刺刀
        if (hasKey(center, SpiritBoneKnifeKey)
                && isCorners(m, TURTLE_SCUTE) && isEdges(m, HEART_OF_THE_SEA))
            return SeaSpineKnife(1);
        // 蚀骨剑: 凋零骷髅头(B)+7灵魂土
        if (hasKey(center, SpiritBoneSwordKey)
                && m[1] != null && m[1].getType() == WITHER_SKELETON_SKULL
                && isAllExcept(m, 1, SOUL_SOIL))
            return CorrodeBoneSword(1);
        // 灵刺剑: 4潮涌核心(角)+4恶魂之泪(边)+海刺剑(中心)
        if (hasKey(center, SeaSpineSwordKey)
                && isCorners(m, CONDUIT) && isEdges(m, GHAST_TEAR))
            return SpiritSpineSword(1);
        // 灵刺刀: 4潮涌核心(角)+4凋零骷髅头(边)+海刺刀(中心)
        if (hasKey(center, SeaSpineKnifeKey)
                && isCorners(m, CONDUIT) && isEdges(m, WITHER_SKELETON_SKULL))
            return SpiritSpineKnife(1);
        // 海哭剑: 4下界之星(角)+4潮涌核心(边)+灵刺剑(中心)
        if (hasKey(center, SpiritSpineSwordKey)
                && isCorners(m, NETHER_STAR) && isEdges(m, CONDUIT))
            return SeaCrySword(1);
        // 海哭刀
        if (hasKey(center, SpiritSpineKnifeKey)
                && isCorners(m, NETHER_STAR) && isEdges(m, CONDUIT))
            return SeaCryKnife(1);
        return null;
    }

    private boolean hasKey(ItemStack item, NamespacedKey key) {
        return ArmsorEnchant.getEnchantLevel(item, key) > 0;
    }

    // 检查四角(A=0, C=2, G=6, I=8)是否全为指定材质
    private boolean isCorners(ItemStack[] m, Material mat) {
        return isMat(m[0], mat) && isMat(m[2], mat)
                && isMat(m[6], mat) && isMat(m[8], mat);
    }

    // 检查四边(B=1, D=3, F=5, H=7)是否全为指定材质
    private boolean isEdges(ItemStack[] m, Material mat) {
        return isMat(m[1], mat) && isMat(m[3], mat)
                && isMat(m[5], mat) && isMat(m[7], mat);
    }

    // 检查除中心外的8格是否全为指定材质
    private boolean isAllSurrounding(ItemStack[] m, Material mat) {
        for (int i = 0; i < 9; i++) {
            if (i == 4) continue;
            if (!isMat(m[i], mat)) return false;
        }
        return true;
    }

    // 检查除中心和指定位置外的7格是否全为指定材质
    private boolean isAllExcept(ItemStack[] m, int except, Material mat) {
        for (int i = 0; i < 9; i++) {
            if (i == 4 || i == except) continue;
            if (!isMat(m[i], mat)) return false;
        }
        return true;
    }

    private boolean isMat(ItemStack item, Material mat) {
        return item != null && item.getType() == mat;
    }

    // ========================================================================
    // 幻影之刃 —— 幻影分身 / 幻影假身
    // ========================================================================

    // 攻击时35%召唤幻影分身, 额外3点真实伤害
    @EventHandler
    public void onIllusionBladeAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (!ArmsorPlusEnchantEventHandler.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, IllusionBladeKey) == 0) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        if (RANDOM.nextInt(100) < 35) {
            if (target.isDead()) return;
            target.damage(3, player); // 幻影分身真实伤害
            target.getWorld().spawnParticle(Particle.PORTAL,
                    target.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.1);
            player.sendActionBar("§d⚔ 幻影分身！额外 3 点真实伤害");
        }
    }

    // 击杀时25%生成幻影假身吸引附近怪物
    @EventHandler
    public void onIllusionBladeKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) return;
        Entity killer = entity.getKiller();
        if (!(killer instanceof Player player)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, IllusionBladeKey) == 0) return;

        if (RANDOM.nextInt(100) >= 25) return;
        Location loc = entity.getLocation();
        World world = loc.getWorld();
        // 生成幻影假身 (盔甲架标记, 5秒后消失)
        ArmorStand clone = world.spawn(loc, ArmorStand.class, s -> {
            s.setVisible(false);
            s.setGravity(false);
            s.setInvulnerable(true);
            s.setMarker(true);
            s.setMetadata("ArmsorPlus_IllusionClone", new FixedMetadataValue(getplugin, player.getUniqueId().toString()));
        });
        // 附近怪物把假身当目标
        for (Entity e : world.getNearbyEntities(loc, 10, 10, 10)) {
            if (e instanceof Mob mob && !(e instanceof Player)) {
                mob.setTarget(clone);
            }
        }
        world.spawnParticle(Particle.PORTAL, loc.add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.2);
        player.sendActionBar("§d🫥 幻影假身！附近怪物被迷惑了");
        Bukkit.getScheduler().runTaskLater(getplugin, clone::remove, 100L);
    }

    // ========================================================================
    // 幻惑法杖 —— 失明攻击 / 幻术飞弹
    // ========================================================================

    // 近战攻击30%使目标失明
    @EventHandler
    public void onIllusionStaffAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (!ArmsorPlusEnchantEventHandler.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, IllusionStaffKey) == 0) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        if (RANDOM.nextInt(100) < 30) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, false, true));
            target.getWorld().spawnParticle(Particle.WITCH,
                    target.getLocation().add(0, 1, 0), 15, 0.3, 0.3, 0.3, 0.1);
            player.sendActionBar("§b🌀 幻惑！目标陷入失明");
        }
    }

    // 右键发射幻术飞弹
    @EventHandler
    public void onIllusionStaffRightClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, IllusionStaffKey) == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        Snowball missile = player.launchProjectile(Snowball.class);
        missile.setMetadata("ArmsorPlus_IllusionStaff", new FixedMetadataValue(getplugin, true));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.WITCH, player.getLocation().add(0, 1, 0), 10, 0.3, 0.5, 0.3, 0.1);
    }

    // 幻术飞弹命中: 10点魔法伤害 + 反胃 + 失明
    @EventHandler
    public void onIllusionStaffHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball snowball)) return;
        if (!snowball.hasMetadata("ArmsorPlus_IllusionStaff")) return;

        if (event.getHitEntity() instanceof LivingEntity target
                && snowball.getShooter() instanceof Player player) {
            if (target.isDead()) return;
            target.damage(10, player);
            target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 0, false, true));
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0, false, true));
            target.getWorld().spawnParticle(Particle.WITCH,
                    target.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.1);
            player.sendActionBar("§b🌀 幻术飞弹命中！");
        }
        snowball.remove();
    }
}
