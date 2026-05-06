package Dim_LJR.armsorPlus;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorPlusEnchantEventHandler.romanNumeral;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;

/**
 * 所有自定义物品的定义与创建方法。
 * <p>
 * 包括: 强化石、魔法球、武器、附魔书、菜单标记等。
 * 每个方法接收数量参数(Amount)和可选的等级参数(level)。
 */
public class ArmsorItem {

    // ========================================================================
    // 物品名称常量
    // ========================================================================

    // 强化石
    static final String ARMS_I_NAME = ChatColor.RED + "武器强化石Ⅰ";
    static final String ARMS_II_NAME = ChatColor.DARK_RED + "武器强化石Ⅱ";
    static final String ARMOR_I_NAME = ChatColor.BLUE + "护甲强化石Ⅰ";
    static final String ARMOR_II_NAME = ChatColor.DARK_BLUE + "护甲强化石Ⅱ";
    static final String BOW_I_NAME = ChatColor.DARK_RED + "弓箭强化石";
    static final String DIAMONDPLUS = ChatColor.GOLD + "精炼金刚石";

    // 魔法球
    private static final String MAGICBALL_I_NAME = ChatColor.GREEN + "寻常的魔法球";
    private static final String MAGICBALL_II_NAME = ChatColor.BLUE + "稀罕的魔法球";
    private static final String MAGICBALL_III_NAME = ChatColor.GOLD + "史诗的魔法球";
    private static final String MAGICBALL_IV_NAME = ChatColor.LIGHT_PURPLE + "传奇的魔法球";

    // 武器
    private static final String BLOOD_SWORD_NAME = ChatColor.RED + "血祭之剑";

    // 附魔书名称 (等级I~V)
    private static final String DODGE_BOOK = ChatColor.GOLD + "闪避";
    private static final String FAMINE_BOOK = ChatColor.GREEN + "饥荒";
    private static final String RIPPLES_BOOK = ChatColor.BLUE + "涟漪";
    private static final String BLOODSACRIFICE_BOOK = ChatColor.DARK_RED + "血祭";
    private static final String EFFECTCLEAR_BOOK = ChatColor.WHITE + "涤魂";
    private static final String FREEZE_BOOK = ChatColor.AQUA + "寒冻";
    private static final String BLOCKING_BOOK = ChatColor.GOLD + "格挡";
    private static final String WITHERING_BOOK = ChatColor.BLACK + "凋零";
    private static final String SURVIVOR_BOOK = ChatColor.GOLD + "幸存";
    private static final String REVENGE_BOOK = ChatColor.DARK_RED + "复仇";
    private static final String HEALTHBOOST_BOOK = ChatColor.RED + "生命提升";
    private static final String EXPLOSIVEARROW_BOOK = ChatColor.YELLOW + "蓄爆";
    private static final String SHADOWDODGE_BOOK = ChatColor.DARK_PURPLE + "影避";
    private static final String ARROWSPEED_BOOK = ChatColor.GOLD + "弹道";
    private static final String SNIPING_BOOK = ChatColor.LIGHT_PURPLE + "狙击";
    private static final String DOUBLEHIT_BOOK = ChatColor.LIGHT_PURPLE + "双重打击";
    private static final String FEEDING_BOOK = ChatColor.RED + "吸血";
    static final String GUIDE_BOOK = ChatColor.GOLD + "高级附魔向导";

    // ========================================================================
    // 强化石 —— 通过拖动到装备上使用
    // ========================================================================

