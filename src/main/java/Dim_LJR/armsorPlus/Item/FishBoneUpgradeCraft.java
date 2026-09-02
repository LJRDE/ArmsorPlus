package Dim_LJR.armsorPlus.Item;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;

// 鱼骨武器升级配方 —— 附魔后仍可通过PDC识别原料
public class FishBoneUpgradeCraft implements Listener {

    // 当 ExactChoice 无法匹配附魔后的武器时，手动检测合成矩阵并设置结果。
    @EventHandler
    public void onFishBoneUpgradeCraft(PrepareItemCraftEvent event) {
        ItemStack[] m = event.getInventory().getMatrix();
        if (m.length < 9) return;

        // 如果 ExactChoice 已匹配(干净武器)，无需处理
        if (event.getInventory().getResult() != null) return;

        ItemStack center = m[4];
        if (center == null || center.getType() != Material.IRON_SWORD) return;

        ItemStack result = tryBuildUpgrade(m, center);
        if (result != null) {
            event.getInventory().setResult(result);
        }
    }

    // 尝试匹配鱼骨升级配方，返回结果物品或 null
    private ItemStack tryBuildUpgrade(ItemStack[] m, ItemStack center) {
        // 鱼刺剑: 4骨块(角)+4海晶沙粒(边)+鱼骨剑(中心)
        if (hasKey(center, FishBoneSwordKey)
                && isCorners(m, BONE_BLOCK) && isEdges(m, PRISMARINE_SHARD))
            return FishSpineSword(1);
        // 鱼刺刀
        if (hasKey(center, FishBoneKnifeKey)
                && isCorners(m, BONE_BLOCK) && isEdges(m, PRISMARINE_SHARD))
            return FishSpineKnife(1);
        // 海骨剑: 4海绵(角)+4海晶灯(边)+鱼刺剑(中心)
        if (hasKey(center, FishSpineSwordKey)
                && isCorners(m, SPONGE) && isEdges(m, SEA_LANTERN))
            return SeaBoneSword(1);
        // 海骨刀
        if (hasKey(center, FishSpineKnifeKey)
                && isCorners(m, SPONGE) && isEdges(m, SEA_LANTERN))
            return SeaBoneKnife(1);
        // 灵骨剑: 8灵魂沙围海骨剑
        if (hasKey(center, SeaBoneSwordKey) && isAllSurrounding(m, SOUL_SAND))
            return SpiritBoneSword(1);
        // 灵骨刀
        if (hasKey(center, SeaBoneKnifeKey) && isAllSurrounding(m, SOUL_SAND))
            return SpiritBoneKnife(1);
        // 海刺剑: 4鳞甲(角)+4海洋之心(边)+灵骨剑(中心)
        if (hasKey(center, SpiritBoneSwordKey)
                && isCorners(m, TURTLE_SCUTE) && isEdges(m, HEART_OF_THE_SEA))
            return SeaSpineSword(1);
        // 海刺刀
        if (hasKey(center, SpiritBoneKnifeKey)
                && isCorners(m, TURTLE_SCUTE) && isEdges(m, HEART_OF_THE_SEA))
            return SeaSpineKnife(1);
        // 蚀骨剑: 凋零骷髅头(B)+7灵魂土
        if (hasKey(center, SpiritBoneSwordKey)
                && m[1] != null && m[1].getType() == WITHER_SKELETON_SKULL
                && isAllExcept(m, 1, SOUL_SOIL))
            return CorrodeBoneSword(1);
        // 灵刺剑: 4潮涌核心(角)+4恶魂之泪(边)+海刺剑(中心)
        if (hasKey(center, SeaSpineSwordKey)
                && isCorners(m, CONDUIT) && isEdges(m, GHAST_TEAR))
            return SpiritSpineSword(1);
        // 灵刺刀: 4潮涌核心(角)+4凋零骷髅头(边)+海刺刀(中心)
        if (hasKey(center, SeaSpineKnifeKey)
                && isCorners(m, CONDUIT) && isEdges(m, WITHER_SKELETON_SKULL))
            return SpiritSpineKnife(1);
        // 海哭剑: 4下界之星(角)+4潮涌核心(边)+灵刺剑(中心)
        if (hasKey(center, SpiritSpineSwordKey)
                && isCorners(m, NETHER_STAR) && isEdges(m, CONDUIT))
            return SeaCrySword(1);
        // 海哭刀
        if (hasKey(center, SpiritSpineKnifeKey)
                && isCorners(m, NETHER_STAR) && isEdges(m, CONDUIT))
            return SeaCryKnife(1);
        return null;
    }

    private boolean hasKey(ItemStack item, NamespacedKey key) {
        return ArmsorEnchant.getEnchantLevel(item, key) > 0;
    }

    // 检查四角(A=0, C=2, G=6, I=8)是否全为指定材质
    private boolean isCorners(ItemStack[] m, Material mat) {
        return isMat(m[0], mat) && isMat(m[2], mat)
                && isMat(m[6], mat) && isMat(m[8], mat);
    }

    // 检查四边(B=1, D=3, F=5, H=7)是否全为指定材质
    private boolean isEdges(ItemStack[] m, Material mat) {
        return isMat(m[1], mat) && isMat(m[3], mat)
                && isMat(m[5], mat) && isMat(m[7], mat);
    }

    // 检查除中心外的8格是否全为指定材质
    private boolean isAllSurrounding(ItemStack[] m, Material mat) {
        for (int i = 0; i < 9; i++) {
            if (i == 4) continue;
            if (!isMat(m[i], mat)) return false;
        }
        return true;
    }

    // 检查除中心和指定位置外的7格是否全为指定材质
    private boolean isAllExcept(ItemStack[] m, int except, Material mat) {
        for (int i = 0; i < 9; i++) {
            if (i == 4 || i == except) continue;
            if (!isMat(m[i], mat)) return false;
        }
        return true;
    }

    private boolean isMat(ItemStack item, Material mat) {
        return item != null && item.getType() == mat;
    }
}
