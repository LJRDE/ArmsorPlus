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

// 剧毒 —— 攻击施加中毒效果
public class PoisonEnchant implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void PoisonHandler(EntityDamageByEntityEvent event) {
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        int poLvl = ArmsorEnchant.getEnchantLevel(weapon, PoisonKey);
        if (poLvl > 0) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, (poLvl+1)*3*20, Math.min(poLvl-1,2), false, true));
            PlayerSettings.notify(event.getDamager(), ChatColor.DARK_GREEN + "你对敌人施加了剧毒" + EnchantUtil.romanNumeral(Math.min(poLvl, 3)));
            PlayerSettings.notify(event.getEntity(), ChatColor.DARK_GREEN + "你被施加了剧毒" + EnchantUtil.romanNumeral(Math.min(poLvl, 3)));
        }
    }
}
