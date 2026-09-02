package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 追踪 —— 箭矢追踪450格内指向目标 (弓)
public class TrackingEnchant implements Listener {

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
}
