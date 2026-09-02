package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.SkeletonScepterKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

// 骷髅权杖 —— 降下箭雨
public class SkeletonScepterWeapon implements Listener {

    private static final Map<UUID, Integer> scepterCooldown = new HashMap<>();
    private static final Random RANDOM = new Random();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        scepterCooldown.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onScepterUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, SkeletonScepterKey) == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        int cd = scepterCooldown.getOrDefault(uuid, 0);
        if (cd > 0) {
            player.sendActionBar("§c权杖冷却中... " + (cd + 19) / 20 + "秒"); // 向上取整, 避免显示0秒
            return;
        }

        // 瞄准天空/虚空时 getTargetBlock 可能返回 null, 回退为面前方向10格
        Block targetBlock = player.getTargetBlock(null, 30);
        Location target = (targetBlock != null ? targetBlock.getLocation()
                : player.getLocation().add(player.getEyeLocation().getDirection().multiply(10)))
                .add(0.5, 1, 0.5);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 0.5f);

        new BukkitRunnable() {
            int wave = 0;

            @Override
            public void run() {
                wave++;
                if (wave > 4) {
                    cancel();
                    return;
                }

                for (int i = 0; i < 8; i++) {
                    double xOffset = (RANDOM.nextDouble() - 0.5) * 3;
                    double zOffset = (RANDOM.nextDouble() - 0.5) * 3;
                    Location arrowLoc = target.clone().add(xOffset, 15, zOffset);

                    // 射出箭矢
                    Arrow arrow = target.getWorld().spawn(arrowLoc, Arrow.class);
                    arrow.setVelocity(new Vector(0, -3, 0));
                    arrow.setDamage(12);
                    arrow.setShooter(player);
                    arrow.setMetadata("ScepterArrow", new FixedMetadataValue(getplugin, true));

                    target.getWorld().spawnParticle(Particle.CRIT, arrowLoc, 5, 0.3, 0.3, 0.3, 0);
                }
                target.getWorld().playSound(target, Sound.ENTITY_ARROW_SHOOT, 0.5f, 0.8f);
            }
        }.runTaskTimer(getplugin, 0L, 8L);

        scepterCooldown.put(uuid, 200); // 10秒冷却
        new BukkitRunnable() {
            int remaining = 200;
            @Override
            public void run() {
                remaining--;
                if (remaining <= 0) {
                    scepterCooldown.remove(uuid); // 冷却结束移除条目, 避免 Map 泄漏
                    cancel();
                } else {
                    scepterCooldown.put(uuid, remaining);
                }
            }
        }.runTaskTimer(getplugin, 1L, 1L);
    }
}
