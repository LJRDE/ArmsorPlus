package Dim_LJR.armsorPlus.Boss;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

public class IllusionerBoss {

    private static final double MAX_HEALTH = 500;
    private static final Random RANDOM = new Random();

    private static boolean bossAlive;
    private static LivingEntity bossEntity;
    private static BukkitTask aiTask;
    private static BossBar bossBar;
    private static boolean vindicatorsSpawned;
    private static boolean arrowRainTriggered;
    private static int noTargetTicks;
    private static LivingEntity currentTarget;
    private static final List<Vindicator> summonedVindicators = new ArrayList<>();

    public static boolean isAlive() { return bossAlive && bossEntity != null && !bossEntity.isDead(); }
    public static Location getBossLocation() { return bossEntity != null ? bossEntity.getLocation() : null; }

    /** BossMenu 回调，记录当前攻击者用于卫道士索敌 */
    public static void updateTarget(Entity damager) {
        if (damager instanceof LivingEntity living) {
            currentTarget = living;
            for (Vindicator v : summonedVindicators) {
                if (v != null && v.isValid() && !v.isDead()) {
                    v.setTarget(currentTarget);
                }
            }
        }
    }

    public static void spawnBoss(Player summoner) {
        if (bossAlive) { summoner.sendMessage("§c幻术师已在战斗中"); return; }
        Location spawnLoc = findSpawnLocation(summoner);
        if (spawnLoc == null) { summoner.sendMessage("§c没有足够空间"); return; }
        spawnLoc.getWorld().loadChunk(spawnLoc.getChunk());

        bossEntity = (LivingEntity) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ILLUSIONER);
        bossEntity.setCustomName("§d◆ 幻术师 §7Lv.88");
        bossEntity.setCustomNameVisible(true);
        bossEntity.setRemoveWhenFarAway(false);
        bossEntity.setPersistent(true);

        var maxHp = bossEntity.getAttribute(Attribute.MAX_HEALTH);
        if (maxHp != null) maxHp.setBaseValue(MAX_HEALTH);
        bossEntity.setHealth(MAX_HEALTH);

        // Power X + Punch III bow
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.addEnchant(Enchantment.POWER, 10, true);
        bowMeta.addEnchant(Enchantment.PUNCH, 3, true);
        bowMeta.setUnbreakable(true);
        bow.setItemMeta(bowMeta);
        bossEntity.getEquipment().setItemInMainHand(bow);
        bossEntity.getEquipment().setItemInMainHandDropChance(0f);

        bossBar = Bukkit.createBossBar("§d◆ 幻术师", BarColor.PINK, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(1.0);
        BossMenu.registerBoss(BossMenu.BossType.ILLUSIONER, bossEntity, MAX_HEALTH, bossBar);

        Location loc = bossEntity.getLocation();
        loc.getWorld().strikeLightningEffect(loc);
        loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1f, 0.5f);
        Bukkit.broadcastMessage("§d◆ 幻术师在 " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " 降临！");

