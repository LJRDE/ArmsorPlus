package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 雷光 —— 雷雨天造成伤害提升25%
public class ThunderGlowEnchant implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void ThunderGlowHandler(EntityDamageByEntityEvent event) {
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        if (ArmsorEnchant.getEnchantLevel(weapon, ThunderGlowKey) > 0 && target.getWorld().isThundering()) {
            event.setDamage(event.getDamage() * 1.25);
            PlayerSettings.notify(event.getDamager(), ChatColor.YELLOW + "雷光 · 雷霆之力: 伤害提升25%");
        }
    }
}
