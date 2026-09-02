package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 熔岩行者 —— 岩浆行走5格内变为岩浆块，5秒后恢复 (靴子)
public class LavaWalkerEnchant implements Listener {

    @EventHandler
    public void LavaWalkerHandler(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack boots = player.getEquipment().getBoots();
        int level = ArmsorEnchant.getEnchantLevel(boots, LavaWalkerKey);
        if (level <= 0) return;

        Location loc = player.getLocation();
        World world = loc.getWorld();

        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                for (int y = -1; y <= 0; y++) {
                    Location check = loc.clone().add(x, y, z);
                    if (check.getBlock().getType() == Material.LAVA) {
                        check.getBlock().setType(Material.MAGMA_BLOCK);
                        Bukkit.getScheduler().runTaskLater(getplugin, () -> {
                            if (check.getBlock().getType() == Material.MAGMA_BLOCK) {
                                check.getBlock().setType(Material.LAVA);
                            }
                        }, 100L);
                    }
                }
            }
        }
    }
}
