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

// 星痕 —— 夜晚(13000~23000刻)造成伤害提升100%
public class StarTraceEnchant implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void StarTraceHandler(EntityDamageByEntityEvent event) {
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        if (ArmsorEnchant.getEnchantLevel(weapon, StarTraceSwordKey) > 0) {
            long t = target.getWorld().getTime();
            if (t >= 13000 && t <= 23000) {
                event.setDamage(event.getDamage() * 2.0);
                PlayerSettings.notify(event.getDamager(), ChatColor.DARK_AQUA + "星痕 · 星光之力: 伤害提升100%");
            }
        }
    }
}
