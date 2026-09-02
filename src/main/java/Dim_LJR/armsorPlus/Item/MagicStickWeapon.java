package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.MagicStickKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

// 法杖 —— 左键发射魔法球 (SmallFireball) 直射攻击
public class MagicStickWeapon implements Listener {

    @EventHandler
    public void onMagicStickLeftClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, MagicStickKey) == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        // 发射魔法球 (SmallFireball直射)
        SmallFireball fireball = player.launchProjectile(SmallFireball.class);
        fireball.setVelocity(player.getEyeLocation().getDirection().multiply(2.0));
        fireball.setMetadata("MagicStick", new FixedMetadataValue(getplugin, true));

        // 飞行粒子追踪
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                tick++;
                if (!fireball.isValid() || fireball.isDead() || tick > 80) {
                    if (fireball.isValid()) fireball.remove();
                    cancel();
                    return;
                }
                Location loc = fireball.getLocation();
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 3, 0.15, 0.15, 0.15, 0.02);
                loc.getWorld().spawnParticle(Particle.SMOKE, loc, 1, 0.1, 0.1, 0.1, 0.01);
            }
        }.runTaskTimer(getplugin, 0L, 1L);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.5f, 1.5f);
    }

    @EventHandler
    public void onMagicStickHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof SmallFireball fireball)) return;
        if (!fireball.hasMetadata("MagicStick")) return;

        if (event.getEntity() instanceof LivingEntity target) {
            event.setDamage(20);
            target.getWorld().spawnParticle(Particle.EXPLOSION, target.getLocation().add(0, 1, 0),
                    2, 0.3, 0.3, 0.3, 0);
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 1.0f);
            target.setFireTicks(60);
        }
    }
}
