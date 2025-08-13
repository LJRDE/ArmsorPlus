package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
}//自己的附魔类，同时添加pdc用
