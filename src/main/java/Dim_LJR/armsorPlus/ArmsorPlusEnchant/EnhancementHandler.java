package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant.addEnchantLore;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.DoubleHitkey;
import static org.bukkit.Material.*;
import static org.bukkit.Material.BOOK;

public class EnhancementHandler implements Listener {
    //辅助方法
    private boolean IfArms(ItemStack item) {//判断是否武器
        Material type = item.getType();
        return type.name().endsWith("_SWORD") ||
                type.name().endsWith("_AXE") ||
                type == TRIDENT;
    }
    private boolean IfArmor(ItemStack item) {//判断是否护甲
        Material type = item.getType();
        return type.name().endsWith("_HELMET") ||
                type.name().endsWith("_CHESTPLATE") ||
                type.name().endsWith("_LEGGINGS") ||
                type.name().endsWith("_BOOTS");
    }
    private boolean IfHelmet(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_HELMET");
    }
    private boolean IfChestplate(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_CHESTPLATE");
    }
    private boolean IfLeggings(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_LEGGINGS");
    }
    private boolean Ifboots(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_BOOTS");
    }
    private boolean IfArmsor(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_SWORD") ||
                type.name().endsWith("_AXE") ||
                type == TRIDENT ||
                type.name().endsWith("_HELMET") ||
                type.name().endsWith("_CHESTPLATE") ||
                type.name().endsWith("_LEGGINGS") ||
                type.name().endsWith("_BOOTS") ||
                type == BOW;
    }
    private boolean IsInt(String args){
        for(int i = args.length();--i >= 0;){
            if (!Character.isDigit(args.charAt(i)))
                return false;
        }
        return true;
    }
    enum ArmsorType {
        ERROR,Diamond_Sword,Iron_Sword,Netherite_Sword,Golden_Sword,Stone_Sword,Wooden_Sword
    }
    private ArmsorType GetArmsorType(ItemStack item){
        if (item.getType().equals(DIAMOND_SWORD))
            return ArmsorType.Diamond_Sword;
        else if(item.getType().equals(IRON_SWORD))
            return ArmsorType.Iron_Sword;
        else if(item.getType().equals(GOLDEN_SWORD))
            return ArmsorType.Golden_Sword;
        else if(item.getType().equals(NETHERITE_SWORD))
            return ArmsorType.Netherite_Sword;
        else if(item.getType().equals(STONE_SWORD))
            return ArmsorType.Stone_Sword;
        else if(item.getType().equals(WOODEN_SWORD))
            return ArmsorType.Wooden_Sword;
        return ArmsorType.ERROR;
    }
    private int GetArmsorLevel(ItemStack item){//获得武器装备伤害，护甲值
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
    @EventHandler
    public void onPlayerInteract(@NotNull InventoryClickEvent event)//当玩家点击鼠标时(强化石/精炼金刚石/魔法附魔书监听器)
    {
        if(event.getCursor() == null ||event.getCurrentItem() == null ) return;
        ItemStack consum = event.getCursor();//所拖动的物品
        ItemStack item = event.getCurrentItem();//指向的物品
        Player player = (Player) event.getWhoClicked();
        ItemMeta meta = item.getItemMeta();
        ItemMeta consummeta = consum.getItemMeta();
        double ArmsValue;
        double ArmorValue;
        AttributeModifier mod;
        if (consummeta == null)
            return;
        String name = consum.getItemMeta().getDisplayName();//获取拖动物品名

        if (consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,Armskey)==1 && IfArms(item))
        {
            //如果是一级强化石,此方法存在BUG可能可以使用同名物品强化，以后应该加上二重验证机制-已完善
            event.setCancelled(true);
            meta.addEnchant(Enchantment.DAMAGE_ALL, meta.getEnchantLevel(Enchantment.DAMAGE_ALL) + 1, true);
            consum.setAmount(consum.getAmount() - 1);
            player.sendMessage(net.md_5.bungee.api.ChatColor.BLUE + "武器强化成功");
            item.setItemMeta(meta);
            player.spawnParticle(Particle.ENCHANTMENT_TABLE,player.getLocation(),96,0.75,0.75,1);
        }//一级武器强化石
        else if (consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,Armskey)==2 && IfArms(item))
        {//武器强化石II
            event.setCancelled(true);
            if(!meta.hasAttributeModifiers()){
                ArmsValue = GetArmsorLevel(item);
                meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
                        new AttributeModifier(UUID.randomUUID(),
                                "arms",ArmsValue + 1,AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlot.HAND));
            }
            else {
                ArmsValue = meta.getAttributeModifiers().get(Attribute.GENERIC_ATTACK_DAMAGE).stream()
                        .mapToDouble(AttributeModifier::getAmount)
                        .sum();
                meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
                meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
                        new AttributeModifier(UUID.randomUUID(),
                                "arms", ArmsValue + 1,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlot.HAND));
            }
            consum.setAmount(consum.getAmount() - 1);
            player.sendMessage(net.md_5.bungee.api.ChatColor.BLUE + "武器强化成功");
            item.setItemMeta(meta);
            player.spawnParticle(Particle.ENCHANTMENT_TABLE,player.getLocation(),96,0.75,0.75,1);
        }//二级武器强化石
        else if (consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,Armorkey)==1 && IfArmor(item))
        {
            event.setCancelled(true);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, meta.getEnchantLevel(Enchantment.PROTECTION_ENVIRONMENTAL) + 1,true);
            consum.setAmount(consum.getAmount() - 1);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "护甲强化成功");
            item.setItemMeta(meta);
            player.spawnParticle(Particle.ENCHANTMENT_TABLE,player.getLocation(),96,0.75,0.75,1);
        }//一级护甲强化石
        else if (consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,Armorkey)==2 && IfArmor(item))
        {//护甲强化石II
            event.setCancelled(true);
            Material type = item.getType();
            if(!meta.hasAttributeModifiers()) {
                if (type.name().endsWith("_HELMET")) {//头盔
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", GetArmsorLevel(item) + 1,//护甲值
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.HEAD));
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", 5,//护甲韧性
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.HEAD));
                } else if (type.name().endsWith("_CHESTPLATE")) {//胸甲
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", GetArmsorLevel(item) + 1,//护甲值
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.CHEST));
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", 5,//盔甲韧性
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.CHEST));
                } else if (type.name().endsWith("_LEGGINGS")) {//裤腿
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", GetArmsorLevel(item) + 1,//护甲值
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.LEGS));
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", 5,//盔甲韧性
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.LEGS));
                } else if (type.name().endsWith("_BOOTS")) {//鞋子
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", GetArmsorLevel(item) + 1,//护甲值
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.FEET));
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,//盔甲韧性
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", 5,
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.FEET));
                }
            }
            else {
                ArmorValue = meta.getAttributeModifiers().get(Attribute.GENERIC_ARMOR).stream()
                        .mapToDouble(AttributeModifier::getAmount)
                        .sum();//原护甲值
                meta.removeAttributeModifier(Attribute.GENERIC_ARMOR);
                if (type.name().endsWith("_HELMET")) {
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", ArmorValue + 1,
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.HEAD));
                } else if (type.name().endsWith("_CHESTPLATE")) {
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", ArmorValue + 1,
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.CHEST));
                } else if (type.name().endsWith("_LEGGINGS")) {
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", ArmorValue + 1,
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.LEGS));
                } else if (type.name().endsWith("_BOOTS")) {
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", ArmorValue + 1,
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.FEET));
                }

            }
            consum.setAmount(consum.getAmount() - 1);
            player.sendMessage(net.md_5.bungee.api.ChatColor.BLUE + "护甲强化成功");
            item.setItemMeta(meta);
            player.spawnParticle(Particle.ENCHANTMENT_TABLE,player.getLocation(),96,0.75,0.75,1);
        }//二级护甲强化石
        else if (consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,Bowkey)!=0 && item.getType() == BOW)
        {//当名字是弓强化石时
            event.setCancelled(true);
            meta.addEnchant(Enchantment.ARROW_DAMAGE, meta.getEnchantLevel(Enchantment.ARROW_DAMAGE) + 1, true);
            consum.setAmount(consum.getAmount() - 1);
            player.sendMessage(ChatColor.BOLD + "弓强化成功");
            item.setItemMeta(meta);
            player.spawnParticle(Particle.ENCHANTMENT_TABLE,player.getLocation(),96,0.75,0.75,1);
        }//弓强化石
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,DiamondPluskey)!=0 && IfArmsor(item))
        {
            event.setCancelled(true);
            meta.setUnbreakable(true);
            consum.setAmount(consum.getAmount() - 1);
            player.sendMessage(ChatColor.BOLD + "金刚石强化成功");
            item.setItemMeta(meta);
            player.spawnParticle(Particle.ENCHANTMENT_TABLE,player.getLocation(),96,0.75,0.75,1);
        }//精炼金刚石
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,Dodgekey)!=0 && consum.getType().equals(BOOK))
        {
            if(!item.getType().name().endsWith("_BOOTS"))
                return;
            int level = 1;
            level = ArmsorEnchant.getEnchantLevel(consum,Dodgekey);
            event.setCancelled(true);
            player.sendMessage("正在附魔" + ChatColor.GOLD + "闪避..." + ChatColor.RESET + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,Dodgekey)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, Dodgekey, level);
            player.sendMessage("附魔成功，魔咒级别" + ArmsorEnchant.getEnchantLevel(item,Dodgekey));
            addEnchantLore(item,ChatColor.GOLD + "闪避",level , Dodgekey);
        }//闪避附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,Faminekey)!=0 && consum.getType().equals(BOOK))
        {
            if(!IfArms(item))
                return;
            event.setCancelled(true);
            int level = ArmsorEnchant.getEnchantLevel(consum,Faminekey);
            player.sendMessage("正在附魔饥荒..." + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,Faminekey)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            ArmsorEnchant.addEnchant(item,Faminekey,level);
            consum.setAmount(consum.getAmount() - 1);
            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item,Faminekey));
            addEnchantLore(item,ChatColor.GREEN + "饥荒",level ,Faminekey);
        }//饥荒附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,RipplesProtectkey)!=0 && consum.getType().equals(BOOK))
        {
            if(!item.getType().name().endsWith("_BOOTS"))
                return;
            event.setCancelled(true);
            int level = ArmsorEnchant.getEnchantLevel(consum,RipplesProtectkey);
            player.sendMessage("正在附魔" + ChatColor.BLUE + "涟漪" + ChatColor.RESET + "..." + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,RipplesProtectkey)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            ArmsorEnchant.addEnchant(item,RipplesProtectkey,level);
            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item,RipplesProtectkey));
            addEnchantLore(item,ChatColor.BLUE + "涟漪",level ,RipplesProtectkey);
            consum.setAmount(consum.getAmount() - 1);
        }//涟漪附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,BloodSacrificekey)!=0 && consum.getType().equals(BOOK))
        {
            if(!item.getType().name().endsWith("_SWORD"))
                return;
            event.setCancelled(true);
            int level = ArmsorEnchant.getEnchantLevel(consum,BloodSacrificekey);
            player.sendMessage("正在附魔" + ChatColor.RED + "血祭" + ChatColor.RESET + "..." + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,BloodSacrificekey)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item,BloodSacrificekey,level);
            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item,BloodSacrificekey));
            addEnchantLore(item,ChatColor.RED + "血祭",level ,BloodSacrificekey);
            consum.setAmount(consum.getAmount() - 1);
        }//血祭附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,EffectClear)!=0 && consum.getType().equals(BOOK))
        {
            if(!item.getType().name().endsWith("_CHESTPLATE"))
                return;
            event.setCancelled(true);
            int level = ArmsorEnchant.getEnchantLevel(consum,EffectClear);
            player.sendMessage("正在附魔" + ChatColor.WHITE + "涤魂" + ChatColor.RESET + "..." + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,EffectClear)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item,EffectClear,level);
            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item,EffectClear));
            addEnchantLore(item,ChatColor.WHITE + "涤魂",level ,EffectClear);
            consum.setAmount(consum.getAmount() - 1);
        }//涤魂附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,FreezeKey)!=0 && consum.getType().equals(BOOK))
        {
            if(!IfArms(item))
                return;
            event.setCancelled(true);
            int level = ArmsorEnchant.getEnchantLevel(consum,FreezeKey);
            player.sendMessage("正在附魔" + ChatColor.AQUA + "寒冻" + ChatColor.RESET + "..." + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,FreezeKey)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            ArmsorEnchant.addEnchant(item,FreezeKey,level);
            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item,FreezeKey));
            addEnchantLore(item,ChatColor.AQUA + "寒冻",level , FreezeKey);
            consum.setAmount(consum.getAmount() - 1);
        }//寒冻附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, BlockingKey) != 0 && consum.getType().equals(BOOK))
        {
            if(!IfHelmet(item))
                return;
            event.setCancelled(true);

            int level = ArmsorEnchant.getEnchantLevel(consum, BlockingKey);
            player.sendMessage("正在附魔" + ChatColor.AQUA + "格挡" + ChatColor.RESET + "..." + level + "级");

            if(ArmsorEnchant.getEnchantLevel(item, BlockingKey) >= level) {
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }

            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, BlockingKey, level);

            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item, BlockingKey));
            addEnchantLore(item, ChatColor.AQUA + "格挡", level ,BlockingKey);
        }//格挡附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, WitheringKey) != 0 && consum.getType().equals(BOOK))
        {
            if(!IfArms(item)) // 凋零需要武器而不是防具
                return;
            event.setCancelled(true);

            int level = ArmsorEnchant.getEnchantLevel(consum, WitheringKey);
            player.sendMessage("正在附魔" + ChatColor.DARK_PURPLE + "凋零" + ChatColor.RESET + "..." + level + "级");

            if(ArmsorEnchant.getEnchantLevel(item, WitheringKey) >= level) {
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }

            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, WitheringKey, level);

            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item, WitheringKey));
            addEnchantLore(item, ChatColor.DARK_PURPLE + "凋零", level , WitheringKey);
        }//凋零附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, SurvivorKey) != 0 && consum.getType().equals(BOOK))
        {
            // 幸存仅用于护腿(裤子)
            if(!IfLeggings(item))
                return;
            event.setCancelled(true);
            int level = ArmsorEnchant.getEnchantLevel(consum, SurvivorKey);
            player.sendMessage("正在附魔" + ChatColor.GOLD + "幸存" + ChatColor.RESET + "..." + level + "级");

            if(ArmsorEnchant.getEnchantLevel(item, SurvivorKey) >= level) {
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, SurvivorKey, level);

            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item, SurvivorKey));
            addEnchantLore(item, ChatColor.GOLD + "幸存", level ,SurvivorKey);
        }//幸存附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, RevengeKey) != 0 && consum.getType().equals(BOOK))
        {
            // 复仇仅用于胸甲
            if(!IfChestplate(item))
                return;
            event.setCancelled(true);
            int level = ArmsorEnchant.getEnchantLevel(consum, RevengeKey);
            player.sendMessage("正在附魔" + ChatColor.DARK_RED + "复仇" + ChatColor.RESET + "..." + level + "级");

            if(ArmsorEnchant.getEnchantLevel(item, RevengeKey) >= level) {
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, RevengeKey, level);

            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item, RevengeKey));
            addEnchantLore(item, ChatColor.DARK_RED + "复仇", level ,RevengeKey);
        }//复仇附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, HealthBoostKey) != 0 && consum.getType().equals(BOOK))
        {
            // 生命提升仅用于胸甲
            if(!IfChestplate(item))
                return;
            event.setCancelled(true);
            int level = ArmsorEnchant.getEnchantLevel(consum, HealthBoostKey);
            player.sendMessage("正在附魔" + ChatColor.RED + "生命提升" + ChatColor.RESET + "..." + level + "级");

            if(ArmsorEnchant.getEnchantLevel(item, HealthBoostKey) >= level) {
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, HealthBoostKey, level);
            Collection<AttributeModifier> modifiers = meta.getAttributeModifiers(Attribute.GENERIC_MAX_HEALTH);
            // 移除旧的同名修饰符
            if(modifiers != null) {
                for(AttributeModifier modifier : new ArrayList<>(modifiers)) {
                    if(modifier.getName().equals("HealthBoostEnchant")) {
                        meta.removeAttributeModifier(Attribute.GENERIC_MAX_HEALTH, modifier);
                    }
                }
            }
            // 添加新的修饰符
            double healthBoost = level * 5; // 每级+5生命
            AttributeModifier healthModifier = new AttributeModifier(
                    UUID.randomUUID(), // 唯一ID
                    "HealthBoostEnchant", // 名称
                    healthBoost, // 数值
                    AttributeModifier.Operation.ADD_NUMBER, // 操作类型
                    EquipmentSlot.CHEST // 装备位置
            );
            meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, healthModifier);
            item.setItemMeta(meta);
            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item, HealthBoostKey));
            addEnchantLore(item, ChatColor.RED + "生命提升", level ,HealthBoostKey);

        }//生命提升附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, ExplosiveArrowKey) != 0 && consum.getType().equals(BOOK))
        {
            // 蓄爆仅用于弓和弩
            if (item.getType().equals(Material.BOW) || item.getType().equals(CROSSBOW)) {
                event.setCancelled(true);
                int level = ArmsorEnchant.getEnchantLevel(consum, ExplosiveArrowKey);
                player.sendMessage("正在附魔" + ChatColor.YELLOW + "蓄爆" + ChatColor.RESET + "..." + level + "级");

                if (ArmsorEnchant.getEnchantLevel(item, ExplosiveArrowKey) >= level) {
                    player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                    return;
                }
                consum.setAmount(consum.getAmount() - 1);
                ArmsorEnchant.addEnchant(item, ExplosiveArrowKey, level);

                player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item, ExplosiveArrowKey));
                addEnchantLore(item, ChatColor.YELLOW + "蓄爆", level , ExplosiveArrowKey);
            }
        }//蓄爆附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,ShadowDodge)!= 0 && consum.getType().equals(BOOK))
        {
            if(!item.getType().name().endsWith("_BOOTS"))
                return;
            int level = 1;
            level = ArmsorEnchant.getEnchantLevel(consum,ShadowDodge);
            event.setCancelled(true);
            player.sendMessage("正在附魔" + ChatColor.DARK_PURPLE + "影避..." + ChatColor.RESET + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,ShadowDodge)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, ShadowDodge, level);
            player.sendMessage("附魔成功，魔咒级别" + ArmsorEnchant.getEnchantLevel(item,ShadowDodge));
            addEnchantLore(item,ChatColor.DARK_PURPLE + "影避",level , ShadowDodge);
        }//影避附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,ArrowSpeed)!= 0 && consum.getType().equals(BOOK))
        {
            if (!(item.getType().equals(Material.BOW) || item.getType().equals(CROSSBOW)))
                return;
            int level = 1;
            level = ArmsorEnchant.getEnchantLevel(consum,ArrowSpeed);
            event.setCancelled(true);
            player.sendMessage("正在附魔" + ChatColor.GOLD + "弹道..." + ChatColor.RESET + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,ArrowSpeed)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, ArrowSpeed, level);
            player.sendMessage("附魔成功，魔咒级别" + ArmsorEnchant.getEnchantLevel(item,ArrowSpeed));
            addEnchantLore(item,ChatColor.GOLD + "弹道",level , ArrowSpeed);
        }//弹道附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,Sniping)!= 0 && consum.getType().equals(BOOK))
        {
            if (!(item.getType().equals(Material.BOW) || item.getType().equals(CROSSBOW)))
                return;
            int level = 1;
            level = ArmsorEnchant.getEnchantLevel(consum,Sniping);
            event.setCancelled(true);
            player.sendMessage("正在附魔" + ChatColor.GOLD + "狙击..." + ChatColor.RESET + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,Sniping)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, Sniping, level);
            player.sendMessage("附魔成功，魔咒级别" + ArmsorEnchant.getEnchantLevel(item,Sniping));
            addEnchantLore(item,ChatColor.LIGHT_PURPLE + "狙击",level , Sniping);
        }//狙击附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,DoubleHitkey)!= 0 && consum.getType().equals(BOOK))
        {
            if (!IfArms(item))
                return;
            int level = 1;
            level = ArmsorEnchant.getEnchantLevel(consum,DoubleHitkey);
            event.setCancelled(true);
            player.sendMessage("正在附魔" + ChatColor.GOLD + "双重打击..." + ChatColor.RESET + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,DoubleHitkey)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, DoubleHitkey, level);
            player.sendMessage("附魔成功，魔咒级别" + ArmsorEnchant.getEnchantLevel(item,DoubleHitkey));
            addEnchantLore(item,ChatColor.LIGHT_PURPLE + "双重打击",level , DoubleHitkey);
        }//双重打击附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,Feedingkey)!= 0 && consum.getType().equals(BOOK))
        {
            if (!IfArms(item))
                return;
            int level = 1;
            level = ArmsorEnchant.getEnchantLevel(consum,Feedingkey);
            event.setCancelled(true);
            player.sendMessage("正在附魔" + ChatColor.RED + "吸血..." + ChatColor.RESET + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,Feedingkey)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, Feedingkey, level);
            player.sendMessage("附魔成功，魔咒级别" + ArmsorEnchant.getEnchantLevel(item,Feedingkey));
            addEnchantLore(item,ChatColor.DARK_RED + "吸血",level , Feedingkey);
        }//吸血附魔书
    }
}
