package Dim_LJR.armsorPlus.Boss;

import Dim_LJR.armsorPlus.OpenSea.OpenSeaEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

// 生物菜单 —— 收录除BOSS外所有插件生成的生物, 点击直接在玩家位置召唤。
// 仿照 BossMenu: 45格单页GUI + 懒加载 + 按显示名称分派召唤。
public class CreatureMenu implements Listener {

    private static Inventory creatureList;

    public static Inventory getCreatureList() {
        if (creatureList == null) createCreatureList();
        return creatureList;
    }

    private static void createCreatureList() {
        creatureList = Bukkit.createInventory(null, 45, "§a生物菜单");

        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName(" ");
        border.setItemMeta(borderMeta);
        for (int i = 0; i < 45; i++) {
            if (i < 9 || i >= 36 || i % 9 == 0 || i % 9 == 8) {
                creatureList.setItem(i, border.clone());
            }
        }

        creatureList.setItem(10, creatureItem(Material.SKELETON_SPAWN_EGG, "§b■ 守卫者",
                "§7公海宝藏的守卫骷髅，",
                "§7手持守卫之剑，身穿全套守卫装备。",
                "",
                "§c❤ 生命值: 60",
                "§f⚔ 守卫之剑 (攻击+15)",
                "§e✦ 移速II，4分钟后自动消失",
                "",
                "§a▼ 点击召唤生物"));

        creatureList.setItem(11, creatureItem(Material.IRON_GOLEM_SPAWN_EGG, "§f■ 傀儡守护者",
                "§7「傀儡守护」附魔召唤的铁傀儡，",
                "§7在穿戴者受击时出现并反击攻击者。",
                "",
                "§f⚔ 铁拳攻击，自动索敌",
                "§e✦ 由傀儡守护附魔触发 (300秒冷却)",
                "",
                "§a▼ 点击召唤生物"));

        creatureList.setItem(12, creatureItem(Material.ARMOR_STAND, "§d■ 幻影假身",
                "§7幻术剑击杀时生成的分身诱饵，",
                "§7附近怪物会被迷惑并攻击它。",
                "",
                "§d⚔ 无攻击，纯诱饵",
                "§e✦ 幻术剑击杀25%触发，5秒后消失",
                "",
                "§a▼ 点击召唤生物"));

        creatureList.setItem(13, creatureItem(Material.SLIME_BALL, "§a■ 小史莱姆",
                "§7史莱姆王半血狂暴时召唤的小兵，",
                "§7成群结队围攻敌人。",
                "",
                "§c❤ 生命值: 30",
                "§a⚔ 跳跃冲撞",
                "§e✦ 史莱姆王半血触发，上限6只",
                "",
                "§a▼ 点击召唤生物"));

        creatureList.setItem(14, creatureItem(Material.VINDICATOR_SPAWN_EGG, "§c■ 幻术护卫",
                "§7幻术师召唤的灾厄村民护卫，",
                "§7手持锋利VI铁斧，冲锋砍杀。",
                "",
                "§c⚔ 锋利VI铁斧",
                "§e✦ 幻术师40%血量时召唤4名",
                "§e✦ 速度II + 急迫II",
                "",
                "§a▼ 点击召唤生物"));

        creatureList.setItem(15, creatureItem(Material.ZOMBIE_HORSE_SPAWN_EGG, "§2■ 铁骑·僵尸战马",
                "§7铁骑双雄的僵尸战马，",
                "§7速度VII冲锋，抗性IV坚固。",
                "",
                "§c❤ 生命值: 125",
                "§b⚡ 速度VII + 抗性IV",
                "§e✦ 已驯服，可骑乘",
                "",
                "§a▼ 点击召唤生物"));

        creatureList.setItem(16, creatureItem(Material.SPIDER_SPAWN_EGG, "§7■ 铁骑·毒蛛",
                "§7铁骑双雄骷髅射手的毒蛛坐骑，",
                "§7速度VII，抗性IV，载骑冲锋。",
                "",
                "§c❤ 生命值: 125",
                "§b⚡ 速度VII + 抗性IV",
                "",
                "§a▼ 点击召唤生物"));

        creatureList.setItem(19, creatureItem(Material.ZOMBIE_SPAWN_EGG, "§2■ 僵尸骑士",
                "§7铁骑双雄的主力骑士，",
                "§7全套下界合金，长矛盾牌冲锋。",
                "",
                "§c❤ 生命值: 150",
                "§2⚔ 下界合金长矛 (攻击25) + 盾",
                "§e✦ 装备不可掉落",
                "",
                "§a▼ 点击召唤生物"));

        creatureList.setItem(20, creatureItem(Material.SKELETON_SPAWN_EGG, "§7■ 骷髅射手",
                "§7铁骑双雄的远程射手，",
                "§7全套下界合金，神弓远射。",
                "",
                "§c❤ 生命值: 100",
                "§7⚔ 神弓 (力量85·弹道V)",
                "§e✦ 装备不可掉落",
                "",
                "§a▼ 点击召唤生物"));

        creatureList.setItem(21, creatureItem(Material.VILLAGER_SPAWN_EGG, "§7■ 村民卫兵",
                "§7村庄的守卫，佩戴铁盔手持铁剑，",
                "§7警惕地驱逐一切威胁。",
                "",
                "§c❤ 生命值: 30",
                "§f⚔ 普通铁剑",
                "§7🛡 铁头盔 (保护II)",
                "§e✦ 玩家模型渲染，可召唤多只",
                "",
                "§a▼ 点击召唤生物"));
    }

