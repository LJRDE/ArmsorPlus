package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.FIREWORK_STAR;

/**
 * 魔法球 —— 右键抽取随机附魔书。
 */
public class MagicBall {

    private static final String MAGICBALL_I_NAME = ChatColor.GREEN + "寻常的魔法球";
    private static final String MAGICBALL_II_NAME = ChatColor.BLUE + "稀罕的魔法球";
    private static final String MAGICBALL_III_NAME = ChatColor.GOLD + "史诗的魔法球";
    private static final String MAGICBALL_IV_NAME = ChatColor.LIGHT_PURPLE + "传奇的魔法球";

    /** 寻常的魔法球 (保底1本) */
    public static ItemStack MagicBallCreateI(int amount) {
        return createMagicBall(amount, 1, MAGICBALL_I_NAME);
    }

    /** 稀罕的魔法球 (保底2本) */
    public static ItemStack MagicBallCreateII(int amount) {
        return createMagicBall(amount, 2, MAGICBALL_II_NAME);
    }

    /** 史诗的魔法球 (保底3本) */
    public static ItemStack MagicBallCreateIII(int amount) {
        return createMagicBall(amount, 3, MAGICBALL_III_NAME);
    }

    /** 传奇的魔法球 (保底4本) */
    public static ItemStack MagicBallCreateIV(int amount) {
        return createMagicBall(amount, 4, MAGICBALL_IV_NAME);
    }

    /** 魔法球通用构造 */
    private static ItemStack createMagicBall(int amount, int tier, String name) {
        ItemStack item = new ItemStack(FIREWORK_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.CHANNELING, 1, false);
        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList(
                ChatColor.LIGHT_PURPLE + "右键获得附魔书 保底" + tier + "本"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, MagicBallKey, tier);
        item.setAmount(amount);
        return item;
    }
}
