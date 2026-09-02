package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.FrostBowKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

// 寒冰弓 —— 射出时额外发射2支寒冰箭 + 冰块粒子
public class FrostBowWeapon implements Listener {

    @EventHandler
    public void onFrostBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack bow = event.getBow();
        if (bow == null || ArmsorEnchant.getEnchantLevel(bow, FrostBowKey) == 0) return;

        Location eye = player.getEyeLocation();
        Vector direction = eye.getDirection();
        World world = player.getWorld();

        // 在玩家前方生成两束冰粒子路径
        world.spawnParticle(Particle.SNOWFLAKE, eye.add(direction.clone().multiply(0.5)), 20, 0.3, 0.3, 0.3, 0.05);

        // 额外发射2支寒冰箭 (延迟几tick发射避免碰撞)
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) return;
                for (int i = -1; i <= 1; i += 2) {
                    Vector offset = new Vector(-direction.getZ() * i * 0.15, 0, direction.getX() * i * 0.15);
                    Arrow iceArrow = player.launchProjectile(Arrow.class,
                            direction.clone().add(offset).normalize().multiply(3.0));
                    iceArrow.setMetadata("FrostArrow", new FixedMetadataValue(getplugin, true));
                    iceArrow.setDamage(25);
                    iceArrow.setColor(Color.AQUA);

                    world.spawnParticle(Particle.SNOWFLAKE, iceArrow.getLocation(), 5, 0.1, 0.1, 0.1, 0);
                }
            }
        }.runTaskLater(getplugin, 2L);

        // 追踪所有刚射出的寒冰箭的视觉效果
        Arrow mainArrow = (Arrow) event.getProjectile();
        mainArrow.setMetadata("FrostArrow", new FixedMetadataValue(getplugin, true));
        mainArrow.setDamage(25);
        mainArrow.setColor(Color.AQUA);
    }

    @EventHandler
    public void onFrostArrowHit(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Projectile proj && proj.getShooter() instanceof Player) {
            if (proj.hasMetadata("FrostArrow") && event.getEntity() instanceof LivingEntity target) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 2));
                target.setFreezeTicks(60);
                target.getWorld().spawnParticle(Particle.ITEM_SNOWBALL,
                        target.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.3);
                target.getWorld().playSound(target.getLocation(), Sound.BLOCK_GLASS_BREAK, 0.5f, 0.5f);
            }
        }
    }
}
