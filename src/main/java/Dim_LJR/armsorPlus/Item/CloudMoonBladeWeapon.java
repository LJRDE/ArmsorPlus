package Dim_LJR.armsorPlus.Item;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.EnchantUtil;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.CloudMoonBladeKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

// 吞云斩月刀 —— 右键向前突刺(最远3格), 指向生物则突刺至其面前并造成[基础9+锋利x2]伤害
// 突刺伤害走真实近战事件(target.damage), 经伤害链自动触发血祭/双重打击等
public class CloudMoonBladeWeapon implements Listener {

    private static final Map<UUID, Integer> cloudMoonBladeCooldown = new HashMap<>();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cloudMoonBladeCooldown.remove(event.getPlayer().getUniqueId());
    }

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
        Location safeEnd = EnchantUtil.findSafeTeleportLocation(end);
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
            // 突刺到位后结算伤害 (真实近战事件, 经伤害链触发血祭/双重打击)
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
}
