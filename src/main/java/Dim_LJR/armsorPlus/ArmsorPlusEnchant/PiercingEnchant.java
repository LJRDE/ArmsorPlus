package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 穿甲 —— 盾牌进入CD + 5点穿透伤害 (弓/弩)
public class PiercingEnchant implements Listener {

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
}
