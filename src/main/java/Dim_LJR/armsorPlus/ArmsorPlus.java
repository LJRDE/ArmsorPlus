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
import Dim_LJR.armsorPlus.OpenSea.OpenSeaDig;
import Dim_LJR.armsorPlus.OpenSea.OpenSeaEntity;
import Dim_LJR.armsorPlus.OpenSea.OpenSeaLottery;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import Dim_LJR.armsorPlus.Command.ArmsorPlusCommand;
import Dim_LJR.armsorPlus.OpenSea.LoadOpenSea;

import java.util.List;
import java.util.Random;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static Dim_LJR.armsorPlus.NamespaceKey.banner;
import static org.bukkit.Material.*;


public final class ArmsorPlus extends JavaPlugin implements Listener {
    private void loadconfing() {
        saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        config.addDefault("SpawnOpenSea", true);
        config.options().copyDefaults(true);
        saveConfig();
        if(config.getBoolean("SpawnOpenSea"))
        {
            getLogger().info("加载公海地图中");
            LoadOpenSea.loadMap(Bukkit.getWorldContainer().toPath(),this.getResource("OpenSea.zip"));
            getLogger().info("加载公海抽奖功能");
            getServer().getPluginManager().registerEvents(new OpenSeaLottery(),this);
            getLogger().info("加载公海地图保护功能");
            getServer().getPluginManager().registerEvents(new OpenSeaDig(),this);
            getLogger().info("加载公海地图生物功能");
            getServer().getPluginManager().registerEvents(new OpenSeaEntity(),this);
        }
        else
        {
            getLogger().info("公海地图已关闭");
        }
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
        banner();
        loadconfing();
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
        //注册重剑的合成配方
        NamespacedKey EpeeKey = new NamespacedKey(this,"ArmsorPlus_EpeeKey");
        ItemStack EpeeSwordItem = Iron_Epee(1);
        ShapedRecipe EpeeSwordRecipe =new ShapedRecipe(EpeeKey,EpeeSwordItem);
        EpeeSwordRecipe.shape(" A "," A "," B ");
        EpeeSwordRecipe.setIngredient('A', IRON_BLOCK);
        EpeeSwordRecipe.setIngredient('B', STICK);
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
        getServer().addRecipe(EpeeSwordRecipe);
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