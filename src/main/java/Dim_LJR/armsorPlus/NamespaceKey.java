package Dim_LJR.armsorPlus;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 插件全局 NamespacedKey 注册表。
 * <p>
 * 所有自定义附魔、强化石、标记等使用的 PDC 键在此统一注册和管理。
 * 必须在插件启动时先调用 {@link Keys#regkey(JavaPlugin)} 初始化。
 */
public class NamespaceKey {

    /**
     * 所有 NamespacedKey 的静态声明与初始化。
     * <p>
     * 使用方式: 在 Main 类的 onEnable 中调用 {@code NamespaceKey.Keys.regkey(this)}，
     * 然后全局通过 {@code NamespaceKey.Keys.xxxkey} 访问。
     */
    public static class Keys {
        public static JavaPlugin getplugin;

        // ---- 玩家数据键 ----
        public static NamespacedKey ShowParticles;
        public static NamespacedKey CriticalHitRate;
        public static NamespacedKey CriticalHitDamage;

        // ---- 强化石键 ----
        public static NamespacedKey Armskey;
        public static NamespacedKey Armorkey;
        public static NamespacedKey Bowkey;
        public static NamespacedKey DiamondPluskey;
        public static NamespacedKey GuideBookKey;
        public static NamespacedKey BasicStone;

        // ---- 自定义附魔键 ----
        public static NamespacedKey Feedingkey;
        public static NamespacedKey MagicBallKey;
        public static NamespacedKey BloodSacrificekey;
        public static NamespacedKey Dodgekey;
        public static NamespacedKey Faminekey;
        public static NamespacedKey DoubleHitkey;
        public static NamespacedKey CriticalHitkey;
        public static NamespacedKey BlockProtectkey;
        public static NamespacedKey RipplesProtectkey;
        public static NamespacedKey EffectClear;
        public static NamespacedKey FreezeKey;
        public static NamespacedKey SurvivorKey;
        public static NamespacedKey WitheringKey;
        public static NamespacedKey BlockingKey;
        public static NamespacedKey RevengeKey;
        public static NamespacedKey HealthBoostKey;
        public static NamespacedKey ExplosiveArrowKey;
        public static NamespacedKey ShadowDodge;
        public static NamespacedKey ArrowSpeed;
        public static NamespacedKey Sniping;
        public static NamespacedKey Exorcism;
        public static NamespacedKey MenuMark;

        // ---- 新武器键 ----
        public static NamespacedKey DaggerKey;
        public static NamespacedKey ThrowingAxeKey;
        public static NamespacedKey SkeletonScepterKey;
        public static NamespacedKey FrostBowKey;
        public static NamespacedKey FlameHalberdKey;
        public static NamespacedKey DevourLifeSwordKey;//噬生
        public static NamespacedKey DevourLifeBloodTimestamps;//血裂时间戳

        // ---- 新附魔键 ----
        public static NamespacedKey QuickThrustKey;

        // ---- 新附魔键 ----
        public static NamespacedKey DiamondDrillKey;
        public static NamespacedKey IndestructibleKey;
        public static NamespacedKey BlindnessKey;

        // ---- 新武器键 ----
        public static NamespacedKey RainSwordKey;
        public static NamespacedKey FlyingSwordKey;
        public static NamespacedKey FlashStepBladeKey;
        public static NamespacedKey MagicStickKey;

        // ---- 食物/药品键 ----
        public static NamespacedKey RejuvenationPowderKey;
        public static NamespacedKey HemostaticBandageKey;
        public static NamespacedKey CompressedBiscuitKey;
        public static NamespacedKey JerkyKey;
        public static NamespacedKey SweetBerryPieKey;
        public static NamespacedKey WineBarrelKey;
        public static NamespacedKey WineKey;
        public static NamespacedKey RottenJerkyKey;
        public static NamespacedKey PorkJerkyKey;
        public static NamespacedKey BigAppleKey;
        public static NamespacedKey MuttonJerkyKey;
        public static NamespacedKey PlumKey;
        public static NamespacedKey HazelnutKey;
        public static NamespacedKey CoconutKey;
        public static NamespacedKey PineappleKey;
        public static NamespacedKey StrawberryKey;
        public static NamespacedKey BlueberryKey;
        public static NamespacedKey OrangeKey;
        public static NamespacedKey TangerineKey;
        public static NamespacedKey IceCubeKey;
        public static NamespacedKey FigKey;
        public static NamespacedKey DateKey;
        public static NamespacedKey PersimmonKey;
        public static NamespacedKey MangosteenKey;
        public static NamespacedKey CherryTomatoKey;
        public static NamespacedKey TomatoKey;
        public static NamespacedKey GrapeKey;
        public static NamespacedKey PomegranateKey;
        public static NamespacedKey ChestnutKey;
        public static NamespacedKey KiwiKey;
        public static NamespacedKey LonganKey;
        public static NamespacedKey LycheeKey;
        public static NamespacedKey CherryKey;
        public static NamespacedKey PeachKey;
        public static NamespacedKey FigSaplingKey;
        public static NamespacedKey DateSaplingKey;
        public static NamespacedKey PersimmonSaplingKey;
        public static NamespacedKey MangosteenSaplingKey;
        public static NamespacedKey CherryTomatoSaplingKey;
        public static NamespacedKey TomatoSaplingKey;
        public static NamespacedKey GrapeSaplingKey;
        public static NamespacedKey PomegranateSaplingKey;
        public static NamespacedKey ChestnutSaplingKey;
        public static NamespacedKey KiwiSaplingKey;
        public static NamespacedKey LonganSaplingKey;
        public static NamespacedKey LycheeSaplingKey;
        public static NamespacedKey CherrySaplingKey;
        public static NamespacedKey PeachSaplingKey;
        public static NamespacedKey PlumSaplingKey;
        public static NamespacedKey HazelnutSaplingKey;
        public static NamespacedKey CoconutSaplingKey;
        public static NamespacedKey PineappleSaplingKey;
        public static NamespacedKey StrawberrySaplingKey;
        public static NamespacedKey BlueberrySaplingKey;
        public static NamespacedKey OrangeSaplingKey;
        public static NamespacedKey TangerineSaplingKey;
        public static NamespacedKey BigAppleSaplingKey;

        // ---- 新武器键 (0.3H) ----
        public static NamespacedKey IceSwordKey;
        public static NamespacedKey WebBowKey;
        public static NamespacedKey ExplosionBowKey;
        public static NamespacedKey ScoreKey;

        // ---- 新附魔键 (0.3H) ----
        public static NamespacedKey ProtectionPROKey;
        public static NamespacedKey StunKey;
        public static NamespacedKey GolemGuardianKey;
        public static NamespacedKey CriticalStrikeKey;
        public static NamespacedKey PiercingKey;
        public static NamespacedKey LavaWalkerKey;
        public static NamespacedKey LightningCallKey;
        public static NamespacedKey HolographicKey;
        public static NamespacedKey TrackingKey;


        // ---- 新附魔键 (0.3I) ----
        public static NamespacedKey HarvestKey;
        public static NamespacedKey AutoPlantKey;
        public static NamespacedKey StrongBurstKey;
        public static NamespacedKey MultiShotKey;

        // ---- 武器使用次数键 ----
        public static NamespacedKey WebBowUsesKey;
        public static NamespacedKey ExplosionBowUsesKey;

        // ---- 新食物键 (0.3I+) ----
        public static NamespacedKey BurgerKey;
        public static NamespacedKey HotDogKey;
        public static NamespacedKey PizzaKey;
        public static NamespacedKey FrenchFriesKey;
        public static NamespacedKey DonutKey;
        public static NamespacedKey IceCreamKey;
        public static NamespacedKey PopcornKey;
        public static NamespacedKey CottonCandyKey;
        public static NamespacedKey ChocolateKey;
        public static NamespacedKey SushiKey;
        public static NamespacedKey RamenKey;
        public static NamespacedKey SandwichKey;
        public static NamespacedKey DrumstickKey;
        public static NamespacedKey CheeseKey;
        public static NamespacedKey PancakeKey;

        // ---- 新食材/物品键 ----
        public static NamespacedKey ChiliKey;
        public static NamespacedKey OnionKey;
        public static NamespacedKey CabbageKey;
        public static NamespacedKey ButterKey;
        public static NamespacedKey PoopKey;

        // ---- 新附魔键 ----
        public static NamespacedKey PoisonKey;
        public static NamespacedKey SharpBladeKey;
        public static NamespacedKey ThunderclapArrowKey;

        // ---- 物品键 ----
        public static NamespacedKey SaltKey;

        /** 初始化所有键 — 必须在插件 onEnable 中调用 */
        public static void regkey(JavaPlugin plugin) {
            getplugin = plugin;
            ScoreKey = new NamespacedKey(plugin,"ArmsorPlus_Score");
            MagicStickKey = new NamespacedKey(plugin,"ArmsorPlus_MagicStick");
            MenuMark = new NamespacedKey(plugin, "ArmsorPlus_MenuMark");

            ShowParticles = new NamespacedKey(plugin, "ArmsorPlus_ShowParticles");
            CriticalHitRate = new NamespacedKey(plugin, "ArmsorPlus_CriticalHitRate");
            CriticalHitDamage = new NamespacedKey(plugin, "ArmsorPlus_CriticalHitDamage");

            Armskey = new NamespacedKey(plugin, "ArmsorPlus_Arms");
            Armorkey = new NamespacedKey(plugin, "ArmsorPlus_Armor");
            Bowkey = new NamespacedKey(plugin, "ArmsorPlus_Bow");
            DiamondPluskey = new NamespacedKey(plugin, "ArmsorPlus_DiamondPlus");
            GuideBookKey = new NamespacedKey(plugin, "ArmsorPlus_GuideBook");
            BasicStone = new NamespacedKey(plugin, "ArmsorPlus_BasicStons");

            Feedingkey = new NamespacedKey(plugin, "ArmsorPlus_Feeding");
            MagicBallKey = new NamespacedKey(plugin, "ArmsorPlus_MagicBall");
            BloodSacrificekey = new NamespacedKey(plugin, "ArmsorPlus_BloodSacrifice");
            Dodgekey = new NamespacedKey(plugin, "ArmsorPlus_Dodge");
            Faminekey = new NamespacedKey(plugin, "ArmsorPlus_Famine");
            DoubleHitkey = new NamespacedKey(plugin, "ArmsorPlus_DoubleHit");
            CriticalHitkey = new NamespacedKey(plugin, "ArmsorPlus_CriticalHit");
            BlockProtectkey = new NamespacedKey(plugin, "ArmsorPlus_Block");
            RipplesProtectkey = new NamespacedKey(plugin, "ArmsorPlus_RipplesProtect");
            EffectClear = new NamespacedKey(plugin, "ArmsorPlus_EffectClear");
            FreezeKey = new NamespacedKey(plugin, "ArmsorPlus_Freeze");
            SurvivorKey = new NamespacedKey(plugin, "ArmsorPlus_Survivor");
            WitheringKey = new NamespacedKey(plugin, "ArmsorPlus_Withering");
            BlockingKey = new NamespacedKey(plugin, "ArmsorPlus_Blocking");
            RevengeKey = new NamespacedKey(plugin, "ArmsorPlus_Revenge");
            HealthBoostKey = new NamespacedKey(plugin, "ArmsorPlus_HealthBoost");
            ExplosiveArrowKey = new NamespacedKey(plugin, "ArmsorPlus_ExplosiveArrow");
            ShadowDodge = new NamespacedKey(plugin, "ArmsorPlus_ShadowDodge");
            ArrowSpeed = new NamespacedKey(plugin, "ArmsorPlus_ArrowSpeed");
            Sniping = new NamespacedKey(plugin, "ArmsorPlus_Sniping");
            Exorcism = new NamespacedKey(plugin, "ArmsorPlus_Exorcism");

            DaggerKey = new NamespacedKey(plugin, "ArmsorPlus_Dagger");
            ThrowingAxeKey = new NamespacedKey(plugin, "ArmsorPlus_ThrowingAxe");
            SkeletonScepterKey = new NamespacedKey(plugin, "ArmsorPlus_SkeletonScepter");
            FrostBowKey = new NamespacedKey(plugin, "ArmsorPlus_FrostBow");
            FlameHalberdKey = new NamespacedKey(plugin, "ArmsorPlus_FlameHalberd");

            QuickThrustKey = new NamespacedKey(plugin, "ArmsorPlus_QuickThrust");

            RejuvenationPowderKey = new NamespacedKey(plugin, "ArmsorPlus_RejuvenationPowder");
            HemostaticBandageKey = new NamespacedKey(plugin, "ArmsorPlus_HemostaticBandage");
            CompressedBiscuitKey = new NamespacedKey(plugin, "ArmsorPlus_CompressedBiscuit");
            JerkyKey = new NamespacedKey(plugin, "ArmsorPlus_Jerky");
            SweetBerryPieKey = new NamespacedKey(plugin, "ArmsorPlus_SweetBerryPie");
            WineBarrelKey = new NamespacedKey(plugin, "ArmsorPlus_WineBarrel");
            WineKey = new NamespacedKey(plugin, "ArmsorPlus_Wine");
            RottenJerkyKey = new NamespacedKey(plugin, "ArmsorPlus_RottenJerky");
            PorkJerkyKey = new NamespacedKey(plugin, "ArmsorPlus_PorkJerky");
            MuttonJerkyKey = new NamespacedKey(plugin, "ArmsorPlus_MuttonJerky");
            BigAppleKey = new NamespacedKey(plugin, "ArmsorPlus_BigApple");
            PlumKey = new NamespacedKey(plugin, "ArmsorPlus_Plum");
            HazelnutKey = new NamespacedKey(plugin, "ArmsorPlus_Hazelnut");
            CoconutKey = new NamespacedKey(plugin, "ArmsorPlus_Coconut");
            PineappleKey = new NamespacedKey(plugin, "ArmsorPlus_Pineapple");
            StrawberryKey = new NamespacedKey(plugin, "ArmsorPlus_Strawberry");
            BlueberryKey = new NamespacedKey(plugin, "ArmsorPlus_Blueberry");
            OrangeKey = new NamespacedKey(plugin, "ArmsorPlus_Orange");
            TangerineKey = new NamespacedKey(plugin, "ArmsorPlus_Tangerine");
            IceCubeKey = new NamespacedKey(plugin, "ArmsorPlus_IceCube");
            FigKey = new NamespacedKey(plugin, "ArmsorPlus_Fig");
            DateKey = new NamespacedKey(plugin, "ArmsorPlus_Date");
            PersimmonKey = new NamespacedKey(plugin, "ArmsorPlus_Persimmon");
            MangosteenKey = new NamespacedKey(plugin, "ArmsorPlus_Mangosteen");
            CherryTomatoKey = new NamespacedKey(plugin, "ArmsorPlus_CherryTomato");
            TomatoKey = new NamespacedKey(plugin, "ArmsorPlus_Tomato");
            GrapeKey = new NamespacedKey(plugin, "ArmsorPlus_Grape");
            PomegranateKey = new NamespacedKey(plugin, "ArmsorPlus_Pomegranate");
            ChestnutKey = new NamespacedKey(plugin, "ArmsorPlus_Chestnut");
            KiwiKey = new NamespacedKey(plugin, "ArmsorPlus_Kiwi");
            LonganKey = new NamespacedKey(plugin, "ArmsorPlus_Longan");
            LycheeKey = new NamespacedKey(plugin, "ArmsorPlus_Lychee");
            CherryKey = new NamespacedKey(plugin, "ArmsorPlus_Cherry");
            PeachKey = new NamespacedKey(plugin, "ArmsorPlus_Peach");
            FigSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_FigSapling");
            DateSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_DateSapling");
            PersimmonSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_PersimmonSapling");
            MangosteenSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_MangosteenSapling");
            CherryTomatoSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_CherryTomatoSapling");
            TomatoSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_TomatoSapling");
            GrapeSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_GrapeSapling");
            PomegranateSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_PomegranateSapling");
            ChestnutSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_ChestnutSapling");
            KiwiSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_KiwiSapling");
            LonganSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_LonganSapling");
            LycheeSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_LycheeSapling");
            CherrySaplingKey = new NamespacedKey(plugin, "ArmsorPlus_CherrySapling");
            PeachSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_PeachSapling");
            PlumSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_PlumSapling");
            HazelnutSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_HazelnutSapling");
            CoconutSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_CoconutSapling");
            PineappleSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_PineappleSapling");
            StrawberrySaplingKey = new NamespacedKey(plugin, "ArmsorPlus_StrawberrySapling");
            BlueberrySaplingKey = new NamespacedKey(plugin, "ArmsorPlus_BlueberrySapling");
            OrangeSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_OrangeSapling");
            TangerineSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_TangerineSapling");
            BigAppleSaplingKey = new NamespacedKey(plugin, "ArmsorPlus_BigAppleSapling");

            SaltKey = new NamespacedKey(plugin, "ArmsorPlus_Salt");

            // 0.3I+ 新食物
            BurgerKey = new NamespacedKey(plugin, "ArmsorPlus_Burger");
            HotDogKey = new NamespacedKey(plugin, "ArmsorPlus_HotDog");
            PizzaKey = new NamespacedKey(plugin, "ArmsorPlus_Pizza");
            FrenchFriesKey = new NamespacedKey(plugin, "ArmsorPlus_FrenchFries");
            DonutKey = new NamespacedKey(plugin, "ArmsorPlus_Donut");
            IceCreamKey = new NamespacedKey(plugin, "ArmsorPlus_IceCream");
            PopcornKey = new NamespacedKey(plugin, "ArmsorPlus_Popcorn");
            CottonCandyKey = new NamespacedKey(plugin, "ArmsorPlus_CottonCandy");
            ChocolateKey = new NamespacedKey(plugin, "ArmsorPlus_Chocolate");
            SushiKey = new NamespacedKey(plugin, "ArmsorPlus_Sushi");
            RamenKey = new NamespacedKey(plugin, "ArmsorPlus_Ramen");
            SandwichKey = new NamespacedKey(plugin, "ArmsorPlus_Sandwich");
            DrumstickKey = new NamespacedKey(plugin, "ArmsorPlus_Drumstick");
            CheeseKey = new NamespacedKey(plugin, "ArmsorPlus_Cheese");
            PancakeKey = new NamespacedKey(plugin, "ArmsorPlus_Pancake");

            ChiliKey = new NamespacedKey(plugin, "ArmsorPlus_Chili");
            OnionKey = new NamespacedKey(plugin, "ArmsorPlus_Onion");
            CabbageKey = new NamespacedKey(plugin, "ArmsorPlus_Cabbage");
            ButterKey = new NamespacedKey(plugin, "ArmsorPlus_Butter");
            PoopKey = new NamespacedKey(plugin, "ArmsorPlus_Poop");

            PoisonKey = new NamespacedKey(plugin, "ArmsorPlus_Poison");
            SharpBladeKey = new NamespacedKey(plugin, "ArmsorPlus_SharpBlade");
            ThunderclapArrowKey = new NamespacedKey(plugin, "ArmsorPlus_ThunderclapArrow");

            DiamondDrillKey = new NamespacedKey(plugin, "ArmsorPlus_DiamondDrill");
            IndestructibleKey = new NamespacedKey(plugin, "ArmsorPlus_Indestructible");
            BlindnessKey = new NamespacedKey(plugin, "ArmsorPlus_Blindness");

            RainSwordKey = new NamespacedKey(plugin, "ArmsorPlus_RainSword");
            FlyingSwordKey = new NamespacedKey(plugin, "ArmsorPlus_FlyingSword");
            FlashStepBladeKey = new NamespacedKey(plugin, "ArmsorPlus_FlashStepBlade");

            // 0.3H 新武器
            IceSwordKey = new NamespacedKey(plugin, "ArmsorPlus_IceSword");
            WebBowKey = new NamespacedKey(plugin, "ArmsorPlus_WebBow");
            ExplosionBowKey = new NamespacedKey(plugin, "ArmsorPlus_ExplosionBow");
            DevourLifeSwordKey = new NamespacedKey(plugin, "ArmsorPlus_DevourLifeSwordKey");
            DevourLifeBloodTimestamps = new NamespacedKey(plugin, "ArmsorPlus_DevourLifeBloodTimestamps");

            // 0.3H 新附魔
            ProtectionPROKey = new NamespacedKey(plugin, "ArmsorPlus_ProtectionPRO");
            StunKey = new NamespacedKey(plugin, "ArmsorPlus_Stun");
            GolemGuardianKey = new NamespacedKey(plugin, "ArmsorPlus_GolemGuardian");
            CriticalStrikeKey = new NamespacedKey(plugin, "ArmsorPlus_CriticalStrike");
            PiercingKey = new NamespacedKey(plugin, "ArmsorPlus_Piercing");
            LavaWalkerKey = new NamespacedKey(plugin, "ArmsorPlus_LavaWalker");
            LightningCallKey = new NamespacedKey(plugin, "ArmsorPlus_LightningCall");
            HolographicKey = new NamespacedKey(plugin, "ArmsorPlus_Holographic");
            TrackingKey = new NamespacedKey(plugin, "ArmsorPlus_Tracking");

            // 0.3I 新附魔
            HarvestKey = new NamespacedKey(plugin, "ArmsorPlus_Harvest");
            AutoPlantKey = new NamespacedKey(plugin, "ArmsorPlus_AutoPlant");
            StrongBurstKey = new NamespacedKey(plugin, "ArmsorPlus_StrongBurst");
            MultiShotKey = new NamespacedKey(plugin, "ArmsorPlus_MultiShot");

            // 武器使用次数键
            WebBowUsesKey = new NamespacedKey(plugin, "ArmsorPlus_WebBowUses");
            ExplosionBowUsesKey = new NamespacedKey(plugin, "ArmsorPlus_ExplosionBowUses");
        }
    }

    /** 打印插件启动横幅 (ASCII Art) */
    public static void banner() {
        Bukkit.getLogger().info(" █████╗ ██████╗ ███╗   ███╗███████╗ ██████╗ ██████╗ ");
        Bukkit.getLogger().info("██╔══██╗██╔══██╗████╗ ████║██╔════╝██╔═══██╗██╔══██╗");
        Bukkit.getLogger().info("███████║██████╔╝██╔████╔██║███████╗██║   ██║██████╔╝");
        Bukkit.getLogger().info("██╔══██║██╔══██╗██║╚██╔╝██║╚════██║██║   ██║██╔══██╗");
        Bukkit.getLogger().info("██║  ██║██║  ██║██║ ╚═╝ ██║███████║╚██████╔╝██║  ██║");
        Bukkit.getLogger().info("╚═╝  ╚═╝╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝");
        Bukkit.getLogger().info("██████╗ ██╗     ██╗   ██╗███████╗");
        Bukkit.getLogger().info("██╔══██╗██║     ██║   ██║██╔════╝");
        Bukkit.getLogger().info("██████╔╝██║     ██║   ██║███████╗");
        Bukkit.getLogger().info("██╔═══╝ ██║     ██║   ██║╚════██║");
        Bukkit.getLogger().info("██║     ███████╗╚██████╔╝███████║");
        Bukkit.getLogger().info("╚═╝     ╚══════╝ ╚═════╝ ╚══════╝");
        Bukkit.getLogger().info("by Dim_LJR");
    }
}
