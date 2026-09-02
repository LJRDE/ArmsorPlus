package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 卸力 —— 受到100+伤害时拆分为 (level+1) 段, 每段间隔4tick (胸甲, 最先结算)
public class DamageDispersalEnchant implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void DamageDispersalHandler(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity le && EnchantUtil.PIERCING_ACTIVE.contains(le.getUniqueId())) return;
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null) return;

        int ddLvl = ArmsorEnchant.getEnchantLevel(chestplate, DamageDispersalKey);
        if (ddLvl <= 0) return;

        UUID pid = player.getUniqueId();
        if (!EnchantUtil.DAMAGE_SPLIT_ACTIVE.contains(pid)) {
            double total = event.getDamage();
            if (total >= 100) {
                int parts = ddLvl + 1;
                double part = total / parts;
                EnchantUtil.DAMAGE_SPLIT_ACTIVE.add(pid);
                event.setDamage(part); // 第一段立即结算, 剩余段延迟 4*i tick
                for (int i = 1; i < parts; i++) {
                    int delay = i * 4;
                    boolean last = (i == parts - 1);
                    Bukkit.getScheduler().runTaskLater(getplugin, () -> {
                        if (player.isOnline() && !player.isDead()) {
                            player.setNoDamageTicks(0); // 绕过受伤无敌帧, 保证每段都生效
                            player.damage(part);
                        }
                        if (last) EnchantUtil.DAMAGE_SPLIT_ACTIVE.remove(pid);
                    }, delay);
                }
                PlayerSettings.notify(player, ChatColor.DARK_GREEN + "卸力: " + total + "点伤害拆分为" + parts + "段");
            }
        }
    }
}
