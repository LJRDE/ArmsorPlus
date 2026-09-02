package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 影避 —— 概率闪避所有伤害 (靴子)
public class ShadowDodgeEnchant implements Listener {

    @EventHandler
    public void ShadowDodgeHandler(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        // 穿透伤害无法被影避
        if (EnchantUtil.PIERCING_ACTIVE.contains(entity.getUniqueId())) return;
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        int level = ArmsorEnchant.getEnchantLevel(equipment.getBoots(), ShadowDodge);
        if (level == 0) return;
        if (!EnchantUtil.percent(8 * level)) return;

        event.setCancelled(true);
        Particle.PORTAL.builder()
                .location(event.getEntity().getLocation())
                .offset(0.1, 0.1, 0.1)
                .count(96)
                .receivers(32, true)
                .spawn();

        if (event.getEntity() instanceof Player player) {
            PlayerSettings.notify(player, ChatColor.DARK_PURPLE + "你影避了伤害");
        }
    }
}
