package Dim_LJR.armsorPlus.Command;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.OpenSea.LoadOpenSea.reloadmap;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

public class ArmsorPlusCommand implements CommandExecutor, TabCompleter {

    private final List<String> args0 = List.of(
            "info", "give", "spawn", "getEnchantmentLevel", "removeEnchant", "reloadmap", "help"
    );
    private final List<String> args1_give = List.of(
            "Arms_I", "Arms_II", "Armor_I", "Armor_II", "Bow_I",
            "DiamondPlus", "MagicBal_I", "MagicBal_II", "MagicBal_III", "MagicBal_IV",
            "Blood_Sword", "Iron_Epee",
            "BasicStone", "GuideBook",
            "Dodge_EnchantedBook", "Famine_EnchantedBook",
            "Ripples_EnchantedBook", "BloodSacrifice_EnchantedBook",
            "Freeze_EnchantedBook", "EffectClear_EnchantedBook",
            "Sniping_EnchantedBook", "DoubleHit_EnchantedBook",
            "Blocking_EnchantedBook", "Withering_EnchantedBook",
            "Survivor_EnchantedBook", "HealthBoost_EnchantedBook",
            "Revenge_EnchantedBook", "ExplosiveArrow_EnchantedBook",
            "ShadowDodge_EnchantedBook", "ArrowSpeed_EnchantedBook",
            "Feeding_EnchantedBook"
    );
    private final List<String> args1_enchant = List.of(
            "Dodge", "Famine", "Ripples", "BloodSacrifice", "EffectClear",
            "Freeze", "ShadowDodge", "Blocking", "Withering", "Survivor",
            "HealthBoost", "Revenge", "ExplosiveArrow", "Sniping",
            "ArrowSpeed", "DoubleHit", "Feeding"
    );

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
            case "spawn" -> handleSpawn(sender);
            case "reloadmap" -> handleReloadMap(sender);
            case "getEnchantmentLevel" -> handleGetEnchantLevel(sender, args);
            case "removeEnchant" -> handleRemoveEnchant(sender, args);
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
            case "Survivor_EnchantedBook" -> {
                int amount = parseAmount(args, 2, 1);
                int level = parseLevel(args, 3, 1);
                player.getInventory().addItem(Survivor_EnchantedBook(amount, level));
                sender.sendMessage("已给予 " + amount + " 本" + ChatColor.GOLD + "幸存" + ChatColor.RESET + "附魔书 (等级" + level + ")");
            }
            default -> sender.sendMessage(ChatColor.RED + "未知物品: " + args[1] + "，请输入 /ArmsorPlus help 查看可用物品");
        }
    }

    private void handleSpawn(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "开发中...");
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
        int level = switch (args[1]) {
            case "Dodge" -> ArmsorEnchant.getEnchantLevel(item, Dodgekey);
            case "Famine" -> ArmsorEnchant.getEnchantLevel(item, Faminekey);
            case "Ripples" -> ArmsorEnchant.getEnchantLevel(item, RipplesProtectkey);
            case "BloodSacrifice" -> ArmsorEnchant.getEnchantLevel(item, BloodSacrificekey);
            case "EffectClear" -> ArmsorEnchant.getEnchantLevel(item, EffectClear);
            case "Freeze" -> ArmsorEnchant.getEnchantLevel(item, FreezeKey);
            case "ShadowDodge" -> ArmsorEnchant.getEnchantLevel(item, ShadowDodge);
            case "Blocking" -> ArmsorEnchant.getEnchantLevel(item, BlockingKey);
            case "Withering" -> ArmsorEnchant.getEnchantLevel(item, WitheringKey);
            case "Survivor" -> ArmsorEnchant.getEnchantLevel(item, SurvivorKey);
            case "HealthBoost" -> ArmsorEnchant.getEnchantLevel(item, HealthBoostKey);
            case "Revenge" -> ArmsorEnchant.getEnchantLevel(item, RevengeKey);
            case "ExplosiveArrow" -> ArmsorEnchant.getEnchantLevel(item, ExplosiveArrowKey);
            case "Sniping" -> ArmsorEnchant.getEnchantLevel(item, Sniping);
            case "ArrowSpeed" -> ArmsorEnchant.getEnchantLevel(item, ArrowSpeed);
            case "DoubleHit" -> ArmsorEnchant.getEnchantLevel(item, DoubleHitkey);
            case "Feeding" -> ArmsorEnchant.getEnchantLevel(item, Feedingkey);
            default -> -1;
        };
        if (level < 0) {
            sender.sendMessage(ChatColor.RED + "未知附魔: " + args[1]);
        } else {
            String displayName = ArmsorEnchant.getEnchantDisplayName(
                    switch (args[1]) {
                        case "Dodge" -> Dodgekey;
                        case "Famine" -> Faminekey;
                        case "Ripples" -> RipplesProtectkey;
                        case "BloodSacrifice" -> BloodSacrificekey;
                        case "EffectClear" -> EffectClear;
                        case "Freeze" -> FreezeKey;
                        case "ShadowDodge" -> ShadowDodge;
                        case "Blocking" -> BlockingKey;
                        case "Withering" -> WitheringKey;
                        case "Survivor" -> SurvivorKey;
                        case "HealthBoost" -> HealthBoostKey;
                        case "Revenge" -> RevengeKey;
                        case "ExplosiveArrow" -> ExplosiveArrowKey;
                        case "Sniping" -> Sniping;
                        case "ArrowSpeed" -> ArrowSpeed;
                        case "DoubleHit" -> DoubleHitkey;
                        case "Feeding" -> Feedingkey;
                        default -> null;
                    }
            );
            if (displayName != null) {
                sender.sendMessage(displayName + " 级别: " + level);
            } else {
                sender.sendMessage("附魔级别: " + level);
            }
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
                        DoubleHitkey, Feedingkey, Armskey, Armorkey, Bowkey, DiamondPluskey,
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
        var key = switch (args[1]) {
            case "Dodge" -> Dodgekey;
            case "Famine" -> Faminekey;
            case "Ripples" -> RipplesProtectkey;
            case "BloodSacrifice" -> BloodSacrificekey;
            case "EffectClear" -> EffectClear;
            case "Freeze" -> FreezeKey;
            case "ShadowDodge" -> ShadowDodge;
            case "Blocking" -> BlockingKey;
            case "Withering" -> WitheringKey;
            case "Survivor" -> SurvivorKey;
            case "HealthBoost" -> HealthBoostKey;
            case "Revenge" -> RevengeKey;
            case "ExplosiveArrow" -> ExplosiveArrowKey;
            case "Sniping" -> Sniping;
            case "ArrowSpeed" -> ArrowSpeed;
            case "DoubleHit" -> DoubleHitkey;
            case "Feeding" -> Feedingkey;
            default -> null;
        };
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

    private void sendHelp(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "===== ArmsorPlus 指令帮助 =====");
        sender.sendMessage(ChatColor.YELLOW + "/ArmsorPlus help" + ChatColor.RESET + " - 显示本帮助");
        sender.sendMessage(ChatColor.YELLOW + "/ArmsorPlus info" + ChatColor.RESET + " - 插件信息");
        sender.sendMessage(ChatColor.YELLOW + "/ArmsorPlus give <物品> [数量] [等级]" + ChatColor.RESET + " - 给予物品");
        sender.sendMessage(ChatColor.GRAY + "  可用物品: Arms_I, Arms_II, Armor_I, Armor_II, Bow_I, DiamondPlus,");
        sender.sendMessage(ChatColor.GRAY + "  MagicBal_I~IV, Blood_Sword, Iron_Epee, BasicStone, GuideBook,");
        sender.sendMessage(ChatColor.GRAY + "  各附魔书(附魔名_EnchantedBook)");
        sender.sendMessage(ChatColor.YELLOW + "/ArmsorPlus getEnchantmentLevel <附魔>" + ChatColor.RESET + " - 查看手持物品的附魔等级");
        sender.sendMessage(ChatColor.YELLOW + "/ArmsorPlus removeEnchant <附魔|all>" + ChatColor.RESET + " - 移除手持物品的附魔");
        sender.sendMessage(ChatColor.YELLOW + "/ArmsorPlus spawn" + ChatColor.RESET + " - 公海地图传送(开发中)");
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

    /** 安全解析数量参数 */
    private int parseAmount(String[] args, int index, int defaultValue) {
        if (args.length > index && IsInt(args[index])) {
            return Math.max(1, Integer.parseInt(args[index]));
        }
        return defaultValue;
    }

    /** 安全解析等级参数 */
    private int parseLevel(String[] args, int index, int defaultValue) {
        if (args.length > index && IsInt(args[index])) {
            return Math.max(1, Integer.parseInt(args[index]));
        }
        return defaultValue;
    }

    /** 判断物品名是否是需要等级参数的附魔书 */
    private boolean isEnchantedBook(String itemName) {
        return itemName.endsWith("_EnchantedBook") || "BasicStone".equals(itemName);
    }
}
