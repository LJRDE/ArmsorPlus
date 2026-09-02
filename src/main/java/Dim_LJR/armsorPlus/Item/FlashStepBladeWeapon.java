package Dim_LJR.armsorPlus.Item;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.EnchantUtil;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
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

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.FlashStepBladeKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

// 瞬步刃 —— 右键向前瞬移，指向目标则瞬移到身后并造成伤害
public class FlashStepBladeWeapon implements Listener {

    private static final Map<UUID, Integer> flashStepBladeCooldown = new HashMap<>();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        flashStepBladeCooldown.remove(event.getPlayer().getUniqueId());
    }

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
            Location safeLoc = EnchantUtil.findSafeTeleportLocation(behindTarget);
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
            Location safeLoc = EnchantUtil.findSafeTeleportLocation(teleportTo);
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
}
