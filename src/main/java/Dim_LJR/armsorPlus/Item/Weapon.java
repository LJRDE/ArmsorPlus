package Dim_LJR.armsorPlus.Item;

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
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Collections;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;

/**
 * 武器 —— 自定义武器和法杖等战斗用品。
 */
public class Weapon {

    private static final String BLOOD_SWORD_NAME = ChatColor.RED + "血祭之剑";
    private static final String DAGGER_NAME = ChatColor.DARK_GREEN + "匕首";
    private static final String THROWING_AXE_NAME = ChatColor.GOLD + "飞斧";
    private static final String SKELETON_SCEPTER_NAME = ChatColor.DARK_GRAY + "骷髅权杖";
    private static final String FROST_BOW_NAME = ChatColor.AQUA + "寒冰弓";
    private static final String FLAME_HALBERD_NAME = ChatColor.GOLD + "火焰戟";
    private static final String RAIN_SWORD_NAME = ChatColor.DARK_AQUA + "雨御前";
    private static final String FLYING_SWORD_NAME = ChatColor.GOLD + "飞天御剑";
    private static final String FLASH_STEP_BLADE_NAME = ChatColor.DARK_PURPLE + "瞬步刃";
    private static final String MAGIC_STICK_NAME = ChatColor.LIGHT_PURPLE + "法杖";
    private static final String ICE_SWORD_NAME = ChatColor.AQUA + "寒冰剑";
    private static final String WEB_BOW_NAME = ChatColor.WHITE + "盘丝弓";
    private static final String EXPLOSION_BOW_NAME = ChatColor.RED + "爆炸弓";