    private static ItemStack creatureItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    // ========================================================================
    // 菜单点击
    // ========================================================================

    @EventHandler
    public void onCreatureListClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(creatureList)) return;
        event.setCancelled(true);

        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;

        String name = event.getCurrentItem().getItemMeta().getDisplayName();
        Player player = (Player) event.getWhoClicked();

        if (name.contains("守卫者")) {
            summonGuardian(player);
        } else if (name.contains("傀儡守护者")) {
            summonGolemPet(player);
        } else if (name.contains("幻影假身")) {
            summonIllusionClone(player);
        } else if (name.contains("小史莱姆")) {
            summonSmallSlime(player);
        } else if (name.contains("幻术护卫")) {
            summonVindicator(player);
        } else if (name.contains("僵尸战马")) {
            summonZombieHorse(player);
        } else if (name.contains("毒蛛")) {
            summonPoisonSpider(player);
        } else if (name.contains("僵尸骑士")) {
            summonZombieKnight(player);
        } else if (name.contains("骷髅射手")) {
            summonSkeletonArcher(player);
        } else if (name.contains("村民卫兵")) {
            summonVillageGuard(player);
        }
    }

    // ========================================================================
    // 召唤实现
    // ========================================================================

    // 守卫者 (公海守卫骷髅)
    private static void summonGuardian(Player player) {
        player.closeInventory();
        OpenSeaEntity.SpawnSkeleton(player.getLocation());
        player.sendMessage("§b守卫者: 已召唤!");
    }

    // 傀儡守护者 (友好铁傀儡)
    private static void summonGolemPet(Player player) {
        player.closeInventory();
        IronGolem golem = player.getWorld().spawn(player.getLocation(), IronGolem.class);
        golem.setCustomName("§f傀儡守护者");
        golem.setCustomNameVisible(true);
        golem.setPlayerCreated(true);
        golem.setRemoveWhenFarAway(false);
        player.sendMessage("§f傀儡守护者: 已召唤!");
    }

    // 幻影假身 (隐形盔甲架诱饵, 5秒后消失)
    private static void summonIllusionClone(Player player) {
        Location loc = player.getLocation();
        World world = loc.getWorld();
        ArmorStand clone = world.spawn(loc, ArmorStand.class, s -> {
            s.setVisible(false);
            s.setGravity(false);
            s.setInvulnerable(true);
            s.setMarker(true);
        });
        clone.setCustomName("§d幻影假身");
        clone.setCustomNameVisible(true);
        // 附近怪物把假身当目标
        for (Entity e : world.getNearbyEntities(loc, 10, 10, 10)) {
            if (e instanceof Mob mob) {
                mob.setTarget(clone);
            }
        }
        world.spawnParticle(Particle.PORTAL, loc.add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.2);
        Bukkit.getScheduler().runTaskLater(getplugin, clone::remove, 100L);
        player.closeInventory();
        player.sendMessage("§d幻影假身: 附近怪物被迷惑了!");
    }

    // 小史莱姆
    private static void summonSmallSlime(Player player) {
        Location loc = player.getLocation();
        Slime small = (Slime) loc.getWorld().spawnEntity(loc, EntityType.SLIME);
        small.setSize(3);
        small.setCustomName("§a小史莱姆");
        small.setCustomNameVisible(true);
        small.setRemoveWhenFarAway(true);
        var maxHp = small.getAttribute(Attribute.MAX_HEALTH);
        if (maxHp != null) maxHp.setBaseValue(30);
        small.setHealth(30);
        loc.getWorld().spawnParticle(Particle.ITEM_SLIME, loc, 10, 0.5, 0.3, 0.5, 0.1);
        player.closeInventory();
        player.sendMessage("§a小史莱姆: 已召唤!");
    }

    // 幻术护卫 (灾厄村民)
    private static void summonVindicator(Player player) {
        Location loc = player.getLocation();
        Vindicator vind = (Vindicator) loc.getWorld().spawnEntity(loc, EntityType.VINDICATOR);
        vind.setCustomName("§c幻术护卫");
        vind.setCustomNameVisible(true);
        vind.setRemoveWhenFarAway(false);
        ItemStack axe = new ItemStack(Material.IRON_AXE);
        ItemMeta axeMeta = axe.getItemMeta();
        axeMeta.addEnchant(Enchantment.SHARPNESS, 6, true);
        axe.setItemMeta(axeMeta);
        EntityEquipment equip = vind.getEquipment();
        equip.setItemInMainHand(axe);
        equip.setItemInMainHandDropChance(0f);
        vind.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 0, false, false));
        vind.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, -1, 0, false, false));
        player.closeInventory();
        player.sendMessage("§c幻术护卫: 已召唤!");
    }

    // 铁骑·僵尸战马
    private static void summonZombieHorse(Player player) {
        Location loc = player.getLocation();
        loc.getWorld().spawn(loc, ZombieHorse.class, h -> {
            h.setCustomName("§2铁骑·僵尸战马");
            h.setCustomNameVisible(true);
            h.setRemoveWhenFarAway(false);
            h.setPersistent(true);
            h.setTamed(true);
            var maxHp = h.getAttribute(Attribute.MAX_HEALTH);
            if (maxHp != null) maxHp.setBaseValue(125);
            h.setHealth(125);
            var speed = h.getAttribute(Attribute.MOVEMENT_SPEED);
            if (speed != null) speed.setBaseValue(0.2);
            h.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 6, false, false));
            h.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, 3, false, false));
        });
        player.closeInventory();
        player.sendMessage("§2铁骑·僵尸战马: 已召唤!");
    }

    // 铁骑·毒蛛
    private static void summonPoisonSpider(Player player) {
        Location loc = player.getLocation();
        loc.getWorld().spawn(loc, Spider.class, s -> {
            s.setCustomName("§7铁骑·毒蛛");
            s.setCustomNameVisible(true);
            s.setRemoveWhenFarAway(false);
            s.setPersistent(true);
            var maxHp = s.getAttribute(Attribute.MAX_HEALTH);
            if (maxHp != null) maxHp.setBaseValue(125);
            s.setHealth(125);
            var speed = s.getAttribute(Attribute.MOVEMENT_SPEED);
            if (speed != null) speed.setBaseValue(0.2);
            s.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 6, false, false));
            s.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, -1, 3, false, false));
        });
        player.closeInventory();
        player.sendMessage("§7铁骑·毒蛛: 已召唤!");
    }

    // 僵尸骑士 (复用铁骑双雄装备)
    private static void summonZombieKnight(Player player) {
        Location loc = player.getLocation();
        loc.getWorld().spawn(loc, Zombie.class, z -> {
            z.setCustomName("§2僵尸骑士");
            z.setCustomNameVisible(true);
            z.setRemoveWhenFarAway(false);
            z.setPersistent(true);
            var maxHp = z.getAttribute(Attribute.MAX_HEALTH);
            if (maxHp != null) maxHp.setBaseValue(150);
            z.setHealth(150);
            DesertCamelBoss.equipNetheriteArmor(z);
            EntityEquipment equip = z.getEquipment();
            equip.setItemInMainHand(DesertCamelBoss.createSpear());
            equip.setItemInOffHand(DesertCamelBoss.createShield());
            equip.setItemInMainHandDropChance(0f);
            equip.setItemInOffHandDropChance(0f);
        });
        player.closeInventory();
        player.sendMessage("§2僵尸骑士: 已召唤!");
    }

    // 骷髅射手 (复用铁骑双雄装备)
    private static void summonSkeletonArcher(Player player) {
        Location loc = player.getLocation();
        loc.getWorld().spawn(loc, Skeleton.class, s -> {
            s.setCustomName("§7骷髅射手");
            s.setCustomNameVisible(true);
            s.setRemoveWhenFarAway(false);
            s.setPersistent(true);
            var maxHp = s.getAttribute(Attribute.MAX_HEALTH);
            if (maxHp != null) maxHp.setBaseValue(100);
            s.setHealth(100);
            DesertCamelBoss.equipNetheriteArmor(s);
            EntityEquipment equip = s.getEquipment();
            equip.setItemInMainHand(DesertCamelBoss.createBow());
            equip.setItemInMainHandDropChance(0f);
        });
        player.closeInventory();
        player.sendMessage("§7骷髅射手: 已召唤!");
    }

    // 村民卫兵 (玩家模型生物, 保护II铁头盔 + 铁剑)
    private static void summonVillageGuard(Player player) {
        VillageGuard.spawn(player);
    }
}
