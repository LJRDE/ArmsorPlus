package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 魔法球 —— 右键抽取随机附魔书
public class MagicBallEnchant implements Listener {

    @EventHandler
    public void IfUSEMagicBallEvent(PlayerInteractEvent event) {
        if (event.getAction().isLeftClick()) return;
        if (event.getItem() == null) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        int amount = ArmsorEnchant.getEnchantLevel(item, MagicBallKey);
        if (amount == 0) return;

        Random r = new Random(System.currentTimeMillis());
        item.setAmount(item.getAmount() - 1);
        event.setCancelled(true);

        int count = 0;
        // 每层保底循环: amount层, 每层独立概率获得附魔书
        for (int i = 0; i < amount; i++) {
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(Sniping_EnchantdeBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.LIGHT_PURPLE + "获得狙击附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(Famine_EnchantdeBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.GREEN + "获得饥荒附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(Dodge_EnchantdeBook(1, r.nextInt(4) + 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得闪避附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(Ripples_EnchantdeBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.BLUE + "获得涟漪附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(BloodSacrifice_EnchantdeBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.DARK_RED + "获得血祭附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(EffectClear_EnchantdeBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.WHITE + "获得涤魂附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(Freeze_EnchantedBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.AQUA + "获得寒冻附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(Revenge_EnchantedBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.DARK_RED + "获得复仇附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(HealthBoost_EnchantedBook(1, r.nextInt(4) + 1));
                PlayerSettings.notify(player,ChatColor.RED + "获得生命提升附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(ExplosiveArrow_EnchantedBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.YELLOW + "获得蓄爆附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(Withering_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.DARK_PURPLE + "获得凋零附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(Blocking_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.AQUA + "获得格挡附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(Survivor_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得幸存附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(ShadowDodge_EnchantdeBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.DARK_PURPLE + "获得影避附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(ArrowSpeed_EnchantdeBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得弹道附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(DoubleHit_EnchantdeBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得双重打击附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(Feeding_EnchantdeBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得吸血附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(QuickThrust_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得疾刺附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(DiamondDrill_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.AQUA + "获得金刚钻附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(Blindness_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.DARK_GRAY + "获得失明附魔书");
                count++;
            }
            // 0.3I 新附魔
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(ProtectionPRO_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得保护PRO附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(Stun_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.DARK_GREEN + "获得眩晕附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(GolemGuardian_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.GRAY + "获得傀儡守护者附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(CriticalStrike_EnchantedBook(1, r.nextInt(5) + 1));
                PlayerSettings.notify(player,ChatColor.RED + "获得暴击附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(Piercing_EnchantedBook(1, 1));
                PlayerSettings.notify(player,ChatColor.DARK_RED + "获得穿甲附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(LavaWalker_EnchantedBook(1, 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得熔岩行者附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(LightningCall_EnchantedBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.YELLOW + "获得唤雷附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(Holographic_EnchantedBook(1, 1));
                PlayerSettings.notify(player,ChatColor.AQUA + "获得全息附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(Tracking_EnchantedBook(1, 1));
                PlayerSettings.notify(player,ChatColor.GREEN + "获得追踪附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(Harvest_EnchantedBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.GOLD + "获得丰收附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(AutoPlant_EnchantedBook(1, 1));
                PlayerSettings.notify(player,ChatColor.GREEN + "获得自动种植附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(StrongBurst_EnchantedBook(1, 1));
                PlayerSettings.notify(player,ChatColor.DARK_PURPLE + "获得强风暴附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(MultiShot_EnchantedBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.LIGHT_PURPLE + "获得千重射击附魔书");
                count++;
            }
            if (EnchantUtil.percent(10)) {
                player.getInventory().addItem(ThunderclapArrow_EnchantedBook(1, r.nextInt(3) + 1));
                PlayerSettings.notify(player,ChatColor.YELLOW + "获得惊雷附魔书");
                count++;
            }
        }

        PlayerSettings.notify(player,"获得数量: " + count);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.spawnParticle(org.bukkit.Particle.FIREWORK, player.getLocation(), 30, 0.5, 1, 0.5, 0.2);
    }
}
