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

// 寒冻 —— level*10% 概率施加缓慢
public class FreezeEnchant implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void FreezeHandler(EntityDamageByEntityEvent event) {
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        int fzLvl = ArmsorEnchant.getEnchantLevel(weapon, FreezeKey);
        if (fzLvl > 0 && EnchantUtil.percent(fzLvl * 10)) {
            if (target instanceof Player) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * fzLvl, 1));
                PlayerSettings.notifyActionBar(target, ChatColor.AQUA + "你被敌人施加了寒冻");
            }
            PlayerSettings.notifyActionBar(damager, ChatColor.AQUA + "你对敌人施加了寒冻");
        }
    }
}
