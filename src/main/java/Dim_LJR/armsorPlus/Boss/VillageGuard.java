package Dim_LJR.armsorPlus.Boss;

import Dim_LJR.armsorPlus.NamespaceKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

// 村民卫兵 —— 玩家模型生物(非BOSS), 收录在生物菜单, 可重复召唤(多个并存)。
// 物理本体是隐形盔甲架(ModelBoss): setVelocity追击, 假玩家每tick同步到盔甲架位置;
// 穿戴保护II铁头盔 + 主手普通铁剑: 装备穿在物理本体上, 铁头盔额外提供 ARMOR 减伤,
// 让"保护II"实际生效(假玩家模型渲染不支持显示装备, 见下注)。
// 目标选择/追击/近战复用 VillageCaptain 的简单模式, 无血条/无全服广播。
public class VillageGuard implements Listener {

    private static final double MAX_HEALTH = 30;      // 铁盔卫兵比村民队长(20)更耐打
    private static final int FOLLOW_RANGE = 32;
    private static final String BOSS_NAME = "VillageGuard";
    private static final double MELEE_DAMAGE = 7.0;   // 普通铁剑 ≈ 7 点伤害
    private static final double ARMOR_POINTS = 4.0;   // 铁头盔(2点) + 保护II(约2点)

    // 支持多只并存: 每次从生物菜单点击都召唤一只新的, 各自管理自己的本体/AI。
    private static final List<VillageGuard> ACTIVE = new ArrayList<>();
    private static final Random RANDOM = new Random();

    private FakePlayer body;
    private World world;
    private BukkitTask aiTask;

    // ========================================================================
    // 召唤
    // ========================================================================

    public static void spawn(Player summoner) {
        World w = summoner.getWorld();
        Location loc = summoner.getLocation();

        VillageGuard guard = new VillageGuard();
        guard.world = w;

        // ---- 统一创建 (附属插件优先, 本地PE兜底) ----
        guard.body = FakePlayerProvider.createVillageGuard(w, loc, BOSS_NAME,
                "§7■ 村民卫兵 §7Lv.30", loc.getYaw(), MAX_HEALTH);
        if (guard.body == null) {
            summoner.sendMessage("§c村民卫兵: 渲染初始化失败");
            NamespaceKey.Keys.getplugin.getLogger().warning("村民卫兵渲染初始化失败");
            return;
        }
        guard.body.setAiType(AiType.RUN); // 卫兵: RUN 追击(更快 + 更近停止)
        if (!FakePlayerProvider.hasBackend()) {
            summoner.sendMessage("§e未检测到 PacketEvents 与附属插件, 村民卫兵将以隐形状态出现(仅盔甲架本体, 伤害仍有效)");
        }

        guard.equipLoadout(); // 铁头盔(保护II) + 铁剑
        Enmity.registerBossBody(guard.body.getBody()); // 供挑拨木棍右键选中

        // ---- 挂游戏逻辑回调 ----
        guard.body.setOnAttack(p -> guard.body.applyDamage(p, ModelBoss.computeDamage(p), true));
        guard.body.setOnDeath(killer -> guard.onDeath());

        ACTIVE.add(guard);
        guard.startAI();

        summoner.closeInventory();
        summoner.sendMessage("§7村民卫兵: 已召唤!");
    }

    // 铁头盔(保护II) + 普通铁剑。
    // 统一走 FakePlayer.setEquipment: 附属方案发包渲染装备(视觉), 本地PE方案穿到盔甲架真实槽(原生减伤);
    // 铁头盔的减伤通过本体 ARMOR 属性直接生效(受击伤害计算会扣掉护甲减伤)。
    private void equipLoadout() {
        if (body == null || body.getBody() == null) return;

        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        ItemMeta helmetMeta = helmet.getItemMeta();
        helmetMeta.addEnchant(Enchantment.PROTECTION, 2, true);
        helmet.setItemMeta(helmetMeta);

        body.setEquipment(helmet, null, null, null, new ItemStack(Material.IRON_SWORD));

        // 保护II铁头盔的真实减伤: 本体 ARMOR 护甲值 (盔甲架受击伤害计算会扣护甲)
        AttributeInstance armor = body.getBody().getAttribute(Attribute.ARMOR);
        if (armor != null) armor.setBaseValue(ARMOR_POINTS);
    }

    // ========================================================================
    // AI: 追击 + 近战 (复用村民队长简单模式)
    // ========================================================================

