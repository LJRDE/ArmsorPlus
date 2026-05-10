package Dim_LJR.armsorPlus.Food;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

import static Dim_LJR.armsorPlus.Food.FoodItems.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

public class TreeListeners implements Listener {
    private static final Random RANDOM = new Random();
    static final String SAPLING_METADATA = "ArmsorPlus_FruitType";

    private static final Map<String, Supplier<ItemStack>> FRUIT_MAP = new HashMap<>();
    private static final Map<Material, TreeType> TREE_TYPE_MAP = new HashMap<>();

    static {
        FRUIT_MAP.put(FigSaplingKey.getKey(), () -> Fig(1));
        FRUIT_MAP.put(DateSaplingKey.getKey(), () -> Date(1));
        FRUIT_MAP.put(PersimmonSaplingKey.getKey(), () -> Persimmon(1));
        FRUIT_MAP.put(MangosteenSaplingKey.getKey(), () -> Mangosteen(1));
        FRUIT_MAP.put(CherryTomatoSaplingKey.getKey(), () -> CherryTomato(1));
        FRUIT_MAP.put(TomatoSaplingKey.getKey(), () -> Tomato(1));
        FRUIT_MAP.put(GrapeSaplingKey.getKey(), () -> Grape(1));
        FRUIT_MAP.put(PomegranateSaplingKey.getKey(), () -> Pomegranate(1));
        FRUIT_MAP.put(ChestnutSaplingKey.getKey(), () -> Chestnut(1));
        FRUIT_MAP.put(KiwiSaplingKey.getKey(), () -> Kiwi(1));
        FRUIT_MAP.put(LonganSaplingKey.getKey(), () -> Longan(1));
        FRUIT_MAP.put(LycheeSaplingKey.getKey(), () -> Lychee(1));
        FRUIT_MAP.put(CherrySaplingKey.getKey(), () -> Cherry(1));
        FRUIT_MAP.put(PeachSaplingKey.getKey(), () -> Peach(1));
        FRUIT_MAP.put(PlumSaplingKey.getKey(), () -> Plum(1));
        FRUIT_MAP.put(HazelnutSaplingKey.getKey(), () -> Hazelnut(1));
        FRUIT_MAP.put(CoconutSaplingKey.getKey(), () -> Coconut(1));
        FRUIT_MAP.put(PineappleSaplingKey.getKey(), () -> Pineapple(1));
        FRUIT_MAP.put(StrawberrySaplingKey.getKey(), () -> Strawberry(1));
        FRUIT_MAP.put(BlueberrySaplingKey.getKey(), () -> Blueberry(1));
        FRUIT_MAP.put(OrangeSaplingKey.getKey(), () -> Orange(1));
        FRUIT_MAP.put(TangerineSaplingKey.getKey(), () -> Tangerine(1));
        FRUIT_MAP.put(BigAppleSaplingKey.getKey(), () -> BigApple(1));

        TREE_TYPE_MAP.put(Material.OAK_SAPLING, TreeType.TREE);
        TREE_TYPE_MAP.put(Material.BIRCH_SAPLING, TreeType.BIRCH);
        TREE_TYPE_MAP.put(Material.JUNGLE_SAPLING, TreeType.JUNGLE);
        TREE_TYPE_MAP.put(Material.DARK_OAK_SAPLING, TreeType.DARK_OAK);
        TREE_TYPE_MAP.put(Material.ACACIA_SAPLING, TreeType.ACACIA);
        TREE_TYPE_MAP.put(Material.SPRUCE_SAPLING, TreeType.REDWOOD);
    }

    @EventHandler
    public void onCustomTreeGrow(StructureGrowEvent event) {
        Block sapling = event.getLocation().getBlock();
        if (!event.getLocation().getBlock().hasMetadata(SAPLING_METADATA)) return;

        String fruitKey = event.getLocation().getBlock().getMetadata(SAPLING_METADATA).get(0).asString();
        Supplier<ItemStack> fruitSupplier = FRUIT_MAP.get(fruitKey);
        if (fruitSupplier == null) return;

        event.setCancelled(true);
        Material saplingType = sapling.getType();
        sapling.setType(Material.AIR);
        sapling.removeMetadata(SAPLING_METADATA, getplugin);

        TreeType treeType = TREE_TYPE_MAP.getOrDefault(saplingType, TreeType.TREE);
        event.getWorld().generateTree(event.getLocation(), treeType);

        Location loc = event.getLocation();
        World world = loc.getWorld();
        ItemStack fruit = fruitSupplier.get();

        for (int x = -2; x <= 2; x++) {
            for (int y = 1; y <= 5; y++) {
                for (int z = -2; z <= 2; z++) {
                    Block block = world.getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
                    if (isLeaf(block.getType()) && RANDOM.nextDouble() < 0.2) {
                        world.dropItemNaturally(block.getLocation().add(0.5, -0.5, 0.5), fruit.clone());
                    }
                }
            }
        }
    }

    private static boolean isLeaf(Material material) {
        return material.name().endsWith("_LEAVES");
    }
}
