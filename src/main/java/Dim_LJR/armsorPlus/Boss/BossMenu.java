package Dim_LJR.armsorPlus.Boss;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.EnchantUtil;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.*;

// BOSS清单 GUI + 全局BOSS战斗系统。
// 管理BOSS召唤界面、躯干部位伤害重定向、元素反应逻辑。
public class BossMenu implements Listener {

    private static Inventory bossList;

    // ========================================================================
    // BOSS 战斗跟踪系统
    // ========================================================================

    public enum BossType { CRYO, PYRO, SLIME, BABY_ZOMBIE_DOUBLE, TREASURE_GUARDIAN, SKELETON_KING, ILLUSIONER, DESERT_CAMEL, SHADOW_WARRIOR, LAVA_DUO, PHANTOM_VEX, VILLAGE_SQUAD }

    // 身体部位实体 -> BOSS类型
    public static final Map<UUID, BossType> BOSS_BODY_PARTS = new HashMap<>();
    // 核心部位实体 -> BOSS类型
    public static final Map<UUID, BossType> BOSS_CORE_PARTS = new HashMap<>();

    private static final Map<BossType, Double> bossHealth = new HashMap<>();
    private static final Map<BossType, Double> bossMaxHealth = new HashMap<>();
    private static final Map<BossType, LivingEntity> bossEntities = new HashMap<>();
    private static final Map<BossType, BossBar> bossBars = new HashMap<>();
    private static final Map<BossType, Set<UUID>> bossAllStands = new HashMap<>();
    private static final Map<BossType, String> bossBarBaseTitles = new HashMap<>();
    // 同队实体(骑士/坐骑/小弟等未注册为部位但属于本BOSS的实体) -> BOSS类型, 用于取消友军伤害
    private static final Map<UUID, BossType> bossFriendlies = new HashMap<>();

    // ========================================================================
    // 注册 / 注销 API
    // ========================================================================

    // 注册BOSS实体与血条
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

    // 注册身体部位实体
    public static void registerBodyStand(BossType type, UUID entityId) {
        BOSS_BODY_PARTS.put(entityId, type);
        bossAllStands.computeIfAbsent(type, k -> new HashSet<>()).add(entityId);
    }

    // 注册核心部位实体
    public static void registerCoreStand(BossType type, UUID entityId) {
        BOSS_CORE_PARTS.put(entityId, type);
        bossAllStands.computeIfAbsent(type, k -> new HashSet<>()).add(entityId);
    }

    // 将核心实体从核心降级为普通身体部位
    public static void downgradeCoreToBody(BossType type, UUID entityId) {
        BOSS_CORE_PARTS.remove(entityId);
        BOSS_BODY_PARTS.put(entityId, type);
    }

    // 将实体从普通身体升级为核心
    public static void upgradeBodyToCore(BossType type, UUID entityId) {
        BOSS_BODY_PARTS.remove(entityId);
        BOSS_CORE_PARTS.put(entityId, type);
    }

    // 获取BOSS当前血量
    public static double getBossHealth(BossType type) {
        return bossHealth.getOrDefault(type, 0.0);
    }

    // 获取BOSS最大血量
    public static double getBossMaxHealth(BossType type) {
        return bossMaxHealth.getOrDefault(type, 1.0);
    }

    // 同步实体实际血量到追踪系统 (幻术师等使用原生血量的Boss)
    public static void syncBossHealth(BossType type, double health, double maxHealth) {
        bossHealth.put(type, health);
        bossMaxHealth.put(type, maxHealth);
    }

    // 更新Boss追踪的主实体 (双实体Boss在坐骑死亡后切换到骑士)
    public static void setBossEntity(BossType type, LivingEntity entity) {
        bossEntities.put(type, entity);
    }

