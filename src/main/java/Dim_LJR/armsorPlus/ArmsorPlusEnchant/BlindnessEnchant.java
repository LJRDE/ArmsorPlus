package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 失明 —— level*10% 概率施加失明
public class BlindnessEnchant implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void BlindnessHandler(EntityDamageByEntityEvent event) {
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        int blLvl = ArmsorEnchant.getEnchantLevel(weapon, BlindnessKey);
        if (blLvl > 0 && EnchantUtil.percent(blLvl * 10)) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40 * blLvl, 0, false, true));
            PlayerSettings.notifyActionBar(damager, ChatColor.DARK_GRAY + "你对敌人施加了失明");
            PlayerSettings.notifyActionBar(target, ChatColor.DARK_GRAY + "你被敌人施加了失明");
        }
    }
}
