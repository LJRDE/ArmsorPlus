package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 其他物品 —— 包括向导书、菜单标记等不属于其他分类的物品。
public class MiscItem {

    public static final String GUIDE_BOOK = ChatColor.GOLD + "高级附魔向导";

    // 高级附魔向导书: 右键打开菜单
    public static ItemStack GuideBook(int amount) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(GUIDE_BOOK);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, GuideBookKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 菜单边框标记 (用于GUI的玻璃板装饰)
    public static ItemStack MenuMark(int amount, Material material) {
        ItemStack item = new ItemStack(material);
        ArmsorEnchant.addEnchant(item, MenuMark, 1);
        item.setAmount(amount);
        return item;
    }

    // 挑拨木棍 (EnmityTool): 右键两个生物使其互相敌对, 不死不休。可重复使用。
    public static ItemStack EnmityTool(int amount) {
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "挑拨木棍");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "右键一个生物，再右键另一个生物",
                ChatColor.GRAY + "两者将结下仇怨，不死不休",
                ChatColor.DARK_RED + "可重复使用"
        ));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, EnmityKey, 1);
        item.setAmount(amount);
        return item;
    }
}
