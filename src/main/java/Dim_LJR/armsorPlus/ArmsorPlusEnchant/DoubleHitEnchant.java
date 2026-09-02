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

// 双重打击 —— 概率造成双倍伤害
public class DoubleHitEnchant implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void DoubleHitHandler(EntityDamageByEntityEvent event) {
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        int dhLvl = ArmsorEnchant.getEnchantLevel(weapon, DoubleHitkey);
        if (dhLvl > 0 && EnchantUtil.percent(20 * dhLvl)) {
            event.setDamage(event.getDamage() * 2);
            PlayerSettings.notify(event.getDamager(), "你发动了" + ChatColor.RED + "双重打击" + ChatColor.RESET + "对对方造成" + event.getDamage() + "点伤害");
            PlayerSettings.notify(event.getEntity(), "对方发动了" + ChatColor.RED + "双重打击" + ChatColor.RESET + "对你造成" + event.getDamage() + "点伤害");
        }
    }
}
