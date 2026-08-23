package Dim_LJR.armsorPlus;

import Dim_LJR.armsorPlus.Item.EnchantedBook;
import Dim_LJR.armsorPlus.Item.EnhancementStone;
import Dim_LJR.armsorPlus.Item.MagicBall;
import Dim_LJR.armsorPlus.Item.MiscItem;
import Dim_LJR.armsorPlus.Item.Weapon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

// 自定义物品的外观类 (Facade)。
// 所有方法委托到 {@link Dim_LJR.armsorPlus.Item} 包下对应的子类。
// 保留此类以保证旧有 {@code import static Dim_LJR.armsorPlus.ArmsorItem.*} 兼容。
//
// @see EnhancementStone
// @see MagicBall
// @see EnchantedBook
// @see Weapon
// @see MiscItem
public final class ArmsorItem {

    // ========================================================================
    // 强化石 (委托至 EnhancementStone)
    // ========================================================================

    static final String ARMS_I_NAME = EnhancementStone.ARMS_I_NAME;
    static final String ARMS_II_NAME = EnhancementStone.ARMS_II_NAME;
    static final String ARMOR_I_NAME = EnhancementStone.ARMOR_I_NAME;
    static final String ARMOR_II_NAME = EnhancementStone.ARMOR_II_NAME;
    static final String BOW_I_NAME = EnhancementStone.BOW_I_NAME;
    static final String DIAMONDPLUS = EnhancementStone.DIAMONDPLUS;

    public static ItemStack ArmsPlusCreateI(int amount) {
        return EnhancementStone.ArmsPlusCreateI(amount);
    }

    public static ItemStack ArmsPlusCreateII(int amount) {
        return EnhancementStone.ArmsPlusCreateII(amount);
    }

    public static ItemStack ArmorPlusCreate(int amount) {
        return EnhancementStone.ArmorPlusCreate(amount);
    }

    public static ItemStack ArmorPlusCreateII(int amount) {
        return EnhancementStone.ArmorPlusCreateII(amount);
    }

    public static ItemStack BowPlusCreate(int amount) {
        return EnhancementStone.BowPlusCreate(amount);
    }

    public static ItemStack BasicStone(int amount) {
        return EnhancementStone.BasicStone(amount);
    }

    public static ItemStack DIAMONDPLUSCreate(int amount) {
        return EnhancementStone.DIAMONDPLUSCreate(amount);
    }

    // ========================================================================
    // 魔法球 (委托至 MagicBall)
    // ========================================================================

    public static ItemStack MagicBallCreateI(int amount) {
        return MagicBall.MagicBallCreateI(amount);
    }

    public static ItemStack MagicBallCreateII(int amount) {
        return MagicBall.MagicBallCreateII(amount);
    }

    public static ItemStack MagicBallCreateIII(int amount) {
        return MagicBall.MagicBallCreateIII(amount);
    }

    public static ItemStack MagicBallCreateIV(int amount) {
        return MagicBall.MagicBallCreateIV(amount);
    }

    // ========================================================================
    // 武器 (委托至 Weapon)
    // ========================================================================

    public static ItemStack BloodSword(int amount) {
        return Weapon.BloodSword(amount);
    }

    public static ItemStack Iron_Epee(int amount) {
        return Weapon.Iron_Epee(amount);
    }

    public static ItemStack DevourLifeSword(int amount) {
        return Weapon.DevourLifeSword(amount);
    }

    public static ItemStack StarTraceSword(int amount) {
        return Weapon.StarTraceSword(amount);
    }

    public static ItemStack BlackTortoiseSword(int amount) {
        return Weapon.BlackTortoiseSword(amount);
    }

    public static ItemStack Dagger(int amount) {
        return Weapon.Dagger(amount);
    }

    public static ItemStack ThrowingAxe(int amount) {
        return Weapon.ThrowingAxe(amount);
    }

    public static ItemStack SkeletonScepter(int amount) {
        return Weapon.SkeletonScepter(amount);
    }

    public static ItemStack FrostBow(int amount) {
        return Weapon.FrostBow(amount);
    }

    public static ItemStack FlameHalberd(int amount) {
        return Weapon.FlameHalberd(amount);
    }

