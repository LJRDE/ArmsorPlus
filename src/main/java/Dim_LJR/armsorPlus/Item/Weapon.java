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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Collections;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;

// 武器 —— 自定义武器和法杖等战斗用品。
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

    // 血祭之剑: 攻击时概率消耗生命造成多倍伤害
    public static ItemStack BloodSword(int amount) {
        ItemStack item = new ItemStack(GOLDEN_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.SHARPNESS, 5, true);
        meta.setDisplayName(BLOOD_SWORD_NAME);
        meta.setLore(Collections.singletonList(
                ChatColor.DARK_RED + "血祭V: 攻击时有100%概率消耗10点生命值造成2~6倍伤害"));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:blood_sword"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, BloodSacrificekey, 5);
        item.setAmount(amount);
        return item;
    }

    // 重剑: 基础攻击+7.5的铁剑
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

    // 尸王: 基础+9伤害, 饥荒II+剧毒II
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
        meta.setItemModel(NamespacedKey.fromString("armsorplus:corpse_king"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, CorpseKingKey, 1);
        ArmsorEnchant.addEnchant(item, Faminekey, 2);
        ArmsorEnchant.addEnchant(item, PoisonKey, 2);
        item.setAmount(amount);
        return item;
    }

    // 钢剑: 攻击力7
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

    // 钢头盔: 2护甲 1韧性
    public static ItemStack SteelHelmet(int amount) {
        return createSteelArmor(IRON_HELMET, ChatColor.GRAY + "钢头盔", 2.0, 1.0, SteelHelmetKey, "armsorplus:steel_helmet", amount);
    }
    // 钢胸甲: 7护甲 2韧性
    public static ItemStack SteelChestplate(int amount) {
        return createSteelArmor(IRON_CHESTPLATE, ChatColor.GRAY + "钢胸甲", 7.0, 2.0, SteelChestplateKey, "armsorplus:steel_chest", amount);
    }
    // 钢护腿: 6护甲 2韧性
    public static ItemStack SteelLeggings(int amount) {
        return createSteelArmor(IRON_LEGGINGS, ChatColor.GRAY + "钢护腿", 6.0, 2.0, SteelLeggingsKey, "armsorplus:steel_legs", amount);
    }
    // 钢靴子: 3护甲 1韧性
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

    // 桃木剑: 亡灵杀手V
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
        meta.setItemModel(NamespacedKey.fromString("armsorplus:peach_wood_sword"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, PeachWoodSwordKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 烈阳: 基础+8, 白天额外+4, 火焰附加V
    public static ItemStack BlazingSun(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.FIRE_ASPECT, 5, true);
        meta.setDisplayName(ChatColor.GOLD + "烈阳");
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:blazing_sun_damage"),
                        10.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "白天时额外造成8点伤害",
                ChatColor.RED + "火焰附加 V",
                ChatColor.GRAY + "骄阳烈焰"));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:blazing_sun"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, BlazingSunKey, 1);
        ArmsorEnchant.addEnchant(item, FireBladeKey, 1); // 合成自带火印
        item.setAmount(amount);
        return item;
    }

    // 雷光: 基础伤害8点, 雷雨天伤害提升25%
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
        meta.setItemModel(NamespacedKey.fromString("armsorplus:thunder_glow"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, ThunderGlowKey, 1);
        ArmsorEnchant.addEnchant(item, ThunderBladeKey, 1); // 合成自带雷印
        item.setAmount(amount);
        return item;
    }

    // 玄武剑: 每造成200点伤害伤害+1，上限+8
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
        meta.setItemModel(NamespacedKey.fromString("armsorplus:black_tortoise_sword"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, BlackTortoiseSwordKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 星痕剑: 夜晚造成伤害时伤害提升100%
    public static ItemStack StarTraceSword(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.SHARPNESS, 3, true);
        meta.setDisplayName(ChatColor.DARK_AQUA + "星痕剑");
        meta.setLore(Arrays.asList(
                ChatColor.AQUA + "夜晚时伤害提升100%",
                ChatColor.DARK_AQUA + "由七颗珍珠点缀而成，对应北斗七星"));
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:star_trace_sword_damage"),
                        8.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));

        meta.setItemModel(NamespacedKey.fromString("armsorplus:star_trace_sword"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, StarTraceSwordKey, 1);
        ArmsorEnchant.addEnchant(item,FreezeKey,2);
        ArmsorEnchant.addEnchant(item, FrostBladeKey, 1); // 合成自带霜印
        item.setAmount(amount);
        return item;
    }

    // 噬生: 造成伤害时随机扣除对方或者自生血量提升伤害
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
        meta.setItemModel(NamespacedKey.fromString("armsorplus:devour_life_sword"));
        // 初始耐久: 0血裂 = 近乎空条 (保留1点耐久防止损坏, 攻击不消耗耐久由事件处理器拦截)
        if (meta instanceof Damageable dmg) {
            dmg.setDamage(NETHERITE_SWORD.getMaxDurability() - 1);
        }
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, DevourLifeSwordKey, 5);
        ArmsorEnchant.addEnchant(item, MagicBladeKey, 1); // 合成自带魔印
        item.setAmount(amount);
        return item;
    }

    // 匕首: 每次攻击必定造成5点额外伤害
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

    // 飞斧: 右键蓄力3s飞出，对沿途生物造成20点伤害
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

    // 骷髅权杖: 使用时降下箭雨
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

    // 寒冰弓: 射出时额外发射2支寒冰箭
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

    // 火焰戟: 攻击额外造成30火焰伤害，射出时灼烧沿途3×3
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

    // 雨御前: 右键3秒隐身+无敌+冰霜领域，冷却10s
    public static ItemStack RainSword(int amount) {
        ItemStack item = new ItemStack(DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(RAIN_SWORD_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.AQUA + "右键: 3秒隐身+免疫伤害",
                ChatColor.AQUA + "释放冰霜领域: 周围生物缓慢255+挖掘疲劳3秒",
                ChatColor.DARK_AQUA + "冷却时间: 10秒",
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

    // 飞天御剑: 右键悬空飞行+脚下飞剑，沿指向方向飞行
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

    // 瞬步刃: 右键瞬移，指向目标则瞬移到身后并造成伤害
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

    // 法杖: 左键发射魔法球，命中造成20伤害+着火3秒
    public static ItemStack MagicStick(int amount) {
        ItemStack item = new ItemStack(BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MAGIC_STICK_NAME);
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "左键: 发射魔法球",
                ChatColor.RED + "命中造成20点伤害+着火3秒",
                ChatColor.GRAY + "蕴含魔力的法杖"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, MagicStickKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 寒冰剑: 攻击时对敌方造成缓慢II 3秒
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

    // 盘丝弓: 击中目标时生成蜘蛛网30秒，PDC计数9次后损坏
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

    // 爆炸弓: 击中目标时产生爆炸，PDC计数9次后损坏
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

    // ========================================================================
    // 鱼骨系列武器 (0.3I+)
    // ========================================================================

    private static final String FISH_BONE_SWORD_NAME = ChatColor.YELLOW + "鱼骨剑";
    private static final String FISH_BONE_KNIFE_NAME = ChatColor.YELLOW + "鱼骨刀";
    private static final String FISH_SPINE_SWORD_NAME = ChatColor.YELLOW + "鱼刺剑";
    private static final String FISH_SPINE_KNIFE_NAME = ChatColor.YELLOW + "鱼刺刀";
    private static final String SEA_BONE_SWORD_NAME = ChatColor.AQUA + "海骨剑";
    private static final String SEA_BONE_KNIFE_NAME = ChatColor.AQUA + "海骨刀";
    private static final String SPIRIT_BONE_SWORD_NAME = ChatColor.LIGHT_PURPLE + "灵骨剑";
    private static final String SPIRIT_BONE_KNIFE_NAME = ChatColor.LIGHT_PURPLE + "灵骨刀";
    private static final String SEA_SPINE_SWORD_NAME = ChatColor.DARK_AQUA + "海刺剑";
    private static final String SEA_SPINE_KNIFE_NAME = ChatColor.DARK_AQUA + "海刺刀";
    private static final String CORRODE_BONE_SWORD_NAME = ChatColor.DARK_PURPLE + "蚀骨剑";
    private static final String SPIRIT_SPINE_SWORD_NAME = ChatColor.DARK_PURPLE + "灵刺剑";
    private static final String SPIRIT_SPINE_KNIFE_NAME = ChatColor.DARK_PURPLE + "灵刺刀";
    private static final String SEA_CRY_SWORD_NAME = ChatColor.DARK_BLUE + "海哭剑";
    private static final String SEA_CRY_KNIFE_NAME = ChatColor.DARK_BLUE + "海哭刀";

    // 鱼骨剑: 伤害5.0, 2鲑鱼+1骨头合成
    public static ItemStack FishBoneSword(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(FISH_BONE_SWORD_NAME);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:fish_bone_sword"),
                        5.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "鲑鱼之骨铸造的利剑"));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:fish_bone_sword"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, FishBoneSwordKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 鱼骨刀: 伤害5.0, 2鳕鱼+1骨头合成
    public static ItemStack FishBoneKnife(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(FISH_BONE_KNIFE_NAME);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:fish_bone_knife"),
                        5.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "鳕鱼之骨铸造的短刀"));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:fish_bone_knife"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, FishBoneKnifeKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 鱼刺剑: 伤害6.5, 4骨块+4海晶沙粒+鱼骨剑合成
    public static ItemStack FishSpineSword(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(FISH_SPINE_SWORD_NAME);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:fish_spine_sword"),
                        6.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "海晶沙粒淬炼的鱼刺之剑"));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:fish_spine_sword"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, FishSpineSwordKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 鱼刺刀: 伤害6.5, 4骨块+4海晶沙粒+鱼骨刀合成
    public static ItemStack FishSpineKnife(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(FISH_SPINE_KNIFE_NAME);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:fish_spine_knife"),
                        6.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "海晶沙粒淬炼的鱼刺之刀"));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:fish_spine_knife"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, FishSpineKnifeKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 海骨剑: 伤害7.5, 水中+10%伤害+10%移速
    public static ItemStack SeaBoneSword(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(SEA_BONE_SWORD_NAME);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:sea_bone_sword"),
                        7.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Arrays.asList(
                ChatColor.AQUA + "在水中/雨天时 伤害+10% 移速+10%",
                ChatColor.GRAY + "海绵孕育的海洋之剑"));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:sea_bone_sword"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SeaBoneSwordKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 海骨刀: 伤害7.5, 水中+16%伤害
    public static ItemStack SeaBoneKnife(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(SEA_BONE_KNIFE_NAME);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:sea_bone_knife"),
                        7.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Arrays.asList(
                ChatColor.AQUA + "在水中/雨天时 伤害+16%",
                ChatColor.GRAY + "海绵孕育的海洋之刀"));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:sea_bone_knife"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SeaBoneKnifeKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 灵骨剑: 伤害8.0, 光灵3s+水中15%穿透6点
    public static ItemStack SpiritBoneSword(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(SPIRIT_BONE_SWORD_NAME);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:spirit_bone_sword"),
                        8.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "攻击施加光灵效果3秒",
                ChatColor.AQUA + "在水中/雨天15%概率造成6点穿透伤害",
                ChatColor.GRAY + "灵魂沙淬炼的亡灵之剑"));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:spirit_bone_sword"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SpiritBoneSwordKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 灵骨刀: 伤害8.0, 光灵3s+水中15%双倍伤害
    public static ItemStack SpiritBoneKnife(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(SPIRIT_BONE_KNIFE_NAME);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:spirit_bone_knife"),
                        8.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "攻击施加光灵效果3秒",
                ChatColor.AQUA + "在水中/雨天15%概率造成双倍伤害",
                ChatColor.GRAY + "灵魂沙淬炼的亡灵之刀"));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:spirit_bone_knife"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SpiritBoneKnifeKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 海刺剑: 伤害10.0, 水中/雨天+40%伤害+水下呼吸
    public static ItemStack SeaSpineSword(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(SEA_SPINE_SWORD_NAME);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:sea_spine_sword"),
                        10.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Arrays.asList(
                ChatColor.DARK_AQUA + "在水中/雨天时 伤害+40%",
                ChatColor.AQUA + "在水中时 获得水下呼吸",
                ChatColor.GRAY + "鳞甲与海洋之心铸就的深海之剑"));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:sea_spine_sword"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SeaSpineSwordKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 海刺刀: 伤害12.0, 水中/雨天-40%受伤+水下呼吸
    public static ItemStack SeaSpineKnife(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(SEA_SPINE_KNIFE_NAME);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:sea_spine_knife"),
                        12.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Arrays.asList(
                ChatColor.DARK_AQUA + "在水中/雨天时 受到伤害-40%",
                ChatColor.AQUA + "在水中时 获得水下呼吸",
                ChatColor.GRAY + "鳞甲与海洋之心铸就的深海之刀"));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:sea_spine_knife"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SeaSpineKnifeKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 蚀骨剑: 伤害13.0, 自带凋零III
    public static ItemStack CorrodeBoneSword(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(CORRODE_BONE_SWORD_NAME);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:corrode_bone_sword"),
                        13.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Arrays.asList(
                ChatColor.DARK_PURPLE + "凋零 III",
                ChatColor.GRAY + "凋零骷髅的诅咒之剑"));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:corrode_bone_sword"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, CorrodeBoneSwordKey, 1);
        ArmsorEnchant.addEnchant(item, WitheringKey, 3);
        item.setAmount(amount);
        return item;
    }

    // 灵刺剑: 伤害13.0, 攻击必穿透1点+水中额外穿透2点
    public static ItemStack SpiritSpineSword(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(SPIRIT_SPINE_SWORD_NAME);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:spirit_spine_sword"),
                        13.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Arrays.asList(
                ChatColor.DARK_PURPLE + "攻击时必定造成1点穿透伤害",
                ChatColor.AQUA + "在水中/雨天额外造成2点穿透伤害",
                ChatColor.GRAY + "潮涌核心与恶魂之泪淬炼的灵刺"));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:spirit_spine_sword"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SpiritSpineSwordKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 灵刺刀: 伤害14.0, 水中/雨天伤害+5
    public static ItemStack SpiritSpineKnife(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(SPIRIT_SPINE_KNIFE_NAME);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:spirit_spine_knife"),
                        14.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Arrays.asList(
                ChatColor.DARK_PURPLE + "在水中/雨天时 伤害+5",
                ChatColor.GRAY + "潮涌核心与凋零骷髅之力淬炼的灵刺"));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:spirit_spine_knife"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SpiritSpineKnifeKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 海哭剑: 伤害16.0, 被动3药水+发光+疲劳+伤害增幅
    public static ItemStack SeaCrySword(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(SEA_CRY_SWORD_NAME);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:sea_cry_sword"),
                        16.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Arrays.asList(
                ChatColor.BLUE + "持有时: 水下呼吸+海豚恩惠+潮涌能量",
                ChatColor.LIGHT_PURPLE + "攻击: 发光+15%挖掘疲劳30s",
                ChatColor.RED + "攻击: 伤害+20% (水中翻倍至40%)",
                ChatColor.GRAY + "深海的哀鸣之剑"));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:sea_cry_sword"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SeaCrySwordKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 海哭刀: 伤害16.0, 被动3药水+发光+疲劳+伤害增幅
    public static ItemStack SeaCryKnife(int amount) {
        ItemStack item = new ItemStack(IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(SEA_CRY_KNIFE_NAME);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:sea_cry_knife"),
                        16.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setLore(Arrays.asList(
                ChatColor.BLUE + "持有时: 水下呼吸+海豚恩惠+潮涌能量",
                ChatColor.LIGHT_PURPLE + "攻击: 发光+15%挖掘疲劳30s",
                ChatColor.RED + "攻击: 伤害+45% (水中翻倍至90%)",
                ChatColor.GRAY + "深海的哀鸣之刀"));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:sea_cry_knife"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, SeaCryKnifeKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 幻影之刃: 幻术师的遗骸锻造, 攻击召唤幻影分身, 击杀生成幻影假身
    public static ItemStack IllusionBlade(int amount) {
        ItemStack item = new ItemStack(DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "幻影之刃");
        meta.setLore(Arrays.asList(
                ChatColor.LIGHT_PURPLE + "攻击时35%召唤幻影分身, 额外3点真实伤害",
                ChatColor.DARK_PURPLE + "击杀时25%生成幻影假身吸引附近怪物",
                ChatColor.GRAY + "由幻术师的遗骸锻造"));
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:illusion_blade_damage"),
                        10.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:illusion_blade"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, IllusionBladeKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 幻惑法杖: 幻术师的遗骨制作, 攻击概率失明, 右键发射幻术飞弹
    public static ItemStack IllusionStaff(int amount) {
        ItemStack item = new ItemStack(BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "幻惑法杖");
        meta.setLore(Arrays.asList(
                ChatColor.AQUA + "攻击时30%使目标失明",
                ChatColor.LIGHT_PURPLE + "右键发射幻术飞弹: 命中10点魔法伤害+反胃+失明",
                ChatColor.GRAY + "由幻术师的遗骨制作"));
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(NamespacedKey.fromString("armsorplus:illusion_staff_damage"),
                        6.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setItemModel(NamespacedKey.fromString("armsorplus:illusion_staff"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, IllusionStaffKey, 1);
        item.setAmount(amount);
        return item;
    }

    // ========================================================================
    // 吞云斩月刀 (0.3J+)
    // ========================================================================

    // 吞云斩月刀: 攻击7.4无法破坏, 右键突刺(最远3格), 指向生物则突刺至面前并造成[基础+锋利x2]伤害
    public static ItemStack CloudMoonBlade(int amount) {
        ItemStack item = new ItemStack(DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "吞云斩月刀");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "右键: 向前突刺 (最远3格)",
                ChatColor.LIGHT_PURPLE + "指向生物时: 突刺至其面前并造成[基础伤害+锋利x2]",
                ChatColor.DARK_RED + "突刺可触发: 双重打击、血祭",
                ChatColor.GRAY + "冷却: 0.2秒",
                ChatColor.GRAY + "吞云吐雾，斩月流光"));
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(new NamespacedKey(getplugin, "cloud_moon_blade_damage"),
                        7.4, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
        meta.setUnbreakable(true);
        meta.setItemModel(NamespacedKey.fromString("armsorplus:cloud_moon_blade"));
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, CloudMoonBladeKey, 1);
        item.setAmount(amount);
        return item;
    }

    // 狂怒掠夺者之弩: 弩原型, 力量V/快速装填V/耐久V/经验修补II
    public static ItemStack RagingPlundererCrossbow(int amount) {
        ItemStack item = new ItemStack(CROSSBOW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "狂怒掠夺者之弩");
        meta.setLore(Arrays.asList(
                ChatColor.GOLD + "力量 V",
                ChatColor.GOLD + "快速装填 V",
                ChatColor.GOLD + "耐久 V",
                ChatColor.GOLD + "经验修补 II",
                ChatColor.GRAY + "狂怒的掠夺者倾泻而下的怒火"));
        meta.addEnchant(Enchantment.POWER, 5, true);
        meta.addEnchant(Enchantment.QUICK_CHARGE, 5, true);
        meta.addEnchant(Enchantment.UNBREAKING, 5, true);
        meta.addEnchant(Enchantment.MENDING, 2, true);
        item.setItemMeta(meta);
        ArmsorEnchant.addEnchant(item, RagingPlundererCrossbowKey, 1);
        item.setAmount(amount);
        return item;
    }
}
