package Dim_LJR.armsorPlus.Boss;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.FreezeKey;

/**
 * BOSS清单 GUI + 全局BOSS战斗系统。
 * <p>
 * 管理BOSS召唤界面、躯干部位伤害重定向、元素反应逻辑。
 */
public class BossMenu implements Listener {

    private static Inventory bossList;

    // ========================================================================
    // BOSS 战斗跟踪系统
    // ========================================================================

    public enum BossType { CRYO, PYRO, SLIME }

    /** 身体部位实体 -> BOSS类型 */
    public static final Map<UUID, BossType> BOSS_BODY_PARTS = new HashMap<>();
    /** 核心部位实体 -> BOSS类型 */
    public static final Map<UUID, BossType> BOSS_CORE_PARTS = new HashMap<>();

    private static final Map<BossType, Double> bossHealth = new HashMap<>();
    private static final Map<BossType, Double> bossMaxHealth = new HashMap<>();
    private static final Map<BossType, LivingEntity> bossEntities = new HashMap<>();
    private static final Map<BossType, BossBar> bossBars = new HashMap<>();
    private static final Map<BossType, Set<UUID>> bossAllStands = new HashMap<>();

    private static final Random RANDOM = new Random();

    // ========================================================================
    // 注册 / 注销 API
    // ========================================================================

    /** 注册BOSS实体与血条 */
    public static void registerBoss(BossType type, LivingEntity entity, double maxHp, BossBar bar) {
        bossEntities.put(type, entity);
        bossMaxHealth.put(type, maxHp);
        bossHealth.put(type, maxHp);
        bossBars.put(type, bar);
        bossAllStands.put(type, new HashSet<>());
    }

    /** 注册身体部位实体 */
    public static void registerBodyStand(BossType type, UUID entityId) {
        BOSS_BODY_PARTS.put(entityId, type);
        bossAllStands.computeIfAbsent(type, k -> new HashSet<>()).add(entityId);
    }

    /** 注册核心部位实体 */
    public static void registerCoreStand(BossType type, UUID entityId) {
        BOSS_CORE_PARTS.put(entityId, type);
        bossAllStands.computeIfAbsent(type, k -> new HashSet<>()).add(entityId);
    }

    /** 将核心实体从核心降级为普通身体部位 */
    public static void downgradeCoreToBody(BossType type, UUID entityId) {
        BOSS_CORE_PARTS.remove(entityId);
        BOSS_BODY_PARTS.put(entityId, type);
    }

    /** 将实体从普通身体升级为核心 */
    public static void upgradeBodyToCore(BossType type, UUID entityId) {
        BOSS_BODY_PARTS.remove(entityId);
        BOSS_CORE_PARTS.put(entityId, type);
    }

    /** 获取BOSS当前血量 */
    public static double getBossHealth(BossType type) {
        return bossHealth.getOrDefault(type, 0.0);
    }

    /** 获取BOSS最大血量 */
    public static double getBossMaxHealth(BossType type) {
        return bossMaxHealth.getOrDefault(type, 1.0);
    }

