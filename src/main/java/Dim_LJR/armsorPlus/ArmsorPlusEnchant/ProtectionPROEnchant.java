package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 保护PRO —— 胸甲, 6%*level 全伤害减免
public class ProtectionPROEnchant implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void ProtectionPROHandler(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity le && EnchantUtil.PIERCING_ACTIVE.contains(le.getUniqueId())) return;
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null) return;

        int proLvl = ArmsorEnchant.getEnchantLevel(chestplate, ProtectionPROKey);
        if (proLvl > 0) event.setDamage(event.getDamage() * (1.0 - 0.06 * proLvl));
    }
}
