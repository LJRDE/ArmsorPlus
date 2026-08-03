package Dim_LJR.armsorPlus.Food;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;

// 所有食物/药品/树苗的定义与创建方法。
// 每个方法接收数量参数(amount)和可选的等级参数(level/tier)。
public class FoodItems {

    // 创建带自定义头颅纹理的食物/物品 (PLAYER_HEAD 基底)
    // profileName: 用于生成唯一头颅 UUID; texture: 皮肤 base64; lore: 物品说明
    private static ItemStack createHeadItem(String profileName, String texture, String displayName,
                                             List<String> lore, NamespacedKey key, int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes(profileName.getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", texture));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, key, 1);
        item.setAmount(amount);
        return item;
    }

    private static final String REJUVENATION_POWDER_NAME = ChatColor.LIGHT_PURPLE + "回春散";
    private static final String HEMOSTATIC_BANDAGE_NAME = ChatColor.RED + "止血绷带";
    private static final String COMPRESSED_BISCUIT_NAME = ChatColor.GOLD + "压缩饼干";

    // 回春散: 生命恢复V 3s，有概率合成出高级品
    public static ItemStack RejuvenationPowder(int amount) {
        return createRejuvenationPowder(amount, 0);
    }

    // 回春散: tier=1上品, tier=2极品, tier=3仙品
    public static ItemStack RejuvenationPowder(int amount, int tier) {
        return createRejuvenationPowder(amount, tier);
    }

    private static ItemStack createRejuvenationPowder(int amount, int tier) {
        ItemStack item = new ItemStack(GLASS_BOTTLE);
        ItemMeta meta = item.getItemMeta();
        String tierLabel;
        if (tier >= 3) {
            meta.setDisplayName(ChatColor.GOLD + "仙品·回春散");
            meta.setLore(Arrays.asList(
                    ChatColor.LIGHT_PURPLE + "右键使用",
                    ChatColor.GOLD + "清除所有负面效果",
                    ChatColor.RED + "生命恢复 X 120秒"));
            tierLabel = "仙品";
        } else if (tier >= 2) {
            meta.setDisplayName(ChatColor.YELLOW + "极品·回春散");
            meta.setLore(Arrays.asList(
                    ChatColor.LIGHT_PURPLE + "右键使用",
                    ChatColor.GOLD + "清除所有负面效果",
                    ChatColor.RED + "生命恢复 X 40秒"));
            tierLabel = "极品";
        } else if (tier >= 1) {
            meta.setDisplayName(ChatColor.GREEN + "上品·回春散");
            meta.setLore(Arrays.asList(
                    ChatColor.LIGHT_PURPLE + "右键使用",
                    ChatColor.RED + "生命恢复 V 6秒"));
            tierLabel = "上品";
        } else {
            meta.setDisplayName(REJUVENATION_POWDER_NAME);
            meta.setLore(Arrays.asList(
                    ChatColor.LIGHT_PURPLE + "右键使用",
                    ChatColor.RED + "生命恢复 V 3秒"));
            tierLabel = "普通";
        }
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, RejuvenationPowderKey, tier + 1);
        item.setAmount(amount);
        return item;
    }

