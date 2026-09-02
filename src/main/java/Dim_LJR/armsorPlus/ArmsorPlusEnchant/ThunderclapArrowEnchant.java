package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;

// 惊雷 —— 命中召唤level道雷，未命中(击中方块)召唤1道雷 (弓, 满级III)
public class ThunderclapArrowEnchant implements Listener {

    @EventHandler
    public void ThunderclapArrowHandler(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player player)) return;

        int level = getThunderclapBowLevel(player);
        if (level <= 0) return;

        Location hitLoc = arrow.getLocation();
        World world = hitLoc.getWorld();

        if (event.getHitEntity() != null) {
            for (int i = 0; i < level; i++) {
                world.strikeLightning(hitLoc);
            }
        } else if (event.getHitBlock() != null) {
            world.strikeLightning(hitLoc);
        }
    }

    private int getThunderclapBowLevel(Player player) {
        int level = 0;
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() == BOW) {
            level = ArmsorEnchant.getEnchantLevel(mainHand, ThunderclapArrowKey);
        }
        if (level == 0) {
            ItemStack offHand = player.getInventory().getItemInOffHand();
            if (offHand.getType() == BOW) {
                level = ArmsorEnchant.getEnchantLevel(offHand, ThunderclapArrowKey);
            }
        }
        return level;
    }
}
