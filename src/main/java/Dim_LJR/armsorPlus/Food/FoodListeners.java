package Dim_LJR.armsorPlus.Food;

import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
// 所有食物/药品/树苗的事件监听器。
public class FoodListeners implements Listener {

    // 通用食物/药品食用前置处理
    // 校验: 主手右键 + 物品带有指定自定义附魔键
    // 通过后取消事件, 并在非创造模式下消耗1个物品
    private boolean consumeIfFood(PlayerInteractEvent event, NamespacedKey key) {
        if (event.getHand() != EquipmentSlot.HAND) return false;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return false;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, key) == 0) return false;
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE) {
            item.setAmount(item.getAmount() - 1);
        }
        return true;
    }

    private static final Random RANDOM = new Random();

    // ========================================================================
    // 食物/药品: 右键使用
    // ========================================================================

    @EventHandler
    public void onRejuvenationUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null || ArmsorEnchant.getEnchantLevel(item, RejuvenationPowderKey) == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        int tier = ArmsorEnchant.getEnchantLevel(item, RejuvenationPowderKey);

        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE) {
            item.setAmount(item.getAmount() - 1);
        }

        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0),
                15, 0.4, 0.4, 0.4, 0.05);
        int[] effects = switch (tier) {
            case 4 -> new int[]{2400, 9};   // 仙品: 生命恢复 X 120s (匹配Lore)
            case 3 -> new int[]{800, 9};    // 极品: 生命恢复 X 40s (匹配Lore)
            case 2 -> new int[]{120, 4};    // 上品: 生命恢复 V 6s (匹配Lore)
            default -> new int[]{60, 4};    // 普通: 生命恢复 V 3s (匹配Lore)
        };
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, effects[0], effects[1]));

        if (tier >= 3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, effects[0], 4));
        }
        if (tier >= 4) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, effects[0], 4));
        }

        if (tier >= 3) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType() != PotionEffectType.REGENERATION
                        && effect.getType() != PotionEffectType.ABSORPTION
                        && effect.getType() != PotionEffectType.RESISTANCE
                        && effect.getType() != PotionEffectType.HERO_OF_THE_VILLAGE) {
                    player.removePotionEffect(effect.getType());
                }
            }
        }

        String tierName = switch (tier) {
            case 4 -> "仙品";
            case 3 -> "极品";
            case 2 -> "上品";
            default -> "普通";
        };
        player.sendActionBar("§d已使用" + tierName + "回春散");
        switch (tier) {
            case 4 -> player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.5f);
            case 3 -> player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
            case 2 -> player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 0.8f, 1.0f);
            default -> player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 0.8f, 1.0f);
        }
    }

    @EventHandler
    public void onBandageUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, HemostaticBandageKey)) return;
        Player player = event.getPlayer();

        double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        double newHealth = Math.min(maxHealth, player.getHealth() + 6);
        player.setHealth(newHealth);

        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0),
                10, 0.4, 0.4, 0.4, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_BUNDLE_REMOVE_ONE, 0.8f, 1.0f);
        player.sendActionBar("§c❤ 已使用止血绷带");
    }

    @EventHandler
    public void onBiscuitUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, CompressedBiscuitKey)) return;
        Player player = event.getPlayer();

        player.setFoodLevel(20);
        player.setSaturation(20);

        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation().add(0, 1, 0),
                5, 0.3, 0.3, 0.3, 0.05);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 0.8f, 1.5f);
        player.sendActionBar("§6压缩饼干 饱食度已恢复");
    }

    @EventHandler
    public void onJerkyUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, JerkyKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 6));
        player.setSaturation(Math.min(20, player.getSaturation() + 7.2f));
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation().add(0, 1, 0), 3, 0.2, 0.2, 0.2, 0.02);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 0.8f, 1.0f);
        player.sendActionBar("§6已食用肉干");
    }

    @EventHandler
    public void onPorkJerkyUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, PorkJerkyKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 7));
        player.setSaturation(Math.min(20, player.getSaturation() + 8.0f));
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation().add(0, 1, 0), 3, 0.2, 0.2, 0.2, 0.02);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 0.8f, 1.0f);
        player.sendActionBar("§6已食用猪肉干");
    }

    @EventHandler
    public void onMuttonJerkyUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, MuttonJerkyKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 5));
        player.setSaturation(Math.min(20, player.getSaturation() + 6.0f));
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation().add(0, 1, 0), 3, 0.2, 0.2, 0.2, 0.02);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 0.8f, 1.0f);
        player.sendActionBar("§6已食用羊肉干");
    }

    @EventHandler
    public void onBigAppleUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, BigAppleKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 4));
        player.setSaturation(Math.min(20f, player.getSaturation() + 2.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用大苹果");
    }

    @EventHandler
    public void onPlumUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, PlumKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 3));
        player.setSaturation(Math.min(20f, player.getSaturation() + 2.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用李子");
    }

    @EventHandler
    public void onHazelnutUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, HazelnutKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 2));
        player.setSaturation(Math.min(20f, player.getSaturation() + 3.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用榛子");
    }

    @EventHandler
    public void onCoconutUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, CoconutKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 5));
        player.setSaturation(Math.min(20f, player.getSaturation() + 3.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用椰子");
    }

    @EventHandler
    public void onPineappleUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, PineappleKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 6));
        player.setSaturation(Math.min(20f, player.getSaturation() + 5.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用菠萝");
    }

    @EventHandler
    public void onStrawberryUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, StrawberryKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 2));
        player.setSaturation(Math.min(20f, player.getSaturation() + 1.5f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用草莓");
    }

    @EventHandler
    public void onBlueberryUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, BlueberryKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 2));
        player.setSaturation(Math.min(20f, player.getSaturation() + 1.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用蓝莓");
    }

    @EventHandler
    public void onOrangeUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, OrangeKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 4));
        player.setSaturation(Math.min(20f, player.getSaturation() + 3.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用橙子");
    }

    @EventHandler
    public void onTangerineUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, TangerineKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 3));
        player.setSaturation(Math.min(20f, player.getSaturation() + 2.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用橘子");
    }

    @EventHandler
    public void onIceCubeUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, IceCubeKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 1));
        player.setSaturation(Math.min(20f, player.getSaturation() + 1.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用冰块");
    }

    @EventHandler
    public void onFigUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, FigKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 4));
        player.setSaturation(Math.min(20f, player.getSaturation() + 3.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用无花果");
    }

    @EventHandler
    public void onDateUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, DateKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 3));
        player.setSaturation(Math.min(20f, player.getSaturation() + 2.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用枣子");
    }

    @EventHandler
    public void onPersimmonUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, PersimmonKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 4));
        player.setSaturation(Math.min(20f, player.getSaturation() + 3.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用柿子");
    }

    @EventHandler
    public void onMangosteenUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, MangosteenKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 5));
        player.setSaturation(Math.min(20f, player.getSaturation() + 4.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用山竹");
    }

    @EventHandler
    public void onCherryTomatoUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, CherryTomatoKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 2));
        player.setSaturation(Math.min(20f, player.getSaturation() + 1.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用圣女果");
    }

    @EventHandler
    public void onTomatoUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, TomatoKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 3));
        player.setSaturation(Math.min(20f, player.getSaturation() + 2.5f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用西红柿");
    }

    @EventHandler
    public void onGrapeUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, GrapeKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 3));
        player.setSaturation(Math.min(20f, player.getSaturation() + 2.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用葡萄");
    }

    @EventHandler
    public void onPomegranateUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, PomegranateKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 5));
        player.setSaturation(Math.min(20f, player.getSaturation() + 4.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用石榴");
    }

    @EventHandler
    public void onChestnutUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, ChestnutKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 3));
        player.setSaturation(Math.min(20f, player.getSaturation() + 4.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用栗子");
    }

    @EventHandler
    public void onKiwiUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, KiwiKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 4));
        player.setSaturation(Math.min(20f, player.getSaturation() + 3.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用猕猴桃");
    }

    @EventHandler
    public void onLonganUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, LonganKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 3));
        player.setSaturation(Math.min(20f, player.getSaturation() + 2.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用龙眼");
    }

    @EventHandler
    public void onLycheeUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, LycheeKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 4));
        player.setSaturation(Math.min(20f, player.getSaturation() + 3.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用荔枝");
    }

    @EventHandler
    public void onCherryUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, CherryKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 2));
        player.setSaturation(Math.min(20f, player.getSaturation() + 1.5f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用樱桃");
    }

    @EventHandler
    public void onPeachUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, PeachKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 5));
        player.setSaturation(Math.min(20f, player.getSaturation() + 4.0f));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        player.sendActionBar("§d已食用桃子");
    }

    // ========================================================================
    // 树苗种植处理器 (统一处理23种，带元数据标记)
    // ========================================================================

    private static final Map<NamespacedKey, SaplingData> SAPLING_MAP = new HashMap<>();
    // 树苗: 种植为原版树苗，自然/骨粉生长后结果
    // 草本植物: 直接种植为树叶灌木，右键采摘 (ExoticGarden风格)
    private static final Set<String> HERBACEOUS_KEYS = new HashSet<>();

    static {
        HERBACEOUS_KEYS.add(CherryTomatoSaplingKey.getKey());
        HERBACEOUS_KEYS.add(TomatoSaplingKey.getKey());
        HERBACEOUS_KEYS.add(GrapeSaplingKey.getKey());
        HERBACEOUS_KEYS.add(KiwiSaplingKey.getKey());
        HERBACEOUS_KEYS.add(PineappleSaplingKey.getKey());
        HERBACEOUS_KEYS.add(StrawberrySaplingKey.getKey());
        HERBACEOUS_KEYS.add(BlueberrySaplingKey.getKey());

        // 木本果树: 统一使用橡树树苗
        SAPLING_MAP.put(FigSaplingKey,          new SaplingData(Material.OAK_SAPLING,    "无花果树苗"));
        SAPLING_MAP.put(DateSaplingKey,         new SaplingData(Material.OAK_SAPLING,    "枣树树苗"));
        SAPLING_MAP.put(PersimmonSaplingKey,    new SaplingData(Material.OAK_SAPLING,    "柿子树树苗"));
        SAPLING_MAP.put(MangosteenSaplingKey,   new SaplingData(Material.OAK_SAPLING,    "山竹树苗"));
        SAPLING_MAP.put(PomegranateSaplingKey,  new SaplingData(Material.OAK_SAPLING,    "石榴树树苗"));
        SAPLING_MAP.put(ChestnutSaplingKey,     new SaplingData(Material.OAK_SAPLING,    "栗子树树苗"));
        SAPLING_MAP.put(LonganSaplingKey,       new SaplingData(Material.OAK_SAPLING,    "龙眼树树苗"));
        SAPLING_MAP.put(LycheeSaplingKey,       new SaplingData(Material.OAK_SAPLING,    "荔枝树树苗"));
        SAPLING_MAP.put(CherrySaplingKey,       new SaplingData(Material.OAK_SAPLING,    "樱桃树树苗"));
        SAPLING_MAP.put(PeachSaplingKey,        new SaplingData(Material.OAK_SAPLING,    "桃树树苗"));
        SAPLING_MAP.put(PlumSaplingKey,         new SaplingData(Material.OAK_SAPLING,    "李子树树苗"));
        SAPLING_MAP.put(HazelnutSaplingKey,     new SaplingData(Material.OAK_SAPLING,    "榛子树树苗"));
        SAPLING_MAP.put(CoconutSaplingKey,      new SaplingData(Material.OAK_SAPLING,    "椰子树树苗"));
        SAPLING_MAP.put(OrangeSaplingKey,       new SaplingData(Material.OAK_SAPLING,    "橙子树树苗"));
        SAPLING_MAP.put(TangerineSaplingKey,    new SaplingData(Material.OAK_SAPLING,    "橘子树树苗"));
        SAPLING_MAP.put(BigAppleSaplingKey,     new SaplingData(Material.OAK_SAPLING,    "苹果树苗"));
        // 草本植物: 保留各自类型用于采摘后重植
        SAPLING_MAP.put(CherryTomatoSaplingKey, new SaplingData(Material.OAK_SAPLING,    "圣女果种子"));
        SAPLING_MAP.put(TomatoSaplingKey,       new SaplingData(Material.OAK_SAPLING,    "西红柿种子"));
        SAPLING_MAP.put(GrapeSaplingKey,        new SaplingData(Material.OAK_SAPLING,    "葡萄藤苗"));
        SAPLING_MAP.put(KiwiSaplingKey,         new SaplingData(Material.OAK_SAPLING,    "猕猴桃藤苗"));
        SAPLING_MAP.put(PineappleSaplingKey,    new SaplingData(Material.OAK_SAPLING,    "菠萝树苗"));
        SAPLING_MAP.put(StrawberrySaplingKey,   new SaplingData(Material.OAK_SAPLING,    "草莓种子"));
        SAPLING_MAP.put(BlueberrySaplingKey,    new SaplingData(Material.OAK_SAPLING,    "蓝莓种子"));
    }

    private record SaplingData(Material saplingType, String displayName) {}

    // 判断是否为草本(直接种植为灌木，无需骨粉)
    static boolean isHerbaceous(NamespacedKey key) {
        return HERBACEOUS_KEYS.contains(key.getKey());
    }

    @EventHandler
    public void onCustomSaplingPlant(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItem();
        if (item == null) return;
        Block clicked = event.getClickedBlock();
        if (clicked == null) return;
        Material type = clicked.getType();
        if (type != Material.DIRT && type != Material.GRASS_BLOCK && type != Material.PODZOL && type != Material.FARMLAND) return;

        for (Map.Entry<NamespacedKey, SaplingData> entry : SAPLING_MAP.entrySet()) {
            if (ArmsorEnchant.getEnchantLevel(item, entry.getKey()) == 0) continue;

            event.setCancelled(true);
            Player player = event.getPlayer();
            if (player.getGameMode() != org.bukkit.GameMode.CREATIVE) item.setAmount(item.getAmount() - 1);
            Block above = clicked.getRelative(BlockFace.UP);
            if (!above.isEmpty()) return;

            if (isHerbaceous(entry.getKey())) {
                // ExoticGarden风格: 草本植物直接种植为灌木（树叶），立即可采摘
                above.setType(Material.OAK_LEAVES);
                above.setMetadata(TreeListeners.SAPLING_METADATA, new FixedMetadataValue(getplugin, entry.getKey().getKey()));
                above.setMetadata("ArmsorPlus_HerbLeaf", new FixedMetadataValue(getplugin, entry.getKey().getKey()));
            } else {
                // 木本果树: 种植为原版树苗，需要生长后结果
                above.setType(entry.getValue().saplingType());
                above.setMetadata(TreeListeners.SAPLING_METADATA, new FixedMetadataValue(getplugin, entry.getKey().getKey()));
            }
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GRASS_PLACE, 1.0f, 1.0f);
            player.sendActionBar("§a种下了" + entry.getValue().displayName());
            return;
        }
    }

    // ========================================================================
    // 食物续: 功能性食物
    // ========================================================================

    @EventHandler
    public void onSweetBerryPieUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, SweetBerryPieKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 9));
        player.setSaturation(Math.min(20, player.getSaturation() + 8f));
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 8, 0.3, 0.3, 0.3, 0.05);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 0.8f, 1.2f);
        player.sendActionBar("§d已食用甜浆果派");
    }

    @EventHandler
    public void onRottenJerkyUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, RottenJerkyKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 4));
        player.setSaturation(Math.min(20, player.getSaturation() + 3f));
        player.getWorld().spawnParticle(Particle.CRIT, player.getLocation().add(0, 1, 0), 3, 0.2, 0.2, 0.2, 0.02);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 0.6f, 0.8f);
        player.sendActionBar("§8已食用腐肉干");
    }

    @EventHandler
    public void onWineBarrelUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, WineBarrelKey)) return;
        Player player = event.getPlayer();

        for (int i = 0; i < 9; i++) {
            double roll = RANDOM.nextDouble();
            int wineTier;
            if (roll < 0.001)       wineTier = 3;
            else if (roll < 0.1)    wineTier = 2;
            else                    wineTier = 1;

            ItemStack wine = FoodItems.Wine(1, wineTier);
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(wine);
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), wine);
            }
        }
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BARREL_OPEN, 1.0f, 1.0f);
        player.sendMessage("§6桶盖打开，9瓶美酒已收入背包！");
    }

    @EventHandler
    public void onWineDrink(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null) return;
        int tier = ArmsorEnchant.getEnchantLevel(item, WineKey);
        if (tier == 0) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE) {
            item.setAmount(item.getAmount() - 1);
        }

        switch (tier) {
            case 3:
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 2800, 4));
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
                player.sendMessage("§6🍶 金樽清酒！力量 V 140秒！死亡可复活一次！");
                player.setMetadata("WineRevive", new FixedMetadataValue(getplugin, true));
                // 140秒后复活机会自动失效 (与力量持续时间一致)
                Bukkit.getScheduler().runTaskLater(getplugin, () -> {
                    if (player.hasMetadata("WineRevive")) {
                        player.removeMetadata("WineRevive", getplugin);
                        player.sendMessage("§7金樽清酒的复活效果已消失");
                    }
                }, 2800L);
                break;
            case 2:
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 900, 2));
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 1.5f);
                player.sendMessage("§e佳酿 力量 III 45秒");
                break;
            default:
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 600, 1));
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 0.8f, 1.0f);
                player.sendMessage("§7酒 力量 II 30秒");
                break;
        }
        player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0);
    }

    // ========================================================================
    // 金樽清酒复活 (死亡时消耗一次复活机会)
    // ========================================================================

    // 玩家死亡时, 若身上有金樽清酒的复活标记则取消死亡并回满血
    @EventHandler
    public void onWineReviveDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!player.hasMetadata("WineRevive")) return;

        player.removeMetadata("WineRevive", getplugin); // 消耗一次复活机会
        event.setCancelled(true);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setFireTicks(0);

        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING,
                player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.5);
        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.8f, 1.2f);
        player.sendMessage("§6🍶 金樽清酒生效！你成功复活了一次！");
    }

    // ========================================================================
    // 15种新食物 (0.3I+)
    // ========================================================================

    @EventHandler
    public void onBurgerUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, BurgerKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 8));
        player.setSaturation(Math.min(20, player.getSaturation() + 6.0f));
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 8, 0.3, 0.3, 0.3, 0);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 0.8f, 1.0f);
        player.sendActionBar("§6已享用汉堡");
    }

    @EventHandler
    public void onHotDogUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, HotDogKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 6));
        player.setSaturation(Math.min(20, player.getSaturation() + 5.0f));
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 6, 0.3, 0.3, 0.3, 0);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 0.8f, 1.0f);
        player.sendActionBar("§6已享用热狗");
    }

    @EventHandler
    public void onChiliUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, ChiliKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 3));
        player.setSaturation(Math.min(20, player.getSaturation() + 2.0f));
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 3, 0.3, 0.3, 0.3, 0);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 0.8f, 1.0f);
        player.sendActionBar("§6已食用辣椒");
    }

    @EventHandler
    public void onOnionUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, OnionKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 3));
        player.setSaturation(Math.min(20, player.getSaturation() + 2.0f));
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 3, 0.3, 0.3, 0.3, 0);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 0.8f, 1.0f);
        player.sendActionBar("§6已食用洋葱");
    }

    @EventHandler
    public void onCabbageUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, CabbageKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 4));
        player.setSaturation(Math.min(20, player.getSaturation() + 3.0f));
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 4, 0.3, 0.3, 0.3, 0);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 0.8f, 1.0f);
        player.sendActionBar("§6已食用卷心菜");
    }

    @EventHandler
    public void onPoopUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, PoopKey)) return;
        Player player = event.getPlayer();
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + 1));
        player.setSaturation(Math.min(20, player.getSaturation() + 1.0f));
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.HUNGER, 200, 0));
        player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation().add(0, 1, 0), 8, 0.3, 0.3, 0.3, 0);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 0.8f, 1.0f);
        player.sendActionBar("§c你为什么要吃这个？！");
    }

    @EventHandler
    public void onGoldShieldElixirUse(PlayerInteractEvent event) {
        if (!consumeIfFood(event, GoldShieldElixirKey)) return;
        Player player = event.getPlayer();
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.ABSORPTION, 12 * 20, 9, false, true));
        player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.8f, 1.0f);
        player.sendActionBar("§6已食用金盾丹 §e伤害吸收X 12秒");
    }
}