    /** 血祭之剑: 攻击时概率消耗生命造成多倍伤害 */
    public static ItemStack BloodSword(int amount) {
        ItemStack item = new ItemStack(GOLDEN_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.SHARPNESS, 5, true);
        meta.setDisplayName(BLOOD_SWORD_NAME);
        meta.setLore(Collections.singletonList(
                ChatColor.DARK_RED + "血祭V: 攻击时有100%概率消耗15点生命值造成2~6倍伤害"));
        meta.setCustomModelData(20260511);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, BloodSacrificekey, 5);
        item.setAmount(amount);
        return item;
    }

    /** 重剑: 基础攻击+7.5的铁剑 */
    public static ItemStack Iron_Epee(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "重剑");
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(
                        NamespacedKey.fromString("armsorplus:custom_damage"),
                        7.5,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.MAINHAND));
        item.setItemMeta(meta);
        item.setAmount(amount);
        return item;
    }

    /** 尸王: 基础+9伤害, 饥荒II+剧毒II */
    public static ItemStack CorpseKing(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GREEN + "尸王");
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:corpse_king_damage"),
                        9.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Arrays.asList(
                ChatColor.DARK_GREEN + "饥荒 II · 剧毒 II",
                ChatColor.GRAY + "尸王之力，腐蚀生灵"));
        meta.setCustomModelData(20260520);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, CorpseKingKey, 1);
        ArmsorEnchant.addEnchant(item, Faminekey, 2);
        ArmsorEnchant.addEnchant(item, PoisonKey, 2);
        item.setAmount(amount);
        return item;
    }

    /** 钢剑: 攻击力7 */
    public static ItemStack SteelSword(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "钢剑");
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:steel_sword_damage"),
                        7.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "精钢锻造的利剑"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SteelSwordKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 钢头盔: 2护甲 1韧性 */
    public static ItemStack SteelHelmet(int amount) {
        return createSteelArmor(IRON_HELMET, ChatColor.GRAY + "钢头盔", 2.0, 1.0, SteelHelmetKey, "armsorplus:steel_helmet", amount);
    }
    /** 钢胸甲: 7护甲 2韧性 */
    public static ItemStack SteelChestplate(int amount) {
        return createSteelArmor(IRON_CHESTPLATE, ChatColor.GRAY + "钢胸甲", 7.0, 2.0, SteelChestplateKey, "armsorplus:steel_chest", amount);
    }
    /** 钢护腿: 6护甲 2韧性 */
    public static ItemStack SteelLeggings(int amount) {
        return createSteelArmor(IRON_LEGGINGS, ChatColor.GRAY + "钢护腿", 6.0, 2.0, SteelLeggingsKey, "armsorplus:steel_legs", amount);
    }
    /** 钢靴子: 3护甲 1韧性 */
    public static ItemStack SteelBoots(int amount) {
        return createSteelArmor(IRON_BOOTS, ChatColor.GRAY + "钢靴子", 3.0, 1.0, SteelBootsKey, "armsorplus:steel_boots", amount);
    }

    private static ItemStack createSteelArmor(Material material, String name, double armor, double toughness, NamespacedKey key, String nsKey, int amount) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.addAttributeModifier(Attribute.ARMOR,
                new AttributeModifier(NamespacedKey.fromString(nsKey + "_armor"), armor, AttributeModifier.Operation.ADD_NUMBER, getSlot(material)));
        meta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
                new AttributeModifier(NamespacedKey.fromString(nsKey + "_tough"), toughness, AttributeModifier.Operation.ADD_NUMBER, getSlot(material)));
        meta.setLore(Arrays.asList(ChatColor.GRAY + "精钢锻造", ChatColor.GRAY + "护甲: +" + (int)armor + " 韧性: +" + (int)toughness));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, key, 1);
        item.setAmount(amount);
        return item;
    }

    private static EquipmentSlotGroup getSlot(Material mat) {
        String n = mat.name();
        if (n.contains("HELMET")) return EquipmentSlotGroup.HEAD;
        if (n.contains("CHESTPLATE")) return EquipmentSlotGroup.CHEST;
        if (n.contains("LEGGINGS")) return EquipmentSlotGroup.LEGS;
        return EquipmentSlotGroup.FEET;
    }

    /** 桃木剑: 亡灵杀手V */
    public static ItemStack PeachWoodSword(int amount) {
        ItemStack item = new ItemStack(WOODEN_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.SMITE, 5, true);
        meta.setDisplayName(ChatColor.GREEN + "桃木剑");
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:peach_wood_damage"),
                        5.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Collections.singletonList(
                ChatColor.DARK_GREEN + "桃木镇邪，亡灵克星"));
        meta.setCustomModelData(20260519);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, PeachWoodSwordKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 烈阳: 基础+8, 白天额外+4, 火焰附加V */
    public static ItemStack BlazingSun(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.FIRE_ASPECT, 5, true);
        meta.setDisplayName(ChatColor.GOLD + "烈阳");
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:blazing_sun_damage"),
                        8.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "白天时额外造成4点伤害",
                ChatColor.RED + "火焰附加 V",
                ChatColor.GRAY + "骄阳烈焰"));
        meta.setCustomModelData(20260518);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, BlazingSunKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 雷光: 基础伤害8点, 雷雨天伤害提升25% */
    public static ItemStack ThunderGlow(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "雷光");
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:thunder_glow_damage"),
                        8.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Arrays.asList(
                ChatColor.YELLOW + "雷雨天时伤害提升25%",
                ChatColor.GRAY + "闪耀雷电之力"));
        meta.setCustomModelData(20260517);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, ThunderGlowKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 玄武剑: 每造成200点伤害伤害+1，上限+8 */
    public static ItemStack BlackTortoiseSword(int amount) {
        ItemStack item = new ItemStack(STONE_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.SHARPNESS, 2, true);
        meta.setDisplayName(ChatColor.DARK_GREEN + "玄武剑");
        meta.setLore(Arrays.asList(
                ChatColor.GREEN + "每造成200点伤害 → 伤害+1",
                ChatColor.DARK_GREEN + "当前加成: 0/8",
                ChatColor.GRAY + "累计伤害: 0/200"));
        meta.getPersistentDataContainer().set(TortoiseAccumulatedKey, PersistentDataType.DOUBLE, 0.0);
        meta.setCustomModelData(20260516);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, BlackTortoiseSwordKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 星痕剑: 夜晚造成伤害时伤害提升100% */
    public static ItemStack StarTraceSword(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.SHARPNESS, 3, true);
        meta.setDisplayName(ChatColor.DARK_AQUA + "星痕剑");
        meta.setLore(Arrays.asList(
                ChatColor.AQUA + "夜晚时伤害提升100%",
                ChatColor.DARK_AQUA + "由七颗珍珠点缀而成，对应北斗七星"));
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:thunder_glow_damage"),
                        8.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));

        meta.setCustomModelData(20260515);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, StarTraceSwordKey, 1);
        ArmsorEnchant.addEnchant(item,FreezeKey,2);
        ArmsorEnchant.addEnchant(item,IceSpikeKey,1);
        item.setAmount(amount);
        return item;
    }

    /** 噬生: 造成伤害时随机扣除对方或者自生血量提升伤害*/
    public static ItemStack DevourLifeSword(int amount) {
        ItemStack item = new ItemStack(NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "噬生");
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(
                        NamespacedKey.fromString("armsorplus:custom_damage"),
                        4,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.MAINHAND));
        meta.setLore(Collections.singletonList(
                ChatColor.DARK_RED + "血裂数: 0/20"));
        meta.getPersistentDataContainer().set(ScoreKey,
                PersistentDataType.DOUBLE,
                0.0);
        meta.getPersistentDataContainer().set(DevourLifeBloodTimestamps,
                PersistentDataType.STRING,
                "");
        meta.setCustomModelData(20260514);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, DevourLifeSwordKey, 5);
        item.setAmount(amount);
        return item;
    }

    /** 匕首: 每次攻击必定造成5点额外伤害 */
    public static ItemStack Dagger(int amount) {
        ItemStack item = new ItemStack(NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(DAGGER_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.GREEN + "每次攻击额外造成5点伤害",
                ChatColor.GRAY + "轻盈而致命的短剑"));
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
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

    /** 雨御前: 右键3秒隐身+无敌+冰霜领域，冷却15s */
    public static ItemStack RainSword(int amount) {
        ItemStack item = new ItemStack(DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(RAIN_SWORD_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.AQUA + "右键: 3秒隐身+免疫伤害",
                ChatColor.AQUA + "释放冰霜领域: 周围生物缓慢255+挖掘疲劳3秒",
                ChatColor.DARK_AQUA + "冷却时间: 15秒",
                ChatColor.GRAY + "冰霜之剑"));
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(new NamespacedKey(getplugin, "rain_sword_damage"),
                        7.0, AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.MAINHAND));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, RainSwordKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 飞天御剑: 右键悬空飞行+脚下飞剑，沿指向方向飞行 */
    public static ItemStack FlyingSword(int amount) {
        ItemStack item = new ItemStack(GOLDEN_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(FLYING_SWORD_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键: 悬空飞行(速度8m/s)",
                ChatColor.YELLOW + "飞行时脚下生成飞剑",
                ChatColor.GRAY + "停下时飞剑消失",
                ChatColor.RED + "御剑飞行之术"));
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
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
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(new NamespacedKey(getplugin, "flash_step_damage"),
                        6.0, AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.MAINHAND));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, FlashStepBladeKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 法杖: 左键发射魔法球，命中造成20伤害+着火3秒 */
    public static ItemStack MagicStick(int amount) {
        ItemStack item = new ItemStack(BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MAGIC_STICK_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "左键: 发射魔法球",
                ChatColor.RED + "命中造成20点伤害+着火3秒",
                ChatColor.DARK_AQUA + "冷却: 0.5秒",
                ChatColor.GRAY + "蕴含魔力的法杖"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, MagicStickKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 寒冰剑: 攻击时对敌方造成缓慢II 3秒 */
    public static ItemStack IceSword(int amount) {
        ItemStack item = new ItemStack(DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ICE_SWORD_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.AQUA + "攻击时对敌方造成缓慢II 3秒",
                ChatColor.GRAY + "极寒之刃"));
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(new NamespacedKey(getplugin, "ice_sword_damage"),
                        7.0, AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.MAINHAND));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, IceSwordKey, 1);
        item.setAmount(amount);
        return item;
    }

    /** 盘丝弓: 击中目标时生成蜘蛛网30秒，PDC计数9次后损坏 */
    public static ItemStack WebBow(int amount) {
        ItemStack item = new ItemStack(BOW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(WEB_BOW_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.WHITE + "攻击时在敌方周围生成蜘蛛网持续30秒",
                ChatColor.DARK_GRAY + "使用9次后必定损坏",
                ChatColor.YELLOW + "剩余次数: 9",
                ChatColor.GRAY + "蛛网缠绕之弓"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, WebBowKey, 1);
        ArmsorEnchant.addEnchant(item, WebBowUsesKey, 9);
        item.setAmount(amount);
        return item;
    }

    /** 爆炸弓: 击中目标时产生爆炸，PDC计数9次后损坏 */
    public static ItemStack ExplosionBow(int amount) {
        ItemStack item = new ItemStack(BOW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(EXPLOSION_BOW_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.RED + "攻击时在敌方周围产生爆炸",
                ChatColor.DARK_GRAY + "使用9次后必定损坏",
                ChatColor.YELLOW + "剩余次数: 9",
                ChatColor.GRAY + "爆破之力"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, ExplosionBowKey, 1);
        ArmsorEnchant.addEnchant(item, ExplosionBowUsesKey, 9);
        item.setAmount(amount);
        return item;
    }
}
