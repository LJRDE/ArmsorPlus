package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 全息 —— 持盾时扩展到全角度防御 (盾牌, 需右键举盾, 斧子可破盾)
public class HolographicEnchant implements Listener {

    @EventHandler
    public void HolographicHandler(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        // 穿透伤害无法被全息盾格挡
        if (EnchantUtil.PIERCING_ACTIVE.contains(player.getUniqueId())) return;

        // 检查玩家是否正在举盾防御
        if (!player.isBlocking()) return;

        // 查找带有全息附魔的盾牌 (主手或副手)
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        ItemStack shield = mainHand.getType() == Material.SHIELD && ArmsorEnchant.getEnchantLevel(mainHand, HolographicKey) > 0
                ? mainHand : offHand.getType() == Material.SHIELD && ArmsorEnchant.getEnchantLevel(offHand, HolographicKey) > 0
                ? offHand : null;
        if (shield == null) return;

        // 消耗盾牌耐久
        Damageable dmg = (Damageable) shield.getItemMeta();
        dmg.setDamage(dmg.getDamage() + 1);
        if (dmg.getDamage() >= shield.getType().getMaxDurability()) {
            shield.setAmount(0);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
        } else {
            shield.setItemMeta(dmg);
        }

        // 斧子攻击: 盾牌进入冷却, 但不格挡伤害
        if (event.getDamager() instanceof LivingEntity damager && damager.getEquipment() != null) {
            ItemStack weapon = damager.getEquipment().getItemInMainHand();
            if (weapon.getType().name().endsWith("_AXE")) {
                player.setCooldown(Material.SHIELD, 100); // 5秒冷却
                player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0f, 1.0f);
                return;
            }
        }

        // 非斧子: 全角度格挡伤害
        event.setCancelled(true);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation().add(0, 1, 0),
                5, 0.3, 0.3, 0.3, 0);
    }
}
