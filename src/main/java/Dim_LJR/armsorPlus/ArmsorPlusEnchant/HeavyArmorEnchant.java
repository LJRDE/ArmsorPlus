package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 重甲 —— 穿戴时持续给予缓慢II+抗性提升II (胸甲)
public class HeavyArmorEnchant implements Listener {

    private final Map<UUID, Long> heavyArmorTimers = new HashMap<>();

    @EventHandler
    public void HeavyArmorHandler(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack chestplate = player.getInventory().getChestplate();
        int level = ArmsorEnchant.getEnchantLevel(chestplate, HeavyArmorKey);

        UUID pid = player.getUniqueId();
        Long last = heavyArmorTimers.get(pid);
        long now = System.currentTimeMillis();

        if (level <= 0) {
            if (last != null) {
                player.removePotionEffect(PotionEffectType.SLOWNESS);
                player.removePotionEffect(PotionEffectType.RESISTANCE);
                heavyArmorTimers.remove(pid);
            }
            return;
        }

        // 每5秒刷新一次效果
        if (last != null && (now - last) < 5000) return;
        heavyArmorTimers.put(pid, now);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 8 * 20, 1, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 8 * 20, 1, false, true));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        heavyArmorTimers.remove(event.getPlayer().getUniqueId());
    }
}
