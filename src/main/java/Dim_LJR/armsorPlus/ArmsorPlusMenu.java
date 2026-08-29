package Dim_LJR.armsorPlus;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import Dim_LJR.armsorPlus.Boss.BossMenu;
import Dim_LJR.armsorPlus.Boss.BossWorld;
import Dim_LJR.armsorPlus.Boss.CreatureMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.Food.FoodItems.*;
import static Dim_LJR.armsorPlus.Food.FoodItems.OrangeSapling;
import static Dim_LJR.armsorPlus.Item.Materials.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.GuideBookKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.MagicBallKey;
import static Dim_LJR.armsorPlus.OpenSea.LoadOpenSea.world;
import static org.bukkit.Material.*;

// 插件 GUI 菜单系统。
// 包括: 主菜单、设置、魔法球兑换商店、附魔书列表、魔法武器列表、魔法物品列表。
// 通过右键"高级附魔向导书"打开主菜单。
public class ArmsorPlusMenu implements Listener {

    private static Inventory menu;
    private static Inventory shop;
    private static Inventory enchantmentList;
    private static final Map<UUID, Integer> playerEnchantPage = new HashMap<>();
    private static final Map<UUID, Integer> playerFoodPage = new HashMap<>();
    private static final Map<UUID, Integer> playerSaplingPage = new HashMap<>();
    private static final Map<UUID, Integer> playerArmsPage = new HashMap<>();

    // 菜单页数常量
    private static final int WEAPON_PAGES = 2;
    private static final int ENCHANT_PAGES = 3;
    private static final int SAPLING_PAGES = 2;

