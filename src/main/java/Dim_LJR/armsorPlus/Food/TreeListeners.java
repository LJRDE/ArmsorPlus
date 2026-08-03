package Dim_LJR.armsorPlus.Food;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.function.Supplier;

import static Dim_LJR.armsorPlus.Food.FoodItems.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

public class TreeListeners implements Listener {
    private static final Random RANDOM = new Random();
    static final String SAPLING_METADATA = "ArmsorPlus_FruitType";
    private static final String LEAF_FRUIT_METADATA = "ArmsorPlus_LeafFruit";
    private static final String HERB_LEAF_METADATA = "ArmsorPlus_HerbLeaf";
    private static final String FRUIT_SKULL_METADATA = "ArmsorPlus_FruitSkull";

    private static final Map<String, Supplier<ItemStack>> FRUIT_MAP = new HashMap<>();
    private static final Map<Material, TreeType> TREE_TYPE_MAP = new HashMap<>();
    private static final Set<String> HERBACEOUS_KEYS = new HashSet<>();
    private static final Map<String, HerbData> HERB_DATA_MAP = new HashMap<>();

    private record HerbData(Material saplingType, NamespacedKey saplingKey) {}

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
        TREE_TYPE_MAP.put(Material.ACACIA_SAPLING, TreeType.ACACIA);
        TREE_TYPE_MAP.put(Material.SPRUCE_SAPLING, TreeType.REDWOOD);
        TREE_TYPE_MAP.put(Material.DARK_OAK_SAPLING, TreeType.DARK_OAK);
        TREE_TYPE_MAP.put(Material.CHERRY_SAPLING, TreeType.CHERRY);
        TREE_TYPE_MAP.put(Material.MANGROVE_PROPAGULE, TreeType.MANGROVE);

        HERBACEOUS_KEYS.add(CherryTomatoSaplingKey.getKey());
        HERBACEOUS_KEYS.add(TomatoSaplingKey.getKey());
        HERBACEOUS_KEYS.add(GrapeSaplingKey.getKey());
        HERBACEOUS_KEYS.add(KiwiSaplingKey.getKey());
        HERBACEOUS_KEYS.add(PineappleSaplingKey.getKey());
        HERBACEOUS_KEYS.add(StrawberrySaplingKey.getKey());
        HERBACEOUS_KEYS.add(BlueberrySaplingKey.getKey());

