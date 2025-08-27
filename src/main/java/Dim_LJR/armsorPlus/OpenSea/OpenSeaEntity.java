package Dim_LJR.armsorPlus.OpenSea;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;

public class OpenSeaEntity implements Listener {
    public static ItemStack SkeletonSword(int Amount)//重剑
    {
        ItemStack item = new ItemStack(DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "守卫之剑");
        item.setAmount(Amount);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
                new AttributeModifier(UUID.randomUUID(),
                        "arms",15,AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlot.HAND));
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
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR,new AttributeModifier(UUID.randomUUID(),
                "armor", 8,//护甲值
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HEAD));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,new AttributeModifier(UUID.randomUUID(),
                "armor", 12,//护甲韧性
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HEAD));
        item.setAmount(Amount);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,10,true);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item,BlockingKey,5);
        return item;
    }
    public static ItemStack SkeletonChestplate(int Amount)//重剑
    {
        ItemStack item = new ItemStack(DIAMOND_CHESTPLATE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + " ");
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR,new AttributeModifier(UUID.randomUUID(),
                "armor", 8,//护甲值
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HEAD));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,new AttributeModifier(UUID.randomUUID(),
                "armor", 8,//护甲韧性
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HEAD));
        item.setAmount(Amount);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,4,true);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item,BlockingKey,5);
        return item;
    }
    public static ItemStack SkeletonLeggings(int Amount)//重剑
    {
        ItemStack item = new ItemStack(DIAMOND_LEGGINGS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + " ");
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR,new AttributeModifier(UUID.randomUUID(),
                "armor", 8,//护甲值
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HEAD));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,new AttributeModifier(UUID.randomUUID(),
                "armor", 8,//护甲韧性
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HEAD));
        item.setAmount(Amount);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,4,true);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item,BlockingKey,5);
        return item;
    }
    public static ItemStack SkeletonBoots(int Amount)//重剑
    {
        ItemStack item = new ItemStack(DIAMOND_BOOTS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + " ");
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR,new AttributeModifier(UUID.randomUUID(),
                "armor", 8,//护甲值
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HEAD));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,new AttributeModifier(UUID.randomUUID(),
                "armor", 8,//护甲韧性
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HEAD));
        item.setAmount(Amount);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,4,true);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item,BlockingKey,5);
        return item;
    }

    public static void SpawnSkeleton(Location location)
    {
        Skeleton skeleton = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
        skeleton.getEquipment().setItemInMainHand(SkeletonSword(1));
        skeleton.getEquipment().setHelmet(SkeletonHelmet(1));
        skeleton.getEquipment().setChestplate(SkeletonChestplate(1));
        skeleton.getEquipment().setLeggings(SkeletonLeggings(1));
        skeleton.getEquipment().setBoots(SkeletonBoots(1));
        skeleton.setMaxHealth(60);
        skeleton.setHealth(60);
        skeleton.getEquipment().setHelmetDropChance(0.1F);
        skeleton.getEquipment().setChestplateDropChance(0);
        skeleton.getEquipment().setBootsDropChance(0);
        skeleton.getEquipment().setLeggingsDropChance(0);
        skeleton.getEquipment().setItemInMainHandDropChance(0);
        skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,4800,1,true,false));
        BukkitTask task = Bukkit.getScheduler().runTaskLater(getplugin,() ->{
            skeleton.getEquipment().setHelmetDropChance(0);
            if(skeleton.isDead())
                return;
            else skeleton.remove();
        },4800);
    }
}
