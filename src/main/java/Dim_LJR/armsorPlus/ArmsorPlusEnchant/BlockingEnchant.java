package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 格挡 —— 按伤害比例减免 (头盔)
public class BlockingEnchant implements Listener {

    @EventHandler
    public void BlockingHandler(EntityDamageEvent event) {
        // 格挡只对物理攻击生效 (魔法伤害由百草附魔处理)
        if (!EnchantUtil.isPhysicalAttack(event)) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        // 穿透伤害无法被格挡
        if (EnchantUtil.PIERCING_ACTIVE.contains(entity.getUniqueId())) return;
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        int level = ArmsorEnchant.getEnchantLevel(equipment.getHelmet(), BlockingKey);
        if (level <= 0) return;

        double original = event.getDamage();
        double reduction = level * 0.10; // 每级10%
        event.setDamage(original * (1.0 - reduction));

        if (entity instanceof Player player) {
            double blocked = original - event.getDamage();
            PlayerSettings.notifyActionBar(player, ChatColor.BLUE + "格挡: 减免了"
                    + String.format("%.1f", blocked) + "点伤害 (" + (level * 10) + "%)");
        }
    }
}
