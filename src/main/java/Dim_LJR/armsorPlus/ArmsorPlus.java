package Dim_LJR.armsorPlus;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorPlusEnchantEventHandler;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.EnhancementHandler;
import Dim_LJR.armsorPlus.Boss.BossMenu;
import Dim_LJR.armsorPlus.Boss.BossWorld;
import Dim_LJR.armsorPlus.Command.ArmsorPlusCommand;
import Dim_LJR.armsorPlus.food.FoodListeners;
import Dim_LJR.armsorPlus.OpenSea.LoadOpenSea;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Random;

import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.food.FoodItems.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static Dim_LJR.armsorPlus.NamespaceKey.banner;
import static org.bukkit.Material.*;

/**
 * ArmsorPlus —— Minecraft武器装备强化插件
 * <p>
 * 完全开源但不可用于商业用途。
 * 此版本是原版本重制版，解决了原版本只有上帝才能看懂的bug。
 * 客户端: Paper 1.21+ (向下兼容至1.13)
 * <p>
 * 核心功能:
 * - 自定义附魔系统 (基于PDC存储)
 * - 武器/护甲/弓强化系统
 * - 魔法球抽奖系统
 * - 公海世界地图
 */
public final class ArmsorPlus extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        regkey(this);           // 注册所有NamespaceKey
        banner();               // 打印启动横幅
        loadconfig();           // 加载配置文件
        PlayerSettings.load(this); // 加载玩家设置
        registerListeners();    // 注册事件监听器
        registerCommands();     // 注册命令
        registerRecipes();      // 注册合成配方
        BossWorld.loadWorld();  // 加载BOSS世界

        getLogger().info("===== ArmsorPlus v" + getDescription().getVersion() + " 已启用 =====");
        getLogger().info("服务端类型: " + Bukkit.getServer().getName());
        getLogger().info("Bukkit API 版本: " + Bukkit.getBukkitVersion());
    }

    @Override
    public void onDisable() {
        PlayerSettings.save(this); // 保存玩家设置
        getLogger().info("ArmsorPlus 插件已禁用");
        HandlerList.unregisterAll();
    }

    /** 注册所有事件监听器 */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ArmsorPlusMenu(), this);
        getServer().getPluginManager().registerEvents(new ArmsorPlusEnchantEventHandler(), this);
        getServer().getPluginManager().registerEvents(new EnhancementHandler(), this);
        getServer().getPluginManager().registerEvents(new BossMenu(), this);
        getServer().getPluginManager().registerEvents(new ArmsorPlusItemHandler(), this);
        getServer().getPluginManager().registerEvents(new FoodListeners(), this);
    }

    /** 注册指令执行器 */
    private void registerCommands() {
        this.getCommand("ArmsorPlus").setExecutor(new ArmsorPlusCommand());
    }

    /** 注册合成配方 */
    private void registerRecipes() {
        int count = 0;

        // 精炼金刚石: 9个钻石块 → 1个
        NamespacedKey diamondPlusKey = new NamespacedKey(this, "ArmsorPlus_DiamondPlus");
        ShapelessRecipe diamondPlusRecipe = new ShapelessRecipe(diamondPlusKey, DIAMONDPLUSCreate(1));
        diamondPlusRecipe.addIngredient(9, DIAMOND_BLOCK);
        getServer().addRecipe(diamondPlusRecipe);
        count++;

        // 血祭之剑: 红石围骷髅头
        NamespacedKey bloodSwordKey = new NamespacedKey(this, "ArmsorPlus_BloodSword");
        ShapedRecipe bloodSwordRecipe = new ShapedRecipe(bloodSwordKey, BloodSword(1));
        bloodSwordRecipe.shape(" A ", "ABA", " A ");
        bloodSwordRecipe.setIngredient('A', REDSTONE);
        bloodSwordRecipe.setIngredient('B', SKELETON_SKULL);
        getServer().addRecipe(bloodSwordRecipe);
        count++;

        // 重剑: 铁块夹木棍
        NamespacedKey epeeKey = new NamespacedKey(this, "ArmsorPlus_EpeeKey");
        ShapedRecipe epeeRecipe = new ShapedRecipe(epeeKey, Iron_Epee(1));
        epeeRecipe.shape(" A ", " A ", " B ");
        epeeRecipe.setIngredient('A', IRON_BLOCK);
        epeeRecipe.setIngredient('B', STICK);
        getServer().addRecipe(epeeRecipe);
        count++;

        // 向导书: 圆石
        NamespacedKey guideKey = new NamespacedKey(this, "GuideRecipe");
        getServer().addRecipe(new ShapelessRecipe(guideKey, GuideBook(1)).addIngredient(1, COBBLESTONE));
        count++;

        // 基础强化石: 4个钻石块
        NamespacedKey stoneKey = new NamespacedKey(this, "StoneRecipe");
        getServer().addRecipe(new ShapelessRecipe(stoneKey, BasicStone(1)).addIngredient(4, DIAMOND_BLOCK));
        count++;

        // 匕首: 绿宝石 + 木棍
        NamespacedKey daggerKey = new NamespacedKey(this, "ArmsorPlus_Dagger");
        ShapedRecipe daggerRecipe = new ShapedRecipe(daggerKey, Dagger(1));
        daggerRecipe.shape(" E ", " S ", "   ");
        daggerRecipe.setIngredient('E', EMERALD);
        daggerRecipe.setIngredient('S', STICK);
        getServer().addRecipe(daggerRecipe);
        count++;

        // 飞斧: 2下界合金锭 + 绿宝石块 + 2木棍
        NamespacedKey axeKey = new NamespacedKey(this, "ArmsorPlus_ThrowingAxe");
        ShapedRecipe axeRecipe = new ShapedRecipe(axeKey, ThrowingAxe(1));
        axeRecipe.shape("N N", " E ", "S S");
        axeRecipe.setIngredient('N', NETHERITE_INGOT);
        axeRecipe.setIngredient('E', EMERALD_BLOCK);
        axeRecipe.setIngredient('S', STICK);
        getServer().addRecipe(axeRecipe);
        count++;

        // 骷髅权杖: 8骷髅头颅 + 不死图腾
        NamespacedKey scepterKey = new NamespacedKey(this, "ArmsorPlus_Scepter");
        ShapedRecipe scepterRecipe = new ShapedRecipe(scepterKey, SkeletonScepter(1));
        scepterRecipe.shape("SSS", "STS", "SSS");
        scepterRecipe.setIngredient('S', SKELETON_SKULL);
        scepterRecipe.setIngredient('T', TOTEM_OF_UNDYING);
        getServer().addRecipe(scepterRecipe);
        count++;

        // 寒冰弓: 蓝冰 + 木棍
        NamespacedKey frostBowKey = new NamespacedKey(this, "ArmsorPlus_FrostBow");
        ShapedRecipe frostBowRecipe = new ShapedRecipe(frostBowKey, FrostBow(1));
        frostBowRecipe.shape(" IB", "I B", " IB");
        frostBowRecipe.setIngredient('I', BLUE_ICE);
        frostBowRecipe.setIngredient('B', STICK);
        getServer().addRecipe(frostBowRecipe);
        count++;

        // 火焰戟: 下界合金锭 + 火焰弹 + 烈焰棒
        NamespacedKey halberdKey = new NamespacedKey(this, "ArmsorPlus_FlameHalberd");
        ShapedRecipe halberdRecipe = new ShapedRecipe(halberdKey, FlameHalberd(1));
        halberdRecipe.shape(" NF", " BN", "B  ");
        halberdRecipe.setIngredient('N', NETHERITE_INGOT);
        halberdRecipe.setIngredient('F', FIRE_CHARGE);
        halberdRecipe.setIngredient('B', BLAZE_ROD);
        getServer().addRecipe(halberdRecipe);
        count++;

        // 回春散: 8闪烁的西瓜片 + 玻璃瓶
        NamespacedKey rejuvenPowderKey = new NamespacedKey(this, "ArmsorPlus_RejuvenPowder");
        ShapelessRecipe rejuvenPowderRecipe = new ShapelessRecipe(rejuvenPowderKey, RejuvenationPowder(1));
        rejuvenPowderRecipe.addIngredient(8, GLISTERING_MELON_SLICE);
        rejuvenPowderRecipe.addIngredient(1, GLASS_BOTTLE);
        getServer().addRecipe(rejuvenPowderRecipe);
        count++;

        // 止血绷带: 2地狱疣 + 白色羊毛
        NamespacedKey bandageKey = new NamespacedKey(this, "ArmsorPlus_Bandage");
        ShapedRecipe bandageRecipe = new ShapedRecipe(bandageKey, HemostaticBandage(1));
        bandageRecipe.shape(" N ", "N W", "   ");
        bandageRecipe.setIngredient('N', NETHER_WART);
        bandageRecipe.setIngredient('W', WHITE_WOOL);
        getServer().addRecipe(bandageRecipe);
        count++;

        // 压缩饼干: 9面包
        NamespacedKey biscuitKey = new NamespacedKey(this, "ArmsorPlus_Biscuit");
        ShapedRecipe biscuitRecipe = new ShapedRecipe(biscuitKey, CompressedBiscuit(1));
        biscuitRecipe.shape("BBB", "BBB", "BBB");
        biscuitRecipe.setIngredient('B', BREAD);
        getServer().addRecipe(biscuitRecipe);
        count++;

        // 盐: 沙子x1
        NamespacedKey saltKey = new NamespacedKey(this, "ArmsorPlus_Salt");
        getServer().addRecipe(new ShapelessRecipe(saltKey, Salt(1)).addIngredient(1, SAND));
        count++;

        // 肉干: 盐x1 + 任意熟肉 (猪肉/羊肉单独配方)
        Material[] meats = {COOKED_BEEF, COOKED_CHICKEN, COOKED_RABBIT, COOKED_COD, COOKED_SALMON};
        for (int i = 0; i < meats.length; i++) {
            NamespacedKey jerkyKey = new NamespacedKey(this, "ArmsorPlus_Jerky_" + i);
            ShapelessRecipe jerkyRecipe = new ShapelessRecipe(jerkyKey, Jerky(1));
            jerkyRecipe.addIngredient(new RecipeChoice.ExactChoice(Salt(1)));
            jerkyRecipe.addIngredient(1, meats[i]);
            getServer().addRecipe(jerkyRecipe);
            count++;
        }

        // 猪肉干: 盐x1 + 熟猪排x1
        NamespacedKey porkJerkyKey = new NamespacedKey(this, "ArmsorPlus_PorkJerky");
        ShapelessRecipe porkJerkyRecipe = new ShapelessRecipe(porkJerkyKey, PorkJerky(1));
        porkJerkyRecipe.addIngredient(new RecipeChoice.ExactChoice(Salt(1)));
        porkJerkyRecipe.addIngredient(1, COOKED_PORKCHOP);
        getServer().addRecipe(porkJerkyRecipe);
        count++;

        // 羊肉干: 盐x1 + 熟羊肉x1
        NamespacedKey muttonJerkyKey = new NamespacedKey(this, "ArmsorPlus_MuttonJerky");
        ShapelessRecipe muttonJerkyRecipe = new ShapelessRecipe(muttonJerkyKey, MuttonJerky(1));
        muttonJerkyRecipe.addIngredient(new RecipeChoice.ExactChoice(Salt(1)));
        muttonJerkyRecipe.addIngredient(1, COOKED_MUTTON);
        getServer().addRecipe(muttonJerkyRecipe);
        count++;

        // 甜浆果派: 甜浆果x3 + 小麦x3
        NamespacedKey pieKey = new NamespacedKey(this, "ArmsorPlus_SweetBerryPie");
        ShapelessRecipe pieRecipe = new ShapelessRecipe(pieKey, SweetBerryPie(1));
        pieRecipe.addIngredient(3, SWEET_BERRIES);
        pieRecipe.addIngredient(3, WHEAT);
        getServer().addRecipe(pieRecipe);
        count++;

        // 酒桶: 小麦x3 + 桶x1
        NamespacedKey wineBarrelKey = new NamespacedKey(this, "ArmsorPlus_WineBarrel");
        ShapedRecipe wineBarrelRecipe = new ShapedRecipe(wineBarrelKey, WineBarrel(1));
        wineBarrelRecipe.shape("WWW", " B ", "   ");
        wineBarrelRecipe.setIngredient('W', WHEAT);
        wineBarrelRecipe.setIngredient('B', BARREL);
        getServer().addRecipe(wineBarrelRecipe);
        count++;

        // 腐肉干: 腐肉x1 + 盐x1
        NamespacedKey rottenJerkyKey = new NamespacedKey(this, "ArmsorPlus_RottenJerky");
        ShapelessRecipe rottenJerkyRecipe = new ShapelessRecipe(rottenJerkyKey, RottenJerky(1));
        rottenJerkyRecipe.addIngredient(1, ROTTEN_FLESH);
        rottenJerkyRecipe.addIngredient(new RecipeChoice.ExactChoice(Salt(1)));
        getServer().addRecipe(rottenJerkyRecipe);
        count++;

        // 雨御前: 海晶碎片x4 + 钻石剑x1
        NamespacedKey rainSwordKey = new NamespacedKey(this, "ArmsorPlus_RainSword");
        ShapedRecipe rainSwordRecipe = new ShapedRecipe(rainSwordKey, RainSword(1));
        rainSwordRecipe.shape(" N ", "NSN", " N ");
        rainSwordRecipe.setIngredient('N', PRISMARINE_SHARD);
        rainSwordRecipe.setIngredient('S', DIAMOND_SWORD);
        getServer().addRecipe(rainSwordRecipe);
        count++;

        // 飞天御剑: 幻翼膜x4 + 金剑x1
        NamespacedKey flyingSwordKey = new NamespacedKey(this, "ArmsorPlus_FlyingSword");
        ShapedRecipe flyingSwordRecipe = new ShapedRecipe(flyingSwordKey, FlyingSword(1));
        flyingSwordRecipe.shape(" N ", "NSN", " N ");
        flyingSwordRecipe.setIngredient('N', PHANTOM_MEMBRANE);
        flyingSwordRecipe.setIngredient('S', GOLDEN_SWORD);
        getServer().addRecipe(flyingSwordRecipe);
        count++;

        // 寒冰剑: 蓝冰x8 + 钻石剑x1
        NamespacedKey iceSwordKey = new NamespacedKey(this, "ArmsorPlus_IceSword");
        ShapedRecipe iceSwordRecipe = new ShapedRecipe(iceSwordKey, IceSword(1));
        iceSwordRecipe.shape("BBB", "BSB", "BBB");
        iceSwordRecipe.setIngredient('B', BLUE_ICE);
        iceSwordRecipe.setIngredient('S', DIAMOND_SWORD);
        getServer().addRecipe(iceSwordRecipe);
        count++;

        // 盘丝弓: 蜘蛛网x8 + 弓x1
        NamespacedKey webBowKey = new NamespacedKey(this, "ArmsorPlus_WebBow");
        ShapedRecipe webBowRecipe = new ShapedRecipe(webBowKey, WebBow(1));
        webBowRecipe.shape("CCC", "CBC", "CCC");
        webBowRecipe.setIngredient('C', COBWEB);
        webBowRecipe.setIngredient('B', BOW);
        getServer().addRecipe(webBowRecipe);
        count++;

        // 爆炸弓: TNTx8 + 弓x1
        NamespacedKey explosionBowKey = new NamespacedKey(this, "ArmsorPlus_ExplosionBow");
        ShapedRecipe explosionBowRecipe = new ShapedRecipe(explosionBowKey, ExplosionBow(1));
        explosionBowRecipe.shape("TTT", "TBT", "TTT");
        explosionBowRecipe.setIngredient('T', TNT);
        explosionBowRecipe.setIngredient('B', BOW);
        getServer().addRecipe(explosionBowRecipe);
        count++;

        // 瞬步刃: 末影珍珠x4 + 下界合金剑x1
        NamespacedKey flashStepKey = new NamespacedKey(this, "ArmsorPlus_FlashStepBlade");
        ShapedRecipe flashStepRecipe = new ShapedRecipe(flashStepKey, FlashStepBlade(1));
        flashStepRecipe.shape(" N ", "NSN", " N ");
        flashStepRecipe.setIngredient('N', ENDER_PEARL);
        flashStepRecipe.setIngredient('S', NETHERITE_SWORD);
        getServer().addRecipe(flashStepRecipe);
        count++;

        // 冰块: 1个冰 → 4个冰块
        NamespacedKey iceCubeRecipeKey = new NamespacedKey(this, "ArmsorPlus_IceCubeRecipe");
        ShapelessRecipe iceCubeRecipe = new ShapelessRecipe(iceCubeRecipeKey, IceCube(4));
        iceCubeRecipe.addIngredient(1, ICE);
        getServer().addRecipe(iceCubeRecipe);
        count++;

        getLogger().info("ArmsorPlus 配方注册完成 数量: " + count);
    }

    // ===== 配置文件加载 =====

    /** 加载/初始化配置文件，按配置决定是否加载公海世界 */
    private void loadconfig() {
        saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        config.addDefault("SpawnOpenSea", false);
        config.addDefault("AutoResetOpenSeaMap", false);
        config.addDefault("OpenSeaName", "OpenSea");
        config.addDefault("MapZipName", "OpenSea.zip");
        config.options().copyDefaults(true);
        saveConfig();

        if (config.getBoolean("SpawnOpenSea")) {
            getLogger().info("加载公海地图中...");
            String zipName = config.getString("MapZipName");
            if (zipName == null) {
                LoadOpenSea.loadMap(Bukkit.getWorldContainer().toPath(),
                        this.getResource("OpenSea.zip"),
                        config.getBoolean("AutoResetOpenSeaMap"),
                        config.getString("OpenSeaName"),
                        "OpenSea.zip");
            } else {
                LoadOpenSea.loadMap(Bukkit.getWorldContainer().toPath(),
                        this.getResource(zipName),
                        config.getBoolean("AutoResetOpenSeaMap"),
                        config.getString("OpenSeaName"),
                        zipName);
            }
            getLogger().info("加载公海抽奖功能");
            getServer().getPluginManager().registerEvents(new OpenSeaLottery(), this);
            getLogger().info("加载公海地图保护功能");
            getServer().getPluginManager().registerEvents(new OpenSeaDig(), this);
            getLogger().info("加载公海地图生物功能");
            getServer().getPluginManager().registerEvents(new OpenSeaEntity(), this);
        } else {
            getLogger().info("公海地图已关闭");
        }
    }

    // ===== 强化石防放置 =====

    /**
     * 防止玩家将带有强化石/基础强化石标记的物品放置到地上，
     * 这些物品应通过右键使用而非放置。
     */
    @EventHandler
    public void StonePlace(BlockPlaceEvent event) {
        int level = ArmsorEnchant.getEnchantLevel(event.getItemInHand(), BasicStone)
                + ArmsorEnchant.getEnchantLevel(event.getItemInHand(), Armskey)
                + ArmsorEnchant.getEnchantLevel(event.getItemInHand(), Armorkey);
        if (level == 0) return;
        event.setCancelled(true);
    }

    // ===== 基础强化石右键使用 =====

    /**
     * 右键使用基础强化石时随机获得一种强化石。
     * 概率分布: 一级护甲 33% | 二级护甲/一级武器/二级武器/弓 各约17%
     */
    @EventHandler
    public void IfUSEBasicStone(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (ArmsorEnchant.getEnchantLevel(item, BasicStone) == 0) return;

        item.setAmount(item.getAmount() - 1);
        event.setCancelled(true);

        Random r = new Random();
        int i = r.nextInt(6); // 0~5

        if (i == 0 || i == 1) {
            event.getPlayer().getInventory().addItem(ArmorPlusCreate(1));
            event.getPlayer().sendMessage("已获得一级护甲强化石");
        } else if (i == 2) {
            event.getPlayer().getInventory().addItem(ArmorPlusCreateII(1));
            event.getPlayer().sendMessage("已获得二级护甲强化石");
        } else if (i == 3) {
            event.getPlayer().getInventory().addItem(ArmsPlusCreateI(1));
            event.getPlayer().sendMessage("已获得一级武器强化石");
        } else if (i == 4) {
            event.getPlayer().getInventory().addItem(ArmsPlusCreateII(1));
            event.getPlayer().sendMessage("已获得二级武器强化石");
        } else {
            // i == 5
            event.getPlayer().getInventory().addItem(BowPlusCreate(1));
            event.getPlayer().sendMessage("已获得一级弓箭强化石");
        }
    }
}
