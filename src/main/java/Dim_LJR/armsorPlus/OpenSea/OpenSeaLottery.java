package Dim_LJR.armsorPlus.OpenSea;

import Dim_LJR.armsorPlus.ArmsorItem;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorPlusEnchantEventHandler;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class OpenSeaLottery implements Listener {
    public boolean percent(int x)//百分率随机器
    {
        Random r =new Random();
        int rand = r.nextInt(101);
        if(x >= rand)
            return true;
        else return false;
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // 检查是否在公海世界右键点击了特定方块（例如宝箱）
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                event.getPlayer().getWorld().getName().equals("OpenSea") &&
                event.getClickedBlock().getType() == Material.CHEST)
        {
                event.getClickedBlock().setType(Material.AIR);
            Player player = event.getPlayer();

            // 打开公海抽奖GUI
            openLotteryGUI(player);

            // 取消事件（防止打开默认宝箱界面）
            event.setCancelled(true);
            //概率生成守护者
            if(percent(50))
                OpenSeaEntity.SpawnSkeleton(event.getPlayer().getLocation());
            //十分钟后填充宝箱
            BukkitTask task = Bukkit.getScheduler().runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ArmsorPlus")), () ->
            {
                event.getClickedBlock().setType(Material.CHEST);
            }, 1200);
        }
    }
    //防止物品被取出
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (ArmsorEnchant.getEnchantLevel(event.getClickedInventory().getItem(0),CHEST)==1) {
            // 取消所有其他操作
            event.setCancelled(true);
            // 给玩家提示
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();
                player.sendMessage("§c抽奖过程中不能取出物品！");
            }
        }
    }
    // GUI尺寸 (5行×9列)
    private static final int GUI_ROWS = 5;
    private static final int GUI_SIZE = GUI_ROWS * 9;
    private static final int CENTER_SLOT = 22; // 第3行第5列 (0-based: row 2, column 4)
    // 奖品池 - 20种原版物品
    private static final List<ItemStack> PRIZE_POOL = createPrizePool();
    // 玻璃板颜色
    private static final Material[] GLASS_COLORS = {
            Material.WHITE_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.MAGENTA_STAINED_GLASS_PANE,
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.LIME_STAINED_GLASS_PANE,
            Material.PINK_STAINED_GLASS_PANE,
            Material.GRAY_STAINED_GLASS_PANE,
            Material.LIGHT_GRAY_STAINED_GLASS_PANE,
            Material.CYAN_STAINED_GLASS_PANE,
            Material.PURPLE_STAINED_GLASS_PANE,
            Material.BLUE_STAINED_GLASS_PANE,
            Material.BROWN_STAINED_GLASS_PANE,
            Material.GREEN_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE,
            Material.BLACK_STAINED_GLASS_PANE
    };

    // 抽奖状态
    private final Map<Player, LotterySession> activeSessions = new HashMap<>();

    /**
     * 为玩家打开公海抽奖界面
     * @param player 玩家
     */
    public void openLotteryGUI(Player player) {
        // 如果玩家已经有活跃的抽奖会话，先关闭
        if (activeSessions.containsKey(player)) {
            activeSessions.get(player).cancel();
            activeSessions.remove(player);
        }

        // 创建抽奖GUI
        Inventory gui = Bukkit.createInventory(null, GUI_SIZE, "§6公海宝藏抽奖");

        // 填充随机颜色的玻璃板
        fillWithRandomGlass(gui);

        // 在中心位置放置初始奖品
        gui.setItem(CENTER_SLOT, getRandomPrize());

        // 打开GUI
        player.openInventory(gui);

        // 创建抽奖会话
        LotterySession session = new LotterySession(player, gui);
        session.start();
        activeSessions.put(player, session);
    }

    /**
     * 关闭玩家的抽奖界面
     * @param player 玩家
     */
    public void closeLotteryGUI(Player player) {
        if (activeSessions.containsKey(player)) {
            activeSessions.get(player).cancel();
            activeSessions.remove(player);
        }
        player.closeInventory();
    }

    /**
     * 填充GUI所有位置为随机颜色的玻璃板（中心除外）
     * @param gui 抽奖GUI
     */
    NamespacedKey CHEST = new NamespacedKey(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ArmsorPlus")),"ArmsorPlus_CHEST");
    private ItemStack yellowGlassPlane()
    {
        ItemStack item = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        item.getItemMeta().setDisplayName(" ");
        item.setAmount(1);
        ArmsorEnchant.addEnchant(item,CHEST,1);
        return item;
    }
    private void fillWithRandomGlass(Inventory gui) {
        for (int i = 0; i < GUI_SIZE; i++) {
            if (i != CENTER_SLOT && i != 0) {
                gui.setItem(i, createRandomGlass());
            }
        }
        gui.setItem(0,yellowGlassPlane());
    }
    /**
     * 创建随机颜色的玻璃板
     * @return 玻璃板物品
     */
    private ItemStack createRandomGlass() {
        Material glassType = GLASS_COLORS[new Random().nextInt(GLASS_COLORS.length)];
        ItemStack glass = new ItemStack(glassType, 1);

        ItemMeta meta = glass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§f");
            glass.setItemMeta(meta);
        }

        return glass;
    }
    /**
     * 获取随机奖品
     * @return 随机奖品物品
     */
    private ItemStack getRandomPrize() {
        return PRIZE_POOL.get(new Random().nextInt(PRIZE_POOL.size())).clone();
    }

    /**
     * 创建20种原版物品作为奖品池
     * @return 奖品列表
     */
    private static List<ItemStack> createPrizePool() {
        List<ItemStack> prizes = new ArrayList<>();

        //普通强化石
        prizes.add(ArmsorItem.BasicStone(1));
        //金锭
        prizes.add(createPrizeItem(Material.GOLD_INGOT, "§6金锭", "§7幸运的收获",16));
        //绿宝石
        prizes.add(createPrizeItem(Material.EMERALD, "§a绿宝石", "§7来自深海的珍宝",5));
        //铁锭
        prizes.add(createPrizeItem(Material.IRON_INGOT, "§f铁锭", "§7实用的金属",12));
        //普通魔法球
        prizes.add(ArmsorItem.MagicBallCreateI(1));
        //金刚石
        prizes.add(ArmsorItem.DIAMONDPLUSCreate(1));
        //骷髅头颅
        prizes.add(createPrizeItem(Material.SKELETON_SKULL,ChatColor.WHITE + "骷髅头颅",ChatColor.RED + "血祭的原料",1));
        //青金石
        prizes.add(createPrizeItem(Material.LAPIS_LAZULI, "§9青金石", "§7染料的原料",32));
        //下界石英
        prizes.add(createPrizeItem(Material.QUARTZ, "§f下界石英", "§7来自地狱的矿物",64));
        //萤石粉
        prizes.add(createPrizeItem(Material.GLOWSTONE_DUST, "§e萤石粉", "§7发光的神秘粉末",24));
        //烈焰棒
        prizes.add(createPrizeItem(Material.BLAZE_ROD, "§6烈焰棒", "§c来自下界要塞的战利品",4));
        //恶魂之泪
        prizes.add(createPrizeItem(Material.GHAST_TEAR, "§d恶魂之泪", "§7悲伤的结晶",3));
        //蜘蛛眼
        prizes.add(createPrizeItem(Material.SPIDER_EYE, "§8蜘蛛眼", "§7剧毒之物",2));
        //骨头
        prizes.add(createPrizeItem(Material.BONE, "§f骨头", "§7骷髅的遗骸",5));
        //粘液球
        prizes.add(createPrizeItem(Material.SLIME_BALL, "§a粘液球", "§7弹跳的绿色物质",27));
        return prizes;
    }

    /**
     * 创建奖品物品
     * @param material 物品材质
     * @param name 显示名称
     * @param lore 描述
     * @return 奖品物品
     */
    private static ItemStack createPrizeItem(Material material, String name, String lore,int Amount) {
        ItemStack item = new ItemStack(material, Amount);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Collections.singletonList(lore));
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 抽奖会话类
     */
    private class LotterySession {
        private final Player player;
        private final Inventory gui;
        private BukkitTask animationTask;
        private int animationStep = 0;
        private final int maxSteps;
        private ItemStack finalPrize;

        public LotterySession(Player player, Inventory gui) {
            this.player = player;
            this.gui = gui;
            this.maxSteps = new Random().nextInt(6) + 10; // 10-15次变化
        }

        /**
         * 开始抽奖动画
         */
        public void start() {
            // 播放开始音效
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

            // 开始动画任务
            animationTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (animationStep >= maxSteps) {
                        // 动画结束，确定最终奖品
                        finishLottery();
                        cancel();
                        return;
                    }

                    // 更新中心奖品
                    gui.setItem(CENTER_SLOT, getRandomPrize());

                    // 更新周围玻璃颜色
                    updateGlassColors();

                    // 播放音效
                    float pitch = 0.5f + (animationStep * 0.05f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, pitch);

                    animationStep++;
                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugins()[0], 0L, 4L); // 每0.2秒更新一次
        }

        /**
         * 更新周围玻璃颜色
         */
        private void updateGlassColors() {
            // 随机更新部分玻璃颜色
            int changeCount = new Random().nextInt(10) + 5; // 每次更新5-15个玻璃

            for (int i = 0; i < changeCount; i++) {
                int slot = new Random().nextInt(GUI_SIZE);
                // 跳过中心位置
                if (slot == CENTER_SLOT || slot==0) continue;

                gui.setItem(slot, createRandomGlass());
            }
        }

        /**
         * 完成抽奖
         */
        private void finishLottery() {
            // 确定最终奖品
            finalPrize = getRandomPrize();
            gui.setItem(CENTER_SLOT, finalPrize);

            // 播放完成音效
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

            // 给予玩家奖品
            givePrizeToPlayer();

            // 关闭GUI（延迟1秒）
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.closeInventory();
                    activeSessions.remove(player);
                }
            }.runTaskLater(Bukkit.getPluginManager().getPlugins()[0], 20L); // 1秒后关闭
        }

        /**
         * 给予玩家奖品
         */
        private void givePrizeToPlayer() {
            // 复制奖品（避免修改原始奖品）
            ItemStack prize = finalPrize.clone();

            // 给予玩家奖品
            Map<Integer, ItemStack> leftOver = player.getInventory().addItem(prize);

            if (!leftOver.isEmpty()) {
                // 如果背包满了，掉落在玩家位置
                for (ItemStack leftoverItem : leftOver.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), leftoverItem);
                }
                player.sendMessage(ChatColor.YELLOW + "你的背包已满，奖品掉落在地上！");
            }

            // 显示获得信息
            String prizeName = prize.hasItemMeta() && prize.getItemMeta().hasDisplayName() ?
                    prize.getItemMeta().getDisplayName() : prize.getType().name();

            player.sendMessage(ChatColor.GREEN + "恭喜你获得了: " + prizeName);
        }

        /**
         * 取消抽奖
         */
        public void cancel() {
            if (animationTask != null) {
                animationTask.cancel();
            }
            activeSessions.remove(player);
        }
    }
}