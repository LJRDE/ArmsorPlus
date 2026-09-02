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
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
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
import java.util.List;
import java.util.Random;

// 村民卫队 —— 1名村民队长(40HP) + 10名村民卫兵(各30HP) 的组合BOSS。
// 收录在BOSS清单, 全局唯一; 所有成员均为玩家模型(FakePlayer), 物理本体为隐形盔甲架。
// 队长死亡后BossBar切换为存活卫兵数; 全部击败后触发总掉落。
public class VillageSquad implements Listener {

    private static final double CAPTAIN_HP = 40;
    private static final double GUARD_HP = 30;
    private static final int GUARD_COUNT = 10;
    private static final int FOLLOW_RANGE = 40;
    private static final double CAPTAIN_DAMAGE = 8.0;
    private static final double GUARD_DAMAGE = 7.0;
    private static final double GUARD_ARMOR = 4.0;

    private static boolean alive = false;
    private static FakePlayer captainBody;
    private static final List<FakePlayer> guards = new ArrayList<>();
    private static World world;
    private static BukkitTask aiTask;
    private static BossBar bossBar;
    private static boolean captainAlive = true;

    private static final Random RANDOM = new Random();

    // ========================================================================
    // 召唤
    // ========================================================================

    public static void spawnBoss(Player summoner) {
        world = summoner.getWorld();
        Location spawnLoc = summoner.getLocation();

        // ---- BossBar ----
        bossBar = Bukkit.createBossBar("§a◆ 村民卫队", BarColor.GREEN, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(1.0);

        // ---- 队长 (40HP, 铁剑+金胸甲) ----
        captainBody = FakePlayerProvider.createVillageCaptain(world, spawnLoc, "VillageCaptain",
                "§a■ 村民队长 §7Lv.40", spawnLoc.getYaw(), CAPTAIN_HP);
        if (captainBody == null) {
            summoner.sendMessage("§c村民卫队渲染初始化失败");
            NamespaceKey.Keys.getplugin.getLogger().warning("村民卫队队长渲染初始化失败");
            cleanup();
            return;
        }
        captainBody.setAiType(AiType.RUN);
        Enmity.registerBossBody(captainBody.getBody());
        captainBody.setEquipment(null, new ItemStack(Material.GOLDEN_CHESTPLATE),
                null, null, new ItemStack(Material.IRON_SWORD));
        captainBody.setOnAttack(p -> captainBody.applyDamage(p, ModelBoss.computeDamage(p), true));
        captainBody.setOnDamage((attacker, remaining) -> {
            bossBar.setProgress(Math.max(0, remaining / CAPTAIN_HP));
            bossBar.setTitle("§a◆ 村民卫队 §7队长 " + Math.round(remaining) + "/" + Math.round(CAPTAIN_HP));
        });

        // ---- 卫兵 x10 (30HP, 保护II铁头盔+铁剑) ----
        for (int i = 0; i < GUARD_COUNT; i++) {
            FakePlayer guard = FakePlayerProvider.createVillageGuard(world, spawnLoc, "VillageGuard",
                    "§7■ 村民卫兵 §7Lv.30", spawnLoc.getYaw(), GUARD_HP);
            if (guard == null) {
                NamespaceKey.Keys.getplugin.getLogger().warning("村民卫兵#" + (i + 1) + "渲染初始化失败, 跳过");
                continue;
            }
            guard.setAiType(AiType.RUN);
            Enmity.registerBossBody(guard.getBody());

            ItemStack helmet = new ItemStack(Material.IRON_HELMET);
            ItemMeta helmetMeta = helmet.getItemMeta();
            helmetMeta.addEnchant(Enchantment.PROTECTION, 2, true);
            helmet.setItemMeta(helmetMeta);
            guard.setEquipment(helmet, null, null, null, new ItemStack(Material.IRON_SWORD));
            AttributeInstance armor = guard.getBody().getAttribute(Attribute.ARMOR);
            if (armor != null) armor.setBaseValue(GUARD_ARMOR);

            guard.setOnAttack(p -> guard.applyDamage(p, ModelBoss.computeDamage(p), true));
            final FakePlayer g = guard;
            g.setOnDeath(killer -> removeMember(g));

            guards.add(guard);
        }

        if (!FakePlayerProvider.hasBackend()) {
            summoner.sendMessage("§e未检测到 PacketEvents 与附属插件, 村民卫队将以隐形状态出现(仅盔甲架本体, 伤害仍有效)");
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§a◆ 村民卫队出现了！(1队长 + " + guards.size() + "卫兵)");
        }

        alive = true;
        captainAlive = true;
        startAI();
    }

    // ========================================================================
    // AI: 统一指挥追击 + 近战
    // ========================================================================

    private static void startAI() {
        aiTask = new BukkitRunnable() {
            int attackCooldown = 0;
            int idleTicks = 0;

            @Override
            public void run() {
                try {
                    if (captainBody == null && guards.isEmpty()) {
                        cleanup();
                        cancel();
                        return;
                    }
                    // 任一存活实体被传送到其他世界 → 全队消失
                    LivingEntity anyBody = captainBody != null ? captainBody.getBody()
                            : (!guards.isEmpty() ? guards.get(0).getBody() : null);
                    if (anyBody != null && !anyBody.getWorld().equals(world)) {
                        despawn();
                        cancel();
                        return;
                    }

                    // 仇怨目标优先
                    LivingEntity target = null;
                    if (captainBody != null && captainBody.isValid()) {
                        target = Enmity.getEnemy(captainBody.getBody());
                    }
                    if (target == null) {
                        for (FakePlayer g : guards) {
                            if (g != null && g.isValid()) {
                                target = Enmity.getEnemy(g.getBody());
                                if (target != null) break;
                            }
                        }
                    }
                    if (target == null) target = findNearestTarget();

                    if (target == null) {
                        setAllTargets(null);
                        if (++idleTicks > 3000) {
                            despawn();
                            cancel();
                        }
                        return;
                    }
                    idleTicks = 0;

                    // 统一下达追击指令
                    setAllTargets(target);
                    if (attackCooldown > 0) attackCooldown--;

                    // ---- 队长近战 ----
                    if (captainBody != null && captainBody.isValid() && attackCooldown <= 0) {
                        double dist = captainBody.getLocation().distance(target.getLocation());
                        if (dist <= 3.5) {
                            captainBody.rotateTo(target);
                            captainBody.swing();
                            world.playSound(captainBody.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);
                            if (!target.isDead()) {
                                target.damage(CAPTAIN_DAMAGE, captainBody.getBody());
                                if (target instanceof Mob mob) mob.setTarget(captainBody.getBody());
                                target.setVelocity(ModelBoss.safeKnockback(
                                        captainBody.getLocation(), target.getLocation(), 0.7, 0.2));
                            }
                            attackCooldown = 25 + RANDOM.nextInt(25);
                        }
                    }

                    // ---- 卫兵近战 ----
                    for (FakePlayer g : new ArrayList<>(guards)) {
                        if (g == null || !g.isValid()) continue;
                        double dist = g.getLocation().distance(target.getLocation());
                        if (dist <= 3.5) {
                            g.rotateTo(target);
                            g.swing();
                            world.playSound(g.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.8f, 1.0f);
                            if (!target.isDead()) {
                                target.damage(GUARD_DAMAGE, g.getBody());
                                if (target instanceof Mob mob) mob.setTarget(g.getBody());
                                target.setVelocity(ModelBoss.safeKnockback(
                                        g.getLocation(), target.getLocation(), 0.7, 0.2));
                            }
                        }
                    }
                } catch (Throwable t) {
                    NamespaceKey.Keys.getplugin.getLogger().warning("[村民卫队] AI循环异常(已忽略): " + t);
                }
            }
        }.runTaskTimer(NamespaceKey.Keys.getplugin, 20L, 2L);
    }

    private static void setAllTargets(LivingEntity target) {
        if (captainBody != null && captainBody.isValid()) captainBody.setTarget(target);
        for (FakePlayer g : guards) {
            if (g != null && g.isValid()) g.setTarget(target);
        }
    }

    // 优先攻击敌对生物, 其次战斗玩家
    private static LivingEntity findNearestTarget() {
        LivingEntity ref = captainBody != null ? captainBody.getBody()
                : (!guards.isEmpty() ? guards.get(0).getBody() : null);
        if (ref == null) return null;

        Location loc = ref.getLocation();
        LivingEntity monster = null;
        LivingEntity player = null;
        double monsterDist = Double.MAX_VALUE;
        double playerDist = Double.MAX_VALUE;

        for (Entity entity : loc.getWorld().getNearbyEntities(loc, FOLLOW_RANGE, 10, FOLLOW_RANGE)) {
            if (entity.isDead() || !entity.isValid() || !entity.getWorld().equals(world)) continue;
            double dist = entity.getLocation().distance(loc);
            if (entity instanceof Monster) {
                if (dist < monsterDist) { monsterDist = dist; monster = (LivingEntity) entity; }
            } else if (entity instanceof Player p && BossTargets.isCombatPlayer(p)) {
                if (dist < playerDist) { playerDist = dist; player = p; }
            }
        }
        return monster != null ? monster : player;
    }

    // ========================================================================
    // 单个成员死亡
    // ========================================================================

    private static void removeMember(FakePlayer member) {
        if (member == null) return;
        Enmity.unregisterBossBody(member.getBody());
        member.remove();

        if (member == captainBody) {
            captainBody = null;
            captainAlive = false;
            bossBar.setTitle("§a◆ 村民卫队 §7剩余卫兵: " + countAliveGuards());
            bossBar.setProgress(1.0);
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage("§a村民卫队的 §e队长 §a倒下了！");
            }
        } else {
            guards.remove(member);
            if (!captainAlive) {
                bossBar.setTitle("§a◆ 村民卫队 §7剩余卫兵: " + countAliveGuards());
            }
        }

        // 全员阵亡 → 击败
        if (!captainAlive && guards.isEmpty()) {
            onDeath();
        }
    }

