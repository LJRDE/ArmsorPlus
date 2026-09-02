package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 蓄爆 —— 概率射出爆炸烟花 (弓/弩)
public class ExplosiveArrowEnchant implements Listener {

    @EventHandler
    public void ExplosiveArrowHandler(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack bow = event.getBow();
        if (bow == null) return;

        int level = ArmsorEnchant.getEnchantLevel(bow, ExplosiveArrowKey);
        if (level <= 0) return;

        // 蓄爆与弹道/狙击不共存
        if (ArmsorEnchant.getEnchantLevel(bow, ArrowSpeed) > 0
                || ArmsorEnchant.getEnchantLevel(bow, Sniping) > 0) return;

        event.setCancelled(true);

        // 在玩家前方生成烟花，避免在体内爆炸
        Location spawnLoc = player.getEyeLocation().add(
                player.getLocation().getDirection().multiply(1.5));

        Firework firework = (Firework) player.getWorld().spawnEntity(
                spawnLoc, org.bukkit.entity.EntityType.FIREWORK_ROCKET);

        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.RED).withFade(Color.ORANGE)
                .with(FireworkEffect.Type.BURST).withTrail().build());
        meta.setPower(1);
        firework.setFireworkMeta(meta);

        Vector velocity = player.getLocation().getDirection()
                .multiply(event.getForce() * 3.0);
        firework.setVelocity(velocity);
        firework.setMetadata("ExplosiveArrow",
                new FixedMetadataValue(getplugin, level));

        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
        PlayerSettings.notify(player, org.bukkit.ChatColor.GOLD + "蓄爆效果触发！发射了烟花火箭");
    }

    // 蓄爆烟花爆炸时造成范围伤害
    @EventHandler
    public void ExplosiveArrowExplode(FireworkExplodeEvent event) {
        Firework firework = event.getEntity();
        if (!firework.hasMetadata("ExplosiveArrow")) return;

        int level = firework.getMetadata("ExplosiveArrow").get(0).asInt();
        Location loc = firework.getLocation();

        loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(),
                2.0f, false, false);

        double damage = 30.0 * level;
        for (Entity entity : loc.getWorld().getNearbyEntities(loc, 3, 3, 3)) {
            if (entity instanceof LivingEntity le && entity != firework && !le.isDead()) {
                le.damage(damage);
            }
        }
    }
}
