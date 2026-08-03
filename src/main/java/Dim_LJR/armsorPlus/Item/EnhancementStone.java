package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;

// 强化石 —— 通过拖动到装备上使用。
public class EnhancementStone {

    public static final String ARMS_I_NAME = ChatColor.RED + "武器强化石Ⅰ";
    public static final String ARMS_II_NAME = ChatColor.DARK_RED + "武器强化石Ⅱ";
    public static final String ARMOR_I_NAME = ChatColor.BLUE + "护甲强化石Ⅰ";
    public static final String ARMOR_II_NAME = ChatColor.DARK_BLUE + "护甲强化石Ⅱ";
    public static final String BOW_I_NAME = ChatColor.DARK_RED + "弓箭强化石";
    public static final String DIAMONDPLUS = ChatColor.GOLD + "精炼金刚石";

    // 一级武器强化石: 使锋利等级+1
    public static ItemStack ArmsPlusCreateI(int amount) {
        ItemStack item = new ItemStack(DIAMOND);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ARMS_I_NAME);
        meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "使用使锋利等级+1"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, Armskey, 1);
        item.setAmount(amount);
        return item;
    }

    // 二级武器强化石: 伤害直接+1 (通过属性修饰符)
    public static ItemStack ArmsPlusCreateII(int amount) {
        ItemStack item = new ItemStack(DIAMOND_ORE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ARMS_II_NAME);
        meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "使用伤害直接+1"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, Armskey, 2);
        item.setAmount(amount);
        return item;
    }

    // 一级护甲强化石: 使保护等级+1
    public static ItemStack ArmorPlusCreate(int amount) {
        ItemStack item = new ItemStack(IRON_INGOT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ARMOR_I_NAME);
        meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "使用使保护等级+1"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, Armorkey, 1);
        item.setAmount(amount);
        return item;
    }

    // 二级护甲强化石: 护甲值+1 (通过属性修饰符)
    public static ItemStack ArmorPlusCreateII(int amount) {
        ItemStack item = new ItemStack(IRON_ORE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ARMOR_II_NAME);
        meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "使用使护甲值+1"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, Armorkey, 2);
        item.setAmount(amount);
        return item;
    }

    // 弓箭强化石: 使力量等级+1
    public static ItemStack BowPlusCreate(int amount) {
        ItemStack item = new ItemStack(OAK_LOG);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(BOW_I_NAME);
        meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "使用使力量等级+1"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, Bowkey, 1);
        item.setAmount(amount);
        return item;
    }

    // 基础强化石: 右键随机获得一种强化石
    public static ItemStack BasicStone(int amount) {
        ItemStack item = new ItemStack(STONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "基础强化石");
        meta.setLore(Arrays.asList(
                ChatColor.BLUE + "用四个钻石块合成基础强化石",
                ChatColor.GOLD + "右键获得武器,护甲强化石"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, BasicStone, 1);
        item.setAmount(amount);
        return item;
    }

    // 精炼金刚石: 拖动到装备上获得无限耐久
    public static ItemStack DIAMONDPLUSCreate(int amount) {
        ItemStack item = new ItemStack(DIAMOND);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.UNBREAKING, 1, false);
        meta.setDisplayName(DIAMONDPLUS);
        meta.setLore(Collections.singletonList(ChatColor.BOLD + "拖动到装备上获得无限耐久"));
        item.setAmount(amount);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, DiamondPluskey, 1);
        return item;
    }
}
