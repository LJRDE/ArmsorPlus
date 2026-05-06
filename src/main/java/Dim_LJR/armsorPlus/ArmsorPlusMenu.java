package Dim_LJR.armsorPlus;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import Dim_LJR.armsorPlus.Boss.BossMenu;
import Dim_LJR.armsorPlus.Boss.BossWorld;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant.addEnchantLore;

import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.GuideBookKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.MagicBallKey;
import static Dim_LJR.armsorPlus.OpenSea.LoadOpenSea.world;
import static org.bukkit.Material.*;

/**
 * 插件 GUI 菜单系统。
 * <p>
 * 包括: 主菜单、设置、魔法球兑换商店、附魔书列表、魔法武器列表、魔法物品列表。
 * 通过右键"高级附魔向导书"打开主菜单。
 */
public class ArmsorPlusMenu implements Listener {

    private static Inventory menu;
    private static Inventory shop;
    private static Inventory enchantmentList;
    private static Inventory armsList;
    private static Inventory magicItemsList;
    private static Inventory foodMenu;

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
        RECIPES.put(ChatColor.DARK_AQUA + "雨御前", new String[]{
                " N ", "NBN", " N ",
                "N=海晶碎片", "B=下界合金剑"});
        RECIPES.put(ChatColor.GOLD + "飞天御剑", new String[]{
                " N ", "NEN", " N ",
                "N=幻翼膜", "E=下界合金剑"});
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
        RECIPES.put(ChatColor.GOLD + "高级附魔向导", new String[]{
                " C ", "   ", "   ",
                "C=圆石"});
    }

    // ========================================================================
    // 菜单创建
    // ========================================================================

    /** 魔法物品列表 (强化石等) */
    public Inventory createMagicItemMenu() {
        magicItemsList = Bukkit.createInventory(null, 45, ChatColor.DARK_PURPLE + "魔法物品");
        addBorder(magicItemsList, PURPLE_STAINED_GLASS_PANE);
        magicItemsList.setItem(10, BasicStone(1));
        magicItemsList.setItem(11, ArmsPlusCreateI(1));
        magicItemsList.setItem(12, ArmsPlusCreateII(1));
        magicItemsList.setItem(13, ArmorPlusCreate(1));
        magicItemsList.setItem(14, ArmorPlusCreateII(1));
        magicItemsList.setItem(15, BowPlusCreate(1));
        return magicItemsList;
    }

    /** 魔法武器列表 */
    public Inventory createArmsListMenu() {
        armsList = Bukkit.createInventory(null, 45, ChatColor.DARK_PURPLE + "魔法武器");
        addBorder(armsList, PURPLE_STAINED_GLASS_PANE);
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
        return armsList;
    }

    /** 食物/药品菜单 */
    public Inventory createFoodMenu() {
        foodMenu = Bukkit.createInventory(null, 45, ChatColor.GREEN + "食物/药品");
        addBorder(foodMenu, GREEN_STAINED_GLASS_PANE);
        foodMenu.setItem(10, RejuvenationPowder(1));
        foodMenu.setItem(11, HemostaticBandage(1));
        foodMenu.setItem(12, CompressedBiscuit(1));
        foodMenu.setItem(13, Jerky(1));
        foodMenu.setItem(14, SweetBerryPie(1));
        foodMenu.setItem(15, RottenJerky(1));
        foodMenu.setItem(16, WineBarrel(1));
        foodMenu.setItem(19, Salt(1));
        return foodMenu;
    }

    /** 魔法球兑换商店 */
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
        return shop;
    }

    /** 高级附魔书列表 (可传入Player用于管理员模式判断) */
    public Inventory createEnchantmentListMenu(Player player) {
        enchantmentList = Bukkit.createInventory(null, 45, "§e高级附魔书列表");
        addBorder(enchantmentList, GRAY_STAINED_GLASS_PANE);
        enchantmentList.setItem(10, Dodge_EnchantdeBook(1, 4));
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
        enchantmentList.setItem(24, ShadowDodge_EnchantdeBook(1, 4));
        enchantmentList.setItem(25, ArrowSpeed_EnchantdeBook(1, 5));
        enchantmentList.setItem(28, Sniping_EnchantdeBook(1, 5));
        enchantmentList.setItem(29, DoubleHit_EnchantdeBook(1, 5));
        enchantmentList.setItem(30, Feeding_EnchantdeBook(1, 5));
        enchantmentList.setItem(31, DiamondDrill_EnchantedBook(1, 5));
        enchantmentList.setItem(32, QuickThrust_EnchantedBook(1, 5));
        enchantmentList.setItem(33, Blindness_EnchantedBook(1, 5));
        if (player != null && PlayerSettings.isAdminMode(player.getUniqueId())
                && player.hasPermission("ArmsorPlus.op")) {
            enchantmentList.setItem(34, Indestructible_EnchantedBook(1, 1));
        }
        return enchantmentList;
    }

    public Inventory createEnchantmentListMenu() {
        return createEnchantmentListMenu(null);
    }

    /** 插件主菜单 */
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
        menu.setItem(19, createInfoItem(BREAD, ChatColor.GREEN + "食物/药品",
                ChatColor.GREEN + "点击查看食物与药品"));
        menu.setItem(1, createInfoItem(COMPARATOR, ChatColor.GRAY + "设置",
                ChatColor.GRAY + "点击打开个人设置"));
        return menu;
    }

    /** 个人设置菜单 (按玩家状态动态生成) */
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

        return settings;
    }

    // ========================================================================
    // 事件监听
    // ========================================================================

    /** 右键向导书打开主菜单 */
    @EventHandler
    public void onBookRightClick(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, GuideBookKey) == 0) return;
        event.setCancelled(true);
        event.getPlayer().openInventory(createMenu());
    }

    /** 菜单点击事件处理 */
    @EventHandler
    public void onShopClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();

        // ---- 主菜单导航 ----
        if (event.getClickedInventory() == menu) {
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
                player.openInventory(createArmsListMenu());
            } else if (name.equals(ChatColor.GREEN + "食物/药品")) {
                player.openInventory(createFoodMenu());
            } else if (name.equals(ChatColor.BLUE + "公海世界")) {
                player.teleport(world.getSpawnLocation());
                player.sendActionBar(Component.text("正在传送..."));
            } else if (name.equals(ChatColor.RED + "BOSS清单")) {
                player.openInventory(BossMenu.getBossList());
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
            }
            return;
        }

        // ---- 商店菜单 — 兑换魔法球 ----
        if (event.getClickedInventory() == shop) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().isEmpty()) return;

            // 管理员模式可直接拿取
            if (PlayerSettings.isAdminMode(uuid) && player.hasPermission("ArmsorPlus.op")) {
                giveAdminItem(player, event.getCurrentItem());
                return;
            }
            handleBallPurchase(player, event.getCurrentItem());
            return;
        }

        // ---- 浏览菜单 — 管理员模式可拿取物品，普通模式显示配方 ----
        boolean isBrowseMenu = event.getClickedInventory() == armsList
                || event.getClickedInventory() == magicItemsList
                || event.getClickedInventory() == enchantmentList
                || event.getClickedInventory() == foodMenu;

        if (isBrowseMenu) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;
            String itemName = clicked.getItemMeta().getDisplayName();
            if (" ".equals(itemName)) return;

            if (PlayerSettings.isAdminMode(uuid) && player.hasPermission("ArmsorPlus.op")) {
                giveAdminItem(player, clicked);
            } else {
                showRecipe(player, clicked);
            }
        }
    }

    // ========================================================================
    // 内部方法
    // ========================================================================

    /** 管理员模式: 复制物品给玩家 */
    private void giveAdminItem(Player player, ItemStack displayItem) {
        ItemStack copy = displayItem.clone();
        player.getInventory().addItem(copy);
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
        player.sendMessage("§6[管理员模式] §e已获得 " + displayItem.getItemMeta().getDisplayName());
    }

    /** 处理魔法球兑换 */
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

    /** 为菜单添加玻璃板边框 (自动适配大小) */
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

    /** 创建带名称和描述的展示物品 */
    private ItemStack createInfoItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    /** 以45格菜单展示合成配方 (3×3工作台样式) */
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
                    Material mat = getRecipeMaterial(recipe, c);
                    String chineseName = getMaterialChineseName(recipe, c);
                    recipeView.setItem(gridSlots[i * 3 + j], createInfoItem(mat,
                            "§f" + chineseName,
                            "§c" + c));
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

        player.openInventory(recipeView);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
    }

    /** 从配方材料说明中解析字母对应的材料 */
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
                    case "甜浆果" -> SWEET_BERRIES;
                    case "小麦" -> WHEAT;
                    case "木桶" -> BARREL;
                    case "腐肉" -> ROTTEN_FLESH;
                    case "海晶碎片" -> PRISMARINE_SHARD;
                    case "下界合金剑" -> NETHERITE_SWORD;
                    case "幻翼膜" -> PHANTOM_MEMBRANE;
                    case "末影珍珠" -> ENDER_PEARL;
                    default -> PAPER;
                };
            }
        }
        return PAPER;
    }

    /** 从配方材料说明中解析字母对应的中文名 */
    private String getMaterialChineseName(String[] recipe, char c) {
        for (int i = 3; i < recipe.length; i++) {
            String note = recipe[i];
            if (note.length() >= 2 && note.charAt(0) == c && note.charAt(1) == '=') {
                return note.substring(2);
            }
        }
        return "???";
    }
}
