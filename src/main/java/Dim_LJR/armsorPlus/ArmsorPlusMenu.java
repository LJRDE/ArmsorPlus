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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
        return armsList;
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

    /** 高级附魔书列表 */
    public Inventory createEnchantmentListMenu() {
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
        return enchantmentList;
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
        menu.setItem(19, createInfoItem(COMPARATOR, ChatColor.GRAY + "设置",
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
                player.openInventory(createEnchantmentListMenu());
            } else if (name.equals(ChatColor.DARK_PURPLE + "兑换魔法球")) {
                player.openInventory(createShopMenu());
            } else if (name.equals(ChatColor.AQUA + "魔法物品列表")) {
                player.openInventory(createMagicItemMenu());
            } else if (name.equals(ChatColor.GOLD + "魔法武器列表")) {
                player.openInventory(createArmsListMenu());
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

        // ---- 浏览菜单 — 管理员模式可拿取物品 ----
        if (event.getClickedInventory() == armsList
                || event.getClickedInventory() == magicItemsList
                || event.getClickedInventory() == enchantmentList) {

            if (PlayerSettings.isAdminMode(uuid) && player.hasPermission("ArmsorPlus.op")) {
                ItemStack clicked = event.getCurrentItem();
                if (clicked != null && !clicked.isEmpty()
                        && clicked.hasItemMeta() && !" ".equals(clicked.getItemMeta().getDisplayName())) {
                    giveAdminItem(player, clicked);
                }
            }
            event.setCancelled(true);
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
}