        bossAlive = true;
        vindicatorsSpawned = false;
        arrowRainTriggered = false;
        noTargetTicks = 0;
        startAI();
    }

    private static void startAI() {
        aiTask = new BukkitRunnable() {
            @Override
            public void run() {
                // 实体自然死亡 → 触发死亡掉落（必须在 !isValid 前检测，因为死亡后 isValid 也为 false）
                if (bossEntity.isDead()) {
                    if (bossAlive) {
                        onDeath();
                    }
                    cancel();
                    return;
                }

                // 实体无效(区块卸载等) → 异常清理
                if (bossEntity == null || !bossEntity.isValid()) {
                    if (bossAlive) {
                        Bukkit.broadcastMessage("§d幻术师异常消失...");
                        cleanup();
                    }
                    cancel();
                    return;
                }

                // 从实体原生血量更新BossBar
                double currentHp = bossEntity.getHealth();
                double maxHp = bossEntity.getAttribute(Attribute.MAX_HEALTH).getValue();
                bossBar.setProgress(Math.max(0, currentHp / maxHp));
                bossBar.setTitle("§d◆ 幻术师 §7" + Math.round(currentHp) + "/" + Math.round(maxHp));
                BossMenu.syncBossHealth(BossMenu.BossType.ILLUSIONER, currentHp, maxHp);
                BossMenu.updateBossBar(BossMenu.BossType.ILLUSIONER);

                double hpPercent = currentHp / maxHp;

                // HP ≤ 40%: summon 4 vindicators
                if (hpPercent <= 0.4 && !vindicatorsSpawned) {
                    vindicatorsSpawned = true;
                    summonVindicators();
                }

                // HP ≤ 20%: arrow rain for 5 seconds
                if (hpPercent <= 0.2 && !arrowRainTriggered) {
                    arrowRainTriggered = true;
                    startArrowRain();
                }

                Location bl = bossEntity.getLocation();
                boolean hasTarget = false;
                for (Player p : bl.getWorld().getPlayers()) {
                    if (p.getLocation().distance(bl) <= 50) { hasTarget = true; break; }
                }
                if (!hasTarget) {
                    noTargetTicks++;
                    if (noTargetTicks >= 1200) {
                        Bukkit.broadcastMessage("§d幻术师因无人应战而消失...");
                        despawn();
                        cancel();
                        return;
                    }
                } else {
                    noTargetTicks = 0;
                }
            }
        }.runTaskTimer(getplugin, 10L, 10L);
    }

    private static void summonVindicators() {
        Location loc = bossEntity.getLocation();
        World world = loc.getWorld();

        summonedVindicators.clear();
        for (int i = 0; i < 4; i++) {
            double angle = i * Math.PI / 2;
            Location spawnLoc = loc.clone().add(Math.cos(angle) * 3, 0, Math.sin(angle) * 3);

            Vindicator vind = (Vindicator) world.spawnEntity(spawnLoc, EntityType.VINDICATOR);
            vind.setCustomName("§c幻术护卫");
            vind.setCustomNameVisible(true);
            vind.setRemoveWhenFarAway(false);

            // 索敌：优先攻击正在攻击幻术师的目标
            if (currentTarget != null && !currentTarget.isDead()) {
                vind.setTarget(currentTarget);
            }

            // Sharpness VI iron axe
            ItemStack axe = new ItemStack(Material.IRON_AXE);
            ItemMeta axeMeta = axe.getItemMeta();
            axeMeta.addEnchant(Enchantment.SHARPNESS, 6, true);
            axe.setItemMeta(axeMeta);
            vind.getEquipment().setItemInMainHand(axe);
            vind.getEquipment().setItemInMainHandDropChance(0f);

            vind.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 0, false, false));
            vind.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, -1, 0, false, false));

            summonedVindicators.add(vind);

            world.spawnParticle(Particle.SOUL_FIRE_FLAME, spawnLoc, 15, 0.5, 0.5, 0.5, 0.05);
        }
        world.playSound(loc, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1f, 1.2f);
        Bukkit.broadcastMessage("§d◆ 幻术师召唤了4名幻术护卫！");
    }

    private static void startArrowRain() {
        Location loc = bossEntity.getLocation();
        World world = loc.getWorld();

        world.playSound(loc, Sound.ENTITY_EVOKER_PREPARE_ATTACK, 1f, 0.8f);
        Bukkit.broadcastMessage("§d◆ 幻术师降下了箭雨！");

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (!isAlive()) { cancel(); return; }
                tick++;
                if (tick > 100) { cancel(); return; }

                Location bl = bossEntity.getLocation();
                for (Player p : bl.getWorld().getPlayers()) {
                    if (p.getLocation().distance(bl) > 25) continue;
                    for (int i = 0; i < 3; i++) {
                        Location arrowLoc = p.getLocation().add(
                                RANDOM.nextDouble() * 6 - 3, 8 + RANDOM.nextDouble() * 5, RANDOM.nextDouble() * 6 - 3);
                        Arrow arrow = bl.getWorld().spawn(arrowLoc, Arrow.class);
                        arrow.setVelocity(new Vector(0, -2, 0));
                        arrow.setDamage(6);
                        arrow.setShooter(bossEntity);
                        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                    }
                }
            }
        }.runTaskTimer(getplugin, 0L, 5L);
    }

    public static void onDeath() {
        if (!bossAlive) return; // 防止重复调用
        if (aiTask != null) aiTask.cancel();
        Location loc = bossEntity.getLocation();
        World world = loc.getWorld();

        world.strikeLightningEffect(loc);
        world.createExplosion(loc, 0f, false, false);
        world.spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 50, 1, 1, 1, 0.1);
        world.playSound(loc, Sound.ENTITY_WITHER_DEATH, 1f, 0.5f);

        // 掉落：幻术师的遗骨(骨头)、幻术师的遗骸(下界残骸)，每类0~2个
        int boneCount = RANDOM.nextInt(3);
        int scrapCount = RANDOM.nextInt(3);

        if (boneCount > 0) {
            ItemStack bone = new ItemStack(Material.BONE, boneCount);
            ItemMeta boneMeta = bone.getItemMeta();
            boneMeta.setDisplayName("§d幻术师的遗骨");
            bone.setItemMeta(boneMeta);
            world.dropItemNaturally(loc, bone);
        }
        if (scrapCount > 0) {
            ItemStack scrap = new ItemStack(Material.NETHERITE_SCRAP, scrapCount);
            ItemMeta scrapMeta = scrap.getItemMeta();
            scrapMeta.setDisplayName("§d幻术师的遗骸");
            scrap.setItemMeta(scrapMeta);
            world.dropItemNaturally(loc, scrap);
        }

        Bukkit.broadcastMessage("§d◆ 幻术师已被彻底击败！");

        cleanup();
    }

    private static void despawn() {
        if (bossEntity != null) {
            Location loc = bossEntity.getLocation();
            loc.getWorld().spawnParticle(Particle.SMOKE, loc, 40, 2, 2, 2, 0.1);
            bossEntity.remove();
        }
        cleanup();
    }

    private static void cleanup() {
        if (aiTask != null) { aiTask.cancel(); aiTask = null; }
        bossAlive = false;
        bossEntity = null;
        if (bossBar != null) { bossBar.setVisible(false); bossBar.removeAll(); bossBar = null; }
        BossMenu.unregisterBoss(BossMenu.BossType.ILLUSIONER);
        vindicatorsSpawned = false;
        arrowRainTriggered = false;
        currentTarget = null;
        summonedVindicators.clear();
    }

    private static Location findSpawnLocation(Player summoner) {
        Location base = summoner.getLocation();
        for (int r = 5; r <= 20; r += 5) {
            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI / 4;
                Location loc = base.clone().add(Math.cos(angle) * r, 0, Math.sin(angle) * r);
                loc.setY(base.getWorld().getHighestBlockYAt(loc) + 1);
                if (loc.getBlock().isEmpty() && loc.clone().add(0, 1, 0).getBlock().isEmpty())
                    return loc;
            }
        }
        return null;
    }
}
