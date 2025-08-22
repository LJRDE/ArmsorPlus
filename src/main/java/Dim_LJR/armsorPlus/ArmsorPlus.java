package Dim_LJR.armsorPlus;
//前言:
//此插件完全开源但不可用于商业用途
//此版本是原版本重制版
//解决了原版本只有上帝才能看懂的bug
//原生Minecraft版本为paper1.20
//但是1.13~1.21应该都可以用
//变量名类名尽量使用驼峰命名法并且使用英文
//毕竟用拼音容易看不懂
//ArmsorEnchant.addEnchant(ItemStack item,NameSpace key,int level)方法添加自定义附魔
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorPlusEnchantEventHandler;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.EnhancementHandler;
import com.sun.jdi.Bootstrap;
import io.papermc.paper.enchantments.EnchantmentRarity;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.Vector;
import org.codehaus.plexus.interpolation.SingleResponseValueSource;
import org.jetbrains.annotations.NotNull;
import Dim_LJR.armsorPlus.Command.ArmsorPlusCommand;
import org.jetbrains.annotations.Nullable;
import net.kyori.adventure.key.Key;

import javax.lang.model.element.Name;
import java.beans.PersistenceDelegate;
import java.util.Random;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.stream.Collectors;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;


public final class ArmsorPlus extends JavaPlugin implements Listener {
    private void loadconfing() {
    }
    @EventHandler
    public void StonePlace(BlockPlaceEvent event)//基础强化石监听器-防放置_包括一二级强化石
    {
        int x = ArmsorEnchant.getEnchantLevel(event.getItemInHand(),BasicStone) +
                ArmsorEnchant.getEnchantLevel(event.getItemInHand(),Armskey) +
                ArmsorEnchant.getEnchantLevel(event.getItemInHand(),Armorkey);
        if(x==0)
            return;
        event.setCancelled(true);
    }
    @EventHandler
    public void IfUSEBasicStone(PlayerInteractEvent event)//获得强化石
    {
        ItemStack item = event.getItem();
        if(ArmsorEnchant.getEnchantLevel(item,BasicStone)==0)
            return;
        item.setAmount(item.getAmount()-1);
        event.setCancelled(true);
        Random r = new Random();
        int i =r.nextInt(6);
        if(i==1 || i==0) {
            event.getPlayer().getInventory().addItem(ArmorPlusCreate(1));
            event.getPlayer().sendMessage("已获得一级护甲强化石");
        }
        else if(i==2){
            event.getPlayer().getInventory().addItem(ArmorPlusCreateII(1));
            event.getPlayer().sendMessage("已获得二级护甲强化石");
        }
        else if(i==3) {
            event.getPlayer().getInventory().addItem(ArmsPlusCreateI(1));
            event.getPlayer().sendMessage("已获得一级武器强化石");
        }
        else if(i==4){
            event.getPlayer().getInventory().addItem(ArmsPlusCreateII(1));
            event.getPlayer().sendMessage("已获得二级武器强化石");
        }
        else if(i==6){
            event.getPlayer().getInventory().addItem(BowPlusCreate(1));
            event.getPlayer().sendMessage("已获得一级弓箭强化石");
        }
    }
    @Override
    public void onEnable()  {
        regkey(this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ArmsorPlusMenu(),this);
        getServer().getPluginManager().registerEvents(new ArmsorPlusEnchantEventHandler(),this);
        getServer().getPluginManager().registerEvents(new EnhancementHandler(),this);
        this.getCommand("ArmsorPlus").setExecutor(new ArmsorPlusCommand());//注册命令
        int RecipeAmount = 0;
        getLogger().info("ArmsorPlus 配方注册中...");
        //注册精炼金刚石的合成配方
        NamespacedKey DiamondPlusKey = new NamespacedKey(this,"ArmsorPlus_DiamondPlus");
        ItemStack DiamondPlusItem = DIAMONDPLUSCreate(1);
        ShapelessRecipe DiamondPlusRecipe =new ShapelessRecipe(DiamondPlusKey,DiamondPlusItem);
        DiamondPlusRecipe.addIngredient(9, DIAMOND_BLOCK);
        //注册血祭之剑的合成配方
        NamespacedKey BloodSwordkey = new NamespacedKey(this,"ArmsorPlus_BloodSword");
        ItemStack BloodSwordItem = BloodSword(1);
        ShapedRecipe BloodSwordRecipe =new ShapedRecipe(BloodSwordkey,BloodSwordItem);
        BloodSwordRecipe.shape(" A ","ABA"," A ");
        BloodSwordRecipe.setIngredient('A', REDSTONE);
        BloodSwordRecipe.setIngredient('B', SKELETON_SKULL);
        //注册向导书的合成配方
        NamespacedKey GuideRecipeKey = new NamespacedKey(this,"GuideRecipe");
        ItemStack GuideBook = GuideBook(1);
        ShapelessRecipe GuideRecipe = new ShapelessRecipe(GuideRecipeKey,GuideBook);
        GuideRecipe.addIngredient(1, COBBLESTONE);
        //注册基础强化石的合成配方
        NamespacedKey BasicStoneRecipeKey = new NamespacedKey(this,"StoneRecipe");
        ShapelessRecipe StoneRecipe = new ShapelessRecipe(BasicStoneRecipeKey,BasicStone(1));
        StoneRecipe.addIngredient(4, DIAMOND_BLOCK);

        getServer().addRecipe(StoneRecipe);
        RecipeAmount++;
        getServer().addRecipe(GuideRecipe);//添加合成配方
        RecipeAmount++;
        getServer().addRecipe(BloodSwordRecipe);//添加合成配方
        RecipeAmount++;
        getServer().addRecipe(DiamondPlusRecipe);//添加合成配方
        RecipeAmount++;
        getLogger().info("ArmsorPlus 配方注册完成 数量:" + RecipeAmount);
        //注册附魔
        getLogger().info("服务端类型" + Bukkit.getServer().getName());
        getLogger().info("Bukkit API 版本: " + Bukkit.getBukkitVersion());
    }

    @Override
    public void onDisable() {
        getLogger().info("ArmsorPlus 插件已禁用");
        HandlerList.unregisterAll();
    }
}