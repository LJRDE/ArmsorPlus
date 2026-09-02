package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.ChatColor;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 傀儡守护者 —— 受伤时召唤铁傀儡反击 (胸甲, 每级1只, 冷却300秒, 满级V)
public class GolemGuardianEnchant implements Listener {

    private final Map<UUID, Long> golemCooldowns = new HashMap<>();

    @EventHandler
    public void GolemGuardianHandler(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        // 穿透伤害不再召唤傀儡
        if (EnchantUtil.PIERCING_ACTIVE.contains(player.getUniqueId())) return;
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;

        ItemStack chest = player.getEquipment().getChestplate();
        int level = ArmsorEnchant.getEnchantLevel(chest, GolemGuardianKey);
        if (level <= 0) return;

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (golemCooldowns.containsKey(uuid) && (now - golemCooldowns.get(uuid)) < 300000) return;
        golemCooldowns.put(uuid, now);

        if (attacker.getLocation().distance(player.getLocation()) > 70) return;

        for (int i = 0; i < level; i++) {
            IronGolem golem = player.getWorld().spawn(player.getLocation(), IronGolem.class);
            golem.setTarget(attacker);
            golem.setPlayerCreated(false);
        }
        player.sendMessage(ChatColor.GRAY + "傀儡守护者: 召唤了" + level + "只铁傀儡");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        golemCooldowns.remove(event.getPlayer().getUniqueId());
    }
}
