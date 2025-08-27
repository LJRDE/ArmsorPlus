package Dim_LJR.armsorPlus;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
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

import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.ArmsorItem.ArmorPlusCreate;
import static Dim_LJR.armsorPlus.ArmsorItem.ArmorPlusCreateII;
import static Dim_LJR.armsorPlus.ArmsorItem.ArrowSpeed_EnchantdeBook;
import static Dim_LJR.armsorPlus.ArmsorItem.Blocking_EnchantedBook;
import static Dim_LJR.armsorPlus.ArmsorItem.BloodSacrifice_EnchantdeBook;
import static Dim_LJR.armsorPlus.ArmsorItem.BloodSword;
import static Dim_LJR.armsorPlus.ArmsorItem.BowPlusCreate;
import static Dim_LJR.armsorPlus.ArmsorItem.Dodge_EnchantdeBook;
import static Dim_LJR.armsorPlus.ArmsorItem.DoubleHit_EnchantdeBook;
import static Dim_LJR.armsorPlus.ArmsorItem.EffectClear_EnchantdeBook;
import static Dim_LJR.armsorPlus.ArmsorItem.ExplosiveArrow_EnchantedBook;
import static Dim_LJR.armsorPlus.ArmsorItem.Famine_EnchantdeBook;
import static Dim_LJR.armsorPlus.ArmsorItem.Freeze_EnchantedBook;
import static Dim_LJR.armsorPlus.ArmsorItem.HealthBoost_EnchantedBook;
import static Dim_LJR.armsorPlus.ArmsorItem.MagicBallCreateI;
import static Dim_LJR.armsorPlus.ArmsorItem.MagicBallCreateII;
import static Dim_LJR.armsorPlus.ArmsorItem.MagicBallCreateIII;
import static Dim_LJR.armsorPlus.ArmsorItem.MagicBallCreateIV;
import static Dim_LJR.armsorPlus.ArmsorItem.Revenge_EnchantedBook;
import static Dim_LJR.armsorPlus.ArmsorItem.Ripples_EnchantdeBook;
import static Dim_LJR.armsorPlus.ArmsorItem.ShadowDodge_EnchantdeBook;
import static Dim_LJR.armsorPlus.ArmsorItem.Sniping_EnchantdeBook;
import static Dim_LJR.armsorPlus.ArmsorItem.Survivor_EnchantedBook;
import static Dim_LJR.armsorPlus.ArmsorItem.Withering_EnchantedBook;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.Feedingkey;
import static Dim_LJR.armsorPlus.OpenSea.LoadOpenSea.world;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.GuideBookKey;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.MagicBallKey;
import static org.bukkit.Material.*;

