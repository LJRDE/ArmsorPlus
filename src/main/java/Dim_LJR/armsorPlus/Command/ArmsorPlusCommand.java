package Dim_LJR.armsorPlus.Command;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorPlusEnchantEventHandler;
import Dim_LJR.armsorPlus.ArmsorPlusMenu;
import Dim_LJR.armsorPlus.Boss.PlayerBoss;
import Dim_LJR.armsorPlus.Boss.VillageCaptain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.Food.FoodItems.*;
import static Dim_LJR.armsorPlus.Item.Materials.MoonShard;
import static Dim_LJR.armsorPlus.OpenSea.LoadOpenSea.reloadmap;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

public class ArmsorPlusCommand implements CommandExecutor, TabCompleter {

    private final List<String> args0 = List.of(
            "info", "give", "guide", "spawn", "bossremove", "getEnchantmentLevel", "removeEnchant", "getBloodCount", "reloadmap", "help"
    );
    private final List<String> args1_give = List.of(
            "Arms_I", "Arms_II", "Armor_I", "Armor_II", "Bow_I",
            "DiamondPlus", "MagicBal_I", "MagicBal_II", "MagicBal_III", "MagicBal_IV",
            "Blood_Sword", "Iron_Epee", "StarTraceSword", "BlackTortoiseSword", "BlazingSun", "PeachWoodSword",
            "BasicStone", "GuideBook",
            "Dodge_EnchantedBook", "Famine_EnchantedBook",
            "Ripples_EnchantedBook", "BloodSacrifice_EnchantedBook",
            "Freeze_EnchantedBook", "EffectClear_EnchantedBook",
            "Sniping_EnchantedBook", "DoubleHit_EnchantedBook",
            "Blocking_EnchantedBook", "Withering_EnchantedBook",
            "Survivor_EnchantedBook", "HealthBoost_EnchantedBook",
            "Revenge_EnchantedBook", "ExplosiveArrow_EnchantedBook",
            "ShadowDodge_EnchantedBook", "ArrowSpeed_EnchantedBook",
            "Feeding_EnchantedBook",
            "Dagger", "ThrowingAxe", "SkeletonScepter", "FrostBow", "FlameHalberd",
            "QuickThrust_EnchantedBook", "DiamondDrill_EnchantedBook",
            "Blindness_EnchantedBook", "Indestructible_EnchantedBook",
            "RainSword", "FlyingSword", "FlashStepBlade", "MagicStick",
            "IceSword", "WebBow", "ExplosionBow",
            "ProtectionPRO_EnchantedBook", "Stun_EnchantedBook",
            "GolemGuardian_EnchantedBook", "CriticalStrike_EnchantedBook",
            "Piercing_EnchantedBook", "LavaWalker_EnchantedBook",
            "LightningCall_EnchantedBook", "Holographic_EnchantedBook",
            "Tracking_EnchantedBook", "Harvest_EnchantedBook",
            "AutoPlant_EnchantedBook", "StrongBurst_EnchantedBook",
            "MultiShot_EnchantedBook",
            "Poison_EnchantedBook", "SharpBlade_EnchantedBook",
            "ThunderclapArrow_EnchantedBook",
            "DamageDispersal_EnchantedBook",
            "HerbGuard_EnchantedBook",
            "Pierce_EnchantedBook",
            "FireBlade_EnchantedBook", "FrostBlade_EnchantedBook",
            "ThunderBlade_EnchantedBook", "MagicBlade_EnchantedBook",
            "IceSpike_EnchantedBook", "Inferno_EnchantedBook",
            "HeavyArmor_EnchantedBook", "EarthFavor_EnchantedBook",
            "Ambush_EnchantedBook",
            "ThunderGlow", "BlazingSun",
            "FishBoneSword", "FishBoneKnife", "FishSpineSword", "FishSpineKnife",
            "SeaBoneSword", "SeaBoneKnife", "SpiritBoneSword", "SpiritBoneKnife",
            "SeaSpineSword", "SeaSpineKnife", "CorrodeBoneSword",
            "SpiritSpineSword", "SpiritSpineKnife", "SeaCrySword", "SeaCryKnife",
            "IllusionBlade", "IllusionStaff", "CloudMoonBlade", "MoonShard", "RagingPlundererCrossbow",
            "GoldShieldElixir",
            "Salt", "Jerky", "PorkJerky", "MuttonJerky", "SweetBerryPie", "WineBarrel", "Wine", "GoldWine", "RottenJerky",
            "RejuvenationPowder", "HemostaticBandage", "CompressedBiscuit"
    );
    private final List<String> args1_enchant = List.of(
            "Dodge", "Famine", "Ripples", "BloodSacrifice", "EffectClear",
            "Freeze", "ShadowDodge", "Blocking", "Withering", "Survivor",
            "HealthBoost", "Revenge", "ExplosiveArrow", "Sniping",
            "ArrowSpeed", "DoubleHit", "Feeding", "QuickThrust",
            "DiamondDrill", "Blindness", "Indestructible",
            "ProtectionPRO", "Stun", "GolemGuardian", "CriticalStrike",
            "Piercing", "LavaWalker", "LightningCall", "Holographic",
            "Tracking", "Harvest", "AutoPlant", "StrongBurst", "MultiShot",
            "Poison", "SharpBlade", "ThunderclapArrow", "DamageDispersal",
            "HerbGuard", "Pierce", "FireBlade", "FrostBlade", "ThunderBlade", "MagicBlade",
            "IceSpike", "Inferno", "HeavyArmor", "EarthFavor", "Ambush"
    );

