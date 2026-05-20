package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.IRON_INGOT;
import static org.bukkit.Material.RAW_IRON;

/**
 * 基础材料 —— 非食物类的合成材料。
 */
public class Materials {

    /** 生钢: 铁锭+煤炭合成, 熔炉烧制后成钢 */
    public static ItemStack RawSteel(int amount) {
        ItemStack item = new ItemStack(RAW_IRON);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GRAY + "生钢");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "铁与碳的混合物", ChatColor.GRAY + "放入熔炉烧制后可获得钢"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, RawSteelKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 钢: 熔炉烧制生钢获得 */
    public static ItemStack SteelIngot(int amount) {
        ItemStack item = new ItemStack(IRON_INGOT);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
        meta.setDisplayName(ChatColor.GRAY + "钢");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "坚硬的钢材", ChatColor.GRAY + "可用于合成钢制装备"));
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SteelIngotKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 末影核心: 虚空幽魂掉落物 */
    public static ItemStack EndCore(int amount) {
        ItemStack item = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "末影核心");
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "虚空幽魂的核心能量体",
                ChatColor.GRAY + "蕴含纯粹的虚空之力，可用于合成高阶装备"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, EndCoreKey, 1);
        item.setAmount(amount);
        return item;
    }
}
