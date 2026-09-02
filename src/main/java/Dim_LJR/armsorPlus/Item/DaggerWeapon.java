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

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.DaggerKey;

// 匕首 —— 保证额外5点伤害 (已通过属性修饰符实现, 此处处理真实伤害)
public class DaggerWeapon implements Listener {

    @EventHandler
    public void onDaggerAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, DaggerKey) == 0) return;

        // 匕首固定额外5点"真实"伤害 (计入玩家击杀, 前后检查目标是否死亡)
        if (event.getEntity() instanceof LivingEntity target) {
            if (target.isDead()) return;
            target.damage(5, player);
            if (target.isDead()) return;
            target.getWorld().spawnParticle(Particle.SWEEP_ATTACK,
                    target.getLocation().add(0, 1, 0), 3, 0.3, 0.3, 0.3, 0);
        }
    }
}
