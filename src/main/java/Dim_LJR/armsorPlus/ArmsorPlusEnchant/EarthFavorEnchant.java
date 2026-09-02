package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 地之眷顾 —— 挖泥土概率掉落金粒/铁粒 (铲子)
public class EarthFavorEnchant implements Listener {

    private static final Material[] DIRT_TYPES = {
            Material.DIRT, Material.GRASS_BLOCK, Material.PODZOL, Material.MYCELIUM,
            Material.DIRT_PATH, Material.ROOTED_DIRT, Material.COARSE_DIRT,
            Material.FARMLAND, Material.MUD
    };

    private boolean isDirt(Material m) {
        for (Material dirt : DIRT_TYPES) {
            if (m == dirt) return true;
        }
        return false;
    }

    @EventHandler
    public void EarthFavorHandler(BlockBreakEvent event) {
        if (!isDirt(event.getBlock().getType())) return;

        Player player = event.getPlayer();
        ItemStack shovel = player.getInventory().getItemInMainHand();
        int level = ArmsorEnchant.getEnchantLevel(shovel, EarthFavorKey);
        if (level <= 0) return;

        if (!EnchantUtil.percent(10 * level)) return;

        Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        World world = event.getBlock().getWorld();
        if (EnchantUtil.percent(50)) {
            world.dropItemNaturally(loc, new ItemStack(Material.GOLD_NUGGET, 1));
        } else {
            world.dropItemNaturally(loc, new ItemStack(Material.IRON_NUGGET, 1));
        }
    }
}
