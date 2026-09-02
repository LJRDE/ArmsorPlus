package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

import static Dim_LJR.armsorPlus.Food.FoodItems.RejuvenationPowder;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.RejuvenationPowderKey;

// 回春散 —— 合成时随机品质
public class RejuvenationPowderCraft implements Listener {

    private static final Random RANDOM = new Random();

    @EventHandler
    public void onCraftRejuvenation(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result == null) return;
        if (ArmsorEnchant.getEnchantLevel(result, RejuvenationPowderKey) == 0) return;

        double roll = RANDOM.nextDouble();
        int tier;
        if (roll < 0.001)       tier = 3; // 0.1% 仙品
        else if (roll < 0.011)  tier = 2; // 1% 极品
        else if (roll < 0.111)  tier = 1; // 10% 上品
        else                    tier = 0; // 普通

        event.getInventory().setResult(RejuvenationPowder(result.getAmount(), tier));
    }
}
