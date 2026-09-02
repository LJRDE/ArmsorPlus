package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.EnchantUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 鱼骨系列武器特效 (海骨/灵骨/海刺/灵刺/海哭 剑与刀)
public class FishBoneWeapon implements Listener {

    private static final Random RANDOM = new Random();

    // ---- 海骨剑: 水中+10%伤害 +10%移速 ----

    @EventHandler(priority = EventPriority.LOW)
    public void onSeaBoneSwordAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SeaBoneSwordKey) == 0) return;
        if (!player.isInWater() && !EnchantUtil.isInRain(player)) return;

        event.setDamage(event.getDamage() * 1.10);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0, false, false));
    }

    // ---- 海骨刀: 水中/雨天+16%伤害 ----

    @EventHandler(priority = EventPriority.LOW)
    public void onSeaBoneKnifeAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SeaBoneKnifeKey) == 0) return;
        if (!player.isInWater() && !EnchantUtil.isInRain(player)) return;

        event.setDamage(event.getDamage() * 1.16);
    }

    // ---- 灵骨剑: 光灵3s + 水中/雨天15%穿透6点 ----

    @EventHandler
    public void onSpiritBoneSwordAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SpiritBoneSwordKey) == 0) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 60, 0, false, false));
        if ((player.isInWater() || EnchantUtil.isInRain(player)) && RANDOM.nextDouble() < 0.15) {
            if (target.isDead()) return; // 前: 目标已死亡则跳过
            target.damage(6.0, player);
            if (target.isDead()) return; // 后: 目标被击杀则停止特效
            target.getWorld().spawnParticle(Particle.SOUL,
                    target.getLocation().add(0, 1, 0), 15, 0.3, 0.3, 0.3, 0.05);
        }
    }

    // ---- 灵骨刀: 光灵3s + 水中/雨天15%双倍伤害 ----

    @EventHandler(priority = EventPriority.LOW)
    public void onSpiritBoneKnifeAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SpiritBoneKnifeKey) == 0) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 60, 0, false, false));
        if ((player.isInWater() || EnchantUtil.isInRain(player)) && RANDOM.nextDouble() < 0.15) {
            event.setDamage(event.getDamage() * 2.0);
            target.getWorld().spawnParticle(Particle.SOUL,
                    target.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.05);
        }
    }

    // ---- 海刺剑: 水中/雨天+40%伤害 ----

    @EventHandler(priority = EventPriority.LOW)
    public void onSeaSpineSwordAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SeaSpineSwordKey) == 0) return;
        if (!player.isInWater() && !EnchantUtil.isInRain(player)) return;

        event.setDamage(event.getDamage() * 1.40);
    }

    // ---- 海刺刀: 水中/雨天-40%受伤 ----

    @EventHandler(priority = EventPriority.HIGH)
    public void onSeaSpineKnifeDefend(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SeaSpineKnifeKey) == 0
                && ArmsorEnchant.getEnchantLevel(player.getInventory().getItemInOffHand(), SeaSpineKnifeKey) == 0) return;
        if (!player.isInWater() && !EnchantUtil.isInRain(player)) return;

        event.setDamage(event.getDamage() * 0.60);
    }

    // ---- 海刺剑/海刺刀 水下呼吸被动 ----

    @EventHandler
    public void onSeaSpineWaterBreathing(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        boolean hasSeaSpine = ArmsorEnchant.getEnchantLevel(main, SeaSpineSwordKey) > 0
                || ArmsorEnchant.getEnchantLevel(main, SeaSpineKnifeKey) > 0
                || ArmsorEnchant.getEnchantLevel(off, SeaSpineSwordKey) > 0
                || ArmsorEnchant.getEnchantLevel(off, SeaSpineKnifeKey) > 0;
        if (!hasSeaSpine) return;
        if (!player.isInWater()) return;

        PotionEffect existing = player.getPotionEffect(PotionEffectType.WATER_BREATHING);
        if (existing == null || existing.getDuration() < 100) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 300, 0, false, false));
        }
    }

    // ---- 灵刺剑: 必穿透1点 + 水中/雨天额外穿透2点 ----

    @EventHandler
    public void onSpiritSpineSwordAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SpiritSpineSwordKey) == 0) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        if (target.isDead()) return; // 前: 目标已死亡则跳过
        target.damage(1.0, player);
        if (target.isDead()) return; // 后: 目标被第一段伤害击杀则停止
        if (player.isInWater() || EnchantUtil.isInRain(player)) {
            target.damage(2.0, player);
            if (target.isDead()) return; // 后: 目标被第二段伤害击杀则停止
        }
        target.getWorld().spawnParticle(Particle.CRIT,
                target.getLocation().add(0, 1, 0), 5, 0.2, 0.2, 0.2, 0);
    }

    // ---- 灵刺刀: 水中/雨天伤害+5 ----

    @EventHandler
    public void onSpiritSpineKnifeAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SpiritSpineKnifeKey) == 0) return;
        if (!player.isInWater() && !EnchantUtil.isInRain(player)) return;

        event.setDamage(event.getDamage() + 5.0);
    }

    // ---- 海哭剑/海哭刀 被动药水效果 ----

    @EventHandler
    public void onSeaCryPassive(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        boolean hasSeaCry = ArmsorEnchant.getEnchantLevel(main, SeaCrySwordKey) > 0
                || ArmsorEnchant.getEnchantLevel(main, SeaCryKnifeKey) > 0
                || ArmsorEnchant.getEnchantLevel(off, SeaCrySwordKey) > 0
                || ArmsorEnchant.getEnchantLevel(off, SeaCryKnifeKey) > 0;
        if (!hasSeaCry) return;

        int duration = 300;
        PotionEffect waterBreathing = player.getPotionEffect(PotionEffectType.WATER_BREATHING);
        if (waterBreathing == null || waterBreathing.getDuration() < 100) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, duration, 0, false, false));
        }
        PotionEffect dolphinsGrace = player.getPotionEffect(PotionEffectType.DOLPHINS_GRACE);
        if (dolphinsGrace == null || dolphinsGrace.getDuration() < 100) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, duration, 0, false, false));
        }
        PotionEffect conduitPower = player.getPotionEffect(PotionEffectType.CONDUIT_POWER);
        if (conduitPower == null || conduitPower.getDuration() < 100) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, duration, 0, false, false));
        }
    }

    // ---- 海哭剑: 发光+15%挖掘疲劳+伤害20%(水中40%) ----

    @EventHandler(priority = EventPriority.LOW)
    public void onSeaCrySwordAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SeaCrySwordKey) == 0) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0, false, false));
        if (RANDOM.nextDouble() < 0.15) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 600, 2, false, false));
        }
        if (player.isInWater() || EnchantUtil.isInRain(player)) {
            event.setDamage(event.getDamage() * 1.40);
        } else {
            event.setDamage(event.getDamage() * 1.20);
        }
    }

    // ---- 海哭刀: 发光+15%挖掘疲劳+伤害45%(水中/雨天90%) ----

    @EventHandler(priority = EventPriority.LOW)
    public void onSeaCryKnifeAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (ArmsorEnchant.getEnchantLevel(weapon, SeaCryKnifeKey) == 0) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0, false, false));
        if (RANDOM.nextDouble() < 0.15) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 600, 2, false, false));
        }
        if (player.isInWater() || EnchantUtil.isInRain(player)) {
            event.setDamage(event.getDamage() * 1.90);
        } else {
            event.setDamage(event.getDamage() * 1.45);
        }
    }
}