    // 止血绷带
    public static ItemStack HemostaticBandage(int amount) {
        ItemStack item = new ItemStack(WHITE_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(HEMOSTATIC_BANDAGE_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "右键使用",
                ChatColor.RED + "瞬间恢复生命"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, HemostaticBandageKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 压缩饼干: 瞬间吃掉，等于9块面包
    public static ItemStack CompressedBiscuit(int amount) {
        ItemStack item = new ItemStack(BREAD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(COMPRESSED_BISCUIT_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "右键瞬间食用",
                ChatColor.GOLD + "恢复等同于9块面包的饱食度"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, CompressedBiscuitKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 盐
    public static ItemStack Salt(int amount) {
        ItemStack item = new ItemStack(SUGAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "盐");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "调味料",
                ChatColor.GRAY + "用于制作肉干和腐肉干"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SaltKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 肉干: 恢复6饥饿值，7.2饱和度
    public static ItemStack Jerky(int amount) {
        ItemStack item = new ItemStack(COOKED_BEEF);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "肉干");
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "右键食用",
                ChatColor.GOLD + "恢复6点饥饿值",
                ChatColor.YELLOW + "7.2饱和度"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, JerkyKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 猪肉干: 恢复7饥饿值，8.0饱和度
    public static ItemStack PorkJerky(int amount) {
        ItemStack item = new ItemStack(COOKED_PORKCHOP);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "猪肉干");
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "右键食用",
                ChatColor.GOLD + "恢复7点饥饿值",
                ChatColor.YELLOW + "8.0饱和度"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, PorkJerkyKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 羊肉干: 恢复5饥饿值，6.0饱和度
    public static ItemStack MuttonJerky(int amount) {
        ItemStack item = new ItemStack(COOKED_MUTTON);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "羊肉干");
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "右键食用",
                ChatColor.GOLD + "恢复5点饥饿值",
                ChatColor.YELLOW + "6.0饱和度"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, MuttonJerkyKey, 1);
        item.setAmount(amount);
        return item;
    }

    // ========================================================================
    // 大苹果与9种原始水果
    // ========================================================================

    private static final String BIG_APPLE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTJiMzViZGE1ZWJkZjEzNWY0ZTcxY2U0OTcyNmZiZWM1NzM5ZjBhZGVkZjAxYzUxOWUyYWVhN2Y1MTk1MWVhMiJ9fX0=";
    private static final String PLUM_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y0YmRjYzFmZTY0YjY0YTY4MGVlNDA3N2U3MzQyNmNkMTg2ZTMxNTg3MTY3ODM3NDVkN2U5MGU5MmJhNDhjMyJ9fX0=";
    private static final String HAZELNUT_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTNkNmNkMmMzNjY2NTUyNDM2YjhjN2FmYmE5MDhlMmM0YmMxNjFiZThiODY1M2U0NWU4MmY3OTViM2YxIn19fQ==";
    private static final String COCONUT_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjkxMzlhNWIxZjkxNjkzODM1ZTM0OTAzY2UzNGJlODZjOTI2ZTlhMWRmZmNiMzc3Y2M1ZWQ4ZjMzZDk5ODIxIn19fQ==";
    private static final String PINEAPPLE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTdjNWU5MjVhOTQ5ZTU1ZGIyYzI1ZWZhYWQ2NDUxMmViNmRhYjc0YWZmYjJlOWYzMDRjMzg1YjRmNGIzMGJhNSJ9fX0=";
    private static final String STRAWBERRY_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JjODI2YWFhZmI4ZGJmNjc4ODFlNjg5NDQ0MTRmMTM5ODUwNjRhM2Y4ZjA0NGQ4ZWRmYjQ0NDNlNzZiYSJ9fX0=";
    private static final String BLUEBERRY_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDI2OTdmM2VmOGY0NjI5YjY0NWZkMmU2NDQ2NDEzMjRhMWMxMTgzNTQ5OGU2MzhmNzU3ZjI3OGFmYmNlNWRiMSJ9fX0=";
    private static final String ORANGE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjViMWRiNTQ3ZDFiNzk1NmQ0NTExYWNjYjE1MzNlMjE3NTZkN2NiYzM4ZWI2NDM1NWEyNjI2NDEyMjEyIn19fQ==";
    private static final String TANGERINE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWNkYWQyMzQ5NDgzYzBkNDg5Y2M0NjRiZWVkNzI4YzZhMDQxOTkyYTIwMWQyM2FlNTU1MzFmMDMzNGZjMTQwOSJ9fX0=";
    private static final String ICE_CUBE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTI2NDQwNzFiNmM3YmJhZTdiNWU0NWQ5ZjgyZjk2ZmZiNWVlOGUxNzdhMjNiODI1YTQ0NjU2MDdmMWM5YyJ9fX0=";

    // 大苹果: 恢复4饥饿值 + 2饱和度
    public static ItemStack BigApple(int amount) {
        return createHeadItem("ArmsorPlus_BigApple", BIG_APPLE_TEXTURE, ChatColor.RED + "大苹果",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复4点饥饿值",
                        ChatColor.YELLOW + "恢复2点饱和度",
                        ChatColor.GRAY + "传说中的大苹果"), BigAppleKey, amount);
    }

    // 李子: 恢复3饥饿值 + 2饱和度
    public static ItemStack Plum(int amount) {
        return createHeadItem("ArmsorPlus_Plum", PLUM_TEXTURE, ChatColor.LIGHT_PURPLE + "李子",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复3点饥饿值",
                        ChatColor.YELLOW + "恢复2点饱和度",
                        ChatColor.GRAY + "酸甜可口的李子"), PlumKey, amount);
    }

    // 榛子: 恢复2饥饿值 + 3饱和度
    public static ItemStack Hazelnut(int amount) {
        return createHeadItem("ArmsorPlus_Hazelnut", HAZELNUT_TEXTURE, ChatColor.GOLD + "榛子",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复2点饥饿值",
                        ChatColor.YELLOW + "恢复3点饱和度",
                        ChatColor.GRAY + "香脆的榛子"), HazelnutKey, amount);
    }

    // 椰子: 恢复5饥饿值 + 3饱和度
    public static ItemStack Coconut(int amount) {
        return createHeadItem("ArmsorPlus_Coconut", COCONUT_TEXTURE, ChatColor.WHITE + "椰子",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复5点饥饿值",
                        ChatColor.YELLOW + "恢复3点饱和度",
                        ChatColor.GRAY + "清凉解渴的椰子"), CoconutKey, amount);
    }

    // 菠萝: 恢复6饥饿值 + 5饱和度
    public static ItemStack Pineapple(int amount) {
        return createHeadItem("ArmsorPlus_Pineapple", PINEAPPLE_TEXTURE, ChatColor.YELLOW + "菠萝",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复6点饥饿值",
                        ChatColor.YELLOW + "恢复5点饱和度",
                        ChatColor.GRAY + "多汁的热带菠萝"), PineappleKey, amount);
    }

