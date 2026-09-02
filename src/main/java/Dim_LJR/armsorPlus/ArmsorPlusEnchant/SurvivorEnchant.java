package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 幸存 —— 致命伤概率复活 (护腿)
public class SurvivorEnchant implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void SurvivorHandler(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        // 穿透伤害无视幸存 (无法抵挡)
        if (EnchantUtil.PIERCING_ACTIVE.contains(player.getUniqueId())) return;

        ItemStack leggings = player.getEquipment().getLeggings();
        int level = ArmsorEnchant.getEnchantLevel(leggings, SurvivorKey);
        if (level <= 0) return;

        // 非致命伤害不触发
        if (player.getHealth() - event.getFinalDamage() > 0) return;

        // 每级10%概率
        if (Math.random() >= level * 0.1) return;

        // 复活
        event.setCancelled(true);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setFireTicks(0);

        PlayerSettings.notify(player,ChatColor.GOLD + "护腿上的幸存效果触发！成功规避死亡");

        if (event instanceof EntityDamageByEntityEvent e) {
            if (e.getDamager() instanceof Player attacker) {
                PlayerSettings.notify(attacker, ChatColor.YELLOW + player.getName()
                        + " 的幸存附魔触发，规避了致命伤害");
            }
        }

        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING,
                player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.5);
        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.8f, 1.2f);

        // 3秒生命恢复
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.REGENERATION, 60, 10, false, false));
    }
}