    public static ItemStack RainSword(int amount) {
        return Weapon.RainSword(amount);
    }

    public static ItemStack FlyingSword(int amount) {
        return Weapon.FlyingSword(amount);
    }

    public static ItemStack FlashStepBlade(int amount) {
        return Weapon.FlashStepBlade(amount);
    }

    public static ItemStack MagicStick(int amount) {
        return Weapon.MagicStick(amount);
    }

    public static ItemStack IceSword(int amount) {
        return Weapon.IceSword(amount);
    }

    public static ItemStack WebBow(int amount) {
        return Weapon.WebBow(amount);
    }

    public static ItemStack ExplosionBow(int amount) {
        return Weapon.ExplosionBow(amount);
    }

    // ---- 鱼骨系列武器 ----
    public static ItemStack FishBoneSword(int amount) { return Weapon.FishBoneSword(amount); }
    public static ItemStack FishBoneKnife(int amount) { return Weapon.FishBoneKnife(amount); }
    public static ItemStack FishSpineSword(int amount) { return Weapon.FishSpineSword(amount); }
    public static ItemStack FishSpineKnife(int amount) { return Weapon.FishSpineKnife(amount); }
    public static ItemStack SeaBoneSword(int amount) { return Weapon.SeaBoneSword(amount); }
    public static ItemStack SeaBoneKnife(int amount) { return Weapon.SeaBoneKnife(amount); }
    public static ItemStack SpiritBoneSword(int amount) { return Weapon.SpiritBoneSword(amount); }
    public static ItemStack SpiritBoneKnife(int amount) { return Weapon.SpiritBoneKnife(amount); }
    public static ItemStack SeaSpineSword(int amount) { return Weapon.SeaSpineSword(amount); }
    public static ItemStack SeaSpineKnife(int amount) { return Weapon.SeaSpineKnife(amount); }
    public static ItemStack CorrodeBoneSword(int amount) { return Weapon.CorrodeBoneSword(amount); }
    public static ItemStack SpiritSpineSword(int amount) { return Weapon.SpiritSpineSword(amount); }
    public static ItemStack SpiritSpineKnife(int amount) { return Weapon.SpiritSpineKnife(amount); }
    public static ItemStack SeaCrySword(int amount) { return Weapon.SeaCrySword(amount); }
    public static ItemStack SeaCryKnife(int amount) { return Weapon.SeaCryKnife(amount); }

    // ========================================================================
    // 附魔书 (委托至 EnchantedBook)
    // ========================================================================

    public static ItemStack Dodge_EnchantdeBook(int amount, int level) {
        return EnchantedBook.Dodge_EnchantdeBook(amount, level);
    }

    public static ItemStack Famine_EnchantdeBook(int amount, int level) {
        return EnchantedBook.Famine_EnchantdeBook(amount, level);
    }

    public static ItemStack Ripples_EnchantdeBook(int amount, int level) {
        return EnchantedBook.Ripples_EnchantdeBook(amount, level);
    }

    public static ItemStack BloodSacrifice_EnchantdeBook(int amount, int level) {
        return EnchantedBook.BloodSacrifice_EnchantdeBook(amount, level);
    }

    public static ItemStack EffectClear_EnchantdeBook(int amount, int level) {
        return EnchantedBook.EffectClear_EnchantdeBook(amount, level);
    }

    public static ItemStack Freeze_EnchantedBook(int amount, int level) {
        return EnchantedBook.Freeze_EnchantedBook(amount, level);
    }

    public static ItemStack Blocking_EnchantedBook(int amount, int level) {
        return EnchantedBook.Blocking_EnchantedBook(amount, level);
    }

    public static ItemStack Withering_EnchantedBook(int amount, int level) {
        return EnchantedBook.Withering_EnchantedBook(amount, level);
    }

    public static ItemStack Survivor_EnchantedBook(int amount, int level) {
        return EnchantedBook.Survivor_EnchantedBook(amount, level);
    }

    public static ItemStack Revenge_EnchantedBook(int amount, int level) {
        return EnchantedBook.Revenge_EnchantedBook(amount, level);
    }

