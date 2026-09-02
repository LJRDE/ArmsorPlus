package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;

// 金刚钻 —— 概率瞬间挖掉黑曜石 (镐子)
public class DiamondDrillEnchant implements Listener {

    @EventHandler
    public void DiamondDrillHandler(BlockBreakEvent event) {
        if (event.getBlock().getType() != OBSIDIAN && event.getBlock().getType() != CRYING_OBSIDIAN) return;
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        int level = ArmsorEnchant.getEnchantLevel(tool, DiamondDrillKey);
        if (level == 0) return;
        if (!EnchantUtil.percent(20 * level)) {
            event.setCancelled(true);
            return;
        }
        PlayerSettings.notifyActionBar(player, ChatColor.AQUA + "金刚钻触发！瞬间挖掉黑曜石");
    }
}
