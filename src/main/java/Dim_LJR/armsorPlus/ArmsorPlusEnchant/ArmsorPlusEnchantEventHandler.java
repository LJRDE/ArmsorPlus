package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import Dim_LJR.armsorPlus.PlayerSettings;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant.addEnchantLore;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;

// 自定义附魔效果的事件处理 —— 所有附魔的触发逻辑。
// 包括: 闪避、影避、饥荒、涟漪、血祭、涤魂、寒冻、格挡、凋零、
// 幸存、复仇、生命提升、蓄爆、双重打击、吸血、弹道、狙击
public class ArmsorPlusEnchantEventHandler implements Listener {

    // 正在被本插件穿透伤害处理中的实体 (用于防止 MergedDamageHandler 递归重入)
    private static final Set<java.util.UUID> PIERCING_ACTIVE = new HashSet<>();

    // 正在被复仇反弹伤害处理中的实体 (标记复仇伤害来源, 避免触发血祭/吸血等攻击侧附魔)
    private static final Set<java.util.UUID> REVENGE_ACTIVE = new HashSet<>();

    // 正在被卸力拆分结算中的实体 (防止拆分的分段伤害被再次拆分)
    private static final Set<java.util.UUID> DAMAGE_SPLIT_ACTIVE = new HashSet<>();

    // 共享随机数 (避免 percent() 每次 new Random 的 GC 压力)
    private static final Random RANDOM = new Random();

