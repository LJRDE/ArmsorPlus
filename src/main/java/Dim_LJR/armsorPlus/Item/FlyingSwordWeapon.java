package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.FlyingSwordKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

// 飞天御剑 —— 右键起飞/收起，按W沿视角方向飞行
public class FlyingSwordWeapon implements Listener {

    private static final Map<UUID, Boolean> flyingSwordActive = new HashMap<>();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        flyingSwordActive.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onFlyingSwordUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, FlyingSwordKey) == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Boolean active = flyingSwordActive.getOrDefault(uuid, false);
        if (active) {
            // 停止飞行
            flyingSwordActive.remove(uuid); // 移除状态条目, 避免 Map 泄漏
            player.setFlying(false);
            player.setAllowFlight(false);
            player.setFlySpeed(0.1f);
            player.sendActionBar("§6飞天御剑 已收起");
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.8f, 1.0f);
        } else {
            // 开启飞行 (按W沿视角方向前进，松开即停)
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setFlySpeed(0.4f); // 8m/s, W按下时沿视角方向移动
            flyingSwordActive.put(uuid, true);
            player.sendActionBar("§6飞天御剑 已起航！");
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 0.5f, 2.0f);

            // 脚下生成飞剑显示 + 检测是否仍手持飞天御剑
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!flyingSwordActive.getOrDefault(uuid, false) || !player.isOnline() || player.isDead()) {
                        if (player.isOnline() && !player.isDead()) {
                            player.setFlying(false);
                            player.setAllowFlight(false);
                            player.setFlySpeed(0.1f);
                        }
                        flyingSwordActive.remove(uuid);
                        cancel();
                        return;
                    }
                    // 切物品自动取消飞行
                    ItemStack currentItem = player.getInventory().getItemInMainHand();
                    if (ArmsorEnchant.getEnchantLevel(currentItem, FlyingSwordKey) == 0) {
                        flyingSwordActive.remove(uuid); // 移除状态条目
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        player.setFlySpeed(0.1f);
                        player.sendActionBar("§c已收起飞天御剑");
                        cancel();
                        return;
                    }
                    if (!player.isFlying()) {
                        player.setFlying(true);
                    }

                    Location foot = player.getLocation().subtract(0, 0.5, 0);
                    player.getWorld().spawnParticle(Particle.END_ROD, foot, 1, 0, 0, 0, 0);
                    player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, foot, 1, 0.5, 0, 0.5, 0);
                }
            }.runTaskTimer(getplugin, 0L, 2L);
        }
    }
}