    /** 一级武器强化石: 使锋利等级+1 */
    public static ItemStack ArmsPlusCreateI(int amount) {
        ItemStack item = new ItemStack(DIAMOND);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ARMS_I_NAME);
        meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "使用使锋利等级+1"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, Armskey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 二级武器强化石: 伤害直接+1 (通过属性修饰符) */
    public static ItemStack ArmsPlusCreateII(int amount) {
        ItemStack item = new ItemStack(DIAMOND_ORE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ARMS_II_NAME);
        meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "使用伤害直接+1"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, Armskey, 2);
        item.setAmount(amount);
        return item;
    }

    /** 一级护甲强化石: 使保护等级+1 */
    public static ItemStack ArmorPlusCreate(int amount) {
        ItemStack item = new ItemStack(IRON_INGOT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ARMOR_I_NAME);
        meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "使用使保护等级+1"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, Armorkey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 二级护甲强化石: 护甲值+1 (通过属性修饰符) */
    public static ItemStack ArmorPlusCreateII(int amount) {
        ItemStack item = new ItemStack(IRON_ORE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ARMOR_II_NAME);
        meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "使用使护甲值+1"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, Armorkey, 2);
        item.setAmount(amount);
        return item;
    }

    /** 弓箭强化石: 使力量等级+1 */
    public static ItemStack BowPlusCreate(int amount) {
        ItemStack item = new ItemStack(OAK_LOG);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(BOW_I_NAME);
        meta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "使用使力量等级+1"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, Bowkey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 基础强化石: 右键随机获得一种强化石 */
    public static ItemStack BasicStone(int amount) {
        ItemStack item = new ItemStack(STONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "基础强化石");
        meta.setLore(Arrays.asList(
                ChatColor.BLUE + "用四个钻石块合成基础强化石",
                ChatColor.GOLD + "右键获得武器,护甲强化石"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, BasicStone, 1);
        item.setAmount(amount);
        return item;
    }

    /** 精炼金刚石: 拖动到装备上获得无限耐久 */
    public static ItemStack DIAMONDPLUSCreate(int amount) {
        ItemStack item = new ItemStack(DIAMOND);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.UNBREAKING, 1, false);
        meta.setDisplayName(DIAMONDPLUS);
        meta.setLore(Collections.singletonList(ChatColor.BOLD + "拖动到装备上获得无限耐久"));
        item.setAmount(amount);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, DiamondPluskey, 1);
        return item;
    }

    // ========================================================================
    // 魔法球 —— 右键抽取随机附魔书
    // ========================================================================

    /** 寻常的魔法球 (保底1本) */
    public static ItemStack MagicBallCreateI(int amount) {
        return createMagicBall(amount, 1, MAGICBALL_I_NAME);
    }

    /** 稀罕的魔法球 (保底2本) */
    public static ItemStack MagicBallCreateII(int amount) {
        return createMagicBall(amount, 2, MAGICBALL_II_NAME);
    }

    /** 史诗的魔法球 (保底3本) */
    public static ItemStack MagicBallCreateIII(int amount) {
        return createMagicBall(amount, 3, MAGICBALL_III_NAME);
    }

    /** 传奇的魔法球 (保底4本) */
    public static ItemStack MagicBallCreateIV(int amount) {
        return createMagicBall(amount, 4, MAGICBALL_IV_NAME);
    }

    /** 魔法球通用构造 */
    private static ItemStack createMagicBall(int amount, int tier, String name) {
        ItemStack item = new ItemStack(FIREWORK_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.CHANNELING, 1, false);
        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList(
                ChatColor.LIGHT_PURPLE + "右键获得附魔书 保底" + tier + "本"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, MagicBallKey, tier);
        item.setAmount(amount);
        return item;
    }

    // ========================================================================
    // 武器
    // ========================================================================

    /** 血祭之剑: 攻击时概率消耗生命造成多倍伤害 */
    public static ItemStack BloodSword(int amount) {
        ItemStack item = new ItemStack(GOLDEN_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.SHARPNESS, 5, true);
        meta.setDisplayName(BLOOD_SWORD_NAME);
        meta.setLore(Collections.singletonList(
                ChatColor.DARK_RED + "血祭V: 攻击时有100%概率消耗15点生命值造成2~6倍伤害"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, BloodSacrificekey, 5);
        item.setAmount(amount);
        return item;
    }

    /** 重剑: 基础攻击+5的铁剑 */
    public static ItemStack Iron_Epee(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "重剑");
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
                new AttributeModifier(
                        NamespacedKey.fromString("armsorplus:custom_damage"),
                        5.0,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.MAINHAND));
        item.setItemMeta(meta);
        item.setAmount(amount);
        return item;
    }

    // ========================================================================
    // 附魔书 —— 拖动到对应装备上使用
    // ========================================================================

    /** 闪避附魔书: 靴子 - 概率闪避伤害 */
    public static ItemStack Dodge_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, Dodgekey, DODGE_BOOK,
                "可用装备:靴子", "概率闪避对方的伤害");
    }

    /** 饥荒附魔书: 武器 - 造成饥饿效果 */
    public static ItemStack Famine_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, Faminekey, FAMINE_BOOK,
                "可用装备:武器", "给对方造成饥饿效果");
    }

    /** 涟漪附魔书: 靴子 - 受到伤害时回复生命 */
    public static ItemStack Ripples_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, RipplesProtectkey, RIPPLES_BOOK,
                "可用装备:靴子", "受到伤害回复生命值");
    }

    /** 血祭附魔书: 剑 - 概率扣血造成多倍伤害 */
    public static ItemStack BloodSacrifice_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, BloodSacrificekey, BLOODSACRIFICE_BOOK,
                "可用装备:剑", "概率扣自己的血量造成多倍伤害",
                "倍率为2~" + (level + 1) + "倍");
    }

    /** 涤魂附魔书: 胸甲 - 周期性免疫魔法伤害 */
    public static ItemStack EffectClear_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, EffectClear, EFFECTCLEAR_BOOK,
                "可用装备:胸甲", "每隔一段时间免疫一次魔法伤害");
    }

    /** 寒冻附魔书: 武器 - 造成减速效果 */
    public static ItemStack Freeze_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, FreezeKey, FREEZE_BOOK,
                "可用装备:武器", "有概率给对方造成寒冻效果", "使对方移动速度下降");
    }

    /** 格挡附魔书: 头盔 - 按比例格挡伤害 */
    public static ItemStack Blocking_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, BlockingKey, BLOCKING_BOOK,
                "可用装备:头盔", "格挡一定伤害,受到伤害越高格挡比例越高");
    }

    /** 凋零附魔书: 武器 - 造成凋零效果 */
    public static ItemStack Withering_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, WitheringKey, WITHERING_BOOK,
                "可用装备:武器", "攻击时给对方造成凋零效果");
    }

    /** 幸存附魔书: 护腿 - 致命伤概率复活 */
    public static ItemStack Survivor_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, SurvivorKey, SURVIVOR_BOOK,
                "可用装备:裤子", "受到致命伤害概率复活");
    }

    /** 复仇附魔书: 胸甲 - 反弹伤害 */
    public static ItemStack Revenge_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, RevengeKey, REVENGE_BOOK,
                "可用装备:胸甲", "反弹一定比例的伤害");
    }

    /** 生命提升附魔书: 胸甲 - 增加最大生命值 */
    public static ItemStack HealthBoost_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, HealthBoostKey, HEALTHBOOST_BOOK,
                "可用装备:胸甲", "提升生命上限");
    }

    /** 蓄爆附魔书: 弓/弩 - 概率发射爆炸箭 */
    public static ItemStack ExplosiveArrow_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, ExplosiveArrowKey, EXPLOSIVEARROW_BOOK,
                "可用装备:弓和弩", "有概率发射一枚火箭弹");
    }

    /** 影避附魔书: 靴子 - 概率闪避所有伤害 */
    public static ItemStack ShadowDodge_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, ShadowDodge, SHADOWDODGE_BOOK,
                "可用装备:靴子", "概率闪避所有的伤害");
    }

    /** 弹道附魔书: 弓/弩 - 提升箭速和伤害 */
    public static ItemStack ArrowSpeed_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, ArrowSpeed, ARROWSPEED_BOOK,
                "可用装备:弓与弩", "提升射出弓箭的速度和伤害,每级提升一倍");
    }

    /** 狙击附魔书: 弓/弩 - 大幅提升箭速和伤害 (需要前置:弹道) */
    public static ItemStack Sniping_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, Sniping, SNIPING_BOOK,
                "可用装备:弓与弩", "提升射出弓箭的速度和伤害,每级提升5倍",
                "需要前置附魔[弹道]", "仅史诗及以上魔法球可以获得");
    }

    /** 双重打击附魔书: 武器 - 概率双倍伤害 */
    public static ItemStack DoubleHit_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, DoubleHitkey, DOUBLEHIT_BOOK,
                "可用装备:武器", "有" + level * 20 + "%概率获得双倍伤害");
    }

    /** 吸血附魔书: 武器 - 概率吸取生命 */
    public static ItemStack Feeding_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, Feedingkey, FEEDING_BOOK,
                "可用装备:武器", "有" + level * 20 + "%概率吸取0.5点生命值");
    }

    // ========================================================================
    // 其他物品
    // ========================================================================

    /** 高级附魔向导书: 右键打开菜单 */
    public static ItemStack GuideBook(int amount) {
        ItemStack item = new ItemStack(BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(GUIDE_BOOK);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, GuideBookKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 菜单边框标记 (用于GUI的玻璃板装饰) */
    public static ItemStack MenuMark(int amount, Material material) {
        ItemStack item = new ItemStack(material);
        ArmsorEnchant.addEnchant(item, MenuMark, 1);
        item.setAmount(amount);
        return item;
    }

    // ========================================================================
    // 新武器
    // ========================================================================

    private static final String DAGGER_NAME = ChatColor.DARK_GREEN + "匕首";
    private static final String THROWING_AXE_NAME = ChatColor.GOLD + "飞斧";
    private static final String SKELETON_SCEPTER_NAME = ChatColor.DARK_GRAY + "骷髅权杖";
    private static final String FROST_BOW_NAME = ChatColor.AQUA + "寒冰弓";
    private static final String FLAME_HALBERD_NAME = ChatColor.GOLD + "火焰戟";

    /** 匕首: 每次攻击必定造成5点额外伤害 */
    public static ItemStack Dagger(int amount) {
        ItemStack item = new ItemStack(NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(DAGGER_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.GREEN + "每次攻击额外造成5点伤害",
                ChatColor.GRAY + "轻盈而致命的短剑"));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:dagger_damage"),
                        5.0, AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.MAINHAND));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, DaggerKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 飞斧: 右键蓄力3s飞出，对沿途生物造成20点伤害 */
    public static ItemStack ThrowingAxe(int amount) {
        ItemStack item = new ItemStack(NETHERITE_AXE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(THROWING_AXE_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键蓄力3秒后飞出",
                ChatColor.RED + "对沿途生物造成20点伤害",
                ChatColor.GRAY + "蓄力时无法移动"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, ThrowingAxeKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 骷髅权杖: 使用时降下箭雨 */
    public static ItemStack SkeletonScepter(int amount) {
        ItemStack item = new ItemStack(BONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(SKELETON_SCEPTER_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.DARK_GRAY + "右键释放箭雨",
                ChatColor.GRAY + "在目标区域降下致命的箭雨"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SkeletonScepterKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 寒冰弓: 射出时额外发射2支寒冰箭 */
    public static ItemStack FrostBow(int amount) {
        ItemStack item = new ItemStack(BOW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(FROST_BOW_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.AQUA + "射出时额外发射2支寒冰箭",
                ChatColor.AQUA + "命中造成25点冷冻伤害并给予缓慢",
                ChatColor.GRAY + "两支箭可叠加效果"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, FrostBowKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 火焰戟: 攻击额外造成30火焰伤害，射出时灼烧沿途3×3 */
    public static ItemStack FlameHalberd(int amount) {
        ItemStack item = new ItemStack(TRIDENT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(FLAME_HALBERD_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "攻击额外造成30点火焰伤害",
                ChatColor.GOLD + "投掷时灼烧沿途3×3范围",
                ChatColor.RED + "烈焰之戟"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, FlameHalberdKey, 1);
        item.setAmount(amount);
        return item;
    }

    // ========================================================================
    // 疾刺附魔书
    // ========================================================================

    private static final String QUICKTHRUST_BOOK = ChatColor.GOLD + "疾刺";

    /** 疾刺附魔书: 长矛/三叉戟 - 右键速度提升 */
    public static ItemStack QuickThrust_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, QuickThrustKey, QUICKTHRUST_BOOK,
                "可用装备:三叉戟/长矛", "右键使用时移动速度提升" + (level * 10) + "%");
    }

    private static final String DIAMONDDRILL_BOOK = ChatColor.AQUA + "金刚钻";
    private static final String BLINDNESS_BOOK = ChatColor.DARK_GRAY + "失明";

    /** 金刚钻附魔书: 镐子 - 挖掘黑曜石概率秒破 */
    public static ItemStack DiamondDrill_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, DiamondDrillKey, DIAMONDDRILL_BOOK,
                "可用装备:镐子", "挖掘黑曜石时" + (level * 20) + "%概率瞬间挖掉");
    }

    private static final String INDESTRUCTIBLE_BOOK = ChatColor.GOLD + "不灭";

    /** 失明附魔书: 武器 - 攻击施加失明效果 */
    public static ItemStack Blindness_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, BlindnessKey, BLINDNESS_BOOK,
                "可用装备:武器", "攻击时" + (level * 10) + "%概率施加失明" + (level * 2) + "秒");
    }

    /** 不灭附魔书: 任意装备 - 防止死亡 (仅管理员可获取) */
    public static ItemStack Indestructible_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, IndestructibleKey, INDESTRUCTIBLE_BOOK,
                "可用装备:任意装备", "受到致命伤害时免疫死亡并回满状态");
    }

    // ========================================================================
    // 新武器
    // ========================================================================

    private static final String RAIN_SWORD_NAME = ChatColor.DARK_AQUA + "雨御前";
    private static final String FLYING_SWORD_NAME = ChatColor.GOLD + "飞天御剑";
    private static final String FLASH_STEP_BLADE_NAME = ChatColor.DARK_PURPLE + "瞬步刃";

    /** 雨御前: 右键3秒隐身+无敌，冷却15s；Shift+右键向前瞬移 */
    public static ItemStack RainSword(int amount) {
        ItemStack item = new ItemStack(NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(RAIN_SWORD_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.AQUA + "右键: 3秒隐身(含装备)且免疫伤害",
                ChatColor.DARK_AQUA + "冷却时间: 15秒",
                ChatColor.LIGHT_PURPLE + "Shift+右键: 向前瞬移一小段距离",
                ChatColor.GRAY + "传说中的雨之神剑"));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
                new AttributeModifier(new NamespacedKey(getplugin, "rain_sword_damage"),
                        7.0, AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.MAINHAND));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, RainSwordKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 飞天御剑: 右键悬空飞行+脚下飞剑，速度8m/s */
    public static ItemStack FlyingSword(int amount) {
        ItemStack item = new ItemStack(NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(FLYING_SWORD_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键: 悬空飞行(速度8m/s)",
                ChatColor.YELLOW + "飞行时脚下生成飞剑",
                ChatColor.GRAY + "停下时飞剑消失",
                ChatColor.RED + "御剑飞行之术"));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
                new AttributeModifier(new NamespacedKey(getplugin, "flying_sword_damage"),
                        8.0, AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.MAINHAND));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, FlyingSwordKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 瞬步刃: 右键瞬移，指向目标则瞬移到身后并造成伤害 */
    public static ItemStack FlashStepBlade(int amount) {
        ItemStack item = new ItemStack(NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(FLASH_STEP_BLADE_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.DARK_PURPLE + "右键: 向前瞬移",
                ChatColor.LIGHT_PURPLE + "指向目标时: 瞬移至目标身后并造成伤害",
                ChatColor.GRAY + "暗影步法之刃"));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
                new AttributeModifier(new NamespacedKey(getplugin, "flash_step_damage"),
                        6.0, AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.MAINHAND));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, FlashStepBladeKey, 1);
        item.setAmount(amount);
        return item;
    }

    // ========================================================================
    // 食物 / 药品
    // ========================================================================

    private static final String REJUVENATION_POWDER_NAME = ChatColor.LIGHT_PURPLE + "回春散";
    private static final String HEMOSTATIC_BANDAGE_NAME = ChatColor.RED + "止血绷带";
    private static final String COMPRESSED_BISCUIT_NAME = ChatColor.GOLD + "压缩饼干";

    /** 回春散: 生命恢复V 3s，有概率合成出高级品 */
    public static ItemStack RejuvenationPowder(int amount) {
        return createRejuvenationPowder(amount, 0);
    }

    /** 回春散: tier=1上品, tier=2极品, tier=3仙品 */
    public static ItemStack RejuvenationPowder(int amount, int tier) {
        return createRejuvenationPowder(amount, tier);
    }

    private static ItemStack createRejuvenationPowder(int amount, int tier) {
        ItemStack item = new ItemStack(GLASS_BOTTLE);
        ItemMeta meta = item.getItemMeta();
        String tierLabel;
        if (tier >= 3) {
            meta.setDisplayName(ChatColor.GOLD + "仙品·回春散");
            meta.setLore(Arrays.asList(
                    ChatColor.LIGHT_PURPLE + "右键使用",
                    ChatColor.GOLD + "清除所有负面效果",
                    ChatColor.RED + "生命恢复 X 120秒"));
            tierLabel = "仙品";
        } else if (tier >= 2) {
            meta.setDisplayName(ChatColor.YELLOW + "极品·回春散");
            meta.setLore(Arrays.asList(
                    ChatColor.LIGHT_PURPLE + "右键使用",
                    ChatColor.GOLD + "清除所有负面效果",
                    ChatColor.RED + "生命恢复 X 40秒"));
            tierLabel = "极品";
        } else if (tier >= 1) {
            meta.setDisplayName(ChatColor.GREEN + "上品·回春散");
            meta.setLore(Arrays.asList(
                    ChatColor.LIGHT_PURPLE + "右键使用",
                    ChatColor.RED + "生命恢复 V 6秒"));
            tierLabel = "上品";
        } else {
            meta.setDisplayName(REJUVENATION_POWDER_NAME);
            meta.setLore(Arrays.asList(
                    ChatColor.LIGHT_PURPLE + "右键使用",
                    ChatColor.RED + "生命恢复 V 3秒"));
            tierLabel = "普通";
        }
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, RejuvenationPowderKey, tier + 1);
        item.setAmount(amount);
        return item;
    }

    /** 止血绷带 */
    public static ItemStack HemostaticBandage(int amount) {
        ItemStack item = new ItemStack(WHITE_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(HEMOSTATIC_BANDAGE_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "右键使用",
                ChatColor.RED + "瞬间恢复生命"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, HemostaticBandageKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 压缩饼干: 瞬间吃掉，等于9块面包 */
    public static ItemStack CompressedBiscuit(int amount) {
        ItemStack item = new ItemStack(BREAD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(COMPRESSED_BISCUIT_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "右键瞬间食用",
                ChatColor.GOLD + "恢复等同于9块面包的饱食度"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, CompressedBiscuitKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 盐 */
    public static ItemStack Salt(int amount) {
        ItemStack item = new ItemStack(SUGAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "盐");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "调味料",
                ChatColor.GRAY + "用于制作肉干和腐肉干"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SaltKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 肉干: 恢复6饥饿值，7.2饱和度 */
    public static ItemStack Jerky(int amount) {
        ItemStack item = new ItemStack(COOKED_BEEF);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "肉干");
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "右键食用",
                ChatColor.GOLD + "恢复6点饥饿值",
                ChatColor.YELLOW + "7.2饱和度"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, JerkyKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 甜浆果派: 恢复9饥饿值，8饱和度 */
    public static ItemStack SweetBerryPie(int amount) {
        ItemStack item = new ItemStack(PUMPKIN_PIE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "甜浆果派");
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "右键食用",
                ChatColor.GOLD + "恢复9点饥饿值",
                ChatColor.YELLOW + "8.0饱和度"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SweetBerryPieKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 酒桶: 放置后打开有9瓶酒 */
    public static ItemStack WineBarrel(int amount) {
        ItemStack item = new ItemStack(BARREL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "酒桶");
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "放置到地上右键打开",
                ChatColor.GOLD + "内含9瓶随机品质的酒"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, WineBarrelKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 酒 (tier: 1=酒, 2=佳酿, 3=金樽清酒) */
    public static ItemStack Wine(int amount, int tier) {
        ItemStack item = new ItemStack(POTION);
        ItemMeta meta = item.getItemMeta();
        String name;
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "右键饮用");
        if (tier >= 3) {
            name = ChatColor.GOLD + "金樽清酒";
            lore.add(ChatColor.GOLD + "力量 V 持续140秒");
            lore.add(ChatColor.RED + "持续时间内死亡可复活一次");
        } else if (tier >= 2) {
            name = ChatColor.YELLOW + "佳酿";
            lore.add(ChatColor.YELLOW + "力量 III 持续45秒");
        } else {
            name = ChatColor.WHITE + "酒";
            lore.add(ChatColor.GRAY + "力量 II 持续30秒");
        }
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, WineKey, tier);
        item.setAmount(amount);
        return item;
    }

    /** 腐肉干: 恢复4饥饿值，3饱和度 */
    public static ItemStack RottenJerky(int amount) {
        ItemStack item = new ItemStack(ROTTEN_FLESH);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GRAY + "腐肉干");
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "右键食用",
                ChatColor.GOLD + "恢复4点饥饿值",
                ChatColor.YELLOW + "3.0饱和度"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, RottenJerkyKey, 1);
        item.setAmount(amount);
        return item;
    }

    // ========================================================================
    // 内部工具方法
    // ========================================================================

    /**
     * 通用附魔书构造。
     *
     * @param amount   数量
     * @param level    附魔等级
     * @param key      PDC键 (用于存储附魔数据)
     * @param name     附魔显示名称 (不含等级)
     * @param loreLine 描述行
     */
    private static ItemStack createEnchantedBook(int amount, int level, NamespacedKey key,
                                                  String name, String... loreLine) {
        if (amount <= 0) amount = 1;
        if (level <= 0) level = 1;

        ItemStack book = new ItemStack(BOOK);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(name + romanNumeral(level));

        List<String> lore = new ArrayList<>(Arrays.asList(loreLine));
        lore.add(ChatColor.RESET + "拖动到装备上来使用");
        lore.add("级别" + level);
        meta.setLore(lore);

        book.setItemMeta(meta);
        ArmsorEnchant.addEnchant(book, key, level);
        book.setAmount(amount);
        return book;
    }
}