    // 草莓: 恢复2饥饿值 + 1.5饱和度
    public static ItemStack Strawberry(int amount) {
        return createHeadItem("ArmsorPlus_Strawberry", STRAWBERRY_TEXTURE, ChatColor.RED + "草莓",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复2点饥饿值",
                        ChatColor.YELLOW + "恢复1.5点饱和度",
                        ChatColor.GRAY + "鲜红的草莓"), StrawberryKey, amount);
    }

    // 蓝莓: 恢复2饥饿值 + 1饱和度
    public static ItemStack Blueberry(int amount) {
        return createHeadItem("ArmsorPlus_Blueberry", BLUEBERRY_TEXTURE, ChatColor.DARK_PURPLE + "蓝莓",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复2点饥饿值",
                        ChatColor.YELLOW + "恢复1点饱和度",
                        ChatColor.GRAY + "小小的蓝莓"), BlueberryKey, amount);
    }

    // 橙子: 恢复4饥饿值 + 3饱和度
    public static ItemStack Orange(int amount) {
        return createHeadItem("ArmsorPlus_Orange", ORANGE_TEXTURE, ChatColor.GOLD + "橙子",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复4点饥饿值",
                        ChatColor.YELLOW + "恢复3点饱和度",
                        ChatColor.GRAY + "新鲜的橙子"), OrangeKey, amount);
    }

    // 橘子: 恢复3饥饿值 + 2饱和度
    public static ItemStack Tangerine(int amount) {
        return createHeadItem("ArmsorPlus_Tangerine", TANGERINE_TEXTURE, ChatColor.GOLD + "橘子",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复3点饥饿值",
                        ChatColor.YELLOW + "恢复2点饱和度",
                        ChatColor.GRAY + "甜甜的橘子"), TangerineKey, amount);
    }

    // 冰块: 恢复1饥饿值 + 1饱和度
    public static ItemStack IceCube(int amount) {
        return createHeadItem("ArmsorPlus_IceCube", ICE_CUBE_TEXTURE, ChatColor.AQUA + "冰块",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复1点饥饿值",
                        ChatColor.YELLOW + "恢复1点饱和度",
                        ChatColor.GRAY + "冰冰凉凉的冰块"), IceCubeKey, amount);
    }

    // ========================================================================
    // 14种新水果
    // ========================================================================

    private static final String FIG_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTBiMDUzN2MwYzBlODkyOGJiN2M4NWE0MjVlY2U3Nzc0OTRkNTA4ZTU1ZGU1OWY4ZThmNDYyZWVjYmMwNzgzNSJ9fX0=";
    private static final String DATE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWRkZGNiMjg3NjRmYzE1NDhiNWNhMmY1Nzc5ODNlZGRkYTY1YTUwMTM4MGUzZmY1NDI3NDE1ZTAyMTY3ODAifX19";
    private static final String PERSIMMON_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjU2MmE5ZTAxOWIwN2YzYjYwYjI0ZjQ2ZWIyOTM0OWQxZDZkMjY5NWI2ZGM2MTllZDZjZmNhZWFmMjFjMGYyYiJ9fX0=";
    private static final String MANGOSTEEN_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzJlYzA5OTk0NWViMDlhZWZhMzc5ODBiZTU2MTE2Y2Q3NTQ2OTI2OGJmNDdhN2M5ZTNjMmU5ZWQ2YzgwNTY3OSJ9fX0=";
    private static final String CHERRY_TOMATO_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc3YWQ3YjBlYmZiM2MyZWVhM2Y5Y2Q0NTI5ZTExNTMxOWFiYTNlNzEyZTMzMzU1MGM5OWQ1Y2YyNGIwODkifX19";
    private static final String TOMATO_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDE4Njg2M2ZkZjNmM2UyOTc5ZWM2ZDk3OGEwODhkOGE0Zjg2NjRjNTBiNWI2ZDI5YWQ0YTdhYzI2NGEwMTcifX19";
    private static final String GRAPE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjdlMzE2M2QyMTM3MDFhYWMwNjFkMDhlMzNiOTE1ZWYyZTEwZjUxNDI2MzMyY2VhNzU2ZjA3ZTkyY2JiMzU1YyJ9fX0=";
    private static final String POMEGRANATE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmQ5ZDNkZGQxZWJkOTQ0OGE2ZGM0ZWU0ZmI5MTgzZDhmYzY4ZDQzNDY4ODE4OWE3MzU2MWZkNmZmODVmMDRiIn19fQ==";
    private static final String CHESTNUT_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjAxMzQyYzE4NzlkZTZjNzNlODVlMjk1YTg5NDU4Yzk3ZTEyZjZhM2IyMjgwZjAzMzQ3MGQzNTAwMWU0NDM2In19fQ==";
    private static final String KIWI_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNjMThlYzQ2NDlmMDdkNWEzOGE1ODNkOTI3MWZkODNhNmYzNzMxODc1OGU0NmVhODdmYzJiMmQxYWZjMmQ5In19fQ==";
    private static final String LONGAN_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTA5YTJkYWM2M2Y4YWZlM2RmZDFhMDgyM2Y2ODQ2NDc1NDU1YTIzYTU2M2Q4MmQzYTNhMzRlNDIyMjQyY2JhNiJ9fX0=";
    private static final String CHERRY_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWU1M2EzYWVlYzk2NjJiZTUxM2M5NDA0OWE5ZWUwYWFkZTM1MTcwMTVhOTc2MzhkOWY4NmIyMWU4YjMxODJlYSJ9fX0=";
    private static final String PEACH_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmFkYmJhYjM4ODFhYWNiYTU3N2UyN2JiZWUxZmJlNGI5YTUwZTE5ZjVhODdmOGQ0OWI2MzYwNTRmYTE3ODhmYyJ9fX0=";

    // 无花果: 恢复4饥饿值 + 3饱和度
    public static ItemStack Fig(int amount) {
        return createHeadItem("ArmsorPlus_Fig", FIG_TEXTURE, ChatColor.LIGHT_PURPLE + "无花果",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复4点饥饿值",
                        ChatColor.YELLOW + "恢复3点饱和度",
                        ChatColor.GRAY + "甜美的无花果"), FigKey, amount);
    }

    // 枣子: 恢复3饥饿值 + 2饱和度
    public static ItemStack Date(int amount) {
        return createHeadItem("ArmsorPlus_Date", DATE_TEXTURE, ChatColor.GOLD + "枣子",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复3点饥饿值",
                        ChatColor.YELLOW + "恢复2点饱和度",
                        ChatColor.GRAY + "甜甜的枣子"), DateKey, amount);
    }

    // 柿子: 恢复4饥饿值 + 3饱和度
    public static ItemStack Persimmon(int amount) {
        return createHeadItem("ArmsorPlus_Persimmon", PERSIMMON_TEXTURE, ChatColor.GOLD + "柿子",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复4点饥饿值",
                        ChatColor.YELLOW + "恢复3点饱和度",
                        ChatColor.GRAY + "软糯的柿子"), PersimmonKey, amount);
    }

    // 山竹: 恢复5饥饿值 + 4饱和度
    public static ItemStack Mangosteen(int amount) {
        return createHeadItem("ArmsorPlus_Mangosteen", MANGOSTEEN_TEXTURE, ChatColor.DARK_PURPLE + "山竹",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复5点饥饿值",
                        ChatColor.YELLOW + "恢复4点饱和度",
                        ChatColor.GRAY + "酸甜可口的山竹"), MangosteenKey, amount);
    }

    // 圣女果: 恢复2饥饿值 + 1饱和度
    public static ItemStack CherryTomato(int amount) {
        return createHeadItem("ArmsorPlus_CherryTomato", CHERRY_TOMATO_TEXTURE, ChatColor.RED + "圣女果",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复2点饥饿值",
                        ChatColor.YELLOW + "恢复1点饱和度",
                        ChatColor.GRAY + "小巧的圣女果"), CherryTomatoKey, amount);
    }

    // 西红柿: 恢复3饥饿值 + 2.5饱和度
    public static ItemStack Tomato(int amount) {
        return createHeadItem("ArmsorPlus_Tomato", TOMATO_TEXTURE, ChatColor.RED + "西红柿",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复3点饥饿值",
                        ChatColor.YELLOW + "恢复2.5点饱和度",
                        ChatColor.GRAY + "新鲜的西红柿"), TomatoKey, amount);
    }

    // 葡萄: 恢复3饥饿值 + 2饱和度
    public static ItemStack Grape(int amount) {
        return createHeadItem("ArmsorPlus_Grape", GRAPE_TEXTURE, ChatColor.DARK_PURPLE + "葡萄",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复3点饥饿值",
                        ChatColor.YELLOW + "恢复2点饱和度",
                        ChatColor.GRAY + "甜甜的葡萄"), GrapeKey, amount);
    }

    // 石榴: 恢复5饥饿值 + 4饱和度
    public static ItemStack Pomegranate(int amount) {
        return createHeadItem("ArmsorPlus_Pomegranate", POMEGRANATE_TEXTURE, ChatColor.RED + "石榴",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复5点饥饿值",
                        ChatColor.YELLOW + "恢复4点饱和度",
                        ChatColor.GRAY + "多汁的石榴"), PomegranateKey, amount);
    }

    // 栗子: 恢复3饥饿值 + 4饱和度
    public static ItemStack Chestnut(int amount) {
        return createHeadItem("ArmsorPlus_Chestnut", CHESTNUT_TEXTURE, ChatColor.GOLD + "栗子",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复3点饥饿值",
                        ChatColor.YELLOW + "恢复4点饱和度",
                        ChatColor.GRAY + "香糯的栗子"), ChestnutKey, amount);
    }

    // 猕猴桃: 恢复4饥饿值 + 3饱和度
    public static ItemStack Kiwi(int amount) {
        return createHeadItem("ArmsorPlus_Kiwi", KIWI_TEXTURE, ChatColor.GREEN + "猕猴桃",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复4点饥饿值",
                        ChatColor.YELLOW + "恢复3点饱和度",
                        ChatColor.GRAY + "酸酸甜甜的猕猴桃"), KiwiKey, amount);
    }

    // 龙眼: 恢复3饥饿值 + 2饱和度
    public static ItemStack Longan(int amount) {
        return createHeadItem("ArmsorPlus_Longan", LONGAN_TEXTURE, ChatColor.YELLOW + "龙眼",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复3点饥饿值",
                        ChatColor.YELLOW + "恢复2点饱和度",
                        ChatColor.GRAY + "甜美的龙眼"), LonganKey, amount);
    }

    // 荔枝: 恢复4饥饿值 + 3饱和度
    public static ItemStack Lychee(int amount) {
        return createHeadItem("ArmsorPlus_Lychee", LONGAN_TEXTURE, ChatColor.RED + "荔枝",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复4点饥饿值",
                        ChatColor.YELLOW + "恢复3点饱和度",
                        ChatColor.GRAY + "多汁的荔枝"), LycheeKey, amount);
    }

    // 樱桃: 恢复2饥饿值 + 1.5饱和度
    public static ItemStack Cherry(int amount) {
        return createHeadItem("ArmsorPlus_Cherry", CHERRY_TEXTURE, ChatColor.RED + "樱桃",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复2点饥饿值",
                        ChatColor.YELLOW + "恢复1.5点饱和度",
                        ChatColor.GRAY + "鲜红的樱桃"), CherryKey, amount);
    }

    // 桃子: 恢复5饥饿值 + 4饱和度
    public static ItemStack Peach(int amount) {
        return createHeadItem("ArmsorPlus_Peach", PEACH_TEXTURE, ChatColor.LIGHT_PURPLE + "桃子",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复5点饥饿值",
                        ChatColor.YELLOW + "恢复4点饱和度",
                        ChatColor.GRAY + "多汁的桃子"), PeachKey, amount);
    }

    // ========================================================================
    // 树苗物品
    // ========================================================================

    private static final String FIG_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTdiMjQ1ZjNmMTUwYmE2MjE2NzU5MTZhMjlkMDA2OTA5OTBkZGQ4ZGU2MDMyOTNkZDJmNWY4MDA3MGEyOTA4In19fQ==";
    private static final String DATE_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmYxMjBiZGQwNDdlNWU1ZGVlZDkzYzU3ZjhmZDE1Y2JkMGZjOWRhZDBmZmYwMWIxYjY3MzMyMGFhZmVhMGY5In19fQ==";
    private static final String PERSIMMON_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg3YzMzNTQ1NDNiNjUyNGUzMDUzOTQ0ZjZmOTNmNTFjMjc0M2Q5ODk4NjY5M2I3NjY5MmFiMGVmNjYxZTliZiJ9fX0=";
    private static final String MANGOSTEEN_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWI5MzMzNjEwYjRkNWM1MDI1MTQzNTA1MmIzOWUwNzIzZjc4YjY4YzExNDYyM2I1ODcyMDJjMjhmNWViYWZkMCJ9fX0=";
    private static final String CHERRY_TOMATO_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTExMjRkYmE3NjZkN2Q2MGNlYWRmMGU0NDVjMGViMDZmOTQ0YmIzMDNjZjhmYTI3YzI0NWZmZWQ4YTgzZjQxOCJ9fX0=";
    private static final String TOMATO_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjdmYTY0YmJiNGJkNTE0Zjc5ZDdlYTQzNGI1MGMwYmQzNDJkOWY4MDdiZTllMTA4NDZkMGU4NTA1MTY2MzlmYyJ9fX0=";
    private static final String GRAPE_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjAxMzU1OTJjZGIzNjdjNTljNjhkYzhmODIxMjM3ZGJhMThmZDUxN2MzYWIyYjc2MDBiZjQyOWYyN2JmMDYxIn19fQ==";
    private static final String POMEGRANATE_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQwZTc3ODU1ZDg1Yjk0OTg3YmQzNTJhYWUxNmM2N2M5NTY0OTg2MzM2OWIxYjZjMzYzNjI3NTVhNjRhMTIxNCJ9fX0=";
    private static final String CHESTNUT_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODVlMWEzOWYyMzBjYWM4NGQ0YzU2NTYxZmQ4MDg5MjU1ZTkyZjdmMDA0NGNkZmI1ZWQ3ZDc2MTFmNjJmYjBhIn19fQ==";
    private static final String KIWI_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmJkNjM4YTgyMGIxOTZiNDY3ZTA0YzEzNThhOTM1ZjAyY2NjZjE1N2YzODI5NzFmM2Q0OGQyNWFiZTE5Y2JjMyJ9fX0=";
    private static final String LONGAN_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjNjNzgxMjQxYTAzN2IzMjFhYmVlODlhYjIzODQ5OWVlOGQ1NWU1MDI2MzBhZTY0OGNmOTNiYWUwNzVjZDgxZCJ9fX0=";
    private static final String LYCHEE_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzAxNzQzYmVmMTUwNDI0NjIwMmQ0MTQ0ZTY0MWIwMGNlYTA3ZjgyZDcwM2M2YzJhYTQ2OWY0ZWY3ZTU0ZDZkMCJ9fX0=";
    private static final String CHERRY_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVjOTdiMDZmY2E5NDg4NzgzMGI1YTMxOWJiOWE5ODc3M2VlZTZlNDQ5MDA3ZDEyODcwZTUwNGEzYmEwZWUyMiJ9fX0=";
    private static final String PEACH_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWEzYWM4YTU5ODM4NjVjYzY5YjJlYTZhYTlmNTA5Y2ExY2UwYzc2OGRhMTUwMDQxNmRhNzFhMzMwOGFkYzlkYiJ9fX0=";
    private static final String PLUM_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTdiMjQ1ZjNmMTUwYmE2MjE2NzU5MTZhMjlkMDA2OTA5OTBkZGQ4ZGU2MDMyOTNkZDJmNWY4MDA3MGEyOTA4In19fQ==";
    private static final String HAZELNUT_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmYxMjBiZGQwNDdlNWU1ZGVlZDkzYzU3ZjhmZDE1Y2JkMGZjOWRhZDBmZmYwMWIxYjY3MzMyMGFhZmVhMGY5In19fQ==";
    private static final String COCONUT_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg3YzMzNTQ1NDNiNjUyNGUzMDUzOTQ0ZjZmOTNmNTFjMjc0M2Q5ODk4NjY5M2I3NjY5MmFiMGVmNjYxZTliZiJ9fX0=";
    private static final String PINEAPPLE_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWI5MzMzNjEwYjRkNWM1MDI1MTQzNTA1MmIzOWUwNzIzZjc4YjY4YzExNDYyM2I1ODcyMDJjMjhmNWViYWZkMCJ9fX0=";
    private static final String STRAWBERRY_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTExMjRkYmE3NjZkN2Q2MGNlYWRmMGU0NDVjMGViMDZmOTQ0YmIzMDNjZjhmYTI3YzI0NWZmZWQ4YTgzZjQxOCJ9fX0=";
    private static final String BLUEBERRY_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjdmYTY0YmJiNGJkNTE0Zjc5ZDdlYTQzNGI1MGMwYmQzNDJkOWY4MDdiZTllMTA4NDZkMGU4NTA1MTY2MzlmYyJ9fX0=";
    private static final String ORANGE_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjAxMzU1OTJjZGIzNjdjNTljNjhkYzhmODIxMjM3ZGJhMThmZDUxN2MzYWIyYjc2MDBiZjQyOWYyN2JmMDYxIn19fQ==";
    private static final String TANGERINE_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQwZTc3ODU1ZDg1Yjk0OTg3YmQzNTJhYWUxNmM2N2M5NTY0OTg2MzM2OWIxYjZjMzYzNjI3NTVhNjRhMTIxNCJ9fX0=";
    private static final String BIG_APPLE_SAPLING_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODVlMWEzOWYyMzBjYWM4NGQ0YzU2NTYxZmQ4MDg5MjU1ZTkyZjdmMDA0NGNkZmI1ZWQ3ZDc2MTFmNjJmYjBhIn19fQ==";

    public static ItemStack createSapling(String displayName, ChatColor color, String texture, NamespacedKey key, String uuidStr, String description, int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes(uuidStr.getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", texture));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(color + displayName + "树苗");
        meta.setLore(Arrays.asList(
                ChatColor.GREEN + "右键泥土/草方块种植",
                ChatColor.GRAY + description));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, key, 1);
        item.setAmount(amount);
        return item;
    }

    public static ItemStack FigSapling(int amount) { return createSapling("无花果", ChatColor.LIGHT_PURPLE, FIG_SAPLING_TEXTURE, FigSaplingKey, "ArmsorPlus_FigSapling", "无花果树树苗", amount); }
    public static ItemStack DateSapling(int amount) { return createSapling("枣子", ChatColor.GOLD, DATE_SAPLING_TEXTURE, DateSaplingKey, "ArmsorPlus_DateSapling", "枣树树苗", amount); }
    public static ItemStack PersimmonSapling(int amount) { return createSapling("柿子", ChatColor.GOLD, PERSIMMON_SAPLING_TEXTURE, PersimmonSaplingKey, "ArmsorPlus_PersimmonSapling", "柿子树树苗", amount); }
    public static ItemStack MangosteenSapling(int amount) { return createSapling("山竹", ChatColor.DARK_PURPLE, MANGOSTEEN_SAPLING_TEXTURE, MangosteenSaplingKey, "ArmsorPlus_MangosteenSapling", "山竹树树苗", amount); }
    public static ItemStack CherryTomatoSapling(int amount) { return createSapling("圣女果", ChatColor.RED, CHERRY_TOMATO_SAPLING_TEXTURE, CherryTomatoSaplingKey, "ArmsorPlus_CherryTomatoSapling", "圣女果种子", amount); }
    public static ItemStack TomatoSapling(int amount) { return createSapling("西红柿", ChatColor.RED, TOMATO_SAPLING_TEXTURE, TomatoSaplingKey, "ArmsorPlus_TomatoSapling", "西红柿种子", amount); }
    public static ItemStack GrapeSapling(int amount) { return createSapling("葡萄", ChatColor.DARK_PURPLE, GRAPE_SAPLING_TEXTURE, GrapeSaplingKey, "ArmsorPlus_GrapeSapling", "葡萄藤苗", amount); }
    public static ItemStack PomegranateSapling(int amount) { return createSapling("石榴", ChatColor.RED, POMEGRANATE_SAPLING_TEXTURE, PomegranateSaplingKey, "ArmsorPlus_PomegranateSapling", "石榴树树苗", amount); }
    public static ItemStack ChestnutSapling(int amount) { return createSapling("栗子", ChatColor.GOLD, CHESTNUT_SAPLING_TEXTURE, ChestnutSaplingKey, "ArmsorPlus_ChestnutSapling", "栗子树树苗", amount); }
    public static ItemStack KiwiSapling(int amount) { return createSapling("猕猴桃", ChatColor.GREEN, KIWI_SAPLING_TEXTURE, KiwiSaplingKey, "ArmsorPlus_KiwiSapling", "猕猴桃藤苗", amount); }
    public static ItemStack LonganSapling(int amount) { return createSapling("龙眼", ChatColor.YELLOW, LONGAN_SAPLING_TEXTURE, LonganSaplingKey, "ArmsorPlus_LonganSapling", "龙眼树树苗", amount); }
    public static ItemStack LycheeSapling(int amount) { return createSapling("荔枝", ChatColor.RED, LYCHEE_SAPLING_TEXTURE, LycheeSaplingKey, "ArmsorPlus_LycheeSapling", "荔枝树树苗", amount); }
    public static ItemStack CherrySapling(int amount) { return createSapling("樱桃", ChatColor.RED, CHERRY_SAPLING_TEXTURE, CherrySaplingKey, "ArmsorPlus_CherrySapling", "樱桃树树苗", amount); }
    public static ItemStack PeachSapling(int amount) { return createSapling("桃子", ChatColor.LIGHT_PURPLE, PEACH_SAPLING_TEXTURE, PeachSaplingKey, "ArmsorPlus_PeachSapling", "桃树树苗", amount); }
    public static ItemStack PlumSapling(int amount) { return createSapling("李子", ChatColor.LIGHT_PURPLE, PLUM_SAPLING_TEXTURE, PlumSaplingKey, "ArmsorPlus_PlumSapling", "李子树树苗", amount); }
    public static ItemStack HazelnutSapling(int amount) { return createSapling("榛子", ChatColor.GOLD, HAZELNUT_SAPLING_TEXTURE, HazelnutSaplingKey, "ArmsorPlus_HazelnutSapling", "榛子树树苗", amount); }
    public static ItemStack CoconutSapling(int amount) { return createSapling("椰子", ChatColor.WHITE, COCONUT_SAPLING_TEXTURE, CoconutSaplingKey, "ArmsorPlus_CoconutSapling", "椰子树树苗", amount); }
    public static ItemStack PineappleSapling(int amount) { return createSapling("菠萝", ChatColor.YELLOW, PINEAPPLE_SAPLING_TEXTURE, PineappleSaplingKey, "ArmsorPlus_PineappleSapling", "菠萝树苗", amount); }
    public static ItemStack StrawberrySapling(int amount) { return createSapling("草莓", ChatColor.RED, STRAWBERRY_SAPLING_TEXTURE, StrawberrySaplingKey, "ArmsorPlus_StrawberrySapling", "草莓种子", amount); }
    public static ItemStack BlueberrySapling(int amount) { return createSapling("蓝莓", ChatColor.DARK_PURPLE, BLUEBERRY_SAPLING_TEXTURE, BlueberrySaplingKey, "ArmsorPlus_BlueberrySapling", "蓝莓种子", amount); }
    public static ItemStack OrangeSapling(int amount) { return createSapling("橙子", ChatColor.GOLD, ORANGE_SAPLING_TEXTURE, OrangeSaplingKey, "ArmsorPlus_OrangeSapling", "橙子树树苗", amount); }
    public static ItemStack TangerineSapling(int amount) { return createSapling("橘子", ChatColor.GOLD, TANGERINE_SAPLING_TEXTURE, TangerineSaplingKey, "ArmsorPlus_TangerineSapling", "橘子树树苗", amount); }
    public static ItemStack BigAppleSapling(int amount) { return createSapling("大苹果", ChatColor.RED, BIG_APPLE_SAPLING_TEXTURE, BigAppleSaplingKey, "ArmsorPlus_BigAppleSapling", "苹果树树苗", amount); }

    // ========================================================================
    // 功能性食物续
    // ========================================================================

    // 甜浆果派: 恢复9饥饿值，8饱和度
    public static ItemStack SweetBerryPie(int amount) {
        ItemStack item = new ItemStack(PUMPKIN_PIE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "甜浆果派");
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "右键食用",
                ChatColor.GOLD + "恢复9点饥饿值",
                ChatColor.YELLOW + "8.0饱和度"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SweetBerryPieKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 酒桶: 放置后打开有9瓶酒
    public static ItemStack WineBarrel(int amount) {
        ItemStack item = new ItemStack(BARREL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "酒桶");
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "放置到地上右键打开",
                ChatColor.GOLD + "内含9瓶随机品质的酒"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, WineBarrelKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 酒 (tier: 1=酒, 2=佳酿, 3=金樽清酒)
    public static ItemStack Wine(int amount, int tier) {
        ItemStack item = new ItemStack(POTION);
        ItemMeta meta = item.getItemMeta();
        String name;
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "右键饮用");
        if (tier >= 3) {
            name = ChatColor.GOLD + "金樽清酒";
            lore.add(ChatColor.GOLD + "力量 V 持续140秒");
            lore.add(ChatColor.RED + "持续时间内死亡可复活一次");
        } else if (tier >= 2) {
            name = ChatColor.YELLOW + "佳酿";
            lore.add(ChatColor.YELLOW + "力量 III 持续45秒");
        } else {
            name = ChatColor.WHITE + "酒";
            lore.add(ChatColor.GRAY + "力量 II 持续30秒");
        }
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, WineKey, tier);
        item.setAmount(amount);
        return item;
    }

    // 腐肉干: 恢复4饥饿值，3饱和度
    public static ItemStack RottenJerky(int amount) {
        ItemStack item = new ItemStack(ROTTEN_FLESH);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GRAY + "腐肉干");
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "右键食用",
                ChatColor.GOLD + "恢复4点饥饿值",
                ChatColor.YELLOW + "3.0饱和度"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, RottenJerkyKey, 1);
        item.setAmount(amount);
        return item;
    }

    // ========================================================================
    // 15种新食物 (PLAYER_HEAD基底)
    // ========================================================================

    // 纹理: 汉堡
    private static final String BURGER_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 热狗 (TODO: Replace with hotdog texture from minecraft-heads.com)
    private static final String HOTDOG_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDY2ZmJmY2NiYWVlZjgxZTY4NWU5NDEwNTBlYThmYmQyMzQxMWMzOGNlMDFkZjJkYWVlYzk0ZDQ1MjFlNzczZCJ9fX0=";
    // 汉堡: 恢复8饥饿值 + 6饱和度
    public static ItemStack Burger(int amount) {
        return createHeadItem("ArmsorPlus_Burger", BURGER_TEXTURE, ChatColor.GOLD + "汉堡",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复8点饥饿值",
                        ChatColor.YELLOW + "恢复6点饱和度",
                        ChatColor.GRAY + "香喷喷的牛肉汉堡"), BurgerKey, amount);
    }

    // 热狗: 恢复6饥饿值 + 5饱和度
    public static ItemStack HotDog(int amount) {
        return createHeadItem("ArmsorPlus_HotDog", HOTDOG_TEXTURE, ChatColor.RED + "热狗",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复6点饥饿值",
                        ChatColor.YELLOW + "恢复5点饱和度",
                        ChatColor.GRAY + "经典美式热狗"), HotDogKey, amount);
    }

    // 辣椒: 恢复3饥饿值 + 2饱和度
    public static ItemStack Chili(int amount) {
        return createHeadItem("ArmsorPlus_Chili", BURGER_TEXTURE, ChatColor.RED + "辣椒",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复3点饥饿值",
                        ChatColor.YELLOW + "恢复2点饱和度",
                        ChatColor.GRAY + "火辣辣的辣椒"), ChiliKey, amount);
    }

    // 洋葱: 恢复3饥饿值 + 2饱和度
    public static ItemStack Onion(int amount) {
        return createHeadItem("ArmsorPlus_Onion", BURGER_TEXTURE, ChatColor.LIGHT_PURPLE + "洋葱",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复3点饥饿值",
                        ChatColor.YELLOW + "恢复2点饱和度",
                        ChatColor.GRAY + "催人泪下的洋葱"), OnionKey, amount);
    }

    // 卷心菜: 恢复4饥饿值 + 3饱和度
    public static ItemStack Cabbage(int amount) {
        return createHeadItem("ArmsorPlus_Cabbage", BURGER_TEXTURE, ChatColor.GREEN + "卷心菜",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复4点饥饿值",
                        ChatColor.YELLOW + "恢复3点饱和度",
                        ChatColor.GRAY + "新鲜的卷心菜"), CabbageKey, amount);
    }

    // 大便: 恢复1饥饿值 + 1饱和度, 给予饥饿效果
    public static ItemStack Poop(int amount) {
        return createHeadItem("ArmsorPlus_Poop", BURGER_TEXTURE, ChatColor.DARK_GRAY + "大便",
                Arrays.asList(
                        ChatColor.GOLD + "右键食用",
                        ChatColor.GOLD + "恢复1点饥饿值",
                        ChatColor.YELLOW + "恢复1点饱和度",
                        ChatColor.RED + "食用后获得10秒饥饿",
                        ChatColor.GRAY + "……你真的要吃这个？"), PoopKey, amount);
    }

    // 金盾丹: 右击食用, 获得12s伤害吸收X (材质: 附魔金苹果)
    public static ItemStack GoldShieldElixir(int amount) {
        ItemStack item = new ItemStack(ENCHANTED_GOLDEN_APPLE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "金盾丹");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "获得12秒伤害吸收X",
                ChatColor.GRAY + "金色护盾之力"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, GoldShieldElixirKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 极寒冰核: 急冻树掉落物，用于合成寒冰弓 (材质: 海洋之心)
    public static ItemStack IceCore(int amount) {
        ItemStack item = new ItemStack(HEART_OF_THE_SEA);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "极寒冰核");
        meta.setLore(Arrays.asList(
                ChatColor.AQUA + "急冻树的核心",
                ChatColor.GRAY + "蕴含极寒之力，可用于合成寒冰弓"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, IceCoreKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 烈焰原核: 爆炎树掉落物，用于合成烈焰戟 (材质: 烈焰弹)
    public static ItemStack FireCore(int amount) {
        ItemStack item = new ItemStack(FIRE_CHARGE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "烈焰原核");
        meta.setLore(Arrays.asList(
                ChatColor.RED + "爆炎树的核心",
                ChatColor.GRAY + "蕴含烈焰之力，可用于合成烈焰戟"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, FireCoreKey, 1);
        item.setAmount(amount);
        return item;
    }

}
