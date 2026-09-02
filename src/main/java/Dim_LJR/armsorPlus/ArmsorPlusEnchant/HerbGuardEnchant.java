package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 百草 —— 药水/魔法伤害减免15%*level, 并有5%*level概率免疫 (胸甲, 满级III)
public class HerbGuardEnchant implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void HerbGuardHandler(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity le && EnchantUtil.PIERCING_ACTIVE.contains(le.getUniqueId())) return;
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null) return;

        int hgLvl = ArmsorEnchant.getEnchantLevel(chestplate, HerbGuardKey);
        if (hgLvl > 0 && EnchantUtil.isMagicDamage(event.getCause())) {
            if (EnchantUtil.percent(5 * hgLvl)) {
                event.setDamage(0);
                PlayerSettings.notifyActionBar(player, "§a[百草]免疫了魔法伤害 §7(" + (5 * hgLvl) + "%)");
            } else {
                event.setDamage(event.getDamage() * (1.0 - 0.15 * hgLvl));
                PlayerSettings.notifyActionBar(player, "§a[百草]魔法伤害减免 §7(" + (hgLvl * 15) + "%)");
            }
        }
    }
}
