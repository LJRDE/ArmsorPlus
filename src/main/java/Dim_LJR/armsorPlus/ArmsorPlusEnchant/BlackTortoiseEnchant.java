package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 玄武剑 —— 每造成200点伤害伤害+1，上限+8
public class BlackTortoiseEnchant implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void BlackTortoiseHandler(EntityDamageByEntityEvent event) {
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        if (ArmsorEnchant.getEnchantLevel(weapon, BlackTortoiseSwordKey) > 0) {
            ItemMeta meta = weapon.getItemMeta();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            Double acc = pdc.get(TortoiseAccumulatedKey, PersistentDataType.DOUBLE);
            if (acc == null) acc = 0.0;
            acc += event.getDamage();
            int bonus = Math.min((int)(acc / 200.0), 8);
            pdc.set(TortoiseAccumulatedKey, PersistentDataType.DOUBLE, acc);
            List<String> lore = meta.getLore();
            if (lore != null && lore.size() >= 3) {
                lore.set(1, ChatColor.DARK_GREEN + "当前加成: " + bonus + "/8");
                lore.set(2, ChatColor.GRAY + "累计伤害: " + (int)(acc % 200) + "/200");
                meta.setLore(lore);
            }
            weapon.setItemMeta(meta);
            if (bonus > 0) event.setDamage(event.getDamage() + bonus);
        }
    }
}
