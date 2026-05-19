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

/**
 * 所有食物/药品/树苗的定义与创建方法。
 * <p>
 * 每个方法接收数量参数(amount)和可选的等级参数(level/tier)。
 */
public class FoodItems {

    private static final String REJUVENATION_POWDER_NAME = ChatColor.LIGHT_PURPLE + "回春散";
    private static final String HEMOSTATIC_BANDAGE_NAME = ChatColor.RED + "止血绷带";
    private static final String COMPRESSED_BISCUIT_NAME = ChatColor.GOLD + "压缩饼干";

    /** 回春散: 生命恢复V 3s，有概率合成出高级品 */
    public static ItemStack RejuvenationPowder(int amount) {
        return createRejuvenationPowder(amount, 0);
    }

    /** 回春散: tier=1上品, tier=2极品, tier=3仙品 */
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

    /** 止血绷带 */
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

    /** 压缩饼干: 瞬间吃掉，等于9块面包 */
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

    /** 盐 */
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

    /** 肉干: 恢复6饥饿值，7.2饱和度 */
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

    /** 猪肉干: 恢复7饥饿值，8.0饱和度 */
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

    /** 羊肉干: 恢复5饥饿值，6.0饱和度 */
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

    /** 大苹果: 恢复4饥饿值 + 2饱和度 */
    public static ItemStack BigApple(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_BigApple".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", BIG_APPLE_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.RED + "大苹果");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复4点饥饿值",
                ChatColor.YELLOW + "恢复2点饱和度",
                ChatColor.GRAY + "传说中的大苹果"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, BigAppleKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 李子: 恢复3饥饿值 + 2饱和度 */
    public static ItemStack Plum(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Plum".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", PLUM_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "李子");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复3点饥饿值",
                ChatColor.YELLOW + "恢复2点饱和度",
                ChatColor.GRAY + "酸甜可口的李子"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, PlumKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 榛子: 恢复2饥饿值 + 3饱和度 */
    public static ItemStack Hazelnut(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Hazelnut".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", HAZELNUT_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.GOLD + "榛子");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复2点饥饿值",
                ChatColor.YELLOW + "恢复3点饱和度",
                ChatColor.GRAY + "香脆的榛子"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, HazelnutKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 椰子: 恢复5饥饿值 + 3饱和度 */
    public static ItemStack Coconut(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Coconut".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", COCONUT_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.WHITE + "椰子");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复5点饥饿值",
                ChatColor.YELLOW + "恢复3点饱和度",
                ChatColor.GRAY + "清凉解渴的椰子"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, CoconutKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 菠萝: 恢复6饥饿值 + 5饱和度 */
    public static ItemStack Pineapple(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Pineapple".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", PINEAPPLE_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.YELLOW + "菠萝");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复6点饥饿值",
                ChatColor.YELLOW + "恢复5点饱和度",
                ChatColor.GRAY + "多汁的热带菠萝"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, PineappleKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 草莓: 恢复2饥饿值 + 1.5饱和度 */
    public static ItemStack Strawberry(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Strawberry".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", STRAWBERRY_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.RED + "草莓");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复2点饥饿值",
                ChatColor.YELLOW + "恢复1.5点饱和度",
                ChatColor.GRAY + "鲜红的草莓"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, StrawberryKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 蓝莓: 恢复2饥饿值 + 1饱和度 */
    public static ItemStack Blueberry(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Blueberry".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", BLUEBERRY_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.DARK_PURPLE + "蓝莓");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复2点饥饿值",
                ChatColor.YELLOW + "恢复1点饱和度",
                ChatColor.GRAY + "小小的蓝莓"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, BlueberryKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 橙子: 恢复4饥饿值 + 3饱和度 */
    public static ItemStack Orange(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Orange".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", ORANGE_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.GOLD + "橙子");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复4点饥饿值",
                ChatColor.YELLOW + "恢复3点饱和度",
                ChatColor.GRAY + "新鲜的橙子"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, OrangeKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 橘子: 恢复3饥饿值 + 2饱和度 */
    public static ItemStack Tangerine(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Tangerine".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", TANGERINE_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.GOLD + "橘子");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复3点饥饿值",
                ChatColor.YELLOW + "恢复2点饱和度",
                ChatColor.GRAY + "甜甜的橘子"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, TangerineKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 冰块: 恢复1饥饿值 + 1饱和度 */
    public static ItemStack IceCube(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_IceCube".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", ICE_CUBE_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.AQUA + "冰块");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复1点饥饿值",
                ChatColor.YELLOW + "恢复1点饱和度",
                ChatColor.GRAY + "冰冰凉凉的冰块"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, IceCubeKey, 1);
        item.setAmount(amount);
        return item;
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
    private static final String LYCHEE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTA5YTJkYWM2M2Y4YWZlM2RmZDFhMDgyM2Y2ODQ2NDc1NDU1YTIzYTU2M2Q4MmQzYTNhMzRlNDIyMjQyY2JhNiJ9fX0=";
    private static final String CHERRY_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWU1M2EzYWVlYzk2NjJiZTUxM2M5NDA0OWE5ZWUwYWFkZTM1MTcwMTVhOTc2MzhkOWY4NmIyMWU4YjMxODJlYSJ9fX0=";
    private static final String PEACH_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmFkYmJhYjM4ODFhYWNiYTU3N2UyN2JiZWUxZmJlNGI5YTUwZTE5ZjVhODdmOGQ0OWI2MzYwNTRmYTE3ODhmYyJ9fX0=";

    /** 无花果: 恢复4饥饿值 + 3饱和度 */
    public static ItemStack Fig(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Fig".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", FIG_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "无花果");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复4点饥饿值",
                ChatColor.YELLOW + "恢复3点饱和度",
                ChatColor.GRAY + "甜美的无花果"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, FigKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 枣子: 恢复3饥饿值 + 2饱和度 */
    public static ItemStack Date(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Date".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", DATE_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.GOLD + "枣子");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复3点饥饿值",
                ChatColor.YELLOW + "恢复2点饱和度",
                ChatColor.GRAY + "甜甜的枣子"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, DateKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 柿子: 恢复4饥饿值 + 3饱和度 */
    public static ItemStack Persimmon(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Persimmon".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", PERSIMMON_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.GOLD + "柿子");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复4点饥饿值",
                ChatColor.YELLOW + "恢复3点饱和度",
                ChatColor.GRAY + "软糯的柿子"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, PersimmonKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 山竹: 恢复5饥饿值 + 4饱和度 */
    public static ItemStack Mangosteen(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Mangosteen".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", MANGOSTEEN_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.DARK_PURPLE + "山竹");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复5点饥饿值",
                ChatColor.YELLOW + "恢复4点饱和度",
                ChatColor.GRAY + "酸甜可口的山竹"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, MangosteenKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 圣女果: 恢复2饥饿值 + 1饱和度 */
    public static ItemStack CherryTomato(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_CherryTomato".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", CHERRY_TOMATO_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.RED + "圣女果");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复2点饥饿值",
                ChatColor.YELLOW + "恢复1点饱和度",
                ChatColor.GRAY + "小巧的圣女果"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, CherryTomatoKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 西红柿: 恢复3饥饿值 + 2.5饱和度 */
    public static ItemStack Tomato(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Tomato".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", TOMATO_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.RED + "西红柿");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复3点饥饿值",
                ChatColor.YELLOW + "恢复2.5点饱和度",
                ChatColor.GRAY + "新鲜的西红柿"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, TomatoKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 葡萄: 恢复3饥饿值 + 2饱和度 */
    public static ItemStack Grape(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Grape".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", GRAPE_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.DARK_PURPLE + "葡萄");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复3点饥饿值",
                ChatColor.YELLOW + "恢复2点饱和度",
                ChatColor.GRAY + "甜甜的葡萄"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, GrapeKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 石榴: 恢复5饥饿值 + 4饱和度 */
    public static ItemStack Pomegranate(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Pomegranate".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", POMEGRANATE_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.RED + "石榴");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复5点饥饿值",
                ChatColor.YELLOW + "恢复4点饱和度",
                ChatColor.GRAY + "多汁的石榴"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, PomegranateKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 栗子: 恢复3饥饿值 + 4饱和度 */
    public static ItemStack Chestnut(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Chestnut".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", CHESTNUT_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.GOLD + "栗子");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复3点饥饿值",
                ChatColor.YELLOW + "恢复4点饱和度",
                ChatColor.GRAY + "香糯的栗子"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, ChestnutKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 猕猴桃: 恢复4饥饿值 + 3饱和度 */
    public static ItemStack Kiwi(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Kiwi".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", KIWI_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.GREEN + "猕猴桃");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复4点饥饿值",
                ChatColor.YELLOW + "恢复3点饱和度",
                ChatColor.GRAY + "酸酸甜甜的猕猴桃"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, KiwiKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 龙眼: 恢复3饥饿值 + 2饱和度 */
    public static ItemStack Longan(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Longan".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", LONGAN_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.YELLOW + "龙眼");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复3点饥饿值",
                ChatColor.YELLOW + "恢复2点饱和度",
                ChatColor.GRAY + "甜美的龙眼"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, LonganKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 荔枝: 恢复4饥饿值 + 3饱和度 */
    public static ItemStack Lychee(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Lychee".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", LYCHEE_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.RED + "荔枝");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复4点饥饿值",
                ChatColor.YELLOW + "恢复3点饱和度",
                ChatColor.GRAY + "多汁的荔枝"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, LycheeKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 樱桃: 恢复2饥饿值 + 1.5饱和度 */
    public static ItemStack Cherry(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Cherry".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", CHERRY_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.RED + "樱桃");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复2点饥饿值",
                ChatColor.YELLOW + "恢复1.5点饱和度",
                ChatColor.GRAY + "鲜红的樱桃"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, CherryKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 桃子: 恢复5饥饿值 + 4饱和度 */
    public static ItemStack Peach(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Peach".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", PEACH_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "桃子");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复5点饥饿值",
                ChatColor.YELLOW + "恢复4点饱和度",
                ChatColor.GRAY + "多汁的桃子"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, PeachKey, 1);
        item.setAmount(amount);
        return item;
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

    /** 甜浆果派: 恢复9饥饿值，8饱和度 */
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

    /** 酒桶: 放置后打开有9瓶酒 */
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

    /** 酒 (tier: 1=酒, 2=佳酿, 3=金樽清酒) */
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

    /** 腐肉干: 恢复4饥饿值，3饱和度 */
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
    // 纹理: 披萨 (TODO: Replace with pizza texture)
    private static final String PIZZA_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 薯条 (TODO: Replace with fries texture)
    private static final String FRIES_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 甜甜圈 (TODO: Replace with donut texture)
    private static final String DONUT_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 冰淇淋 (TODO: Replace with ice cream texture)
    private static final String ICECREAM_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 爆米花 (TODO: Replace with popcorn texture)
    private static final String POPCORN_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 棉花糖 (TODO: Replace with cotton candy texture)
    private static final String COTTON_CANDY_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 巧克力 (TODO: Replace with chocolate texture)
    private static final String CHOCOLATE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 寿司 (TODO: Replace with sushi texture)
    private static final String SUSHI_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 拉面 (TODO: Replace with ramen texture)
    private static final String RAMEN_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 三明治 (TODO: Replace with sandwich texture)
    private static final String SANDWICH_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 鸡腿 (TODO: Replace with drumstick texture)
    private static final String DRUMSTICK_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 奶酪 (TODO: Replace with cheese texture)
    private static final String CHEESE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 薄饼 (TODO: Replace with pancake texture)
    private static final String PANCAKE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 辣椒 (TODO: Replace with chili texture)
    private static final String CHILI_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 洋葱 (TODO: Replace with onion texture)
    private static final String ONION_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 卷心菜 (TODO: Replace with cabbage texture)
    private static final String CABBAGE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 黄油 (TODO: Replace with butter texture)
    private static final String BUTTER_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 大便 (TODO: Replace with poop texture)
    private static final String POOP_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 金盾丹 (TODO: Replace with gold shield elixir texture)
    private static final String GOLD_SHIELD_ELIXIR_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 极寒冰核 (TODO: Replace)
    private static final String ICE_CORE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";
    // 纹理: 烈焰原核 (TODO: Replace)
    private static final String FIRE_CORE_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU0ODAzOWUxOTZkMDNjZWZmNjJmZTk3Njg0ZTcxMmY4ZDMxYjZlN2IxYTZjYjFjOTU1YTg2NzU1Yjg0N2IxNyJ9fX0=";

    /** 汉堡: 恢复8饥饿值 + 6饱和度 */
    public static ItemStack Burger(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Burger".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", BURGER_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.GOLD + "汉堡");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复8点饥饿值",
                ChatColor.YELLOW + "恢复6点饱和度",
                ChatColor.GRAY + "香喷喷的牛肉汉堡"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, BurgerKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 热狗: 恢复6饥饿值 + 5饱和度 */
    public static ItemStack HotDog(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_HotDog".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", HOTDOG_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.RED + "热狗");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复6点饥饿值",
                ChatColor.YELLOW + "恢复5点饱和度",
                ChatColor.GRAY + "经典美式热狗"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, HotDogKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 披萨: 恢复9饥饿值 + 7饱和度 */
    public static ItemStack Pizza(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Pizza".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", PIZZA_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.YELLOW + "披萨");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复9点饥饿值",
                ChatColor.YELLOW + "恢复7点饱和度",
                ChatColor.GRAY + "芝士满满的披萨"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, PizzaKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 薯条: 恢复4饥饿值 + 3饱和度 */
    public static ItemStack FrenchFries(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_FrenchFries".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", FRIES_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.YELLOW + "薯条");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复4点饥饿值",
                ChatColor.YELLOW + "恢复3点饱和度",
                ChatColor.GRAY + "金黄酥脆的薯条"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, FrenchFriesKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 甜甜圈: 恢复5饥饿值 + 4饱和度 */
    public static ItemStack Donut(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Donut".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", DONUT_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "甜甜圈");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复5点饥饿值",
                ChatColor.YELLOW + "恢复4点饱和度",
                ChatColor.GRAY + "撒满糖霜的甜甜圈"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, DonutKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 冰淇淋: 恢复3饥饿值 + 2饱和度 */
    public static ItemStack IceCream(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_IceCream".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", ICECREAM_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.AQUA + "冰淇淋");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复3点饥饿值",
                ChatColor.YELLOW + "恢复2点饱和度",
                ChatColor.GRAY + "冰凉甜美的冰淇淋"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, IceCreamKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 爆米花: 恢复4饥饿值 + 2饱和度 */
    public static ItemStack Popcorn(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Popcorn".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", POPCORN_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.GOLD + "爆米花");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复4点饥饿值",
                ChatColor.YELLOW + "恢复2点饱和度",
                ChatColor.GRAY + "香脆可口的爆米花"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, PopcornKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 棉花糖: 恢复3饥饿值 + 4饱和度 */
    public static ItemStack CottonCandy(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_CottonCandy".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", COTTON_CANDY_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "棉花糖");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复3点饥饿值",
                ChatColor.YELLOW + "恢复4点饱和度",
                ChatColor.GRAY + "松软甜蜜的棉花糖"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, CottonCandyKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 巧克力: 恢复5饥饿值 + 5饱和度 */
    public static ItemStack Chocolate(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Chocolate".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", CHOCOLATE_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.DARK_RED + "巧克力");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复5点饥饿值",
                ChatColor.YELLOW + "恢复5点饱和度",
                ChatColor.GRAY + "香浓丝滑的巧克力"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, ChocolateKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 寿司: 恢复5饥饿值 + 4饱和度 */
    public static ItemStack Sushi(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Sushi".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", SUSHI_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.DARK_GREEN + "寿司");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复5点饥饿值",
                ChatColor.YELLOW + "恢复4点饱和度",
                ChatColor.GRAY + "新鲜美味的寿司"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SushiKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 拉面: 恢复8饥饿值 + 6饱和度 */
    public static ItemStack Ramen(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Ramen".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", RAMEN_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.GOLD + "拉面");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复8点饥饿值",
                ChatColor.YELLOW + "恢复6点饱和度",
                ChatColor.GRAY + "热气腾腾的拉面"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, RamenKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 三明治: 恢复7饥饿值 + 5饱和度 */
    public static ItemStack Sandwich(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Sandwich".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", SANDWICH_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.GOLD + "三明治");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复7点饥饿值",
                ChatColor.YELLOW + "恢复5点饱和度",
                ChatColor.GRAY + "用料丰富的三明治"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SandwichKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 鸡腿: 恢复6饥饿值 + 4饱和度 */
    public static ItemStack Drumstick(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Drumstick".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", DRUMSTICK_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.GOLD + "鸡腿");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复6点饥饿值",
                ChatColor.YELLOW + "恢复4点饱和度",
                ChatColor.GRAY + "香喷喷的烤鸡腿"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, DrumstickKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 奶酪: 恢复4饥饿值 + 5饱和度 */
    public static ItemStack Cheese(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Cheese".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", CHEESE_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.YELLOW + "奶酪");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复4点饥饿值",
                ChatColor.YELLOW + "恢复5点饱和度",
                ChatColor.GRAY + "浓郁醇厚的奶酪"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, CheeseKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 薄饼: 恢复6饥饿值 + 5饱和度 */
    public static ItemStack Pancake(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Pancake".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", PANCAKE_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.GOLD + "薄饼");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复6点饥饿值",
                ChatColor.YELLOW + "恢复5点饱和度",
                ChatColor.GRAY + "淋着糖浆的薄饼"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, PancakeKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 辣椒: 恢复3饥饿值 + 2饱和度 */
    public static ItemStack Chili(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Chili".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", CHILI_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.RED + "辣椒");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复3点饥饿值",
                ChatColor.YELLOW + "恢复2点饱和度",
                ChatColor.GRAY + "火辣辣的辣椒"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, ChiliKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 洋葱: 恢复3饥饿值 + 2饱和度 */
    public static ItemStack Onion(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Onion".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", ONION_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "洋葱");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复3点饥饿值",
                ChatColor.YELLOW + "恢复2点饱和度",
                ChatColor.GRAY + "催人泪下的洋葱"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, OnionKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 卷心菜: 恢复4饥饿值 + 3饱和度 */
    public static ItemStack Cabbage(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Cabbage".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", CABBAGE_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.GREEN + "卷心菜");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复4点饥饿值",
                ChatColor.YELLOW + "恢复3点饱和度",
                ChatColor.GRAY + "新鲜的卷心菜"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, CabbageKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 黄油: 恢复2饥饿值 + 3饱和度 */
    public static ItemStack Butter(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Butter".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", BUTTER_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.YELLOW + "黄油");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复2点饥饿值",
                ChatColor.YELLOW + "恢复3点饱和度",
                ChatColor.GRAY + "香浓的黄油"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, ButterKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 大便: 恢复1饥饿值 + 1饱和度, 给予饥饿效果 */
    public static ItemStack Poop(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_Poop".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", POOP_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.DARK_GRAY + "大便");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键食用",
                ChatColor.GOLD + "恢复1点饥饿值",
                ChatColor.YELLOW + "恢复1点饱和度",
                ChatColor.GRAY + "……你真的要吃这个？"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, PoopKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 金盾丹: 右击食用, 获得12s伤害吸收X */
    public static ItemStack GoldShieldElixir(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_GoldShieldElixir".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", GOLD_SHIELD_ELIXIR_TEXTURE));
        meta.setPlayerProfile(profile);
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

    /** 极寒冰核: 急冻树掉落物，用于合成寒冰弓 */
    public static ItemStack IceCore(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_IceCore".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", ICE_CORE_TEXTURE));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.AQUA + "极寒冰核");
        meta.setLore(Arrays.asList(
                ChatColor.AQUA + "急冻树的核心",
                ChatColor.GRAY + "蕴含极寒之力，可用于合成寒冰弓"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, IceCoreKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 烈焰原核: 爆炎树掉落物，用于合成烈焰戟 */
    public static ItemStack FireCore(int amount) {
        ItemStack item = new ItemStack(PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes("ArmsorPlus_FireCore".getBytes()), null);
        profile.setProperty(new ProfileProperty("textures", FIRE_CORE_TEXTURE));
        meta.setPlayerProfile(profile);
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
