package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 暴击 —— 概率造成额外暴击伤害
public class CriticalStrikeEnchant implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void CriticalStrikeHandler(EntityDamageByEntityEvent event) {
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        int csLvl = ArmsorEnchant.getEnchantLevel(weapon, CriticalStrikeKey);
        if (csLvl > 0 && EnchantUtil.percent(15 * csLvl)) {
            event.setDamage(event.getDamage() * (1.0 + 0.25 * csLvl));
            if (event.getDamager() instanceof Player)
                PlayerSettings.notify(event.getDamager(), ChatColor.RED + "暴击! 造成" + (int)(25 * csLvl) + "%额外伤害");
        }
    }
}
