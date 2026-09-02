package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;

// 弹道 & 狙击 —— 提升箭矢速度
public class ArrowSpeedEnchant implements Listener {

    @EventHandler
    public void ArrowSpeedHander(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Arrow)) return;
        Arrow arrow = (Arrow) event.getEntity();
        if (!(arrow.getShooter() instanceof LivingEntity)) return;
        LivingEntity shooter = (LivingEntity) arrow.getShooter();

        // 获取射出箭矢的弓/弩 (弩发射时 getActiveItem() 不可靠, 需查主手/副手)
        ItemStack weapon = null;
        if (shooter instanceof Player p) {
            ItemStack main = p.getInventory().getItemInMainHand();
            if (main.getType() == BOW || main.getType() == CROSSBOW) {
                weapon = main;
            } else {
                ItemStack off = p.getInventory().getItemInOffHand();
                if (off.getType() == BOW || off.getType() == CROSSBOW) {
                    weapon = off;
                }
            }
        } else if (shooter.getEquipment() != null) {
            weapon = shooter.getEquipment().getItemInMainHand();
        }
        if (weapon == null || (weapon.getType() != BOW && weapon.getType() != CROSSBOW)) return;

        int speedLevel = ArmsorEnchant.getEnchantLevel(weapon, ArrowSpeed);
        if (speedLevel == 0) return;

        Vector velocity = arrow.getVelocity();
        velocity.multiply(speedLevel);

        int snipeLevel = ArmsorEnchant.getEnchantLevel(weapon, Sniping);
        if (snipeLevel != 0) {
            velocity.multiply(5L * snipeLevel);
        }
        arrow.setVelocity(velocity);
    }
}
