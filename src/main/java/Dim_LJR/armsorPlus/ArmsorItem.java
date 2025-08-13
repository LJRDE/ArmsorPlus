package Dim_LJR.armsorPlus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import static Dim_LJR.armsorPlus.ArmsorPlus.romanNumeral;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.Armorkey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.Armskey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.BasicStone;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.BlockingKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.BloodSacrificekey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.Bowkey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.DiamondPluskey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.Dodgekey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.EffectClear;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.ExplosiveArrowKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.Faminekey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.FreezeKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.GuideBookKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.HealthBoostKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.MagicBallKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.RevengeKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.RipplesProtectkey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.SurvivorKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.WitheringKey;
import static org.bukkit.Material.*;
import static org.bukkit.Material.OAK_LOG;

public class ArmsorItem {
    //将名称设为常量以防篡改同时可以进行比较
    public static final String ARMS_I_NAME = ChatColor.RED + "武器强化石Ⅰ";
    public static final String ARMS_II_NAME = ChatColor.DARK_RED + "武器强化石Ⅱ";
    public static final String ARMOR_I_NAME = ChatColor.BLUE + "护甲强化石Ⅰ";
    public static final String ARMOR_II_NAME = ChatColor.DARK_BLUE + "护甲强化石Ⅱ";
    public static final String BOW_I_NAME = ChatColor.DARK_RED + "弓箭强化石";
    public static final String DIAMONDPLUS = ChatColor.GOLD + "精炼金刚石";
    private static final String MagicBall_I_NAME = ChatColor.GREEN + "寻常的魔法球";
    private static final String MagicBall_II_NAME = ChatColor.BLUE + "稀罕的魔法球";
    private static final String MagicBall_III_NAME = ChatColor.GOLD + "史诗的魔法球";
    private static final String MagicBall_IV_NAME = ChatColor.LIGHT_PURPLE + "传奇的魔法球";
    private static final String BLOOD_SWORD_NAME = ChatColor.RED + "血祭之剑";
    private static final String Dodge_I_EnchantdeBook = ChatColor.GOLD + "闪避I";
    private static final String Dodge_II_EnchantdeBook = ChatColor.GOLD + "闪避II";
    private static final String Dodge_III_EnchantdeBook = ChatColor.GOLD + "闪避III";
    private static final String Dodge_IV_EnchantdeBook = ChatColor.GOLD + "闪避IV";
    private static final String Dodge_V_EnchantdeBook = ChatColor.GOLD + "闪避V";
    private static final String Famine_I_EnchantedBook = ChatColor.GREEN + "饥荒I";
    private static final String Famine_II_EnchantedBook = ChatColor.GREEN + "饥荒II";
    private static final String Famine_III_EnchantedBook = ChatColor.GREEN + "饥荒III";
    private static final String Ripples_I_EnchantedBook = ChatColor.BLUE + "涟漪I";
    private static final String Ripples_II_EnchantedBook = ChatColor.BLUE + "涟漪II";
    private static final String Ripples_III_EnchantedBook = ChatColor.BLUE + "涟漪III";
    private static final String BloodSacrifice_I_EnchantedBook = ChatColor.DARK_RED + "血祭I";
    private static final String BloodSacrifice_II_EnchantedBook = ChatColor.DARK_RED + "血祭II";
    private static final String BloodSacrifice_III_EnchantedBook = ChatColor.DARK_RED + "血祭III";
    private static final String EffectClear_EnchantedBook = ChatColor.WHITE + "涤魂";//涤魂
    private static final String Freeze_I_EnchantedBook = ChatColor.AQUA + "寒冻I";//寒冻
    private static final String Freeze_II_EnchantedBook = ChatColor.AQUA + "寒冻II";//寒冻
    private static final String Freeze_III_EnchantedBook = ChatColor.AQUA + "寒冻III";//寒冻
    private static final String Blocking_I_EnchantedBook = ChatColor.GOLD + "格挡I";
    private static final String Blocking_II_EnchantedBook = ChatColor.GOLD + "格挡II";
    private static final String Blocking_III_EnchantedBook = ChatColor.GOLD + "格挡III";
    private static final String Blocking_IV_EnchantedBook = ChatColor.GOLD + "格挡IV";
    private static final String Blocking_V_EnchantedBook = ChatColor.GOLD + "格挡V";
    private static final String Withering_I_EnchantedBook = ChatColor.BLACK + "凋零I";
    private static final String Withering_II_EnchantedBook = ChatColor.BLACK + "凋零II";
    private static final String Withering_III_EnchantedBook = ChatColor.BLACK + "凋零III";
    private static final String Withering_IV_EnchantedBook = ChatColor.BLACK + "凋零IV";
    private static final String Withering_V_EnchantedBook = ChatColor.BLACK + "凋零V";
    private static final String Survivor_I_EnchantedBook = ChatColor.GOLD + "幸存I";
    private static final String Survivor_II_EnchantedBook = ChatColor.GOLD + "幸存II";
    private static final String Survivor_III_EnchantedBook = ChatColor.GOLD + "幸存III";
    private static final String Survivor_IV_EnchantedBook = ChatColor.GOLD + "幸存IV";
    private static final String Survivor_V_EnchantedBook = ChatColor.GOLD + "幸存V";
    private static final String Revenge_I_EnchantedBook = ChatColor.DARK_RED + "复仇I";
    private static final String Revenge_II_EnchantedBook = ChatColor.DARK_RED + "复仇II";
    private static final String Revenge_III_EnchantedBook = ChatColor.DARK_RED + "复仇III";
    private static final String HealthBoost_I_EnchantedBook = ChatColor.RED + "生命提升I";
    private static final String HealthBoost_II_EnchantedBook = ChatColor.RED + "生命提升II";
    private static final String HealthBoost_III_EnchantedBook = ChatColor.RED + "生命提升III";
    private static final String HealthBoost_IV_EnchantedBook = ChatColor.RED + "生命提升IV";
    private static final String ExplosiveArrow_I_EnchantedBook = ChatColor.YELLOW + "蓄爆I";
    private static final String ExplosiveArrow_II_EnchantedBook = ChatColor.YELLOW + "蓄爆II";
    private static final String ExplosiveArrow_III_EnchantedBook = ChatColor.YELLOW + "蓄爆III";
    private static final String ShadowDodge_I_EnchantdeBook = ChatColor.DARK_PURPLE + "影避I";
    private static final String ShadowDodge_II_EnchantdeBook = ChatColor.DARK_PURPLE + "影避II";
    private static final String ShadowDodge_III_EnchantdeBook = ChatColor.DARK_PURPLE + "影避III";
    private static final String ShadowDodge_IV_EnchantdeBook = ChatColor.DARK_PURPLE + "影避IV";
    private static final String ShadowDodge_V_EnchantdeBook = ChatColor.DARK_PURPLE + "影避V";
    private static final String ArrowSpeed_EnchantedBook = ChatColor.GOLD + "弹道";
    private static final String Exorcism_EnchantedBook = ChatColor.GOLD + "除魔";
    private static final String Sniping_EnchantedBook = ChatColor.LIGHT_PURPLE + "狙击";
    private static final String DoubleHit_EnchantedBook = ChatColor.LIGHT_PURPLE + "双重打击";
    private static final String GuideBook = ChatColor.GOLD + "高级附魔向导";

