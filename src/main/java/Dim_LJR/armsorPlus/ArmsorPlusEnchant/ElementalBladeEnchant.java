package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 元素之刃 —— 将攻击伤害转化为对应属性 (最高优先级最后执行)
public class ElementalBladeEnchant implements Listener {

    private static final String ELEMENTAL_BLADE_FLAG = "ArmsorPlus_ElementalBlade";

    @EventHandler(priority = EventPriority.LOWEST)
    public void ElementalBladeHandler(EntityDamageByEntityEvent event) {
        if (event.getEntity().equals(event.getDamager())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (target.hasMetadata(ELEMENTAL_BLADE_FLAG)) return;
        if (damager.getEquipment() == null) return;

        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        double damage = event.getDamage();
        event.setDamage(0);
        event.setCancelled(true);

        target.setMetadata(ELEMENTAL_BLADE_FLAG, new FixedMetadataValue(getplugin, true));
        try {

        if (ArmsorEnchant.getEnchantLevel(weapon, FireBladeKey) > 0) {
            target.setFireTicks(60);
            PlayerSettings.notify(damager, ChatColor.RED + "火印: 火焰伤害");
        } else if (ArmsorEnchant.getEnchantLevel(weapon, FrostBladeKey) > 0) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 1));
            PlayerSettings.notify(damager, ChatColor.AQUA + "霜印: 冰冻伤害");
        } else if (ArmsorEnchant.getEnchantLevel(weapon, ThunderBladeKey) > 0) {
            target.getWorld().strikeLightningEffect(target.getLocation());
            PlayerSettings.notify(damager, ChatColor.YELLOW + "雷印: 雷电伤害");
        } else if (ArmsorEnchant.getEnchantLevel(weapon, MagicBladeKey) > 0) {
            target.getWorld().spawnParticle(Particle.ENCHANT, target.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0);
            PlayerSettings.notify(damager, ChatColor.DARK_PURPLE + "魔印: 魔法伤害");
        } else {
            target.removeMetadata(ELEMENTAL_BLADE_FLAG, getplugin);
            event.setDamage(damage);
            event.setCancelled(false);
            return;
        }

        int fireLvl = ArmsorEnchant.getEnchantLevel(weapon, FireBladeKey);
        int frostLvl = ArmsorEnchant.getEnchantLevel(weapon, FrostBladeKey);
        int thunderLvl = ArmsorEnchant.getEnchantLevel(weapon, ThunderBladeKey);
        int magicLvl = ArmsorEnchant.getEnchantLevel(weapon, MagicBladeKey);
        double extra = 0;
        String msg = "";