    // 玩家登出时清理冷却/状态, 防止 Map 泄漏
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        java.util.UUID uuid = event.getPlayer().getUniqueId();
        golemCooldowns.remove(uuid);
        heavyArmorTimers.remove(uuid);
        pendingBloodDecayRefresh.remove(uuid);
        DAMAGE_SPLIT_ACTIVE.remove(uuid);
    }

    // 构造"无法抵挡"的穿透伤害源: 无视护甲/保护附魔/抗性/无敌帧 (等价于原版 /kill)
    // 走事件系统, 死亡由正常流程处理, 避免直接 setHealth(0) 造成的假死状态
    private static DamageSource pierceSource(Entity causer) {
        return DamageSource.builder(DamageType.GENERIC_KILL)
                .withDirectEntity(causer)
                .withCausingEntity(causer)
                .build();
    }

    // 伤害溯源: 判断是否为真实近战攻击 (ENTITY_ATTACK/ENTITY_SWEEP_ATTACK)。
    // 荆棘(THORNS)等反弹伤害的 damager 是持武器玩家, 但并非玩家主动挥击,
    // 所有武器命中效果必须先过此关, 避免在反弹伤害上误触发 (血祭/双重打击/火焰戟/匕首等)
    public static boolean isDirectMeleeAttack(EntityDamageByEntityEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        return cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK;
    }

    // ========================================================================
    // 工具方法
    // ========================================================================

    // 百分率随机: 返回 true 的概率为 x%
    public boolean percent(int x) {
        return RANDOM.nextInt(101) <= x;
    }

    // 将整数转为罗马数字 (I~X)
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

    // 获取实体的可读名称
    private String getEntityName(Entity entity) {
        if (entity instanceof Player) return ((Player) entity).getName();
        return entity.getCustomName() != null ? entity.getCustomName() : entity.getName();
    }

    //伏击: 使用盾牌后获得力量效果 (盾牌, 满级III)
    @EventHandler
    public void AmbushHandler(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!isDirectMeleeAttack(event)) return;
        ItemStack offhand = player.getInventory().getItemInOffHand();
        int level = ArmsorEnchant.getEnchantLevel(offhand, AmbushKey);
        if (level <= 0) return;
        if (player.isBlocking()) return;
        if (player.hasMetadata("ArmsorPlus_AmbushCD")) return;

        int duration = level * 4; // 0.2*level秒 = level*4 ticks
        player.setMetadata("ArmsorPlus_AmbushCD", new FixedMetadataValue(getplugin, true));

        // 伏击: 伤害提升 level*15% (ADD_SCALAR 真实百分比修饰符)
        AttributeInstance atkAttr = player.getAttribute(Attribute.ATTACK_DAMAGE);
        if (atkAttr != null) {
            AttributeModifier ambushMod = new AttributeModifier(
                    new NamespacedKey(getplugin, "ArmsorPlus_AmbushDamage"),
                    0.15 * level, AttributeModifier.Operation.ADD_SCALAR,
                    org.bukkit.inventory.EquipmentSlotGroup.HAND);
            atkAttr.addTransientModifier(ambushMod);
            Bukkit.getScheduler().runTaskLater(getplugin,
                    () -> atkAttr.removeModifier(ambushMod), duration);
        }

        Bukkit.getScheduler().runTaskLater(getplugin,
                () -> player.removeMetadata("ArmsorPlus_AmbushCD", getplugin),
                duration);
    }

    // 解析时间戳字符串为List
    private static List<Long> parseTimestamps(String tsStr) {
        List<Long> list = new ArrayList<>();
        if (tsStr == null || tsStr.isEmpty()) return list;
        for (String s : tsStr.split(",")) {
            try { list.add(Long.parseLong(s)); } catch (NumberFormatException ignored) {}
        }
        return list;
    }

    // 计算噬生剑当前有效血裂数 (30秒内的时间戳数量, 上限20)
    public static int getDevourLifeBloodCount(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) return 0;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        List<Long> timestamps = parseTimestamps(pdc.get(DevourLifeBloodTimestamps, PersistentDataType.STRING));
        long now = System.currentTimeMillis();
        timestamps.removeIf(ts -> now - ts >= 30000);
        return Math.min(timestamps.size(), 20);
    }

    // 更新噬生剑显示: 耐久条 + Lore 血裂数 (0/20 → 20/20)
    private void updateDevourLifeDisplay(ItemMeta meta, int maxDurability, int bloodCount) {
        // 耐久条显示 (0血裂≈空条, 20血裂=满条; 至少保留1点耐久防止损坏, 攻击不消耗耐久)
        if (meta instanceof Damageable dmg) {
            int damage = maxDurability - 1 - (int) Math.round(
                    (double) (maxDurability - 1) * Math.min(bloodCount, 20) / 20);
            dmg.setDamage(damage);
        }
        // Lore 显示血裂数 (保留其他 Lore 行, 如附魔行)
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        String line = ChatColor.DARK_RED + "血裂数: " + bloodCount + "/20";
        boolean replaced = false;
        for (int i = 0; i < lore.size(); i++) {
            if (ChatColor.stripColor(lore.get(i)).contains("血裂数")) {
                lore.set(i, line);
                replaced = true;
                break;
            }
        }
        if (!replaced) lore.add(line);
        meta.setLore(lore);
    }

    // 血裂衰减刷新: 血裂存在30秒后衰减, 定时刷新显示 (耐久条+Lore)。
    // 每次只保留一个待定刷新, 并精确在"最旧血裂过期时刻"触发;
    // 刷新后若仍有未衰减的血裂则继续追踪, 保证血裂数下降时显示即时更新
    private final Set<UUID> pendingBloodDecayRefresh = new HashSet<>();

    private void scheduleBloodDecayRefresh(Player player) {
        scheduleBloodDecayRefresh(player, player.getEquipment().getItemInMainHand());
    }

    private void scheduleBloodDecayRefresh(Player player, ItemStack item) {
        if (pendingBloodDecayRefresh.contains(player.getUniqueId())) return;
        if (item == null || item.getType().isAir() || item.getItemMeta() == null) return;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        List<Long> timestamps = parseTimestamps(pdc.get(DevourLifeBloodTimestamps, PersistentDataType.STRING));
        if (timestamps.isEmpty()) return;
        // 精确到最旧血裂的过期时刻 (时间戳按追加顺序排列, 第一个最旧)
        long delayMs = timestamps.get(0) + 30000 - System.currentTimeMillis();
        scheduleBloodDecayRefresh(player, Math.max(1, (delayMs + 49) / 50)); // ms -> ticks 向上取整
    }

    private void scheduleBloodDecayRefresh(Player player, long delayTicks) {
        if (pendingBloodDecayRefresh.contains(player.getUniqueId())) return;
        pendingBloodDecayRefresh.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(getplugin, () -> {
            pendingBloodDecayRefresh.remove(player.getUniqueId());
            // 玩家已登出/死亡则跳过, 避免空指针
            if (!player.isOnline() || player.isDead()) return;
            ItemStack item = player.getEquipment().getItemInMainHand();
            if (item.getType().isAir() || ArmsorEnchant.getEnchantLevel(item, DevourLifeSwordKey) == 0) return;
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            String tsStr = pdc.get(DevourLifeBloodTimestamps, PersistentDataType.STRING);
            List<Long> timestamps = parseTimestamps(tsStr);
            long now = System.currentTimeMillis();
            timestamps.removeIf(ts -> now - ts >= 30000);
            int bloodCount = Math.min(timestamps.size(), 20);
            // 保存清理后的时间戳
            pdc.set(DevourLifeBloodTimestamps, PersistentDataType.STRING,
                    timestamps.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse(""));
            updateDevourLifeDisplay(meta, item.getType().getMaxDurability(), bloodCount);
            item.setItemMeta(meta);
            // 血裂未完全衰减: 继续追踪下一次衰减, 直到血裂清零
            if (!timestamps.isEmpty()) {
                long delayMs = timestamps.get(0) + 30000 - System.currentTimeMillis();
                scheduleBloodDecayRefresh(player, Math.max(1, (delayMs + 49) / 50));
            }
        }, Math.max(1, delayTicks));
    }

    // 噬生剑: 耐久度用来显示血裂数, 攻击/使用时不得消耗耐久
    @EventHandler
    public void onDevourLifeItemDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        if (ArmsorEnchant.getEnchantLevel(item, DevourLifeSwordKey) > 0) {
            event.setCancelled(true);
        }
    }

    // 刷新噬生剑显示 (Lore + 耐久条): 根据当前有效血裂数重写
    private void refreshDevourLifeDisplay(ItemStack item) {
        if (item == null || item.getType().isAir()) return;
        if (ArmsorEnchant.getEnchantLevel(item, DevourLifeSwordKey) == 0) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        int count = getDevourLifeBloodCount(item);
        updateDevourLifeDisplay(meta, item.getType().getMaxDurability(), count);
        item.setItemMeta(meta);
    }

    // 噬生切到主手时刷新显示: 剑在背包中时衰减刷新链会停掉, 显示变旧, 切回主手立即同步
    @EventHandler
    public void onDevourLifeItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());
        if (item == null || item.getType().isAir()) return;
        if (ArmsorEnchant.getEnchantLevel(item, DevourLifeSwordKey) == 0) return;
        int count = getDevourLifeBloodCount(item);
        refreshDevourLifeDisplay(item);
        // 剑已在主手, 若还有未衰减的血裂则续排衰减追踪
        if (count > 0) scheduleBloodDecayRefresh(player, item);
    }

    // 在背包/容器点击噬生剑时刷新显示
    @EventHandler
    public void onDevourLifeInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        refreshDevourLifeDisplay(event.getCurrentItem());
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

        // 获取射出箭矢的弓/弩 (弩发射时 getActiveItem() 不可靠, 需查主手/副手)
        ItemStack weapon = null;
        if (shooter instanceof Player p) {
            ItemStack main = p.getInventory().getItemInMainHand();
            if (main.getType() == BOW || main.getType() == CROSSBOW) {
                weapon = main;
            } else {
                ItemStack off = p.getInventory().getItemInOffHand();
                if (off.getType() == BOW || off.getType() == CROSSBOW) {
                    weapon = off;
                }
            }
        } else if (shooter.getEquipment() != null) {
            weapon = shooter.getEquipment().getItemInMainHand();
        }
        if (weapon == null || (weapon.getType() != BOW && weapon.getType() != CROSSBOW)) return;

        int speedLevel = ArmsorEnchant.getEnchantLevel(weapon, ArrowSpeed);
        if (speedLevel == 0) return;

        Vector velocity = arrow.getVelocity();
        velocity.multiply(speedLevel);

        int snipeLevel = ArmsorEnchant.getEnchantLevel(weapon, Sniping);
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
        // 穿透伤害无法被影避
        if (PIERCING_ACTIVE.contains(entity.getUniqueId())) return;
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        int level = ArmsorEnchant.getEnchantLevel(equipment.getBoots(), ShadowDodge);
        if (level == 0) return;
        if (!percent(8 * level)) return;

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

    // 蓄爆烟花爆炸时造成范围伤害
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
            if (entity instanceof LivingEntity le && entity != firework && !le.isDead()) {
                le.damage(damage);
            }
        }
    }

    // ========================================================================
    // 复仇 —— 反弹伤害 (胸甲)
    // ========================================================================

    @EventHandler(priority = EventPriority.LOW)
    public void RevengeHandler(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        // 穿透伤害不触发复仇反弹 (无法抵挡)
        if (PIERCING_ACTIVE.contains(player.getUniqueId())) return;
        // 防递归: 复仇反弹伤害再触发复仇 (双方都有复仇时)
        if (REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;

        ItemStack chestplate = player.getEquipment().getChestplate();
        if (chestplate == null) return;

        int level = ArmsorEnchant.getEnchantLevel(chestplate, RevengeKey);
        if (level <= 0) return;

        if (Math.random() >= 0.2 * level) return;

        Entity damager = event.getDamager();
        if (!(damager instanceof LivingEntity target)) return;

        double revengeDamage = event.getDamage() * 0.5;
        if (target.isDead()) return; // 前: 目标已死亡则不再反弹
        // 标记复仇伤害来源: 使 MergedDamageHandler 攻击侧跳过, 不触发血祭/吸血等附魔
        REVENGE_ACTIVE.add(target.getUniqueId());
        try {
            target.damage(revengeDamage, player);
        } finally {
            REVENGE_ACTIVE.remove(target.getUniqueId());
        }
        if (target.isDead()) return; // 后: 目标被反弹伤害击杀则停止

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
        // 穿透伤害不再重复施加凋零
        if (PIERCING_ACTIVE.contains(target.getUniqueId())) return;

        ItemStack weapon = null;
        if (damager instanceof Player) {
            weapon = ((Player) damager).getInventory().getItemInMainHand();
        } else if (damager instanceof LivingEntity living && living.getEquipment() != null) {
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
        // 格挡只对物理攻击生效 (魔法伤害由百草附魔处理)
        if (!isPhysicalAttack(event)) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        // 穿透伤害无法被格挡
        if (PIERCING_ACTIVE.contains(entity.getUniqueId())) return;
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        int level = ArmsorEnchant.getEnchantLevel(equipment.getHelmet(), BlockingKey);
        if (level <= 0) return;

        double original = event.getDamage();
        double reduction = level * 0.10; // 每级10%
        event.setDamage(original * (1.0 - reduction));

        if (entity instanceof Player player) {
            double blocked = original - event.getDamage();
            PlayerSettings.notifyActionBar(player, ChatColor.BLUE + "格挡: 减免了"
                    + String.format("%.1f", blocked) + "点伤害 (" + (level * 10) + "%)");
        }
    }

    // ========================================================================
    // 涤魂 —— 获得负面效果时 25%*level 概率免除 (胸甲, 满级III)
    // ========================================================================

    private static final Set<PotionEffectType> NEGATIVE_EFFECTS = new HashSet<>(Arrays.asList(
            PotionEffectType.POISON, PotionEffectType.WITHER, PotionEffectType.SLOWNESS,
            PotionEffectType.WEAKNESS, PotionEffectType.BLINDNESS, PotionEffectType.NAUSEA,
            PotionEffectType.HUNGER, PotionEffectType.MINING_FATIGUE, PotionEffectType.INSTANT_DAMAGE,
            PotionEffectType.LEVITATION, PotionEffectType.UNLUCK, PotionEffectType.DARKNESS,
            PotionEffectType.WIND_CHARGED, PotionEffectType.WEAVING, PotionEffectType.OOZING,
            PotionEffectType.INFESTED
    ));

    // 涤魂: 获得负面效果时概率免除 (胸甲, 满级III)
    @EventHandler
    public void onPotionEffectAdd(EntityPotionEffectEvent event) {
        if (event.isCancelled()) return;
        if (event.getAction() != EntityPotionEffectEvent.Action.ADDED
                && event.getAction() != EntityPotionEffectEvent.Action.CHANGED) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (!NEGATIVE_EFFECTS.contains(event.getModifiedType())) return;
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null) return;
        int lvl = ArmsorEnchant.getEnchantLevel(chestplate, EffectClear);
        if (lvl <= 0) return;
        if (percent(25 * lvl)) {
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL, 0.8f, 1.2f);
            PlayerSettings.notifyActionBar(player, "§f[涤魂]免疫了负面效果 §7(" + (25 * lvl) + "%)");
        }
    }

    // ========================================================================
    // 百草 —— 药水/魔法伤害减免 15%*level, 并有 5%*level 概率免疫 (胸甲, 满级III)
    // ========================================================================

    private static final EntityDamageEvent.DamageCause[] MAGIC_DAMAGE_TYPES = {
            EntityDamageEvent.DamageCause.MAGIC,
            EntityDamageEvent.DamageCause.POISON,
            EntityDamageEvent.DamageCause.WITHER,
            EntityDamageEvent.DamageCause.DRAGON_BREATH
    };

    private boolean isMagicDamage(EntityDamageEvent.DamageCause cause) {
        for (var type : MAGIC_DAMAGE_TYPES) {
            if (cause == type) return true;
        }
        return false;
    }

    // 判断是否为"物理攻击": 由实体造成 (近战/弓箭/投射物), 且不是魔法伤害
    // 落伤/火焰/虚空/魔法等非实体攻击不算物理攻击
    private boolean isPhysicalAttack(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return false;
        return !isMagicDamage(event.getCause());
    }

    // ========================================================================
    // 闪避 —— 概率闪避攻击伤害 (靴子)
    // ========================================================================

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnBeaten(EntityDamageByEntityEvent event) {
        // 穿透伤害无法被闪避
        if (PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        // 闪避只对物理攻击生效 (魔法伤害无法闪避, 由百草附魔处理)
        if (!isPhysicalAttack(event)) return;

        // 玩家闪避玩家
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof Player damager) {
            ItemStack boots = player.getInventory().getBoots();
            int level = ArmsorEnchant.getEnchantLevel(boots, Dodgekey);
            if (level == 0 || !percent(8 * level)) return;

            event.setDamage(event.getDamage() * 0.2); // 闪避: 减免80%伤害 (只受20%)
            Particle.ENCHANTED_HIT.builder().location(player.getLocation())
                    .offset(0.1, 0.1, 0.1).count(48).receivers(32, true).spawn();
            PlayerSettings.notifyActionBar(player, ChatColor.GOLD + "你闪避了对方大部分伤害（减免80%）");
            PlayerSettings.notifyActionBar(damager, ChatColor.GOLD + "对方闪避了你的伤害（减免80%）");
            return;
        }

        // 玩家闪避生物
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof LivingEntity) {
            ItemStack boots = player.getInventory().getBoots();
            int level = ArmsorEnchant.getEnchantLevel(boots, Dodgekey);
            if (level == 0 || !percent(8 * level)) return;

            event.setDamage(event.getDamage() * 0.2); // 闪避: 减免80%伤害 (只受20%)
            Particle.PORTAL.builder().location(player.getLocation())
                    .offset(0.1, 0.1, 0.1).count(64).receivers(32, true).spawn();
            PlayerSettings.notifyActionBar(player, ChatColor.GOLD + "你闪避了对方大部分伤害（减免80%）");
            return;
        }

        // 生物闪避生物
        if (event.getEntity() instanceof LivingEntity entity) {
            EntityEquipment equipment = entity.getEquipment();
            if (equipment == null) return;
            ItemStack boots = equipment.getBoots();
            int level = ArmsorEnchant.getEnchantLevel(boots, Dodgekey);
            if (level == 0 || !percent(20 * level)) return;

            event.setDamage(event.getDamage() * 0.2); // 闪避: 减免80%伤害 (只受20%)
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
        // 穿透伤害不再触发涟漪回复
        if (PIERCING_ACTIVE.contains(entity.getUniqueId())) return;
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        int level = ArmsorEnchant.getEnchantLevel(
                equipment.getBoots(), RipplesProtectkey);
        if (level <= 0) return;

        // 涟漪: 受到伤害后回复生命 (每级回复所受伤害的20%, 匹配Lore)
        double heal = event.getDamage() * 0.2 * level;
        if (heal > 0) {
            entity.setHealth(Math.min(entity.getMaxHealth(), entity.getHealth() + heal));
            if (entity instanceof Player p) {
                PlayerSettings.notifyActionBar(p, ChatColor.BLUE + "涟漪回复了 " + String.format("%.1f", heal) + " 点生命");
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
            if (percent(10)) {
                player.getInventory().addItem(ThunderclapArrow_EnchantedBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.YELLOW + "获得惊雷附魔书");
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
        // 穿透伤害无视幸存 (无法抵挡)
        if (PIERCING_ACTIVE.contains(player.getUniqueId())) return;

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
    // 不灭 —— 防止死亡 (任意装备栏)
    // ========================================================================

    @EventHandler(priority = EventPriority.HIGHEST)
    public void IndestructibleHandler(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        // 穿透伤害无视不朽 (无法抵挡)
        if (PIERCING_ACTIVE.contains(player.getUniqueId())) return;
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
    // 傀儡守护者 —— 受伤时召唤铁傀儡反击 (胸甲, 每级1只, 冷却300秒, 满级V)
    // ========================================================================

    private final Map<UUID, Long> golemCooldowns = new HashMap<>();

    @EventHandler
    public void GolemGuardianHandler(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        // 穿透伤害不再召唤傀儡
        if (PIERCING_ACTIVE.contains(player.getUniqueId())) return;
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

        // 5点穿透伤害 (前后检查目标是否死亡)
        if (target.isDead()) return; // 前: 目标已死亡则跳过
        target.damage(5, player);
        if (target.isDead()) return; // 后: 目标被击杀则停止特效
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
        // 穿透伤害无法被全息盾格挡
        if (PIERCING_ACTIVE.contains(player.getUniqueId())) return;

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
        if (event.getDamager() instanceof LivingEntity damager && damager.getEquipment() != null) {
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
                // 1 tick 后重新读取副手, 避免玩家换物品时减错
                if (player.getGameMode() != GameMode.CREATIVE) {
                    ItemStack currentOffHand = player.getInventory().getItemInOffHand();
                    if (currentOffHand.getType() == seedType) {
                        currentOffHand.setAmount(currentOffHand.getAmount() - 1);
                    }
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
        if (!isDirectMeleeAttack(event)) return;

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
    // 惊雷 —— 命中召唤level道雷，未命中(击中方块)召唤1道雷 (弓, 满级III)
    // ========================================================================

    @EventHandler
    public void ThunderclapArrowHandler(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player player)) return;

        int level = getThunderclapBowLevel(player);
        if (level <= 0) return;

        Location hitLoc = arrow.getLocation();
        World world = hitLoc.getWorld();

        if (event.getHitEntity() != null) {
            for (int i = 0; i < level; i++) {
                world.strikeLightning(hitLoc);
            }
        } else if (event.getHitBlock() != null) {
            world.strikeLightning(hitLoc);
        }
    }

    private int getThunderclapBowLevel(Player player) {
        int level = 0;
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() == BOW) {
            level = ArmsorEnchant.getEnchantLevel(mainHand, ThunderclapArrowKey);
        }
        if (level == 0) {
            ItemStack offHand = player.getInventory().getItemInOffHand();
            if (offHand.getType() == BOW) {
                level = ArmsorEnchant.getEnchantLevel(offHand, ThunderclapArrowKey);
            }
        }
        return level;
    }

    // 根据护甲材质估算基础护甲值
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

    // ========================================================================
    // 元素之刃 —— 将攻击伤害转化为对应属性 (最高优先级最后执行)
    // ========================================================================

    private static final String ELEMENTAL_BLADE_FLAG = "ArmsorPlus_ElementalBlade";

    @EventHandler(priority = EventPriority.LOWEST)
    public void ElementalBladeHandler(EntityDamageByEntityEvent event) {
        if (event.getEntity().equals(event.getDamager())) return;
        if (!isDirectMeleeAttack(event)) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (target.hasMetadata(ELEMENTAL_BLADE_FLAG)) return;
        if (damager.getEquipment() == null) return;

        org.bukkit.inventory.ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        double damage = event.getDamage();
        event.setDamage(0);
        event.setCancelled(true);

        target.setMetadata(ELEMENTAL_BLADE_FLAG, new org.bukkit.metadata.FixedMetadataValue(getplugin, true));
        try {

        if (ArmsorEnchant.getEnchantLevel(weapon, FireBladeKey) > 0) {
            target.setFireTicks(60);
            PlayerSettings.notify(damager, ChatColor.RED + "火印: 火焰伤害");
        } else if (ArmsorEnchant.getEnchantLevel(weapon, FrostBladeKey) > 0) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 1));
            PlayerSettings.notify(damager, ChatColor.AQUA + "霜印: 冰冻伤害");
        } else if (ArmsorEnchant.getEnchantLevel(weapon, ThunderBladeKey) > 0) {
            target.getWorld().strikeLightningEffect(target.getLocation());
            PlayerSettings.notify(damager, ChatColor.YELLOW + "雷印: 雷电伤害");
        } else if (ArmsorEnchant.getEnchantLevel(weapon, MagicBladeKey) > 0) {
            target.getWorld().spawnParticle(Particle.ENCHANT, target.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0);
            PlayerSettings.notify(damager, ChatColor.DARK_PURPLE + "魔印: 魔法伤害");
        } else {
            target.removeMetadata(ELEMENTAL_BLADE_FLAG, getplugin);
            event.setDamage(damage);
            event.setCancelled(false);
            return;
        }

        int fireLvl = ArmsorEnchant.getEnchantLevel(weapon, FireBladeKey);
        int frostLvl = ArmsorEnchant.getEnchantLevel(weapon, FrostBladeKey);
        int thunderLvl = ArmsorEnchant.getEnchantLevel(weapon, ThunderBladeKey);
        int magicLvl = ArmsorEnchant.getEnchantLevel(weapon, MagicBladeKey);
        double extra = 0;
        String msg = "";

        if (fireLvl > 0) {
            if (target.hasMetadata("ArmsorPlus_FrostMark")) {
                target.removeMetadata("ArmsorPlus_FrostMark", getplugin);
                damage *= 2.25; msg = ChatColor.RED + "火印·融霜: +125%";
            } else if (target.hasMetadata("ArmsorPlus_ThunderMark")) {
                target.removeMetadata("ArmsorPlus_ThunderMark", getplugin);
                damage *= 2.25; msg = ChatColor.RED + "火印·爆雷: +125%";
            } else {
                target.setMetadata("ArmsorPlus_FireMark", new FixedMetadataValue(getplugin, true));
                target.setFireTicks(40 * fireLvl); msg = ChatColor.RED + "施加火印(" + fireLvl + ")";
                spawnMarkParticles(target, "ArmsorPlus_FireMark", 60 * fireLvl, Particle.FLAME);
                int f = fireLvl;
                Bukkit.getScheduler().runTaskLater(getplugin,
                        () -> target.removeMetadata("ArmsorPlus_FireMark", getplugin), 60L * f);
            }
        } else if (frostLvl > 0) {
            if (target.hasMetadata("ArmsorPlus_FireMark")) {
                target.removeMetadata("ArmsorPlus_FireMark", getplugin);
                damage *= 2.0; msg = ChatColor.AQUA + "霜印·灭焰: +100%";
            } else if (target.hasMetadata("ArmsorPlus_ThunderMark")) {
                target.removeMetadata("ArmsorPlus_ThunderMark", getplugin);
                damage *= 1.6; extra = damage * 0.6; msg = ChatColor.AQUA + "霜印·导雷: +60%+雷伤";
            } else {
                target.setMetadata("ArmsorPlus_FrostMark", new FixedMetadataValue(getplugin, true));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * frostLvl, 1));
                msg = ChatColor.AQUA + "施加霜印(" + frostLvl + ")";
                spawnMarkParticles(target, "ArmsorPlus_FrostMark", 60 * frostLvl, Particle.SNOWFLAKE);
                int f = frostLvl;
                Bukkit.getScheduler().runTaskLater(getplugin,
                        () -> target.removeMetadata("ArmsorPlus_FrostMark", getplugin), 60L * f);
            }
        } else if (thunderLvl > 0) {
            if (target.hasMetadata("ArmsorPlus_FireMark")) {
                target.removeMetadata("ArmsorPlus_FireMark", getplugin);
                damage *= 2.0; msg = ChatColor.YELLOW + "雷印·引火: +100%";
            } else if (target.hasMetadata("ArmsorPlus_FrostMark")) {
                target.removeMetadata("ArmsorPlus_FrostMark", getplugin);
                damage *= 1.75; msg = ChatColor.YELLOW + "雷印·碎霜: +75%";
            } else {
                target.setMetadata("ArmsorPlus_ThunderMark", new FixedMetadataValue(getplugin, true));
                target.getWorld().strikeLightningEffect(target.getLocation());
                msg = ChatColor.YELLOW + "施加雷印(" + thunderLvl + ")";
                spawnMarkParticles(target, "ArmsorPlus_ThunderMark", 60 * thunderLvl, Particle.ELECTRIC_SPARK);
                int t = thunderLvl;
                Bukkit.getScheduler().runTaskLater(getplugin,
                        () -> target.removeMetadata("ArmsorPlus_ThunderMark", getplugin), 60L * t);
            }
        } else if (magicLvl > 0) {
            if (target.hasMetadata("ArmsorPlus_MagicMark")) {
                damage *= 2.0;
            }
            target.setMetadata("ArmsorPlus_MagicMark", new FixedMetadataValue(getplugin, true));
            target.getWorld().spawnParticle(Particle.ENCHANT, target.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0);
            msg = ChatColor.DARK_PURPLE + "施加魔印: 魔法伤害2x";
            spawnMarkParticles(target, "ArmsorPlus_MagicMark", 60 * magicLvl, Particle.ENCHANT);
            int m = magicLvl;
            Bukkit.getScheduler().runTaskLater(getplugin,
                    () -> target.removeMetadata("ArmsorPlus_MagicMark", getplugin), 60L * m);
        }

        PlayerSettings.notify(damager, msg);
        if (target.isDead()) return;
        target.damage(damage + extra, damager);
        if (target.isDead()) return;
        } finally {
            // 确保 flag 一定会被清除, 即使中途异常或提前 return
            Bukkit.getScheduler().runTaskLater(getplugin,
                    () -> target.removeMetadata(ELEMENTAL_BLADE_FLAG, getplugin), 1L);
        }
    }

    // 让带印记的目标持续散发对应元素粒子 (印记存续期间, 每0.25秒一次)
    private void spawnMarkParticles(Entity target, String mark, int durationTicks, Particle particle) {
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= durationTicks || target.isDead() || !target.hasMetadata(mark)) {
                    cancel();
                    return;
                }
                target.getWorld().spawnParticle(particle,
                        target.getLocation().add(0, 1, 0), 3, 0.3, 0.3, 0.3, 0.01);
                ticks++;
            }
        }.runTaskTimer(getplugin, 0L, 5L);
    }

    // ========================================================================
    // 重甲 —— 穿戴时持续给予缓慢II+抗性提升II (胸甲)
    // ========================================================================

    private final Map<UUID, Long> heavyArmorTimers = new HashMap<>();

    @EventHandler
    public void HeavyArmorHandler(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack chestplate = player.getInventory().getChestplate();
        int level = ArmsorEnchant.getEnchantLevel(chestplate, HeavyArmorKey);

        UUID pid = player.getUniqueId();
        Long last = heavyArmorTimers.get(pid);
        long now = System.currentTimeMillis();

        if (level <= 0) {
            if (last != null) {
                player.removePotionEffect(PotionEffectType.SLOWNESS);
                player.removePotionEffect(PotionEffectType.RESISTANCE);
                heavyArmorTimers.remove(pid);
            }
            return;
        }

        // 每5秒刷新一次效果
        if (last != null && (now - last) < 5000) return;
        heavyArmorTimers.put(pid, now);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 8 * 20, 1, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 8 * 20, 1, false, true));
    }

    // ========================================================================
    // 地之眷顾 —— 挖泥土概率掉落金粒/铁粒 (铲子)
    // ========================================================================

    private static final Material[] DIRT_TYPES = {
            Material.DIRT, Material.GRASS_BLOCK, Material.PODZOL, Material.MYCELIUM,
            Material.DIRT_PATH, Material.ROOTED_DIRT, Material.COARSE_DIRT,
            Material.FARMLAND, Material.MUD
    };

    private boolean isDirt(Material m) {
        for (Material dirt : DIRT_TYPES) {
            if (m == dirt) return true;
        }
        return false;
    }

    @EventHandler
    public void EarthFavorHandler(BlockBreakEvent event) {
        if (!isDirt(event.getBlock().getType())) return;

        Player player = event.getPlayer();
        ItemStack shovel = player.getInventory().getItemInMainHand();
        int level = ArmsorEnchant.getEnchantLevel(shovel, EarthFavorKey);
        if (level <= 0) return;

        if (!percent(10 * level)) return;

        Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        World world = event.getBlock().getWorld();
        if (percent(50)) {
            world.dropItemNaturally(loc, new ItemStack(Material.GOLD_NUGGET, 1));
        } else {
            world.dropItemNaturally(loc, new ItemStack(Material.IRON_NUGGET, 1));
        }
    }

    // ========================================================================
    // 统一处理器 (HIGHEST, 血祭+卸力最先)
    // 攻击: EntityDamageByEntityEvent → 武器附魔
    // 防御: EntityDamageEvent → 胸甲附魔
    // ========================================================================
    @EventHandler(priority = EventPriority.HIGHEST)
    public void MergedDamageHandler(EntityDamageEvent event) {
        // 本插件发出的穿透伤害触发的新事件 → 直接跳过 (防止递归重入, 也保证穿透伤害不被防御附魔削减)
        if (event.getEntity() instanceof LivingEntity le && PIERCING_ACTIVE.contains(le.getUniqueId())) return;

        // ====== 防御侧: 胸甲附魔 (只对Player) ======
        if (event.getEntity() instanceof Player player) {
            ItemStack chestplate = player.getInventory().getChestplate();
            if (chestplate != null) {
                // 卸力 (最先): 受到100+伤害时拆分为 (level+1) 段, 每段间隔4tick
                int ddLvl = ArmsorEnchant.getEnchantLevel(chestplate, DamageDispersalKey);
                if (ddLvl > 0) {
                    java.util.UUID pid = player.getUniqueId();
                    if (!DAMAGE_SPLIT_ACTIVE.contains(pid)) {
                        double total = event.getDamage();
                        if (total >= 100) {
                            int parts = ddLvl + 1;
                            double part = total / parts;
                            DAMAGE_SPLIT_ACTIVE.add(pid);
                            event.setDamage(part); // 第一段立即结算, 剩余段延迟 4*i tick
                            for (int i = 1; i < parts; i++) {
                                int delay = i * 4;
                                boolean last = (i == parts - 1);
                                Bukkit.getScheduler().runTaskLater(getplugin, () -> {
                                    if (player.isOnline() && !player.isDead()) {
                                        player.setNoDamageTicks(0); // 绕过受伤无敌帧, 保证每段都生效
                                        player.damage(part);
                                    }
                                    if (last) DAMAGE_SPLIT_ACTIVE.remove(pid);
                                }, delay);
                            }
                            PlayerSettings.notify(player, ChatColor.DARK_GREEN + "卸力: " + total + "点伤害拆分为" + parts + "段");
                        }
                    }
                }
                // 百草: 药水/魔法伤害减免15%*level, 并有5%*level概率免疫 (满级III)
                int hgLvl = ArmsorEnchant.getEnchantLevel(chestplate, HerbGuardKey);
                if (hgLvl > 0 && isMagicDamage(event.getCause())) {
                    if (percent(5 * hgLvl)) {
                        event.setDamage(0);
                        PlayerSettings.notifyActionBar(player, "§a[百草]免疫了魔法伤害 §7(" + (5 * hgLvl) + "%)");
                    } else {
                        event.setDamage(event.getDamage() * (1.0 - 0.15 * hgLvl));
                        PlayerSettings.notifyActionBar(player, "§a[百草]魔法伤害减免 §7(" + (hgLvl * 15) + "%)");
                    }
                }
                // 保护PRO
                int proLvl = ArmsorEnchant.getEnchantLevel(chestplate, ProtectionPROKey);
                if (proLvl > 0) event.setDamage(event.getDamage() * (1.0 - 0.06 * proLvl));
            }
        }

        // ====== 攻击侧: 武器附魔 (只对EntityDamageByEntityEvent) ======
        if (!(event instanceof EntityDamageByEntityEvent ebEvent)) return;
        // 复仇反弹伤害溯源: 跳过攻击侧, 避免触发血祭/吸血等武器附魔
        if (REVENGE_ACTIVE.contains(ebEvent.getEntity().getUniqueId())) return;
        // 伤害溯源: 只有真实近战攻击(ENTITY_ATTACK/ENTITY_SWEEP_ATTACK)才触发武器附魔。
        // 荆棘(THORNS)等反弹伤害的 damager 是持剑玩家, 但并非玩家主动挥剑, 必须排除,
        // 否则血祭/双重打击会在荆棘伤害上重复触发(血祭还会额外自伤10点生命)
        if (!isDirectMeleeAttack(ebEvent)) return;
        if (ebEvent.getEntity().equals(ebEvent.getDamager())) return;
        if (!(ebEvent.getDamager() instanceof LivingEntity damager)) return;
        if (!(ebEvent.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        // 血祭 (最先)
        int bslvl = ArmsorEnchant.getEnchantLevel(weapon, BloodSacrificekey);
        if (bslvl > 0 && percent(20 * bslvl)) {
            int rate = new Random().nextInt(bslvl) + 2;
            double dmg = ebEvent.getDamage() * rate;
            ebEvent.setDamage(dmg);
            // 血祭代价: 攻击者受10点无法抵挡的穿透伤害 (走事件系统, 避免 setHealth(0) 假死)
            if (!PIERCING_ACTIVE.contains(damager.getUniqueId())) {
                PIERCING_ACTIVE.add(damager.getUniqueId());
                try {
                    damager.damage(10, pierceSource(damager));
                } finally {
                    PIERCING_ACTIVE.remove(damager.getUniqueId());
                }
            }
            PlayerSettings.notify(ebEvent.getDamager(), "你发动了" + ChatColor.RED + "血祭" + ChatColor.RESET + "对对方造成" + rate + "倍伤害");
            PlayerSettings.notify(ebEvent.getEntity(), "对方发动了" + ChatColor.RED + "血祭" + ChatColor.RESET + "对你造成" + rate + "倍伤害");
            if (damager.isDead()) return;
        }

        // 利刃
        int sbLvl = ArmsorEnchant.getEnchantLevel(weapon, SharpBladeKey);
        if (sbLvl > 0) {
            double armor = 0;
            if (target.getEquipment() != null)
                for (ItemStack p : target.getEquipment().getArmorContents())
                    if (p != null && p.getType() != Material.AIR) armor += getArmorValue(p.getType());
            double multiplier = armor > 18 ? 1.10 : 1.0 + (18.0 - armor) / 18.0 * sbLvl * 0.08;
            ebEvent.setDamage(ebEvent.getDamage() * multiplier);
            if (armor <= 18)
                PlayerSettings.notify(ebEvent.getDamager(), ChatColor.DARK_AQUA + "利刃: 伤害提升" + String.format("%.0f", (multiplier - 1) * 100) + "%");
        }

        // 双重打击
        int dhLvl = ArmsorEnchant.getEnchantLevel(weapon, DoubleHitkey);
        if (dhLvl > 0 && percent(20 * dhLvl)) {
            ebEvent.setDamage(ebEvent.getDamage() * 2);
            PlayerSettings.notify(ebEvent.getDamager(), "你发动了" + ChatColor.RED + "双重打击" + ChatColor.RESET + "对对方造成" + ebEvent.getDamage() + "点伤害");
            PlayerSettings.notify(ebEvent.getEntity(), "对方发动了" + ChatColor.RED + "双重打击" + ChatColor.RESET + "对你造成" + ebEvent.getDamage() + "点伤害");
        }

        // 贯穿: 长矛冲锋攻击(冲刺)后固定造成2*level穿透伤害 (满级III)
        int pierceLvl = ArmsorEnchant.getEnchantLevel(weapon, PierceKey);
        if (pierceLvl > 0 && damager instanceof Player piercer
                && piercer.isSprinting() && weapon.getType().name().endsWith("_SPEAR")) {
            if (target.isDead()) return; // 前: 目标已死亡则跳过
            if (!PIERCING_ACTIVE.contains(target.getUniqueId())) {
                PIERCING_ACTIVE.add(target.getUniqueId());
                try {
                    target.damage(2.0 * pierceLvl, pierceSource(damager));
                } finally {
                    PIERCING_ACTIVE.remove(target.getUniqueId());
                }
            }
            if (target.isDead()) return; // 后: 目标被击杀则停止特效
            target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0),
                    pierceLvl * 4, 0.3, 0.3, 0.3, 0.1);
            PlayerSettings.notify(damager, ChatColor.DARK_PURPLE + "贯穿: 额外造成 " + (2 * pierceLvl) + " 点穿透伤害");
        }

        // 星痕
        if (ArmsorEnchant.getEnchantLevel(weapon, StarTraceSwordKey) > 0) {
            long t = target.getWorld().getTime();
            if (t >= 13000 && t <= 23000) {
                ebEvent.setDamage(ebEvent.getDamage() * 2.0);
                PlayerSettings.notify(ebEvent.getDamager(), ChatColor.DARK_AQUA + "星痕 · 星光之力: 伤害提升100%");
            }
        }

        // 雷光
        if (ArmsorEnchant.getEnchantLevel(weapon, ThunderGlowKey) > 0 && target.getWorld().isThundering()) {
            ebEvent.setDamage(ebEvent.getDamage() * 1.25);
            PlayerSettings.notify(ebEvent.getDamager(), ChatColor.YELLOW + "雷光 · 雷霆之力: 伤害提升25%");
        }

        // 暴击
        int csLvl = ArmsorEnchant.getEnchantLevel(weapon, CriticalStrikeKey);
        if (csLvl > 0 && percent(15 * csLvl)) {
            ebEvent.setDamage(ebEvent.getDamage() * (1.0 + 0.25 * csLvl));
            if (ebEvent.getDamager() instanceof Player)
                PlayerSettings.notify(ebEvent.getDamager(), ChatColor.RED + "暴击! 造成" + (int)(25 * csLvl) + "%额外伤害");
        }

        World world = target.getWorld();

        // 烈阳
        if (ArmsorEnchant.getEnchantLevel(weapon, BlazingSunKey) > 0 && world.getTime() < 13000)
            ebEvent.setDamage(ebEvent.getDamage() + 8.0);

        // 冰刺
        int isLvl = ArmsorEnchant.getEnchantLevel(weapon, IceSpikeKey);
        if (isLvl > 0) ebEvent.setDamage(ebEvent.getDamage() + isLvl * 5.0);

        // 烈焰
        int ifLvl = ArmsorEnchant.getEnchantLevel(weapon, InfernoKey);
        if (ifLvl > 0) { ebEvent.setDamage(ebEvent.getDamage() + ifLvl * 5.0); target.setFireTicks(20); }

        // 玄武剑
        if (ArmsorEnchant.getEnchantLevel(weapon, BlackTortoiseSwordKey) > 0) {
            ItemMeta meta = weapon.getItemMeta();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            Double acc = pdc.get(TortoiseAccumulatedKey, PersistentDataType.DOUBLE);
            if (acc == null) acc = 0.0;
            acc += ebEvent.getDamage();
            int bonus = Math.min((int)(acc / 200.0), 8);
            pdc.set(TortoiseAccumulatedKey, PersistentDataType.DOUBLE, acc);
            List<String> lore = meta.getLore();
            if (lore != null && lore.size() >= 3) {
                lore.set(1, ChatColor.DARK_GREEN + "当前加成: " + bonus + "/8");
                lore.set(2, ChatColor.GRAY + "累计伤害: " + (int)(acc % 200) + "/200");
                meta.setLore(lore);
            }
            weapon.setItemMeta(meta);
            if (bonus > 0) ebEvent.setDamage(ebEvent.getDamage() + bonus);
        }

        // 噬生
        if (ArmsorEnchant.getEnchantLevel(weapon, DevourLifeSwordKey) > 0) {
            ItemMeta meta = weapon.getItemMeta();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            Double score = pdc.get(ScoreKey, PersistentDataType.DOUBLE);
            if (score == null) score = 0.0;
            String tsStr = pdc.get(DevourLifeBloodTimestamps, PersistentDataType.STRING);
            List<Long> timestamps = parseTimestamps(tsStr);
            long now = System.currentTimeMillis();
            timestamps.removeIf(ts -> now - ts >= 30000);
            int bc = Math.min(timestamps.size(), 20);
            score = Math.min(score, bc * 4.0 + 3.99);
            int oldT = (int)(score / 4.0);
            score += ebEvent.getFinalDamage();
            int newT = (int)(score / 4.0);
            int gained = newT - oldT;
            for (int i = 0; i < gained && bc < 20; i++) { timestamps.add(now); bc++; }
            if (bc >= 20) score = Math.min(score, 20 * 4.0 + 3.99);
            ebEvent.setDamage(ebEvent.getDamage() + bc);
            pdc.set(ScoreKey, PersistentDataType.DOUBLE, score);
            pdc.set(DevourLifeBloodTimestamps, PersistentDataType.STRING,
                    timestamps.stream().map(String::valueOf).reduce((a,b)->a+","+b).orElse(""));
            updateDevourLifeDisplay(meta, weapon.getType().getMaxDurability(), bc);
            weapon.setItemMeta(meta);
            if (gained > 0 && ebEvent.getDamager() instanceof Player)
                PlayerSettings.notify(ebEvent.getDamager(), ChatColor.DARK_PURPLE + "产生了一点血裂! 当前血裂数: " + bc);
            if (damager instanceof Player player) scheduleBloodDecayRefresh(player);
        }

        // 吸血
        int fdLvl = ArmsorEnchant.getEnchantLevel(weapon, Feedingkey);
        if (fdLvl > 0) {
            double heal = 1.5 * fdLvl;
            if (target.isDead()) return; // 前: 目标已死亡则跳过
            // 穿透伤害: 目标受 heal 点无法抵挡的伤害 (走事件系统, 避免 setHealth(0) 假死)
            if (!PIERCING_ACTIVE.contains(target.getUniqueId())) {
                PIERCING_ACTIVE.add(target.getUniqueId());
                try {
                    target.damage(heal, pierceSource(damager));
                } finally {
                    PIERCING_ACTIVE.remove(target.getUniqueId());
                }
            }
            damager.setHealth(Math.min(damager.getHealth() + heal, damager.getMaxHealth())); // 攻击者回血
            PlayerSettings.notify(ebEvent.getDamager(), ChatColor.RED + "吸血 恢复了" + String.format("%.1f", heal) + "点生命值");
            if (target.isDead()) return; // 后: 目标被吸血击杀则停止后续药水
        }

        // 剧毒
        int poLvl = ArmsorEnchant.getEnchantLevel(weapon, PoisonKey);
        if (poLvl > 0) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, (poLvl+1)*3*20, Math.min(poLvl-1,2), false, true));
            PlayerSettings.notify(ebEvent.getDamager(), ChatColor.DARK_GREEN + "你对敌人施加了剧毒" + romanNumeral(Math.min(poLvl, 3)));
            PlayerSettings.notify(ebEvent.getEntity(), ChatColor.DARK_GREEN + "你被施加了剧毒" + romanNumeral(Math.min(poLvl, 3)));
        }

        // 寒冻 (level*10% 概率)
        int fzLvl = ArmsorEnchant.getEnchantLevel(weapon, FreezeKey);
        if (fzLvl > 0 && percent(fzLvl * 10)) {
            if (target instanceof Player) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20*fzLvl, 1));
                PlayerSettings.notifyActionBar(target, ChatColor.AQUA + "你被敌人施加了寒冻");
            }
            PlayerSettings.notifyActionBar(damager, ChatColor.AQUA + "你对敌人施加了寒冻");
        }

        // 饥荒
        int faLvl = ArmsorEnchant.getEnchantLevel(weapon, Faminekey);
        if (faLvl > 0) {
            if (target instanceof Player) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 40*faLvl, 4*faLvl));
                PlayerSettings.notify(target, ChatColor.GREEN + "你被敌人施加了饥荒");
            }
            PlayerSettings.notify(ebEvent.getDamager(), ChatColor.GREEN + "你对敌人施加了饥荒");
        }

        // 失明 (level*10% 概率)
        int blLvl = ArmsorEnchant.getEnchantLevel(weapon, BlindnessKey);
        if (blLvl > 0 && percent(blLvl * 10)) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40*blLvl, 0, false, true));
            PlayerSettings.notifyActionBar(damager, ChatColor.DARK_GRAY + "你对敌人施加了失明");
            PlayerSettings.notifyActionBar(target, ChatColor.DARK_GRAY + "你被敌人施加了失明");
        }

        // 眩晕
        int stLvl = ArmsorEnchant.getEnchantLevel(weapon, StunKey);
        if (stLvl > 0)
            target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, (int)(stLvl*0.7*20), 0, false, false));
    }
}
