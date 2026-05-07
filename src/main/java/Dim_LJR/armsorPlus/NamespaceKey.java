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

        // ---- 物品键 ----
        public static NamespacedKey SaltKey;

        /** 初始化所有键 — 必须在插件 onEnable 中调用 */
        public static void regkey(JavaPlugin plugin) {
            getplugin = plugin;

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

            SaltKey = new NamespacedKey(plugin, "ArmsorPlus_Salt");

            DiamondDrillKey = new NamespacedKey(plugin, "ArmsorPlus_DiamondDrill");
            IndestructibleKey = new NamespacedKey(plugin, "ArmsorPlus_Indestructible");
            BlindnessKey = new NamespacedKey(plugin, "ArmsorPlus_Blindness");

            RainSwordKey = new NamespacedKey(plugin, "ArmsorPlus_RainSword");
            FlyingSwordKey = new NamespacedKey(plugin, "ArmsorPlus_FlyingSword");
            FlashStepBladeKey = new NamespacedKey(plugin, "ArmsorPlus_FlashStepBlade");
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
