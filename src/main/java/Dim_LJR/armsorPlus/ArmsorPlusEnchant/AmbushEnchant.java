package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 伏击 —— 使用盾牌后获得力量效果 (盾牌, 满级III)
public class AmbushEnchant implements Listener {

    private static final Set<UUID> AMBUSH_CD = new HashSet<>();

    @EventHandler
    public void AmbushHandler(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        ItemStack offhand = player.getInventory().getItemInOffHand();
        int level = ArmsorEnchant.getEnchantLevel(offhand, AmbushKey);
        if (level <= 0) return;
        if (player.isBlocking()) return;
        if (player.hasMetadata("ArmsorPlus_AmbushCD")) return;

        int duration = level * 4; // 0.2*level秒 = level*4 ticks
        player.setMetadata("ArmsorPlus_AmbushCD", new FixedMetadataValue(getplugin, true));
        AMBUSH_CD.add(player.getUniqueId());

        // 伏击: 伤害提升 level*15% (ADD_SCALAR 真实百分比修饰符)
        AttributeInstance atkAttr = player.getAttribute(Attribute.ATTACK_DAMAGE);
        if (atkAttr != null) {
            AttributeModifier ambushMod = new AttributeModifier(
                    new NamespacedKey(getplugin, "ArmsorPlus_AmbushDamage"),
                    0.15 * level, AttributeModifier.Operation.ADD_SCALAR,
                    EquipmentSlotGroup.HAND);
            atkAttr.addTransientModifier(ambushMod);
            Bukkit.getScheduler().runTaskLater(getplugin,
                    () -> atkAttr.removeModifier(ambushMod), duration);
        }

        Bukkit.getScheduler().runTaskLater(getplugin, () -> {
            AMBUSH_CD.remove(player.getUniqueId());
            player.removeMetadata("ArmsorPlus_AmbushCD", getplugin);
        }, duration);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        AMBUSH_CD.remove(event.getPlayer().getUniqueId());
    }
}
