package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 涤魂 —— 获得负面效果时 25%*level 概率免除 (胸甲, 满级III)
public class EffectClearEnchant implements Listener {

    private static final Set<PotionEffectType> NEGATIVE_EFFECTS = new HashSet<>(Arrays.asList(
            PotionEffectType.POISON, PotionEffectType.WITHER, PotionEffectType.SLOWNESS,
            PotionEffectType.WEAKNESS, PotionEffectType.BLINDNESS, PotionEffectType.NAUSEA,
            PotionEffectType.HUNGER, PotionEffectType.MINING_FATIGUE, PotionEffectType.INSTANT_DAMAGE,
            PotionEffectType.LEVITATION, PotionEffectType.UNLUCK, PotionEffectType.DARKNESS,
            PotionEffectType.WIND_CHARGED, PotionEffectType.WEAVING, PotionEffectType.OOZING,
            PotionEffectType.INFESTED
    ));

    @EventHandler
    public void onPotionEffectAdd(EntityPotionEffectEvent event) {
        if (event.isCancelled()) return;
        if (event.getAction() != EntityPotionEffectEvent.Action.ADDED
                && event.getAction() != EntityPotionEffectEvent.Action.CHANGED) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (!NEGATIVE_EFFECTS.contains(event.getModifiedType())) return;
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null) return;
        int lvl = ArmsorEnchant.getEnchantLevel(chestplate, EffectClear);
        if (lvl <= 0) return;
        if (EnchantUtil.percent(25 * lvl)) {
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL, 0.8f, 1.2f);
            PlayerSettings.notifyActionBar(player, "§f[涤魂]免疫了负面效果 §7(" + (25 * lvl) + "%)");
        }
    }
}
