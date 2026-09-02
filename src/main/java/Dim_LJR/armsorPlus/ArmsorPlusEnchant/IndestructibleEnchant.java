package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 不灭 —— 防止死亡 (任意装备栏)
public class IndestructibleEnchant implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void IndestructibleHandler(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        // 穿透伤害无视不朽 (无法抵挡)
        if (EnchantUtil.PIERCING_ACTIVE.contains(player.getUniqueId())) return;
        if (player.getHealth() - event.getFinalDamage() > 0) return;

        int level = 0;
        for (ItemStack item : player.getInventory().getArmorContents()) {
            level = Math.max(level, ArmsorEnchant.getEnchantLevel(item, IndestructibleKey));
        }
        level = Math.max(level, ArmsorEnchant.getEnchantLevel(player.getInventory().getItemInMainHand(), IndestructibleKey));
        level = Math.max(level, ArmsorEnchant.getEnchantLevel(player.getInventory().getItemInOffHand(), IndestructibleKey));
        if (level == 0) return;

        event.setCancelled(true);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setFireTicks(0);

        PlayerSettings.notify(player, ChatColor.GOLD + "不灭效果触发！成功规避死亡");

        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING,
                player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.5);
        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.8f, 1.2f);
    }
}
