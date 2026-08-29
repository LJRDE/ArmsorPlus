package Dim_LJR.armsorPlus;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorPlusEnchantEventHandler;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.EnhancementHandler;
import Dim_LJR.armsorPlus.Boss.BossMenu;
import Dim_LJR.armsorPlus.Boss.CreatureMenu;
import Dim_LJR.armsorPlus.Boss.BossRenderer;
import Dim_LJR.armsorPlus.Boss.BossSkin;
import Dim_LJR.armsorPlus.Boss.BossWorld;
import Dim_LJR.armsorPlus.Boss.Enmity;
import Dim_LJR.armsorPlus.Boss.EnmityToolListener;
import Dim_LJR.armsorPlus.Boss.FakePlayerFactory;
import Dim_LJR.armsorPlus.Boss.FakePlayerProvider;
import Dim_LJR.armsorPlus.Boss.ModelBoss;
import Dim_LJR.armsorPlus.Boss.PlayerBoss;
import Dim_LJR.armsorPlus.Boss.SkinServer;
import Dim_LJR.armsorPlus.Boss.VillageCaptain;
import Dim_LJR.armsorPlus.Boss.VillageGuard;
import Dim_LJR.armsorPlus.Command.ArmsorPlusCommand;
import Dim_LJR.armsorPlus.Food.FoodListeners;
import Dim_LJR.armsorPlus.Food.TreeListeners;
import Dim_LJR.armsorPlus.OpenSea.LoadOpenSea;
import Dim_LJR.armsorPlus.OpenSea.OpenSeaDig;
import Dim_LJR.armsorPlus.OpenSea.OpenSeaEntity;
import Dim_LJR.armsorPlus.OpenSea.OpenSeaLottery;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static Dim_LJR.armsorPlus.NamespaceKey.banner;

