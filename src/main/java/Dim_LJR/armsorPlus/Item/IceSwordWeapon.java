package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.EnchantUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.IceSwordKey;

// 寒冰剑 —— 攻击时对敌方造成缓慢II 3秒
public class IceSwordWeapon implements Listener {

    @EventHandler
    public void onIceSwordAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, IceSwordKey) == 0) return;

        if (event.getEntity() instanceof LivingEntity target) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1, false, false));
            target.getWorld().spawnParticle(Particle.SNOWFLAKE,
                    target.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
        }
    }
}
