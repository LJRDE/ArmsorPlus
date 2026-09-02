package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.EnchantUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Random;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.IllusionBladeKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

// 幻影之刃 —— 幻影分身 / 幻影假身
public class IllusionBladeWeapon implements Listener {

    private static final Random RANDOM = new Random();

    // 攻击时35%召唤幻影分身, 额外3点真实伤害
    @EventHandler
    public void onIllusionBladeAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, IllusionBladeKey) == 0) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        if (RANDOM.nextInt(100) < 35) {
            if (target.isDead()) return;
            target.damage(3, player); // 幻影分身真实伤害
            target.getWorld().spawnParticle(Particle.PORTAL,
                    target.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.1);
            player.sendActionBar("§d⚔ 幻影分身！额外 3 点真实伤害");
        }
    }

    // 击杀时25%生成幻影假身吸引附近怪物
    @EventHandler
    public void onIllusionBladeKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) return;
        Entity killer = entity.getKiller();
        if (!(killer instanceof Player player)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, IllusionBladeKey) == 0) return;

        if (RANDOM.nextInt(100) >= 25) return;
        Location loc = entity.getLocation();
        World world = loc.getWorld();
        // 生成幻影假身 (盔甲架标记, 5秒后消失)
        ArmorStand clone = world.spawn(loc, ArmorStand.class, s -> {
            s.setVisible(false);
            s.setGravity(false);
            s.setInvulnerable(true);
            s.setMarker(true);
            s.setMetadata("ArmsorPlus_IllusionClone", new FixedMetadataValue(getplugin, player.getUniqueId().toString()));
        });
        // 附近怪物把假身当目标
        for (Entity e : world.getNearbyEntities(loc, 10, 10, 10)) {
            if (e instanceof Mob mob && !(e instanceof Player)) {
                mob.setTarget(clone);
            }
        }
        world.spawnParticle(Particle.PORTAL, loc.add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.2);
        player.sendActionBar("§d🫥 幻影假身！附近怪物被迷惑了");
        Bukkit.getScheduler().runTaskLater(getplugin, clone::remove, 100L);
    }
}