// ArmsorPlus —— Minecraft武器装备强化插件
// 完全开源但不可用于商业用途。
// 此版本是原版本重制版，解决了原版本只有上帝才能看懂的bug。
// 客户端: Paper 1.21+ (向下兼容至1.13)
// 核心功能:
// - 自定义附魔系统 (基于PDC存储)
// - 武器/护甲/弓强化系统
// - 魔法球抽奖系统
// - 公海世界地图
public final class ArmsorPlus extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        regkey(this);           // 注册所有NamespaceKey
        banner();               // 打印启动横幅
        loadconfig();           // 加载配置文件
        SkinServer.init(this);  // 皮肤初始化 (默认不占端口, 见config)
        PlayerSettings.load(this); // 加载玩家设置

        // 玩家模型Boss后端发现: PacketEvents(发包渲染) 为可选依赖(softdepend), 附属插件(FakePlayerFactory)优先。
        // 附属插件 softdepend 主插件、必然后加载, 故不在此处一次性取 factory,
        // 而是由 FakePlayerProvider 实时懒发现 (附属就绪时触发 ServiceRegisterEvent, 见下方监听)。
        boolean packetEvents = Bukkit.getPluginManager().getPlugin("packetevents") != null;
        FakePlayerProvider.init(packetEvents);
        if (packetEvents) {
            initPacketEvents();
            BossSkin.prefetchShadowWarrior(); // 后台预取影武者皮肤 (Mojang在线拉取)
        }
        registerListeners();    // 注册事件监听器
        Enmity.startTask(this); // 挑拨木棍仇怨强制执行任务
        registerCommands();     // 注册命令
        ArmsorPlusRecipes.register(this);      // 注册合成配方
        BossWorld.loadWorld();  // 加载BOSS世界

        getLogger().info("===== ArmsorPlus v" + getDescription().getVersion() + " 已启用 =====");
        getLogger().info("服务端类型: " + Bukkit.getServer().getName());
        getLogger().info("Bukkit API 版本: " + Bukkit.getBukkitVersion());
    }
    @Override
    public void onDisable() {
        SkinServer.shutdown();  // 关闭皮肤文件服务器
        PlayerSettings.save(this); // 保存玩家设置
        if (FakePlayerProvider.hasPacketEvents() && PacketEvents.getAPI() != null) {
            PacketEvents.getAPI().terminate();
        }
        getLogger().info("ArmsorPlus 插件已禁用");
        HandlerList.unregisterAll();
    }

    // 初始化 PacketEvents 发包库 (玩家模型BOSS使用)
    private void initPacketEvents() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().debug(false);
        PacketEvents.getAPI().load();
        PacketEvents.getAPI().init();
        BossRenderer.registerListeners(); // 注册发包攻击拦截 + tick末尾假玩家同步 (影武者/村民队长共用)
        getLogger().info("PacketEvents 初始化完成");
    }

    // 附属插件 softdepend 主插件、必然后加载; 其 FakePlayerFactory 服务注册时触发此事件。
    // 用于在主插件已启用后感知附属就绪 (懒发现, 见 FakePlayerProvider.companion())。
    @EventHandler
    public void onServiceRegister(ServiceRegisterEvent event) {
        if (event.getProvider().getService() == FakePlayerFactory.class) {
            getLogger().info("已接入附属插件 ArmsorPlusFakePlayer (FakePlayerFactory), 玩家模型Boss将使用假玩家渲染");
        }
    }

    // 注册所有事件监听器
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ArmsorPlusMenu(), this);
        getServer().getPluginManager().registerEvents(new ArmsorPlusEnchantEventHandler(), this);
        getServer().getPluginManager().registerEvents(new EnhancementHandler(), this);
        getServer().getPluginManager().registerEvents(new BossMenu(), this);
        getServer().getPluginManager().registerEvents(new CreatureMenu(), this);
        getServer().getPluginManager().registerEvents(new PlayerBoss(), this);
        getServer().getPluginManager().registerEvents(new VillageCaptain(), this);
        getServer().getPluginManager().registerEvents(new VillageGuard(), this);
        getServer().getPluginManager().registerEvents(new ModelBoss.RealEntityDamageListener(), this); // 近战/弓箭/爆炸/火焰等真实伤害 → 统一血量
        getServer().getPluginManager().registerEvents(new ArmsorPlusItemHandler(), this);
        getServer().getPluginManager().registerEvents(new EnmityToolListener(), this);
        getServer().getPluginManager().registerEvents(new FoodListeners(), this);
        getServer().getPluginManager().registerEvents(new TreeListeners(),this);
    }

    // 注册指令执行器
    private void registerCommands() {
        this.getCommand("ArmsorPlus").setExecutor(new ArmsorPlusCommand());
    }

    // ===== 配置文件加载 =====

    // 是否启用插件植物系统 (水果/树苗) — 关闭后向导书菜单不显示相关物品
    public static boolean EnablePlants = false;

    // 资源包下载地址 (设置菜单中玩家可点击下载)
    public static String ResourcePackUrl = "";

    // 加载/初始化配置文件，按配置决定是否加载公海世界
    private void loadconfig() {
        saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        config.addDefault("SpawnOpenSea", false);
        config.addDefault("AutoResetOpenSeaMap", false);
        config.addDefault("OpenSeaName", "OpenSea");
        config.addDefault("MapZipName", "OpenSea.zip");
        config.addDefault("EnablePlants", false);
        config.addDefault("ResourcePackUrl", "");
        config.addDefault("BossSkinPlayerName", "Village_master");
        config.addDefault("BossSkinFile", "Village_master.png");
        config.addDefault("EnableSkinServer", false);
        config.addDefault("SkinServerHost", "");
        config.addDefault("SkinServerPort", 26666);
        config.addDefault("BossSkinValue", "");
        config.addDefault("BossSkinSignature", "");
        config.addDefault("CaptainSkinValue", "");
        config.addDefault("CaptainSkinSignature", "");
        config.options().copyDefaults(true);
        saveConfig();

        // 读取植物系统开关
        EnablePlants = config.getBoolean("EnablePlants", false);
        getLogger().info("插件植物系统: " + (EnablePlants ? "已启用" : "已关闭"));

        // 读取资源包地址
        ResourcePackUrl = config.getString("ResourcePackUrl", "");
        if (!ResourcePackUrl.isEmpty()) {
            getLogger().info("资源包地址已配置: " + ResourcePackUrl);
        }

        if (config.getBoolean("SpawnOpenSea")) {
            getLogger().info("加载公海地图中...");
            String zipName = config.getString("MapZipName");
            if (zipName == null) {
                LoadOpenSea.loadMap(Bukkit.getWorldContainer().toPath(),
                        this.getResource("OpenSea.zip"),
                        config.getBoolean("AutoResetOpenSeaMap"),
                        config.getString("OpenSeaName"),
                        "OpenSea.zip");
            } else {
                LoadOpenSea.loadMap(Bukkit.getWorldContainer().toPath(),
                        this.getResource(zipName),
                        config.getBoolean("AutoResetOpenSeaMap"),
                        config.getString("OpenSeaName"),
                        zipName);
            }
            getLogger().info("加载公海抽奖功能");
            getServer().getPluginManager().registerEvents(new OpenSeaLottery(), this);
            getLogger().info("加载公海地图保护功能");
            getServer().getPluginManager().registerEvents(new OpenSeaDig(), this);
            getLogger().info("加载公海地图生物功能");
            getServer().getPluginManager().registerEvents(new OpenSeaEntity(), this);
        } else {
            getLogger().info("公海地图已关闭");
        }
    }

    // ===== 强化石防放置 =====

    // 防止玩家将带有强化石/基础强化石标记的物品放置到地上，
    // 这些物品应通过右键使用而非放置。
    @EventHandler
    public void StonePlace(BlockPlaceEvent event) {
        int level = ArmsorEnchant.getEnchantLevel(event.getItemInHand(), BasicStone)
                + ArmsorEnchant.getEnchantLevel(event.getItemInHand(), Armskey)
                + ArmsorEnchant.getEnchantLevel(event.getItemInHand(), Armorkey);
        if (level == 0) return;
        event.setCancelled(true);
    }

    // ===== 基础强化石右键使用 =====

    // 右键使用基础强化石时随机获得一种强化石。
    // 概率分布: 一级护甲 33% | 二级护甲/一级武器/二级武器/弓 各约17%
    @EventHandler
    public void IfUSEBasicStone(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (ArmsorEnchant.getEnchantLevel(item, BasicStone) == 0) return;

        item.setAmount(item.getAmount() - 1);
        event.setCancelled(true);

        Random r = new Random();
        int i = r.nextInt(6); // 0~5

        if (i == 0 || i == 1) {
            event.getPlayer().getInventory().addItem(ArmorPlusCreate(1));
            event.getPlayer().sendMessage("已获得一级护甲强化石");
        } else if (i == 2) {
            event.getPlayer().getInventory().addItem(ArmorPlusCreateII(1));
            event.getPlayer().sendMessage("已获得二级护甲强化石");
        } else if (i == 3) {
            event.getPlayer().getInventory().addItem(ArmsPlusCreateI(1));
            event.getPlayer().sendMessage("已获得一级武器强化石");
        } else if (i == 4) {
            event.getPlayer().getInventory().addItem(ArmsPlusCreateII(1));
            event.getPlayer().sendMessage("已获得二级武器强化石");
        } else {
            // i == 5
            event.getPlayer().getInventory().addItem(BowPlusCreate(1));
            event.getPlayer().sendMessage("已获得一级弓箭强化石");
        }
    }
}
