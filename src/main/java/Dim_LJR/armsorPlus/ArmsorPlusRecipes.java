package Dim_LJR.armsorPlus;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.Food.FoodItems.*;
import static Dim_LJR.armsorPlus.Item.Materials.*;
import static org.bukkit.Material.*;

// 所有自定义合成配方的注册入口。
// 由 ArmsorPlus.onEnable() 调用, 集中管理全部原版合成配方。
public class ArmsorPlusRecipes {

    // 注册所有自定义合成配方 (由主插件 onEnable 调用)
    public static void register(JavaPlugin plugin) {
        int count = 0;

        // 精炼金刚石: 9个钻石块 → 1个
        NamespacedKey diamondPlusKey = new NamespacedKey(plugin, "ArmsorPlus_DiamondPlus");
        ShapelessRecipe diamondPlusRecipe = new ShapelessRecipe(diamondPlusKey, DIAMONDPLUSCreate(1));
        diamondPlusRecipe.addIngredient(9, DIAMOND_BLOCK);
        plugin.getServer().addRecipe(diamondPlusRecipe);
        count++;

        // 血祭之剑: 红石围骷髅头
        NamespacedKey bloodSwordKey = new NamespacedKey(plugin, "ArmsorPlus_BloodSword");
        ShapedRecipe bloodSwordRecipe = new ShapedRecipe(bloodSwordKey, BloodSword(1));
        bloodSwordRecipe.shape(" A ", "ABA", " A ");
        bloodSwordRecipe.setIngredient('A', REDSTONE);
        bloodSwordRecipe.setIngredient('B', SKELETON_SKULL);
        plugin.getServer().addRecipe(bloodSwordRecipe);
        count++;

        // 重剑: 铁块夹木棍
        NamespacedKey epeeKey = new NamespacedKey(plugin, "ArmsorPlus_EpeeKey");
        ShapedRecipe epeeRecipe = new ShapedRecipe(epeeKey, Iron_Epee(1));
        epeeRecipe.shape(" A ", " A ", " B ");
        epeeRecipe.setIngredient('A', IRON_BLOCK);
        epeeRecipe.setIngredient('B', STICK);
        plugin.getServer().addRecipe(epeeRecipe);
        count++;

        // 向导书: 圆石
        NamespacedKey guideKey = new NamespacedKey(plugin, "GuideRecipe");
        plugin.getServer().addRecipe(new ShapelessRecipe(guideKey, GuideBook(1)).addIngredient(1, COBBLESTONE));
        count++;

        // 基础强化石: 4个钻石块
        NamespacedKey stoneKey = new NamespacedKey(plugin, "StoneRecipe");
        plugin.getServer().addRecipe(new ShapelessRecipe(stoneKey, BasicStone(1)).addIngredient(4, DIAMOND_BLOCK));
        count++;

        // 匕首: 绿宝石 + 木棍
        NamespacedKey daggerKey = new NamespacedKey(plugin, "ArmsorPlus_Dagger");
        ShapedRecipe daggerRecipe = new ShapedRecipe(daggerKey, Dagger(1));
        daggerRecipe.shape(" E ", " S ", "   ");
        daggerRecipe.setIngredient('E', EMERALD);
        daggerRecipe.setIngredient('S', STICK);
        plugin.getServer().addRecipe(daggerRecipe);
        count++;

        // 飞斧: 2下界合金锭 + 绿宝石块 + 2木棍
        NamespacedKey axeKey = new NamespacedKey(plugin, "ArmsorPlus_ThrowingAxe");
        ShapedRecipe axeRecipe = new ShapedRecipe(axeKey, ThrowingAxe(1));
        axeRecipe.shape("N N", " E ", "S S");
        axeRecipe.setIngredient('N', NETHERITE_INGOT);
        axeRecipe.setIngredient('E', EMERALD_BLOCK);
        axeRecipe.setIngredient('S', STICK);
        plugin.getServer().addRecipe(axeRecipe);
        count++;

        // 骷髅权杖: 8骷髅头颅 + 不死图腾
        NamespacedKey scepterKey = new NamespacedKey(plugin, "ArmsorPlus_Scepter");
        ShapedRecipe scepterRecipe = new ShapedRecipe(scepterKey, SkeletonScepter(1));
        scepterRecipe.shape("SSS", "STS", "SSS");
        scepterRecipe.setIngredient('S', SKELETON_SKULL);
        scepterRecipe.setIngredient('T', TOTEM_OF_UNDYING);
        plugin.getServer().addRecipe(scepterRecipe);
        count++;

        // 寒冰弓: 极寒冰核x4 + 弓
        NamespacedKey frostBowKey = new NamespacedKey(plugin, "ArmsorPlus_FrostBow");
        ShapedRecipe frostBowRecipe = new ShapedRecipe(frostBowKey, FrostBow(1));
        frostBowRecipe.shape(" I ", "IBI", " I ");
        frostBowRecipe.setIngredient('I', new RecipeChoice.ExactChoice(IceCore(1)));
        frostBowRecipe.setIngredient('B', BOW);
        plugin.getServer().addRecipe(frostBowRecipe);
        count++;

        // 火焰戟: 烈焰原核x3 + 烈焰棒x2
        NamespacedKey halberdKey = new NamespacedKey(plugin, "ArmsorPlus_FlameHalberd");
        ShapedRecipe halberdRecipe = new ShapedRecipe(halberdKey, FlameHalberd(1));
        halberdRecipe.shape("F F", "BFB", " B ");
        halberdRecipe.setIngredient('F', new RecipeChoice.ExactChoice(FireCore(1)));
        halberdRecipe.setIngredient('B', BLAZE_ROD);
        plugin.getServer().addRecipe(halberdRecipe);
        count++;

        // 回春散: 8闪烁的西瓜片 + 玻璃瓶
        NamespacedKey rejuvenPowderKey = new NamespacedKey(plugin, "ArmsorPlus_RejuvenPowder");
        ShapelessRecipe rejuvenPowderRecipe = new ShapelessRecipe(rejuvenPowderKey, RejuvenationPowder(1));
        rejuvenPowderRecipe.addIngredient(8, GLISTERING_MELON_SLICE);
        rejuvenPowderRecipe.addIngredient(1, GLASS_BOTTLE);
        plugin.getServer().addRecipe(rejuvenPowderRecipe);
        count++;

        // 止血绷带: 2地狱疣 + 白色羊毛
        NamespacedKey bandageKey = new NamespacedKey(plugin, "ArmsorPlus_Bandage");
        ShapedRecipe bandageRecipe = new ShapedRecipe(bandageKey, HemostaticBandage(1));
        bandageRecipe.shape(" N ", "N W", "   ");
        bandageRecipe.setIngredient('N', NETHER_WART);
        bandageRecipe.setIngredient('W', WHITE_WOOL);
        plugin.getServer().addRecipe(bandageRecipe);
        count++;

        // 压缩饼干: 9面包
        NamespacedKey biscuitKey = new NamespacedKey(plugin, "ArmsorPlus_Biscuit");
        ShapedRecipe biscuitRecipe = new ShapedRecipe(biscuitKey, CompressedBiscuit(1));
        biscuitRecipe.shape("BBB", "BBB", "BBB");
        biscuitRecipe.setIngredient('B', BREAD);
        plugin.getServer().addRecipe(biscuitRecipe);
        count++;

        // 盐: 沙子x1
        NamespacedKey saltKey = new NamespacedKey(plugin, "ArmsorPlus_Salt");
        plugin.getServer().addRecipe(new ShapelessRecipe(saltKey, Salt(1)).addIngredient(1, SAND));
        count++;

        // 肉干: 盐x1 + 任意熟肉 (猪肉/羊肉单独配方)
        Material[] meats = {COOKED_BEEF, COOKED_CHICKEN, COOKED_RABBIT, COOKED_COD, COOKED_SALMON};
        for (int i = 0; i < meats.length; i++) {
            NamespacedKey jerkyKey = new NamespacedKey(plugin, "ArmsorPlus_Jerky_" + i);
            ShapelessRecipe jerkyRecipe = new ShapelessRecipe(jerkyKey, Jerky(1));
            jerkyRecipe.addIngredient(new RecipeChoice.ExactChoice(Salt(1)));
            jerkyRecipe.addIngredient(1, meats[i]);
            plugin.getServer().addRecipe(jerkyRecipe);
            count++;
        }

        // 猪肉干: 盐x1 + 熟猪排x1
        NamespacedKey porkJerkyKey = new NamespacedKey(plugin, "ArmsorPlus_PorkJerky");
        ShapelessRecipe porkJerkyRecipe = new ShapelessRecipe(porkJerkyKey, PorkJerky(1));
        porkJerkyRecipe.addIngredient(new RecipeChoice.ExactChoice(Salt(1)));
        porkJerkyRecipe.addIngredient(1, COOKED_PORKCHOP);
        plugin.getServer().addRecipe(porkJerkyRecipe);
        count++;

        // 羊肉干: 盐x1 + 熟羊肉x1
        NamespacedKey muttonJerkyKey = new NamespacedKey(plugin, "ArmsorPlus_MuttonJerky");
        ShapelessRecipe muttonJerkyRecipe = new ShapelessRecipe(muttonJerkyKey, MuttonJerky(1));
        muttonJerkyRecipe.addIngredient(new RecipeChoice.ExactChoice(Salt(1)));
        muttonJerkyRecipe.addIngredient(1, COOKED_MUTTON);
        plugin.getServer().addRecipe(muttonJerkyRecipe);
        count++;

        // 甜浆果派: 甜浆果x3 + 小麦x3
        NamespacedKey pieKey = new NamespacedKey(plugin, "ArmsorPlus_SweetBerryPie");
        ShapelessRecipe pieRecipe = new ShapelessRecipe(pieKey, SweetBerryPie(1));
        pieRecipe.addIngredient(3, SWEET_BERRIES);
        pieRecipe.addIngredient(3, WHEAT);
        plugin.getServer().addRecipe(pieRecipe);
        count++;

        // 酒桶: 小麦x3 + 桶x1
        NamespacedKey wineBarrelKey = new NamespacedKey(plugin, "ArmsorPlus_WineBarrel");
        ShapedRecipe wineBarrelRecipe = new ShapedRecipe(wineBarrelKey, WineBarrel(1));
        wineBarrelRecipe.shape("WWW", " B ", "   ");
        wineBarrelRecipe.setIngredient('W', WHEAT);
        wineBarrelRecipe.setIngredient('B', BARREL);
        plugin.getServer().addRecipe(wineBarrelRecipe);
        count++;

        // 腐肉干: 腐肉x1 + 盐x1
        NamespacedKey rottenJerkyKey = new NamespacedKey(plugin, "ArmsorPlus_RottenJerky");
        ShapelessRecipe rottenJerkyRecipe = new ShapelessRecipe(rottenJerkyKey, RottenJerky(1));
        rottenJerkyRecipe.addIngredient(1, ROTTEN_FLESH);
        rottenJerkyRecipe.addIngredient(new RecipeChoice.ExactChoice(Salt(1)));
        plugin.getServer().addRecipe(rottenJerkyRecipe);
        count++;

        // 雨御前: 海晶碎片x4 + 钻石剑x1
        NamespacedKey rainSwordKey = new NamespacedKey(plugin, "ArmsorPlus_RainSword");
        ShapedRecipe rainSwordRecipe = new ShapedRecipe(rainSwordKey, RainSword(1));
        rainSwordRecipe.shape(" N ", "NSN", " N ");
        rainSwordRecipe.setIngredient('N', PRISMARINE_SHARD);
        rainSwordRecipe.setIngredient('S', DIAMOND_SWORD);
        plugin.getServer().addRecipe(rainSwordRecipe);
        count++;

        // 飞天御剑: 幻翼膜x4 + 金剑x1
        NamespacedKey flyingSwordKey = new NamespacedKey(plugin, "ArmsorPlus_FlyingSword");
        ShapedRecipe flyingSwordRecipe = new ShapedRecipe(flyingSwordKey, FlyingSword(1));
        flyingSwordRecipe.shape(" N ", "NSN", " N ");
        flyingSwordRecipe.setIngredient('N', PHANTOM_MEMBRANE);
        flyingSwordRecipe.setIngredient('S', GOLDEN_SWORD);
        plugin.getServer().addRecipe(flyingSwordRecipe);
        count++;

        // 寒冰剑: 蓝冰x8 + 钻石剑x1
        NamespacedKey iceSwordKey = new NamespacedKey(plugin, "ArmsorPlus_IceSword");
        ShapedRecipe iceSwordRecipe = new ShapedRecipe(iceSwordKey, IceSword(1));
        iceSwordRecipe.shape("BBB", "BSB", "BBB");
        iceSwordRecipe.setIngredient('B', BLUE_ICE);
        iceSwordRecipe.setIngredient('S', DIAMOND_SWORD);
        plugin.getServer().addRecipe(iceSwordRecipe);
        count++;

        // 盘丝弓: 蜘蛛网x8 + 弓x1
        NamespacedKey webBowKey = new NamespacedKey(plugin, "ArmsorPlus_WebBow");
        ShapedRecipe webBowRecipe = new ShapedRecipe(webBowKey, WebBow(1));
        webBowRecipe.shape("CCC", "CBC", "CCC");
        webBowRecipe.setIngredient('C', COBWEB);
        webBowRecipe.setIngredient('B', BOW);
        plugin.getServer().addRecipe(webBowRecipe);
        count++;

        // 爆炸弓: TNTx8 + 弓x1
        NamespacedKey explosionBowKey = new NamespacedKey(plugin, "ArmsorPlus_ExplosionBow");
        ShapedRecipe explosionBowRecipe = new ShapedRecipe(explosionBowKey, ExplosionBow(1));
        explosionBowRecipe.shape("TTT", "TBT", "TTT");
        explosionBowRecipe.setIngredient('T', TNT);
        explosionBowRecipe.setIngredient('B', BOW);
        plugin.getServer().addRecipe(explosionBowRecipe);
        count++;

        // 瞬步刃: 末影珍珠x4 + 下界合金剑x1
        NamespacedKey flashStepKey = new NamespacedKey(plugin, "ArmsorPlus_FlashStepBlade");
        ShapedRecipe flashStepRecipe = new ShapedRecipe(flashStepKey, FlashStepBlade(1));
        flashStepRecipe.shape(" N ", "NSN", " N ");
        flashStepRecipe.setIngredient('N', ENDER_PEARL);
        flashStepRecipe.setIngredient('S', NETHERITE_SWORD);
        plugin.getServer().addRecipe(flashStepRecipe);
        count++;

        // 星痕剑: 紫水晶碎片x4 + 铁剑x1
        NamespacedKey starTraceKey = new NamespacedKey(plugin, "ArmsorPlus_StarTraceSword");
        ShapedRecipe starTraceRecipe = new ShapedRecipe(starTraceKey, StarTraceSword(1));
        starTraceRecipe.shape(" N ", "NSN", " N ");
        starTraceRecipe.setIngredient('N', AMETHYST_SHARD);
        starTraceRecipe.setIngredient('S', IRON_SWORD);
        plugin.getServer().addRecipe(starTraceRecipe);
        count++;

        // 尸王: 僵尸头颅x2 + 骨头x1
        NamespacedKey corpseKey = new NamespacedKey(plugin, "ArmsorPlus_CorpseKing");
        ShapedRecipe corpseRecipe = new ShapedRecipe(corpseKey, CorpseKing(1));
        corpseRecipe.shape(" Z ", "ZBZ", " Z ");
        corpseRecipe.setIngredient('Z', ZOMBIE_HEAD);
        corpseRecipe.setIngredient('B', BONE);
        plugin.getServer().addRecipe(corpseRecipe);
        count++;

        // 生钢: 铁锭x1 + 煤炭x1
        NamespacedKey rawSteelKey = new NamespacedKey(plugin, "ArmsorPlus_RawSteel");
        ShapelessRecipe rawSteelRecipe = new ShapelessRecipe(rawSteelKey, RawSteel(1));
        rawSteelRecipe.addIngredient(1, IRON_INGOT);
        rawSteelRecipe.addIngredient(1, COAL);
        plugin.getServer().addRecipe(rawSteelRecipe);
        count++;

        // 钢: 熔炉烧制生钢
        NamespacedKey steelSmeltKey = new NamespacedKey(plugin, "ArmsorPlus_SteelSmelt");
        FurnaceRecipe steelSmelt = new FurnaceRecipe(steelSmeltKey, SteelIngot(1),
                new RecipeChoice.ExactChoice(RawSteel(1)), 0.5f, 200);
        plugin.getServer().addRecipe(steelSmelt);
        count++;

        // 钢剑: 钢x2 + 木棍
        NamespacedKey steelSwordKey = new NamespacedKey(plugin, "ArmsorPlus_SteelSword");
        ShapedRecipe steelSwordRecipe = new ShapedRecipe(steelSwordKey, SteelSword(1));
        steelSwordRecipe.shape(" S ", " S ", " B ");
        steelSwordRecipe.setIngredient('S', new RecipeChoice.ExactChoice(SteelIngot(1)));
        steelSwordRecipe.setIngredient('B', STICK);
        plugin.getServer().addRecipe(steelSwordRecipe);
        count++;

        // 钢装备
        NamespacedKey[] steelArmorKeys = {
            new NamespacedKey(plugin, "ArmsorPlus_SteelHelmet"),
            new NamespacedKey(plugin, "ArmsorPlus_SteelChest"),
            new NamespacedKey(plugin, "ArmsorPlus_SteelLegs"),
            new NamespacedKey(plugin, "ArmsorPlus_SteelBoots")
        };
        ItemStack[] steelArmors = {SteelHelmet(1), SteelChestplate(1), SteelLeggings(1), SteelBoots(1)};
        String[][] shapes = {{"SSS","S S"}, {"S S","SSS","SSS"}, {"SSS","S S","S S"}, {"S S","S S"}};
        RecipeChoice steelChoice = new RecipeChoice.ExactChoice(SteelIngot(1));
        for (int i = 0; i < 4; i++) {
            ShapedRecipe r = new ShapedRecipe(steelArmorKeys[i], steelArmors[i]);
            r.shape(shapes[i]);
            r.setIngredient('S', steelChoice);
            plugin.getServer().addRecipe(r);
            count++;
        }

        // 玄武剑: 玄武岩x2 + 烈焰棒x1
        NamespacedKey tortoiseKey = new NamespacedKey(plugin, "ArmsorPlus_BlackTortoiseSword");
        ShapedRecipe tortoiseRecipe = new ShapedRecipe(tortoiseKey, BlackTortoiseSword(1));
        tortoiseRecipe.shape(" B ", "BSB", " B ");
        tortoiseRecipe.setIngredient('B', BASALT);
        tortoiseRecipe.setIngredient('S', BLAZE_ROD);
        plugin.getServer().addRecipe(tortoiseRecipe);
        count++;

        // 金盾丹: 8金块 + 1地狱疣 → 1个
        NamespacedKey goldShieldKey = new NamespacedKey(plugin, "ArmsorPlus_GoldShieldElixir");
        ShapedRecipe goldShieldRecipe = new ShapedRecipe(goldShieldKey, GoldShieldElixir(1));
        goldShieldRecipe.shape("GGG", "GNG", "GGG");
        goldShieldRecipe.setIngredient('G', GOLD_BLOCK);
        goldShieldRecipe.setIngredient('N', NETHER_WART);
        plugin.getServer().addRecipe(goldShieldRecipe);
        count++;

        // 冰块: 1个冰 → 4个冰块
        NamespacedKey iceCubeRecipeKey = new NamespacedKey(plugin, "ArmsorPlus_IceCubeRecipe");
        ShapelessRecipe iceCubeRecipe = new ShapelessRecipe(iceCubeRecipeKey, IceCube(4));
        iceCubeRecipe.addIngredient(1, ICE);
        plugin.getServer().addRecipe(iceCubeRecipe);
        count++;

        // ===== 食物配方 =====
        // 汉堡: 2面包 + 熟牛肉 (全部原版材料)
        NamespacedKey burgerKey = new NamespacedKey(plugin, "ArmsorPlus_Burger");
        ShapelessRecipe burgerRecipe = new ShapelessRecipe(burgerKey, Burger(1));
        burgerRecipe.addIngredient(2, BREAD);
        burgerRecipe.addIngredient(1, COOKED_BEEF);
        plugin.getServer().addRecipe(burgerRecipe);
        count++;

        // 热狗: 面包 + 熟猪排 (全部原版材料)
        NamespacedKey hotDogKey = new NamespacedKey(plugin, "ArmsorPlus_HotDog");
        ShapelessRecipe hotDogRecipe = new ShapelessRecipe(hotDogKey, HotDog(1));
        hotDogRecipe.addIngredient(1, BREAD);
        hotDogRecipe.addIngredient(1, COOKED_PORKCHOP);
        plugin.getServer().addRecipe(hotDogRecipe);
        count++;

        // 辣椒: 碗 + 红染料 + 熟牛肉 (植物产物, 随 EnablePlants 开关)
        if (ArmsorPlus.EnablePlants) {
            NamespacedKey chiliKey = new NamespacedKey(plugin, "ArmsorPlus_Chili");
            ShapelessRecipe chiliRecipe = new ShapelessRecipe(chiliKey, Chili(1));
            chiliRecipe.addIngredient(1, BOWL);
            chiliRecipe.addIngredient(1, RED_DYE);
            chiliRecipe.addIngredient(1, COOKED_BEEF);
            plugin.getServer().addRecipe(chiliRecipe);
            count++;
        }

        // ===== 鱼骨系列武器配方 =====

        // 鱼骨剑: 2鲑鱼 + 1骨头 (竖排)
        NamespacedKey fishBoneSwordKey = new NamespacedKey(plugin, "ArmsorPlus_FishBoneSword");
        ShapedRecipe fishBoneSwordRecipe = new ShapedRecipe(fishBoneSwordKey, FishBoneSword(1));
        fishBoneSwordRecipe.shape("A", "B", "C");
        fishBoneSwordRecipe.setIngredient('A', SALMON);
        fishBoneSwordRecipe.setIngredient('B', SALMON);
        fishBoneSwordRecipe.setIngredient('C', BONE);
        plugin.getServer().addRecipe(fishBoneSwordRecipe);
        count++;

        // 鱼骨刀: 2鳕鱼 + 1骨头 (竖排)
        NamespacedKey fishBoneKnifeKey = new NamespacedKey(plugin, "ArmsorPlus_FishBoneKnife");
        ShapedRecipe fishBoneKnifeRecipe = new ShapedRecipe(fishBoneKnifeKey, FishBoneKnife(1));
        fishBoneKnifeRecipe.shape("A", "B", "C");
        fishBoneKnifeRecipe.setIngredient('A', COD);
        fishBoneKnifeRecipe.setIngredient('B', COD);
        fishBoneKnifeRecipe.setIngredient('C', BONE);
        plugin.getServer().addRecipe(fishBoneKnifeRecipe);
        count++;

        // 鱼刺剑: 4骨块+4海晶沙粒+鱼骨剑(中心)
        NamespacedKey fishSpineSwordKey = new NamespacedKey(plugin, "ArmsorPlus_FishSpineSword");
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
        plugin.getServer().addRecipe(fishSpineSwordRecipe);
        count++;

        // 鱼刺刀: 4骨块+4海晶沙粒+鱼骨刀(中心)
        NamespacedKey fishSpineKnifeKey = new NamespacedKey(plugin, "ArmsorPlus_FishSpineKnife");
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
        plugin.getServer().addRecipe(fishSpineKnifeRecipe);
        count++;

        // 海骨剑: 4海绵+4海晶灯+鱼刺剑(中心)
        NamespacedKey seaBoneSwordKey = new NamespacedKey(plugin, "ArmsorPlus_SeaBoneSword");
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
        plugin.getServer().addRecipe(seaBoneSwordRecipe);
        count++;

        // 海骨刀: 4海绵+4海晶灯+鱼刺刀(中心)
        NamespacedKey seaBoneKnifeKey = new NamespacedKey(plugin, "ArmsorPlus_SeaBoneKnife");
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
        plugin.getServer().addRecipe(seaBoneKnifeRecipe);
        count++;

        // 灵骨剑: 8灵魂沙+海骨剑(中心)
        NamespacedKey spiritBoneSwordKey = new NamespacedKey(plugin, "ArmsorPlus_SpiritBoneSword");
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
        plugin.getServer().addRecipe(spiritBoneSwordRecipe);
        count++;

        // 灵骨刀: 8灵魂沙+海骨刀(中心)
        NamespacedKey spiritBoneKnifeKey = new NamespacedKey(plugin, "ArmsorPlus_SpiritBoneKnife");
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
        plugin.getServer().addRecipe(spiritBoneKnifeRecipe);
        count++;

        // 海刺剑: 4鳞甲+4海洋之心+灵骨剑(中心)
        NamespacedKey seaSpineSwordKey = new NamespacedKey(plugin, "ArmsorPlus_SeaSpineSword");
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
        plugin.getServer().addRecipe(seaSpineSwordRecipe);
        count++;

        // 海刺刀: 4鳞甲+4海洋之心+灵骨刀(中心)
        NamespacedKey seaSpineKnifeKey = new NamespacedKey(plugin, "ArmsorPlus_SeaSpineKnife");
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
        plugin.getServer().addRecipe(seaSpineKnifeRecipe);
        count++;

        // 蚀骨剑: 7灵魂土+1凋零骷髅头+灵骨剑(中心)
        NamespacedKey corrodeBoneSwordKey = new NamespacedKey(plugin, "ArmsorPlus_CorrodeBoneSword");
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
        plugin.getServer().addRecipe(corrodeBoneSwordRecipe);
        count++;

        // 灵刺剑: 4潮涌核心+4恶魂之泪+海刺剑(中心)
        NamespacedKey spiritSpineSwordKey = new NamespacedKey(plugin, "ArmsorPlus_SpiritSpineSword");
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
        plugin.getServer().addRecipe(spiritSpineSwordRecipe);
        count++;

        // 灵刺刀: 4潮涌核心+4凋零骷髅头+海刺刀(中心)
        NamespacedKey spiritSpineKnifeKey = new NamespacedKey(plugin, "ArmsorPlus_SpiritSpineKnife");
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
        plugin.getServer().addRecipe(spiritSpineKnifeRecipe);
        count++;

        // 海哭剑: 4下界之星+4潮涌核心+灵刺剑(中心)
        NamespacedKey seaCrySwordKey = new NamespacedKey(plugin, "ArmsorPlus_SeaCrySword");
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
        plugin.getServer().addRecipe(seaCrySwordRecipe);
        count++;

        // 海哭刀: 4下界之星+4潮涌核心+灵刺刀(中心)
        NamespacedKey seaCryKnifeKey = new NamespacedKey(plugin, "ArmsorPlus_SeaCryKnife");
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
        plugin.getServer().addRecipe(seaCryKnifeRecipe);
        count++;

        // ===== 幻术师武器 =====

        // 幻影之刃: 下界合金剑 + 幻术师的遗骸 + 2紫颂果
        NamespacedKey illusionBladeKey = new NamespacedKey(plugin, "ArmsorPlus_IllusionBlade");
        ShapelessRecipe illusionBladeRecipe = new ShapelessRecipe(illusionBladeKey, IllusionBlade(1));
        illusionBladeRecipe.addIngredient(1, NETHERITE_SWORD);
        illusionBladeRecipe.addIngredient(new RecipeChoice.ExactChoice(IllusionerScrap(1)));
        illusionBladeRecipe.addIngredient(2, CHORUS_FRUIT);
        plugin.getServer().addRecipe(illusionBladeRecipe);
        count++;

        // 幻惑法杖: 2烈焰棒 + 幻术师的遗骨 + 末影珍珠
        NamespacedKey illusionStaffKey = new NamespacedKey(plugin, "ArmsorPlus_IllusionStaff");
        ShapelessRecipe illusionStaffRecipe = new ShapelessRecipe(illusionStaffKey, IllusionStaff(1));
        illusionStaffRecipe.addIngredient(2, BLAZE_ROD);
        illusionStaffRecipe.addIngredient(new RecipeChoice.ExactChoice(IllusionerBone(1)));
        illusionStaffRecipe.addIngredient(1, ENDER_PEARL);
        plugin.getServer().addRecipe(illusionStaffRecipe);
        count++;

        plugin.getLogger().info("ArmsorPlus 配方注册完成 数量: " + count);
    }
}
