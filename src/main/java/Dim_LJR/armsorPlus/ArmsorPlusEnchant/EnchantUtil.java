package Dim_LJR.armsorPlus.ArmsorPlusEnchant;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.getplugin;

// 附魔/武器事件处理器共用的静态工具与共享状态。
// 拆分前这些都在 ArmsorPlusEnchantEventHandler 里, 拆分为逐附魔/逐武器类后统一收敛于此。
public final class EnchantUtil {

    private EnchantUtil() {}

    // 共享随机数 (避免 percent() 每次 new Random 的 GC 压力)
    public static final Random RANDOM = new Random();

    // 正在被本插件穿透伤害处理中的实体 (用于防止穿透伤害递归重入)
    public static final Set<UUID> PIERCING_ACTIVE = new HashSet<>();
    // 正在被复仇反弹伤害处理中的实体 (标记复仇伤害来源, 避免触发血祭/吸血等攻击侧附魔)
    public static final Set<UUID> REVENGE_ACTIVE = new HashSet<>();
    // 正在被卸力拆分结算中的实体 (防止拆分的分段伤害被再次拆分)
    public static final Set<UUID> DAMAGE_SPLIT_ACTIVE = new HashSet<>();

    // 百分率随机: 返回 true 的概率为 x%
    public static boolean percent(int x) {
        return RANDOM.nextInt(101) <= x;
    }

    // 将整数转为罗马数字 (I~X)
    public static String romanNumeral(int num) {
        return switch (num) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(num);
        };
    }

    // 获取实体的可读名称
    public static String getEntityName(Entity entity) {
        if (entity instanceof Player) return ((Player) entity).getName();
        return entity.getCustomName() != null ? entity.getCustomName() : entity.getName();
    }

    // 构造"无法抵挡"的穿透伤害源: 无视护甲/保护附魔/抗性/无敌帧 (等价于原版 /kill)
    public static DamageSource pierceSource(Entity causer) {
        return DamageSource.builder(DamageType.GENERIC_KILL)
                .withDirectEntity(causer)
                .withCausingEntity(causer)
                .build();
    }

    // 伤害溯源: 判断是否为真实近战攻击 (ENTITY_ATTACK/ENTITY_SWEEP_ATTACK)。
    // 荆棘(THORNS)等反弹伤害的 damager 是持武器玩家, 但并非玩家主动挥击,
    // 所有武器命中效果必须先过此关, 避免在反弹伤害上误触发 (血祭/双重打击/火焰戟/匕首等)
    public static boolean isDirectMeleeAttack(EntityDamageByEntityEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        return cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK;
    }

    private static final EntityDamageEvent.DamageCause[] MAGIC_DAMAGE_TYPES = {
            EntityDamageEvent.DamageCause.MAGIC,
            EntityDamageEvent.DamageCause.POISON,
            EntityDamageEvent.DamageCause.WITHER,
            EntityDamageEvent.DamageCause.DRAGON_BREATH
    };

    public static boolean isMagicDamage(EntityDamageEvent.DamageCause cause) {
        for (EntityDamageEvent.DamageCause type : MAGIC_DAMAGE_TYPES) {
            if (cause == type) return true;
        }
        return false;
    }

    // 判断是否为"物理攻击": 由实体造成 (近战/弓箭/投射物), 且不是魔法伤害
    public static boolean isPhysicalAttack(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent)) return false;
        return !isMagicDamage(event.getCause());
    }

    // 根据护甲材质估算基础护甲值
    public static double getArmorValue(Material material) {
        String name = material.name().toLowerCase();
        if (name.contains("netherite")) {
            if (name.contains("chestplate")) return 8;
            if (name.contains("leggings")) return 6;
            if (name.contains("helmet") || name.contains("boots")) return 3;
        } else if (name.contains("diamond")) {
            if (name.contains("chestplate")) return 8;
            if (name.contains("leggings")) return 6;
            if (name.contains("helmet") || name.contains("boots")) return 3;
        } else if (name.contains("iron")) {
            if (name.contains("chestplate")) return 6;
            if (name.contains("leggings")) return 5;
            if (name.contains("helmet") || name.contains("boots")) return 2;
        } else if (name.contains("chainmail")) {
            if (name.contains("chestplate")) return 5;
            if (name.contains("leggings")) return 4;
            if (name.contains("helmet") || name.contains("boots")) return 2;
        } else if (name.contains("gold")) {
            if (name.contains("chestplate")) return 5;
            if (name.contains("leggings")) return 3;
            if (name.contains("helmet") || name.contains("boots")) return 2;
        } else if (name.contains("leather")) {
            if (name.contains("chestplate")) return 3;
            if (name.contains("leggings")) return 2;
            if (name.contains("helmet") || name.contains("boots")) return 1;
        } else if (name.contains("turtle")) {
            return 2; // 海龟壳
        }
        return 0;
    }

    // 更新武器Lore中的剩余次数显示
    public static void updateUsesLore(ItemStack item, int uses) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return;
        List<String> lore = meta.getLore();
        for (int i = 0; i < lore.size(); i++) {
            if (ChatColor.stripColor(lore.get(i)).contains("剩余次数")) {
                lore.set(i, ChatColor.YELLOW + "剩余次数: " + uses);
                break;
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    // 寻找安全传送位置: 向上搜索5格寻找双脚和头部均可通行的位置
    public static Location findSafeTeleportLocation(Location base) {
        for (int yOffset = 0; yOffset <= 5; yOffset++) {
            Location check = base.clone().add(0, yOffset, 0);
            if (check.getBlock().isPassable() && check.clone().add(0, 1, 0).getBlock().isPassable()) {
                return check;
            }
        }
        return null;
    }

    // 检查玩家是否露天（用于雨天判定）
    public static boolean isExposedToRain(Player player) {
        if (!player.getWorld().hasStorm()) return false;
        Location loc = player.getLocation();
        return loc.getWorld().getHighestBlockYAt(loc) <= loc.getBlockY();
    }

    // 检查玩家是否处于雨天环境
    public static boolean isInRain(Player player) {
        return player.getWorld().hasStorm() && isExposedToRain(player);
    }
}