    // 玩家登出时清理菜单页码, 防止 Map 泄漏
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        playerEnchantPage.remove(uuid);
        playerFoodPage.remove(uuid);
        playerSaplingPage.remove(uuid);
        playerArmsPage.remove(uuid);
    }
    private static Inventory armsList;
    private static Inventory magicItemsList;
    private static Inventory foodMenu;
    private static Inventory medicineMenu;
    private static Inventory saplingMenu;

    // 合成配方 Map (物品名 -> 配方材料)
    private static final Map<String, String[]> RECIPES = new LinkedHashMap<>();
    static {
        RECIPES.put(ChatColor.DARK_GREEN + "匕首", new String[]{
                "   ", " E ", " S ",
                "E=绿宝石", "S=木棍"});
        RECIPES.put(ChatColor.GOLD + "飞斧", new String[]{
                "N N", " E ", "S S",
                "N=下届合金锭", "E=绿宝石块", "S=木棍"});
        RECIPES.put(ChatColor.DARK_GRAY + "骷髅权杖", new String[]{
                "SSS", "STS", "SSS",
                "S=骷髅头颅", "T=不死图腾"});
        RECIPES.put(ChatColor.AQUA + "寒冰弓", new String[]{
                " IB", "I B", " IB",
                "I=蓝冰", "B=木棍"});
        RECIPES.put(ChatColor.GOLD + "火焰戟", new String[]{
                " NF", " BN", "B  ",
                "N=下届合金锭", "F=火焰弹", "B=烈焰棒"});
        RECIPES.put(ChatColor.LIGHT_PURPLE + "回春散", new String[]{
                "MMM", "MGM", "MMM",
                "M=闪烁的西瓜片", "G=玻璃瓶"});
        RECIPES.put(ChatColor.RED + "止血绷带", new String[]{
                " N ", "N W", "   ",
                "N=地狱疣", "W=白色羊毛"});
        RECIPES.put(ChatColor.GOLD + "压缩饼干", new String[]{
                "BBB", "BBB", "BBB",
                "B=面包"});
        RECIPES.put(ChatColor.WHITE + "盐", new String[]{
                "S  ", "   ", "   ",
                "S=沙子"});
        RECIPES.put(ChatColor.GOLD + "肉干", new String[]{
                "S  ", "M  ", "   ",
                "S=盐", "M=任意熟肉(非腐肉)"});
        RECIPES.put(ChatColor.LIGHT_PURPLE + "甜浆果派", new String[]{
                "BBB", "WWW", "   ",
                "B=甜浆果", "W=小麦"});
        RECIPES.put(ChatColor.GOLD + "酒桶", new String[]{
                "WWW", " B ", "   ",
                "W=小麦", "B=木桶"});
        RECIPES.put(ChatColor.DARK_GRAY + "腐肉干", new String[]{
                "R  ", "S  ", "   ",
                "R=腐肉", "S=盐"});
        RECIPES.put(ChatColor.GOLD + "猪肉干", new String[]{
                "P  ", "S  ", "   ",
                "P=熟猪排", "S=盐"});
        RECIPES.put(ChatColor.GOLD + "羊肉干", new String[]{
                "M  ", "S  ", "   ",
                "M=熟羊肉", "S=盐"});
        RECIPES.put(ChatColor.DARK_AQUA + "雨御前", new String[]{
                " N ", "NBN", " N ",
                "N=海晶碎片", "B=钻石剑"});
        RECIPES.put(ChatColor.GOLD + "飞天御剑", new String[]{
                " N ", "NEN", " N ",
                "N=幻翼膜", "E=金剑"});
        RECIPES.put(ChatColor.DARK_PURPLE + "瞬步刃", new String[]{
                " N ", "NEN", " N ",
                "N=末影珍珠", "E=下界合金剑"});
        RECIPES.put(ChatColor.RED + "血祭之剑", new String[]{
                " R ", "RSR", " R ",
                "R=红石", "S=骷髅头颅"});
        RECIPES.put(ChatColor.WHITE + "重剑", new String[]{
                " I ", " I ", " S ",
                "I=铁块", "S=木棍"});
        RECIPES.put(ChatColor.GOLD + "精炼金刚石", new String[]{
                "DDD", "DDD", "DDD",
                "D=钻石块"});
        RECIPES.put(ChatColor.GOLD + "基础强化石", new String[]{
                "DD  ", "    ", "    ",
                "D=钻石块", "需要4个"});
        RECIPES.put(ChatColor.AQUA + "寒冰剑", new String[]{
                "BBB", "BSB", "BBB",
                "B=蓝冰", "S=钻石剑"});
        RECIPES.put(ChatColor.WHITE + "盘丝弓", new String[]{
                "CCC", "CBC", "CCC",
                "C=蜘蛛网", "B=弓"});
        RECIPES.put(ChatColor.RED + "爆炸弓", new String[]{
                "TTT", "TBT", "TTT",
                "T=TNT", "B=弓"});
        RECIPES.put(ChatColor.GOLD + "高级附魔向导", new String[]{
                " C ", "   ", "   ",
                "C=圆石"});

        // ===== 食物合成配方 =====
        RECIPES.put(ChatColor.GOLD + "汉堡", new String[]{
                " B ", " C ", "   ",
                "B=面包", "C=熟牛肉"});
        RECIPES.put(ChatColor.RED + "热狗", new String[]{
                " B ", " P ", "   ",
                "B=面包", "P=熟猪排"});
        RECIPES.put(ChatColor.RED + "辣椒", new String[]{
                "B  ", "R  ", "C  ",
                "B=碗", "R=红染料", "C=熟牛肉"});
        RECIPES.put(ChatColor.GOLD + "金盾丹", new String[]{
                "GGG", "GNG", "GGG",
                "G=金块", "N=地狱疣"});

        // ===== 钢制装备合成配方 =====
        RECIPES.put(ChatColor.GRAY + "钢剑", new String[]{
                " I ", " I ", " S ",
                "I=钢锭", "S=木棍"});
        RECIPES.put(ChatColor.GRAY + "钢头盔", new String[]{
                "III", "I I", "   ",
                "I=钢锭"});
        RECIPES.put(ChatColor.GRAY + "钢胸甲", new String[]{
                "I I", "III", "III",
                "I=钢锭"});
        RECIPES.put(ChatColor.GRAY + "钢护腿", new String[]{
                "III", "I I", "I I",
                "I=钢锭"});
        RECIPES.put(ChatColor.GRAY + "钢靴子", new String[]{
                "I I", "I I", "   ",
                "I=钢锭"});

        // ===== 鱼骨系列武器合成配方 (上一步武器在中心E位置) =====
        RECIPES.put(ChatColor.DARK_GREEN + "尸王", new String[]{
                " Z ", "ZBZ", " Z ",
                "Z=僵尸头颅", "B=骨头"});
        RECIPES.put(ChatColor.YELLOW + "鱼骨剑", new String[]{
                "A  ", "B  ", "C  ",
                "A=鲑鱼", "B=鲑鱼", "C=骨头"});
        RECIPES.put(ChatColor.YELLOW + "鱼骨刀", new String[]{
                "A  ", "B  ", "C  ",
                "A=鳕鱼", "B=鳕鱼", "C=骨头"});
        RECIPES.put(ChatColor.YELLOW + "鱼刺剑", new String[]{
                "ABA", "BCB", "ABA",
                "A=骨块", "B=海晶沙粒", "C=鱼骨剑"});
        RECIPES.put(ChatColor.YELLOW + "鱼刺刀", new String[]{
                "ABA", "BCB", "ABA",
                "A=骨块", "B=海晶沙粒", "C=鱼骨刀"});
        RECIPES.put(ChatColor.AQUA + "海骨剑", new String[]{
                "ABA", "BCB", "ABA",
                "A=海绵", "B=海晶灯", "C=鱼刺剑"});
        RECIPES.put(ChatColor.AQUA + "海骨刀", new String[]{
                "ABA", "BCB", "ABA",
                "A=海绵", "B=海晶灯", "C=鱼刺刀"});
        RECIPES.put(ChatColor.LIGHT_PURPLE + "灵骨剑", new String[]{
                "AAA", "ABA", "AAA",
                "A=灵魂沙", "B=海骨剑"});
        RECIPES.put(ChatColor.LIGHT_PURPLE + "灵骨刀", new String[]{
                "AAA", "ABA", "AAA",
                "A=灵魂沙", "B=海骨刀"});
        RECIPES.put(ChatColor.DARK_AQUA + "海刺剑", new String[]{
                "ABA", "BCB", "ABA",
                "A=鳞甲", "B=海洋之心", "C=灵骨剑"});
        RECIPES.put(ChatColor.DARK_AQUA + "海刺刀", new String[]{
                "ABA", "BCB", "ABA",
                "A=鳞甲", "B=海洋之心", "C=灵骨刀"});
        RECIPES.put(ChatColor.DARK_PURPLE + "蚀骨剑", new String[]{
                "ABA", "ACA", "AAA",
                "A=灵魂土", "B=凋零骷髅头", "C=灵骨剑"});
        RECIPES.put(ChatColor.DARK_PURPLE + "灵刺剑", new String[]{
                "ABA", "BCB", "ABA",
                "A=潮涌核心", "B=恶魂之泪", "C=海刺剑"});
        RECIPES.put(ChatColor.DARK_PURPLE + "灵刺刀", new String[]{
                "ABA", "BCB", "ABA",
                "A=潮涌核心", "B=凋零骷髅头", "C=海刺刀"});
        RECIPES.put(ChatColor.DARK_BLUE + "海哭剑", new String[]{
                "ABA", "BCB", "ABA",
                "A=下界之星", "B=潮涌核心", "C=灵刺剑"});
        RECIPES.put(ChatColor.DARK_BLUE + "海哭刀", new String[]{
                "ABA", "BCB", "ABA",
                "A=下界之星", "B=潮涌核心", "C=灵刺刀"});

        // ===== 吞云斩月刀 (0.3J+) =====
        RECIPES.put(ChatColor.WHITE + "吞云斩月刀", new String[]{
                " M ", "BSB", " M ",
                "M=月之碎片", "B=幻术师的遗骨", "S=钻石剑"});
    }

    // ========================================================================
    // 菜单创建
    // ========================================================================

    // 魔法物品列表 (强化石等)
    public Inventory createMagicItemMenu() {
        magicItemsList = Bukkit.createInventory(null, 45, ChatColor.DARK_PURPLE + "魔法物品");
        addBorder(magicItemsList, PURPLE_STAINED_GLASS_PANE);
        magicItemsList.setItem(10, BasicStone(1));
        magicItemsList.setItem(11, ArmsPlusCreateI(1));
        magicItemsList.setItem(12, ArmsPlusCreateII(1));
        magicItemsList.setItem(13, ArmorPlusCreate(1));
        magicItemsList.setItem(14, ArmorPlusCreateII(1));
        magicItemsList.setItem(15, BowPlusCreate(1));
        magicItemsList.setItem(36, createInfoItem(Material.BARRIER, "§c返回", "§7点击返回主菜单"));
        return magicItemsList;
    }

    public static int getArmsPage(UUID uuid) { return playerArmsPage.getOrDefault(uuid, 0); }

    // 魔法武器列表 (支持翻页)
    public Inventory createArmsListMenu(Player player, int page) {
        if (player != null) playerArmsPage.put(player.getUniqueId(), page);
        int totalPages = WEAPON_PAGES;
        String title = ChatColor.DARK_PURPLE + "魔法武器 " + (page + 1) + "/" + totalPages;
        armsList = Bukkit.createInventory(null, 45, title);
        addBorder(armsList, PURPLE_STAINED_GLASS_PANE);
        if (page == 0) {
            armsList.setItem(10, BloodSword(1));
            armsList.setItem(11, Iron_Epee(1));
            armsList.setItem(12, Dagger(1));
            armsList.setItem(13, ThrowingAxe(1));
            armsList.setItem(14, SkeletonScepter(1));
            armsList.setItem(15, FrostBow(1));
            armsList.setItem(16, FlameHalberd(1));
            armsList.setItem(19, RainSword(1));
            armsList.setItem(20, FlyingSword(1));
            armsList.setItem(21, FlashStepBlade(1));
            armsList.setItem(22, MagicStick(1));
            armsList.setItem(23, IceSword(1));
            armsList.setItem(24, WebBow(1));
            armsList.setItem(25, ExplosionBow(1));
            armsList.setItem(28, DevourLifeSword(1));
            armsList.setItem(29, StarTraceSword(1));
            armsList.setItem(30, BlackTortoiseSword(1));
            armsList.setItem(31, ThunderGlow(1));
            armsList.setItem(32, BlazingSun(1));
            armsList.setItem(33, PeachWoodSword(1));
            armsList.setItem(34, SteelSword(1));
        } else if (page == 1) {
            armsList.setItem(10, CorpseKing(1));
            armsList.setItem(11, FishBoneSword(1));
            armsList.setItem(12, FishBoneKnife(1));
            armsList.setItem(13, FishSpineSword(1));
            armsList.setItem(14, FishSpineKnife(1));
            armsList.setItem(15, SeaBoneSword(1));
            armsList.setItem(16, SeaBoneKnife(1));
            armsList.setItem(19, SpiritBoneSword(1));
            armsList.setItem(20, SpiritBoneKnife(1));
            armsList.setItem(21, SeaSpineSword(1));
            armsList.setItem(22, SeaSpineKnife(1));
            armsList.setItem(23, CorrodeBoneSword(1));
            armsList.setItem(24, SpiritSpineSword(1));
            armsList.setItem(25, SpiritSpineKnife(1));
            armsList.setItem(28, SeaCrySword(1));
            armsList.setItem(29, SeaCryKnife(1));
            // 幻术师武器
            armsList.setItem(30, IllusionBlade(1));
            armsList.setItem(31, IllusionStaff(1));
            // 吞云斩月刀
            armsList.setItem(32, CloudMoonBlade(1));
            // 狂怒掠夺者之弩
            armsList.setItem(33, RagingPlundererCrossbow(1));
        }
        if (page > 0) armsList.setItem(39, createInfoItem(Material.ARROW, "§a← 上一页", "§7点击返回上一页"));
        if (page < totalPages - 1) armsList.setItem(41, createInfoItem(Material.ARROW, "§a下一页 →", "§7点击查看下一页"));
        armsList.setItem(40, createInfoItem(Material.BARRIER, "§c返回", "§7点击返回主菜单"));
        return armsList;
    }
    public Inventory createArmsListMenu(Player player) { return createArmsListMenu(player, 0); }
    public Inventory createArmsListMenu() { return createArmsListMenu(null, 0); }

    // 护甲菜单
    public Inventory createArmorListMenu() {
        Inventory armorInv = Bukkit.createInventory(null, 45, ChatColor.DARK_GREEN + "护甲");
        addBorder(armorInv, GREEN_STAINED_GLASS_PANE);
        armorInv.setItem(20, SteelHelmet(1));
        armorInv.setItem(21, SteelChestplate(1));
        armorInv.setItem(22, SteelLeggings(1));
        armorInv.setItem(23, SteelBoots(1));
        armorInv.setItem(40, createInfoItem(Material.BARRIER, "§c返回", "§7点击返回主菜单"));
        return armorInv;
    }


    // 获取食物页数
    public static int getFoodPage(UUID uuid) { return playerFoodPage.getOrDefault(uuid, 0); }

    // 获取所有食物 (按展示顺序, 植物关闭时排除水果)
    private List<ItemStack> getAllFoodItems() {
        List<ItemStack> items = new ArrayList<>();
        // 基础食物
        items.add(Jerky(1));
        items.add(SweetBerryPie(1));
        items.add(RottenJerky(1));
        items.add(WineBarrel(1));
        items.add(Salt(1));
        items.add(PorkJerky(1));
        items.add(MuttonJerky(1));
        // 水果 (植物系统开启时)
        if (ArmsorPlus.EnablePlants) {
            items.add(Plum(1));
            items.add(Hazelnut(1));
            items.add(Coconut(1));
            items.add(BigApple(1));
            items.add(Pineapple(1));
            items.add(Strawberry(1));
            items.add(Blueberry(1));
            items.add(Orange(1));
            items.add(Tangerine(1));
            items.add(IceCube(1));
            items.add(Fig(1));
            items.add(Date(1));
            items.add(Persimmon(1));
            items.add(Mangosteen(1));
            items.add(CherryTomato(1));
            items.add(Tomato(1));
            items.add(Grape(1));
            items.add(Pomegranate(1));
            items.add(Chestnut(1));
            items.add(Kiwi(1));
            items.add(Longan(1));
            items.add(Lychee(1));
            items.add(Cherry(1));
            items.add(Peach(1));
        }
        // 新食物 (辣椒/洋葱/卷心菜 属于插件植物产物, 随植物开关隐藏)
        items.add(Burger(1));
        items.add(HotDog(1));
        if (ArmsorPlus.EnablePlants) {
            items.add(Chili(1));
            items.add(Onion(1));
            items.add(Cabbage(1));
        }
        items.add(Poop(1));
        return items;
    }

    // 在食物菜单内容区按顺序放置物品, 自动换行
    // 内容区槽位: 第2行 10-16, 第3行 19-25, 第4行 28-34 (首尾列为边框)
    // 返回下一个可用槽位 (超出内容区后返回 36)
    private int putFoodItem(Inventory inv, int slot, ItemStack item) {
        if (slot >= 36) return slot;
        inv.setItem(slot, item);
        if (slot == 16) return 19;
        if (slot == 25) return 28;
        if (slot == 34) return 36;
        return slot + 1;
    }

    // 原材料菜单 (BOSS掉落+材料)
    public Inventory createMaterialMenu() {
        Inventory matMenu = Bukkit.createInventory(null, 45, ChatColor.GRAY + "原材料");
        addBorder(matMenu, GRAY_STAINED_GLASS_PANE);
        // BOSS掉落材料
        matMenu.setItem(20, IceCore(1));          // 雪人王
        matMenu.setItem(21, FireCore(1));         // 烈焰领主
        matMenu.setItem(22, IllusionerBone(1));   // 幻术师
        matMenu.setItem(23, IllusionerScrap(1));  // 幻术师
        // 合成材料
        matMenu.setItem(24, RawSteel(1));
        matMenu.setItem(25, SteelIngot(1));
        matMenu.setItem(36, createInfoItem(Material.BARRIER, "§c返回", "§7点击返回主菜单"));
        return matMenu;
    }

    // 药品菜单 (单页)
    public Inventory createMedicineMenu() {
        medicineMenu = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "药品");
        addBorder(medicineMenu, PURPLE_STAINED_GLASS_PANE);
        medicineMenu.setItem(11, RejuvenationPowder(1));
        medicineMenu.setItem(12, HemostaticBandage(1));
        medicineMenu.setItem(13, CompressedBiscuit(1));
        medicineMenu.setItem(14, GoldShieldElixir(1));
        medicineMenu.setItem(15, IceCore(1));
        medicineMenu.setItem(16, FireCore(1));
        medicineMenu.setItem(22, createInfoItem(Material.BARRIER, "§c返回", "§7点击返回主菜单"));
        return medicineMenu;
    }

    // 食物/药品菜单 (支持翻页)
    public Inventory createFoodMenu(Player player, int page) {
        List<ItemStack> items = getAllFoodItems();
        int perPage = 21; // 内容区 3x7
        int totalPages = Math.max(1, (int) Math.ceil(items.size() / (double) perPage));
        int safePage = Math.max(0, Math.min(page, totalPages - 1));
        if (player != null) playerFoodPage.put(player.getUniqueId(), safePage);

        String title = ChatColor.GREEN + "食物 " + (safePage + 1) + "/" + totalPages;
        foodMenu = Bukkit.createInventory(null, 45, title);
        addBorder(foodMenu, GREEN_STAINED_GLASS_PANE);

        // 按实际物品数填充本页
        int slot = 10;
        int start = safePage * perPage;
        int end = Math.min(items.size(), start + perPage);
        for (int i = start; i < end; i++) {
            slot = putFoodItem(foodMenu, slot, items.get(i));
        }

        if (safePage > 0) foodMenu.setItem(39, createInfoItem(Material.ARROW, "§a← 上一页", "§7点击返回上一页"));
        if (safePage < totalPages - 1) foodMenu.setItem(41, createInfoItem(Material.ARROW, "§a下一页 →", "§7点击查看下一页"));
        foodMenu.setItem(40, createInfoItem(Material.BARRIER, "§c返回", "§7点击返回主菜单"));
        return foodMenu;
    }

    public Inventory createFoodMenu(Player player) { return createFoodMenu(player, 0); }
    public Inventory createFoodMenu() { return createFoodMenu(null, 0); }


    // 树苗商店 (支持翻页) — 植物系统关闭时提示不可用
    public Inventory createSaplingMenu(Player player, int page) {
        if (!ArmsorPlus.EnablePlants) {
            Inventory disabled = Bukkit.createInventory(null, 27, ChatColor.RED + "树苗商店");
            addBorder(disabled, GRAY_STAINED_GLASS_PANE);
            disabled.setItem(13, createInfoItem(BARRIER, "§c植物系统已关闭",
                    "§7在 config.yml 中开启 EnablePlants 后可查看树苗"));
            disabled.setItem(22, createInfoItem(Material.BARRIER, "§c返回", "§7点击返回主菜单"));
            return disabled;
        }
        if (player != null) playerSaplingPage.put(player.getUniqueId(), page);
        String title = ChatColor.GREEN + "树苗商店 " + (page + 1) + "/" + SAPLING_PAGES;
        saplingMenu = Bukkit.createInventory(null, 45, title);
        addBorder(saplingMenu, GREEN_STAINED_GLASS_PANE);
        if (page == 0) {
            saplingMenu.setItem(10, FigSapling(1));
            saplingMenu.setItem(11, DateSapling(1));
            saplingMenu.setItem(12, PersimmonSapling(1));
            saplingMenu.setItem(13, MangosteenSapling(1));
            saplingMenu.setItem(14, CherryTomatoSapling(1));
            saplingMenu.setItem(15, TomatoSapling(1));
            saplingMenu.setItem(16, GrapeSapling(1));
            saplingMenu.setItem(19, PomegranateSapling(1));
            saplingMenu.setItem(20, ChestnutSapling(1));
            saplingMenu.setItem(21, KiwiSapling(1));
            saplingMenu.setItem(22, LonganSapling(1));
            saplingMenu.setItem(23, LycheeSapling(1));
            saplingMenu.setItem(24, CherrySapling(1));
            saplingMenu.setItem(25, OrangeSapling(1));
        } else {
            saplingMenu.setItem(10, PlumSapling(1));
            saplingMenu.setItem(11, HazelnutSapling(1));
            saplingMenu.setItem(12, CoconutSapling(1));
            saplingMenu.setItem(13, PineappleSapling(1));
            saplingMenu.setItem(14, StrawberrySapling(1));
            saplingMenu.setItem(15, BlueberrySapling(1));
            saplingMenu.setItem(16, PeachSapling(1));
            saplingMenu.setItem(19, TangerineSapling(1));
            saplingMenu.setItem(20, BigAppleSapling(1));
        }
        if (page > 0) saplingMenu.setItem(39, createInfoItem(Material.ARROW, "§a← 上一页", "§7点击返回上一页"));
        if (page < SAPLING_PAGES - 1) saplingMenu.setItem(41, createInfoItem(Material.ARROW, "§a下一页 →", "§7点击查看下一页"));
        saplingMenu.setItem(40, createInfoItem(Material.BARRIER, "§c返回", "§7点击返回主菜单"));
        return saplingMenu;
    }

    public Inventory createSaplingMenu(Player player) { return createSaplingMenu(player, 0); }
    public Inventory createSaplingMenu() { return createSaplingMenu(null, 0); }
    // 魔法球兑换商店
    public Inventory createShopMenu() {
        shop = Bukkit.createInventory(null, 45, "§e魔法球兑换商店");
        addBorder(shop, YELLOW_STAINED_GLASS_PANE);
        shop.setItem(10, MagicBallCreateI(1));
        shop.setItem(11, MagicBallCreateII(1));
        shop.setItem(12, MagicBallCreateIII(1));
        shop.setItem(13, MagicBallCreateIV(1));
        shop.setItem(40, createInfoItem(EXPERIENCE_BOTTLE, "§a兑换说明",
                "§7使用经验值兑换魔法球", "§7不同等级魔法球需要不同经验值",
                "§7点击商品进行兑换"));
        shop.setItem(36, createInfoItem(Material.BARRIER, "§c返回", "§7点击返回主菜单"));
        return shop;
    }

    // 获取玩家当前附魔列表页码
    public static int getEnchantPage(UUID uuid) { return playerEnchantPage.getOrDefault(uuid, 0); }

    // 高级附魔书列表 (支持翻页)
    public Inventory createEnchantmentListMenu(Player player, int page) {
        if (player != null) playerEnchantPage.put(player.getUniqueId(), page);
        String title = "§e高级附魔书列表 " + (page + 1) + "/" + ENCHANT_PAGES;
        enchantmentList = Bukkit.createInventory(null, 45, title);
        addBorder(enchantmentList, GRAY_STAINED_GLASS_PANE);

        if (page == 0) {
            // 第1页: 原有附魔
            enchantmentList.setItem(10, Dodge_EnchantdeBook(1, 5));
            enchantmentList.setItem(11, Famine_EnchantdeBook(1, 3));
            enchantmentList.setItem(12, BloodSacrifice_EnchantdeBook(1, 3));
            enchantmentList.setItem(13, Ripples_EnchantdeBook(1, 3));
            enchantmentList.setItem(14, EffectClear_EnchantdeBook(1, 3));
            enchantmentList.setItem(15, Freeze_EnchantedBook(1, 3));
            enchantmentList.setItem(16, Survivor_EnchantedBook(1, 5));
            enchantmentList.setItem(19, Withering_EnchantedBook(1, 5));
            enchantmentList.setItem(20, Blocking_EnchantedBook(1, 5));
            enchantmentList.setItem(21, Revenge_EnchantedBook(1, 3));
            enchantmentList.setItem(22, HealthBoost_EnchantedBook(1, 4));
            enchantmentList.setItem(23, ExplosiveArrow_EnchantedBook(1, 3));
            enchantmentList.setItem(24, ShadowDodge_EnchantdeBook(1, 5));
            enchantmentList.setItem(25, ArrowSpeed_EnchantdeBook(1, 5));
            enchantmentList.setItem(28, Sniping_EnchantdeBook(1, 5));
            enchantmentList.setItem(29, DoubleHit_EnchantdeBook(1, 5));
            enchantmentList.setItem(30, Feeding_EnchantdeBook(1, 5));
            enchantmentList.setItem(31, DiamondDrill_EnchantedBook(1, 5));
            enchantmentList.setItem(32, QuickThrust_EnchantedBook(1, 5));
            enchantmentList.setItem(33, Blindness_EnchantedBook(1, 5));
            enchantmentList.setItem(34, Pierce_EnchantedBook(1, 3));
        } else if (page == 1) {
            // 第2页: 0.3I 新附魔
            enchantmentList.setItem(10, ProtectionPRO_EnchantedBook(1, 5));
            enchantmentList.setItem(11, Stun_EnchantedBook(1, 5));
            enchantmentList.setItem(12, GolemGuardian_EnchantedBook(1, 5));
            enchantmentList.setItem(13, CriticalStrike_EnchantedBook(1, 5));
            enchantmentList.setItem(14, Piercing_EnchantedBook(1, 1));
            enchantmentList.setItem(15, LavaWalker_EnchantedBook(1, 1));
            enchantmentList.setItem(16, LightningCall_EnchantedBook(1, 3));
            enchantmentList.setItem(19, Holographic_EnchantedBook(1, 1));
            enchantmentList.setItem(20, Tracking_EnchantedBook(1, 1));
            enchantmentList.setItem(21, Harvest_EnchantedBook(1, 3));
            enchantmentList.setItem(22, AutoPlant_EnchantedBook(1, 1));
            enchantmentList.setItem(23, StrongBurst_EnchantedBook(1, 1));
            enchantmentList.setItem(24, MultiShot_EnchantedBook(1, 3));
            enchantmentList.setItem(25, Poison_EnchantedBook(1, 5));
            enchantmentList.setItem(28, SharpBlade_EnchantedBook(1, 3));
            enchantmentList.setItem(29, DamageDispersal_EnchantedBook(1, 5));
            enchantmentList.setItem(30, HerbGuard_EnchantedBook(1, 3));
            enchantmentList.setItem(31, FireBlade_EnchantedBook(1, 3));
            enchantmentList.setItem(32, FrostBlade_EnchantedBook(1, 3));
            enchantmentList.setItem(33, ThunderBlade_EnchantedBook(1, 3));
            enchantmentList.setItem(34, MagicBlade_EnchantedBook(1, 3));
        } else {
            // 第3页: 更多新附魔
            enchantmentList.setItem(10, IceSpike_EnchantedBook(1, 3));
            enchantmentList.setItem(11, Inferno_EnchantedBook(1, 3));
            enchantmentList.setItem(12, HeavyArmor_EnchantedBook(1, 1));
            enchantmentList.setItem(13, EarthFavor_EnchantedBook(1, 3));
            enchantmentList.setItem(14, Ambush_EnchantedBook(1, 3));
            enchantmentList.setItem(15, ThunderclapArrow_EnchantedBook(1, 3));
        }

        // 翻页导航按钮
        if (page > 0) enchantmentList.setItem(39, createInfoItem(Material.ARROW, "§a← 上一页", "§7点击返回上一页"));
        if (page < ENCHANT_PAGES - 1) enchantmentList.setItem(41, createInfoItem(Material.ARROW, "§a下一页 →", "§7点击查看下一页"));
        enchantmentList.setItem(40, createInfoItem(Material.BARRIER, "§c返回", "§7点击返回主菜单"));

        return enchantmentList;
    }

    public Inventory createEnchantmentListMenu(Player player) {
        return createEnchantmentListMenu(player, 0);
    }

    public Inventory createEnchantmentListMenu() {
        return createEnchantmentListMenu(null, 0);
    }

    // 插件主菜单
    public Inventory createMenu() {
        menu = Bukkit.createInventory(null, 45, ChatColor.DARK_PURPLE + "ArmsorPlus插件菜单");
        addBorder(menu, PURPLE_STAINED_GLASS_PANE);
        menu.setItem(10, createInfoItem(IRON_SWORD, ChatColor.GOLD + "魔法武器列表",
                ChatColor.GOLD + "点击查看"));
        menu.setItem(11, createInfoItem(FIREWORK_STAR, ChatColor.DARK_PURPLE + "兑换魔法球",
                ChatColor.GOLD + "点击进入兑换窗口"));
        menu.setItem(12, createInfoItem(BOOK, ChatColor.GOLD + "高级附魔书列表",
                ChatColor.GOLD + "点击查看"));
        menu.setItem(13, createInfoItem(DIAMOND, ChatColor.AQUA + "魔法物品列表",
                ChatColor.AQUA + "点击查看"));
        menu.setItem(14, createInfoItem(GRASS_BLOCK, ChatColor.BLUE + "公海世界",
                ChatColor.BLUE + "点击传送"));
        menu.setItem(15, createInfoItem(ZOMBIE_HEAD, ChatColor.RED + "BOSS清单",
                ChatColor.RED + "点击查看可召唤的BOSS"));
        menu.setItem(16, createInfoItem(ENDER_PEARL, ChatColor.RED + "前往BOSS世界",
                ChatColor.RED + "点击传送到BOSS世界"));
        menu.setItem(19, createInfoItem(BREAD, ChatColor.GREEN + "食物",
                ChatColor.GREEN + "点击查看食物"));
        menu.setItem(20, createInfoItem(GOLDEN_APPLE, ChatColor.DARK_PURPLE + "药品",
                ChatColor.DARK_PURPLE + "点击查看药品与材料"));
        menu.setItem(21, createInfoItem(IRON_INGOT, ChatColor.GRAY + "原材料",
                ChatColor.GRAY + "点击查看BOSS掉落物和材料"));
        menu.setItem(22, createInfoItem(IRON_CHESTPLATE, ChatColor.DARK_GREEN + "护甲",
                ChatColor.DARK_GREEN + "点击查看护甲"));
        // 植物系统开启时显示树苗商店 + 回到主世界; 关闭时回到主世界自动补位到 slot 23
        if (ArmsorPlus.EnablePlants) {
            menu.setItem(23, createInfoItem(OAK_SAPLING, ChatColor.GREEN + "树苗商店",
                    ChatColor.GREEN + "点击查看树苗"));
            menu.setItem(24, createInfoItem(COMPASS, ChatColor.GREEN + "回到主世界",
                    ChatColor.GREEN + "点击传送回主世界"));
        } else {
            menu.setItem(23, createInfoItem(COMPASS, ChatColor.GREEN + "回到主世界",
                    ChatColor.GREEN + "点击传送回主世界"));
        }
        menu.setItem(25, createInfoItem(IRON_GOLEM_SPAWN_EGG, ChatColor.GREEN + "生物菜单",
                ChatColor.GREEN + "点击查看可召唤的生物"));
        menu.setItem(34, createInfoItem(COMPARATOR, ChatColor.GRAY + "设置",
                ChatColor.GRAY + "点击打开个人设置"));
        return menu;
    }

    // 个人设置菜单 (按玩家状态动态生成)
    public Inventory createSettingsMenu(Player player) {
        UUID uuid = player.getUniqueId();
        Inventory settings = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "个人设置");

        addBorder(settings, GRAY_STAINED_GLASS_PANE);

        // 附魔生效通知开关
        boolean notifyOn = PlayerSettings.isNotificationEnabled(uuid);
        settings.setItem(11, createInfoItem(
                notifyOn ? GREEN_DYE : GRAY_DYE,
                (notifyOn ? "§a" : "§7") + "附魔生效通知",
                "§7当前: " + (notifyOn ? "§a§l开启" : "§7§l关闭"),
                "",
                "§e点击切换",
                "§7关闭后将不再收到附魔触发的聊天消息"));

        // 管理员模式 (仅op可见)
        if (player.hasPermission("ArmsorPlus.op")) {
            boolean adminOn = PlayerSettings.isAdminMode(uuid);
            settings.setItem(15, createInfoItem(
                    adminOn ? GOLDEN_APPLE : APPLE,
                    (adminOn ? "§6" : "§7") + "管理员模式",
                    "§7当前: " + (adminOn ? "§6§l已开启" : "§7§l已关闭"),
                    "",
                    "§e点击切换",
                    "§7开启后可在菜单列表中直接拿取物品"));
        }

        // 自动下载资源包
        boolean hasPack = !ArmsorPlus.ResourcePackUrl.isEmpty();
        settings.setItem(13, createInfoItem(
                hasPack ? GRASS_BLOCK : BARRIER,
                (hasPack ? "§a" : "§7") + "自动下载资源包",
                hasPack ? "§7点击发送资源包下载请求" : "§c资源包地址未配置",
                hasPack ? "§7地址: " + ArmsorPlus.ResourcePackUrl : "§7请在 config.yml 设置 ResourcePackUrl"));

        return settings;
    }

    // ========================================================================
    // 事件监听
    // ========================================================================

    // 右键向导书打开主菜单
    @EventHandler
    public void onBookRightClick(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, GuideBookKey) == 0) return;
        event.setCancelled(true);
        event.getPlayer().openInventory(createMenu());
    }

    // 菜单点击事件处理
    @EventHandler
    public void onShopClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();

        // ---- 主菜单导航 (用标题识别, 避免静态实例被覆盖后失效) ----
        if (event.getClickedInventory() == event.getView().getTopInventory()
                && event.getView().getTitle().equals(ChatColor.DARK_PURPLE + "ArmsorPlus插件菜单")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            String name = clicked.getItemMeta().getDisplayName();
            if (name.equals(ChatColor.GOLD + "高级附魔书列表")) {
                player.openInventory(createEnchantmentListMenu(player));
            } else if (name.equals(ChatColor.DARK_PURPLE + "兑换魔法球")) {
                player.openInventory(createShopMenu());
            } else if (name.equals(ChatColor.AQUA + "魔法物品列表")) {
                player.openInventory(createMagicItemMenu());
            } else if (name.equals(ChatColor.GOLD + "魔法武器列表")) {
                player.openInventory(createArmsListMenu(player));
            } else if (name.equals(ChatColor.DARK_GREEN + "护甲")) {
                player.openInventory(createArmorListMenu());
            } else if (name.equals(ChatColor.GREEN + "食物")) {
                player.openInventory(createFoodMenu(player));
            } else if (name.equals(ChatColor.DARK_PURPLE + "药品")) {
                player.openInventory(createMedicineMenu());
            } else if (name.equals(ChatColor.GRAY + "原材料")) {
                player.openInventory(createMaterialMenu());
            } else if (name.equals(ChatColor.GREEN + "树苗商店")) {
                player.openInventory(createSaplingMenu());
            } else if (name.equals(ChatColor.BLUE + "公海世界")) {
                player.teleport(world.getSpawnLocation());
                player.sendActionBar(Component.text("正在传送..."));
            } else if (name.equals(ChatColor.GREEN + "回到主世界")) {
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                player.sendActionBar(Component.text("已回到主世界"));
            } else if (name.equals(ChatColor.RED + "BOSS清单")) {
                player.openInventory(BossMenu.getBossList());
            } else if (name.equals(ChatColor.GREEN + "生物菜单")) {
                player.openInventory(CreatureMenu.getCreatureList());
            } else if (name.equals(ChatColor.RED + "前往BOSS世界")) {
                if (BossWorld.world != null) {
                    player.teleport(BossWorld.world.getSpawnLocation());
                    player.sendMessage(ChatColor.RED + "已传送到BOSS世界");
                } else {
                    player.sendMessage(ChatColor.RED + "BOSS世界未加载");
                }
            } else if (name.equals(ChatColor.GRAY + "设置")) {
                player.openInventory(createSettingsMenu(player));
            }
            return;
        }

        // ---- 设置菜单 ----
        if (event.getView().getTitle().equals(ChatColor.DARK_GRAY + "个人设置")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            String name = clicked.getItemMeta().getDisplayName();
            if (name.contains("附魔生效通知")) {
                boolean current = PlayerSettings.isNotificationEnabled(uuid);
                PlayerSettings.setNotificationEnabled(uuid, !current);
                player.openInventory(createSettingsMenu(player));
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            } else if (name.contains("管理员模式") && player.hasPermission("ArmsorPlus.op")) {
                boolean current = PlayerSettings.isAdminMode(uuid);
                PlayerSettings.setAdminMode(uuid, !current);
                player.openInventory(createSettingsMenu(player));
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            } else if (name.contains("自动下载资源包")) {
                if (ArmsorPlus.ResourcePackUrl.isEmpty()) {
                    player.sendMessage("§c资源包地址未配置，请在 config.yml 设置 ResourcePackUrl");
                } else {
                    player.setResourcePack(ArmsorPlus.ResourcePackUrl);
                    player.sendMessage("§a已发送资源包下载请求，请在弹出的界面中确认");
                }
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            }
            return;
        }

        // ---- 商店菜单 — 兑换魔法球 (用标题识别, 避免静态实例失效) ----
        if (event.getClickedInventory() == event.getView().getTopInventory()
                && event.getView().getTitle().equals("§e魔法球兑换商店")) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().isEmpty()) return;

            ItemStack shopClicked = event.getCurrentItem();
            if (shopClicked.hasItemMeta() && "§c返回".equals(shopClicked.getItemMeta().getDisplayName())) {
                player.openInventory(createMenu());
                return;
            }

            // 管理员模式可直接拿取
            if (PlayerSettings.isAdminMode(uuid) && player.hasPermission("ArmsorPlus.op")) {
                giveAdminItem(player, event.getCurrentItem());
                return;
            }
            handleBallPurchase(player, event.getCurrentItem());
            return;
        }

        // ---- 浏览菜单 — 管理员模式可拿取物品，普通模式显示配方 ----
        String invTitle = event.getView().getTitle();
        boolean isBrowseMenu = event.getClickedInventory() == armsList
                || event.getClickedInventory() == magicItemsList
                || invTitle.startsWith("§e高级附魔书列表")
                || invTitle.startsWith(ChatColor.GREEN + "食物")
                || invTitle.startsWith(ChatColor.DARK_PURPLE + "魔法武器")
                || invTitle.startsWith(ChatColor.DARK_PURPLE + "药品")
                || invTitle.startsWith(ChatColor.GREEN + "树苗商店")
                || invTitle.equals(ChatColor.DARK_GREEN + "护甲")
                || invTitle.equals(ChatColor.GRAY + "原材料")
                || invTitle.equals(ChatColor.DARK_PURPLE + "魔法物品")
                || event.getClickedInventory() == foodMenu
                || event.getClickedInventory() == medicineMenu
                || event.getClickedInventory() == saplingMenu;

        if (isBrowseMenu) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;
            String itemName = clicked.getItemMeta().getDisplayName();

            // 附魔菜单翻页导航
            if (invTitle.startsWith("§e高级附魔书列表")) {
                int page = playerEnchantPage.getOrDefault(uuid, 0);
                if (event.getSlot() == 39 && page > 0) {
                    player.openInventory(createEnchantmentListMenu(player, page - 1));
                    return;
                }
                if (event.getSlot() == 41 && page < ENCHANT_PAGES - 1) {
                    player.openInventory(createEnchantmentListMenu(player, page + 1));
                    return;
                }
            }

            // 武器菜单翻页导航
            if (invTitle.startsWith(ChatColor.DARK_PURPLE + "魔法武器") || event.getClickedInventory() == armsList) {
                int page = playerArmsPage.getOrDefault(uuid, 0);
                if (event.getSlot() == 39 && page > 0) {
                    player.openInventory(createArmsListMenu(player, page - 1));
                    return;
                }
                if (event.getSlot() == 41 && page < WEAPON_PAGES - 1) {
                    player.openInventory(createArmsListMenu(player, page + 1));
                    return;
                }
            }

            // 食物菜单翻页导航 (动态总页数)
            if (invTitle.startsWith(ChatColor.GREEN + "食物") || event.getClickedInventory() == foodMenu) {
                int page = playerFoodPage.getOrDefault(uuid, 0);
                int totalPages = Math.max(1, (int) Math.ceil(getAllFoodItems().size() / 21.0));
                if (event.getSlot() == 39 && page > 0) {
                    player.openInventory(createFoodMenu(player, page - 1));
                    return;
                }
                if (event.getSlot() == 41 && page < totalPages - 1) {
                    player.openInventory(createFoodMenu(player, page + 1));
                    return;
                }
            }

            // 树苗菜单翻页导航
            if (invTitle.startsWith(ChatColor.GREEN + "树苗商店") || event.getClickedInventory() == saplingMenu) {
                int page = playerSaplingPage.getOrDefault(uuid, 0);
                if (event.getSlot() == 39 && page > 0) {
                    player.openInventory(createSaplingMenu(player, page - 1));
                    return;
                }
                if (event.getSlot() == 41 && page < SAPLING_PAGES - 1) {
                    player.openInventory(createSaplingMenu(player, page + 1));
                    return;
                }
            }

            if (" ".equals(itemName)) return;

            // 返回主菜单按钮
            if ("§c返回".equals(itemName)) {
                player.openInventory(createMenu());
                return;
            }

            if (PlayerSettings.isAdminMode(uuid) && player.hasPermission("ArmsorPlus.op")) {
                giveAdminItem(player, clicked);
            } else {
                showRecipe(player, clicked);
            }
        }

        // ---- 合成配方展示菜单 ----
        if (invTitle.startsWith("§e合成配方:")) {
            event.setCancelled(true);
            ItemStack clickedRecipe = event.getCurrentItem();
            if (clickedRecipe == null || !clickedRecipe.hasItemMeta()) return;
            String clickedName = clickedRecipe.getItemMeta().getDisplayName();
            // 返回按钮
            if ("§c§l返回武器列表".equals(clickedName)) {
                player.openInventory(createArmsListMenu(player));
                return;
            }
            // 点击材料中可合成物品→递归显示该物品的配方
            if (RECIPES.containsKey(clickedName)) {
                showRecipe(player, clickedRecipe);
            }
            return;
        }
    }

    // ========================================================================
    // 内部方法
    // ========================================================================

    // 管理员模式: 复制物品给玩家
    private void giveAdminItem(Player player, ItemStack displayItem) {
        ItemStack copy = displayItem.clone();
        player.getInventory().addItem(copy);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
        player.sendMessage("§6[管理员模式] §e已获得 " + displayItem.getItemMeta().getDisplayName());
    }

    // 处理魔法球兑换
    private void handleBallPurchase(Player player, ItemStack shopItem) {
        if (!shopItem.hasItemMeta()) return;

        int ballLevel = ArmsorEnchant.getEnchantLevel(shopItem, MagicBallKey);
        if (ballLevel == 0) return;

        int cost = switch (ballLevel) {
            case 1 -> 30;
            case 2 -> 50;
            case 3 -> 70;
            case 4 -> 150;
            default -> 99999;
        };

        if (player.getLevel() < cost) {
            player.sendMessage("§c经验值不足！需要 §e" + cost + "级 §c经验");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        player.setLevel(player.getLevel() - cost);

        ItemStack ball = switch (ballLevel) {
            case 1 -> MagicBallCreateI(1);
            case 2 -> MagicBallCreateII(1);
            case 3 -> MagicBallCreateIII(1);
            case 4 -> MagicBallCreateIV(1);
            default -> null;
        };
        if (ball == null) return;

        player.getInventory().addItem(ball);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        player.spawnParticle(Particle.FIREWORK, player.getLocation(), 30, 0.5, 0.5, 0.5);
        player.sendMessage("§a成功兑换 §6" + ball.getItemMeta().getDisplayName() + "§a!");
    }

    // 为菜单添加玻璃板边框 (自动适配大小)
    private void addBorder(Inventory inv, Material material) {
        ItemStack border = MenuMark(1, material);
        ItemMeta meta = border.getItemMeta();
        meta.setDisplayName(" ");
        border.setItemMeta(meta);

        int size = inv.getSize();
        int rows = size / 9;
        // 顶行
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, border);
        }
        // 底行
        for (int i = size - 9; i < size; i++) {
            inv.setItem(i, border);
        }
        // 左右边框
        for (int i = 1; i < rows - 1; i++) {
            inv.setItem(i * 9, border);
            inv.setItem(i * 9 + 8, border);
        }
    }

    // 创建带名称和描述的展示物品
    private ItemStack createInfoItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    // 以45格菜单展示合成配方 (3×3工作台样式)
    private void showRecipe(Player player, ItemStack item) {
        String displayName = item.getItemMeta().getDisplayName();
        String[] recipe = RECIPES.get(displayName);

        String title = "§e合成配方: " + displayName;
        Inventory recipeView = Bukkit.createInventory(null, 45, title.length() > 32 ? title.substring(0, 32) : title);

        // 边框
        ItemStack border = new ItemStack(GRAY_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName(" ");
        border.setItemMeta(borderMeta);
        for (int i = 0; i < 45; i++) {
            recipeView.setItem(i, border.clone());
        }

        // 清除3×3合成格区域的边框
        int[] gridSlots = {10, 11, 12, 19, 20, 21, 28, 29, 30};
        for (int slot : gridSlots) {
            recipeView.setItem(slot, null);
        }

        if (recipe == null) {
            recipeView.setItem(22, createInfoItem(BARRIER, "§c暂无配方",
                    "§7该物品没有预定义的合成配方"));
        } else {
            // 填充3×3合成格
            for (int i = 0; i < 3 && i < recipe.length; i++) {
                String row = recipe[i];
                for (int j = 0; j < 3 && j < row.length(); j++) {
                    char c = row.charAt(j);
                    if (c == ' ') continue;
                    String chineseName = getMaterialChineseName(recipe, c);
                    ItemStack exactItem = getArmsItem(chineseName);
                    if (exactItem != null) {
                        recipeView.setItem(gridSlots[i * 3 + j], exactItem);
                    } else {
                        Material mat = getRecipeMaterial(recipe, c);
                        recipeView.setItem(gridSlots[i * 3 + j], new ItemStack(mat));
                    }
                }
            }

            // 合成方式指示 (工作台)
            recipeView.setItem(23, createInfoItem(CRAFTING_TABLE, "§e合成方式: 工作台",
                    "§7此合成配方使用工作台合成"));

            // 合成结果
            recipeView.setItem(25, item.clone());

            // 材料说明
            List<String> materialNotes = new ArrayList<>();
            for (int i = 3; i < recipe.length; i++) {
                materialNotes.add("§7" + recipe[i]);
            }
            recipeView.setItem(31, createInfoItem(PAPER, "§e合成材料",
                    materialNotes.toArray(new String[0])));
        }

        // 返回按钮 (回到武器列表)
        recipeView.setItem(40, createInfoItem(BARRIER, "§c§l返回武器列表", "§7点击返回魔法武器列表"));

        player.openInventory(recipeView);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
    }

    // 从配方材料说明中解析字母对应的材料
    private Material getRecipeMaterial(String[] recipe, char c) {
        for (int i = 3; i < recipe.length; i++) {
            String note = recipe[i];
            if (note.length() >= 2 && note.charAt(0) == c && note.charAt(1) == '=') {
                String matName = note.substring(2);
                return switch (matName) {
                    case "绿宝石" -> EMERALD;
                    case "木棍" -> STICK;
                    case "下届合金锭" -> NETHERITE_INGOT;
                    case "绿宝石块" -> EMERALD_BLOCK;
                    case "骷髅头颅" -> SKELETON_SKULL;
                    case "不死图腾" -> TOTEM_OF_UNDYING;
                    case "蓝冰" -> BLUE_ICE;
                    case "火焰弹" -> FIRE_CHARGE;
                    case "烈焰棒" -> BLAZE_ROD;
                    case "闪烁的西瓜片" -> GLISTERING_MELON_SLICE;
                    case "玻璃瓶" -> GLASS_BOTTLE;
                    case "地狱疣" -> NETHER_WART;
                    case "白色羊毛" -> WHITE_WOOL;
                    case "面包" -> BREAD;
                    case "红石" -> REDSTONE;
                    case "铁块" -> IRON_BLOCK;
                    case "钻石块" -> DIAMOND_BLOCK;
                    case "圆石" -> COBBLESTONE;
                    case "沙子" -> SAND;
                    case "盐" -> SUGAR;
                    case "任意熟肉(非腐肉)" -> COOKED_BEEF;
                    case "熟猪排" -> COOKED_PORKCHOP;
                    case "熟羊肉" -> COOKED_MUTTON;
                    case "甜浆果" -> SWEET_BERRIES;
                    case "小麦" -> WHEAT;
                    case "木桶" -> BARREL;
                    case "腐肉" -> ROTTEN_FLESH;
                    case "海晶碎片" -> PRISMARINE_SHARD;
                    case "下界合金剑" -> NETHERITE_SWORD;
                    case "幻翼膜" -> PHANTOM_MEMBRANE;
                    case "末影珍珠" -> ENDER_PEARL;
                    case "钻石剑" -> DIAMOND_SWORD;
                    case "金剑" -> GOLDEN_SWORD;
                    case "蜘蛛网" -> COBWEB;
                    case "TNT" -> TNT;
                    case "弓" -> BOW;
                    case "熟牛肉" -> COOKED_BEEF;
                    case "鸡蛋" -> EGG;
                    case "奶桶" -> MILK_BUCKET;
                    case "线" -> STRING;
                    case "可可豆" -> COCOA_BEANS;
                    case "干海带" -> DRIED_KELP;
                    case "熟鳕鱼" -> COOKED_COD;
                    case "碗" -> BOWL;
                    case "熟鸡肉" -> COOKED_CHICKEN;
                    case "金块" -> GOLD_BLOCK;
                    case "金苹果" -> GOLDEN_APPLE;
                    case "雪球" -> SNOWBALL;
                    case "烤马铃薯" -> BAKED_POTATO;
                    case "红染料" -> RED_DYE;
                    case "糖" -> SUGAR;
                    case "西红柿" -> RED_DYE;
                    case "奶酪" -> YELLOW_DYE;
                    case "卷心菜" -> GREEN_DYE;
                    case "钢锭" -> IRON_INGOT;
                    case "僵尸头颅" -> ZOMBIE_HEAD;
                    case "骨头" -> BONE;
                    case "鲑鱼" -> SALMON;
                    case "鳕鱼" -> COD;
                    case "骨块" -> BONE_BLOCK;
                    case "海晶沙粒" -> PRISMARINE_SHARD;
                    case "海绵" -> SPONGE;
                    case "海晶灯" -> SEA_LANTERN;
                    case "灵魂沙" -> SOUL_SAND;
                    case "鳞甲" -> TURTLE_SCUTE;
                    case "海洋之心" -> HEART_OF_THE_SEA;
                    case "灵魂土" -> SOUL_SOIL;
                    case "凋零骷髅头" -> WITHER_SKELETON_SKULL;
                    case "潮涌核心" -> CONDUIT;
                    case "恶魂之泪" -> GHAST_TEAR;
                    case "下界之星" -> NETHER_STAR;
                    case "鱼骨剑" -> BONE;
                    case "鱼骨刀" -> BONE;
                    case "鱼刺剑" -> BONE_BLOCK;
                    case "鱼刺刀" -> BONE_BLOCK;
                    case "海骨剑" -> SPONGE;
                    case "海骨刀" -> SPONGE;
                    case "灵骨剑" -> SOUL_SAND;
                    case "灵骨刀" -> SOUL_SAND;
                    case "海刺剑" -> TURTLE_SCUTE;
                    case "海刺刀" -> TURTLE_SCUTE;
                    case "灵刺剑" -> CONDUIT;
                    case "灵刺刀" -> CONDUIT;
                    default -> PAPER;
                };
            }
        }
        return PAPER;
    }

    // 从配方材料说明中解析字母对应的中文名
    private String getMaterialChineseName(String[] recipe, char c) {
        for (int i = 3; i < recipe.length; i++) {
            String note = recipe[i];
            if (note.length() >= 2 && note.charAt(0) == c && note.charAt(1) == '=') {
                return note.substring(2);
            }
        }
        return "???";
    }

    // 根据中文名获取对应的插件武器ItemStack，没有则返回null
    private ItemStack getArmsItem(String chineseName) {
        return switch (chineseName) {
            case "鱼骨剑" -> FishBoneSword(1);
            case "鱼骨刀" -> FishBoneKnife(1);
            case "鱼刺剑" -> FishSpineSword(1);
            case "鱼刺刀" -> FishSpineKnife(1);
            case "海骨剑" -> SeaBoneSword(1);
            case "海骨刀" -> SeaBoneKnife(1);
            case "灵骨剑" -> SpiritBoneSword(1);
            case "灵骨刀" -> SpiritBoneKnife(1);
            case "海刺剑" -> SeaSpineSword(1);
            case "海刺刀" -> SeaSpineKnife(1);
            case "灵刺剑" -> SpiritSpineSword(1);
            case "灵刺刀" -> SpiritSpineKnife(1);
            case "尸王" -> CorpseKing(1);
            case "重剑" -> Iron_Epee(1);
            case "寒冰剑" -> IceSword(1);
            case "盘丝弓" -> WebBow(1);
            case "爆炸弓" -> ExplosionBow(1);
            case "雨御前" -> RainSword(1);
            case "飞天御剑" -> FlyingSword(1);
            case "瞬步刃" -> FlashStepBlade(1);
            case "血祭之剑" -> BloodSword(1);
            case "精炼金刚石" -> ArmsPlusCreateII(1);
            case "基础强化石" -> ArmsPlusCreateI(1);
            case "月之碎片" -> MoonShard(1);
            case "幻术师的遗骨" -> IllusionerBone(1);
            default -> null;
        };
    }
}
