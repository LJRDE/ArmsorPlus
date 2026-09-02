package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 血祭 —— 概率消耗自生生命造成多倍伤害 (最先结算)
public class BloodSacrificeEnchant implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void BloodSacrificeHandler(EntityDamageByEntityEvent event) {
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        int bslvl = ArmsorEnchant.getEnchantLevel(weapon, BloodSacrificekey);
        if (bslvl > 0 && EnchantUtil.percent(20 * bslvl)) {
            int rate = new Random().nextInt(bslvl) + 2;
            double dmg = event.getDamage() * rate;
            event.setDamage(dmg);
            // 血祭代价: 攻击者受10点无法抵挡的穿透伤害 (走事件系统, 避免 setHealth(0) 假死)
            if (!EnchantUtil.PIERCING_ACTIVE.contains(damager.getUniqueId())) {
                EnchantUtil.PIERCING_ACTIVE.add(damager.getUniqueId());
                try {
                    damager.damage(10, EnchantUtil.pierceSource(damager));
                } finally {
                    EnchantUtil.PIERCING_ACTIVE.remove(damager.getUniqueId());
                }
            }
            PlayerSettings.notify(event.getDamager(), "你发动了" + ChatColor.RED + "血祭" + ChatColor.RESET + "对对方造成" + rate + "倍伤害");
            PlayerSettings.notify(event.getEntity(), "对方发动了" + ChatColor.RED + "血祭" + ChatColor.RESET + "对你造成" + rate + "倍伤害");
            if (damager.isDead()) return;
        }
    }
}