        HERB_DATA_MAP.put(CherryTomatoSaplingKey.getKey(), new HerbData(Material.OAK_SAPLING, CherryTomatoSaplingKey));
        HERB_DATA_MAP.put(TomatoSaplingKey.getKey(), new HerbData(Material.OAK_SAPLING, TomatoSaplingKey));
        HERB_DATA_MAP.put(GrapeSaplingKey.getKey(), new HerbData(Material.OAK_SAPLING, GrapeSaplingKey));
        HERB_DATA_MAP.put(KiwiSaplingKey.getKey(), new HerbData(Material.OAK_SAPLING, KiwiSaplingKey));
        HERB_DATA_MAP.put(PineappleSaplingKey.getKey(), new HerbData(Material.OAK_SAPLING, PineappleSaplingKey));
        HERB_DATA_MAP.put(StrawberrySaplingKey.getKey(), new HerbData(Material.OAK_SAPLING, StrawberrySaplingKey));
        HERB_DATA_MAP.put(BlueberrySaplingKey.getKey(), new HerbData(Material.OAK_SAPLING, BlueberrySaplingKey));
    }

    @EventHandler
    public void onCustomTreeGrow(StructureGrowEvent event) {
        Block sapling = event.getLocation().getBlock();
        if (!sapling.hasMetadata(SAPLING_METADATA)) return;

        String fruitKey = sapling.getMetadata(SAPLING_METADATA).get(0).asString();
        Supplier<ItemStack> fruitSupplier = FRUIT_MAP.get(fruitKey);
        if (fruitSupplier == null) return;

        event.setCancelled(true);
        Material saplingType = sapling.getType();
        sapling.removeMetadata(SAPLING_METADATA, getplugin);

        if (HERBACEOUS_KEYS.contains(fruitKey)) {
            sapling.setType(Material.OAK_LEAVES);
            sapling.setMetadata(HERB_LEAF_METADATA, new FixedMetadataValue(getplugin, fruitKey));
        } else {
            sapling.setType(Material.AIR);
            TreeType treeType = TREE_TYPE_MAP.getOrDefault(saplingType, TreeType.TREE);
            event.getWorld().generateTree(event.getLocation(), treeType);
            markLeavesWithFruit(event.getLocation(), fruitKey);
            placeFruitSkulls(event.getLocation(), fruitKey);
        }
    }

    private void markLeavesWithFruit(Location loc, String fruitKey) {
        World world = loc.getWorld();
        // 扩大扫描范围以覆盖大型树 (ExoticGarden参考)
        for (int x = -4; x <= 4; x++) {
            for (int y = 1; y <= 7; y++) {
                for (int z = -4; z <= 4; z++) {
                    Block block = world.getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
                    if (isLeaf(block.getType()) && RANDOM.nextDouble() < 0.2) {
                        block.setMetadata(LEAF_FRUIT_METADATA, new FixedMetadataValue(getplugin, fruitKey));
                    }
                }
            }
        }
    }

    // 在树叶下方生成果实头颅 (概率25%)，状态设为上方悬挂
    private void placeFruitSkulls(Location loc, String fruitKey) {
        Supplier<ItemStack> fruitSupplier = FRUIT_MAP.get(fruitKey);
        if (fruitSupplier == null) return;

        // 获取果实物品的PlayerProfile用于头颅
        ItemStack sample = fruitSupplier.get();
        PlayerProfile fruitProfile = null;
        if (sample.getItemMeta() instanceof SkullMeta skullMeta) {
            fruitProfile = skullMeta.getPlayerProfile();
        }

        World world = loc.getWorld();
        for (int x = -4; x <= 4; x++) {
            for (int y = 1; y <= 7; y++) {
                for (int z = -4; z <= 4; z++) {
                    Block leaf = world.getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
                    if (!isLeaf(leaf.getType())) continue;

                    Block below = leaf.getRelative(0, -1, 0);
                    if (below.getType() != Material.AIR) continue;

                    if (RANDOM.nextDouble() >= 0.25) continue;

                    // 使用PLAYER_HEAD放置在树叶下方
                    below.setType(Material.PLAYER_HEAD);
                    below.setMetadata(FRUIT_SKULL_METADATA, new FixedMetadataValue(getplugin, fruitKey));

                    if (fruitProfile != null && below.getState() instanceof Skull skull) {
                        skull.setPlayerProfile(fruitProfile);
                        skull.update(true);
                    }
                }
            }
        }
    }

    // 破坏果实头颅时掉落对应果实
    @EventHandler
    public void onFruitSkullBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.PLAYER_HEAD) return;
        if (!block.hasMetadata(FRUIT_SKULL_METADATA)) return;

        String fruitKey = block.getMetadata(FRUIT_SKULL_METADATA).get(0).asString();
        Supplier<ItemStack> fruitSupplier = FRUIT_MAP.get(fruitKey);
        block.removeMetadata(FRUIT_SKULL_METADATA, getplugin);

        event.setDropItems(false);
        if (fruitSupplier != null) {
            block.getWorld().dropItemNaturally(block.getLocation(), fruitSupplier.get());
        }
    }

    @EventHandler
    public void onLeafHarvest(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null || !isLeaf(block.getType())) return;

        if (block.hasMetadata(HERB_LEAF_METADATA)) {
            String fruitKey = block.getMetadata(HERB_LEAF_METADATA).get(0).asString();
            Supplier<ItemStack> fruitSupplier = FRUIT_MAP.get(fruitKey);
            HerbData herbData = HERB_DATA_MAP.get(fruitKey);
            if (fruitSupplier == null || herbData == null) return;

            event.setCancelled(true);
            Player player = event.getPlayer();
            ItemStack fruit = fruitSupplier.get();
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(fruit);
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), fruit);
            }

            block.removeMetadata(HERB_LEAF_METADATA, getplugin);
            block.setType(herbData.saplingType());
            block.setMetadata(SAPLING_METADATA, new FixedMetadataValue(getplugin, herbData.saplingKey().getKey()));

            player.getWorld().playSound(block.getLocation(), Sound.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, 1.0f, 1.0f);
            player.sendActionBar("§a已收获果实");
            return;
        }

        if (block.hasMetadata(LEAF_FRUIT_METADATA)) {
            String fruitKey = block.getMetadata(LEAF_FRUIT_METADATA).get(0).asString();
            Supplier<ItemStack> fruitSupplier = FRUIT_MAP.get(fruitKey);
            if (fruitSupplier == null) return;

            event.setCancelled(true);
            Player player = event.getPlayer();
            ItemStack fruit = fruitSupplier.get();
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(fruit);
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), fruit);
            }

            block.removeMetadata(LEAF_FRUIT_METADATA, getplugin);
            player.getWorld().playSound(block.getLocation(), Sound.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES, 1.0f, 1.0f);
            player.sendActionBar("§a已收获果实");
        }
    }

    // 树叶自然消失时清理果实元数据，并概率掉落果实
    @EventHandler
    public void onLeafDecay(org.bukkit.event.block.LeavesDecayEvent event) {
        Block block = event.getBlock();
        if (block.hasMetadata(LEAF_FRUIT_METADATA)) {
            String fruitKey = block.getMetadata(LEAF_FRUIT_METADATA).get(0).asString();
            Supplier<ItemStack> fruitSupplier = FRUIT_MAP.get(fruitKey);
            block.removeMetadata(LEAF_FRUIT_METADATA, getplugin);
            if (fruitSupplier != null && RANDOM.nextDouble() < 0.3) {
                block.getWorld().dropItemNaturally(block.getLocation(), fruitSupplier.get());
            }
        }
        if (block.hasMetadata(HERB_LEAF_METADATA)) {
            block.removeMetadata(HERB_LEAF_METADATA, getplugin);
        }
        if (block.hasMetadata(SAPLING_METADATA)) {
            block.removeMetadata(SAPLING_METADATA, getplugin);
        }
    }

    private static boolean isLeaf(Material material) {
        return material.name().endsWith("_LEAVES");
    }

    // 破坏带自定义树苗/果实元数据的方块时清理, 避免残留导致重种结错果
    @EventHandler
    public void onPlantBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.hasMetadata(SAPLING_METADATA)) block.removeMetadata(SAPLING_METADATA, getplugin);
        if (block.hasMetadata(HERB_LEAF_METADATA)) block.removeMetadata(HERB_LEAF_METADATA, getplugin);
        if (block.hasMetadata(FRUIT_SKULL_METADATA)) block.removeMetadata(FRUIT_SKULL_METADATA, getplugin);
        if (block.hasMetadata(LEAF_FRUIT_METADATA)) block.removeMetadata(LEAF_FRUIT_METADATA, getplugin);
    }
}