    // 对BOSS造成伤害 (返回true表示BOSS死亡)
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
                case BABY_ZOMBIE_DOUBLE -> BabyZombieDoubleBoss.onDeath();
                case TREASURE_GUARDIAN -> TreasureGuardianBoss.onDeath();
                case SKELETON_KING -> SkeletonKing.onDeath();
                case ILLUSIONER -> IllusionerBoss.onDeath();
                case DESERT_CAMEL -> DesertCamelBoss.onDeath();
                case SHADOW_WARRIOR -> PlayerBoss.onDeath();
                case LAVA_DUO -> LavaDuoBoss.onDeath();
                case PHANTOM_VEX -> PhantomVexBoss.onDeath();
                case VILLAGE_SQUAD -> VillageSquad.onDeath();
            }
            return true;
        }
        return false;
    }

    // 清理BOSS所有跟踪数据
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
        bossFriendlies.entrySet().removeIf(e -> e.getValue() == type);
    }

    // 注册同队实体: 取消它与本BOSS其他实体之间的伤害 (不影响玩家攻击它)
    public static void registerFriendlyEntity(BossType type, Entity entity) {
        bossFriendlies.put(entity.getUniqueId(), type);
    }

    // 更新BOSS血条: 同步血量进度/标题 + 可见范围 (150格)
    public static void updateBossBar(BossType type) {
        BossBar bar = bossBars.get(type);
        LivingEntity entity = bossEntities.get(type);
        if (bar == null || entity == null || entity.isDead()) return;

        Double health = bossHealth.get(type);
        Double maxHp = bossMaxHealth.get(type);
        if (health != null && maxHp != null && maxHp > 0) {
            bar.setProgress(Math.min(1.0, Math.max(0.0, health / maxHp)));
            String base = bossBarBaseTitles.get(type);
            if (base != null && type != BossType.CRYO) {
                bar.setTitle(base + " §7" + Math.round(health) + "/" + Math.round(maxHp));
            }
        }

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
        // 爆炸附加伤害的damage()不再重复放大
        if (explosionBonusActive) return;

        Entity damaged = event.getEntity();
        UUID id = damaged.getUniqueId();

        // 同一BOSS的同队实体之间不互相伤害 (铁骑双雄骷髅/僵尸, 熔岩双王岩浆怪/烈焰人/小弟)
        BossType damagedType = lookupBossType(id);
        if (damagedType != null) {
            Entity damager = event.getDamager();
            if (damager instanceof Projectile proj) {
                ProjectileSource src = proj.getShooter();
                if (src instanceof Entity) damager = (Entity) src;
            }
            if (lookupBossType(damager.getUniqueId()) == damagedType) {
                event.setCancelled(true);
                return;
            }
        }

        // 幻翼/恼鬼: 免疫远程伤害 (箭/三叉戟/火焰弹等投射物), 近战保留原版血量
        if (damagedType == BossType.PHANTOM_VEX) {
            if (event.getDamager() instanceof Projectile) {
                event.setCancelled(true);
            }
            // 被击中时发光2秒 (光灵效果): 隐身BOSS被命中后短暂显形
            if (damaged instanceof LivingEntity le) {
                le.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 0, false, false));
            }
            return;
        }

        // 熔岩双王·烈焰人: 火焰弹直击 — 火焰弹(目标无抗火)→伤害×9(小弟×4.5)+点燃; 爆炸弹(目标有抗火)→伤害×4.5(小弟×2.25)
        if (damaged instanceof Player && event.getDamager() instanceof Projectile proj) {
            ProjectileSource src = proj.getShooter();
            if (src instanceof Entity srcEntity) {
                boolean main = findBossTypeByEntity(srcEntity.getUniqueId()) == BossType.LAVA_DUO;
                boolean minion = LavaDuoBoss.isMinion(srcEntity.getUniqueId());
                if (main || minion) {
                    String type = proj.getPersistentDataContainer().get(
                            LavaDuoBoss.FIREBALL_KEY, PersistentDataType.STRING);
                    if ("fire".equals(type)) {
                        event.setDamage(event.getDamage() * (main ? 9 : 4.5)); // 火焰弹: 伤害×9
                        ((Player) damaged).setFireTicks(240); // 覆盖原版100tick, 长时燃烧
                    } else {
                        event.setDamage(event.getDamage() * (main ? 4.5 : 2.25)); // 爆炸弹: 伤害×4.5
                    }
                    return;
                }
            }
        }

        // 熔岩双王·烈焰人: 接触伤害 ×10 (本体触碰玩家; 小弟×5)
        if (damaged instanceof Player && event.getDamager() instanceof LivingEntity dmgEnt) {
            if (findBossTypeByEntity(dmgEnt.getUniqueId()) == BossType.LAVA_DUO) {
                event.setDamage(event.getDamage() * 10);
                return;
            } else if (LavaDuoBoss.isMinion(dmgEnt.getUniqueId())) {
                event.setDamage(event.getDamage() * 5);
                return;
            }
        }

        // 幻翼/恼鬼: 攻击玩家 — 两者均固定2点强制穿透; 幻翼附剧毒/失明, 恼鬼附凋零/失明
        if (damaged instanceof Player victim && event.getDamager() instanceof LivingEntity dmgEnt) {
            if (lookupBossType(dmgEnt.getUniqueId()) == BossType.PHANTOM_VEX) {
                event.setCancelled(true);
                // 不带directEntity: 重新触发的是纯EntityDamageEvent, 不会再次进入本(实体伤害)处理器, 避免递归
                // GENERIC_KILL 穿透: 无视护甲/保护附魔/抗性效果/无敌帧 (与/enchant穿透同源)
                // 套 PIERCING_ACTIVE: 让插件自身防护附魔(保护PRO/影避/不灭/幸存等)也无法减免/闪避, 才是真正的强制穿透
                UUID vId = victim.getUniqueId();
                if (!EnchantUtil.PIERCING_ACTIVE.contains(vId)) {
                    EnchantUtil.PIERCING_ACTIVE.add(vId);
                    try {
                        victim.damage(2.0, DamageSource.builder(DamageType.GENERIC_KILL).build());
                    } finally {
                        EnchantUtil.PIERCING_ACTIVE.remove(vId);
                    }
                }
                // 命中附加效果 (5秒): 仅对该BOSS的攻击生效 (由 lookupBossType==PHANTOM_VEX 保障)
                if (dmgEnt instanceof Phantom) {
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON,  100, 0, false, true));
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0, false, true));
                } else {
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,    100, 0, false, true));
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0, false, true));
                }
                return;
            }
        }

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
        // 原生实体薄封装: 史莱姆王/熔岩双王/小僵尸Double/宝藏守护者/沙漠骆驼/影武者 使用原生血量+原生AI, 不重定向伤害
        if (type == BossType.SLIME || type == BossType.LAVA_DUO
                || type == BossType.BABY_ZOMBIE_DOUBLE
                || type == BossType.TREASURE_GUARDIAN || type == BossType.DESERT_CAMEL
                || type == BossType.SHADOW_WARRIOR) {
            return;
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
            damageBoss(type, dmg * 2.0, damager);
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

    // 综合查询: 身体部位 → 核心部位 → 主实体 → 同队实体
    private static BossType lookupBossType(UUID id) {
        BossType t = BOSS_BODY_PARTS.get(id);
        if (t != null) return t;
        t = BOSS_CORE_PARTS.get(id);
        if (t != null) return t;
        t = findBossTypeByEntity(id);
        if (t != null) return t;
        return bossFriendlies.get(id);
    }

    // 火焰弹命中玩家的爆炸特效 (纯视觉效果, 伤害由 explosionDamage 负责)
    private static void explosionEffect(Location loc, float size) {
        loc.add(0, 0.5, 0);
        loc.getWorld().spawnParticle(Particle.EXPLOSION, loc, 4, size, size, size, 0);
        loc.getWorld().spawnParticle(Particle.FLAME, loc, 20, size, size, size, 0.05);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.7f);
    }

    // 爆炸附加伤害是否正在进行 (避免爆炸AoE的damage()再次被上面的放大逻辑重复放大)
    private static boolean explosionBonusActive = false;

    // 烈焰人火焰弹命中/落地 → 按弹种结算:
    //   爆炸弹(目标有抗火) → 命中点爆炸 (主烈焰人15点/小弟5点, 范围AoE, 无视抗火)
    //   火焰弹(目标无抗火) → 仅火焰伤害: 点燃命中及附近玩家, 长时燃烧, 不产生爆炸
    @EventHandler
    public void onBlazeFireballHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof SmallFireball fb)) return;
        ProjectileSource src = fb.getShooter();
        if (!(src instanceof Entity srcEntity)) return;

        boolean main = findBossTypeByEntity(srcEntity.getUniqueId()) == BossType.LAVA_DUO;
        boolean minion = LavaDuoBoss.isMinion(srcEntity.getUniqueId());
        if (!main && !minion) return;

        String type = fb.getPersistentDataContainer().get(
                LavaDuoBoss.FIREBALL_KEY, PersistentDataType.STRING);
        Location hit = fb.getLocation();

        if ("fire".equals(type)) {
            // 仅火焰伤害的火焰弹: 点燃命中玩家及附近玩家 (燃烧无视护甲, 目标无抗火时生效)
            int fireTicks = 240;
            for (Entity e : hit.getWorld().getNearbyEntities(hit, 3, 3, 3)) {
                if (e instanceof Player p && !p.isDead()) p.setFireTicks(fireTicks);
            }
        } else {
            // 爆炸弹: 命中点爆炸 (无视抗火)
            double baseDmg = main ? 15 : 5;
            double radius = main ? 4.0 : 3.0;
            explosionDamage(hit, srcEntity, baseDmg, radius);
            explosionEffect(hit, main ? 1.5f : 1.0f);
        }
    }

    // 命中点真实爆炸: 对范围内玩家造成爆炸伤害 (距离衰减) + 轻微击退
    private static void explosionDamage(Location center, Entity source, double baseDmg, double radius) {
        World world = center.getWorld();
        if (world == null) return;
        explosionBonusActive = true;
        try {
            for (Entity e : world.getNearbyEntities(center, radius, radius, radius)) {
                if (!(e instanceof Player p) || p.isDead()) continue;
                double dist = p.getLocation().distance(center);
                if (dist > radius) continue;
                double falloff = 1.0 - (dist / radius);
                double dmg = Math.max(1.0, baseDmg * Math.max(0.15, falloff));
                p.damage(dmg, source);
                // 爆炸击退: 远离爆炸中心
                Vector away = p.getLocation().toVector().subtract(center.toVector());
                if (away.lengthSquared() > 0.01) {
                    p.setVelocity(away.normalize().multiply(0.6 * (0.3 + falloff))
                            .setY(0.35 + falloff * 0.5));
                }
            }
        } finally {
            explosionBonusActive = false;
        }
    }

    // ========================================================================
    // 环境伤害拦截 (坠落/火焰/窒息等)
    // ========================================================================

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBossEnvironmentalDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) return;
        // 放行 /kill (generic_kill伤害源): 环境保护只挡环境伤害, 不能把管理员的kill也拦掉,
        // 否则 /kill @e 无法清除BOSS (实体死亡后由各BOSS的AI检测并触发onDeath清理)
        DamageSource ds = event.getDamageSource();
        if (ds != null && ds.getDamageType() == DamageType.GENERIC_KILL) return;
        if (lookupBossType(event.getEntity().getUniqueId()) != null) {
            event.setCancelled(true);
        }
    }

    // ========================================================================
    // 传送门拦截 (BOSS不能穿过下界/末地传送门)
    // ========================================================================

    @EventHandler
    public void onBossPortal(EntityPortalEvent event) {
        Entity entity = event.getEntity();
        // 拦截所有已追踪的BOSS本体/身体部位/核心实体传送
        if (BOSS_BODY_PARTS.containsKey(entity.getUniqueId())
                || BOSS_CORE_PARTS.containsKey(entity.getUniqueId())
                || findBossTypeByEntity(entity.getUniqueId()) != null) {
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
                "",
                "§a▼ 左键召唤BOSS / 右键传送"
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
                "§a▼ 左键召唤BOSS / 右键传送"
        ));
        slime.setItemMeta(slimeMeta);
        bossList.setItem(11, slime);

        ItemStack pyro = new ItemStack(Material.MAGMA_BLOCK);
        ItemMeta pyroMeta = pyro.getItemMeta();
        pyroMeta.setDisplayName("§c■ 烈焰领主");
        pyroMeta.setLore(Arrays.asList(
                "§7来自层岩巨渊的远古植物，",
                "§7拥有操控火元素的力量。",
                "",
                "§c❤ 生命值: 700",
                "§c🔥 核心: 烈焰人",
                "§e✦ 攻击树状盔甲架转移伤害至核心",
                "",
                "§a▼ 左键召唤BOSS / 右键传送"
        ));
        pyro.setItemMeta(pyroMeta);
        bossList.setItem(12, pyro);


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
                "§a▼ 左键召唤BOSS / 右键传送"
        ));
        treasure.setItemMeta(treasureMeta);
        bossList.setItem(13, treasure);

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
                "§a▼ 左键召唤BOSS / 右键传送"
        ));
        doubleZombie.setItemMeta(doubleMeta);
        bossList.setItem(14, doubleZombie);

        ItemStack skKing = new ItemStack(Material.BOW);
        ItemMeta skMeta = skKing.getItemMeta();
        skMeta.setDisplayName("§8■ 骷髅王");
        skMeta.setLore(Arrays.asList("§7手持力量X神弓的骷髅王者，", "§7每隔10秒召唤箭雨。", "", "§c❤ 生命值: 600", "§8⚔ 箭雨伤害: 6/箭", "§e✦ 弓箭无法掉落", "", "§a▼ 左键召唤BOSS / 右键传送"));
        skKing.setItemMeta(skMeta);
        bossList.setItem(15, skKing);


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
                "§a▼ 左键召唤BOSS / 右键传送"
        ));
        illusioner.setItemMeta(illusionerMeta);
        bossList.setItem(16, illusioner);

        ItemStack desertCamel = new ItemStack(Material.ZOMBIE_HORSE_SPAWN_EGG);
        ItemMeta desertCamelMeta = desertCamel.getItemMeta();
        desertCamelMeta.setDisplayName("§2■ 铁骑双雄");
        desertCamelMeta.setLore(Arrays.asList(
                "§7沙漠中的移动死亡堡垒，",
                "§7僵尸骑士驾驶僵尸战马冲锋，",
                "§7骷髅射手骑在毒蛛上远程支援。",
                "",
                "§c❤ 坐骑: 125×2 / 僵尸: 150 / 骷髅: 100",
                "§2⚔ 僵尸: 全套下界合金 + 基础伤害25·锋利70·击退X·贯穿III长矛",
                "§7✦ 骷髅: 全套下界合金 + 力量85·弹道V·冲击III·火矢I神弓",
                "§b⚡ 双坐骑: 速度VII + 抗性IV",
                "",
                "§a▼ 左键召唤BOSS / 右键传送"
        ));
        desertCamel.setItemMeta(desertCamelMeta);
        bossList.setItem(19, desertCamel);

        ItemStack shadowWarrior = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta shadowMeta = shadowWarrior.getItemMeta();
        shadowMeta.setDisplayName("§8■ ShadowWarrior");
        shadowMeta.setLore(Arrays.asList(
                "§7以召唤者皮肤为模型的幻影武者，",
                "§7手持钻石剑，来去如影。",
                "",
                "§c❤ 生命值: 300",
                "§8⚔ 近战伤害: 12 / 影遁 15 / 幻影斩 10",
                "§e✦ 使用真实玩家皮肤渲染 (自定义皮肤Boss)",
                "",
                "§a▼ 左键召唤BOSS / 右键传送"
        ));
        shadowWarrior.setItemMeta(shadowMeta);
        bossList.setItem(20, shadowWarrior);

        ItemStack lavaDuo = new ItemStack(Material.MAGMA_BLOCK);
        ItemMeta lavaMeta = lavaDuo.getItemMeta();
        lavaMeta.setDisplayName("§c■ 熔岩双王");
        lavaMeta.setLore(Arrays.asList(
                "§7烈焰人与岩浆史莱姆王同时降临，",
                "§7二者都击败才算获胜。",
                "",
                "§e❤ 烈焰人: 200",
                "§e🔥 火焰弹/接触伤害: 原版10倍",
                "§c❤ 岩浆史莱姆王: 750",
                "§c⚡ 重锤粉碎攻击 · 半血激怒分裂",
                "",
                "§a▼ 左键召唤BOSS / 右键传送"
        ));
        lavaDuo.setItemMeta(lavaMeta);
        bossList.setItem(21, lavaDuo);

        ItemStack pv = new ItemStack(Material.PHANTOM_SPAWN_EGG);
        ItemMeta pvMeta = pv.getItemMeta();
        pvMeta.setDisplayName("§9■ 幻翼·恼鬼");
        pvMeta.setLore(Arrays.asList(
                "§7幻翼与恼鬼同时降临，",
                "§7二者都击败才算获胜。",
                "",
                "§c❤ 幻翼: 125",
                "§c❤ 恼鬼: 125",
                "§9⚔ 幻翼: 固定2点 穿透 + 剧毒/失明",
                "§d⚔ 恼鬼: 固定2点 穿透 + 凋零/失明",
                "§e✦ 隐身 · 免疫远程/火焰/中毒/凋零",
                "",
                "§a▼ 左键召唤BOSS / 右键传送"
        ));
        pv.setItemMeta(pvMeta);
        bossList.setItem(22, pv);

        ItemStack villageSquad = new ItemStack(Material.VILLAGER_SPAWN_EGG);
        ItemMeta vsMeta = villageSquad.getItemMeta();
        vsMeta.setDisplayName("§a■ 村民卫队");
        vsMeta.setLore(Arrays.asList(
                "§7村庄的精锐卫队，",
                "§7队长率领10名卫兵一同出战。",
                "",
                "§c❤ 队长: 40 / 卫兵: 30×10",
                "§a⚔ 队长: 铁剑(8伤) + 金胸甲",
                "§7⚔ 卫兵: 铁剑(7伤) + 保护II铁头盔",
                "§e✦ 全部击败才算获胜",
                "",
                "§a▼ 左键召唤BOSS / 右键传送"
        ));
        villageSquad.setItemMeta(vsMeta);
        bossList.setItem(23, villageSquad);
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
        player.closeInventory();

        // ---- 右键: 传送至已存活BOSS位置 ----
        if (event.isRightClick()) {
            Location loc = null;
            String bossLabel = null;
            if (name.contains("雪人王")       && CryoRegisvine.isAlive())         { loc = CryoRegisvine.getBossLocation();         bossLabel = "雪人王"; }
            else if (name.contains("烈焰领主") && PyroRegisvine.isAlive())        { loc = PyroRegisvine.getBossLocation();         bossLabel = "烈焰领主"; }
            else if (name.contains("史莱姆王") && SlimeBoss.isAlive())            { loc = SlimeBoss.getBossLocation();             bossLabel = "史莱姆王"; }
            else if (name.contains("小僵尸")   && BabyZombieDoubleBoss.isAlive()) { loc = BabyZombieDoubleBoss.getBossLocation();  bossLabel = "小僵尸Double"; }
            else if (name.contains("宝藏守护者") && TreasureGuardianBoss.isAlive()) { loc = TreasureGuardianBoss.getBossLocation(); bossLabel = "宝藏守护者"; }
            else if (name.contains("骷髅王")   && SkeletonKing.isAlive())         { loc = SkeletonKing.getBossLocation();          bossLabel = "骷髅王"; }
            else if (name.contains("幻术师")   && IllusionerBoss.isAlive())       { loc = IllusionerBoss.getBossLocation();        bossLabel = "幻术师"; }
            else if (name.contains("铁骑双雄") && DesertCamelBoss.isAlive())      { loc = DesertCamelBoss.getBossLocation();       bossLabel = "铁骑双雄"; }
            else if (name.contains("Shadow")  && PlayerBoss.isAlive())            { loc = PlayerBoss.getBossLocation();            bossLabel = "ShadowWarrior"; }
            else if (name.contains("熔岩双王") && LavaDuoBoss.isAlive())          { loc = LavaDuoBoss.getBossLocation();           bossLabel = "熔岩双王"; }
            else if (name.contains("幻翼")    && PhantomVexBoss.isAlive())        { loc = PhantomVexBoss.getBossLocation();        bossLabel = "幻翼·恼鬼"; }
            else if (name.contains("村民卫队") && VillageSquad.isAlive())          { loc = VillageSquad.getBossLocation();          bossLabel = "村民卫队"; }
            if (loc != null) {
                player.teleport(loc);
                player.sendMessage("§e已传送至" + bossLabel + "的位置");
            } else {
                player.sendMessage("§7该BOSS当前未存活，左键可召唤");
            }
            return;
        }

        // ---- 左键: 召唤BOSS (不限数量) ----
        if (name.contains("雪人王")) {
            player.sendMessage("§b◆ 雪人王已降临！");
            CryoRegisvine.spawnBoss(player);
        } else if (name.contains("烈焰领主")) {
            player.sendMessage("§c◆ 烈焰领主已降临！");
            PyroRegisvine.spawnBoss(player);
        } else if (name.contains("史莱姆王")) {
            player.sendMessage("§a◆ 史莱姆王已降临！");
            SlimeBoss.spawnBoss(player);
        } else if (name.contains("小僵尸")) {
            player.sendMessage("§5◆ 小僵尸Double出现了！");
            BabyZombieDoubleBoss.spawnBoss(player);
        } else if (name.contains("宝藏守护者")) {
            player.sendMessage("§6◆ 宝藏守护者出现了！");
            TreasureGuardianBoss.spawnBoss(player);
        } else if (name.contains("骷髅王")) {
            player.sendMessage("§6◆ 骷髅王出现了！");
            SkeletonKing.spawnBoss(player);
        } else if (name.contains("幻术师")) {
            player.sendMessage("§d◆ 幻术师已降临！");
            IllusionerBoss.spawnBoss(player);
        } else if (name.contains("铁骑双雄")) {
            player.sendMessage("§2◆ 铁骑双雄已降临！");
            DesertCamelBoss.spawnBoss(player);
        } else if (name.contains("Shadow")) {
            player.sendMessage("§8◆ ShadowWarrior以你的形象降临了！");
            PlayerBoss.spawnBoss(player);
        } else if (name.contains("熔岩双王")) {
            player.sendMessage("§c◆ 熔岩双王已降临！");
            LavaDuoBoss.spawnBoss(player);
        } else if (name.contains("幻翼")) {
            player.sendMessage("§b◆ 幻翼与恼鬼已降临！");
            PhantomVexBoss.spawnBoss(player);
        } else if (name.contains("村民卫队")) {
            player.sendMessage("§a◆ 村民卫队已降临！");
            VillageSquad.spawnBoss(player);
        }
    }
}
