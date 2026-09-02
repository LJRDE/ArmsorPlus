package Dim_LJR.armsorPlus.ArmsorPlusEnchant;
import Dim_LJR.armsorPlus.PlayerSettings;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;

// 噬生剑 —— 耐久度显示血裂数, 攻击累积血裂提升伤害
public class DevourLifeEnchant implements Listener {

    // 血裂衰减刷新: 血裂存在30秒后衰减, 定时刷新显示 (耐久条+Lore)
    private final Set<UUID> pendingBloodDecayRefresh = new HashSet<>();

    // 解析时间戳字符串为List
    private static List<Long> parseTimestamps(String tsStr) {
        List<Long> list = new ArrayList<>();
        if (tsStr == null || tsStr.isEmpty()) return list;
        for (String s : tsStr.split(",")) {
            try { list.add(Long.parseLong(s)); } catch (NumberFormatException ignored) {}
        }
        return list;
    }

    // 计算噬生剑当前有效血裂数 (30秒内的时间戳数量, 上限20)
    public static int getDevourLifeBloodCount(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) return 0;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        List<Long> timestamps = parseTimestamps(pdc.get(DevourLifeBloodTimestamps, PersistentDataType.STRING));
        long now = System.currentTimeMillis();
        timestamps.removeIf(ts -> now - ts >= 30000);
        return Math.min(timestamps.size(), 20);
    }

    // 更新噬生剑显示: 耐久条 + Lore 血裂数 (0/20 → 20/20)
    private void updateDevourLifeDisplay(ItemMeta meta, int maxDurability, int bloodCount) {
        // 耐久条显示 (0血裂≈空条, 20血裂=满条; 至少保留1点耐久防止损坏, 攻击不消耗耐久)
        if (meta instanceof Damageable dmg) {
            int damage = maxDurability - 1 - (int) Math.round(
                    (double) (maxDurability - 1) * Math.min(bloodCount, 20) / 20);
            dmg.setDamage(damage);
        }
        // Lore 显示血裂数 (保留其他 Lore 行, 如附魔行)
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        String line = ChatColor.DARK_RED + "血裂数: " + bloodCount + "/20";
        boolean replaced = false;
        for (int i = 0; i < lore.size(); i++) {
            if (ChatColor.stripColor(lore.get(i)).contains("血裂数")) {
                lore.set(i, line);
                replaced = true;
                break;
            }
        }
        if (!replaced) lore.add(line);
        meta.setLore(lore);
    }

    private void scheduleBloodDecayRefresh(Player player) {
        scheduleBloodDecayRefresh(player, player.getEquipment().getItemInMainHand());
    }

    private void scheduleBloodDecayRefresh(Player player, ItemStack item) {
        if (pendingBloodDecayRefresh.contains(player.getUniqueId())) return;
        if (item == null || item.getType().isAir() || item.getItemMeta() == null) return;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        List<Long> timestamps = parseTimestamps(pdc.get(DevourLifeBloodTimestamps, PersistentDataType.STRING));
        if (timestamps.isEmpty()) return;
        // 精确到最旧血裂的过期时刻 (时间戳按追加顺序排列, 第一个最旧)
        long delayMs = timestamps.get(0) + 30000 - System.currentTimeMillis();
        scheduleBloodDecayRefresh(player, Math.max(1, (delayMs + 49) / 50)); // ms -> ticks 向上取整
    }

    private void scheduleBloodDecayRefresh(Player player, long delayTicks) {
        if (pendingBloodDecayRefresh.contains(player.getUniqueId())) return;
        pendingBloodDecayRefresh.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(getplugin, () -> {
            pendingBloodDecayRefresh.remove(player.getUniqueId());
            // 玩家已登出/死亡则跳过, 避免空指针
            if (!player.isOnline() || player.isDead()) return;
            ItemStack item = player.getEquipment().getItemInMainHand();
            if (item.getType().isAir() || ArmsorEnchant.getEnchantLevel(item, DevourLifeSwordKey) == 0) return;
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            String tsStr = pdc.get(DevourLifeBloodTimestamps, PersistentDataType.STRING);
            List<Long> timestamps = parseTimestamps(tsStr);
            long now = System.currentTimeMillis();
            timestamps.removeIf(ts -> now - ts >= 30000);
            int bloodCount = Math.min(timestamps.size(), 20);
            // 保存清理后的时间戳
            pdc.set(DevourLifeBloodTimestamps, PersistentDataType.STRING,
                    timestamps.stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse(""));
            updateDevourLifeDisplay(meta, item.getType().getMaxDurability(), bloodCount);
            item.setItemMeta(meta);
            // 血裂未完全衰减: 继续追踪下一次衰减, 直到血裂清零
            if (!timestamps.isEmpty()) {
                long delayMs = timestamps.get(0) + 30000 - System.currentTimeMillis();
                scheduleBloodDecayRefresh(player, Math.max(1, (delayMs + 49) / 50));
            }
        }, Math.max(1, delayTicks));
    }

    // 噬生剑: 耐久度用来显示血裂数, 攻击/使用时不得消耗耐久
    @EventHandler
    public void onDevourLifeItemDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        if (ArmsorEnchant.getEnchantLevel(item, DevourLifeSwordKey) > 0) {
            event.setCancelled(true);
        }
    }

    // 刷新噬生剑显示 (Lore + 耐久条): 根据当前有效血裂数重写
    private void refreshDevourLifeDisplay(ItemStack item) {
        if (item == null || item.getType().isAir()) return;
        if (ArmsorEnchant.getEnchantLevel(item, DevourLifeSwordKey) == 0) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        int count = getDevourLifeBloodCount(item);
        updateDevourLifeDisplay(meta, item.getType().getMaxDurability(), count);
        item.setItemMeta(meta);
    }

    // 噬生切到主手时刷新显示
    @EventHandler
    public void onDevourLifeItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());
        if (item == null || item.getType().isAir()) return;
        if (ArmsorEnchant.getEnchantLevel(item, DevourLifeSwordKey) == 0) return;
        int count = getDevourLifeBloodCount(item);
        refreshDevourLifeDisplay(item);
        // 剑已在主手, 若还有未衰减的血裂则续排衰减追踪
        if (count > 0) scheduleBloodDecayRefresh(player, item);
    }

    // 在背包/容器点击噬生剑时刷新显示
    @EventHandler
    public void onDevourLifeInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        refreshDevourLifeDisplay(event.getCurrentItem());
    }

    // 噬生攻击侧: 攻击累积血裂
    @EventHandler
    public void onDevourLifeAttack(EntityDamageByEntityEvent event) {
        if (EnchantUtil.PIERCING_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (EnchantUtil.REVENGE_ACTIVE.contains(event.getEntity().getUniqueId())) return;
        if (!EnchantUtil.isDirectMeleeAttack(event)) return;
        if (event.getEntity().equals(event.getDamager())) return;
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (damager.getEquipment() == null) return;
        ItemStack weapon = damager.getEquipment().getItemInMainHand();
        if (weapon.getType().isAir()) return;
        if (ArmsorEnchant.getEnchantLevel(weapon, DevourLifeSwordKey) <= 0) return;

        ItemMeta meta = weapon.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Double score = pdc.get(ScoreKey, PersistentDataType.DOUBLE);
        if (score == null) score = 0.0;
        String tsStr = pdc.get(DevourLifeBloodTimestamps, PersistentDataType.STRING);
        List<Long> timestamps = parseTimestamps(tsStr);
        long now = System.currentTimeMillis();
        timestamps.removeIf(ts -> now - ts >= 30000);
        int bc = Math.min(timestamps.size(), 20);
        score = Math.min(score, bc * 4.0 + 3.99);
        int oldT = (int)(score / 4.0);
        score += event.getFinalDamage();
        int newT = (int)(score / 4.0);
        int gained = newT - oldT;
        for (int i = 0; i < gained && bc < 20; i++) { timestamps.add(now); bc++; }
        if (bc >= 20) score = Math.min(score, 20 * 4.0 + 3.99);
        event.setDamage(event.getDamage() + bc);
        pdc.set(ScoreKey, PersistentDataType.DOUBLE, score);
        pdc.set(DevourLifeBloodTimestamps, PersistentDataType.STRING,
                timestamps.stream().map(String::valueOf).reduce((a,b)->a+","+b).orElse(""));
        updateDevourLifeDisplay(meta, weapon.getType().getMaxDurability(), bc);
        weapon.setItemMeta(meta);
        if (gained > 0 && event.getDamager() instanceof Player)
            PlayerSettings.notify(event.getDamager(), ChatColor.DARK_PURPLE + "产生了一点血裂! 当前血裂数: " + bc);
        if (damager instanceof Player player) scheduleBloodDecayRefresh(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        pendingBloodDecayRefresh.remove(event.getPlayer().getUniqueId());
    }
}
