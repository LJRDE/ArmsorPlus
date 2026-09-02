package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Dim_LJR.armsorPlus.ArmsorPlusEnchant.EnchantUtil.romanNumeral;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.BOOK;

// 附魔书 —— 拖动到对应装备上使用。
public class EnchantedBook {

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
    private static final String QUICKTHRUST_BOOK = ChatColor.GOLD + "疾刺";
    private static final String DIAMONDDRILL_BOOK = ChatColor.AQUA + "金刚钻";
    private static final String BLINDNESS_BOOK = ChatColor.DARK_GRAY + "失明";
    private static final String INDESTRUCTIBLE_BOOK = ChatColor.GOLD + "不灭";
    private static final String PROTECTIONPRO_BOOK = ChatColor.GOLD + "保护PRO";
    private static final String STUN_BOOK = ChatColor.DARK_GREEN + "眩晕";
    private static final String GOLEM_GUARDIAN_BOOK = ChatColor.GRAY + "傀儡守护者";
    private static final String CRITICAL_STRIKE_BOOK = ChatColor.RED + "暴击";
    private static final String PIERCING_BOOK = ChatColor.DARK_RED + "穿甲";
    private static final String LAVA_WALKER_BOOK = ChatColor.GOLD + "熔岩行者";
    private static final String LIGHTNING_CALL_BOOK = ChatColor.YELLOW + "唤雷";
    private static final String HOLOGRAPHIC_BOOK = ChatColor.AQUA + "全息";
    private static final String TRACKING_BOOK = ChatColor.GREEN + "追踪";
    private static final String HARVEST_BOOK = ChatColor.GOLD + "丰收";
    private static final String AUTO_PLANT_BOOK = ChatColor.GREEN + "自动种植";
    private static final String STRONG_BURST_BOOK = ChatColor.DARK_PURPLE + "强风暴";
    private static final String MULTI_SHOT_BOOK = ChatColor.LIGHT_PURPLE + "千重射击";
    private static final String POISON_BOOK = ChatColor.DARK_GREEN + "剧毒";
    private static final String SHARP_BLADE_BOOK = ChatColor.DARK_AQUA + "利刃";
    private static final String THUNDERCLAP_ARROW_BOOK = ChatColor.YELLOW + "惊雷";
    private static final String DAMAGE_DISPERSAL_BOOK = ChatColor.DARK_GREEN + "卸力";
    private static final String HERB_GUARD_BOOK = ChatColor.GREEN + "百草";
    private static final String PIERCE_BOOK = ChatColor.DARK_PURPLE + "贯穿";
    private static final String FIRE_BLADE_BOOK = ChatColor.RED + "火印";
    private static final String FROST_BLADE_BOOK = ChatColor.AQUA + "霜印";
    private static final String THUNDER_BLADE_BOOK = ChatColor.YELLOW + "雷印";
    private static final String MAGIC_BLADE_BOOK = ChatColor.DARK_PURPLE + "魔印";
    private static final String ICE_SPIKE_BOOK = ChatColor.AQUA + "冰刺";
    private static final String INFERNO_BOOK = ChatColor.RED + "烈焰";
    private static final String HEAVY_ARMOR_BOOK = ChatColor.DARK_GRAY + "重甲";
    private static final String EARTH_FAVOR_BOOK = ChatColor.DARK_GREEN + "地之眷顾";
    private static final String AMBUSH_BOOK = ChatColor.DARK_RED + "伏击";

