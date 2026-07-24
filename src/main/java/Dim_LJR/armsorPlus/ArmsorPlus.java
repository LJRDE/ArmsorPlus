package Dim_LJR.armsorPlus;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorPlusEnchantEventHandler;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.EnhancementHandler;
import Dim_LJR.armsorPlus.Boss.BossMenu;
import Dim_LJR.armsorPlus.Boss.BossWorld;
import Dim_LJR.armsorPlus.Command.ArmsorPlusCommand;
import Dim_LJR.armsorPlus.Food.FoodListeners;
import Dim_LJR.armsorPlus.Food.TreeListeners;
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
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.Food.FoodItems.*;
import static Dim_LJR.armsorPlus.Item.Materials.*;
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
    public JavaPlugin GetJavaPlugin() {
        return this;
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
        getServer().getPluginManager().registerEvents(new TreeListeners(),this);
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

        // 寒冰弓: 极寒冰核x4 + 弓
        NamespacedKey frostBowKey = new NamespacedKey(this, "ArmsorPlus_FrostBow");
        ShapedRecipe frostBowRecipe = new ShapedRecipe(frostBowKey, FrostBow(1));
        frostBowRecipe.shape(" I ", "IBI", " I ");
        frostBowRecipe.setIngredient('I', new RecipeChoice.ExactChoice(IceCore(1)));
        frostBowRecipe.setIngredient('B', BOW);
        getServer().addRecipe(frostBowRecipe);
        count++;

        // 火焰戟: 烈焰原核x3 + 烈焰棒x2
        NamespacedKey halberdKey = new NamespacedKey(this, "ArmsorPlus_FlameHalberd");
        ShapedRecipe halberdRecipe = new ShapedRecipe(halberdKey, FlameHalberd(1));
        halberdRecipe.shape("F F", "BFB", " B ");
        halberdRecipe.setIngredient('F', new RecipeChoice.ExactChoice(FireCore(1)));
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

        // 星痕剑: 紫水晶碎片x4 + 铁剑x1
        NamespacedKey starTraceKey = new NamespacedKey(this, "ArmsorPlus_StarTraceSword");
        ShapedRecipe starTraceRecipe = new ShapedRecipe(starTraceKey, StarTraceSword(1));
        starTraceRecipe.shape(" N ", "NSN", " N ");
        starTraceRecipe.setIngredient('N', AMETHYST_SHARD);
        starTraceRecipe.setIngredient('S', IRON_SWORD);
        getServer().addRecipe(starTraceRecipe);
        count++;

        // 尸王: 僵尸头颅x2 + 骨头x1
        NamespacedKey corpseKey = new NamespacedKey(this, "ArmsorPlus_CorpseKing");
        ShapedRecipe corpseRecipe = new ShapedRecipe(corpseKey, CorpseKing(1));
        corpseRecipe.shape(" Z ", "ZBZ", " Z ");
        corpseRecipe.setIngredient('Z', ZOMBIE_HEAD);
        corpseRecipe.setIngredient('B', BONE);
        getServer().addRecipe(corpseRecipe);
        count++;

        // 生钢: 铁锭x1 + 煤炭x1
        NamespacedKey rawSteelKey = new NamespacedKey(this, "ArmsorPlus_RawSteel");
        ShapelessRecipe rawSteelRecipe = new ShapelessRecipe(rawSteelKey, RawSteel(1));
        rawSteelRecipe.addIngredient(1, IRON_INGOT);
        rawSteelRecipe.addIngredient(1, COAL);
        getServer().addRecipe(rawSteelRecipe);
        count++;

        // 钢: 熔炉烧制生钢
        NamespacedKey steelSmeltKey = new NamespacedKey(this, "ArmsorPlus_SteelSmelt");
        FurnaceRecipe steelSmelt = new FurnaceRecipe(steelSmeltKey, SteelIngot(1),
                new RecipeChoice.ExactChoice(RawSteel(1)), 0.5f, 200);
        getServer().addRecipe(steelSmelt);
        count++;

        // 钢剑: 钢x2 + 木棍
        NamespacedKey steelSwordKey = new NamespacedKey(this, "ArmsorPlus_SteelSword");
        ShapedRecipe steelSwordRecipe = new ShapedRecipe(steelSwordKey, SteelSword(1));
        steelSwordRecipe.shape(" S ", " S ", " B ");
        steelSwordRecipe.setIngredient('S', new RecipeChoice.ExactChoice(SteelIngot(1)));
        steelSwordRecipe.setIngredient('B', STICK);
        getServer().addRecipe(steelSwordRecipe);
        count++;

        // 钢装备
        NamespacedKey[] steelArmorKeys = {
            new NamespacedKey(this, "ArmsorPlus_SteelHelmet"),
            new NamespacedKey(this, "ArmsorPlus_SteelChest"),
            new NamespacedKey(this, "ArmsorPlus_SteelLegs"),
            new NamespacedKey(this, "ArmsorPlus_SteelBoots")
        };
        ItemStack[] steelArmors = {SteelHelmet(1), SteelChestplate(1), SteelLeggings(1), SteelBoots(1)};
        String[][] shapes = {{"SSS","S S"}, {"S S","SSS","SSS"}, {"SSS","S S","S S"}, {"S S","S S"}};
        RecipeChoice steelChoice = new RecipeChoice.ExactChoice(SteelIngot(1));
        for (int i = 0; i < 4; i++) {
            ShapedRecipe r = new ShapedRecipe(steelArmorKeys[i], steelArmors[i]);
            r.shape(shapes[i]);
            r.setIngredient('S', steelChoice);
            getServer().addRecipe(r);
            count++;
        }

        // 玄武剑: 玄武岩x2 + 烈焰棒x1
        NamespacedKey tortoiseKey = new NamespacedKey(this, "ArmsorPlus_BlackTortoiseSword");
        ShapedRecipe tortoiseRecipe = new ShapedRecipe(tortoiseKey, BlackTortoiseSword(1));
        tortoiseRecipe.shape(" B ", "BSB", " B ");
        tortoiseRecipe.setIngredient('B', BASALT);
        tortoiseRecipe.setIngredient('S', BLAZE_ROD);
        getServer().addRecipe(tortoiseRecipe);
        count++;

        // 金盾丹: 8金块 + 1地狱疣 → 1个
        NamespacedKey goldShieldKey = new NamespacedKey(this, "ArmsorPlus_GoldShieldElixir");
        ShapedRecipe goldShieldRecipe = new ShapedRecipe(goldShieldKey, GoldShieldElixir(1));
        goldShieldRecipe.shape("GGG", "GNG", "GGG");
        goldShieldRecipe.setIngredient('G', GOLD_BLOCK);
        goldShieldRecipe.setIngredient('N', NETHER_WART);
        getServer().addRecipe(goldShieldRecipe);
        count++;

        // 冰块: 1个冰 → 4个冰块
        NamespacedKey iceCubeRecipeKey = new NamespacedKey(this, "ArmsorPlus_IceCubeRecipe");
        ShapelessRecipe iceCubeRecipe = new ShapelessRecipe(iceCubeRecipeKey, IceCube(4));
        iceCubeRecipe.addIngredient(1, ICE);
        getServer().addRecipe(iceCubeRecipe);
        count++;

        // ===== 食物配方 =====
        // 汉堡: 面包 + 熟牛肉 + 卷心菜
        NamespacedKey burgerKey = new NamespacedKey(this, "ArmsorPlus_Burger");
        ShapelessRecipe burgerRecipe = new ShapelessRecipe(burgerKey, Burger(1));
        burgerRecipe.addIngredient(1, BREAD);
        burgerRecipe.addIngredient(1, COOKED_BEEF);
        burgerRecipe.addIngredient(new RecipeChoice.ExactChoice(Cabbage(1)));
        getServer().addRecipe(burgerRecipe);
        count++;

        // 热狗: 面包 + 熟猪排
        NamespacedKey hotDogKey = new NamespacedKey(this, "ArmsorPlus_HotDog");
        ShapelessRecipe hotDogRecipe = new ShapelessRecipe(hotDogKey, HotDog(1));
        hotDogRecipe.addIngredient(1, BREAD);
        hotDogRecipe.addIngredient(1, COOKED_PORKCHOP);
        getServer().addRecipe(hotDogRecipe);
        count++;

        // 披萨: 面包 + 西红柿 + 奶酪
        NamespacedKey pizzaKey = new NamespacedKey(this, "ArmsorPlus_Pizza");
        ShapelessRecipe pizzaRecipe = new ShapelessRecipe(pizzaKey, Pizza(1));
        pizzaRecipe.addIngredient(1, BREAD);
        pizzaRecipe.addIngredient(new RecipeChoice.ExactChoice(Tomato(1)));
        pizzaRecipe.addIngredient(new RecipeChoice.ExactChoice(Cheese(1)));
        getServer().addRecipe(pizzaRecipe);
        count++;

        // 薯条: 烤马铃薯 + 盐
        NamespacedKey friesKey = new NamespacedKey(this, "ArmsorPlus_FrenchFries");
        ShapelessRecipe friesRecipe = new ShapelessRecipe(friesKey, FrenchFries(1));
        friesRecipe.addIngredient(1, BAKED_POTATO);
        friesRecipe.addIngredient(new RecipeChoice.ExactChoice(Salt(1)));
        getServer().addRecipe(friesRecipe);
        count++;

        // 甜甜圈: 小麦 + 糖 + 鸡蛋
        NamespacedKey donutKey = new NamespacedKey(this, "ArmsorPlus_Donut");
        ShapelessRecipe donutRecipe = new ShapelessRecipe(donutKey, Donut(1));
        donutRecipe.addIngredient(1, WHEAT);
        donutRecipe.addIngredient(1, SUGAR);
        donutRecipe.addIngredient(1, EGG);
        getServer().addRecipe(donutRecipe);
        count++;

        // 冰淇淋: 雪球 + 糖 + 奶桶
        NamespacedKey iceCreamKey = new NamespacedKey(this, "ArmsorPlus_IceCream");
        ShapelessRecipe iceCreamRecipe = new ShapelessRecipe(iceCreamKey, IceCream(1));
        iceCreamRecipe.addIngredient(1, SNOWBALL);
        iceCreamRecipe.addIngredient(1, SUGAR);
        iceCreamRecipe.addIngredient(1, MILK_BUCKET);
        getServer().addRecipe(iceCreamRecipe);
        count++;

        // 爆米花: 小麦
        NamespacedKey popcornKey = new NamespacedKey(this, "ArmsorPlus_Popcorn");
        ShapelessRecipe popcornRecipe = new ShapelessRecipe(popcornKey, Popcorn(1));
        popcornRecipe.addIngredient(1, WHEAT);
        getServer().addRecipe(popcornRecipe);
        count++;

        // 棉花糖: 糖 + 线
        NamespacedKey cottonCandyKey = new NamespacedKey(this, "ArmsorPlus_CottonCandy");
        ShapelessRecipe cottonCandyRecipe = new ShapelessRecipe(cottonCandyKey, CottonCandy(1));
        cottonCandyRecipe.addIngredient(1, SUGAR);
        cottonCandyRecipe.addIngredient(1, STRING);
        getServer().addRecipe(cottonCandyRecipe);
        count++;

        // 巧克力: 可可豆 + 糖 + 奶桶
        NamespacedKey chocolateKey = new NamespacedKey(this, "ArmsorPlus_Chocolate");
        ShapelessRecipe chocolateRecipe = new ShapelessRecipe(chocolateKey, Chocolate(1));
        chocolateRecipe.addIngredient(1, COCOA_BEANS);
        chocolateRecipe.addIngredient(1, SUGAR);
        chocolateRecipe.addIngredient(1, MILK_BUCKET);
        getServer().addRecipe(chocolateRecipe);
        count++;

        // 寿司: 干海带 + 熟鳕鱼 + 小麦
        NamespacedKey sushiKey = new NamespacedKey(this, "ArmsorPlus_Sushi");
        ShapelessRecipe sushiRecipe = new ShapelessRecipe(sushiKey, Sushi(1));
        sushiRecipe.addIngredient(1, DRIED_KELP);
        sushiRecipe.addIngredient(1, COOKED_COD);
        sushiRecipe.addIngredient(1, WHEAT);
        getServer().addRecipe(sushiRecipe);
        count++;

        // 拉面: 碗 + 小麦 + 干海带 + 鸡蛋
        NamespacedKey ramenKey = new NamespacedKey(this, "ArmsorPlus_Ramen");
        ShapelessRecipe ramenRecipe = new ShapelessRecipe(ramenKey, Ramen(1));
        ramenRecipe.addIngredient(1, BOWL);
        ramenRecipe.addIngredient(1, WHEAT);
        ramenRecipe.addIngredient(1, DRIED_KELP);
        ramenRecipe.addIngredient(1, EGG);
        getServer().addRecipe(ramenRecipe);
        count++;

        // 三明治: 面包 + 熟牛肉 + 卷心菜
        NamespacedKey sandwichKey = new NamespacedKey(this, "ArmsorPlus_Sandwich");
        ShapelessRecipe sandwichRecipe = new ShapelessRecipe(sandwichKey, Sandwich(1));
        sandwichRecipe.addIngredient(1, BREAD);
        sandwichRecipe.addIngredient(1, COOKED_BEEF);
        sandwichRecipe.addIngredient(new RecipeChoice.ExactChoice(Cabbage(1)));
        getServer().addRecipe(sandwichRecipe);
        count++;

        // 鸡腿: 熟鸡肉 + 面包
        NamespacedKey drumstickKey = new NamespacedKey(this, "ArmsorPlus_Drumstick");
        ShapelessRecipe drumstickRecipe = new ShapelessRecipe(drumstickKey, Drumstick(1));
        drumstickRecipe.addIngredient(1, COOKED_CHICKEN);
        drumstickRecipe.addIngredient(1, BREAD);
        getServer().addRecipe(drumstickRecipe);
        count++;

        // 奶酪: 奶桶
        NamespacedKey cheeseKey = new NamespacedKey(this, "ArmsorPlus_Cheese");
        ShapelessRecipe cheeseRecipe = new ShapelessRecipe(cheeseKey, Cheese(1));
        cheeseRecipe.addIngredient(1, MILK_BUCKET);
        getServer().addRecipe(cheeseRecipe);
        count++;

        // 薄饼: 小麦 + 鸡蛋 + 糖 + 奶桶
        NamespacedKey pancakeKey = new NamespacedKey(this, "ArmsorPlus_Pancake");
        ShapelessRecipe pancakeRecipe = new ShapelessRecipe(pancakeKey, Pancake(1));
        pancakeRecipe.addIngredient(1, WHEAT);
        pancakeRecipe.addIngredient(1, EGG);
        pancakeRecipe.addIngredient(1, SUGAR);
        pancakeRecipe.addIngredient(1, MILK_BUCKET);
        getServer().addRecipe(pancakeRecipe);
        count++;

        // 辣椒: 碗 + 红染料 + 熟牛肉
        NamespacedKey chiliKey = new NamespacedKey(this, "ArmsorPlus_Chili");
        ShapelessRecipe chiliRecipe = new ShapelessRecipe(chiliKey, Chili(1));
        chiliRecipe.addIngredient(1, BOWL);
        chiliRecipe.addIngredient(1, RED_DYE);
        chiliRecipe.addIngredient(1, COOKED_BEEF);
        getServer().addRecipe(chiliRecipe);
        count++;

        // 黄油: 奶桶
        NamespacedKey butterKey = new NamespacedKey(this, "ArmsorPlus_Butter");
        ShapelessRecipe butterRecipe = new ShapelessRecipe(butterKey, Butter(1));
        butterRecipe.addIngredient(1, MILK_BUCKET);
        getServer().addRecipe(butterRecipe);
        count++;

        // ===== 鱼骨系列武器配方 =====

        // 鱼骨剑: 2鲑鱼 + 1骨头 (竖排)
        NamespacedKey fishBoneSwordKey = new NamespacedKey(this, "ArmsorPlus_FishBoneSword");
        ShapedRecipe fishBoneSwordRecipe = new ShapedRecipe(fishBoneSwordKey, FishBoneSword(1));
        fishBoneSwordRecipe.shape("A", "B", "C");
        fishBoneSwordRecipe.setIngredient('A', SALMON);
        fishBoneSwordRecipe.setIngredient('B', SALMON);
        fishBoneSwordRecipe.setIngredient('C', BONE);
        getServer().addRecipe(fishBoneSwordRecipe);
        count++;

        // 鱼骨刀: 2鳕鱼 + 1骨头 (竖排)
        NamespacedKey fishBoneKnifeKey = new NamespacedKey(this, "ArmsorPlus_FishBoneKnife");
        ShapedRecipe fishBoneKnifeRecipe = new ShapedRecipe(fishBoneKnifeKey, FishBoneKnife(1));
        fishBoneKnifeRecipe.shape("A", "B", "C");
        fishBoneKnifeRecipe.setIngredient('A', COD);
        fishBoneKnifeRecipe.setIngredient('B', COD);
        fishBoneKnifeRecipe.setIngredient('C', BONE);
        getServer().addRecipe(fishBoneKnifeRecipe);
        count++;

        // 鱼刺剑: 4骨块+4海晶沙粒+鱼骨剑(中心)
        NamespacedKey fishSpineSwordKey = new NamespacedKey(this, "ArmsorPlus_FishSpineSword");
        ShapedRecipe fishSpineSwordRecipe = new ShapedRecipe(fishSpineSwordKey, FishSpineSword(1));
        fishSpineSwordRecipe.shape("ABC", "DEF", "GHI");
        fishSpineSwordRecipe.setIngredient('A', BONE_BLOCK);
        fishSpineSwordRecipe.setIngredient('B', PRISMARINE_SHARD);
        fishSpineSwordRecipe.setIngredient('C', BONE_BLOCK);
        fishSpineSwordRecipe.setIngredient('D', PRISMARINE_SHARD);
        fishSpineSwordRecipe.setIngredient('E', new RecipeChoice.ExactChoice(FishBoneSword(1)));
        fishSpineSwordRecipe.setIngredient('F', PRISMARINE_SHARD);
        fishSpineSwordRecipe.setIngredient('G', BONE_BLOCK);
        fishSpineSwordRecipe.setIngredient('H', PRISMARINE_SHARD);
        fishSpineSwordRecipe.setIngredient('I', BONE_BLOCK);
        getServer().addRecipe(fishSpineSwordRecipe);
        count++;

        // 鱼刺刀: 4骨块+4海晶沙粒+鱼骨刀(中心)
        NamespacedKey fishSpineKnifeKey = new NamespacedKey(this, "ArmsorPlus_FishSpineKnife");
        ShapedRecipe fishSpineKnifeRecipe = new ShapedRecipe(fishSpineKnifeKey, FishSpineKnife(1));
        fishSpineKnifeRecipe.shape("ABC", "DEF", "GHI");
        fishSpineKnifeRecipe.setIngredient('A', BONE_BLOCK);
        fishSpineKnifeRecipe.setIngredient('B', PRISMARINE_SHARD);
        fishSpineKnifeRecipe.setIngredient('C', BONE_BLOCK);
        fishSpineKnifeRecipe.setIngredient('D', PRISMARINE_SHARD);
        fishSpineKnifeRecipe.setIngredient('E', new RecipeChoice.ExactChoice(FishBoneKnife(1)));
        fishSpineKnifeRecipe.setIngredient('F', PRISMARINE_SHARD);
        fishSpineKnifeRecipe.setIngredient('G', BONE_BLOCK);
        fishSpineKnifeRecipe.setIngredient('H', PRISMARINE_SHARD);
        fishSpineKnifeRecipe.setIngredient('I', BONE_BLOCK);
        getServer().addRecipe(fishSpineKnifeRecipe);
        count++;

        // 海骨剑: 4海绵+4海晶灯+鱼刺剑(中心)
        NamespacedKey seaBoneSwordKey = new NamespacedKey(this, "ArmsorPlus_SeaBoneSword");
        ShapedRecipe seaBoneSwordRecipe = new ShapedRecipe(seaBoneSwordKey, SeaBoneSword(1));
        seaBoneSwordRecipe.shape("ABC", "DEF", "GHI");
        seaBoneSwordRecipe.setIngredient('A', SPONGE);
        seaBoneSwordRecipe.setIngredient('B', SEA_LANTERN);
        seaBoneSwordRecipe.setIngredient('C', SPONGE);
        seaBoneSwordRecipe.setIngredient('D', SEA_LANTERN);
        seaBoneSwordRecipe.setIngredient('E', new RecipeChoice.ExactChoice(FishSpineSword(1)));
        seaBoneSwordRecipe.setIngredient('F', SEA_LANTERN);
        seaBoneSwordRecipe.setIngredient('G', SPONGE);
        seaBoneSwordRecipe.setIngredient('H', SEA_LANTERN);
        seaBoneSwordRecipe.setIngredient('I', SPONGE);
        getServer().addRecipe(seaBoneSwordRecipe);
        count++;

        // 海骨刀: 4海绵+4海晶灯+鱼刺刀(中心)
        NamespacedKey seaBoneKnifeKey = new NamespacedKey(this, "ArmsorPlus_SeaBoneKnife");
        ShapedRecipe seaBoneKnifeRecipe = new ShapedRecipe(seaBoneKnifeKey, SeaBoneKnife(1));
        seaBoneKnifeRecipe.shape("ABC", "DEF", "GHI");
        seaBoneKnifeRecipe.setIngredient('A', SPONGE);
        seaBoneKnifeRecipe.setIngredient('B', SEA_LANTERN);
        seaBoneKnifeRecipe.setIngredient('C', SPONGE);
        seaBoneKnifeRecipe.setIngredient('D', SEA_LANTERN);
        seaBoneKnifeRecipe.setIngredient('E', new RecipeChoice.ExactChoice(FishSpineKnife(1)));
        seaBoneKnifeRecipe.setIngredient('F', SEA_LANTERN);
        seaBoneKnifeRecipe.setIngredient('G', SPONGE);
        seaBoneKnifeRecipe.setIngredient('H', SEA_LANTERN);
        seaBoneKnifeRecipe.setIngredient('I', SPONGE);
        getServer().addRecipe(seaBoneKnifeRecipe);
        count++;

        // 灵骨剑: 8灵魂沙+海骨剑(中心)
        NamespacedKey spiritBoneSwordKey = new NamespacedKey(this, "ArmsorPlus_SpiritBoneSword");
        ShapedRecipe spiritBoneSwordRecipe = new ShapedRecipe(spiritBoneSwordKey, SpiritBoneSword(1));
        spiritBoneSwordRecipe.shape("ABC", "DEF", "GHI");
        spiritBoneSwordRecipe.setIngredient('A', SOUL_SAND);
        spiritBoneSwordRecipe.setIngredient('B', SOUL_SAND);
        spiritBoneSwordRecipe.setIngredient('C', SOUL_SAND);
        spiritBoneSwordRecipe.setIngredient('D', SOUL_SAND);
        spiritBoneSwordRecipe.setIngredient('E', new RecipeChoice.ExactChoice(SeaBoneSword(1)));
        spiritBoneSwordRecipe.setIngredient('F', SOUL_SAND);
        spiritBoneSwordRecipe.setIngredient('G', SOUL_SAND);
        spiritBoneSwordRecipe.setIngredient('H', SOUL_SAND);
        spiritBoneSwordRecipe.setIngredient('I', SOUL_SAND);
        getServer().addRecipe(spiritBoneSwordRecipe);
        count++;

        // 灵骨刀: 8灵魂沙+海骨刀(中心)
        NamespacedKey spiritBoneKnifeKey = new NamespacedKey(this, "ArmsorPlus_SpiritBoneKnife");
        ShapedRecipe spiritBoneKnifeRecipe = new ShapedRecipe(spiritBoneKnifeKey, SpiritBoneKnife(1));
        spiritBoneKnifeRecipe.shape("ABC", "DEF", "GHI");
        spiritBoneKnifeRecipe.setIngredient('A', SOUL_SAND);
        spiritBoneKnifeRecipe.setIngredient('B', SOUL_SAND);
        spiritBoneKnifeRecipe.setIngredient('C', SOUL_SAND);
        spiritBoneKnifeRecipe.setIngredient('D', SOUL_SAND);
        spiritBoneKnifeRecipe.setIngredient('E', new RecipeChoice.ExactChoice(SeaBoneKnife(1)));
        spiritBoneKnifeRecipe.setIngredient('F', SOUL_SAND);
        spiritBoneKnifeRecipe.setIngredient('G', SOUL_SAND);
        spiritBoneKnifeRecipe.setIngredient('H', SOUL_SAND);
        spiritBoneKnifeRecipe.setIngredient('I', SOUL_SAND);
        getServer().addRecipe(spiritBoneKnifeRecipe);
        count++;

        // 海刺剑: 4鳞甲+4海洋之心+灵骨剑(中心)
        NamespacedKey seaSpineSwordKey = new NamespacedKey(this, "ArmsorPlus_SeaSpineSword");
        ShapedRecipe seaSpineSwordRecipe = new ShapedRecipe(seaSpineSwordKey, SeaSpineSword(1));
        seaSpineSwordRecipe.shape("ABC", "DEF", "GHI");
        seaSpineSwordRecipe.setIngredient('A', TURTLE_SCUTE);
        seaSpineSwordRecipe.setIngredient('B', HEART_OF_THE_SEA);
        seaSpineSwordRecipe.setIngredient('C', TURTLE_SCUTE);
        seaSpineSwordRecipe.setIngredient('D', HEART_OF_THE_SEA);
        seaSpineSwordRecipe.setIngredient('E', new RecipeChoice.ExactChoice(SpiritBoneSword(1)));
        seaSpineSwordRecipe.setIngredient('F', HEART_OF_THE_SEA);
        seaSpineSwordRecipe.setIngredient('G', TURTLE_SCUTE);
        seaSpineSwordRecipe.setIngredient('H', HEART_OF_THE_SEA);
        seaSpineSwordRecipe.setIngredient('I', TURTLE_SCUTE);
        getServer().addRecipe(seaSpineSwordRecipe);
        count++;

        // 海刺刀: 4鳞甲+4海洋之心+灵骨刀(中心)
        NamespacedKey seaSpineKnifeKey = new NamespacedKey(this, "ArmsorPlus_SeaSpineKnife");
        ShapedRecipe seaSpineKnifeRecipe = new ShapedRecipe(seaSpineKnifeKey, SeaSpineKnife(1));
        seaSpineKnifeRecipe.shape("ABC", "DEF", "GHI");
        seaSpineKnifeRecipe.setIngredient('A', TURTLE_SCUTE);
        seaSpineKnifeRecipe.setIngredient('B', HEART_OF_THE_SEA);
        seaSpineKnifeRecipe.setIngredient('C', TURTLE_SCUTE);
        seaSpineKnifeRecipe.setIngredient('D', HEART_OF_THE_SEA);
        seaSpineKnifeRecipe.setIngredient('E', new RecipeChoice.ExactChoice(SpiritBoneKnife(1)));
        seaSpineKnifeRecipe.setIngredient('F', HEART_OF_THE_SEA);
        seaSpineKnifeRecipe.setIngredient('G', TURTLE_SCUTE);
        seaSpineKnifeRecipe.setIngredient('H', HEART_OF_THE_SEA);
        seaSpineKnifeRecipe.setIngredient('I', TURTLE_SCUTE);
        getServer().addRecipe(seaSpineKnifeRecipe);
        count++;

        // 蚀骨剑: 7灵魂土+1凋零骷髅头+灵骨剑(中心)
        NamespacedKey corrodeBoneSwordKey = new NamespacedKey(this, "ArmsorPlus_CorrodeBoneSword");
        ShapedRecipe corrodeBoneSwordRecipe = new ShapedRecipe(corrodeBoneSwordKey, CorrodeBoneSword(1));
        corrodeBoneSwordRecipe.shape("ABC", "DEF", "GHI");
        corrodeBoneSwordRecipe.setIngredient('A', SOUL_SOIL);
        corrodeBoneSwordRecipe.setIngredient('B', WITHER_SKELETON_SKULL);
        corrodeBoneSwordRecipe.setIngredient('C', SOUL_SOIL);
        corrodeBoneSwordRecipe.setIngredient('D', SOUL_SOIL);
        corrodeBoneSwordRecipe.setIngredient('E', new RecipeChoice.ExactChoice(SpiritBoneSword(1)));
        corrodeBoneSwordRecipe.setIngredient('F', SOUL_SOIL);
        corrodeBoneSwordRecipe.setIngredient('G', SOUL_SOIL);
        corrodeBoneSwordRecipe.setIngredient('H', SOUL_SOIL);
        corrodeBoneSwordRecipe.setIngredient('I', SOUL_SOIL);
        getServer().addRecipe(corrodeBoneSwordRecipe);
        count++;

        // 灵刺剑: 4潮涌核心+4恶魂之泪+海刺剑(中心)
        NamespacedKey spiritSpineSwordKey = new NamespacedKey(this, "ArmsorPlus_SpiritSpineSword");
        ShapedRecipe spiritSpineSwordRecipe = new ShapedRecipe(spiritSpineSwordKey, SpiritSpineSword(1));
        spiritSpineSwordRecipe.shape("ABC", "DEF", "GHI");
        spiritSpineSwordRecipe.setIngredient('A', CONDUIT);
        spiritSpineSwordRecipe.setIngredient('B', GHAST_TEAR);
        spiritSpineSwordRecipe.setIngredient('C', CONDUIT);
        spiritSpineSwordRecipe.setIngredient('D', GHAST_TEAR);
        spiritSpineSwordRecipe.setIngredient('E', new RecipeChoice.ExactChoice(SeaSpineSword(1)));
        spiritSpineSwordRecipe.setIngredient('F', GHAST_TEAR);
        spiritSpineSwordRecipe.setIngredient('G', CONDUIT);
        spiritSpineSwordRecipe.setIngredient('H', GHAST_TEAR);
        spiritSpineSwordRecipe.setIngredient('I', CONDUIT);
        getServer().addRecipe(spiritSpineSwordRecipe);
        count++;

        // 灵刺刀: 4潮涌核心+4凋零骷髅头+海刺刀(中心)
        NamespacedKey spiritSpineKnifeKey = new NamespacedKey(this, "ArmsorPlus_SpiritSpineKnife");
        ShapedRecipe spiritSpineKnifeRecipe = new ShapedRecipe(spiritSpineKnifeKey, SpiritSpineKnife(1));
        spiritSpineKnifeRecipe.shape("ABC", "DEF", "GHI");
        spiritSpineKnifeRecipe.setIngredient('A', CONDUIT);
        spiritSpineKnifeRecipe.setIngredient('B', WITHER_SKELETON_SKULL);
        spiritSpineKnifeRecipe.setIngredient('C', CONDUIT);
        spiritSpineKnifeRecipe.setIngredient('D', WITHER_SKELETON_SKULL);
        spiritSpineKnifeRecipe.setIngredient('E', new RecipeChoice.ExactChoice(SeaSpineKnife(1)));
        spiritSpineKnifeRecipe.setIngredient('F', WITHER_SKELETON_SKULL);
        spiritSpineKnifeRecipe.setIngredient('G', CONDUIT);
        spiritSpineKnifeRecipe.setIngredient('H', WITHER_SKELETON_SKULL);
        spiritSpineKnifeRecipe.setIngredient('I', CONDUIT);
        getServer().addRecipe(spiritSpineKnifeRecipe);
        count++;

        // 海哭剑: 4下界之星+4潮涌核心+灵刺剑(中心)
        NamespacedKey seaCrySwordKey = new NamespacedKey(this, "ArmsorPlus_SeaCrySword");
        ShapedRecipe seaCrySwordRecipe = new ShapedRecipe(seaCrySwordKey, SeaCrySword(1));
        seaCrySwordRecipe.shape("ABC", "DEF", "GHI");
        seaCrySwordRecipe.setIngredient('A', NETHER_STAR);
        seaCrySwordRecipe.setIngredient('B', CONDUIT);
        seaCrySwordRecipe.setIngredient('C', NETHER_STAR);
        seaCrySwordRecipe.setIngredient('D', CONDUIT);
        seaCrySwordRecipe.setIngredient('E', new RecipeChoice.ExactChoice(SpiritSpineSword(1)));
        seaCrySwordRecipe.setIngredient('F', CONDUIT);
        seaCrySwordRecipe.setIngredient('G', NETHER_STAR);
        seaCrySwordRecipe.setIngredient('H', CONDUIT);
        seaCrySwordRecipe.setIngredient('I', NETHER_STAR);
        getServer().addRecipe(seaCrySwordRecipe);
        count++;

        // 海哭刀: 4下界之星+4潮涌核心+灵刺刀(中心)
        NamespacedKey seaCryKnifeKey = new NamespacedKey(this, "ArmsorPlus_SeaCryKnife");
        ShapedRecipe seaCryKnifeRecipe = new ShapedRecipe(seaCryKnifeKey, SeaCryKnife(1));
        seaCryKnifeRecipe.shape("ABC", "DEF", "GHI");
        seaCryKnifeRecipe.setIngredient('A', NETHER_STAR);
        seaCryKnifeRecipe.setIngredient('B', CONDUIT);
        seaCryKnifeRecipe.setIngredient('C', NETHER_STAR);
        seaCryKnifeRecipe.setIngredient('D', CONDUIT);
        seaCryKnifeRecipe.setIngredient('E', new RecipeChoice.ExactChoice(SpiritSpineKnife(1)));
        seaCryKnifeRecipe.setIngredient('F', CONDUIT);
        seaCryKnifeRecipe.setIngredient('G', NETHER_STAR);
        seaCryKnifeRecipe.setIngredient('H', CONDUIT);
        seaCryKnifeRecipe.setIngredient('I', NETHER_STAR);
        getServer().addRecipe(seaCryKnifeRecipe);
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
