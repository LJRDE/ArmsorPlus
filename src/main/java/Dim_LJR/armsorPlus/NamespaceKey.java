package Dim_LJR.armsorPlus;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class NamespaceKey {
    public class Keys {
        // 玩家键
        public static NamespacedKey ShowParticles;
        public static NamespacedKey CriticalHitRate;
        public static NamespacedKey CriticalHitDamage;

        // 强化石键
        public static NamespacedKey Armskey;
        public static NamespacedKey Armorkey;
        public static NamespacedKey Bowkey;
        public static NamespacedKey DiamondPluskey;
        public static NamespacedKey GuideBookKey;
        public static NamespacedKey BasicStone;

        // 附魔键
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

        // 初始化所有键
        public static void regkey(JavaPlugin plugin) {
            // 玩家键
            ShowParticles = new NamespacedKey(plugin, "ArmsorPlus_ShowParticles");
            CriticalHitRate = new NamespacedKey(plugin, "ArmsorPlus_CriticalHitRate");
            CriticalHitDamage = new NamespacedKey(plugin, "ArmsorPlus_CriticalHitDamage");

            // 强化石键
            Armskey = new NamespacedKey(plugin, "ArmsorPlus_Arms");
            Armorkey = new NamespacedKey(plugin, "ArmsorPlus_Armor");
            Bowkey = new NamespacedKey(plugin, "ArmsorPlus_Bow");
            DiamondPluskey = new NamespacedKey(plugin, "ArmsorPlus_DiamondPlus");
            GuideBookKey = new NamespacedKey(plugin, "ArmsorPlus_GuideBook");
            BasicStone = new NamespacedKey(plugin, "ArmsorPlus_BasicStons");

            // 附魔键
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
        }
    }
}
