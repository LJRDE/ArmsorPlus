package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 强风暴 —— 无需下落即可触发风暴 (重锤)
public class StrongBurstEnchant implements Listener {

    @EventHandler
    public void StrongBurstHandler(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;

        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (weapon.getType() != Material.MACE) return;
        int level = ArmsorEnchant.getEnchantLevel(weapon, StrongBurstKey);
        if (level <= 0) return;

        if (!(event.getEntity() instanceof LivingEntity target)) return;

        Location loc = target.getLocation();
        loc.getWorld().createExplosion(loc, 2.0f, false, false);
        loc.getWorld().spawnParticle(Particle.GUST, loc, 30, 2, 1, 2, 0.5);
        loc.getWorld().playSound(loc, Sound.ENTITY_WIND_CHARGE_WIND_BURST, 1.0f, 1.0f);
    }
}
