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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 饥荒 —— 攻击对玩家施加饥饿
public class FamineEnchant implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void FamineHandler(EntityDamageByEntityEvent event) {
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        int faLvl = ArmsorEnchant.getEnchantLevel(weapon, Faminekey);
        if (faLvl > 0) {
            if (target instanceof Player) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 40 * faLvl, 4 * faLvl));
                PlayerSettings.notify(target, ChatColor.GREEN + "你被敌人施加了饥荒");
            }
            PlayerSettings.notify(event.getDamager(), ChatColor.GREEN + "你对敌人施加了饥荒");
        }
    }
}