    private void startAI() {
        aiTask = new BukkitRunnable() {
            int attackCooldown = 0;
            int idleTicks = 0;

            @Override
            public void run() {
                try {
                    if (body == null || !body.isValid()) {
                        cleanup();
                        cancel();
                        return;
                    }
                    // 盔甲架被传送门带到其他世界 → 直接消失
                    if (!body.getLocation().getWorld().equals(world)) {
                        despawn();
                        cancel();
                        return;
                    }
                    LivingEntity target = Enmity.getEnemy(body.getBody());
                    if (target == null) target = findNearestTarget();
                    if (target == null) {
                        body.setTarget(null);
                        if (++idleTicks > 3000) {
                            despawn();
                            cancel();
                        }
                        return;
                    }
                    idleTicks = 0;

                    // ---- 追击: ModelBoss 每tick setVelocity 朝目标推进 ----
                    body.setTarget(target);
                    double dist = body.getLocation().distance(target.getLocation());

                    // ---- 近战 (普通铁剑) ----
                    if (attackCooldown > 0) attackCooldown--;
                    if (attackCooldown <= 0 && dist <= 3.5) {
                        body.rotateTo(target);
                        body.swing();
                        world.playSound(body.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);
                        if (!target.isDead()) {
                            // 以盔甲架本体作为伤害来源, 让被击中的生物能收到"是谁打的它"
                            target.damage(MELEE_DAMAGE, body.getBody());
                            if (target instanceof Mob mob) {
                                mob.setTarget(body.getBody());
                            }
                            target.setVelocity(ModelBoss.safeKnockback(
                                    body.getLocation(), target.getLocation(), 0.7, 0.2));
                        }
                        attackCooldown = 25 + RANDOM.nextInt(25);
                    }
                } catch (Throwable t) {
                    NamespaceKey.Keys.getplugin.getLogger().warning("[村民卫兵] AI循环异常(已忽略): " + t);
                }
            }
        }.runTaskTimer(NamespaceKey.Keys.getplugin, 20L, 2L);
    }

    // 寻找目标: 优先攻击附近敌对生物(怪物), 无怪物时攻击最近的战斗玩家。
    private LivingEntity findNearestTarget() {
        if (body == null || !body.isValid()) return null;
        Location bodyLoc = body.getLocation();
        LivingEntity monster = null;
        LivingEntity player = null;
        double monsterDist = Double.MAX_VALUE;
        double playerDist = Double.MAX_VALUE;
        for (Entity entity : bodyLoc.getWorld().getNearbyEntities(bodyLoc, FOLLOW_RANGE, 10, FOLLOW_RANGE)) {
            if (entity.isDead() || !entity.isValid() || !entity.getWorld().equals(world)) continue;
            double dist = entity.getLocation().distance(bodyLoc);
            if (entity instanceof Monster) {
                if (dist < monsterDist) {
                    monsterDist = dist;
                    monster = (LivingEntity) entity;
                }
            } else if (entity instanceof Player p && BossTargets.isCombatPlayer(p)) {
                if (dist < playerDist) {
                    playerDist = dist;
                    player = p;
                }
            }
        }
        return monster != null ? monster : player;
    }

    // ========================================================================
    // 死亡 & 消失
    // ========================================================================

    private void onDeath() {
        if (body == null) return;
        Location loc = body.getLocation();
        body.remove();

        world.spawnParticle(Particle.CLOUD, loc, 60, 2, 2, 2, 0.3);
        world.spawnParticle(Particle.CRIT, loc, 30, 1, 1, 1, 0.2);
        world.playSound(loc, Sound.ENTITY_PLAYER_DEATH, 1.0f, 0.8f);

        // 掉落: 铁头盔(保护II) + 铁剑 + 绿宝石
        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        ItemMeta helmetMeta = helmet.getItemMeta();
        helmetMeta.addEnchant(Enchantment.PROTECTION, 2, true);
        helmet.setItemMeta(helmetMeta);
        world.dropItemNaturally(loc, helmet);
        world.dropItemNaturally(loc, new ItemStack(Material.IRON_SWORD));
        world.dropItemNaturally(loc, new ItemStack(Material.EMERALD, RANDOM.nextInt(2) + 1));

        cleanup();
    }

    private void despawn() {
        if (body == null) return;
        body.remove();
        cleanup();
    }

    private void cleanup() {
        if (aiTask != null) { aiTask.cancel(); aiTask = null; }
        if (body != null) {
            Enmity.unregisterBossBody(body.getBody());
            body.remove();
            body = null;
        }
        world = null;
        ACTIVE.remove(this);
    }

    // ========================================================================
    // 玩家上线补齐 (让后加入的玩家也能看到村民卫兵)
    // ========================================================================

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        for (VillageGuard g : new ArrayList<>(ACTIVE)) {
            if (g.body == null || g.world == null) continue;
            if (p.getWorld().equals(g.world)) {
                Bukkit.getScheduler().runTaskLater(NamespaceKey.Keys.getplugin,
                        () -> { if (g.body != null && p.isOnline()) g.body.spawnTo(p); }, 20L);
            }
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player p = event.getPlayer();
        for (VillageGuard g : new ArrayList<>(ACTIVE)) {
            if (g.body == null || g.world == null) continue;
            if (p.getWorld().equals(g.world)) {
                Bukkit.getScheduler().runTaskLater(NamespaceKey.Keys.getplugin,
                        () -> { if (g.body != null && p.isOnline()) g.body.spawnTo(p); }, 20L);
            } else {
                g.body.despawnFrom(p);
            }
        }
    }
}
