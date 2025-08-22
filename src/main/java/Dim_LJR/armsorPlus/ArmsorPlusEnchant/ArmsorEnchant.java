package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.ArrowSpeed;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.BlockingKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.BloodSacrificekey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.Dodgekey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.DoubleHitkey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.EffectClear;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.ExplosiveArrowKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.HealthBoostKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.RipplesProtectkey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.ShadowDodge;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.Sniping;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.SurvivorKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.WitheringKey;

public class ArmsorEnchant implements Listener {//PDC!!!
    public static ItemStack addEnchant(ItemStack item, NamespacedKey key, int level){//附魔方法
        item.editMeta(meta -> {meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER,level);});
        return item;
    }
    public static int getEnchantLevel(ItemStack item,NamespacedKey key){
        if(item == null)
            return 0;
        if(!item.hasItemMeta())
            return 0;
        PersistentDataContainer container =
                item.getItemMeta().getPersistentDataContainer();
        if(container.has(key,PersistentDataType.INTEGER)) {
            return container.get(key,PersistentDataType.INTEGER);
        } else return 0;
    }
    public static ItemStack addEnchantLore(ItemStack item, String lore, int level, NamespacedKey key)//附魔添加lore
    {
        if (item == null || item.getType() == Material.AIR) return item;
        removeEnchantLore(item,key);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        // 生成新的 Lore 文本
        String newLore = generateEnchantLore( lore,level);
        // 获取当前 Lore
        List<String> loreList = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        loreList.add(newLore);
        // 设置回物品
        meta.setLore(loreList);
        item.setItemMeta(meta);
        return item;
    }
    public static boolean removeLoreLine(ItemStack item, String keyword)
    {
        if(item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if(!meta.hasLore()) return false;

        List<String> lore = meta.getLore();
        List<String> newLore = new ArrayList<>();
        boolean removed = false;

        String strippedKeyword = ChatColor.stripColor(keyword);

        for(String line : lore) {
            String strippedLine = ChatColor.stripColor(line);
            if(strippedLine.contains(strippedKeyword)) {
                removed = true;
            } else {
                newLore.add(line);
            }
        }

        if(removed) {
            meta.setLore(newLore);
            item.setItemMeta(meta);
            return true;
        }
        return false;
    }
    public static void removeEnchantLore(ItemStack item, NamespacedKey key)
    {
        String displayName = getEnchantDisplayName(key);
        if(displayName != null) {
            removeLoreLine(item, displayName);
        }
    }
    public static String generateEnchantLore(String baseLore, int level)
    {
        String levelStr;
        switch (level) {
            case 1: levelStr = "I"; break;
            case 2: levelStr = "II"; break;
            case 3: levelStr = "III"; break;
            case 4: levelStr = "IV"; break;
            case 5: levelStr = "V"; break;
            case 6: levelStr = "VI"; break;
            case 7: levelStr = "VII"; break;
            case 8: levelStr = "VIII"; break;
            case 9: levelStr = "IX"; break;
            case 10: levelStr = "X"; break;
            default: levelStr = String.valueOf(level);
        }
        return baseLore + levelStr;
    }
    //上面的代码除非遇到重大bug我再也懒得改了
    public static String getEnchantDisplayName(NamespacedKey key)
    {
        if(key.equals(FreezeKey))//1
            return "寒冻";
        else if(key.equals(Faminekey))//2
            return "饥荒";
        else if(key.equals(RevengeKey))
            return "复仇";
        else if(key.equals(WitheringKey))//3
            return "凋零";
        else if(key.equals(RipplesProtectkey))//4
            return "涟漪";
        else if(key.equals(Dodgekey))//5
            return "闪避";
        else if(key.equals(HealthBoostKey))//6
            return "生命提升";
        else if(key.equals(SurvivorKey))//7
            return "幸存";
        else if(key.equals(BloodSacrificekey))//8
            return "血祭";
        else if(key.equals(ExplosiveArrowKey))//9
            return "蓄爆";
        else if(key.equals(EffectClear))//10
            return "涤魂";
        else if(key.equals(BlockingKey))//11
            return "格挡";
        else if(key.equals(ShadowDodge))//12
            return "影避";
        else if(key.equals(ArrowSpeed))//13
            return "弹道";
        else if(key.equals(Sniping))//14
            return "狙击";
        else if(key.equals(DoubleHitkey))//15
            return "双重打击";
        else return null;
    }

}//自己的附魔类，同时添加pdc用
