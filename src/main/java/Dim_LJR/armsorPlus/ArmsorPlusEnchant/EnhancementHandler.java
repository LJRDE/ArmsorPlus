package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.Objects;

import static Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant.addEnchantLore;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;

/**
 * 强化/附魔处理 —— 监听背包点击事件(拖拽物品到装备上)。
 * <p>
 * 处理逻辑:
 * - 强化石 → 提升原版附魔等级或添加属性修饰符
 * - 精炼金刚石 → 无限耐久
 * - 自定义附魔书 → 通过PDC添加自定义附魔
 */
public class EnhancementHandler implements Listener {

    // ===== 装备类型判断 =====

    private boolean isSwordOrAxe(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_SWORD") || type.name().endsWith("_AXE") || type == TRIDENT;
    }

    private boolean isArmor(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_HELMET")
                || type.name().endsWith("_CHESTPLATE")
                || type.name().endsWith("_LEGGINGS")
                || type.name().endsWith("_BOOTS");
    }

    private boolean isHelmet(ItemStack item) {
        return item.getType().name().endsWith("_HELMET");
    }

    private boolean isChestplate(ItemStack item) {
        return item.getType().name().endsWith("_CHESTPLATE");
    }

    private boolean isLeggings(ItemStack item) {
        return item.getType().name().endsWith("_LEGGINGS");
    }

    private boolean isBoots(ItemStack item) {
        return item.getType().name().endsWith("_BOOTS");
    }

    private boolean isWeaponOrArmor(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_SWORD")
                || type.name().endsWith("_AXE")
                || type == TRIDENT
                || isArmor(item)
                || type == BOW;
    }

    // ===== 武器等级对照 =====

    /**
     * 根据材质获取该装备的原版护甲值/武器伤害。
     * 用于二级强化石的属性叠加计算。
     */
    private int getBaseValue(ItemStack item) {
        Material mate = item.getType();
        return switch (mate) {
            case DIAMOND_SWORD -> 7;
            case DIAMOND_AXE -> 9;
            case DIAMOND_HELMET -> 3;
            case DIAMOND_CHESTPLATE -> 8;
            case DIAMOND_LEGGINGS -> 6;
            case DIAMOND_BOOTS -> 3;
            case IRON_SWORD -> 6;
            case IRON_AXE -> 9;
            case IRON_HELMET -> 2;
            case IRON_CHESTPLATE -> 6;
            case IRON_LEGGINGS -> 5;
            case IRON_BOOTS -> 2;
            case GOLDEN_SWORD -> 6;
            case GOLDEN_AXE -> 9;
            case GOLDEN_HELMET -> 2;
            case GOLDEN_CHESTPLATE -> 6;
            case GOLDEN_LEGGINGS -> 5;
            case GOLDEN_BOOTS -> 2;
            case NETHERITE_SWORD -> 8;
            case NETHERITE_AXE -> 9;
            case NETHERITE_HELMET -> 3;
            case NETHERITE_CHESTPLATE -> 8;
            case NETHERITE_LEGGINGS -> 6;
            case NETHERITE_BOOTS -> 3;
            case WOODEN_SWORD -> 4;
            case WOODEN_AXE -> 6;
            case LEATHER_HELMET -> 1;
            case LEATHER_CHESTPLATE -> 4;
            case LEATHER_LEGGINGS -> 3;
            case LEATHER_BOOTS -> 2;
            case STONE_SWORD -> 5;
            case STONE_AXE -> 9;
            case CHAINMAIL_HELMET -> 2;
            case CHAINMAIL_CHESTPLATE -> 5;
            case CHAINMAIL_LEGGINGS -> 4;
            case CHAINMAIL_BOOTS -> 3;
            default -> 0;
        };
    }

    // ===== 主监听器: 背包点击(拖拽)事件 =====

    /**
     * 监听背包点击事件，处理所有强化石/附魔书的拖拽使用。
     * <p>
     * 玩家将强化石或附魔书拖拽到装备上时触发对应的强化/附魔逻辑。
     */
    @EventHandler
    public void onPlayerInteract(InventoryClickEvent event) {
        if (event.getCursor() == null || event.getCurrentItem() == null) return;

        ItemStack consum = event.getCursor();         // 拖动的物品(消耗品)
        ItemStack item = event.getCurrentItem();       // 指向的物品(目标装备)
        Player player = (Player) event.getWhoClicked();
        ItemMeta itemMeta = item.getItemMeta();
        ItemMeta consumMeta = consum.getItemMeta();

        if (consumMeta == null) return;

        // ====================================================================
        // 一级武器强化石 → 锋利等级+1
        // ====================================================================
        if (consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, Armskey) == 1 && isSwordOrAxe(item)) {
            event.setCancelled(true);
            itemMeta.addEnchant(Enchantment.SHARPNESS, itemMeta.getEnchantLevel(Enchantment.SHARPNESS) + 1, true);
            consumeItem(player, consum, 1);
            player.sendMessage(ChatColor.BLUE + "武器强化成功");
            item.setItemMeta(itemMeta);
            player.spawnParticle(Particle.ENCHANT, player.getLocation(), 96, 0.75, 0.75, 1);
            return;
        }

