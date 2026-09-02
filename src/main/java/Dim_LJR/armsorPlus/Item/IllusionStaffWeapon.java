package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.EnchantUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.IllusionStaffKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

// 幻惑法杖 —— 失明攻击 / 幻术飞弹
public class IllusionStaffWeapon implements Listener {

    private static final Random RANDOM = new Random();

    // 近战攻击30%使目标失明
    @EventHandler
    public void onIllusionStaffAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, IllusionStaffKey) == 0) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        if (RANDOM.nextInt(100) < 30) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, false, true));
            target.getWorld().spawnParticle(Particle.WITCH,
                    target.getLocation().add(0, 1, 0), 15, 0.3, 0.3, 0.3, 0.1);
            player.sendActionBar("§b🌀 幻惑！目标陷入失明");
        }
    }

    // 右键发射幻术飞弹
    @EventHandler
    public void onIllusionStaffRightClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, IllusionStaffKey) == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        Snowball missile = player.launchProjectile(Snowball.class);
        missile.setMetadata("ArmsorPlus_IllusionStaff", new FixedMetadataValue(getplugin, true));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.WITCH, player.getLocation().add(0, 1, 0), 10, 0.3, 0.5, 0.3, 0.1);
    }

    // 幻术飞弹命中: 10点魔法伤害 + 反胃 + 失明
    @EventHandler
    public void onIllusionStaffHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball snowball)) return;
        if (!snowball.hasMetadata("ArmsorPlus_IllusionStaff")) return;

        if (event.getHitEntity() instanceof LivingEntity target
                && snowball.getShooter() instanceof Player player) {
            if (target.isDead()) return;
            target.damage(10, player);
            target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 0, false, true));
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0, false, true));
            target.getWorld().spawnParticle(Particle.WITCH,
                    target.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.1);
            player.sendActionBar("§b🌀 幻术飞弹命中！");
        }
        snowball.remove();
    }
}