    /** 对BOSS造成伤害 (返回true表示BOSS死亡) */
    public static boolean damageBoss(BossType type, double damage, Entity damager) {
        Double current = bossHealth.get(type);
        Double maxHp = bossMaxHealth.get(type);
        if (current == null || maxHp == null) return false;

        double newHealth = Math.max(0, current - damage);
        bossHealth.put(type, newHealth);

        BossBar bar = bossBars.get(type);
        if (bar != null) bar.setProgress(newHealth / maxHp);

        LivingEntity entity = bossEntities.get(type);
        if (entity != null && !entity.isDead()) {
            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PLAYER_HURT, 0.5f, 1.0f);
            entity.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR,
                    entity.getLocation().add(0, 2, 0), 12, 0.8, 0.5, 0.8, 0.3);
        }

        if (newHealth <= 0) {
            switch (type) {
                case CRYO -> CryoRegisvine.onDeath();
                case PYRO -> PyroRegisvine.onDeath();
                case SLIME -> SlimeBoss.onDeath();
            }
            return true;
        }
        return false;
    }

    /** 清理BOSS所有跟踪数据 */
    public static void unregisterBoss(BossType type) {
        Set<UUID> stands = bossAllStands.remove(type);
        if (stands != null) {
            for (UUID id : stands) {
                BOSS_BODY_PARTS.remove(id);
                BOSS_CORE_PARTS.remove(id);
            }
        }
        BossBar bar = bossBars.remove(type);
        if (bar != null) bar.removeAll();
        bossHealth.remove(type);
        bossMaxHealth.remove(type);
        bossEntities.remove(type);
    }

    /** 更新BOSS血条可见范围 (150格) */
    public static void updateBossBar(BossType type) {
        BossBar bar = bossBars.get(type);
        LivingEntity entity = bossEntities.get(type);
        if (bar == null || entity == null || entity.isDead()) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().equals(entity.getWorld()) && p.getLocation().distance(entity.getLocation()) <= 150) {
                if (!bar.getPlayers().contains(p)) bar.addPlayer(p);
            } else {
                bar.removePlayer(p);
            }
        }
    }

    // ========================================================================
    // 元素反应检测
    // ========================================================================

    private static boolean hasFireDamage(Player player, Entity originalDamager) {
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (weapon.containsEnchantment(Enchantment.FIRE_ASPECT)) return true;
        if (originalDamager instanceof Arrow arrow && arrow.getFireTicks() > 0) return true;
        return false;
    }

    private static boolean hasLightningDamage(Player player, Entity originalDamager) {
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (weapon.containsEnchantment(Enchantment.CHANNELING)) return true;
        if (originalDamager instanceof Trident) return true;
        return false;
    }

    private static boolean hasFrostDamage(Player player, Entity originalDamager) {
        ItemStack weapon = player.getInventory().getItemInMainHand();
        return ArmsorEnchant.getEnchantLevel(weapon, FreezeKey) > 0;
    }

    // ========================================================================
    // 躯干部位伤害处理
    // ========================================================================

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBossBodyPartDamage(EntityDamageByEntityEvent event) {

        Entity damaged = event.getEntity();
        UUID id = damaged.getUniqueId();

        // 保存原始伤害来源和伤害值 (投射物为箭矢/三叉戟等)
        Entity originalDamager = event.getDamager();
        double rawDamage = event.getDamage();

        Entity damager = originalDamager;
        if (damager instanceof Projectile proj) {
            ProjectileSource src = proj.getShooter();
            if (src instanceof Entity) damager = (Entity) src;
        }
        if (!(damager instanceof Player player)) return;

        // ======== 身体部位命中 (3倍武器伤害) ========
        BossType type = BOSS_BODY_PARTS.get(id);
        if (type != null) {
            double dmg = Math.max(rawDamage, 1.0) * 3;
            event.setCancelled(true);
            LivingEntity boss = bossEntities.get(type);
            if (boss == null || boss.isDead()) return;

            // 元素反应
            boolean elementalProc = false;
            if (type == BossType.CRYO) {
                if (hasFireDamage(player, originalDamager) || hasLightningDamage(player, originalDamager)) {
                    dmg *= 2;
                    elementalProc = true;
                    if (RANDOM.nextInt(100) < 50) CryoRegisvine.exposeCore();
                }
            } else if (type == BossType.PYRO) {
                if (hasLightningDamage(player, originalDamager) || hasFrostDamage(player, originalDamager)) {
                    dmg *= 2;
                    elementalProc = true;
                    if (RANDOM.nextInt(100) < 50) PyroRegisvine.exposeCore();
                }
            }

            // 元素反应特效
            if (elementalProc) {
                boss.getWorld().spawnParticle(Particle.FIREWORK, boss.getLocation().add(0, 2, 0),
                        40, 1.5, 1.0, 1.5, 0.3);
                boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.5f);
                String msg = type == BossType.CRYO
                        ? "§e⚡ 元素反应！对急冻树造成双倍伤害！"
                        : "§e⚡ 元素反应！对爆炎树造成双倍伤害！";
                player.sendMessage(msg);
            }

            damageBoss(type, dmg, player);
            damaged.getWorld().spawnParticle(Particle.CRIT, damaged.getLocation().add(0, 0.5, 0),
                    6, 0.3, 0.3, 0.3, 0.1);
            return;
        }

        // ======== 核心命中 (15倍武器伤害) ========
        type = BOSS_CORE_PARTS.get(id);
        if (type != null) {
            double dmg = Math.max(rawDamage, 1.0) * 15;
            event.setCancelled(true);
            LivingEntity boss = bossEntities.get(type);
            if (boss == null || boss.isDead()) return;

            // 核心命中特效
            boss.getWorld().strikeLightningEffect(boss.getLocation());
            boss.getWorld().spawnParticle(Particle.EXPLOSION, boss.getLocation().add(0, 2, 0),
                    2, 0.5, 0.5, 0.5, 0);
            boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.5f, 1.0f);

            player.sendMessage("§c✦ 命中核心！造成大量伤害！");

            damageBoss(type, dmg, player);
        }
    }

    // ========================================================================
    // BOSS清单界面
    // ========================================================================

    public static Inventory getBossList() {
        if (bossList == null) createBossList();
        return bossList;
    }

    private static void createBossList() {
        bossList = Bukkit.createInventory(null, 27, "§cBOSS清单");

        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName(" ");
        border.setItemMeta(borderMeta);
        for (int i = 0; i < 27; i++) {
            if (i < 9 || i >= 18 || i % 9 == 0 || i % 9 == 8) {
                bossList.setItem(i, border.clone());
            }
        }

        ItemStack cryo = new ItemStack(Material.PACKED_ICE);
        ItemMeta cryoMeta = cryo.getItemMeta();
        cryoMeta.setDisplayName("§b■ 急冻树");
        cryoMeta.setLore(Arrays.asList(
                "§7来自龙脊雪山的远古植物，",
                "§7拥有操控冰元素的力量。",
                "",
                "§c❤ 生命值: 750",
                "§b❄ 冰元素攻击",
                "§e✦ 攻击躯干部位造成3倍武器伤害，命中核心造成15倍武器伤害",
                "§6⚡ 火焰/雷电伤害触发元素反应: 双倍伤害+50%暴露核心",
                "",
                "§a▼ 点击召唤BOSS",
                "§7(请在空旷处召唤)"
        ));
        cryo.setItemMeta(cryoMeta);
        bossList.setItem(11, cryo);

        ItemStack slime = new ItemStack(Material.SLIME_BLOCK);
        ItemMeta slimeMeta = slime.getItemMeta();
        slimeMeta.setDisplayName("§a■ 史莱姆王");
        slimeMeta.setLore(Arrays.asList(
                "§7来自神秘沼泽的巨型史莱姆，",
                "§7手持满级附魔重锤。",
                "",
                "§c❤ 生命值: 750",
                "§a⚡ 重锤粉碎攻击",
                "§e✦ 跳跃时触发重锤粉碎，造成大量伤害",
                "§e✦ 半血激怒，召唤小史莱姆",
                "",
                "§a▼ 点击召唤BOSS",
                "§7(请在空旷处召唤)"
        ));
        slime.setItemMeta(slimeMeta);
        bossList.setItem(13, slime);

        ItemStack pyro = new ItemStack(Material.MAGMA_BLOCK);
        ItemMeta pyroMeta = pyro.getItemMeta();
        pyroMeta.setDisplayName("§c■ 爆炎树");
        pyroMeta.setLore(Arrays.asList(
                "§7来自层岩巨渊的远古植物，",
                "§7拥有操控火元素的力量。",
                "",
                "§c❤ 生命值: 750",
                "§c❄ 火元素攻击",
                "§e✦ 攻击躯干部位造成3倍武器伤害，命中核心造成15倍武器伤害",
                "§b❄ 雷电/冰冻伤害触发元素反应: 双倍伤害+50%暴露核心",
                "",
                "§a▼ 点击召唤BOSS",
                "§7(请在空旷处召唤)"
        ));
        pyro.setItemMeta(pyroMeta);
        bossList.setItem(15, pyro);
    }

    // ========================================================================
    // 菜单点击
    // ========================================================================

    @EventHandler
    public void onBossListClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(bossList)) return;
        event.setCancelled(true);

        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;

        String name = event.getCurrentItem().getItemMeta().getDisplayName();
        Player player = (Player) event.getWhoClicked();

        if (BossWorld.world == null || !player.getWorld().equals(BossWorld.world)) {
            player.sendMessage("§c⚠ 请在BOSS世界召唤BOSS！");
            player.sendMessage("§e使用主菜单中的「前往BOSS世界」传送");
            player.closeInventory();
            return;
        }

        if (name.contains("急冻树")) {
            if (CryoRegisvine.isAlive()) {
                Location loc = CryoRegisvine.getBossLocation();
                if (loc != null) {
                    player.teleport(loc);
                    player.sendMessage("§e急冻树尚未被击败，已传送至BOSS位置");
                }
                player.closeInventory();
                return;
            }
            player.closeInventory();
            player.sendMessage("§b◆ 急冻树已降临！");
            CryoRegisvine.spawnBoss(player);
        } else if (name.contains("爆炎树")) {
            if (PyroRegisvine.isAlive()) {
                Location loc = PyroRegisvine.getBossLocation();
                if (loc != null) {
                    player.teleport(loc);
                    player.sendMessage("§e爆炎树尚未被击败，已传送至BOSS位置");
                }
                player.closeInventory();
                return;
            }
            player.closeInventory();
            player.sendMessage("§c◆ 爆炎树已降临！");
            PyroRegisvine.spawnBoss(player);
        } else if (name.contains("史莱姆王")) {
            if (SlimeBoss.isAlive()) {
                Location loc = SlimeBoss.getBossLocation();
                if (loc != null) {
                    player.teleport(loc);
                    player.sendMessage("§e史莱姆王尚未被击败，已传送至BOSS位置");
                }
                player.closeInventory();
                return;
            }
            player.closeInventory();
            player.sendMessage("§a◆ 史莱姆王已降临！");
            SlimeBoss.spawnBoss(player);
        }
    }
}
