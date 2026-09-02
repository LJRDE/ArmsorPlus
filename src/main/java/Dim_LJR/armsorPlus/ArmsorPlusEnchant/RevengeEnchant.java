package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 复仇 —— 反弹伤害 (胸甲)
public class RevengeEnchant implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void RevengeHandler(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        // 穿透伤害不触发复仇反弹 (无法抵挡)
        if (EnchantUtil.PIERCING_ACTIVE.contains(player.getUniqueId())) return;
        // 防递归: 复仇反弹伤害再触发复仇 (双方都有复仇时)
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;

        ItemStack chestplate = player.getEquipment().getChestplate();
        if (chestplate == null) return;

        int level = ArmsorEnchant.getEnchantLevel(chestplate, RevengeKey);
        if (level <= 0) return;

        if (Math.random() >= 0.2 * level) return;

        Entity damager = event.getDamager();
        if (!(damager instanceof LivingEntity target)) return;

        double revengeDamage = event.getDamage() * 0.5;
        if (target.isDead()) return; // 前: 目标已死亡则不再反弹
        // 标记复仇伤害来源: 使攻击侧附魔跳过, 不触发血祭/吸血等附魔
        EnchantUtil.REVENGE_ACTIVE.add(target.getUniqueId());
        try {
            target.damage(revengeDamage, player);
        } finally {
            EnchantUtil.REVENGE_ACTIVE.remove(target.getUniqueId());
        }
        if (target.isDead()) return; // 后: 目标被反弹伤害击杀则停止

        PlayerSettings.notify(player, ChatColor.RED + "复仇效果反弹了"
                + String.format("%.1f", revengeDamage) + "点伤害！");

        PlayerSettings.notify(target, ChatColor.RED + player.getName() + "的复仇效果反弹了你的攻击！");

        player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR,
                target.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5);
    }
}