    public static ItemStack HealthBoost_EnchantedBook(int amount, int level) {
        return EnchantedBook.HealthBoost_EnchantedBook(amount, level);
    }

    public static ItemStack ExplosiveArrow_EnchantedBook(int amount, int level) {
        return EnchantedBook.ExplosiveArrow_EnchantedBook(amount, level);
    }

    public static ItemStack ShadowDodge_EnchantdeBook(int amount, int level) {
        return EnchantedBook.ShadowDodge_EnchantdeBook(amount, level);
    }

    public static ItemStack ArrowSpeed_EnchantdeBook(int amount, int level) {
        return EnchantedBook.ArrowSpeed_EnchantdeBook(amount, level);
    }

    public static ItemStack Sniping_EnchantdeBook(int amount, int level) {
        return EnchantedBook.Sniping_EnchantdeBook(amount, level);
    }

    public static ItemStack DoubleHit_EnchantdeBook(int amount, int level) {
        return EnchantedBook.DoubleHit_EnchantdeBook(amount, level);
    }

    public static ItemStack Feeding_EnchantdeBook(int amount, int level) {
        return EnchantedBook.Feeding_EnchantdeBook(amount, level);
    }

    public static ItemStack QuickThrust_EnchantedBook(int amount, int level) {
        return EnchantedBook.QuickThrust_EnchantedBook(amount, level);
    }

    public static ItemStack DiamondDrill_EnchantedBook(int amount, int level) {
        return EnchantedBook.DiamondDrill_EnchantedBook(amount, level);
    }

    public static ItemStack Blindness_EnchantedBook(int amount, int level) {
        return EnchantedBook.Blindness_EnchantedBook(amount, level);
    }

    public static ItemStack Indestructible_EnchantedBook(int amount, int level) {
        return EnchantedBook.Indestructible_EnchantedBook(amount, level);
    }

    public static ItemStack ProtectionPRO_EnchantedBook(int amount, int level) {
        return EnchantedBook.ProtectionPRO_EnchantedBook(amount, level);
    }

    public static ItemStack Stun_EnchantedBook(int amount, int level) {
        return EnchantedBook.Stun_EnchantedBook(amount, level);
    }

    public static ItemStack GolemGuardian_EnchantedBook(int amount, int level) {
        return EnchantedBook.GolemGuardian_EnchantedBook(amount, level);
    }

    public static ItemStack CriticalStrike_EnchantedBook(int amount, int level) {
        return EnchantedBook.CriticalStrike_EnchantedBook(amount, level);
    }

    public static ItemStack Piercing_EnchantedBook(int amount, int level) {
        return EnchantedBook.Piercing_EnchantedBook(amount, level);
    }

    public static ItemStack LavaWalker_EnchantedBook(int amount, int level) {
        return EnchantedBook.LavaWalker_EnchantedBook(amount, level);
    }

    public static ItemStack LightningCall_EnchantedBook(int amount, int level) {
        return EnchantedBook.LightningCall_EnchantedBook(amount, level);
    }

    public static ItemStack Holographic_EnchantedBook(int amount, int level) {
        return EnchantedBook.Holographic_EnchantedBook(amount, level);
    }

    public static ItemStack Tracking_EnchantedBook(int amount, int level) {
        return EnchantedBook.Tracking_EnchantedBook(amount, level);
    }

    public static ItemStack Harvest_EnchantedBook(int amount, int level) {
        return EnchantedBook.Harvest_EnchantedBook(amount, level);
    }

    public static ItemStack AutoPlant_EnchantedBook(int amount, int level) {
        return EnchantedBook.AutoPlant_EnchantedBook(amount, level);
    }

    public static ItemStack StrongBurst_EnchantedBook(int amount, int level) {
        return EnchantedBook.StrongBurst_EnchantedBook(amount, level);
    }

    public static ItemStack MultiShot_EnchantedBook(int amount, int level) {
        return EnchantedBook.MultiShot_EnchantedBook(amount, level);
    }

    public static ItemStack Poison_EnchantedBook(int amount, int level) {
        return EnchantedBook.Poison_EnchantedBook(amount, level);
    }

    public static ItemStack SharpBlade_EnchantedBook(int amount, int level) {
        return EnchantedBook.SharpBlade_EnchantedBook(amount, level);
    }

