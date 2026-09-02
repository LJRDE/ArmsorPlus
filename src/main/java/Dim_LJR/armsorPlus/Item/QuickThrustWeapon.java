package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.QuickThrustKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;
import static org.bukkit.Material.TRIDENT;

// 疾刺附魔 —— 右键三叉戟时速度提升 (10%*level)
public class QuickThrustWeapon implements Listener {

    @EventHandler
    public void onQuickThrustUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null) return;
        // 疾刺对三叉戟/长矛生效 (1.21+ 新增 SPEAR 类型)
        if (item.getType() != TRIDENT && !item.getType().name().endsWith("_SPEAR")) return;

        int level = ArmsorEnchant.getEnchantLevel(item, QuickThrustKey);
        if (level == 0) return;

        // 不取消事件: 保留原版右键功能 (三叉戟/火焰戟仍可投掷), 疾刺只附加速度提升
        Player player = event.getPlayer();
        int duration = 60 + level * 20; // 基础60 ticks + 每级20 ticks

        // 疾刺: 移动速度提升 level*10% (ADD_SCALAR 真实百分比修饰符)
        AttributeInstance speedAttr = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (speedAttr != null) {
            AttributeModifier speedMod = new AttributeModifier(
                    new NamespacedKey(getplugin, "ArmsorPlus_QuickThrustSpeed"),
                    0.1 * level, AttributeModifier.Operation.ADD_SCALAR,
                    EquipmentSlotGroup.HAND);
            speedAttr.addTransientModifier(speedMod);
            Bukkit.getScheduler().runTaskLater(getplugin, () -> speedAttr.removeModifier(speedMod), duration);
        }

        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 10, 0.3, 0.1, 0.3, 0.05);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 0.5f, 1.8f);
        player.sendActionBar("§e⚡ 疾刺！速度提升 " + (level * 10) + "%");
    }
}