public class ArmsorPlusMenu implements Listener {
    private static Inventory menu,shop,enchantmentlist,armslist,magicitemslist;
    public Inventory createMagicItemMenu() {
        magicitemslist = Bukkit.createInventory(null, 45, ChatColor.DARK_PURPLE + "魔法物品");
        addBorder(magicitemslist,PURPLE_STAINED_GLASS_PANE);
        magicitemslist.setItem(10,BasicStone(1));
        magicitemslist.setItem(11,ArmsPlusCreateI(1));
        magicitemslist.setItem(12,ArmsPlusCreateII(1));
        magicitemslist.setItem(13,ArmorPlusCreate(1));
        magicitemslist.setItem(14,ArmorPlusCreateII(1));
        magicitemslist.setItem(15,BowPlusCreate(1));
        return magicitemslist;
    }
    public Inventory createArmsListMenu() {
        magicitemslist = Bukkit.createInventory(null, 45, ChatColor.DARK_PURPLE + "魔法武器");
        addBorder(magicitemslist,PURPLE_STAINED_GLASS_PANE);
        magicitemslist.setItem(10,BloodSword(1));
        magicitemslist.setItem(11,Iron_Epee(1));
        return magicitemslist;
    }
    public Inventory createShopMenu() {
        shop = Bukkit.createInventory(null, 45, "§e魔法球兑换商店");
        addBorder(shop, YELLOW_STAINED_GLASS_PANE);
        shop.setItem(10, MagicBallCreateI(1));
        shop.setItem(11, MagicBallCreateII(1));
        shop.setItem(12, MagicBallCreateIII(1));
        shop.setItem(13, MagicBallCreateIV(1));
        shop.setItem(40, createInfoItem(
                EXPERIENCE_BOTTLE,
                "§a兑换说明",
                "§7使用经验值兑换魔法球",
                "§7不同等级魔法球需要不同经验值",
                "§7点击商品进行兑换"
        ));
        return shop;
    }
    public Inventory createenchantmentlisetmenu() {
        enchantmentlist = Bukkit.createInventory(null, 45, "§e高级附魔书列表");
        addBorder(enchantmentlist, GRAY_STAINED_GLASS_PANE);
        enchantmentlist.setItem(10,Dodge_EnchantdeBook(1,4));
        enchantmentlist.setItem(11,Famine_EnchantdeBook(1,3));
        enchantmentlist.setItem(12,BloodSacrifice_EnchantdeBook(1,3));
        enchantmentlist.setItem(13,Ripples_EnchantdeBook(1,3));
        enchantmentlist.setItem(14,EffectClear_EnchantdeBook(1,3));
        enchantmentlist.setItem(15,Freeze_EnchantedBook(1,3));
        enchantmentlist.setItem(16,Survivor_EnchantedBook(1,5));
        enchantmentlist.setItem(19,Withering_EnchantedBook(1,5));
        enchantmentlist.setItem(20,Blocking_EnchantedBook(1,5));
        enchantmentlist.setItem(21,Revenge_EnchantedBook(1,3));
        enchantmentlist.setItem(22,HealthBoost_EnchantedBook(1,4));
        enchantmentlist.setItem(23,ExplosiveArrow_EnchantedBook(1,3));
        enchantmentlist.setItem(24,ShadowDodge_EnchantdeBook(1,4));
        enchantmentlist.setItem(25,ArrowSpeed_EnchantdeBook(1,5));
        enchantmentlist.setItem(28,Sniping_EnchantdeBook(1,5));
        enchantmentlist.setItem(29,DoubleHit_EnchantdeBook(1,5));
        enchantmentlist.setItem(30,Feeding_EnchantdeBook(1,5));

        return enchantmentlist;
    }
    public Inventory createMenu() {
        menu = Bukkit.createInventory(null, 45,  ChatColor.DARK_PURPLE + "ArmsorPlus插件菜单");
        addBorder(menu,PURPLE_STAINED_GLASS_PANE);
        menu.setItem(11,createInfoItem(FIREWORK_STAR,ChatColor.DARK_PURPLE + "兑换魔法球",ChatColor.GOLD + "点击进入兑换窗口"));
        menu.setItem(10,createInfoItem(IRON_SWORD,ChatColor.GOLD + "魔法武器列表",ChatColor.GOLD + "点击查看"));
        menu.setItem(12,createInfoItem(BOOK,ChatColor.GOLD + "高级附魔书列表",ChatColor.GOLD + "点击查看"));
        menu.setItem(13,createInfoItem(DIAMOND,ChatColor.AQUA + "魔法物品列表",ChatColor.AQUA + "点击查看"));
        menu.setItem(14,createInfoItem(GRASS_BLOCK,ChatColor.BLUE + "公海世界",ChatColor.BLUE + "点击传送"));
        return menu;
    }
    @EventHandler
    public void onBookRightClick(PlayerInteractEvent event) //向导书监听器
    {
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item,GuideBookKey)==0) return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        player.sendMessage("正在打开菜单");
        player.openInventory(createMenu());
    }
    @EventHandler
    public void onShopClick(InventoryClickEvent event) // 商店菜单点击事件
    {
        if(event.getClickedInventory() == shop ||
                event.getClickedInventory() == enchantmentlist ||
                event.getClickedInventory() == menu||
                event.getClickedInventory() == armslist||
                event.getClickedInventory() == magicitemslist)
        {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            // 处理魔法球兑换
            if(clickedItem==null)
                return;
            if(event.getClickedInventory() == shop)
                handleBallPurchase(player, clickedItem);
            if(event.getClickedInventory() == menu)
            {
                if(clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "高级附魔书列表"))
                    player.openInventory(createenchantmentlisetmenu());
                if(clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.DARK_PURPLE + "兑换魔法球"))
                    player.openInventory(createShopMenu());
                if(clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "魔法物品列表"))
                    player.openInventory(createMagicItemMenu());
                if(clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "魔法武器列表"))
                    player.openInventory(createArmsListMenu());
                if(clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName().equals(ChatColor.BLUE + "公海世界"))
                {
                    player.teleport(world.getSpawnLocation());
                    player.sendActionBar(Component.text().content("正在传送..."));
                }
            }
        }
    }
    /*
     @EventHandler
    public void IfUSEMagicBallEvent(PlayerInteractEvent event)//0.2-c+重写魔法球抽奖
    {
        if(event.getAction().isLeftClick()) return;

        if(event.getItem() == null) return;
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        // 获取魔法球等级
        int Amount = ArmsorEnchant.getEnchantLevel(item, MagicBallKey);
        if(Amount == 0) return;
        // 消耗魔法球
        item.setAmount(item.getAmount() - 1);
        // 创建异步任务池
        List<CompletableFuture<ItemStack>> futures = new ArrayList<>();
        // 添加所有可能的附魔书到任务池
        for(int i = 0; i < Amount; i++) {
            CompletableFuture<ItemStack> future = CompletableFuture.supplyAsync(() -> {
                Random r = ThreadLocalRandom.current();
                int level_m3 = r.nextInt(3) + 1; // 1-3级
                int level_m4 = r.nextInt(4) + 1; // 2-4级
                int level_m5 = r.nextInt(5) + 1; // 3-5级
                // 基于概率生成不同附魔书
                double roll = r.nextDouble();
                if(roll < 0.10) return Famine_EnchantdeBook(1, level_m3);
                else if(roll < 0.20) return Dodge_EnchantdeBook(1, level_m4);
                else if(roll < 0.30) return Ripples_EnchantdeBook(1, level_m3);
                else if(roll < 0.40) return BloodSacrifice_EnchantdeBook(1, level_m3);
                else if(roll < 0.50) return EffectClear_EnchantdeBook(1, 1);
                else if(roll < 0.60) return Freeze_EnchantedBook(1, level_m3);
                else if(roll < 0.70) return Blocking_EnchantedBook(1, level_m5);
                else if(roll < 0.80) return Withering_EnchantedBook(1, level_m5);
                else if(roll < 0.90) return Survivor_EnchantedBook(1, level_m5);
                else return null; // 10%概率无收获
            }, threadPool);
            futures.add(future);
        }
        // 处理结果并给玩家发放奖励
        Map<String, Integer> rewards = new LinkedHashMap<>();
        player.sendMessage(ChatColor.GOLD + "魔法球施展中，请稍候...");
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                // 在主线程安全地添加物品
                Bukkit.getScheduler().runTask(this, () -> {
                    for(CompletableFuture<ItemStack> future : futures) {
                        try {
                            ItemStack book = future.get();
                            if(book == null) continue;
                            String bookName = ChatColor.stripColor(
                                    Objects.requireNonNull(book.getItemMeta()).getDisplayName()
                            );
                            // 统计和添加物品
                            rewards.put(bookName, rewards.getOrDefault(bookName, 0) + 1);
                            player.getInventory().addItem(book);
                        } catch (Exception e) {
                            // 错误处理
                            player.sendMessage(ChatColor.RED + "获取魔法奖励时发生错误: " + e.getMessage());
                            this.getLogger().log(Level.SEVERE, "魔法球奖励发放错误", e);
                        }
                    }
                    // 显示获取结果
                    if(rewards.isEmpty()) {
                        player.sendMessage(ChatColor.GRAY + "这次魔法球施展没有产生附魔书");
                    } else {
                        StringBuilder msg = new StringBuilder(ChatColor.GOLD + "你获得了: ");
                        for(Map.Entry<String, Integer> entry : rewards.entrySet()) {
                            msg.append(ChatColor.GREEN).append(entry.getKey())
                                    .append(ChatColor.WHITE).append("×").append(entry.getValue())
                                    .append(ChatColor.GRAY).append(", ");
                        }
                        player.sendMessage(msg.substring(0, msg.length() - 2));
                    }
                });
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "魔法施展失败！请联系管理员");
                this.getLogger().log(Level.SEVERE, "魔法球处理错误", e);
            }
        });
    }
    @EventHandler//当使用鼠标时(魔法球监听器)
    public void IfUSEMagicBallEvent(PlayerInteractEvent event)//魔法球监听器--0.2-b+重写
    {
        if(event.getAction().isLeftClick())
            return;//左键不生效
        Random r = new Random(System.currentTimeMillis());
        int level_m4 = r.nextInt(5);
        int level_m3 = r.nextInt(4);
        int Amount = 0;
        if(event.getItem() == null) return;
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Amount = ArmsorEnchant.getEnchantLevel(item,MagicBallKey);
        if(Amount==0)
            return;
        int c=0;
        item.setAmount(item.getAmount() - 1);
        while (Amount >= 0) {
            event.setCancelled(true);
            if (percent(10)) {
                player.getInventory().addItem(Famine_EnchantdeBook(1,level_m3));
                player.sendMessage(ChatColor.GREEN + "获得饥荒附魔书");
                Amount--;c++;
            } else if (percent(10)) {
                player.getInventory().addItem(Dodge_EnchantdeBook(1,level_m4));
                player.sendMessage(ChatColor.GOLD + "获得闪避附魔书");
                Amount--;c++;
            } else if (percent(10)) {
                player.getInventory().addItem(Ripples_EnchantdeBook(1,level_m3));
                player.sendMessage(ChatColor.BLUE + "获得涟漪附魔书");
                Amount--;c++;
            } else if (percent(10)) {
                player.getInventory().addItem(BloodSacrifice_EnchantdeBook(1,level_m3));
                player.sendMessage(ChatColor.DARK_RED + "获得血祭附魔书");
                Amount--;c++;
            } else if (percent(10)) {
                player.getInventory().addItem(EffectClear_EnchantdeBook(1,1));
                player.sendMessage(ChatColor.WHITE + "获得涤魂附魔书");
                Amount--;c++;
            } else if (percent(10)) {
                player.getInventory().addItem(Freeze_EnchantedBook(1,level_m3));
                player.sendMessage(ChatColor.AQUA + "获得寒冻附魔书");
                Amount--;c++;
            }
        }
        player.sendMessage("获得数量:" + c);
    }

    *///0.2-b老版本魔法球抽奖系统
    private void handleBallPurchase(Player player, ItemStack shopItem) //魔法球兑换界面
    {
        if(shopItem.isEmpty())
            return;
        if(!shopItem.hasItemMeta())
            return;
        // 获取魔法球等级
        ItemMeta meta = shopItem.getItemMeta();
        int ballLevel = ArmsorEnchant.getEnchantLevel(shopItem,MagicBallKey);
        if (ballLevel==0) return;
        // 确定兑换价格
        int cost = switch (ballLevel) {
            case 1 -> 30;
            case 2 -> 50;
            case 3 -> 70;
            case 4 -> 150;
            default -> 99999;
        };
        // 检查玩家经验值
        if (player.getLevel() < cost) {
            player.sendMessage("§c经验值不足！需要 §e" + cost + "级 §c经验");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        // 扣除经验值
        player.setLevel(player.getLevel() - cost);
        // 给予魔法球
        ItemStack magicBall;
        if(ballLevel==1) magicBall = MagicBallCreateI(1);
        else if(ballLevel==2) magicBall = MagicBallCreateII(1);
        else if(ballLevel==3) magicBall = MagicBallCreateIII(1);
        else if(ballLevel==4) magicBall = MagicBallCreateIV(1);
        else return;
        player.getInventory().addItem(magicBall);
        // 播放效果
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 30, 0.5, 0.5, 0.5);
        player.sendMessage("§a成功兑换 §6" + magicBall.getItemMeta().getDisplayName() + "§a!");
    }
    private void addBorder(Inventory inv, Material material) {
        ItemStack border = new ItemStack(material);
        ItemMeta meta = border.getItemMeta();
        meta.setDisplayName(" ");
        border.setItemMeta(meta);
        // 顶部和底部边框
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, border);
            inv.setItem(i + 36, border);
        }
        // 两侧边框
        for (int i = 0; i < 36; i += 9) {
            inv.setItem(i, border);
            inv.setItem(i + 8, border);
        }
    }
    private void addScreen(Inventory inv, Material material) {
        ItemStack border = new ItemStack(material);
        ItemMeta meta = border.getItemMeta();
        meta.setDisplayName(" ");
        border.setItemMeta(meta);
        for (int i = 0; i < 45; i++) {
            inv.setItem(i, border);
        }
    }
    private ItemStack createInfoItem(Material material, String name, String... info) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(info));
        item.setItemMeta(meta);
        return item;
    }
}
