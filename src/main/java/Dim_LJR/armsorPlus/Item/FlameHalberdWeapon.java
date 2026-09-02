package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.EnchantUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.FlameHalberdKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

// 火焰戟 —— 攻击额外30火焰伤害 + 投掷时3×3灼烧
public class FlameHalberdWeapon implements Listener {

    @EventHandler
    public void onFlameHalberdAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        // 伤害溯源: 只有真实近战攻击才附加火焰伤害, 荆棘(THORNS)反弹伤害的 damager 是持戟玩家,
        // 但并非玩家主动挥戟, 必须排除, 否则荆棘会造成额外火焰伤害
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, FlameHalberdKey) == 0) return;

        // 额外30点火焰伤害 (如果是三叉戟近战攻击)
        if (event.getEntity() instanceof LivingEntity target) {
            target.setFireTicks(100);
            target.getWorld().spawnParticle(Particle.FLAME,
                    target.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_BLAZE_HURT, 1.0f, 1.0f);
            event.setDamage(event.getDamage() + 30);
        }
    }

    @EventHandler
    public void onFlameHalberdLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, FlameHalberdKey) == 0) return;

        Projectile proj = event.getEntity();
        proj.setMetadata("FlameHalberd", new FixedMetadataValue(getplugin, true));

        // 追踪投掷物，沿途3×3灼烧
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                tick++;
                if (!proj.isValid() || proj.isDead() || tick > 100) {
                    cancel();
                    return;
                }

                Location loc = proj.getLocation();
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 10, 0.3, 0.3, 0.3, 0.02);
                loc.getWorld().spawnParticle(Particle.SMOKE, loc, 5, 0.3, 0.3, 0.3, 0.01);

                for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1.5, 1.5, 1.5)) {
                    if (entity instanceof LivingEntity target && target != player) {
                        target.setFireTicks(60);
                    }
                }
            }
        }.runTaskTimer(getplugin, 0L, 1L);
    }

    @EventHandler
    public void onFlameHalberdHit(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Trident trident && trident.hasMetadata("FlameHalberd")) {
            if (event.getEntity() instanceof LivingEntity target) {
                target.setFireTicks(100);
                target.getWorld().createExplosion(target.getLocation(), 1.5f, false, false);
                target.getWorld().spawnParticle(Particle.LAVA,
                        target.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 1);
            }
        }
    }
}