    //物品声明
    public static ItemStack DoubleHit_EnchantdeBook(int Amount, int level)//双重打击附魔书
    {
        if(Amount==0)
            Amount=1;
        if(level==0)
            level=1;
        ItemStack book = new ItemStack(BOOK);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(DoubleHit_EnchantedBook + romanNumeral(level));
        meta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "可用装备:武器",
                ChatColor.LIGHT_PURPLE + "有" + level * 20 + "%概率获得双倍伤害",
                ChatColor.RESET + "拖动到装备上来使用",
                "级别" + level));
        book.setItemMeta(meta);
        ArmsorEnchant.addEnchant(book, NamespaceKey.Keys.DoubleHitkey,level);
        book.setAmount(Amount);
        return book;
    }
    public static ItemStack Sniping_EnchantdeBook(int Amount,int level)//狙击附魔书
    {
        if(Amount==0)
            Amount=1;
        if(level==0)
            level=1;
        ItemStack book = new ItemStack(BOOK);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(Sniping_EnchantedBook + romanNumeral(level));
        meta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "可用装备:弓与弩",
                ChatColor.LIGHT_PURPLE + "提升射出弓箭的速度和伤害，每级提升10倍",
                ChatColor.LIGHT_PURPLE + "需要前置附魔[弹道]",
                ChatColor.LIGHT_PURPLE + "仅史诗及以上魔法球可以获得",
                ChatColor.RESET + "拖动到装备上来使用",
                "级别" + level));
        book.setItemMeta(meta);
        ArmsorEnchant.addEnchant(book,Sniping,level);
        book.setAmount(Amount);
        return book;
    }
    public static ItemStack ArrowSpeed_EnchantdeBook(int Amount,int level)//弹道附魔书
    {
        if(Amount==0)
            Amount=1;
        if(level==0)
            level=1;
        ItemStack book = new ItemStack(BOOK);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(ArrowSpeed_EnchantedBook + romanNumeral(level));
        meta.setLore(Arrays.asList(ChatColor.GOLD + "可用装备:弓与弩",
                ChatColor.GOLD + "提升射出弓箭的速度和伤害，每级提升一倍",
                ChatColor.RESET + "拖动到装备上来使用",
                "级别" + level));
        book.setItemMeta(meta);
        ArmsorEnchant.addEnchant(book,ArrowSpeed,level);
        book.setAmount(Amount);
        return book;
    }
    public static ItemStack ShadowDodge_EnchantdeBook(int Amount,int level)//影避附魔书
    {
        if(Amount==0)
            Amount=1;
        if(level==0)
            level=1;
        ItemStack book = new ItemStack(BOOK);
        ItemMeta meta = book.getItemMeta();
        if(level==1) meta.setDisplayName(ShadowDodge_I_EnchantdeBook);
        else if(level==2) meta.setDisplayName(ShadowDodge_II_EnchantdeBook);
        else if(level==3) meta.setDisplayName(ShadowDodge_III_EnchantdeBook);
        else if(level==4) meta.setDisplayName(ShadowDodge_IV_EnchantdeBook);
        else if(level==5) meta.setDisplayName(ShadowDodge_V_EnchantdeBook);
        else meta.setDisplayName(ShadowDodge_I_EnchantdeBook);
        meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "可用装备:靴子",
                ChatColor.DARK_PURPLE + "概率闪避所有的伤害伤害",
                ChatColor.RESET + "拖动到装备上来使用",
                "级别" + level));
        book.setItemMeta(meta);
        ArmsorEnchant.addEnchant(book,ShadowDodge,level);
        book.setAmount(Amount);
        return book;
    }
    public static ItemStack ExplosiveArrow_EnchantedBook(int Amount, int level)//蓄爆附魔书
    {
        if(Amount == 0) Amount = 1;
        if(level == 0) level = 1;
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta meta = book.getItemMeta();
        switch(level) {
            case 1: meta.setDisplayName(ExplosiveArrow_I_EnchantedBook); break;
            case 2: meta.setDisplayName(ExplosiveArrow_II_EnchantedBook); break;
            case 3: meta.setDisplayName(ExplosiveArrow_III_EnchantedBook); break;
            default: meta.setDisplayName(ExplosiveArrow_I_EnchantedBook);
        }

        meta.setLore(Arrays.asList(ChatColor.RED + "可用装备:弓和弩 有概率发射一枚火箭弹",
                ChatColor.RESET + "拖动到弓或弩上来使用",
                "级别" + level));
        book.setItemMeta(meta);
        ArmsorEnchant.addEnchant(book, ExplosiveArrowKey, level);
        book.setAmount(Amount);
        return book;
    }
    public static ItemStack HealthBoost_EnchantedBook(int Amount, int level)//生命提升附魔书
    {
        if(Amount == 0) Amount = 1;
        if(level == 0) level = 1;
        if(level > 4) level = 4; // 最高4级

        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta meta = book.getItemMeta();

        switch(level) {
            case 1: meta.setDisplayName(HealthBoost_I_EnchantedBook); break;
            case 2: meta.setDisplayName(HealthBoost_II_EnchantedBook); break;
            case 3: meta.setDisplayName(HealthBoost_III_EnchantedBook); break;
            case 4: meta.setDisplayName(HealthBoost_IV_EnchantedBook); break;
            default: meta.setDisplayName(HealthBoost_I_EnchantedBook);
        }

        meta.setLore(Arrays.asList(ChatColor.RED + "可用装备:胸甲 提升生命上线",
                ChatColor.RESET + "拖动到胸甲上来使用",
                "级别" + level));
        book.setItemMeta(meta);
        ArmsorEnchant.addEnchant(book, HealthBoostKey, level);
        book.setAmount(Amount);
        return book;
    }
    public static ItemStack Revenge_EnchantedBook(int Amount, int level)//复仇附魔书
    {
        if(Amount == 0) Amount = 1;
        if(level == 0) level = 1;
        if(level > 3) level = 3; // 最高3级

        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta meta = book.getItemMeta();

        switch(level) {
            case 1: meta.setDisplayName(Revenge_I_EnchantedBook); break;
            case 2: meta.setDisplayName(Revenge_II_EnchantedBook); break;
            case 3: meta.setDisplayName(Revenge_III_EnchantedBook); break;
            default: meta.setDisplayName(Revenge_I_EnchantedBook);
        }

        meta.setLore(Arrays.asList(ChatColor.RED + "可用装备:胸甲 反弹一定比例的伤害",
                ChatColor.RESET + "拖动到胸甲上来使用",
                "级别" + level));
        book.setItemMeta(meta);
        ArmsorEnchant.addEnchant(book, RevengeKey, level);
        book.setAmount(Amount);
        return book;
    }
    public static ItemStack Blocking_EnchantedBook(int Amount, int level)//格挡附魔书
    {
        if(Amount == 0) Amount = 1;
        if(level == 0) level = 1;

        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta meta = book.getItemMeta();
        if(level == 1) meta.setDisplayName(Blocking_I_EnchantedBook);
        else if(level == 2) meta.setDisplayName(Blocking_II_EnchantedBook);
        else if(level == 3) meta.setDisplayName(Blocking_III_EnchantedBook);
        else if(level == 4) meta.setDisplayName(Blocking_IV_EnchantedBook);
        else if(level == 5) meta.setDisplayName(Blocking_V_EnchantedBook);
        else meta.setDisplayName(Blocking_I_EnchantedBook);
        meta.setLore(Arrays.asList(ChatColor.RED + "可用装备:头盔",ChatColor.RESET + "格挡一定伤害,受到伤害越高，格挡比例越高",
                ChatColor.RESET + "拖动到装备上来使用",
                "级别" + level));

        book.setItemMeta(meta);
        ArmsorEnchant.addEnchant(book, BlockingKey, level);
        book.setAmount(Amount);
        return book;
    }
    public static ItemStack Withering_EnchantedBook(int Amount, int level)//凋零附魔书
    {
        if(Amount == 0) Amount = 1;
        if(level == 0) level = 1;

        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta meta = book.getItemMeta();
        if(level == 1) meta.setDisplayName(Withering_I_EnchantedBook);
        else if(level == 2) meta.setDisplayName(Withering_II_EnchantedBook);
        else if(level == 3) meta.setDisplayName(Withering_III_EnchantedBook);
        else if(level == 4) meta.setDisplayName(Withering_IV_EnchantedBook);
        else if(level == 5) meta.setDisplayName(Withering_V_EnchantedBook);
        else meta.setDisplayName(Withering_I_EnchantedBook);
        meta.setLore(Arrays.asList(ChatColor.RED + "可用装备:武器",ChatColor.RED + "攻击时给对方造成凋零效果",
                ChatColor.RESET + "拖动到装备上来使用",
                "级别" + level));

        book.setItemMeta(meta);
        ArmsorEnchant.addEnchant(book, WitheringKey, level);
        book.setAmount(Amount);
        return book;
    }
    public static ItemStack Survivor_EnchantedBook(int Amount, int level)//幸存附魔书
    {
        if(Amount == 0) Amount = 1;
        if(level == 0) level = 1;
        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta meta = book.getItemMeta();
        if(level == 1) meta.setDisplayName(Survivor_I_EnchantedBook);
        else if(level == 2) meta.setDisplayName(Survivor_II_EnchantedBook);
        else if(level == 3) meta.setDisplayName(Survivor_III_EnchantedBook);
        else if(level == 4) meta.setDisplayName(Survivor_IV_EnchantedBook);
        else if(level == 5) meta.setDisplayName(Survivor_V_EnchantedBook);
        else meta.setDisplayName(Survivor_I_EnchantedBook);
        meta.setLore(Arrays.asList(ChatColor.RED + "可用装备:裤子",ChatColor.GOLD + "受到致命伤害概率复活",
                ChatColor.RESET + "拖动到装备上来使用",
                "级别" + level));

        book.setItemMeta(meta);
        ArmsorEnchant.addEnchant(book, SurvivorKey, level);
        book.setAmount(Amount);
        return book;
    }
    public static ItemStack Freeze_EnchantedBook(int Amount,int level)//寒冻附魔书
    {
        if(Amount==0)
            Amount=1;
        if(level==0)
            level=1;
        ItemStack book = new ItemStack(BOOK);
        ItemMeta meta = book.getItemMeta();
        if(level==1) meta.setDisplayName(Freeze_I_EnchantedBook);
        else if(level==2) meta.setDisplayName(Freeze_II_EnchantedBook);
        else if(level==3) meta.setDisplayName(Freeze_III_EnchantedBook);
        else meta.setDisplayName(Freeze_I_EnchantedBook);
        meta.setLore(Arrays.asList(ChatColor.RED + "可用装备:武器",ChatColor.GOLD + "有概率给对方造成寒冻效果",
                ChatColor.GOLD + "使对方移动速度下降",
                ChatColor.RESET + "拖动到装备上来使用",
                "级别" + level));
        book.setItemMeta(meta);
        ArmsorEnchant.addEnchant(book,FreezeKey,level);
        book.setAmount(Amount);
        return book;
    }
    public static ItemStack EffectClear_EnchantdeBook(int Amount,int level)//涤魂附魔书
    {
        if(Amount==0)
            Amount=1;
        if(level==0)
            level=1;
        ItemStack book = new ItemStack(BOOK);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(EffectClear_EnchantedBook + romanNumeral(level));
        meta.setLore(Arrays.asList(ChatColor.RED + "可用装备:胸甲",
                ChatColor.GOLD + "每隔一段时间免疫一次魔法伤害",
                ChatColor.RESET + "拖动到装备上来使用",
                "级别" + level));
        book.setItemMeta(meta);
        ArmsorEnchant.addEnchant(book,EffectClear,level);
        book.setAmount(Amount);
        return book;
    }
    public static ItemStack BasicStone(int Amount)//基础强化石
    {
        ItemStack item = new ItemStack(STONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "基础强化石");
        meta.setLore(Arrays.asList(ChatColor.BLUE + "用四个钻石块合成基础强化石",ChatColor.GOLD + "右键获得武器,护甲强化石"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item,BasicStone,1);
        item.setAmount(Amount);
        return item;
    }
    public static ItemStack GuideBook(int Amount)//向导书
    {
        ItemStack item = new ItemStack(BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(GuideBook);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item,GuideBookKey,1);
        item.setAmount(Amount);
        return item;
    }
    public static ItemStack BloodSacrifice_EnchantdeBook(int Amount,int level)//血祭附魔书
    {
        if(Amount==0)
            Amount=1;
        if(level==0)
            level=1;
        ItemStack book = new ItemStack(BOOK);
        ItemMeta meta = book.getItemMeta();
        if(level==1) meta.setDisplayName(BloodSacrifice_I_EnchantedBook);
        else if(level==2) meta.setDisplayName(BloodSacrifice_II_EnchantedBook);
        else if(level==3) meta.setDisplayName(BloodSacrifice_III_EnchantedBook);
        else meta.setDisplayName(BloodSacrifice_I_EnchantedBook);
        meta.setLore(Arrays.asList(ChatColor.RED + "可用装备:剑",
                ChatColor.GOLD + "概率扣自己的血量造成多倍伤害",
                ChatColor.GOLD + "倍率为2~" + level + 1 + "倍",
                ChatColor.RESET + "拖动到装备上来使用",
                "级别" + level));
        book.setItemMeta(meta);
        ArmsorEnchant.addEnchant(book,BloodSacrificekey,level);
        book.setAmount(Amount);
        return book;
    }
    public static ItemStack Ripples_EnchantdeBook(int Amount,int level)//涟漪附魔书
    {
        if(Amount==0)
            Amount=1;
        if(level==0)
            level=1;
        ItemStack book = new ItemStack(BOOK);
        ItemMeta meta = book.getItemMeta();
        if(level==1) meta.setDisplayName(Ripples_I_EnchantedBook);
        else if(level==2) meta.setDisplayName(Ripples_II_EnchantedBook);
        else if(level==3) meta.setDisplayName(Ripples_III_EnchantedBook);
        else meta.setDisplayName(Ripples_I_EnchantedBook);
        meta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "可用装备:靴子",
                ChatColor.GOLD + "受到伤害回复生命值",
                ChatColor.RESET + "拖动到装备上来使用",
                "级别" + level));
        book.setItemMeta(meta);
        ArmsorEnchant.addEnchant(book,RipplesProtectkey,level);
        book.setAmount(Amount);
        return book;
    }
    public static ItemStack Famine_EnchantdeBook(int Amount,int level)//饥荒附魔书
    {
        if(Amount==0)
            Amount=1;
        if(level==0)
            level=1;
        ItemStack book = new ItemStack(BOOK);
        ItemMeta meta = book.getItemMeta();
        if(level==1) meta.setDisplayName(Famine_I_EnchantedBook);
        else if(level==2) meta.setDisplayName(Famine_II_EnchantedBook);
        else if(level==3) meta.setDisplayName(Famine_III_EnchantedBook);
        else meta.setDisplayName(Famine_I_EnchantedBook);
        meta.setLore(Arrays.asList(ChatColor.GOLD + "可用装备:武器",
                ChatColor.GOLD + "给对方造成饥饿效果",
                ChatColor.RESET + "拖动到装备上来使用",
                "级别" + level));
        book.setItemMeta(meta);
        ArmsorEnchant.addEnchant(book,Faminekey,level);
        book.setAmount(Amount);
        return book;
    }
    public static ItemStack Dodge_EnchantdeBook(int Amount,int level)//闪避附魔书
    {
        if(Amount==0)
            Amount=1;
        if(level==0)
            level=1;
        ItemStack book = new ItemStack(BOOK);
        ItemMeta meta = book.getItemMeta();
        if(level==1) meta.setDisplayName(Dodge_I_EnchantdeBook);
        else if(level==2) meta.setDisplayName(Dodge_II_EnchantdeBook);
        else if(level==3) meta.setDisplayName(Dodge_III_EnchantdeBook);
        else if(level==4) meta.setDisplayName(Dodge_IV_EnchantdeBook);
        else if(level==5) meta.setDisplayName(Dodge_V_EnchantdeBook);
        else meta.setDisplayName(Dodge_I_EnchantdeBook);
        meta.setLore(Arrays.asList(ChatColor.GOLD + "可用装备:靴子",
                ChatColor.GOLD + "概率闪避对方的伤害",
                ChatColor.RESET + "拖动到装备上来使用",
                "级别" + level));
        book.setItemMeta(meta);
        ArmsorEnchant.addEnchant(book,Dodgekey,level);
        book.setAmount(Amount);
        return book;
    }
    public static ItemStack BloodSword(int x)//血祭之剑
    {
        ItemStack item = new ItemStack(GOLDEN_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.DAMAGE_ALL,5,true);
        meta.setDisplayName(BLOOD_SWORD_NAME);
        meta.setLore(Collections.singletonList(ChatColor.DARK_RED + "血祭V:攻击时有100%概率消耗15点生命值造成2~6倍伤害"));
        item.setAmount(x);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item,BloodSacrificekey,5);
        return item;
    }
    public static ItemStack MagicBallCreateI(int x)//寻常的魔法球
    {
        ItemStack item = new ItemStack(FIREWORK_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.CHANNELING,1,false);
        meta.setDisplayName(MagicBall_I_NAME);
        meta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE+ "右键获得魔法书",ChatColor.RESET + "保底1本"));
        item.setItemMeta(meta);
        item.setAmount(x);
        ArmsorEnchant.addEnchant(item,MagicBallKey,1);
        return item;
    }
    public static ItemStack MagicBallCreateII(int x)//稀罕的魔法球
    {
        ItemStack item = new ItemStack(FIREWORK_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.CHANNELING,1,false);
        meta.setDisplayName(MagicBall_II_NAME);
        meta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE+ "右键获得魔法书",ChatColor.RESET + "保底2本"));
        item.setItemMeta(meta);
        item.setAmount(x);
        ArmsorEnchant.addEnchant(item,MagicBallKey,2);
        return item;
    }
    public static ItemStack MagicBallCreateIII(int x)//史诗的魔法球
    {
        ItemStack item = new ItemStack(FIREWORK_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.CHANNELING,1,false);
        meta.setDisplayName(MagicBall_III_NAME);
        meta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE+ "右键获得魔法书",ChatColor.RESET + "保底3本"));
        item.setItemMeta(meta);
        item.setAmount(x);
        ArmsorEnchant.addEnchant(item,MagicBallKey,3);
        return item;
    }
    public static ItemStack MagicBallCreateIV(int x)//传奇的魔法球
    {
        ItemStack item = new ItemStack(FIREWORK_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.CHANNELING,1,false);
        meta.setDisplayName(MagicBall_IV_NAME);
        meta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE+ "右键获得魔法书",ChatColor.RESET + "保底4本"));
        item.setItemMeta(meta);
        item.setAmount(x);
        ArmsorEnchant.addEnchant(item,MagicBallKey,4);
        return item;
    }
    public static ItemStack DIAMONDPLUSCreate(int x)//精炼金刚石
    {
        ItemStack item = new ItemStack(DIAMOND);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY,1,false);
        meta.setDisplayName(DIAMONDPLUS);
        meta.setLore(Arrays.asList(ChatColor.BOLD + "拖动到装备上获得无限耐久"));
        item.setAmount(x);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item,DiamondPluskey,1);
        return item;
    }
    public static ItemStack ArmsPlusCreateI(int x)//武器强化石
    {
        ItemStack outward = new ItemStack(DIAMOND);//设置外观
        ItemMeta meta = outward.getItemMeta();
        meta.setDisplayName(ARMS_I_NAME);//设置名称
        meta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "使用使锋利等级+1"));
        outward.setAmount(x);
        outward.setItemMeta(meta);
        ArmsorEnchant.addEnchant(outward,Armskey,1);
        return outward;
    }
    public static ItemStack ArmsPlusCreateII(int x)//武器强化石II
    {
        ItemStack outward = new ItemStack(DIAMOND_ORE);//设置外观
        ItemMeta meta = outward.getItemMeta();
        meta.setDisplayName(ARMS_II_NAME);//设置名称
        meta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "使用伤害直接+1"));
        outward.setAmount(x);
        outward.setItemMeta(meta);
        ArmsorEnchant.addEnchant(outward,Armskey,2);
        return outward;
    }
    public static ItemStack ArmorPlusCreate(int x)//护甲强化石
    {
        ItemStack outward = new ItemStack(IRON_INGOT);
        ItemMeta meta = outward.getItemMeta();
        meta.setDisplayName(ARMOR_I_NAME);//设置名称
        meta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "使用使保护等级+1"));
        outward.setAmount(x);
        outward.setItemMeta(meta);
        ArmsorEnchant.addEnchant(outward,Armorkey,1);
        return outward;
    }
    public static ItemStack ArmorPlusCreateII(int x)//护甲强化石II
    {
        ItemStack outward = new ItemStack(IRON_ORE);
        ItemMeta meta = outward.getItemMeta();
        meta.setDisplayName(ARMOR_II_NAME);//设置名称
        meta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "使用使护甲值+1"));
        outward.setAmount(x);
        outward.setItemMeta(meta);
        ArmsorEnchant.addEnchant(outward,Armorkey,2);
        return outward;
    }
    public static ItemStack BowPlusCreate(int x)//弓箭强化石
    {
        ItemStack outward = new ItemStack(OAK_LOG);
        ItemMeta meta = outward.getItemMeta();
        meta.setDisplayName(BOW_I_NAME);//设置名称
        meta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "使用使力量等级+1"));
        outward.setAmount(x);
        outward.setItemMeta(meta);
        ArmsorEnchant.addEnchant(outward,Bowkey,1);
        return outward;
    }
}