        // ====================================================================
        // 二级武器强化石 → 攻击伤害+1 (属性修饰符)
        // ====================================================================
        if (consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, Armskey) == 2 && isSwordOrAxe(item)) {
            event.setCancelled(true);
            if (!itemMeta.hasAttributeModifiers()) {
                double base = getBaseValue(item);
                itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
                        new AttributeModifier(NamespacedKey.fromString("armsorplus:modifier"),
                                base + 1, AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlotGroup.HAND));
            } else {
                double current = itemMeta.getAttributeModifiers().get(Attribute.GENERIC_ATTACK_DAMAGE).stream()
                        .mapToDouble(AttributeModifier::getAmount).sum();
                itemMeta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
                itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
                        new AttributeModifier(Objects.requireNonNull(NamespacedKey.fromString("armsorplus:modifier")),
                                current + 1, AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlotGroup.HAND));
            }
            consumeItem(player, consum, 1);
            player.sendMessage(ChatColor.BLUE + "武器强化成功");
            item.setItemMeta(itemMeta);
            player.spawnParticle(Particle.ENCHANT, player.getLocation(), 96, 0.75, 0.75, 1);
            return;
        }

        // ====================================================================
        // 一级护甲强化石 → 保护等级+1
        // ====================================================================
        if (consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, Armorkey) == 1 && isArmor(item)) {
            event.setCancelled(true);
            itemMeta.addEnchant(Enchantment.PROTECTION, itemMeta.getEnchantLevel(Enchantment.PROTECTION) + 1, true);
            consumeItem(player, consum, 1);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "护甲强化成功");
            item.setItemMeta(itemMeta);
            player.spawnParticle(Particle.ENCHANT, player.getLocation(), 96, 0.75, 0.75, 1);
            return;
        }

        // ====================================================================
        // 二级护甲强化石 → 护甲值+1 (属性修饰符)
        // ====================================================================
        if (consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, Armorkey) == 2 && isArmor(item)) {
            event.setCancelled(true);
            Material type = item.getType();

            if (!itemMeta.hasAttributeModifiers()) {
                double base = getBaseValue(item);
                if (type.name().endsWith("_HELMET")) {
                    addArmorModifier(itemMeta, base + 1, EquipmentSlotGroup.HEAD);
                } else if (type.name().endsWith("_CHESTPLATE")) {
                    addArmorModifier(itemMeta, base + 1, EquipmentSlotGroup.CHEST);
                } else if (type.name().endsWith("_LEGGINGS")) {
                    addArmorModifier(itemMeta, base + 1, EquipmentSlotGroup.LEGS);
                } else if (type.name().endsWith("_BOOTS")) {
                    addArmorModifier(itemMeta, base + 1, EquipmentSlotGroup.FEET);
                }
            } else {
                double current = itemMeta.getAttributeModifiers().get(Attribute.GENERIC_ARMOR).stream()
                        .mapToDouble(AttributeModifier::getAmount).sum();
                itemMeta.removeAttributeModifier(Attribute.GENERIC_ARMOR);
                EquipmentSlotGroup slot = getSlotByType(type);
                itemMeta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                        new AttributeModifier(new NamespacedKey(getplugin, "ArmsorPlus_ArmorUpgrade"),
                                current + 1, AttributeModifier.Operation.ADD_NUMBER, slot));
            }

            consumeItem(player, consum, 1);
            player.sendMessage(ChatColor.BLUE + "护甲强化成功");
            item.setItemMeta(itemMeta);
            player.spawnParticle(Particle.ENCHANT, player.getLocation(), 96, 0.75, 0.75, 1);
            return;
        }

        // ====================================================================
        // 弓强化石 → 力量等级+1
        // ====================================================================
        if (consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, Bowkey) != 0 && item.getType() == BOW) {
            event.setCancelled(true);
            itemMeta.addEnchant(Enchantment.POWER, itemMeta.getEnchantLevel(Enchantment.POWER) + 1, true);
            consumeItem(player, consum, 1);
            player.sendMessage(ChatColor.BOLD + "弓强化成功");
            item.setItemMeta(itemMeta);
            player.spawnParticle(Particle.ENCHANT, player.getLocation(), 96, 0.75, 0.75, 1);
            return;
        }

        // ====================================================================
        // 精炼金刚石 → 无限耐久
        // ====================================================================
        if (consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, DiamondPluskey) != 0 && isWeaponOrArmor(item)) {
            event.setCancelled(true);
            itemMeta.setUnbreakable(true);
            consumeItem(player, consum, 1);
            player.sendMessage(ChatColor.BOLD + "金刚石强化成功");
            item.setItemMeta(itemMeta);
            player.spawnParticle(Particle.ENCHANT, player.getLocation(), 96, 0.75, 0.75, 1);
            return;
        }

        // ====================================================================
        // 自定义附魔书处理
        // ====================================================================
        handleEnchantBook(event, consum, item, player, itemMeta);
    }

    // ===== 附魔书处理 =====

    /** 统一处理所有自定义附魔书的拖拽附魔 */
    private void handleEnchantBook(InventoryClickEvent event, ItemStack consum,
                                    ItemStack item, Player player, ItemMeta itemMeta) {
        if (consum.getType() != Material.BOOK) return;

        // [闪避] 靴子
        if (tryApplyEnchant(event, consum, item, player, Dodgekey, Dodgekey,
                isBoots(item), ChatColor.GOLD + "闪避")) return;
        // [饥荒] 武器
        if (tryApplyEnchant(event, consum, item, player, Faminekey, Faminekey,
                isSwordOrAxe(item), ChatColor.GREEN + "饥荒")) return;
        // [涟漪] 靴子
        if (tryApplyEnchant(event, consum, item, player, RipplesProtectkey, RipplesProtectkey,
                isBoots(item), ChatColor.BLUE + "涟漪")) return;
        // [血祭] 剑
        if (tryApplyEnchant(event, consum, item, player, BloodSacrificekey, BloodSacrificekey,
                item.getType().name().endsWith("_SWORD"), ChatColor.RED + "血祭")) return;
        // [涤魂] 胸甲
        if (tryApplyEnchant(event, consum, item, player, EffectClear, EffectClear,
                isChestplate(item), ChatColor.WHITE + "涤魂")) return;
        // [寒冻] 武器
        if (tryApplyEnchant(event, consum, item, player, FreezeKey, FreezeKey,
                isSwordOrAxe(item), ChatColor.AQUA + "寒冻")) return;
        // [格挡] 头盔
        if (tryApplyEnchant(event, consum, item, player, BlockingKey, BlockingKey,
                isHelmet(item), ChatColor.AQUA + "格挡")) return;
        // [凋零] 武器
        if (tryApplyEnchant(event, consum, item, player, WitheringKey, WitheringKey,
                isSwordOrAxe(item), ChatColor.DARK_PURPLE + "凋零")) return;
        // [幸存] 护腿
        if (tryApplyEnchant(event, consum, item, player, SurvivorKey, SurvivorKey,
                isLeggings(item), ChatColor.GOLD + "幸存")) return;
        // [复仇] 胸甲
        if (tryApplyEnchant(event, consum, item, player, RevengeKey, RevengeKey,
                isChestplate(item), ChatColor.DARK_RED + "复仇")) return;
        // [生命提升] 胸甲 (特殊: 还要加MaxHealth修饰符)
        if (tryApplyHealthBoost(event, consum, item, player, itemMeta)) return;
        // [蓄爆] 弓/弩 (与弹道/狙击不共存)
        if (tryApplyEnchant(event, consum, item, player, ExplosiveArrowKey, ExplosiveArrowKey,
                (item.getType() == BOW || item.getType() == CROSSBOW)
                        && ArmsorEnchant.getEnchantLevel(item, ArrowSpeed) == 0
                        && ArmsorEnchant.getEnchantLevel(item, Sniping) == 0,
                ChatColor.YELLOW + "蓄爆")) return;
        // [影避] 靴子
        if (tryApplyEnchant(event, consum, item, player, ShadowDodge, ShadowDodge,
                isBoots(item), ChatColor.DARK_PURPLE + "影避")) return;
        // [弹道] 弓/弩 (与蓄爆不共存)
        if (tryApplyEnchant(event, consum, item, player, ArrowSpeed, ArrowSpeed,
                (item.getType() == BOW || item.getType() == CROSSBOW)
                        && ArmsorEnchant.getEnchantLevel(item, ExplosiveArrowKey) == 0,
                ChatColor.GOLD + "弹道")) return;
        // [狙击] 弓/弩 (与蓄爆不共存)
        if (tryApplyEnchant(event, consum, item, player, Sniping, Sniping,
                (item.getType() == BOW || item.getType() == CROSSBOW)
                        && ArmsorEnchant.getEnchantLevel(item, ExplosiveArrowKey) == 0,
                ChatColor.LIGHT_PURPLE + "狙击")) return;
        // [双重打击] 武器
        if (tryApplyEnchant(event, consum, item, player, DoubleHitkey, DoubleHitkey,
                isSwordOrAxe(item), ChatColor.LIGHT_PURPLE + "双重打击")) return;
        // [吸血] 武器
        if (tryApplyEnchant(event, consum, item, player, Feedingkey, Feedingkey,
                isSwordOrAxe(item), ChatColor.RED + "吸血")) return;
        // [疾刺] 三叉戟
        if (tryApplyEnchant(event, consum, item, player, QuickThrustKey, QuickThrustKey,
                item.getType() == TRIDENT, ChatColor.GOLD + "疾刺")) return;
    }

    /**
     * 尝试应用附魔书的通用方法。
     *
     * @return true=已处理(附魔成功或失败), false=不匹配
     */
    private boolean tryApplyEnchant(InventoryClickEvent event, ItemStack consum,
                                     ItemStack item, Player player,
                                     NamespacedKey consumKey, NamespacedKey itemKey,
                                     boolean condition, String displayName) {
        if (!condition) return false;
        int level = ArmsorEnchant.getEnchantLevel(consum, consumKey);
        if (level == 0) return false;

        event.setCancelled(true);
        player.sendMessage("正在附魔" + displayName + ChatColor.RESET + "..." + level + "级");

        if (ArmsorEnchant.getEnchantLevel(item, itemKey) >= level) {
            player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
            return true;
        }

        consumeItem(player, consum, 1);
        ArmsorEnchant.addEnchant(item, itemKey, level);
        player.sendMessage("附魔成功, 魔咒级别" + ArmsorEnchant.getEnchantLevel(item, itemKey));
        addEnchantLore(item, displayName, level, itemKey);
        return true;
    }

    /** 生命提升特殊处理 (需要额外添加MaxHealth修饰符) */
    private boolean tryApplyHealthBoost(InventoryClickEvent event, ItemStack consum,
                                         ItemStack item, Player player, ItemMeta itemMeta) {
        if (!isChestplate(item)) return false;
        int level = ArmsorEnchant.getEnchantLevel(consum, HealthBoostKey);
        if (level == 0) return false;

        event.setCancelled(true);
        player.sendMessage("正在附魔" + ChatColor.RED + "生命提升" + ChatColor.RESET + "..." + level + "级");

        if (ArmsorEnchant.getEnchantLevel(item, HealthBoostKey) >= level) {
            player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
            return true;
        }

        consumeItem(player, consum, 1);
        ArmsorEnchant.addEnchant(item, HealthBoostKey, level);

        // 移除旧的 MaxHealth 修饰符
        Collection<AttributeModifier> modifiers = itemMeta.getAttributeModifiers(Attribute.GENERIC_MAX_HEALTH);
        if (modifiers != null) {
            for (AttributeModifier mod : modifiers) {
                if (mod.getName().equals("HealthBoostEnchant")) {
                    itemMeta.removeAttributeModifier(Attribute.GENERIC_MAX_HEALTH, mod);
                }
            }
        }

        // 添加新的生命加成 (每级+5)
        AttributeModifier healthMod = new AttributeModifier(
                new NamespacedKey(getplugin, "ArmsorPlus_HealthBoost"),
                level * 5.0, AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.CHEST);
        itemMeta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, healthMod);
        item.setItemMeta(itemMeta);

        player.sendMessage("附魔成功, 魔咒级别" + ArmsorEnchant.getEnchantLevel(item, HealthBoostKey));
        addEnchantLore(item, ChatColor.RED + "生命提升", level, HealthBoostKey);
        return true;
    }

    // ===== 工具方法 =====

    /** 根据装备类型获取对应的装备槽位 */
    private EquipmentSlotGroup getSlotByType(Material type) {
        if (type.name().endsWith("_HELMET")) return EquipmentSlotGroup.HEAD;
        if (type.name().endsWith("_CHESTPLATE")) return EquipmentSlotGroup.CHEST;
        if (type.name().endsWith("_LEGGINGS")) return EquipmentSlotGroup.LEGS;
        if (type.name().endsWith("_BOOTS")) return EquipmentSlotGroup.FEET;
        return EquipmentSlotGroup.HEAD;
    }

    /** 消耗物品 (创造模式下不消耗) */
    private void consumeItem(Player player, ItemStack item, int amount) {
        if (player.getGameMode() != GameMode.CREATIVE) {
            item.setAmount(item.getAmount() - amount);
        }
    }

    /** 添加护甲+韧性属性修饰符 */
    private void addArmorModifier(ItemMeta meta, double value, EquipmentSlotGroup slot) {
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                new AttributeModifier(new NamespacedKey(getplugin, "ArmsorPlus_ArmorAdd"),
                        value, AttributeModifier.Operation.ADD_NUMBER, slot));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,
                new AttributeModifier(new NamespacedKey(getplugin, "ArmsorPlus_ToughnessAdd"),
                        value, AttributeModifier.Operation.ADD_NUMBER, slot));
    }
}
