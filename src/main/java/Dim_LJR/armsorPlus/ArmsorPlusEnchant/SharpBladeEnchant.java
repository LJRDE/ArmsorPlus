package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 利刃 —— 目标护甲越低伤害提升越多
public class SharpBladeEnchant implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void SharpBladeHandler(EntityDamageByEntityEvent event) {
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        int sbLvl = ArmsorEnchant.getEnchantLevel(weapon, SharpBladeKey);
        if (sbLvl <= 0) return;

        double armor = 0;
        if (target.getEquipment() != null)
            for (ItemStack p : target.getEquipment().getArmorContents())
                if (p != null && p.getType() != Material.AIR) armor += EnchantUtil.getArmorValue(p.getType());
        double multiplier = armor > 18 ? 1.10 : 1.0 + (18.0 - armor) / 18.0 * sbLvl * 0.08;
        event.setDamage(event.getDamage() * multiplier);
        if (armor <= 18)
            PlayerSettings.notify(event.getDamager(), ChatColor.DARK_AQUA + "利刃: 伤害提升" + String.format("%.0f", (multiplier - 1) * 100) + "%");
    }
}
