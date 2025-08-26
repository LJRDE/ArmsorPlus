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

import java.util.List;
import java.util.stream.Collectors;

import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.OpenSea.LoadOpenSea.reloadmap;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

public class ArmsorPlusCommand implements CommandExecutor, TabCompleter {//指令自动补全
    private final List<String> args0 =List.of("info","give","spawn","getEnchantmentLevel","removeEnchant","reloadmap");
    private final List<String> args1_give =List.of("Arms_I","Arms_II","Armor_I","Armor_II","Bow_I",
            "DiamondPlus","MagicBal_I","MagicBal_II","MagicBal_III","MagicBal_IV","Blood_Sword","Dodge_EnchantedBook","Famine_EnchantedBook",
            "Ripples_EnchantedBook","BloodSacrifice_EnchantedBook","Freeze_EnchantedBook","BasicStone","EffectClear_EnchantedBook",
            "Sniping_EnchantedBook");
    private final List<String> args1_spawn =List.of("");
    private final List<String> args1_enchant =List.of("Dodge","Famine","Ripples","BloodSacrifice","EffectClear");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String lable, @NotNull String[] args) {
        if(!cmd.getName().equalsIgnoreCase("ArmsorPlus"))
            return false;
        if(!sender.hasPermission("ArmsorPlus.op")){
            sender.sendMessage(ChatColor.GRAY + "你没有权限使用该指令");
            return false;
        }
        Player player = (Player) sender;
        ItemStack ArmsStone_I ;//武器强化石I
        ItemStack ArmsStone_II ;
        ItemStack ArmorStone_I = ArmorPlusCreate(1);
        ItemStack ArmorStone_II = ArmorPlusCreateII(1);
        ItemStack BowStone_I = BowPlusCreate(1);
        ItemStack DIAMONDPLUS = DIAMONDPLUSCreate(1);
        ItemStack MagicBall_I = MagicBallCreateI(1);
        ItemStack MagicBall_II = MagicBallCreateII(1);
        ItemStack MagicBall_III = MagicBallCreateIII(1);
        ItemStack MagicBall_IV = MagicBallCreateIV(1);
        ItemStack BloodSword = BloodSword(1);
        ItemStack Dodge_EnchantedBook;
        ItemStack Famine_EnchantedBook;
        ItemStack Ripples_EnchantedBook;
        ItemStack BloodSacrifice_EnchantedBook;
        if(args.length==0){
            sender.sendMessage("请输入指令");
            return false;
        }
        switch (args[0] ) {
            case "give" -> {
                if (args.length==1) {
                    sender.sendMessage("请选择要给予的物品");
                    return false;
                }
                switch (args[1]) {
                    case "Sniping_EnchantedBook" -> {
                        if (args.length == 4 && IsInt(args[2])) {
                            player.getInventory().addItem(Sniping_EnchantdeBook(Integer.parseInt(args[2]), Integer.parseInt(args[3])));
                            sender.sendMessage("已给予" + Integer.parseInt(args[2]) + "本" + Integer.parseInt(args[3]) + "级" +
                                    ChatColor.LIGHT_PURPLE
                                    + "狙击" + ChatColor.RESET + "附魔书");
                            return true;
                        }
                    }
                    case "EffectClear_EnchantedBook" -> {
                        if(IsInt(args[2]) && args.length==4){
                            player.getInventory().addItem(EffectClear_EnchantdeBook(Integer.parseInt(args[2]),Integer.parseInt(args[3])));
                            sender.sendMessage("已给予"+ Integer.parseInt(args[2]) + "本" + Integer.parseInt(args[3]) + "级" + ChatColor.WHITE
                                    + "涤魂" + ChatColor.RESET + "附魔书");
                            return true;
                        }
                    }
                    case "BasicStone" -> {
                        if(IsInt(args[2]) && args.length==3){
                            player.getInventory().addItem(BasicStone(Integer.parseInt(args[2])));
                            sender.sendMessage("已给予" + Integer.parseInt(args[2]) + "个基础强化石");
                            return true;
                        }
                    }
                    case "Freeze_EnchantedBook" -> {
                        if(IsInt(args[2]) && args.length==4){
                            player.getInventory().addItem(Freeze_EnchantedBook(Integer.parseInt(args[2]),Integer.parseInt(args[3])));
                            sender.sendMessage("已给予"+ Integer.parseInt(args[2]) + "本" + Integer.parseInt(args[3]) + "级" + ChatColor.AQUA
                                    + "寒冻" + ChatColor.RESET + "附魔书");
                            return true;
                        }
                    }
                    case "BloodSacrifice_EnchantedBook" -> {
                        BloodSacrifice_EnchantedBook = BloodSacrifice_EnchantdeBook(1,1);
                        if(IsInt(args[2]) && args.length==4)
                            BloodSacrifice_EnchantedBook = BloodSacrifice_EnchantdeBook(Integer.parseInt(args[2]),Integer.parseInt(args[3]));
                        player.getInventory().addItem(BloodSacrifice_EnchantedBook);
                        sender.sendMessage("已给予"+ Integer.parseInt(args[2]) + "本" + Integer.parseInt(args[3]) + "级" + ChatColor.RED
                                + "血祭" + ChatColor.RESET + "附魔书");
                        return true;
                    }
                    case "Ripples_EnchantedBook" -> {
                        Ripples_EnchantedBook = Ripples_EnchantdeBook(1,1);
                        if(IsInt(args[2]) && args.length==3)
                            Ripples_EnchantedBook = Ripples_EnchantdeBook(Integer.parseInt(args[2]),Integer.parseInt(args[3]));
                        player.getInventory().addItem(Ripples_EnchantedBook);
                        sender.sendMessage("已给予"+ Integer.parseInt(args[2]) + "本" + Integer.parseInt(args[3]) + "级" + ChatColor.BLUE
                                + "涟漪" + ChatColor.RESET + "附魔书");
                        return true;
                    }
                    case "Famine_EnchantedBook" -> {
                        Famine_EnchantedBook = Famine_EnchantdeBook(1,1);
                        if(IsInt(args[2]) && args.length==3)
                            Famine_EnchantedBook = Famine_EnchantdeBook(Integer.parseInt(args[2]),Integer.parseInt(args[3]));
                        player.getInventory().addItem(Famine_EnchantedBook);
                        sender.sendMessage("已给予"+ Integer.parseInt(args[2]) + "本" + Integer.parseInt(args[3]) + "级饥荒附魔书");
                        return true;
                    }
                    case "Dodge_EnchantedBook" -> {
                        Dodge_EnchantedBook = Dodge_EnchantdeBook(1,1);
                        if(IsInt(args[2]) && args.length==3)
                            Dodge_EnchantedBook = Dodge_EnchantdeBook(Integer.parseInt(args[2]),Integer.parseInt(args[3]));
                        player.getInventory().addItem(Dodge_EnchantedBook);
                        sender.sendMessage("已给予"+ Integer.parseInt(args[2]) + "本" + Integer.parseInt(args[3]) + "级闪避附魔书");
                        return true;
                    }
                    case "DiamondPlus" -> {
                        player.getInventory().addItem(DIAMONDPLUS);
                        sender.sendMessage("已获得强化金刚石");
                        return true;
                    }
                    case "Arms_I" -> {
                        if (args.length==3 && IsInt(args[2])) {
                            ArmsStone_I = ArmsPlusCreateI(Integer.parseInt(args[2]));
                        } else ArmsStone_I = ArmsPlusCreateI(1);
                        player.getInventory().addItem(ArmsStone_I);
                        sender.sendMessage("已获得一级武器强化石");
                        return true;
                    }
                    case "Arms_II" -> {
                        if (args.length==3 && IsInt(args[2])) {
                            ArmsStone_II = ArmsPlusCreateII(Integer.parseInt(args[2]));
                        } else ArmsStone_II = ArmsPlusCreateII(1);
                        player.getInventory().addItem(ArmsStone_II);
                        sender.sendMessage("已获得二级武器强化石");
                        return true;
                    }
                    case "Armor_I" -> {
                        player.getInventory().addItem(ArmorStone_I);
                        sender.sendMessage("已获得一级护甲强化石");
                        return true;
                    }
                    case "Armor_II" -> {
                        player.getInventory().addItem(ArmorStone_II);
                        sender.sendMessage("已获得二级护甲强化石");
                        return true;
                    }
                    case "Bow_I" -> {
                        player.getInventory().addItem(BowStone_I);
                        sender.sendMessage("已获得一级弓强化石");
                        return true;
                    }
                    case "MagicBal_I" -> {
                        player.getInventory().addItem(MagicBall_I);
                        sender.sendMessage("已获得寻常的魔法球");
                        return true;
                    }
                    case "MagicBal_II" -> {
                        player.getInventory().addItem(MagicBall_II);
                        sender.sendMessage("已获得稀有的魔法球");
                        return true;
                    }
                    case "MagicBal_III" -> {
                        player.getInventory().addItem(MagicBall_III);
                        sender.sendMessage("已获得史诗的魔法球");
                        return true;
                    }
                    case "MagicBal_IV" -> {
                        player.getInventory().addItem(MagicBall_IV);
                        sender.sendMessage("已获得传奇的魔法球");
                        return true;
                    }
                    case "Blood_Sword" -> {
                        player.getInventory().addItem(BloodSword);
                        sender.sendMessage(ChatColor.RED + "已获得血祭之剑");
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.RED +"请输入物品及其数量!");
                return false;
            }
            case "info" -> {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "嘿嘿，这个是Dim_LJR写的插件哦");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "虽然说不知道为什么BUG越改越多就是了");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "如果你不知道这么用强化石的话，可以把它拖动到你的装备上单击鼠标左键试试呢");
                return true;
            }
            case "spawn" -> {
                sender.sendMessage("开发中...");
                return true;
            }
            case "reloadmap" -> {
                sender.sendMessage("正在重载地图文件");
                reloadmap();
                return true;
            }
            case "getEnchantmentLevel" -> {
                if(args.length==1)
                    sender.sendMessage("请选择要检查的附魔");
                if(args[1].equals("Dodge")) {
                    ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
                    int level = ArmsorEnchant.getEnchantLevel(item, Dodgekey);
                    player.sendMessage("闪避级别" + level);
                    return true;
                }
                else if(args[1].equals("Famine")) {
                    ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
                    int level = ArmsorEnchant.getEnchantLevel(item, Faminekey);
                    player.sendMessage("饥荒级别" + level);
                    return true;
                }
                else if(args[1].equals("Ripples")) {
                    ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
                    int level = ArmsorEnchant.getEnchantLevel(item, RipplesProtectkey);
                    player.sendMessage("涟漪级别" + level);
                    return true;
                }
                else if(args[1].equals("BloodSacrifice")) {
                    ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
                    int level = ArmsorEnchant.getEnchantLevel(item, BloodSacrificekey);
                    player.sendMessage("血祭级别" + level);
                    return true;
                }
                else if(args[1].equals("EffectClear")) {
                    ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
                    int level = ArmsorEnchant.getEnchantLevel(item, EffectClear);
                    player.sendMessage("涤魂级别" + level);
                    return true;
                }
                else return false;
            }
        }
        sender.sendMessage(ChatColor.RED + "ERROR");
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String label,
                                                @NotNull String[] args) {
        switch (args.length) {
            case 1:
                if(args[0].isEmpty()) return args0;
                else return args0.stream().filter(s->s.startsWith(args[0])).collect(Collectors.toList());
            case 2:
                if(args[0].equals("give")){
                    if(args[1].isEmpty()) return args1_give;
                    else return args1_give.stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
                }
                else if(args[0].equals("spawn")){
                    if(args[1].isEmpty()) return args1_spawn;
                }
                else if(args[0].equals("getEnchantmentLevel")){
                    if(args[1].isEmpty()) return args1_enchant;
                    else return args1_enchant.stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
                }
        }
        return List.of();
    }
    private boolean IsInt(String args){
        for(int i = args.length();--i >= 0;){
            if (!Character.isDigit(args.charAt(i)))
                return false;
        }
        return true;
    }
}//指令执行，自动补全