    // 闪避附魔书: 靴子 - 概率闪避伤害, 满级V
    public static ItemStack Dodge_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, Dodgekey, DODGE_BOOK,
                "可用装备:靴子", "有" + (level * 8) + "%概率闪避对方的伤害", "满级V (40%)");
    }

    // 饥荒附魔书: 武器 - 造成饥饿效果
    public static ItemStack Famine_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, Faminekey, FAMINE_BOOK,
                "可用装备:武器", "给对方造成饥饿效果");
    }

    // 涟漪附魔书: 靴子 - 受到伤害时回复生命
    public static ItemStack Ripples_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, RipplesProtectkey, RIPPLES_BOOK,
                "可用装备:靴子", "受到伤害回复生命值");
    }

    // 血祭附魔书: 剑 - 概率扣血造成多倍伤害
    public static ItemStack BloodSacrifice_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, BloodSacrificekey, BLOODSACRIFICE_BOOK,
                "可用装备:剑", "概率扣自己的血量造成多倍伤害",
                "倍率为2~" + (level + 1) + "倍");
    }

    // 涤魂附魔书: 胸甲 - 获得负面效果时概率免除, 满级III
    public static ItemStack EffectClear_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, EffectClear, EFFECTCLEAR_BOOK,
                "可用装备:胸甲", "获得负面效果时有" + (level * 25) + "%概率免除", "满级III (75%)");
    }

    // 寒冻附魔书: 武器 - 造成减速效果
    public static ItemStack Freeze_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, FreezeKey, FREEZE_BOOK,
                "可用装备:武器", "攻击时" + (level * 10) + "%概率造成寒冻", "使对方移动速度下降");
    }

    // 格挡附魔书: 头盔 - 固定比例格挡伤害, 满级V
    public static ItemStack Blocking_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, BlockingKey, BLOCKING_BOOK,
                "可用装备:头盔", "格挡" + (level * 10) + "%伤害", "满级V (50%)");
    }

    // 凋零附魔书: 武器 - 造成凋零效果
    public static ItemStack Withering_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, WitheringKey, WITHERING_BOOK,
                "可用装备:武器", "攻击时给对方造成凋零效果");
    }

    // 幸存附魔书: 护腿 - 致命伤概率复活
    public static ItemStack Survivor_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, SurvivorKey, SURVIVOR_BOOK,
                "可用装备:裤子", "受到致命伤害概率复活");
    }

    // 复仇附魔书: 胸甲 - 反弹伤害
    public static ItemStack Revenge_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, RevengeKey, REVENGE_BOOK,
                "可用装备:胸甲", "反弹一定比例的伤害");
    }

    // 生命提升附魔书: 胸甲 - 增加最大生命值
    public static ItemStack HealthBoost_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, HealthBoostKey, HEALTHBOOST_BOOK,
                "可用装备:胸甲", "提升生命上限");
    }

    // 蓄爆附魔书: 弓/弩 - 概率发射爆炸箭
    public static ItemStack ExplosiveArrow_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, ExplosiveArrowKey, EXPLOSIVEARROW_BOOK,
                "可用装备:弓和弩", "有概率发射一枚火箭弹");
    }

    // 影避附魔书: 靴子 - 概率闪避所有伤害, 满级V
    public static ItemStack ShadowDodge_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, ShadowDodge, SHADOWDODGE_BOOK,
                "可用装备:靴子", "有" + (level * 8) + "%概率闪避所有伤害", "满级V (40%)");
    }

    // 弹道附魔书: 弓/弩 - 提升箭速和伤害
    public static ItemStack ArrowSpeed_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, ArrowSpeed, ARROWSPEED_BOOK,
                "可用装备:弓与弩", "提升射出弓箭的速度和伤害,每级提升一倍");
    }

    // 狙击附魔书: 弓/弩 - 大幅提升箭速和伤害 (需要前置:弹道)
    public static ItemStack Sniping_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, Sniping, SNIPING_BOOK,
                "可用装备:弓与弩", "提升射出弓箭的速度和伤害,每级提升5倍",
                "需要前置附魔[弹道]", "仅史诗及以上魔法球可以获得");
    }

    // 双重打击附魔书: 武器 - 概率双倍伤害
    public static ItemStack DoubleHit_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, DoubleHitkey, DOUBLEHIT_BOOK,
                "可用装备:武器", "有" + level * 20 + "%概率获得双倍伤害");
    }

    // 吸血附魔书: 武器 - 概率吸取生命
    public static ItemStack Feeding_EnchantdeBook(int amount, int level) {
        return createEnchantedBook(amount, level, Feedingkey, FEEDING_BOOK,
                "可用装备:武器", "攻击时吸取 " + String.format("%.1f", 1.5 * level) + " 点生命值");
    }

    // 疾刺附魔书: 长矛/三叉戟 - 右键速度提升
    public static ItemStack QuickThrust_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, QuickThrustKey, QUICKTHRUST_BOOK,
                "可用装备:三叉戟/长矛", "右键使用时移动速度提升" + (level * 10) + "%");
    }

    // 金刚钻附魔书: 镐子 - 挖掘黑曜石概率秒破
    public static ItemStack DiamondDrill_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, DiamondDrillKey, DIAMONDDRILL_BOOK,
                "可用装备:镐子", "挖掘黑曜石时" + (level * 20) + "%概率瞬间挖掉");
    }

    // 失明附魔书: 武器 - 攻击施加失明效果
    public static ItemStack Blindness_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, BlindnessKey, BLINDNESS_BOOK,
                "可用装备:武器", "攻击时" + (level * 10) + "%概率施加失明" + (level * 2) + "秒");
    }

    // 不灭附魔书: 任意装备 - 防止死亡 (仅管理员可获取)
    public static ItemStack Indestructible_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, IndestructibleKey, INDESTRUCTIBLE_BOOK,
                "可用装备:任意装备", "受到致命伤害时免疫死亡并回满状态");
    }

    // 保护PRO附魔书: 胸甲 - 每级额外减少6%伤害，满级V
    public static ItemStack ProtectionPRO_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, ProtectionPROKey, PROTECTIONPRO_BOOK,
                "可用装备:胸甲", "每级额外减少6%伤害", "满级V");
    }

    // 眩晕附魔书: 剑 - 攻击造成反胃效果，每级0.7秒，满级V
    public static ItemStack Stun_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, StunKey, STUN_BOOK,
                "可用装备:剑", "攻击时造成反胃效果" + String.format("%.1f", level * 0.7) + "秒", "满级V");
    }

    // 傀儡守护者附魔书: 胸甲 - 受伤召唤铁傀儡，每级1只，冷却300秒，满级V
    public static ItemStack GolemGuardian_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, GolemGuardianKey, GOLEM_GUARDIAN_BOOK,
                "可用装备:胸甲", "被攻击时召唤" + level + "只铁傀儡反击", "冷却300秒", "满级V");
    }

    // 暴击附魔书: 斧 - 概率造成额外伤害，满级V
    public static ItemStack CriticalStrike_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, CriticalStrikeKey, CRITICAL_STRIKE_BOOK,
                "可用装备:斧", "暴击概率" + (15 * level) + "%", "额外伤害" + (25 * level) + "%", "满级V");
    }

    // 穿甲附魔书: 弓/弩 - 概率使盾牌CD+5穿透伤害
    public static ItemStack Piercing_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, PiercingKey, PIERCING_BOOK,
                "可用装备:弓/弩", "击中时使敌方盾牌进入冷却", "造成5点穿透伤害");
    }

    // 熔岩行者附魔书: 靴子 - 岩浆行走5格内变为岩浆块5s后恢复
    public static ItemStack LavaWalker_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, LavaWalkerKey, LAVA_WALKER_BOOK,
                "可用装备:靴子", "在岩浆上行走时5格内岩浆变为岩浆块", "5秒后恢复");
    }

    // 唤雷附魔书: 三叉戟 - 无视天气召唤level道雷，满级III
    public static ItemStack LightningCall_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, LightningCallKey, LIGHTNING_CALL_BOOK,
                "可用装备:三叉戟", "无视天气召唤" + level + "道雷", "满级III");
    }

    // 全息附魔书: 盾牌 - 全角度持盾防御
    public static ItemStack Holographic_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, HolographicKey, HOLOGRAPHIC_BOOK,
                "可用装备:盾牌", "持盾防御扩展到全角度");
    }

    // 追踪附魔书: 弓 - 箭矢追踪450格内指向目标
    public static ItemStack Tracking_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, TrackingKey, TRACKING_BOOK,
                "可用装备:弓", "箭矢追踪450格内玩家指向的目标", "未指向目标则不生效");
    }

    // 丰收附魔书: 锄头 - 概率获得多倍收获，满级III
    public static ItemStack Harvest_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, HarvestKey, HARVEST_BOOK,
                "可用装备:锄头", (30 * level) + "%概率获得" + (level + 1) + "倍收获", "满级III");
    }

    // 自动种植附魔书: 锄头 - 自动种植副手种子
    public static ItemStack AutoPlant_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, AutoPlantKey, AUTO_PLANT_BOOK,
                "可用装备:锄头", "采集作物时自动补种副手种子");
    }

    // 强风暴附魔书: 重锤 - 无需下落即可触发风暴
    public static ItemStack StrongBurst_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, StrongBurstKey, STRONG_BURST_BOOK,
                "可用装备:重锤", "无需蓄力下落即可触发风暴");
    }

    // 千重射击附魔书: 弩 - 射击时多射level支箭，满级III
    public static ItemStack MultiShot_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, MultiShotKey, MULTI_SHOT_BOOK,
                "可用装备:弩", "射击时额外射出" + level + "支箭", "满级III");
    }

    // 剧毒附魔书: 武器 - 造成中毒效果
    public static ItemStack Poison_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, PoisonKey, POISON_BOOK,
                "可用装备:武器", "攻击时给对方造成中毒效果", "等级越高时间越长，最高中毒III");
    }

    // 利刃附魔书: 武器 - 目标护甲越低伤害越高
    public static ItemStack SharpBlade_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, SharpBladeKey, SHARP_BLADE_BOOK,
                "可用装备:武器", "目标护甲值越低伤害越高", "护甲超过18点时仅提升10%");
    }

    // 惊雷附魔书: 弓 - 命中召唤level道雷，未命中召唤1道雷，满级III
    public static ItemStack ThunderclapArrow_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, ThunderclapArrowKey, THUNDERCLAP_ARROW_BOOK,
                "可用装备:弓", "命中目标时召唤" + level + "道雷", "未命中(击中方块)召唤1道雷", "满级III");
    }

    // 卸力附魔书: 胸甲 - 受到100+伤害时拆分为多段，满级V
    public static ItemStack DamageDispersal_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, DamageDispersalKey, DAMAGE_DISPERSAL_BOOK,
                "可用装备:胸甲", "受到100+伤害时拆分为" + (level + 1) + "段", "每段间隔4tick");
    }

    // 百草附魔书: 胸甲 - 减少魔法/药水伤害, 概率免疫, 满级III
    public static ItemStack HerbGuard_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, HerbGuardKey, HERB_GUARD_BOOK,
                "可用装备:胸甲", "魔法/药水伤害减免" + (level * 15) + "%", "并有" + (level * 5) + "%概率免疫", "满级III");
    }

    // 贯穿附魔书: 长矛 - 冲锋攻击后固定穿透伤害, 满级III
    public static ItemStack Pierce_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, PierceKey, PIERCE_BOOK,
                "可用装备:长矛", "长矛冲锋攻击后固定造成" + (level * 2) + "点穿透伤害", "满级III");
    }

    // 火印附魔书: 武器 - 攻击转为火焰伤害
    public static ItemStack FireBlade_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, FireBladeKey, FIRE_BLADE_BOOK,
                "可用装备:武器", "攻击时将伤害转化为火焰伤害并点燃目标");
    }

    // 霜印附魔书: 武器 - 攻击转为冰冻伤害
    public static ItemStack FrostBlade_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, FrostBladeKey, FROST_BLADE_BOOK,
                "可用装备:武器", "攻击时将伤害转化为冰冻伤害并减速目标");
    }

    // 雷印附魔书: 武器 - 攻击转为雷电伤害
    public static ItemStack ThunderBlade_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, ThunderBladeKey, THUNDER_BLADE_BOOK,
                "可用装备:武器", "攻击时将伤害转化为雷电伤害");
    }

    // 魔印附魔书: 武器 - 攻击转为魔法伤害
    public static ItemStack MagicBlade_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, MagicBladeKey, MAGIC_BLADE_BOOK,
                "可用装备:武器", "攻击时将伤害转化为魔法伤害");
    }

    // 冰刺附魔书: 武器 - 额外冰冻伤害, 满级III
    public static ItemStack IceSpike_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, IceSpikeKey, ICE_SPIKE_BOOK,
                "可用装备:武器", "造成伤害时额外造成" + (level * 5) + "点冰冻伤害", "满级III (15点)");
    }

    // 烈焰附魔书: 武器 - 额外火焰伤害, 满级III
    public static ItemStack Inferno_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, InfernoKey, INFERNO_BOOK,
                "可用装备:武器", "造成伤害时额外造成" + (level * 5) + "点火焰伤害", "满级III (15点)");
    }

    // 重甲附魔书: 胸甲 - 给予缓慢II和抗性提升II
    public static ItemStack HeavyArmor_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, HeavyArmorKey, HEAVY_ARMOR_BOOK,
                "可用装备:胸甲", "穿戴时给予缓慢II和抗性提升II", "以速度为代价换取防御");
    }

    // 地之眷顾附魔书: 铲子 - 挖泥土概率掉落金粒/铁粒
    public static ItemStack EarthFavor_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, EarthFavorKey, EARTH_FAVOR_BOOK,
                "可用装备:铲子", "挖掘泥土时获得" + (level * 10) + "%概率掉落矿物粒");
    }

    // 伏击附魔书: 盾牌 - 放下盾牌后短时间增伤, 满级III
    public static ItemStack Ambush_EnchantedBook(int amount, int level) {
        return createEnchantedBook(amount, level, AmbushKey, AMBUSH_BOOK,
                "可用装备:盾牌", "收起盾牌后" + String.format("%.1f", 0.2 * level) + "秒内提升" + (level * 15) + "%伤害", "满级III");
    }

    // ========================================================================
    // 内部工具方法
    // ========================================================================

    // 通用附魔书构造。
    //
    // @param amount   数量
    // @param level    附魔等级
    // @param key      PDC键 (用于存储附魔数据)
    // @param name     附魔显示名称 (不含等级)
    // @param loreLine 描述行
    static ItemStack createEnchantedBook(int amount, int level, NamespacedKey key,
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