        if (fireLvl > 0) {
            if (target.hasMetadata("ArmsorPlus_FrostMark")) {
                target.removeMetadata("ArmsorPlus_FrostMark", getplugin);
                damage *= 2.25; msg = ChatColor.RED + "火印·融霜: +125%";
            } else if (target.hasMetadata("ArmsorPlus_ThunderMark")) {
                target.removeMetadata("ArmsorPlus_ThunderMark", getplugin);
                damage *= 2.25; msg = ChatColor.RED + "火印·爆雷: +125%";
            } else {
                target.setMetadata("ArmsorPlus_FireMark", new FixedMetadataValue(getplugin, true));
                target.setFireTicks(40 * fireLvl); msg = ChatColor.RED + "施加火印(" + fireLvl + ")";
                spawnMarkParticles(target, "ArmsorPlus_FireMark", 60 * fireLvl, Particle.FLAME);
                int f = fireLvl;
                Bukkit.getScheduler().runTaskLater(getplugin,
                        () -> target.removeMetadata("ArmsorPlus_FireMark", getplugin), 60L * f);
            }
        } else if (frostLvl > 0) {
            if (target.hasMetadata("ArmsorPlus_FireMark")) {
                target.removeMetadata("ArmsorPlus_FireMark", getplugin);
                damage *= 2.0; msg = ChatColor.AQUA + "霜印·灭焰: +100%";
            } else if (target.hasMetadata("ArmsorPlus_ThunderMark")) {
                target.removeMetadata("ArmsorPlus_ThunderMark", getplugin);
                damage *= 1.6; extra = damage * 0.6; msg = ChatColor.AQUA + "霜印·导雷: +60%+雷伤";
            } else {
                target.setMetadata("ArmsorPlus_FrostMark", new FixedMetadataValue(getplugin, true));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * frostLvl, 1));
                msg = ChatColor.AQUA + "施加霜印(" + frostLvl + ")";
                spawnMarkParticles(target, "ArmsorPlus_FrostMark", 60 * frostLvl, Particle.SNOWFLAKE);
                int f = frostLvl;
                Bukkit.getScheduler().runTaskLater(getplugin,
                        () -> target.removeMetadata("ArmsorPlus_FrostMark", getplugin), 60L * f);
            }
        } else if (thunderLvl > 0) {
            if (target.hasMetadata("ArmsorPlus_FireMark")) {
                target.removeMetadata("ArmsorPlus_FireMark", getplugin);
                damage *= 2.0; msg = ChatColor.YELLOW + "雷印·引火: +100%";
            } else if (target.hasMetadata("ArmsorPlus_FrostMark")) {
                target.removeMetadata("ArmsorPlus_FrostMark", getplugin);
                damage *= 1.75; msg = ChatColor.YELLOW + "雷印·碎霜: +75%";
            } else {
                target.setMetadata("ArmsorPlus_ThunderMark", new FixedMetadataValue(getplugin, true));
                target.getWorld().strikeLightningEffect(target.getLocation());
                msg = ChatColor.YELLOW + "施加雷印(" + thunderLvl + ")";
                spawnMarkParticles(target, "ArmsorPlus_ThunderMark", 60 * thunderLvl, Particle.ELECTRIC_SPARK);
                int t = thunderLvl;
                Bukkit.getScheduler().runTaskLater(getplugin,
                        () -> target.removeMetadata("ArmsorPlus_ThunderMark", getplugin), 60L * t);
            }
        } else if (magicLvl > 0) {
            if (target.hasMetadata("ArmsorPlus_MagicMark")) {
                damage *= 2.0;
            }
            target.setMetadata("ArmsorPlus_MagicMark", new FixedMetadataValue(getplugin, true));
            target.getWorld().spawnParticle(Particle.ENCHANT, target.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0);
            msg = ChatColor.DARK_PURPLE + "施加魔印: 魔法伤害2x";
            spawnMarkParticles(target, "ArmsorPlus_MagicMark", 60 * magicLvl, Particle.ENCHANT);
            int m = magicLvl;
            Bukkit.getScheduler().runTaskLater(getplugin,
                    () -> target.removeMetadata("ArmsorPlus_MagicMark", getplugin), 60L * m);
        }

        PlayerSettings.notify(damager, msg);
        if (target.isDead()) return;
        target.damage(damage + extra, damager);
        if (target.isDead()) return;
        } finally {
            // 确保 flag 一定会被清除, 即使中途异常或提前 return
            Bukkit.getScheduler().runTaskLater(getplugin,
                    () -> target.removeMetadata(ELEMENTAL_BLADE_FLAG, getplugin), 1L);
        }
    }

    // 让带印记的目标持续散发对应元素粒子 (印记存续期间, 每0.25秒一次)
    private void spawnMarkParticles(Entity target, String mark, int durationTicks, Particle particle) {
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= durationTicks || target.isDead() || !target.hasMetadata(mark)) {
                    cancel();
                    return;
                }
                target.getWorld().spawnParticle(particle,
                        target.getLocation().add(0, 1, 0), 3, 0.3, 0.3, 0.3, 0.01);
                ticks++;
            }
        }.runTaskTimer(getplugin, 0L, 5L);
    }
}
