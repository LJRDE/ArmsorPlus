package Dim_LJR.armsorPlus.Item;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.EnchantUtil;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.ExplosionBowKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.ExplosionBowUsesKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

// 爆炸弓 —— 攻击时在敌方周围爆炸, PDC计数9次后损坏
public class ExplosionBowWeapon implements Listener {

    @EventHandler
    public void onExplosionBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack bow = event.getBow();
        if (bow == null || ArmsorEnchant.getEnchantLevel(bow, ExplosionBowKey) == 0) return;
        // 给箭打标记, 命中时不再依赖主手判断
        event.getProjectile().setMetadata("ExplosionBow", new FixedMetadataValue(getplugin, true));

        // PDC使用次数管理
        int uses = ArmsorEnchant.getEnchantLevel(bow, ExplosionBowUsesKey);
        if (uses == 0) uses = 9;
        uses--;
        final int finalUses = uses;
        bow.editMeta(meta -> meta.getPersistentDataContainer()
                .set(ExplosionBowUsesKey, PersistentDataType.INTEGER, finalUses));
        EnchantUtil.updateUsesLore(bow, finalUses);
        if (finalUses <= 0) {
            bow.setAmount(0);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onExplosionBowHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow arrow)) return;
        if (!arrow.hasMetadata("ExplosionBow")) return;
        if (!(arrow.getShooter() instanceof Player player)) return;

        Location loc = event.getEntity().getLocation();
        loc.getWorld().createExplosion(loc, 2.0f, false, false);
        loc.getWorld().spawnParticle(Particle.EXPLOSION, loc.add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0);
    }
}
