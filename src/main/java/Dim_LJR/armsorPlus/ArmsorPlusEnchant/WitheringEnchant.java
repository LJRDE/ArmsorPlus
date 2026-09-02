package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 凋零 —— 攻击造成凋零效果 (武器)
public class WitheringEnchant implements Listener {

    @EventHandler
    public void WitheringHandler(EntityDamageByEntityEvent event) {
        if (event.getEntity().equals(event.getDamager())) return;
        Entity damager = event.getDamager();
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        // 穿透伤害不再重复施加凋零
        if (EnchantUtil.PIERCING_ACTIVE.contains(target.getUniqueId())) return;

        ItemStack weapon = null;
        if (damager instanceof Player) {
            weapon = ((Player) damager).getInventory().getItemInMainHand();
        } else if (damager instanceof LivingEntity living && living.getEquipment() != null) {
            weapon = living.getEquipment().getItemInMainHand();
        }

        if (weapon == null || weapon.getType() == Material.AIR) return;

        int level = ArmsorEnchant.getEnchantLevel(weapon, WitheringKey);
        if (level <= 0) return;

        int duration = level * 5 * 20; // level*5 秒 (ticks)
        int effectLevel = Math.min(level, 2);
        target.addPotionEffect(new PotionEffect(
                PotionEffectType.WITHER, duration, effectLevel, false, true));

        String msg = "施加了凋零" + EnchantUtil.romanNumeral(effectLevel + 1)
                + "（时长：" + (level * 5) + "秒）";
        PlayerSettings.notify(damager, ChatColor.GOLD + "你的武器" + msg);
        PlayerSettings.notify(target, ChatColor.RED + "你被" + EnchantUtil.getEntityName(damager) + msg);
    }
}
