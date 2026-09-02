package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 烈阳 —— 白天(13000刻之前)额外造成8点伤害
public class BlazingSunEnchant implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void BlazingSunHandler(EntityDamageByEntityEvent event) {
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        if (ArmsorEnchant.getEnchantLevel(weapon, BlazingSunKey) > 0 && target.getWorld().getTime() < 13000)
            event.setDamage(event.getDamage() + 8.0);
    }
}
