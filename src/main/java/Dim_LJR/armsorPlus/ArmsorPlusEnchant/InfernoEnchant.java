package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 烈焰 —— 攻击附加 level*5 点伤害并点燃目标
public class InfernoEnchant implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void InfernoHandler(EntityDamageByEntityEvent event) {
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        int ifLvl = ArmsorEnchant.getEnchantLevel(weapon, InfernoKey);
        if (ifLvl > 0) { event.setDamage(event.getDamage() + ifLvl * 5.0); target.setFireTicks(20); }
    }
}
