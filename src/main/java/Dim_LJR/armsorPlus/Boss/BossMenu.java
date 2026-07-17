package Dim_LJR.armsorPlus.Boss;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

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

    public enum BossType { CRYO, PYRO, SLIME, ZOMBIE_GIANT, BABY_ZOMBIE_DOUBLE, TREASURE_GUARDIAN, SKELETON_KING, VOID_WRAITH, ILLUSIONER }

    /** 身体部位实体 -> BOSS类型 */
    public static final Map<UUID, BossType> BOSS_BODY_PARTS = new HashMap<>();
    /** 核心部位实体 -> BOSS类型 */
    public static final Map<UUID, BossType> BOSS_CORE_PARTS = new HashMap<>();

    private static final Map<BossType, Double> bossHealth = new HashMap<>();
    private static final Map<BossType, Double> bossMaxHealth = new HashMap<>();
    private static final Map<BossType, LivingEntity> bossEntities = new HashMap<>();
    private static final Map<BossType, BossBar> bossBars = new HashMap<>();
    private static final Map<BossType, Set<UUID>> bossAllStands = new HashMap<>();
    private static final Map<BossType, String> bossBarBaseTitles = new HashMap<>();

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
        bossBarBaseTitles.put(type, bar.getTitle());
        if (type != BossType.CRYO) {
            bar.setTitle(bar.getTitle() + " §7" + Math.round(maxHp) + "/" + Math.round(maxHp));
        }
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

    /** 同步实体实际血量到追踪系统 (幻术师等使用原生血量的Boss) */
    public static void syncBossHealth(BossType type, double health, double maxHealth) {
        bossHealth.put(type, health);
        bossMaxHealth.put(type, maxHealth);
    }

    /** 对BOSS造成伤害 (返回true表示BOSS死亡) */
    public static boolean damageBoss(BossType type, double damage, Entity damager) {
        Double current = bossHealth.get(type);
        Double maxHp = bossMaxHealth.get(type);
        if (current == null || maxHp == null) return false;

        double newHealth = Math.max(0, current - damage);
        bossHealth.put(type, newHealth);

        BossBar bar = bossBars.get(type);
        if (bar != null) {
            bar.setProgress(newHealth / maxHp);
            if (type != BossType.CRYO) {
                String base = bossBarBaseTitles.get(type);
                if (base != null) {
                    bar.setTitle(base + " §7" + Math.round(newHealth) + "/" + Math.round(maxHp));
                }
            }
        }

        LivingEntity entity = bossEntities.get(type);
        if (entity != null && !entity.isDead()) {
            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PLAYER_HURT, 0.5f, 1.0f);
            entity.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR,
                    entity.getLocation().add(0, 2, 0), 12, 0.8, 0.5, 0.8, 0.3);
        }

        // 幻术师：通知当前攻击者用于卫道士索敌
        if (type == BossType.ILLUSIONER) {
            IllusionerBoss.updateTarget(damager);
        }

        if (newHealth <= 0) {
            switch (type) {
                case CRYO -> CryoRegisvine.onDeath();
                case PYRO -> PyroRegisvine.onDeath();
                case SLIME -> SlimeBoss.onDeath();
                case ZOMBIE_GIANT -> ZombieGiantBoss.onDeath();
                case BABY_ZOMBIE_DOUBLE -> BabyZombieDoubleBoss.onDeath();
                case TREASURE_GUARDIAN -> TreasureGuardianBoss.onDeath();
                case SKELETON_KING -> SkeletonKing.onDeath();
                case VOID_WRAITH -> VoidWraith.onDeath();
                case ILLUSIONER -> IllusionerBoss.onDeath();
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
    // 躯干部位伤害处理
    // ========================================================================

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBossBodyPartDamage(EntityDamageByEntityEvent event) {

        Entity damaged = event.getEntity();
        UUID id = damaged.getUniqueId();

        // 检查是否为已追踪的BOSS实体/部位
        BossType type = BOSS_BODY_PARTS.get(id);
        if (type == null) type = BOSS_CORE_PARTS.get(id);
        if (type == null) type = findBossTypeByEntity(id);
        if (type == null) return;

        // 幻术师/骷髅王：使用实体原生血量，保留原版AI，不取消伤害
        if (type == BossType.ILLUSIONER) {
            Entity damager = event.getDamager();
            if (damager instanceof Projectile proj) {
                ProjectileSource src = proj.getShooter();
                if (src instanceof Entity) damager = (Entity) src;
            }
            IllusionerBoss.updateTarget(damager);
            return;
        }
        if (type == BossType.SKELETON_KING) {
            return; // 原版伤害，AI循环读实体血量
        }

        // 无条件取消伤害 —— 防止实体实际血量被非玩家来源扣除致死
        event.setCancelled(true);
        LivingEntity boss = bossEntities.get(type);
        if (boss == null || boss.isDead()) return;

        double rawDamage = event.getDamage();

        // 解析伤害来源
        Entity originalDamager = event.getDamager();
        Entity damager = originalDamager;
        if (damager instanceof Projectile proj) {
            ProjectileSource src = proj.getShooter();
            if (src instanceof Entity) damager = (Entity) src;
        }

        // 阻止Boss自身造成的伤害(如箭雨箭矢/召唤物)
        if (damager.getUniqueId().equals(boss.getUniqueId())) return;

        double dmg = Math.max(rawDamage, 1.0);

        // ======== 核心命中 ========
        if (BOSS_CORE_PARTS.containsKey(id)) {
            boss.getWorld().strikeLightningEffect(boss.getLocation());
            boss.getWorld().spawnParticle(Particle.EXPLOSION, boss.getLocation().add(0, 2, 0),
                    2, 0.5, 0.5, 0.5, 0);
            boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.5f, 1.0f);
            if (damager instanceof Player player) {
                player.sendMessage("§c✦ 命中核心！造成大量伤害！");
            }
            damageBoss(type, dmg, damager);
            return;
        }

        // ======== 身体部位 / 本体命中 ========
        damageBoss(type, dmg, damager);
        damaged.getWorld().spawnParticle(Particle.CRIT, damaged.getLocation().add(0, 0.5, 0),
                6, 0.3, 0.3, 0.3, 0.1);
    }

    private static BossType findBossTypeByEntity(UUID id) {
        for (Map.Entry<BossType, LivingEntity> entry : bossEntities.entrySet()) {
            if (entry.getValue() != null && entry.getValue().getUniqueId().equals(id)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // ========================================================================
    // 环境伤害拦截 (坠落/火焰/窒息等)
    // ========================================================================

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBossEnvironmentalDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) return;
        if (findBossTypeByEntity(event.getEntity().getUniqueId()) != null) {
            event.setCancelled(true);
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
        bossList = Bukkit.createInventory(null, 45, "§cBOSS清单");

        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName(" ");
        border.setItemMeta(borderMeta);
        for (int i = 0; i < 45; i++) {
            if (i < 9 || i >= 36 || i % 9 == 0 || i % 9 == 8) {
                bossList.setItem(i, border.clone());
            }
        }

        ItemStack cryo = new ItemStack(Material.PACKED_ICE);
        ItemMeta cryoMeta = cryo.getItemMeta();
        cryoMeta.setDisplayName("§b■ 雪人王");
        cryoMeta.setLore(Arrays.asList(
                "§7来自龙脊雪山的远古植物，",
                "§7拥有操控冰元素的力量。",
                "",
                "§c❤ 生命值: 700",
                "§b❄ 核心: 雪人",
                "§e✦ 攻击树状盔甲架转移伤害至核心",
                "§6⚡ 火焰/雷电伤害触发元素反应: 双倍伤害+50%暴露核心",
                "",
                "§a▼ 点击召唤BOSS",
                "§7(请在空旷处召唤)"
        ));
        cryo.setItemMeta(cryoMeta);
        bossList.setItem(10, cryo);

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
        bossList.setItem(12, slime);

        ItemStack pyro = new ItemStack(Material.MAGMA_BLOCK);
        ItemMeta pyroMeta = pyro.getItemMeta();
        pyroMeta.setDisplayName("§c■ 烈焰领主");
        pyroMeta.setLore(Arrays.asList(
                "§7来自层岩巨渊的远古植物，",
                "§7拥有操控火元素的力量。",
                "",
                "§c❤ 生命值: 700",
                "§c❄ 核心: 烈焰人",
                "§e✦ 攻击树状盔甲架转移伤害至核心",
                "§b❄ 雷电/冰冻伤害触发元素反应: 双倍伤害+50%暴露核心",
                "",
                "§a▼ 点击召唤BOSS",
                "§7(请在空旷处召唤)"
        ));
        pyro.setItemMeta(pyroMeta);
        bossList.setItem(14, pyro);

        ItemStack giant = new ItemStack(Material.ZOMBIE_HEAD);
        ItemMeta giantMeta = giant.getItemMeta();
        giantMeta.setDisplayName("§4■ 僵尸巨人");
        giantMeta.setLore(Arrays.asList(
                "§7沉睡了千年的巨型僵尸，",
                "§7每一步都能让大地颤抖。",
                "",
                "§c❤ 生命值: 1500",
                "§4⚔ 攻击伤害: 80",
                "§e✦ 范围击退+缓慢效果",
                "",
                "§a▼ 点击召唤BOSS",
                "§7(请在空旷处召唤)"
        ));
        giant.setItemMeta(giantMeta);
        bossList.setItem(16, giant);

        ItemStack treasure = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta treasureMeta = treasure.getItemMeta();
        treasureMeta.setDisplayName("§6■ 宝藏守护者");
        treasureMeta.setLore(Arrays.asList(
                "§7守护着深海宝藏的神秘骷髅，",
                "§7手持重剑，身手敏捷。",
                "",
                "§c❤ 生命值: 250",
                "§6⚔ 攻击伤害: 15",
                "§e✦ 移速极快，拥有冰冻能力",
                "",
                "§a▼ 点击召唤BOSS",
                "§7(请在空旷处召唤)"
        ));
        treasure.setItemMeta(treasureMeta);
        bossList.setItem(30, treasure);

        ItemStack doubleZombie = new ItemStack(Material.ZOMBIE_SPAWN_EGG);
        ItemMeta doubleMeta = doubleZombie.getItemMeta();
        doubleMeta.setDisplayName("§5■ 小僵尸Double");
        doubleMeta.setLore(Arrays.asList(
                "§7两个小僵尸的组合，",
                "§7全身保护IV下界合金套。",
                "",
                "§c❤ 生命值: 600×2",
                "§5⚔ 一矛一剑，双重打击",
                "§e✦ 装备不可掉落",
                "",
                "§a▼ 点击召唤BOSS",
                "§7(请在空旷处召唤)"
        ));
        doubleZombie.setItemMeta(doubleMeta);
        bossList.setItem(28, doubleZombie);

        ItemStack skKing = new ItemStack(Material.BOW);
        ItemMeta skMeta = skKing.getItemMeta();
        skMeta.setDisplayName("§8■ 骷髅王");
        skMeta.setLore(Arrays.asList("§7手持力量X神弓的骷髅王者，", "§7每隔10秒召唤箭雨。", "", "§c❤ 生命值: 600", "§8⚔ 箭雨伤害: 6/箭", "§e✦ 弓箭无法掉落", "", "§a▼ 点击召唤BOSS", "§7(请在空旷处召唤)"));
        skKing.setItemMeta(skMeta);
        bossList.setItem(32, skKing);

        ItemStack wraith = new ItemStack(Material.ENDER_EYE);
        ItemMeta wraithMeta = wraith.getItemMeta();
        wraithMeta.setDisplayName("§5■ 虚空幽魂");
        wraithMeta.setLore(Arrays.asList(
                "§7来自虚空的远古凋灵骷髅，",
                "§7掌控着末地传送门的力量。",
                "",
                "§c❤ 生命值: 800",
                "§5✦ 双阶段战斗 (50%进入P2)",
                "§5✦ 6种攻击技能: 虚空弹/末影脉冲/虚空裂隙/暗影分身/终末裁决/虚空吸取",
                "§5✦ 每7.5-12.5秒随机传送",
                "§e⚡ 火焰/雷电触发元素反应: 双倍伤害+50%暴露核心",
                "",
                "§a▼ 点击召唤BOSS",
                "§7(请在空旷处召唤)"
        ));
        wraith.setItemMeta(wraithMeta);
        bossList.setItem(34, wraith);

        ItemStack illusioner = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta illusionerMeta = illusioner.getItemMeta();
        illusionerMeta.setDisplayName("§d■ 幻术师");
        illusionerMeta.setLore(Arrays.asList(
                "§7掌控幻术的神秘灾厄村民，",
                "§7手持力量X冲击III神弓。",
                "",
                "§c❤ 生命值: 500",
                "§d✦ 血量40%时召唤4名幻术护卫",
                "§d✦ 血量20%时降下5秒箭雨",
                "",
                "§a▼ 点击召唤BOSS",
                "§7(请在空旷处召唤)"
        ));
        illusioner.setItemMeta(illusionerMeta);
        bossList.setItem(22, illusioner);
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

        if (name.contains("雪人王")) {
            if (CryoRegisvine.isAlive()) {
                Location loc = CryoRegisvine.getBossLocation();
                if (loc != null) {
                    player.teleport(loc);
                    player.sendMessage("§e雪人王尚未被击败，已传送至BOSS位置");
                }
                player.closeInventory();
                return;
            }
            player.closeInventory();
            player.sendMessage("§b◆ 雪人王已降临！");
            CryoRegisvine.spawnBoss(player);
        } else if (name.contains("烈焰领主")) {
            if (PyroRegisvine.isAlive()) {
                Location loc = PyroRegisvine.getBossLocation();
                if (loc != null) {
                    player.teleport(loc);
                    player.sendMessage("§e烈焰领主尚未被击败，已传送至BOSS位置");
                }
                player.closeInventory();
                return;
            }
            player.closeInventory();
            player.sendMessage("§c◆ 烈焰领主已降临！");
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
        } else if (name.contains("僵尸巨人")) {
            if (ZombieGiantBoss.isAlive()) {
                Location loc = ZombieGiantBoss.getBossLocation();
                if (loc != null) {
                    player.teleport(loc);
                    player.sendMessage("§e僵尸巨人尚未被击败，已传送至BOSS位置");
                }
                player.closeInventory();
                return;
            }
            player.closeInventory();
            player.sendMessage("§4◆ 僵尸巨人苏醒了！");
            ZombieGiantBoss.spawnBoss(player);
        } else if (name.contains("小僵尸Double")) {
            if (BabyZombieDoubleBoss.isAlive()) {
                Location loc = BabyZombieDoubleBoss.getBossLocation();
                if (loc != null) {
                    player.teleport(loc);
                    player.sendMessage("§e小僵尸Double尚未被击败，已传送至BOSS位置");
                }
                player.closeInventory();
                return;
            }
            player.closeInventory();
            player.sendMessage("§5◆ 小僵尸Double出现了！");
            BabyZombieDoubleBoss.spawnBoss(player);
        } else if (name.contains("宝藏守护者")) {
            if (TreasureGuardianBoss.isAlive()) {
                Location loc = TreasureGuardianBoss.getBossLocation();
                if (loc != null) {
                    player.teleport(loc);
                    player.sendMessage("§e宝藏守护者尚未被击败，已传送至BOSS位置");
                }
                player.closeInventory();
                return;
            }
            player.closeInventory();
            TreasureGuardianBoss.spawnBoss(player);
            player.sendMessage("§6◆ 宝藏守护者出现了！");
        } else if (name.contains("骷髅王")) {
            if (SkeletonKing.isAlive()) {
                Location loc = SkeletonKing.getBossLocation();
                if (loc != null) { player.teleport(loc); return; }
            }
            player.closeInventory();
            SkeletonKing.spawnBoss(player);
            player.sendMessage("§6◆ 骷髅王出现了！");
        } else if (name.contains("虚空幽魂")) {
            if (VoidWraith.isAlive()) {
                Location loc = VoidWraith.getBossLocation();
                if (loc != null) { player.teleport(loc); return; }
            }
            player.closeInventory();
            VoidWraith.spawnBoss(player);
            player.sendMessage("§5◆ 虚空幽魂从虚空中降临！");
        } else if (name.contains("幻术师")) {
            if (IllusionerBoss.isAlive()) {
                Location loc = IllusionerBoss.getBossLocation();
                if (loc != null) {
                    player.teleport(loc);
                    player.sendMessage("§e幻术师尚未被击败，已传送至BOSS位置");
                }
                player.closeInventory();
                return;
            }
            player.closeInventory();
            player.sendMessage("§d◆ 幻术师已降临！");
            IllusionerBoss.spawnBoss(player);
        }
    }
}