    private static int countAliveGuards() {
        int count = 0;
        for (FakePlayer g : guards) {
            if (g != null && g.isValid()) count++;
        }
        return count;
    }

    // ========================================================================
    // 全队击败
    // ========================================================================

    public static void onDeath() {
        if (!alive) return;
        Location loc = captainBody != null ? captainBody.getLocation()
                : (!guards.isEmpty() ? guards.get(0).getLocation() : null);
        if (loc == null) { cleanup(); return; }

        loc.getWorld().spawnParticle(Particle.CLOUD, loc, 80, 3, 2, 3, 0.3);
        loc.getWorld().spawnParticle(Particle.ENCHANT, loc, 50, 2, 2, 2, 0.2);
        loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_DEATH, 1.0f, 0.6f);

        // 掉落
        loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.EMERALD, 6 + RANDOM.nextInt(7)));
        loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.VILLAGER_SPAWN_EGG, 2));
        loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.EXPERIENCE_BOTTLE, 16));
        loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.IRON_SWORD));

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§a◆ 村民卫队已被击败！");
        }
        cleanup();
    }

    private static void despawn() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("§a村民卫队因失去目标而消失了");
        }
        cleanup();
    }

    private static void cleanup() {
        if (aiTask != null) { aiTask.cancel(); aiTask = null; }
        if (captainBody != null) {
            Enmity.unregisterBossBody(captainBody.getBody());
            captainBody.remove();
            captainBody = null;
        }
        for (FakePlayer g : guards) {
            if (g != null) {
                Enmity.unregisterBossBody(g.getBody());
                g.remove();
            }
        }
        guards.clear();
        if (bossBar != null) { bossBar.removeAll(); bossBar = null; }
        alive = false;
        captainAlive = true;
        world = null;
    }

    public static boolean isAlive() { return alive; }

    public static Location getBossLocation() {
        if (captainBody != null) return captainBody.getLocation();
        if (!guards.isEmpty() && guards.get(0) != null) return guards.get(0).getLocation();
        return null;
    }

    // ========================================================================
    // 玩家上线/切世界 补齐
    // ========================================================================

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!alive || world == null) return;
        Player p = event.getPlayer();
        if (p.getWorld().equals(world)) {
            Bukkit.getScheduler().runTaskLater(NamespaceKey.Keys.getplugin, () -> {
                if (!alive || !p.isOnline()) return;
                if (captainBody != null) captainBody.spawnTo(p);
                for (FakePlayer g : guards) {
                    if (g != null && g.isValid()) g.spawnTo(p);
                }
            }, 20L);
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        if (!alive || world == null) return;
        Player p = event.getPlayer();
        if (p.getWorld().equals(world)) {
            Bukkit.getScheduler().runTaskLater(NamespaceKey.Keys.getplugin, () -> {
                if (!alive || !p.isOnline()) return;
                if (captainBody != null) captainBody.spawnTo(p);
                for (FakePlayer g : guards) {
                    if (g != null && g.isValid()) g.spawnTo(p);
                }
            }, 20L);
        } else {
            if (captainBody != null) captainBody.despawnFrom(p);
            for (FakePlayer g : guards) {
                if (g != null && g.isValid()) g.despawnFrom(p);
            }
        }
    }
}
