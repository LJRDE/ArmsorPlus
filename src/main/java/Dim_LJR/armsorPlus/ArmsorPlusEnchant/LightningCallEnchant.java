package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 唤雷 —— 无视天气召唤level道雷 (三叉戟, 满级III)
public class LightningCallEnchant implements Listener {

    @EventHandler
    public void LightningCallHandler(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Trident trident)) return;
        if (!(trident.getShooter() instanceof Player player)) return;

        int level = ArmsorEnchant.getEnchantLevel(player.getInventory().getItemInMainHand(), LightningCallKey);
        if (level <= 0) return;

        Location hitLoc = trident.getLocation();
        World world = hitLoc.getWorld();

        for (int i = 0; i < level; i++) {
            world.strikeLightning(hitLoc);
        }
    }
}
