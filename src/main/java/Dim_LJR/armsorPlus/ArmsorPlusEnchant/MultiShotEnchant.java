package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 千重射击 —— 射击时多射level支箭 (弩, 满级III)
public class MultiShotEnchant implements Listener {

    @EventHandler
    public void MultiShotHandler(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) return;
        if (!(arrow.getShooter() instanceof Player player)) return;

        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (weapon.getType() != Material.CROSSBOW) return;
        int level = ArmsorEnchant.getEnchantLevel(weapon, MultiShotKey);
        if (level <= 0) return;

        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection();
        World world = player.getWorld();

        for (int i = 0; i < level; i++) {
            double spread = (Math.random() - 0.5) * 0.3;
            Vector offset = new Vector(-dir.getZ() * spread, (Math.random() - 0.5) * 0.15, dir.getX() * spread);
            Arrow extraArrow = world.spawn(eye, Arrow.class);
            extraArrow.setShooter(player);
            extraArrow.setVelocity(dir.clone().add(offset).normalize().multiply(3.0));
        }
    }
}
