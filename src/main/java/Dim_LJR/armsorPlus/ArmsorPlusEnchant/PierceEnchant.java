package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 贯穿 —— 长矛冲锋攻击(冲刺)后固定造成2*level穿透伤害 (满级III)
public class PierceEnchant implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void PierceHandler(EntityDamageByEntityEvent event) {
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        int pierceLvl = ArmsorEnchant.getEnchantLevel(weapon, PierceKey);
        if (pierceLvl > 0 && damager instanceof Player piercer
                && piercer.isSprinting() && weapon.getType().name().endsWith("_SPEAR")) {
            if (target.isDead()) return; // 前: 目标已死亡则跳过
            if (!EnchantUtil.PIERCING_ACTIVE.contains(target.getUniqueId())) {
                EnchantUtil.PIERCING_ACTIVE.add(target.getUniqueId());
                try {
                    target.damage(2.0 * pierceLvl, EnchantUtil.pierceSource(damager));
                } finally {
                    EnchantUtil.PIERCING_ACTIVE.remove(target.getUniqueId());
                }
            }
            if (target.isDead()) return; // 后: 目标被击杀则停止特效
            target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0),
                    pierceLvl * 4, 0.3, 0.3, 0.3, 0.1);
            PlayerSettings.notify(damager, ChatColor.DARK_PURPLE + "贯穿: 额外造成 " + (2 * pierceLvl) + " 点穿透伤害");
        }
    }
}
