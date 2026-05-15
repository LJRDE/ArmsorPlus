package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.ChatColor;
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