    public static ItemStack ThunderclapArrow_EnchantedBook(int amount, int level) {
        return EnchantedBook.ThunderclapArrow_EnchantedBook(amount, level);
    }

    public static ItemStack DamageDispersal_EnchantedBook(int amount, int level) {
        return EnchantedBook.DamageDispersal_EnchantedBook(amount, level);
    }

    public static ItemStack HerbGuard_EnchantedBook(int amount, int level) {
        return EnchantedBook.HerbGuard_EnchantedBook(amount, level);
    }

    public static ItemStack Pierce_EnchantedBook(int amount, int level) {
        return EnchantedBook.Pierce_EnchantedBook(amount, level);
    }

    public static ItemStack FireBlade_EnchantedBook(int amount, int level) {
        return EnchantedBook.FireBlade_EnchantedBook(amount, level);
    }

    public static ItemStack FrostBlade_EnchantedBook(int amount, int level) {
        return EnchantedBook.FrostBlade_EnchantedBook(amount, level);
    }

    public static ItemStack ThunderBlade_EnchantedBook(int amount, int level) {
        return EnchantedBook.ThunderBlade_EnchantedBook(amount, level);
    }

    public static ItemStack MagicBlade_EnchantedBook(int amount, int level) {
        return EnchantedBook.MagicBlade_EnchantedBook(amount, level);
    }

    public static ItemStack IceSpike_EnchantedBook(int amount, int level) {
        return EnchantedBook.IceSpike_EnchantedBook(amount, level);
    }

    public static ItemStack Inferno_EnchantedBook(int amount, int level) {
        return EnchantedBook.Inferno_EnchantedBook(amount, level);
    }

    public static ItemStack HeavyArmor_EnchantedBook(int amount, int level) {
        return EnchantedBook.HeavyArmor_EnchantedBook(amount, level);
    }

    public static ItemStack EarthFavor_EnchantedBook(int amount, int level) {
        return EnchantedBook.EarthFavor_EnchantedBook(amount, level);
    }

    public static ItemStack ThunderGlow(int amount) {
        return Weapon.ThunderGlow(amount);
    }

    public static ItemStack BlazingSun(int amount) {
        return Weapon.BlazingSun(amount);
    }

    public static ItemStack PeachWoodSword(int amount) {
        return Weapon.PeachWoodSword(amount);
    }

    public static ItemStack CorpseKing(int amount) {
        return Weapon.CorpseKing(amount);
    }

    public static ItemStack SteelSword(int amount) { return Weapon.SteelSword(amount); }
    public static ItemStack SteelHelmet(int amount) { return Weapon.SteelHelmet(amount); }
    public static ItemStack SteelChestplate(int amount) { return Weapon.SteelChestplate(amount); }
    public static ItemStack SteelLeggings(int amount) { return Weapon.SteelLeggings(amount); }
    public static ItemStack SteelBoots(int amount) { return Weapon.SteelBoots(amount); }

    public static ItemStack Ambush_EnchantedBook(int amount, int level) {
        return EnchantedBook.Ambush_EnchantedBook(amount, level);
    }

    // ========================================================================
    // 其他物品 (委托至 MiscItem)
    // ========================================================================

    static final String GUIDE_BOOK = MiscItem.GUIDE_BOOK;

    public static ItemStack GuideBook(int amount) {
        return MiscItem.GuideBook(amount);
    }

    public static ItemStack MenuMark(int amount, Material material) {
        return MiscItem.MenuMark(amount, material);
    }

    // ========================================================================
    // 幻术师武器 (委托至 Weapon)
    // ========================================================================

    public static ItemStack IllusionBlade(int amount) {
        return Weapon.IllusionBlade(amount);
    }

    public static ItemStack IllusionStaff(int amount) {
        return Weapon.IllusionStaff(amount);
    }

    // ========================================================================
    // 吞云斩月刀 (0.3J+)
    // ========================================================================

    public static ItemStack CloudMoonBlade(int amount) {
        return Weapon.CloudMoonBlade(amount);
    }

    public static ItemStack RagingPlundererCrossbow(int amount) {
        return Weapon.RagingPlundererCrossbow(amount);
    }
}
