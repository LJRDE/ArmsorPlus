package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.ThrowingAxeKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

// 飞斧 —— 右键蓄力3秒后飞出
public class ThrowingAxeWeapon implements Listener {

    private static final Map<UUID, Long> axeChargeStart = new HashMap<>();
    private static final Map<UUID, Integer> axeChargeTask = new HashMap<>();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Integer task = axeChargeTask.remove(uuid);
        if (task != null) Bukkit.getScheduler().cancelTask(task);
        axeChargeStart.remove(uuid);
    }

    @EventHandler
    public void onThrowingAxeClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, ThrowingAxeKey) == 0) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // 取消正在进行的蓄力
        if (axeChargeTask.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(axeChargeTask.remove(uuid));
            axeChargeStart.remove(uuid);
            player.sendActionBar("§c飞斧蓄力已取消");
            event.setCancelled(true);
            return;
        }

        // 开始蓄力
        event.setCancelled(true);
        axeChargeStart.put(uuid, System.currentTimeMillis());
        player.sendActionBar("§e飞斧蓄力中... 请保持右键");
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 4, false, false));

        int taskId = new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (!player.isOnline() || player.isDead()) {
                    axeChargeStart.remove(uuid);
                    axeChargeTask.remove(uuid);
                    cancel();
                    return;
                }

                ItemStack currentItem = player.getInventory().getItemInMainHand();
                if (ArmsorEnchant.getEnchantLevel(currentItem, ThrowingAxeKey) == 0) {
                    axeChargeStart.remove(uuid);
                    axeChargeTask.remove(uuid);
                    player.sendActionBar("§c请保持手持飞斧");
                    cancel();
                    return;
                }

                tick++;
                if (tick >= 30) { // 3秒 (30 ticks)
                    throwAxe(player);
                    axeChargeStart.remove(uuid);
                    axeChargeTask.remove(uuid);
                    cancel();
                } else if (tick % 10 == 0) {
                    player.sendActionBar("§e飞斧蓄力 §6" + (tick / 10 + 1) + "§e/§63");
                }
            }
        }.runTaskTimer(getplugin, 0L, 2L).getTaskId();

        axeChargeTask.put(uuid, taskId);
    }

    private void throwAxe(Player player) {
        ItemStack axe = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(axe, ThrowingAxeKey) == 0) return;

        // 创建投掷物 (雪球用于检测路径)
        Location eye = player.getEyeLocation();
        Vector direction = eye.getDirection();
        Snowball projectile = player.launchProjectile(Snowball.class, direction);
        projectile.setMetadata("ThrowingAxe", new FixedMetadataValue(getplugin, true));
        projectile.setVelocity(direction.multiply(2.5));

        player.sendActionBar("§6飞斧已掷出！");
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.2f);

        // 消耗耐久 (meta 可能为 null, 加保护)
        if (player.getGameMode() != GameMode.CREATIVE) {
            ItemMeta axeMeta = axe.getItemMeta();
            if (axeMeta instanceof Damageable meta) {
                meta.setDamage(meta.getDamage() + 1);
                if (meta.getDamage() >= axe.getType().getMaxDurability()) {
                    axe.setAmount(0);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                } else {
                    axe.setItemMeta(meta);
                }
            }
        }

        // 追踪投掷物
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                tick++;
                if (!projectile.isValid() || projectile.isDead() || tick > 40) {
                    projectile.remove();
                    cancel();
                    return;
                }

                Location loc = projectile.getLocation();
                loc.getWorld().spawnParticle(Particle.CRIT, loc, 3, 0.2, 0.2, 0.2, 0);
                loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, loc, 1, 0.3, 0, 0.3, 0);

                for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1.5, 1.5, 1.5)) {
                    if (entity instanceof LivingEntity target && target != player && !target.isDead()) {
                        target.damage(20, player);
                        if (target.isDead()) { // 目标已被飞斧击杀, 直接结束
                            projectile.remove();
                            cancel();
                            return;
                        }
                        loc.getWorld().spawnParticle(Particle.EXPLOSION, loc, 1, 0, 0, 0, 0);
                        loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.0f);
                        projectile.remove();
                        cancel();
                        return;
                    }
                }
            }
        }.runTaskTimer(getplugin, 0L, 1L);
    }
}
