package Dim_LJR.armsorPlus.OpenSea;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;

public class OpenSeaEntity implements Listener {
    public static ItemStack SkeletonSword(int Amount)//重剑
    {
        ItemStack item = new ItemStack(DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "守卫之剑");
        item.setAmount(Amount);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(
                        new NamespacedKey(getplugin, "ArmsorPlus_SwordDamage"),
                        15,AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.HAND));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item,DoubleHitkey,5);
        ArmsorEnchant.addEnchant(item,FreezeKey,2);
        return item;
    }
    public static ItemStack SkeletonHelmet(int Amount)//重剑
    {
        ItemStack item = new ItemStack(CARVED_PUMPKIN);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "守卫者的头套");
        meta.addAttributeModifier(Attribute.ARMOR,new AttributeModifier(
                new NamespacedKey(getplugin, "ArmsorPlus_HelmetArmor")
                , 8,//护甲值
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.HEAD));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,new AttributeModifier(
                new NamespacedKey(getplugin, "ArmsorPlus_HelmetToughness"),
                12,//护甲韧性
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.HEAD));
        item.setAmount(Amount);
        meta.addEnchant(Enchantment.PROTECTION,10,true);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item,BlockingKey,5);
        return item;
    }
    public static ItemStack SkeletonChestplate(int Amount)
    {
        ItemStack item = new ItemStack(DIAMOND_CHESTPLATE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + " ");
        meta.addAttributeModifier(Attribute.ARMOR,new AttributeModifier(
                new NamespacedKey(getplugin, "ArmsorPlus_ChestplateArmor")
                , 8,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.CHEST));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,new AttributeModifier(
                new NamespacedKey(getplugin, "ArmsorPlus_ChestplateToughness")
                ,  8,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.CHEST));
        item.setAmount(Amount);
        meta.addEnchant(Enchantment.PROTECTION,4,true);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item,BlockingKey,5);
        return item;
    }
    public static ItemStack SkeletonLeggings(int Amount)
    {
        ItemStack item = new ItemStack(DIAMOND_LEGGINGS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + " ");
        meta.addAttributeModifier(Attribute.ARMOR,new AttributeModifier(
                new NamespacedKey(getplugin, "ArmsorPlus_LeggingsArmor"),
                8,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.LEGS));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,new AttributeModifier(
                new NamespacedKey(getplugin, "ArmsorPlus_LeggingsToughness")
                ,  8,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.LEGS));
        item.setAmount(Amount);
        meta.addEnchant(Enchantment.PROTECTION,4,true);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item,BlockingKey,5);
        return item;
    }
    public static ItemStack SkeletonBoots(int Amount)
    {
        ItemStack item = new ItemStack(DIAMOND_BOOTS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + " ");
        meta.addAttributeModifier(Attribute.ARMOR,new AttributeModifier(
                new NamespacedKey(getplugin, "ArmsorPlus_BootsArmor")
                , 8,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.FEET));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,new AttributeModifier(
                new NamespacedKey(getplugin, "ArmsorPlus_BootsToughness")
                ,  8,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.FEET));
        item.setAmount(Amount);
        meta.addEnchant(Enchantment.PROTECTION,4,true);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item,BlockingKey,5);
        return item;
    }

    public static void SpawnSkeleton(Location location)
    {
        Skeleton skeleton = location.getWorld().spawn(location, Skeleton.class, skel -> {
            // Paper 1.21: 在实体初始化阶段设置装备，确保装备正确应用
            EntityEquipment equip = skel.getEquipment();
            if (equip != null) {
                equip.setItem(EquipmentSlot.HAND, SkeletonSword(1));
                equip.setItem(EquipmentSlot.HEAD, SkeletonHelmet(1));
                equip.setItem(EquipmentSlot.CHEST, SkeletonChestplate(1));
                equip.setItem(EquipmentSlot.LEGS, SkeletonLeggings(1));
                equip.setItem(EquipmentSlot.FEET, SkeletonBoots(1));
                equip.setHelmetDropChance(0.1F);
                equip.setChestplateDropChance(0);
                equip.setLeggingsDropChance(0);
                equip.setBootsDropChance(0);
                equip.setItemInMainHandDropChance(0);
            }
            skel.setMaxHealth(60);
            skel.setHealth(60);
            skel.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 4800, 1, true, false));
        });
        // 4分钟后移除守卫者
        BukkitTask task = Bukkit.getScheduler().runTaskLater(getplugin, () -> {
            if (!skeleton.isDead())
                skeleton.remove();
        }, 4800);
    }
}
