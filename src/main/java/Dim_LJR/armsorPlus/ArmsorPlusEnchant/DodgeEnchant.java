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
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 闪避 —— 概率闪避攻击伤害 (靴子)
public class DodgeEnchant implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void OnBeaten(EntityDamageByEntityEvent event) {
        // 穿透伤害无法被闪避
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        // 闪避只对物理攻击生效 (魔法伤害无法闪避, 由百草附魔处理)
        if (!EnchantUtil.isPhysicalAttack(event)) return;

        // 玩家闪避玩家
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof Player damager) {
            ItemStack boots = player.getInventory().getBoots();
            int level = ArmsorEnchant.getEnchantLevel(boots, Dodgekey);
            if (level == 0 || !EnchantUtil.percent(8 * level)) return;

            event.setDamage(event.getDamage() * 0.2); // 闪避: 减免80%伤害 (只受20%)
            Particle.ENCHANTED_HIT.builder().location(player.getLocation())
                    .offset(0.1, 0.1, 0.1).count(48).receivers(32, true).spawn();
            PlayerSettings.notifyActionBar(player, ChatColor.GOLD + "你闪避了对方大部分伤害（减免80%）");
            PlayerSettings.notifyActionBar(damager, ChatColor.GOLD + "对方闪避了你的伤害（减免80%）");
            return;
        }

        // 玩家闪避生物
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof LivingEntity) {
            ItemStack boots = player.getInventory().getBoots();
            int level = ArmsorEnchant.getEnchantLevel(boots, Dodgekey);
            if (level == 0 || !EnchantUtil.percent(8 * level)) return;

            event.setDamage(event.getDamage() * 0.2); // 闪避: 减免80%伤害 (只受20%)
            Particle.PORTAL.builder().location(player.getLocation())
                    .offset(0.1, 0.1, 0.1).count(64).receivers(32, true).spawn();
            PlayerSettings.notifyActionBar(player, ChatColor.GOLD + "你闪避了对方大部分伤害（减免80%）");
            return;
        }

        // 生物闪避生物
        if (event.getEntity() instanceof LivingEntity entity) {
            EntityEquipment equipment = entity.getEquipment();
            if (equipment == null) return;
            ItemStack boots = equipment.getBoots();
            int level = ArmsorEnchant.getEnchantLevel(boots, Dodgekey);
            if (level == 0 || !EnchantUtil.percent(20 * level)) return;

            event.setDamage(event.getDamage() * 0.2); // 闪避: 减免80%伤害 (只受20%)
            Particle.PORTAL.builder().location(entity.getLocation())
                    .offset(0.1, 0.1, 0.1).count(64).receivers(32, true).spawn();
        }
    }
}
