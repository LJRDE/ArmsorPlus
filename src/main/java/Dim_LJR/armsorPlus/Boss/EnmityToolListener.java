package Dim_LJR.armsorPlus.Boss;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import Dim_LJR.armsorPlus.NamespaceKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// 挑拨木棍 (EnmityTool): 右键一个生物再右键另一个, 两者互相敌对, 不死不休 (持续到一方死亡)。
// 可重复使用, 不消耗。目标须为原版生物(Mob) 或 假玩家Boss本体(盔甲架)。
public class EnmityToolListener implements Listener {

    // 每个玩家待确认的第一次选中目标
    private static final Map<UUID, LivingEntity> selected = new ConcurrentHashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(item, NamespaceKey.Keys.EnmityKey) == 0) return;

        Entity clicked = event.getRightClicked();
        if (!(clicked instanceof LivingEntity target) || target instanceof Player) return;
        // 只允许原版生物 或 假玩家Boss本体
        if (!(target instanceof Mob) && !(target instanceof ArmorStand && Enmity.isBossBody(target))) return;

        event.setCancelled(true);

        LivingEntity pending = selected.get(player.getUniqueId());
        if (pending != null && pending.isValid() && !pending.isDead()
                && pending.getUniqueId().equals(target.getUniqueId())) {
            // 再次右键同一只 → 取消选中
            selected.remove(player.getUniqueId());
            player.sendMessage("§7已取消选中 " + name(pending));
            return;
        }
        if (pending == null || !pending.isValid() || pending.isDead()) {
            selected.put(player.getUniqueId(), target);
            player.sendMessage("§e已选中 §c" + name(target) + "§e，再右键另一个生物使其结仇");
            return;
        }

        // 第二下: 结怨
        Enmity.setEnmity(pending, target);
        selected.remove(player.getUniqueId());
        player.getWorld().spawnParticle(Particle.HEART,
                target.getLocation().clone().add(0, 1.5, 0), 8, 0.4, 0.4, 0.4, 0);
        player.getWorld().playSound(target.getLocation(), Sound.ENTITY_WITHER_HURT, 0.8f, 0.6f);
        player.sendMessage("§c" + name(pending) + " §7与 §c" + name(target) + " §7结下仇怨，不死不休！");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        selected.remove(event.getPlayer().getUniqueId());
    }

    private static String name(LivingEntity e) {
        if (e.getCustomName() != null) return e.getCustomName();
        return "§e" + e.getType().name();
    }
}