    // 附魔名 -> 附魔键 映射表 (供 getEnchantmentLevel / removeEnchant 共用, 消除重复switch)
    private static final Map<String, NamespacedKey> ENCHANT_KEY_MAP = createEnchantMap();

    private static Map<String, NamespacedKey> createEnchantMap() {
        Map<String, NamespacedKey> map = new LinkedHashMap<>();
        map.put("Dodge", Dodgekey);
        map.put("Famine", Faminekey);
        map.put("Ripples", RipplesProtectkey);
        map.put("BloodSacrifice", BloodSacrificekey);
        map.put("EffectClear", EffectClear);
        map.put("Freeze", FreezeKey);
        map.put("ShadowDodge", ShadowDodge);
        map.put("Blocking", BlockingKey);
        map.put("Withering", WitheringKey);
        map.put("Survivor", SurvivorKey);
        map.put("HealthBoost", HealthBoostKey);
        map.put("Revenge", RevengeKey);
        map.put("ExplosiveArrow", ExplosiveArrowKey);
        map.put("Sniping", Sniping);
        map.put("ArrowSpeed", ArrowSpeed);
        map.put("DoubleHit", DoubleHitkey);
        map.put("Feeding", Feedingkey);
        map.put("QuickThrust", QuickThrustKey);
        map.put("DiamondDrill", DiamondDrillKey);
        map.put("Blindness", BlindnessKey);
        map.put("Indestructible", IndestructibleKey);
        map.put("Poison", PoisonKey);
        map.put("SharpBlade", SharpBladeKey);
        map.put("ThunderclapArrow", ThunderclapArrowKey);
        map.put("DamageDispersal", DamageDispersalKey);
        map.put("HerbGuard", HerbGuardKey);
        map.put("Pierce", PierceKey);
        map.put("FireBlade", FireBladeKey);
        map.put("FrostBlade", FrostBladeKey);
        map.put("ThunderBlade", ThunderBladeKey);
        map.put("MagicBlade", MagicBladeKey);
        map.put("IceSpike", IceSpikeKey);
        map.put("Inferno", InfernoKey);
        map.put("HeavyArmor", HeavyArmorKey);
        map.put("EarthFavor", EarthFavorKey);
        map.put("Ambush", AmbushKey);
        return map;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("ArmsorPlus"))
            return false;
        if (!sender.hasPermission("ArmsorPlus.op")) {
            sender.sendMessage(ChatColor.GRAY + "你没有权限使用该指令");
            return true;
        }
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        switch (args[0]) {
            case "info" -> handleInfo(sender);
            case "give" -> handleGive(sender, args);
            case "guide" -> handleGuide(sender);
            case "spawn" -> handleSpawn(sender, args);
            case "bossremove" -> handleBossRemove(sender);
            case "reloadmap" -> handleReloadMap(sender);
            case "getEnchantmentLevel" -> handleGetEnchantLevel(sender, args);
            case "removeEnchant" -> handleRemoveEnchant(sender, args);
            case "getBloodCount" -> handleGetBloodCount(sender);
            case "help" -> sendHelp(sender);
            default -> sender.sendMessage(ChatColor.RED + "未知指令，请输入 /ArmsorPlus help 查看帮助");
        }
        return true;
    }

    // ====== 指令处理器 ======

    private void handleInfo(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "嘿嘿，这个是Dim_LJR写的插件哦");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "虽然说不知道为什么BUG越改越多就是了");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "如果你不知道这么用强化石的话，可以把它拖动到你的装备上单击鼠标左键试试呢");
        sender.sendMessage(ChatColor.GRAY + "输入 /ArmsorPlus help 查看指令帮助");
    }

    private void handleGuide(@NotNull CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "该指令必须由玩家执行");
            return;
        }
        player.openInventory(new ArmsorPlusMenu().createMenu());
    }

    private void handleGive(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /ArmsorPlus give <物品名> [数量] [等级]");
            return;
        }
        // 需要玩家才能给物品
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "该指令必须由玩家执行");
            return;
        }
        switch (args[1]) {
            // ===== 强化石 =====
            case "Arms_I" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(ArmsPlusCreateI(amount));
                sender.sendMessage("已获得 " + amount + " 个一级武器强化石");
            }
            case "Arms_II" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(ArmsPlusCreateII(amount));
                sender.sendMessage("已获得 " + amount + " 个二级武器强化石");
            }
            case "Armor_I" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(ArmorPlusCreate(amount));
                sender.sendMessage("已获得 " + amount + " 个一级护甲强化石");
            }
            case "Armor_II" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(ArmorPlusCreateII(amount));
                sender.sendMessage("已获得 " + amount + " 个二级护甲强化石");
            }
            case "Bow_I" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(BowPlusCreate(amount));
                sender.sendMessage("已获得 " + amount + " 个弓箭强化石");
            }
            case "DiamondPlus" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(DIAMONDPLUSCreate(amount));
                sender.sendMessage("已获得 " + amount + " 个精炼金刚石");
            }
            case "BasicStone" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(BasicStone(amount));
                sender.sendMessage("已获得 " + amount + " 个基础强化石");
            }
            case "GuideBook" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(GuideBook(amount));
                sender.sendMessage("已获得 " + amount + " 本高级附魔向导");
            }
            // ===== 魔法球 =====
            case "MagicBal_I" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(MagicBallCreateI(amount));
                sender.sendMessage("已获得 " + amount + " 个寻常的魔法球");
            }
            case "MagicBal_II" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(MagicBallCreateII(amount));
                sender.sendMessage("已获得 " + amount + " 个稀罕的魔法球");
            }
            case "MagicBal_III" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(MagicBallCreateIII(amount));
                sender.sendMessage("已获得 " + amount + " 个史诗的魔法球");
            }
            case "MagicBal_IV" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(MagicBallCreateIV(amount));
                sender.sendMessage("已获得 " + amount + " 个传奇的魔法球");
            }
            // ===== 武器 =====
            case "Blood_Sword" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(BloodSword(amount));
                sender.sendMessage(ChatColor.RED + "已获得 " + amount + " 把血祭之剑");
            }
            case "Iron_Epee" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(Iron_Epee(amount));
                sender.sendMessage("已获得 " + amount + " 把重剑");
            }
            case "StarTraceSword" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(StarTraceSword(amount));
                sender.sendMessage(ChatColor.DARK_AQUA + "已获得 " + amount + " 把星痕剑");
            }
            case "BlackTortoiseSword" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(BlackTortoiseSword(amount));
                sender.sendMessage(ChatColor.DARK_GREEN + "已获得 " + amount + " 把玄武剑");
            }
            // ===== 附魔书（需要数量 + 等级）=====
            case "Sniping_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Sniping_EnchantdeBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.LIGHT_PURPLE + "狙击" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "EffectClear_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(EffectClear_EnchantdeBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.WHITE + "涤魂" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Freeze_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Freeze_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.AQUA + "寒冻" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "BloodSacrifice_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(BloodSacrifice_EnchantdeBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.RED + "血祭" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Ripples_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Ripples_EnchantdeBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.BLUE + "涟漪" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Famine_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Famine_EnchantdeBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.GREEN + "饥荒" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Dodge_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Dodge_EnchantdeBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.GOLD + "闪避" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "DoubleHit_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(DoubleHit_EnchantdeBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.LIGHT_PURPLE + "双重打击" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Feeding_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Feeding_EnchantdeBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.RED + "吸血" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "ArrowSpeed_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(ArrowSpeed_EnchantdeBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.GOLD + "弹道" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "ShadowDodge_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(ShadowDodge_EnchantdeBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.DARK_PURPLE + "影避" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "ExplosiveArrow_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(ExplosiveArrow_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.YELLOW + "蓄爆" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "HealthBoost_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(HealthBoost_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.RED + "生命提升" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Revenge_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Revenge_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.DARK_RED + "复仇" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Blocking_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Blocking_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.GOLD + "格挡" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Withering_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Withering_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.BLACK + "凋零" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            // ===== 新武器 =====
            case "Dagger" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(Dagger(amount));
                sender.sendMessage("已获得 " + amount + " 把匕首");
            }
            case "ThrowingAxe" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(ThrowingAxe(amount));
                sender.sendMessage("已获得 " + amount + " 把飞斧");
            }
            case "SkeletonScepter" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(SkeletonScepter(amount));
                sender.sendMessage("已获得 " + amount + " 个骷髅权杖");
            }
            case "FrostBow" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(FrostBow(amount));
                sender.sendMessage("已获得 " + amount + " 把寒冰弓");
            }
            case "FlameHalberd" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(FlameHalberd(amount));
                sender.sendMessage("已获得 " + amount + " 把火焰戟");
            }
            case "QuickThrust_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(QuickThrust_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.GOLD + "疾刺" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            // ===== 食物/药品 =====
            case "RejuvenationPowder" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                int tier = (args.length >= 4 && IsInt(args[3])) ? Integer.parseInt(args[3]) : 0;
                player.getInventory().addItem(RejuvenationPowder(amount, tier));
                sender.sendMessage("已获得 " + amount + " 个回春散 (品级:" + tier + ")");
            }
            case "HemostaticBandage" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(HemostaticBandage(amount));
                sender.sendMessage("已获得 " + amount + " 个止血绷带");
            }
            case "CompressedBiscuit" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(CompressedBiscuit(amount));
                sender.sendMessage("已获得 " + amount + " 个压缩饼干");
            }
            case "Survivor_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Survivor_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.GOLD + "幸存" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            // ===== 新附魔书 =====
            case "DiamondDrill_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(DiamondDrill_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.AQUA + "金刚钻" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Blindness_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Blindness_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.DARK_GRAY + "失明" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Indestructible_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Indestructible_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.GOLD + "不灭" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            // ===== 新武器 =====
            case "RainSword" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(RainSword(amount));
                sender.sendMessage("已获得 " + amount + " 把雨御前");
            }
            case "FlyingSword" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(FlyingSword(amount));
                sender.sendMessage("已获得 " + amount + " 把飞天御剑");
            }
            case "FlashStepBlade" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(FlashStepBlade(amount));
                sender.sendMessage("已获得 " + amount + " 把瞬步刃");
            }
            case "MagicStick" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(MagicStick(amount));
                sender.sendMessage("已获得 " + amount + " 个法杖");
            }
            // ===== 新食物/物品 =====
            case "Salt" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(Salt(amount));
                sender.sendMessage("已获得 " + amount + " 个盐");
            }
            case "Jerky" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(Jerky(amount));
                sender.sendMessage("已获得 " + amount + " 个肉干");
            }
            case "PorkJerky" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(PorkJerky(amount));
                sender.sendMessage("已获得 " + amount + " 个猪肉干");
            }
            case "MuttonJerky" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(MuttonJerky(amount));
                sender.sendMessage("已获得 " + amount + " 个羊肉干");
            }
            case "SweetBerryPie" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(SweetBerryPie(amount));
                sender.sendMessage("已获得 " + amount + " 个甜浆果派");
            }
            case "WineBarrel" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(WineBarrel(amount));
                sender.sendMessage("已获得 " + amount + " 个酒桶");
            }
            case "Wine" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                int tier = (args.length >= 4 && IsInt(args[3])) ? Integer.parseInt(args[3]) : 1;
                player.getInventory().addItem(Wine(amount, tier));
                sender.sendMessage("已获得 " + amount + " 瓶酒 (品级:" + tier + ")");
            }
            case "GoldWine" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(Wine(amount, 3));
                sender.sendMessage("已获得 " + amount + " 瓶金樽清酒 (力量V 140秒, 死亡可复活一次)");
            }
            case "RottenJerky" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(RottenJerky(amount));
                sender.sendMessage("已获得 " + amount + " 个腐肉干");
            }
            // ===== 0.3I 新武器 =====
            case "IceSword" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(IceSword(amount));
                sender.sendMessage("已获得 " + amount + " 把寒冰剑");
            }
            case "WebBow" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(WebBow(amount));
                sender.sendMessage("已获得 " + amount + " 把盘丝弓");
            }
            case "ExplosionBow" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(ExplosionBow(amount));
                sender.sendMessage("已获得 " + amount + " 把爆炸弓");
            }
            // ===== 0.3I 新附魔书 =====
            case "ProtectionPRO_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(ProtectionPRO_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.GOLD + "保护PRO" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Stun_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Stun_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.DARK_GREEN + "眩晕" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "GolemGuardian_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(GolemGuardian_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.GRAY + "傀儡守护者" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "CriticalStrike_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(CriticalStrike_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.RED + "暴击" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Piercing_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Piercing_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.DARK_RED + "穿甲" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "LavaWalker_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(LavaWalker_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.GOLD + "熔岩行者" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "LightningCall_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(LightningCall_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.YELLOW + "唤雷" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Holographic_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Holographic_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.AQUA + "全息" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Tracking_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Tracking_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.GREEN + "追踪" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Harvest_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Harvest_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.GOLD + "丰收" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "AutoPlant_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(AutoPlant_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.GREEN + "自动种植" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "StrongBurst_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(StrongBurst_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.DARK_PURPLE + "强风暴" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "MultiShot_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(MultiShot_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.LIGHT_PURPLE + "千重射击" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Poison_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Poison_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.DARK_GREEN + "剧毒" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "SharpBlade_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(SharpBlade_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.DARK_AQUA + "利刃" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "ThunderclapArrow_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(ThunderclapArrow_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.YELLOW + "惊雷" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "DamageDispersal_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(DamageDispersal_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.DARK_GREEN + "卸力" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "HerbGuard_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(HerbGuard_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.GREEN + "百草" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Pierce_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Pierce_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.DARK_PURPLE + "贯穿" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "FireBlade_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(FireBlade_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.RED + "火印" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "FrostBlade_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(FrostBlade_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.AQUA + "霜印" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "ThunderBlade_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(ThunderBlade_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.YELLOW + "雷印" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "MagicBlade_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(MagicBlade_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.DARK_PURPLE + "魔印" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "IceSpike_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(IceSpike_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.AQUA + "冰刺" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "Inferno_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Inferno_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.RED + "烈焰" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "HeavyArmor_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(HeavyArmor_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.DARK_GRAY + "重甲" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "EarthFavor_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(EarthFavor_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.DARK_GREEN + "地之眷顾" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "ThunderGlow" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(ThunderGlow(amount));
                sender.sendMessage(ChatColor.YELLOW + "已获得 " + amount + " 把雷光");
            }
            case "BlazingSun" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(BlazingSun(amount));
                sender.sendMessage(ChatColor.GOLD + "已获得 " + amount + " 把烈阳");
            }
            case "PeachWoodSword" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(PeachWoodSword(amount));
                sender.sendMessage(ChatColor.GREEN + "已获得 " + amount + " 把桃木剑");
            }
            case "Ambush_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Ambush_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.DARK_RED + "伏击" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            case "GoldShieldElixir" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(GoldShieldElixir(amount));
                sender.sendMessage(ChatColor.GOLD + "已获得 " + amount + " 个金盾丹");
            }
            // ===== 鱼骨系列武器 (0.3I+) =====
            case "FishBoneSword" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(FishBoneSword(amount));
                sender.sendMessage(ChatColor.YELLOW + "已获得 " + amount + " 把鱼骨剑");
            }
            case "FishBoneKnife" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(FishBoneKnife(amount));
                sender.sendMessage(ChatColor.YELLOW + "已获得 " + amount + " 把鱼骨刀");
            }
            case "FishSpineSword" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(FishSpineSword(amount));
                sender.sendMessage(ChatColor.YELLOW + "已获得 " + amount + " 把鱼刺剑");
            }
            case "FishSpineKnife" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(FishSpineKnife(amount));
                sender.sendMessage(ChatColor.YELLOW + "已获得 " + amount + " 把鱼刺刀");
            }
            case "SeaBoneSword" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(SeaBoneSword(amount));
                sender.sendMessage(ChatColor.AQUA + "已获得 " + amount + " 把海骨剑");
            }
            case "SeaBoneKnife" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(SeaBoneKnife(amount));
                sender.sendMessage(ChatColor.AQUA + "已获得 " + amount + " 把海骨刀");
            }
            case "SpiritBoneSword" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(SpiritBoneSword(amount));
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "已获得 " + amount + " 把灵骨剑");
            }
            case "SpiritBoneKnife" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(SpiritBoneKnife(amount));
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "已获得 " + amount + " 把灵骨刀");
            }
            case "SeaSpineSword" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(SeaSpineSword(amount));
                sender.sendMessage(ChatColor.DARK_AQUA + "已获得 " + amount + " 把海刺剑");
            }
            case "SeaSpineKnife" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(SeaSpineKnife(amount));
                sender.sendMessage(ChatColor.DARK_AQUA + "已获得 " + amount + " 把海刺刀");
            }
            case "CorrodeBoneSword" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(CorrodeBoneSword(amount));
                sender.sendMessage(ChatColor.DARK_PURPLE + "已获得 " + amount + " 把蚀骨剑");
            }
            case "SpiritSpineSword" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(SpiritSpineSword(amount));
                sender.sendMessage(ChatColor.DARK_PURPLE + "已获得 " + amount + " 把灵刺剑");
            }
            case "SpiritSpineKnife" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(SpiritSpineKnife(amount));
                sender.sendMessage(ChatColor.DARK_PURPLE + "已获得 " + amount + " 把灵刺刀");
            }
            case "SeaCrySword" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(SeaCrySword(amount));
                sender.sendMessage(ChatColor.DARK_BLUE + "已获得 " + amount + " 把海哭剑");
            }
            case "SeaCryKnife" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(SeaCryKnife(amount));
                sender.sendMessage(ChatColor.DARK_BLUE + "已获得 " + amount + " 把海哭刀");
            }
            case "IllusionBlade" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(IllusionBlade(amount));
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "已获得 " + amount + " 把幻影之刃");
            }
            case "IllusionStaff" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(IllusionStaff(amount));
                sender.sendMessage(ChatColor.AQUA + "已获得 " + amount + " 把幻惑法杖");
            }
            case "CloudMoonBlade" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(CloudMoonBlade(amount));
                sender.sendMessage(ChatColor.WHITE + "已获得 " + amount + " 把吞云斩月刀");
            }
            case "MoonShard" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(MoonShard(amount));
                sender.sendMessage(ChatColor.AQUA + "已获得 " + amount + " 个月之碎片");
            }
            case "RagingPlundererCrossbow" -> {
                int amount = (args.length >= 3 && IsInt(args[2])) ? Integer.parseInt(args[2]) : 1;
                player.getInventory().addItem(RagingPlundererCrossbow(amount));
                sender.sendMessage(ChatColor.RED + "已获得 " + amount + " 把狂怒掠夺者之弩");
            }
            default -> sender.sendMessage(ChatColor.RED + "未知物品: " + args[1] + "，请输入 /ArmsorPlus help 查看可用物品");
        }
    }

    private void handleSpawn(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "该指令必须由玩家执行");
            return;
        }
        // /ArmsorPlus spawn 或 /ArmsorPlus spawn shadow —— 召唤影武者 (默认用召唤者皮肤)
        if (args.length == 1 || (args.length >= 2 && args[1].equalsIgnoreCase("shadow"))) {
            if (args.length >= 3) {
                Player skinSource = Bukkit.getPlayer(args[2]);
                if (skinSource == null) {
                    sender.sendMessage(ChatColor.RED + "找不到在线玩家: " + args[2]);
                    return;
                }
                PlayerBoss.spawnBoss(player, skinSource);
            } else {
                PlayerBoss.spawnBoss(player);
            }
            return;
        }
        // /ArmsorPlus spawn captain —— 召唤村民队长 (简单近战Boss, 使用新皮肤)
        if (args.length >= 2 && args[1].equalsIgnoreCase("captain")) {
            VillageCaptain.spawnBoss(player);
            return;
        }
        sender.sendMessage(ChatColor.YELLOW + "用法: /ArmsorPlus spawn [shadow [玩家名] | captain]");
    }

    // 击杀/移除存活的影武者 (假玩家为纯发包实体, 原版 /kill 无法选中, 故提供此指令)
    private void handleBossRemove(@NotNull CommandSender sender) {
        if (PlayerBoss.killBoss()) {
            sender.sendMessage(ChatColor.GREEN + "已击杀 ShadowWarrior (含掉落结算)");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "当前没有存活的 ShadowWarrior");
        }
    }

    private void handleReloadMap(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "正在重载地图文件");
        reloadmap();
    }

    private void handleGetEnchantLevel(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "该指令必须由玩家执行");
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "请选择要检查的附魔，可用附魔: " + String.join(", ", args1_enchant));
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            sender.sendMessage(ChatColor.RED + "请手持要检查附魔的物品");
            return;
        }
        NamespacedKey key = ENCHANT_KEY_MAP.get(args[1]);
        if (key == null) {
            sender.sendMessage(ChatColor.RED + "未知附魔: " + args[1]);
            return;
        }
        int level = ArmsorEnchant.getEnchantLevel(item, key);
        String displayName = ArmsorEnchant.getEnchantDisplayName(key);
        if (displayName != null) {
            sender.sendMessage(displayName + " 级别: " + level);
        } else {
            sender.sendMessage("附魔级别: " + level);
        }
    }

    private void handleRemoveEnchant(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "该指令必须由玩家执行");
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            sender.sendMessage(ChatColor.RED + "请手持要去除附魔的物品");
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "用法: /ArmsorPlus removeEnchant <附魔名>，可用附魔: " + String.join(", ", args1_enchant));
            sender.sendMessage(ChatColor.YELLOW + "提示: 输入 all 去除所有自定义附魔");
            return;
        }
        if ("all".equalsIgnoreCase(args[1])) {
            item.editMeta(meta -> {
                for (var key : List.of(
                        Dodgekey, Faminekey, RipplesProtectkey, BloodSacrificekey, EffectClear,
                        FreezeKey, ShadowDodge, BlockingKey, WitheringKey, SurvivorKey,
                        HealthBoostKey, RevengeKey, ExplosiveArrowKey, Sniping, ArrowSpeed,
                        DoubleHitkey, Feedingkey, QuickThrustKey, DiamondDrillKey, BlindnessKey,
                        IndestructibleKey, ProtectionPROKey, StunKey, GolemGuardianKey,
                        CriticalStrikeKey, PiercingKey, LavaWalkerKey, LightningCallKey,
                        HolographicKey, TrackingKey, HarvestKey, AutoPlantKey, StrongBurstKey,
                        MultiShotKey, PoisonKey, SharpBladeKey, PierceKey,
                        ThunderclapArrowKey,
                        Armskey, Armorkey, Bowkey, DiamondPluskey,
                        BasicStone, GuideBookKey, MagicBallKey, MenuMark
                )) {
                    meta.getPersistentDataContainer().remove(key);
                }
                // 清理附魔lore
                List<String> lore = meta.getLore();
                if (lore != null) {
                    List<String> filtered = new ArrayList<>();
                    boolean hasCustomLore = false;
                    for (String line : lore) {
                        if (line.contains("拖动到装备上来使用") || line.contains("级别")) {
                            hasCustomLore = true;
                            continue;
                        }
                        filtered.add(line);
                    }
                    if (hasCustomLore) {
                        meta.setLore(filtered.isEmpty() ? null : filtered);
                    }
                }
            });
            sender.sendMessage(ChatColor.GREEN + "已移除物品上的所有自定义附魔");
            return;
        }
        NamespacedKey key = ENCHANT_KEY_MAP.get(args[1]);
        if (key == null) {
            sender.sendMessage(ChatColor.RED + "未知附魔: " + args[1]);
            return;
        }
        int oldLevel = ArmsorEnchant.getEnchantLevel(item, key);
        if (oldLevel == 0) {
            sender.sendMessage(ChatColor.RED + "物品上没有该附魔");
            return;
        }
        item.editMeta(meta -> meta.getPersistentDataContainer().remove(key));
        ArmsorEnchant.removeEnchantLore(item, key);
        String displayName = ArmsorEnchant.getEnchantDisplayName(key);
        sender.sendMessage(ChatColor.GREEN + "已移除 " + (displayName != null ? displayName : args[1]) + " 附魔 (原等级: " + oldLevel + ")");
    }

    private void handleGetBloodCount(@NotNull CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "该指令必须由玩家执行");
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            sender.sendMessage(ChatColor.RED + "请手持噬生剑");
            return;
        }
        if (ArmsorEnchant.getEnchantLevel(item, DevourLifeSwordKey) == 0) {
            sender.sendMessage(ChatColor.RED + "手持物品没有噬生附魔");
            return;
        }
        int count = ArmsorPlusEnchantEventHandler.getDevourLifeBloodCount(item);
        sender.sendMessage(ChatColor.DARK_PURPLE + "噬生当前血裂数: " + count + "/20");
    }

    private void sendHelp(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "===== ArmsorPlus 指令帮助 =====");
        sender.sendMessage(ChatColor.YELLOW + "/ArmsorPlus help" + ChatColor.RESET + " - 显示本帮助");
        sender.sendMessage(ChatColor.YELLOW + "/ArmsorPlus info" + ChatColor.RESET + " - 插件信息");
        sender.sendMessage(ChatColor.YELLOW + "/ArmsorPlus guide" + ChatColor.RESET + " - 打开插件主菜单");
        sender.sendMessage(ChatColor.YELLOW + "/ArmsorPlus give <物品> [数量] [等级]" + ChatColor.RESET + " - 给予物品");
        sender.sendMessage(ChatColor.GRAY + "  可用物品: Arms_I, Arms_II, Armor_I, Armor_II, Bow_I, DiamondPlus,");
        sender.sendMessage(ChatColor.GRAY + "  MagicBal_I~IV, Blood_Sword, Iron_Epee, BasicStone, GuideBook,");
        sender.sendMessage(ChatColor.GRAY + "  各附魔书(附魔名_EnchantedBook)");
        sender.sendMessage(ChatColor.YELLOW + "/ArmsorPlus getEnchantmentLevel <附魔>" + ChatColor.RESET + " - 查看手持物品的附魔等级");
        sender.sendMessage(ChatColor.YELLOW + "/ArmsorPlus removeEnchant <附魔|all>" + ChatColor.RESET + " - 移除手持物品的附魔");
        sender.sendMessage(ChatColor.YELLOW + "/ArmsorPlus getBloodCount" + ChatColor.RESET + " - 查看手持噬生剑当前血裂数");
        sender.sendMessage(ChatColor.YELLOW + "/ArmsorPlus spawn [shadow [玩家名] | captain]" + ChatColor.RESET + " - 召唤影武者(ShadowWarrior)或村民队长(VillageCaptain)");
        sender.sendMessage(ChatColor.YELLOW + "/ArmsorPlus bossremove" + ChatColor.RESET + " - 击杀存活的影武者(ShadowWarrior)");
        sender.sendMessage(ChatColor.YELLOW + "/ArmsorPlus reloadmap" + ChatColor.RESET + " - 重载地图文件");
    }

    // ====== Tab补全 ======

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                 @NotNull Command command,
                                                 @NotNull String label,
                                                 @NotNull String[] args) {
        switch (args.length) {
            case 1 -> {
                if (args[0].isEmpty()) return args0;
                return args0.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
            }
            case 2 -> {
                if (args[0].equals("give")) {
                    if (args[1].isEmpty()) return args1_give;
                    return args1_give.stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
                } else if (args[0].equals("spawn")) {
                    List<String> spawns = List.of("shadow", "captain");
                    if (args[1].isEmpty()) return spawns;
                    return spawns.stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
                } else if (args[0].equals("getEnchantmentLevel") || args[0].equals("removeEnchant")) {
                    if (args[1].isEmpty()) return args1_enchant;
                    return args1_enchant.stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
                }
            }
            case 3 -> {
                if (args[0].equals("give")) {
                    return List.of("<数量>");
                }
            }
            case 4 -> {
                if (args[0].equals("give") && isEnchantedBook(args[1])) {
                    return List.of("<等级>");
                }
            }
        }
        return List.of();
    }

    // ====== 工具方法 ======

    private boolean IsInt(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i)))
                return false;
        }
        return true;
    }

    // 安全解析数量参数
    private int parseAmount(String[] args, int index, int defaultValue) {
        if (args.length > index && IsInt(args[index])) {
            return Math.max(1, Integer.parseInt(args[index]));
        }
        return defaultValue;
    }

    // 安全解析等级参数
    private int parseLevel(String[] args, int index, int defaultValue) {
        if (args.length > index && IsInt(args[index])) {
            return Math.max(1, Integer.parseInt(args[index]));
        }
        return defaultValue;
    }

    // 判断物品名是否是需要等级参数的附魔书
    private boolean isEnchantedBook(String itemName) {
        return itemName.endsWith("_EnchantedBook") || "BasicStone".equals(itemName);
    }
}
