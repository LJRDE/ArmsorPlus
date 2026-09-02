package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 丰收 —— 概率多倍收获 (锄头, 30%*level概率level+1倍, 满级III)
public class HarvestEnchant implements Listener {

    @EventHandler
    public void HarvestHandler(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack hoe = player.getInventory().getItemInMainHand();
        int level = ArmsorEnchant.getEnchantLevel(hoe, HarvestKey);
        if (level <= 0) return;

        Material block = event.getBlock().getType();
        if (!isCrop(block)) return;

        if (!EnchantUtil.percent(30 * level)) return;

        event.setDropItems(false);
        Collection<ItemStack> drops = event.getBlock().getDrops(hoe);
        for (int i = 0; i <= level; i++) {
            for (ItemStack drop : drops) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop.clone());
            }
        }
    }

    // 检查是否为作物 (包括甜浆果)
    private boolean isCrop(Material mat) {
        return mat == Material.WHEAT || mat == Material.CARROTS || mat == Material.POTATOES
                || mat == Material.BEETROOTS || mat == Material.NETHER_WART
                || mat == Material.SWEET_BERRY_BUSH || mat == Material.COCOA;
    }
}
