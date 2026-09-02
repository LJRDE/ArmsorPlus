package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 自动种植 —— 采集作物时自动补种副手种子 (锄头)
public class AutoPlantEnchant implements Listener {

    @EventHandler
    public void AutoPlantHandler(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack hoe = player.getInventory().getItemInMainHand();
        int level = ArmsorEnchant.getEnchantLevel(hoe, AutoPlantKey);
        if (level <= 0) return;

        Material block = event.getBlock().getType();
        if (!isCrop(block)) return;

        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (offHand == null || offHand.getType() == Material.AIR) return;

        Material seedType = offHand.getType();
        Material soil = event.getBlock().getLocation().subtract(0, 1, 0).getBlock().getType();
        if (soil != Material.FARMLAND && soil != Material.SOUL_SAND) return;

        Material cropToPlant = getCropFromSeed(seedType);
        if (cropToPlant == null) return;

        Bukkit.getScheduler().runTaskLater(getplugin, () -> {
            Location loc = event.getBlock().getLocation();
            if (loc.getBlock().getType() == Material.AIR) {
                loc.getBlock().setType(cropToPlant);
                // 1 tick 后重新读取副手, 避免玩家换物品时减错
                if (player.getGameMode() != GameMode.CREATIVE) {
                    ItemStack currentOffHand = player.getInventory().getItemInOffHand();
                    if (currentOffHand.getType() == seedType) {
                        currentOffHand.setAmount(currentOffHand.getAmount() - 1);
                    }
                }
            }
        }, 1L);
    }

    private Material getCropFromSeed(Material seed) {
        return switch (seed) {
            case WHEAT_SEEDS -> Material.WHEAT;
            case CARROT -> Material.CARROTS;
            case POTATO -> Material.POTATOES;
            case BEETROOT_SEEDS -> Material.BEETROOTS;
            case SWEET_BERRIES -> Material.SWEET_BERRY_BUSH;
            case COCOA_BEANS -> Material.COCOA;
            default -> null;
        };
    }

    private boolean isCrop(Material mat) {
        return mat == Material.WHEAT || mat == Material.CARROTS || mat == Material.POTATOES
                || mat == Material.BEETROOTS || mat == Material.NETHER_WART
                || mat == Material.SWEET_BERRY_BUSH || mat == Material.COCOA;
    }
}
