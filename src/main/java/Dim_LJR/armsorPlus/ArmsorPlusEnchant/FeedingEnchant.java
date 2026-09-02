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

// 吸血 —— 攻击造成穿透伤害并回复自身生命
public class FeedingEnchant implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void FeedingHandler(EntityDamageByEntityEvent event) {
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        int fdLvl = ArmsorEnchant.getEnchantLevel(weapon, Feedingkey);
        if (fdLvl <= 0) return;

        double heal = 1.5 * fdLvl;
        if (target.isDead()) return; // 前: 目标已死亡则跳过
        // 穿透伤害: 目标受 heal 点无法抵挡的伤害 (走事件系统, 避免 setHealth(0) 假死)
        if (!EnchantUtil.PIERCING_ACTIVE.contains(target.getUniqueId())) {
            EnchantUtil.PIERCING_ACTIVE.add(target.getUniqueId());
            try {
                target.damage(heal, EnchantUtil.pierceSource(damager));
            } finally {
                EnchantUtil.PIERCING_ACTIVE.remove(target.getUniqueId());
            }
        }
        damager.setHealth(Math.min(damager.getHealth() + heal, damager.getMaxHealth())); // 攻击者回血
        PlayerSettings.notify(event.getDamager(), ChatColor.RED + "吸血 恢复了" + String.format("%.1f", heal) + "点生命值");
        if (target.isDead()) return; // 后: 目标被吸血击杀则停止后续药水
    }
}
