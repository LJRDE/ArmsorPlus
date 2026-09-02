package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.RainSwordKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

// 雨御前 —— 右键3秒隐身+无敌+周围生物缓慢255/挖掘疲劳3秒 (冷却10s)
public class RainSwordWeapon implements Listener {

    private static final Map<UUID, Integer> rainSwordCooldown = new HashMap<>();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        rainSwordCooldown.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onRainSwordUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, RainSwordKey) == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        int cd = rainSwordCooldown.getOrDefault(uuid, 0);
        if (cd > 0) {
            player.sendActionBar("§c雨御前冷却中... " + (cd + 19) / 20 + "秒");
            return;
        }

        // 3秒隐身+无敌
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0, false, false));
        player.setInvulnerable(true);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.05);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.0f, 1.5f);

        // 给周围所有生物施加缓慢255 + 挖掘疲劳 3秒
        for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
            if (entity instanceof LivingEntity living && living != player) {
                living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 254, false, false));
                living.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 60, 2, false, false));
            }
        }
        player.getWorld().spawnParticle(Particle.SNOWFLAKE, player.getLocation(), 50, 5, 3, 5, 0.1);
        player.sendActionBar("§b雨御前！3秒隐身 + 冰霜领域");

        // 3秒后取消无敌
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setInvulnerable(false);
                player.sendActionBar("§c隐身效果已结束");
            }
        }.runTaskLater(getplugin, 60L);

        rainSwordCooldown.put(uuid, 200); // 10秒冷却
        new BukkitRunnable() {
            int remaining = 200;
            @Override
            public void run() {
                remaining--;
                if (remaining <= 0) {
                    rainSwordCooldown.remove(uuid); // 冷却结束移除条目, 避免 Map 泄漏
                    cancel();
                } else {
                    rainSwordCooldown.put(uuid, remaining);
                }
            }
        }.runTaskTimer(getplugin, 1L, 1L);
    }
}
