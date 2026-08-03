package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 自定义附魔系统 —— 基于 PersistentDataContainer (PDC) 存储。
// 所有自定义附魔不依赖原版附魔系统，而是通过 PDC 将附魔等级直接写入物品的
// PersistentDataContainer，从而实现与原版附魔完全独立的自定义附魔体系。
// 核心方法:
// - addEnchant(ItemStack, NamespacedKey, int) — 写入附魔
// - getEnchantLevel(ItemStack, NamespacedKey) — 读取附魔等级
// - addEnchantLore(ItemStack, String, int, NamespacedKey) — 添加Lore显示
// - getEnchantDisplayName(NamespacedKey) — 获取附魔中文名
public class ArmsorEnchant {

    // 向物品写入自定义附魔等级 (PDC)
    public static ItemStack addEnchant(ItemStack item, NamespacedKey key, int level) {
        item.editMeta(meta -> meta.getPersistentDataContainer()
                .set(key, PersistentDataType.INTEGER, level));
        return item;
    }

    // 读取物品上指定自定义附魔的等级 (0=无此附魔)
    public static int getEnchantLevel(ItemStack item, NamespacedKey key) {
        if (item == null || !item.hasItemMeta()) return 0;
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (container.has(key, PersistentDataType.INTEGER)) {
            return container.get(key, PersistentDataType.INTEGER);
        }
        return 0;
    }

    // 为物品添加附魔 Lore 显示行 (自动去重)
    public static ItemStack addEnchantLore(ItemStack item, String lore, int level, NamespacedKey key) {
        if (item == null || item.getType() == Material.AIR) return item;
        removeEnchantLore(item, key);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        List<String> loreList = meta.hasLore()
                ? new ArrayList<>(meta.getLore())
                : new ArrayList<>();
        loreList.add(generateEnchantLore(lore, level));
        meta.setLore(loreList);
        item.setItemMeta(meta);
        return item;
    }

    // 根据关键字删除Lore行
    public static boolean removeLoreLine(ItemStack item, String keyword) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return false;

        List<String> lore = meta.getLore();
        List<String> newLore = new ArrayList<>();
        boolean removed = false;
        String strippedKeyword = ChatColor.stripColor(keyword);

        for (String line : lore) {
            if (ChatColor.stripColor(line).contains(strippedKeyword)) {
                removed = true;
            } else {
                newLore.add(line);
            }
        }
        if (removed) {
            meta.setLore(newLore);
            item.setItemMeta(meta);
            return true;
        }
        return false;
    }

    // 移除指定附魔的 Lore 显示
    public static void removeEnchantLore(ItemStack item, NamespacedKey key) {
        String name = getEnchantDisplayName(key);
        if (name != null) removeLoreLine(item, name);
    }

    // 生成带罗马数字等级的附魔 Lore 文本
    public static String generateEnchantLore(String baseLore, int level) {
        return baseLore + switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(level);
        };
    }

    // 通过 NamespacedKey 获取附魔的中文显示名称
    public static String getEnchantDisplayName(NamespacedKey key) {
        if (key.equals(FreezeKey))           return "寒冻";
        if (key.equals(Faminekey))           return "饥荒";
        if (key.equals(RevengeKey))          return "复仇";
        if (key.equals(WitheringKey))        return "凋零";
        if (key.equals(RipplesProtectkey))   return "涟漪";
        if (key.equals(Dodgekey))            return "闪避";
        if (key.equals(HealthBoostKey))      return "生命提升";
        if (key.equals(SurvivorKey))         return "幸存";
        if (key.equals(BloodSacrificekey))   return "血祭";
        if (key.equals(ExplosiveArrowKey))   return "蓄爆";
        if (key.equals(EffectClear))         return "涤魂";
        if (key.equals(BlockingKey))         return "格挡";
        if (key.equals(ShadowDodge))         return "影避";
        if (key.equals(ArrowSpeed))          return "弹道";
        if (key.equals(Sniping))             return "狙击";
        if (key.equals(DoubleHitkey))        return "双重打击";
        if (key.equals(Feedingkey))          return "吸血";
        if (key.equals(QuickThrustKey))     return "疾刺";
        if (key.equals(DiamondDrillKey))   return "金刚钻";
        if (key.equals(IndestructibleKey)) return "不灭";
        if (key.equals(BlindnessKey))      return "失明";
        if (key.equals(ProtectionPROKey))  return "保护PRO";
        if (key.equals(StunKey))           return "眩晕";
        if (key.equals(GolemGuardianKey))  return "傀儡守护者";
        if (key.equals(CriticalStrikeKey)) return "暴击";
        if (key.equals(PiercingKey))       return "穿甲";
        if (key.equals(LavaWalkerKey))     return "熔岩行者";
        if (key.equals(LightningCallKey))  return "唤雷";
        if (key.equals(HolographicKey))    return "全息";
        if (key.equals(TrackingKey))       return "追踪";
        if (key.equals(HarvestKey))        return "丰收";
        if (key.equals(AutoPlantKey))      return "自动种植";
        if (key.equals(StrongBurstKey))    return "强风暴";
        if (key.equals(MultiShotKey))      return "千重射击";
        if (key.equals(PoisonKey))         return "剧毒";
        if (key.equals(SharpBladeKey))     return "利刃";
        if (key.equals(ThunderclapArrowKey)) return "惊雷";
        if (key.equals(DamageDispersalKey))  return "卸力";
        if (key.equals(HerbGuardKey))        return "百草";
        if (key.equals(FireBladeKey))        return "火印";
        if (key.equals(FrostBladeKey))       return "霜印";
        if (key.equals(ThunderBladeKey))     return "雷印";
        if (key.equals(MagicBladeKey))       return "魔印";
        if (key.equals(IceSpikeKey))         return "冰刺";
        if (key.equals(InfernoKey))          return "烈焰";
        if (key.equals(HeavyArmorKey))       return "重甲";
        if (key.equals(EarthFavorKey))       return "地之眷顾";
        if (key.equals(AmbushKey))           return "伏击";
        return null;
    }
}
