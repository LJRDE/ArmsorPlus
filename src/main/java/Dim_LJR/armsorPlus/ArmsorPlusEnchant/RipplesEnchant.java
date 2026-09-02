package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 涟漪 —— 受到伤害时回复生命 (靴子, 满级III)
public class RipplesEnchant implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void RipplesHandler(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        // 穿透伤害不再触发涟漪回复
        if (EnchantUtil.PIERCING_ACTIVE.contains(entity.getUniqueId())) return;
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        int level = ArmsorEnchant.getEnchantLevel(
                equipment.getBoots(), RipplesProtectkey);
        if (level <= 0) return;

        // 涟漪: 受到伤害后回复生命 (每级回复所受伤害的20%, 匹配Lore)
        double heal = event.getDamage() * 0.2 * level;
        if (heal > 0) {
            entity.setHealth(Math.min(entity.getMaxHealth(), entity.getHealth() + heal));
            if (entity instanceof Player p) {
                PlayerSettings.notifyActionBar(p, ChatColor.BLUE + "涟漪回复了 " + String.format("%.1f", heal) + " 点生命");
            }
        }
    }
}
