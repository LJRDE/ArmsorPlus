package Dim_LJR.armsorPlus.Item;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.EnchantUtil;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.WebBowKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.WebBowUsesKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

// 盘丝弓 —— 攻击时在敌方周围生成蜘蛛网30秒, PDC计数9次后损坏
public class WebBowWeapon implements Listener {

    @EventHandler
    public void onWebBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack bow = event.getBow();
        if (bow == null || ArmsorEnchant.getEnchantLevel(bow, WebBowKey) == 0) return;
        // 给箭打标记, 命中时不再依赖主手判断
        event.getProjectile().setMetadata("WebBow", new FixedMetadataValue(getplugin, true));

        // PDC使用次数管理
        int uses = ArmsorEnchant.getEnchantLevel(bow, WebBowUsesKey);
        if (uses == 0) uses = 9;
        uses--;
        final int finalUses = uses;
        bow.editMeta(meta -> meta.getPersistentDataContainer()
                .set(WebBowUsesKey, PersistentDataType.INTEGER, finalUses));
        EnchantUtil.updateUsesLore(bow, finalUses);
        if (finalUses <= 0) {
            bow.setAmount(0);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onWebBowHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow arrow)) return;
        if (!arrow.hasMetadata("WebBow")) return;
        if (!(arrow.getShooter() instanceof Player player)) return;

        Location loc = event.getEntity().getLocation();
        World world = loc.getWorld();
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                Location webLoc = loc.clone().add(x, 0, z);
                if (webLoc.getBlock().getType() == Material.AIR) {
                    webLoc.getBlock().setType(Material.COBWEB);
                    Bukkit.getScheduler().runTaskLater(getplugin, () -> {
                        if (webLoc.getBlock().getType() == Material.COBWEB) {
                            webLoc.getBlock().setType(Material.AIR);
                        }
                    }, 600L); // 30秒后恢复
                }
            }
        }
        world.spawnParticle(Particle.ITEM_COBWEB, loc.add(0, 1, 0), 20, 1, 1, 1, 0.1);
        world.playSound(loc, Sound.BLOCK_SLIME_BLOCK_PLACE, 0.5f, 0.8f);
    }
}
