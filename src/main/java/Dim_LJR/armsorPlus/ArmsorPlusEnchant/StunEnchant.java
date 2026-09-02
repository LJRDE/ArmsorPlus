package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 眩晕 —— 攻击施加反胃(眩晕)效果
public class StunEnchant implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void StunHandler(EntityDamageByEntityEvent event) {
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        int stLvl = ArmsorEnchant.getEnchantLevel(weapon, StunKey);
        if (stLvl > 0)
            target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, (int)(stLvl * 0.7 * 20), 0, false, false));
    }
}
