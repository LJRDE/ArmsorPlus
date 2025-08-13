package Dim_LJR.armsorPlus;
//前言:
//此插件完全开源但不可用于商业用途
//此版本是原版本重制版
//解决了原版本只有上帝才能看懂的bug
//原生Minecraft版本为paper1.20
//但是1.13~1.21应该都可以用
//变量名类名尽量使用驼峰命名法并且使用英文
//毕竟用拼音容易看不懂
//ArmsorEnchant.addEnchant(ItemStack item,NameSpace key,int level)方法添加自定义附魔
import com.sun.jdi.Bootstrap;
import io.papermc.paper.enchantments.EnchantmentRarity;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.Vector;
import org.codehaus.plexus.interpolation.SingleResponseValueSource;
import org.jetbrains.annotations.NotNull;
import Dim_LJR.armsorPlus.Command.ArmsorPlusCommand;
import org.jetbrains.annotations.Nullable;
import net.kyori.adventure.key.Key;

import javax.lang.model.element.Name;
import java.beans.PersistenceDelegate;
import java.util.Random;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.stream.Collectors;
import Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant;
import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;


public final class ArmsorPlus extends JavaPlugin implements Listener {
    private void loadconfing() {
    }

    private boolean IfArms(ItemStack item) {//判断是否武器
        Material type = item.getType();
        return type.name().endsWith("_SWORD") ||
                type.name().endsWith("_AXE") ||
                type == TRIDENT;
    }
    private boolean IfArmor(ItemStack item) {//判断是否护甲
        Material type = item.getType();
        return type.name().endsWith("_HELMET") ||
                type.name().endsWith("_CHESTPLATE") ||
                type.name().endsWith("_LEGGINGS") ||
                type.name().endsWith("_BOOTS");
    }
    private boolean IfHelmet(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_HELMET");
    }
    private boolean IfChestplate(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_CHESTPLATE");
    }
    private boolean IfLeggings(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_LEGGINGS");
    }
    private boolean Ifboots(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_BOOTS");
    }
    private boolean IfArmsor(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_SWORD") ||
                type.name().endsWith("_AXE") ||
                type == TRIDENT ||
                type.name().endsWith("_HELMET") ||
                type.name().endsWith("_CHESTPLATE") ||
                type.name().endsWith("_LEGGINGS") ||
                type.name().endsWith("_BOOTS") ||
                type == BOW;
    }
    private boolean IsInt(String args){
        for(int i = args.length();--i >= 0;){
            if (!Character.isDigit(args.charAt(i)))
                return false;
        }
        return true;
    }
    @EventHandler
    public void ArrowSpeedHander(ProjectileLaunchEvent event)//弹道，狙击监听器
    {
        if(!(event.getEntity() instanceof Arrow))
            return;
        Arrow arrow = (Arrow) event.getEntity();
        if(!(arrow.getShooter() instanceof LivingEntity))
            return;
        LivingEntity livingEntity = (LivingEntity) arrow.getShooter();
        int level = ArmsorEnchant.getEnchantLevel(livingEntity.getActiveItem(),ArrowSpeed);//获得等级
        if(level == 0) return;
        Vector velocity = arrow.getVelocity();
        velocity.multiply(level);
        if(ArmsorEnchant.getEnchantLevel(livingEntity.getActiveItem(),Sniping)!=0)
            velocity.multiply(10 * ArmsorEnchant.getEnchantLevel(livingEntity.getActiveItem(),Sniping));
        arrow.setVelocity(velocity);
    }
    @EventHandler
    public void ShadowDodgeHandler(EntityDamageEvent event)//影避监听器
    {
        if(event.getEntity() instanceof LivingEntity){
            int level = ArmsorEnchant.getEnchantLevel(((LivingEntity) event.getEntity()).getEquipment().getBoots(),ShadowDodge);
            if(level==0) return;
            if(!percent(6 * level))
                return;
            event.setCancelled(true);
            if(event.getEntity() instanceof Player)
                Particle.PORTAL.builder().location(event.getEntity().getLocation())
                        .offset(0.1,0.1,0.1)
                        .count(96)
                        .receivers(32,true)
                        .spawn();
            ((Player) event.getEntity()).sendMessage(ChatColor.DARK_PURPLE + "你影避了伤害");
        }
    }
    @EventHandler
    public void ExplosiveArrowHandler(EntityShootBowEvent event) //蓄爆监听器
    {
        if(!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        ItemStack bow = event.getBow();

        if(bow == null)
            return;
        int level = ArmsorEnchant.getEnchantLevel(bow, ExplosiveArrowKey);
        if(level <= 0)
            return;
        // 30%概率触发蓄爆效果
        if(Math.random() < 0.3) {
            // 取消原箭矢
            event.setCancelled(true);
            // 创建烟花火箭
            Firework firework = (Firework) player.getWorld().spawnEntity(
                    player.getLocation(),
                    EntityType.FIREWORK
            );

            // 设置烟花属性
            FireworkMeta meta = firework.getFireworkMeta();
            FireworkEffect effect = FireworkEffect.builder()
                    .withColor(Color.RED)
                    .withFade(Color.ORANGE)
                    .with(FireworkEffect.Type.BURST)
                    .withTrail()
                    .build();
            meta.addEffect(effect);
            meta.setPower(1);
            firework.setFireworkMeta(meta);

            // 设置飞行方向和速度
            @NotNull Vector velocity = player.getLocation().getDirection().multiply(event.getForce() * 3.0);
            firework.setVelocity(velocity);

            // 添加自定义数据用于爆炸伤害
            firework.setMetadata("ExplosiveArrow", new FixedMetadataValue(this, level));

            // 特效和声音
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
            player.sendMessage(ChatColor.GOLD + "蓄爆效果触发！发射了烟花火箭");
        }
    }
    @EventHandler
    public void ExplosiveArrowExplode(FireworkExplodeEvent event) //蓄爆烟花监听器
    {
        Firework firework = event.getEntity();

        if(!firework.hasMetadata("ExplosiveArrow"))
            return;

        int level = firework.getMetadata("ExplosiveArrow").get(0).asInt();
        Location location = firework.getLocation();

        // 创建爆炸效果
        location.getWorld().createExplosion(
                location.getX(),
                location.getY(),
                location.getZ(),
                2.0f, // 爆炸范围
                false, // 不破坏方块
                true   // 产生火焰
        );

        // 对周围生物造成伤害
        double damage = 10.0 * level;
        for(Entity entity : location.getWorld().getNearbyEntities(location, 3, 3, 3)) {
            if(entity instanceof LivingEntity && entity != firework) {
                ((LivingEntity) entity).damage(damage);
            }
        }
    }
    @EventHandler (priority = EventPriority.LOW)
    public void RevengeHandler(EntityDamageByEntityEvent event)//复仇监听器
    {
        if(!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        ItemStack chestplate = player.getEquipment().getChestplate();

        if(chestplate == null)
            return;

        int level = ArmsorEnchant.getEnchantLevel(chestplate, RevengeKey);
        if(level <= 0)
            return;

        // 20% * level 概率触发复仇效果
        double chance = 0.2 * level;
        if(Math.random() < chance) {
            // 反弹伤害
            double damage = event.getDamage();
            Entity damager = event.getDamager();

            if(damager instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) damager;

                // 反弹伤害（原伤害的50%）
                double revengeDamage = damage * 0.5;
                target.damage(revengeDamage, player);

                // 通知玩家
                player.sendMessage(ChatColor.RED + "复仇效果反弹了" + String.format("%.1f", revengeDamage) + "点伤害！");

                // 如果攻击者是玩家，也通知攻击者
                if(target instanceof Player) {
                    Player attacker = (Player) target;
                    attacker.sendMessage(ChatColor.RED + player.getName() + "的复仇效果反弹了你的攻击！");
                }

                // 特效
                player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR,
                        target.getLocation().add(0, 1, 0),
                        20, 0.5, 0.5, 0.5);
            }
        }
    }
    @EventHandler
    public void WitheringHandler(EntityDamageByEntityEvent event)//凋零附魔监听器
    {
        // 获取攻击者
        Entity damager = event.getDamager();
        // 获取被攻击者
        LivingEntity target = (event.getEntity() instanceof LivingEntity) ? (LivingEntity) event.getEntity() : null;

        if (target == null)
            return;

        // 获取攻击者持有的武器（如果有）
        ItemStack weapon = null;
        if (damager instanceof Player) {
            weapon = ((Player) damager).getInventory().getItemInMainHand();
        } else if (damager instanceof LivingEntity) {
            LivingEntity livingDamager = (LivingEntity) damager;
            weapon = livingDamager.getEquipment().getItemInMainHand();
        }

        // 检查武器是否有凋零附魔
        if (weapon == null || weapon.getType() == Material.AIR)
            return;

        int level = ArmsorEnchant.getEnchantLevel(weapon, WitheringKey);
        if (level <= 0)
            return;

        // 计算凋零效果参数
        int duration = level * 5 * 20;  // level*5秒（转换为ticks）
        int effectLevel = Math.min(level, 2); // 效果等级上限为II（原版凋零最高II级）

        // 应用凋零效果
        target.addPotionEffect(new PotionEffect(
                PotionEffectType.WITHER,
                duration,
                effectLevel,
                false,  // 没有粒子效果（减少性能开销）
                true    // 显示图标（如果目标是玩家）
        ));

        // 通知系统
        String effectMsg = "施加了凋零" + romanNumeral(effectLevel + 1) + "（时长：" + (level * 5) + "秒）";

        // 通知攻击者（如果是玩家）
        if (damager instanceof Player) {
            Player attacker = (Player) damager;
            attacker.sendMessage(ChatColor.GOLD + "你的武器" + effectMsg);
        }

        // 通知被攻击者（如果是玩家）
        if (target instanceof Player) {
            Player victim = (Player) target;
            victim.sendMessage(ChatColor.RED + "你被" + getEntityName(damager) + effectMsg);
        }
    }
    @EventHandler
    public void BlockingHandler(EntityDamageEvent event)//格挡附魔监听器
    {
        if(!(event.getEntity() instanceof LivingEntity))
            return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        EntityEquipment equipment = entity.getEquipment();
        int totalLevel = ArmsorEnchant.getEnchantLevel(equipment.getHelmet(), BlockingKey);
        if(totalLevel <= 0)
            return;
        double originalDamage = event.getDamage();
        double finalDamage = originalDamage;
        // 应用格挡效果
        if(originalDamage > 120) {
            // 伤害>120时格挡75%
            finalDamage *= 0.25; // 只承受25%的伤害
        } else if(originalDamage > 80) {
            // 伤害>80时格挡60%
            finalDamage *= 0.4; // 只承受40%的伤害
        } else {
            // 正常情况下格挡40%
            finalDamage *= 0.6; // 只承受60%的伤害
        }
        event.setDamage(finalDamage);
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            double blocked = originalDamage - finalDamage;
            player.sendActionBar(ChatColor.BLUE + "格挡效果减免了" + String.format("%.1f", blocked) + "点伤害");
        }
    }
    @EventHandler
    public void FreezeHandler(EntityDamageByEntityEvent event)//寒冻监听器
    {
        LivingEntity Damager;
        Player Victims;
        if(event.getEntity() instanceof LivingEntity && event.getDamager() instanceof LivingEntity)
        {
            Damager = (LivingEntity) event.getDamager();
            int level = ArmsorEnchant.getEnchantLevel(Damager.getEquipment().getItemInMainHand(),FreezeKey);
            if(level==0)
                return;
            if(event.getEntity() instanceof Player) {
                Victims = (Player) event.getEntity();
                Victims.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * level, 4 * level));
                Victims.sendActionBar(ChatColor.AQUA + "你被敌人施加了寒冻");
                if (event.getDamager() instanceof Player)
                    ((Player) event.getDamager()).sendActionBar(ChatColor.AQUA + "你对敌人施加了寒冻");
                return;
            }
            else {
                ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * level, 4 * level));
                if (event.getDamager() instanceof Player)
                    ((Player) event.getDamager()).sendActionBar(ChatColor.AQUA + "你对敌人施加了寒冻");
                return;
            }
        }
    }
    @EventHandler
    public void onMagicDamage(EntityDamageEvent event)//涤魂监听器
    {
        // 只处理玩家受到的伤害
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        UUID playerId = player.getUniqueId();
        ItemStack item = player.getInventory().getChestplate();
        if(ArmsorEnchant.getEnchantLevel(item,EffectClear)<=0)
            return;
        // 检查是否是魔法伤害
        if (!isMagicDamage(event.getCause())) return;

        // 检查冷却状态
        long currentTime = System.currentTimeMillis();
        Long lastImmuneTime = cooldowns.get(playerId);
        int level = ArmsorEnchant.getEnchantLevel(item,EffectClear);
        long COOLDOWN_TIME = 6 - level;
        if (lastImmuneTime == null || (currentTime - lastImmuneTime) >= COOLDOWN_TIME * 1000) {
            // 免疫伤害并更新冷却时间
            event.setCancelled(true);
            cooldowns.put(playerId, currentTime);

            // 播放免疫效果
            player.playSound(player.getLocation(),
                    org.bukkit.Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.5f);
            player.spawnParticle(org.bukkit.Particle.ENCHANTMENT_TABLE,
                    player.getLocation(), 20, 0.5, 0.5, 0.5);

            player.sendActionBar("§b[涤魂]魔法伤害免疫 §7(冷却中)");
        } else {
            // 计算剩余冷却时间
            long remaining = COOLDOWN_TIME - (currentTime - lastImmuneTime);
            player.sendActionBar("§c[涤魂]魔法免疫冷却中 §7(" + (remaining / 1000) + "秒)");
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnBeaten(EntityDamageByEntityEvent event)//闪避监听器0.2-b+小砍了一手
    {
        Player playerdamage,player;
        LivingEntity entitydamage,entity;
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
            player = (Player) event.getEntity();
            playerdamage = (Player) event.getDamager();
            ItemStack boot = player.getInventory().getBoots();//获取靴子物品
            if (boot != null && ArmsorEnchant.getEnchantLevel(boot, Dodgekey) != 0) {
                int level = ArmsorEnchant.getEnchantLevel(boot, Dodgekey);
                if(!percent(15 * level))
                    return;
                event.setCancelled(true);
                Particle.CRIT_MAGIC.builder().location(player.getLocation())
                        .offset(0.1, 0.1, 0.1)
                        .count(48)
                        .receivers(32, true)
                        .spawn();
                player.sendActionBar(ChatColor.GOLD + "你闪避了对方的伤害");
                playerdamage.sendActionBar(ChatColor.GOLD + "对方闪避了你的伤害");
            }
        }
        else if (event.getEntity() instanceof Player && event.getDamager() instanceof LivingEntity)
        {
            player = (Player) event.getEntity();
            entitydamage = (LivingEntity) event.getDamager();
            ItemStack boot = player.getInventory().getBoots();//获取靴子物品
            if (boot != null && ArmsorEnchant.getEnchantLevel(boot, Dodgekey) != 0) {
                int level = ArmsorEnchant.getEnchantLevel(boot, Dodgekey);
                if(!percent(20 * level))
                    return;
                event.setCancelled(true);
                Particle.PORTAL.builder().location(player.getLocation())
                        .offset(0.1,0.1,0.1)
                        .count(64)
                        .receivers(32,true)
                        .spawn();
                player.sendActionBar(ChatColor.GOLD + "你闪避了对方的伤害");
            }
            else return;
        }
        else if(event.getEntity() instanceof LivingEntity){
            entity = (LivingEntity) event.getEntity();
            ItemStack boot = entity.getEquipment().getBoots();//获取靴子物品
            if (boot != null && ArmsorEnchant.getEnchantLevel(boot, Dodgekey) != 0) {
                int level = ArmsorEnchant.getEnchantLevel(boot, Dodgekey);
                if(!percent(20 * level))
                    return;
                event.setCancelled(true);
                Particle.PORTAL.builder().location(entity.getLocation())
                        .offset(0.1,0.1,0.1)
                        .count(64)
                        .receivers(32,true)
                        .spawn();
            }
            else return;
        }
    }
    @EventHandler
    public void RipplesHander(EntityDamageByEntityEvent event)//涟漪监听器
    {
        if(!(event.getEntity() instanceof LivingEntity))
            return;
        int level = ArmsorEnchant.getEnchantLevel(((LivingEntity) event.getEntity()).getEquipment().getBoots(),RipplesProtectkey);
        if(level<=0)
            return;
        double damage = event.getDamage();
        double maxhealth = ((LivingEntity) event.getEntity()).getMaxHealth();
        double health = ((LivingEntity) event.getEntity()).getHealth() + level * 3 - event.getDamage();
        if(health >= maxhealth)
        {
            health = maxhealth;
            event.setDamage(0);
            ((LivingEntity) event.getEntity()).setHealth(health);
        }
        else if(level * 3 > event.getDamage())
        {
            event.setDamage(0);
            ((LivingEntity) event.getEntity()).setHealth(health);
        }
        else {
            event.setDamage(event.getDamage() - 3 * level);
        }
        if(event.getEntity() instanceof Player)
        {
            event.getEntity().sendMessage(ChatColor.BLUE + "涟漪恢复了" + level * 3 + "点生命值");
        }
    }
    @EventHandler
    public void DoubleHit(EntityDamageByEntityEvent event)
    {
        if(event.getEntity().equals(event.getDamager()))
            return;
        if(event.getEntity() instanceof LivingEntity && event.getDamager() instanceof LivingEntity)//如果二者不是生命体直接退出
        {
            int level=ArmsorEnchant.getEnchantLevel(((LivingEntity) event.getDamager()).getEquipment().getItemInMainHand(),DoubleHitkey);
            if(level==0)
                return;
            if(!percent(20 * level))
                return;
            event.setDamage(event.getDamage() * 2);
            if(event.getDamager() instanceof Player)
                event.getDamager().sendMessage("你发动了" + ChatColor.RED + "双重打击" + ChatColor.RESET + "对对方造成" + event.getDamage() + "点伤害");
            if(event.getEntity() instanceof Player)
                event.getEntity().sendMessage("对方发动了" + ChatColor.RED + "双重打击" + ChatColor.RESET + "对你造成" + event.getDamage() + "点伤害");
        }
    }
    @EventHandler
    public void OnDamageEntity(EntityDamageByEntityEvent event)//当一个实体伤害另一个实体时判定(血祭监听器)-0.2-b重写
    {
        if(event.getEntity().equals(event.getDamager()))
            return;
        if(event.getEntity() instanceof LivingEntity && event.getDamager() instanceof LivingEntity)//如果二者不是生命体直接退出
        {
            int level=ArmsorEnchant.getEnchantLevel(((LivingEntity) event.getDamager()).getEquipment().getItemInMainHand(),BloodSacrificekey);
            if(level==0)
                return;
            if(!percent(20 * level))
                return;
            Random r = new Random();
            int rate = r.nextInt(level) + 2;
            event.setDamage(event.getDamage() * rate);
            ((LivingEntity) event.getDamager()).damage(15,null);
            if(event.getDamager() instanceof Player)
                event.getDamager().sendMessage("你发动了" + ChatColor.RED + "血祭" + ChatColor.RESET + "对对方造成" + rate + "倍伤害");
            if(event.getEntity() instanceof Player)
                event.getEntity().sendMessage("对方发动了" + ChatColor.RED + "血祭" + ChatColor.RESET + "对你造成" + rate + "倍伤害");
        }
    }
    @EventHandler
    public void FamineHandler(EntityDamageByEntityEvent event)//饥荒监听器
    {
        LivingEntity Damager;
        Player Victims;
        if(event.getEntity() instanceof LivingEntity && event.getDamager() instanceof LivingEntity)
        {
        Damager = (LivingEntity) event.getDamager();
        int level = ArmsorEnchant.getEnchantLevel(Damager.getEquipment().getItemInMainHand(),Faminekey);
        if(level==0)
            return;
        if(event.getEntity() instanceof Player) {
            Victims = (Player) event.getEntity();
            Victims.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 40 * level, 4 * level));
            Victims.sendMessage(ChatColor.GREEN + "你被敌人施加了饥荒");
            if (event.getDamager() instanceof Player)
                event.getDamager().sendMessage(ChatColor.GREEN + "你对敌人施加了饥荒");
            return;
        }
        if (event.getDamager() instanceof Player)
            event.getDamager().sendMessage(ChatColor.GREEN + "你对敌人施加了饥荒");
        }
    }
    @EventHandler
    public void IfUSEMagicBallEvent(PlayerInteractEvent event)//抽奖系统，改回来了
    {
        if(event.getAction().isLeftClick())
            return;//左键不生效

        if(event.getItem() == null) return;
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        int Amount = ArmsorEnchant.getEnchantLevel(item, MagicBallKey);
        if(Amount == 0)
            return;

        Random r = new Random(System.currentTimeMillis());
        int level_m10 = r.nextInt(10) + 1;
        int level_m5 = r.nextInt(5) + 1;
        int level_m4 = r.nextInt(4) + 1;
        int level_m3 = r.nextInt(3) + 1;
        int c = 0;
        item.setAmount(item.getAmount() - 1);
        event.setCancelled(true);

        while (Amount >= 0) {
            if(Amount >= 3 && percent(10)) {
                player.getInventory().addItem(Sniping_EnchantdeBook(1, level_m3));
                player.sendMessage(ChatColor.LIGHT_PURPLE + "获得狙击附魔书");
                c++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Famine_EnchantdeBook(1, level_m3));
                player.sendMessage(ChatColor.GREEN + "获得饥荒附魔书");
                 c++;
            } if (percent(10)) {
                player.getInventory().addItem(Dodge_EnchantdeBook(1, level_m4));
                player.sendMessage(ChatColor.GOLD + "获得闪避附魔书");
                 c++;
            } if (percent(10)) {
                player.getInventory().addItem(Ripples_EnchantdeBook(1, level_m3));
                player.sendMessage(ChatColor.BLUE + "获得涟漪附魔书");
                 c++;
            } if (percent(10)) {
                player.getInventory().addItem(BloodSacrifice_EnchantdeBook(1, level_m3));
                player.sendMessage(ChatColor.DARK_RED + "获得血祭附魔书");
                 c++;
            } if (percent(10)) {
                player.getInventory().addItem(EffectClear_EnchantdeBook(1, level_m5));
                player.sendMessage(ChatColor.WHITE + "获得涤魂附魔书");
                 c++;
            } if (percent(10)) {
                player.getInventory().addItem(Freeze_EnchantedBook(1, level_m3));
                player.sendMessage(ChatColor.AQUA + "获得寒冻附魔书");
                 c++;
            } if (percent(10)) {
                player.getInventory().addItem(Revenge_EnchantedBook(1, level_m3));
                player.sendMessage(ChatColor.DARK_RED + "获得复仇附魔书");
                 c++;
            } if (percent(10)) {
                player.getInventory().addItem(HealthBoost_EnchantedBook(1, level_m4));
                player.sendMessage(ChatColor.RED + "获得生命提升附魔书");
                 c++;
            } if (percent(10)) {
                player.getInventory().addItem(ExplosiveArrow_EnchantedBook(1, level_m3));
                player.sendMessage(ChatColor.YELLOW + "获得蓄爆附魔书");
                 c++;
            } if (percent(10)) {
                player.getInventory().addItem(Withering_EnchantedBook(1, level_m5));
                player.sendMessage(ChatColor.DARK_PURPLE + "获得凋零附魔书");
                 c++;
            } if (percent(10)) {
                player.getInventory().addItem(Blocking_EnchantedBook(1, level_m5));
                player.sendMessage(ChatColor.AQUA + "获得格挡附魔书");
                 c++;
            } if (percent(10)) {
                player.getInventory().addItem(Survivor_EnchantedBook(1, level_m5));
                player.sendMessage(ChatColor.GOLD + "获得幸存附魔书");
                 c++;
            }if (percent(10)) {
                player.getInventory().addItem(ShadowDodge_EnchantdeBook(1, level_m5));
                player.sendMessage(ChatColor.DARK_PURPLE + "获得影避附魔书");
                c++;
            }
            if (percent(10)) {
                player.getInventory().addItem(ArrowSpeed_EnchantdeBook(1, level_m5));
                player.sendMessage(ChatColor.GOLD + "获得弹道附魔书");
                c++;
            }
            if (percent(10)) {
                player.getInventory().addItem(DoubleHit_EnchantdeBook(1, level_m5));
                player.sendMessage(ChatColor.GOLD + "获得双重打击附魔书");
                c++;
            }
            Amount--;
        }
        player.sendMessage("获得数量:" + c);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 30, 0.5, 1, 0.5, 0.2);
    }
    @EventHandler
    public void onPlayerInteract(@NotNull InventoryClickEvent event)//当玩家点击鼠标时(强化石/精炼金刚石/魔法附魔书监听器)
    {
        if(event.getCursor() == null ||event.getCurrentItem() == null ) return;
        ItemStack consum = event.getCursor();//所拖动的物品
        ItemStack item = event.getCurrentItem();//指向的物品
        Player player = (Player) event.getWhoClicked();
        ItemMeta meta = item.getItemMeta();
        ItemMeta consummeta = consum.getItemMeta();
        double ArmsValue;
        double ArmorValue;
        AttributeModifier mod;
        if (consummeta == null)
            return;
        String name = consum.getItemMeta().getDisplayName();//获取拖动物品名

        if (consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,Armskey)==1 && IfArms(item))
        {
            //如果是一级强化石,此方法存在BUG可能可以使用同名物品强化，以后应该加上二重验证机制-已完善
            event.setCancelled(true);
            meta.addEnchant(Enchantment.DAMAGE_ALL, meta.getEnchantLevel(Enchantment.DAMAGE_ALL) + 1, true);
            consum.setAmount(consum.getAmount() - 1);
            player.sendMessage(net.md_5.bungee.api.ChatColor.BLUE + "武器强化成功");
            item.setItemMeta(meta);
            player.spawnParticle(Particle.ENCHANTMENT_TABLE,player.getLocation(),96,0.75,0.75,1);
        }//一级武器强化石
        else if (consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,Armskey)==2 && IfArms(item))
        {//武器强化石II
            event.setCancelled(true);
            if(!meta.hasAttributeModifiers()){
                ArmsValue = GetArmsorLevel(item);
                meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
                        new AttributeModifier(UUID.randomUUID(),
                                "arms",ArmsValue + 1,AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlot.HAND));
            }
            else {
                ArmsValue = meta.getAttributeModifiers().get(Attribute.GENERIC_ATTACK_DAMAGE).stream()
                        .mapToDouble(AttributeModifier::getAmount)
                        .sum();
                meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
                meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
                        new AttributeModifier(UUID.randomUUID(),
                                "arms", ArmsValue + 1,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlot.HAND));
            }
            consum.setAmount(consum.getAmount() - 1);
            player.sendMessage(net.md_5.bungee.api.ChatColor.BLUE + "武器强化成功");
            item.setItemMeta(meta);
            player.spawnParticle(Particle.ENCHANTMENT_TABLE,player.getLocation(),96,0.75,0.75,1);
        }//二级武器强化石
        else if (consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,Armorkey)==1 && IfArmor(item))
        {
            event.setCancelled(true);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, meta.getEnchantLevel(Enchantment.PROTECTION_ENVIRONMENTAL) + 1,true);
            consum.setAmount(consum.getAmount() - 1);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "护甲强化成功");
            item.setItemMeta(meta);
            player.spawnParticle(Particle.ENCHANTMENT_TABLE,player.getLocation(),96,0.75,0.75,1);
        }//一级护甲强化石
        else if (consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,Armorkey)==2 && IfArmor(item))
        {//护甲强化石II
            event.setCancelled(true);
            Material type = item.getType();
            if(!meta.hasAttributeModifiers()) {
                if (type.name().endsWith("_HELMET")) {//头盔
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", GetArmsorLevel(item) + 1,//护甲值
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.HEAD));
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", 5,//护甲韧性
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.HEAD));
                } else if (type.name().endsWith("_CHESTPLATE")) {//胸甲
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", GetArmsorLevel(item) + 1,//护甲值
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.CHEST));
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", 5,//盔甲韧性
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.CHEST));
                } else if (type.name().endsWith("_LEGGINGS")) {//裤腿
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", GetArmsorLevel(item) + 1,//护甲值
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.LEGS));
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", 5,//盔甲韧性
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.LEGS));
                } else if (type.name().endsWith("_BOOTS")) {//鞋子
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", GetArmsorLevel(item) + 1,//护甲值
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.FEET));
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,//盔甲韧性
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", 5,
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.FEET));
                }
            }
            else {
                ArmorValue = meta.getAttributeModifiers().get(Attribute.GENERIC_ARMOR).stream()
                        .mapToDouble(AttributeModifier::getAmount)
                        .sum();//原护甲值
                meta.removeAttributeModifier(Attribute.GENERIC_ARMOR);
                if (type.name().endsWith("_HELMET")) {
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", ArmorValue + 1,
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.HEAD));
                } else if (type.name().endsWith("_CHESTPLATE")) {
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", ArmorValue + 1,
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.CHEST));
                } else if (type.name().endsWith("_LEGGINGS")) {
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", ArmorValue + 1,
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.LEGS));
                } else if (type.name().endsWith("_BOOTS")) {
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR,
                            new AttributeModifier(UUID.randomUUID(),
                                    "armor", ArmorValue + 1,
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.FEET));
                }

        }
            consum.setAmount(consum.getAmount() - 1);
            player.sendMessage(net.md_5.bungee.api.ChatColor.BLUE + "护甲强化成功");
            item.setItemMeta(meta);
            player.spawnParticle(Particle.ENCHANTMENT_TABLE,player.getLocation(),96,0.75,0.75,1);
        }//二级护甲强化石
        else if (consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,Bowkey)!=0 && item.getType() == BOW)
        {//当名字是弓强化石时
            event.setCancelled(true);
            meta.addEnchant(Enchantment.ARROW_DAMAGE, meta.getEnchantLevel(Enchantment.ARROW_DAMAGE) + 1, true);
            consum.setAmount(consum.getAmount() - 1);
            player.sendMessage(ChatColor.BOLD + "弓强化成功");
            item.setItemMeta(meta);
            player.spawnParticle(Particle.ENCHANTMENT_TABLE,player.getLocation(),96,0.75,0.75,1);
        }//弓强化石
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,DiamondPluskey)!=0 && IfArmsor(item))
        {
            event.setCancelled(true);
            meta.setUnbreakable(true);
            consum.setAmount(consum.getAmount() - 1);
            player.sendMessage(ChatColor.BOLD + "金刚石强化成功");
            item.setItemMeta(meta);
            player.spawnParticle(Particle.ENCHANTMENT_TABLE,player.getLocation(),96,0.75,0.75,1);
        }//精炼金刚石
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,Dodgekey)!=0 && consum.getType().equals(BOOK))
        {
            if(!item.getType().name().endsWith("_BOOTS"))
                return;
            int level = 1;
            level = ArmsorEnchant.getEnchantLevel(consum,Dodgekey);
            event.setCancelled(true);
            player.sendMessage("正在附魔" + ChatColor.GOLD + "闪避..." + ChatColor.RESET + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,Dodgekey)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, Dodgekey, level);
            player.sendMessage("附魔成功，魔咒级别" + ArmsorEnchant.getEnchantLevel(item,Dodgekey));
            addEnchantLore(item,ChatColor.GOLD + "闪避",level , Dodgekey);
        }//闪避附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,Faminekey)!=0 && consum.getType().equals(BOOK))
        {
            if(!IfArms(item))
                return;
            event.setCancelled(true);
            int level = ArmsorEnchant.getEnchantLevel(consum,Faminekey);
            player.sendMessage("正在附魔饥荒..." + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,Faminekey)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            ArmsorEnchant.addEnchant(item,Faminekey,level);
            consum.setAmount(consum.getAmount() - 1);
            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item,Faminekey));
            addEnchantLore(item,ChatColor.GREEN + "饥荒",level ,Faminekey);
        }//饥荒附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,RipplesProtectkey)!=0 && consum.getType().equals(BOOK))
        {
            if(!item.getType().name().endsWith("_BOOTS"))
                return;
            event.setCancelled(true);
            int level = ArmsorEnchant.getEnchantLevel(consum,RipplesProtectkey);
            player.sendMessage("正在附魔" + ChatColor.BLUE + "涟漪" + ChatColor.RESET + "..." + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,RipplesProtectkey)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            ArmsorEnchant.addEnchant(item,RipplesProtectkey,level);
            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item,RipplesProtectkey));
            addEnchantLore(item,ChatColor.BLUE + "涟漪",level ,RipplesProtectkey);
            consum.setAmount(consum.getAmount() - 1);
        }//涟漪附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,BloodSacrificekey)!=0 && consum.getType().equals(BOOK))
        {
            if(!item.getType().name().endsWith("_SWORD"))
                return;
            event.setCancelled(true);
            int level = ArmsorEnchant.getEnchantLevel(consum,BloodSacrificekey);
            player.sendMessage("正在附魔" + ChatColor.RED + "血祭" + ChatColor.RESET + "..." + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,BloodSacrificekey)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item,BloodSacrificekey,level);
            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item,BloodSacrificekey));
            addEnchantLore(item,ChatColor.RED + "血祭",level ,BloodSacrificekey);
            consum.setAmount(consum.getAmount() - 1);
        }//血祭附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,EffectClear)!=0 && consum.getType().equals(BOOK))
        {
            if(!item.getType().name().endsWith("_CHESTPLATE"))
                return;
            event.setCancelled(true);
            int level = ArmsorEnchant.getEnchantLevel(consum,EffectClear);
            player.sendMessage("正在附魔" + ChatColor.WHITE + "涤魂" + ChatColor.RESET + "..." + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,EffectClear)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item,EffectClear,level);
            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item,EffectClear));
            addEnchantLore(item,ChatColor.WHITE + "涤魂",level ,EffectClear);
            consum.setAmount(consum.getAmount() - 1);
        }//涤魂附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,FreezeKey)!=0 && consum.getType().equals(BOOK))
        {
            if(!IfArms(item))
                return;
            event.setCancelled(true);
            int level = ArmsorEnchant.getEnchantLevel(consum,FreezeKey);
            player.sendMessage("正在附魔" + ChatColor.AQUA + "寒冻" + ChatColor.RESET + "..." + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,FreezeKey)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            ArmsorEnchant.addEnchant(item,FreezeKey,level);
            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item,FreezeKey));
            addEnchantLore(item,ChatColor.AQUA + "寒冻",level , FreezeKey);
            consum.setAmount(consum.getAmount() - 1);
        }//寒冻附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, BlockingKey) != 0 && consum.getType().equals(BOOK))
        {
            if(!IfHelmet(item))
                return;
            event.setCancelled(true);

            int level = ArmsorEnchant.getEnchantLevel(consum, BlockingKey);
            player.sendMessage("正在附魔" + ChatColor.AQUA + "格挡" + ChatColor.RESET + "..." + level + "级");

            if(ArmsorEnchant.getEnchantLevel(item, BlockingKey) >= level) {
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }

            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, BlockingKey, level);

            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item, BlockingKey));
            addEnchantLore(item, ChatColor.AQUA + "格挡", level ,BlockingKey);
        }//格挡附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, WitheringKey) != 0 && consum.getType().equals(BOOK))
        {
            if(!IfArms(item)) // 凋零需要武器而不是防具
                return;
            event.setCancelled(true);

            int level = ArmsorEnchant.getEnchantLevel(consum, WitheringKey);
            player.sendMessage("正在附魔" + ChatColor.DARK_PURPLE + "凋零" + ChatColor.RESET + "..." + level + "级");

            if(ArmsorEnchant.getEnchantLevel(item, WitheringKey) >= level) {
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }

            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, WitheringKey, level);

            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item, WitheringKey));
            addEnchantLore(item, ChatColor.DARK_PURPLE + "凋零", level , WitheringKey);
        }//凋零附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, SurvivorKey) != 0 && consum.getType().equals(BOOK))
        {
            // 幸存仅用于护腿(裤子)
            if(!IfLeggings(item))
                return;
            event.setCancelled(true);
            int level = ArmsorEnchant.getEnchantLevel(consum, SurvivorKey);
            player.sendMessage("正在附魔" + ChatColor.GOLD + "幸存" + ChatColor.RESET + "..." + level + "级");

            if(ArmsorEnchant.getEnchantLevel(item, SurvivorKey) >= level) {
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, SurvivorKey, level);

            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item, SurvivorKey));
            addEnchantLore(item, ChatColor.GOLD + "幸存", level ,SurvivorKey);
        }//幸存附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, RevengeKey) != 0 && consum.getType().equals(BOOK))
        {
            // 复仇仅用于胸甲
            if(!IfChestplate(item))
                return;
            event.setCancelled(true);
            int level = ArmsorEnchant.getEnchantLevel(consum, RevengeKey);
            player.sendMessage("正在附魔" + ChatColor.DARK_RED + "复仇" + ChatColor.RESET + "..." + level + "级");

            if(ArmsorEnchant.getEnchantLevel(item, RevengeKey) >= level) {
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, RevengeKey, level);

            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item, RevengeKey));
            addEnchantLore(item, ChatColor.DARK_RED + "复仇", level ,RevengeKey);
        }//复仇附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, HealthBoostKey) != 0 && consum.getType().equals(BOOK))
        {
            // 生命提升仅用于胸甲
            if(!IfChestplate(item))
                return;
            event.setCancelled(true);
            int level = ArmsorEnchant.getEnchantLevel(consum, HealthBoostKey);
            player.sendMessage("正在附魔" + ChatColor.RED + "生命提升" + ChatColor.RESET + "..." + level + "级");

            if(ArmsorEnchant.getEnchantLevel(item, HealthBoostKey) >= level) {
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, HealthBoostKey, level);
            Collection<AttributeModifier> modifiers = meta.getAttributeModifiers(Attribute.GENERIC_MAX_HEALTH);
            // 移除旧的同名修饰符
            if(modifiers != null) {
                for(AttributeModifier modifier : new ArrayList<>(modifiers)) {
                    if(modifier.getName().equals("HealthBoostEnchant")) {
                        meta.removeAttributeModifier(Attribute.GENERIC_MAX_HEALTH, modifier);
                    }
                }
            }
            // 添加新的修饰符
            double healthBoost = level * 5; // 每级+5生命
            AttributeModifier healthModifier = new AttributeModifier(
                    UUID.randomUUID(), // 唯一ID
                    "HealthBoostEnchant", // 名称
                    healthBoost, // 数值
                    AttributeModifier.Operation.ADD_NUMBER, // 操作类型
                    EquipmentSlot.CHEST // 装备位置
            );
            meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, healthModifier);
            item.setItemMeta(meta);
            player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item, HealthBoostKey));
            addEnchantLore(item, ChatColor.RED + "生命提升", level ,HealthBoostKey);

        }//生命提升附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum, ExplosiveArrowKey) != 0 && consum.getType().equals(BOOK))
        {
            // 蓄爆仅用于弓和弩
            if (item.getType().equals(Material.BOW) || item.getType().equals(CROSSBOW)) {
                event.setCancelled(true);
                int level = ArmsorEnchant.getEnchantLevel(consum, ExplosiveArrowKey);
                player.sendMessage("正在附魔" + ChatColor.YELLOW + "蓄爆" + ChatColor.RESET + "..." + level + "级");

                if (ArmsorEnchant.getEnchantLevel(item, ExplosiveArrowKey) >= level) {
                    player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                    return;
                }
                consum.setAmount(consum.getAmount() - 1);
                ArmsorEnchant.addEnchant(item, ExplosiveArrowKey, level);

                player.sendMessage("附魔成功,魔咒级别" + ArmsorEnchant.getEnchantLevel(item, ExplosiveArrowKey));
                addEnchantLore(item, ChatColor.YELLOW + "蓄爆", level , ExplosiveArrowKey);
            }
        }//蓄爆附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,ShadowDodge)!= 0 && consum.getType().equals(BOOK))
        {
            if(!item.getType().name().endsWith("_BOOTS"))
                return;
            int level = 1;
            level = ArmsorEnchant.getEnchantLevel(consum,ShadowDodge);
            event.setCancelled(true);
            player.sendMessage("正在附魔" + ChatColor.DARK_PURPLE + "影避..." + ChatColor.RESET + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,ShadowDodge)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, ShadowDodge, level);
            player.sendMessage("附魔成功，魔咒级别" + ArmsorEnchant.getEnchantLevel(item,ShadowDodge));
            addEnchantLore(item,ChatColor.DARK_PURPLE + "影避",level , ShadowDodge);
        }//影避附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,ArrowSpeed)!= 0 && consum.getType().equals(BOOK))
        {
            if (!(item.getType().equals(Material.BOW) || item.getType().equals(CROSSBOW)))
                return;
            int level = 1;
            level = ArmsorEnchant.getEnchantLevel(consum,ArrowSpeed);
            event.setCancelled(true);
            player.sendMessage("正在附魔" + ChatColor.GOLD + "弹道..." + ChatColor.RESET + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,ArrowSpeed)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, ArrowSpeed, level);
            player.sendMessage("附魔成功，魔咒级别" + ArmsorEnchant.getEnchantLevel(item,ArrowSpeed));
            addEnchantLore(item,ChatColor.GOLD + "弹道",level , ArrowSpeed);
        }//弹道附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,Sniping)!= 0 && consum.getType().equals(BOOK))
        {
            if (!(item.getType().equals(Material.BOW) || item.getType().equals(CROSSBOW)))
                return;
            int level = 1;
            level = ArmsorEnchant.getEnchantLevel(consum,Sniping);
            event.setCancelled(true);
            player.sendMessage("正在附魔" + ChatColor.GOLD + "狙击..." + ChatColor.RESET + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,Sniping)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, Sniping, level);
            player.sendMessage("附魔成功，魔咒级别" + ArmsorEnchant.getEnchantLevel(item,Sniping));
            addEnchantLore(item,ChatColor.LIGHT_PURPLE + "狙击",level , Sniping);
        }//狙击附魔书
        else if(consum.hasItemMeta() && ArmsorEnchant.getEnchantLevel(consum,DoubleHitkey)!= 0 && consum.getType().equals(BOOK))
        {
            if (!IfArms(item))
                return;
            int level = 1;
            level = ArmsorEnchant.getEnchantLevel(consum,DoubleHitkey);
            event.setCancelled(true);
            player.sendMessage("正在附魔" + ChatColor.GOLD + "双重打击..." + ChatColor.RESET + level + "级");
            if(ArmsorEnchant.getEnchantLevel(item,DoubleHitkey)>=level){
                player.sendMessage("附魔失败，原有该附魔等级高于或等于此附魔书");
                return;
            }
            consum.setAmount(consum.getAmount() - 1);
            ArmsorEnchant.addEnchant(item, DoubleHitkey, level);
            player.sendMessage("附魔成功，魔咒级别" + ArmsorEnchant.getEnchantLevel(item,DoubleHitkey));
            addEnchantLore(item,ChatColor.LIGHT_PURPLE + "双重打击",level , DoubleHitkey);
        }//双重打击附魔书
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void SurvivorHandler(EntityDamageEvent event)//幸存附魔监听器（仅护腿）
    {
        Random random = new Random();
        if(!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        // 仅检测护腿（裤子）的幸存附魔等级
        ItemStack leggings = player.getEquipment().getLeggings();
        int level = ArmsorEnchant.getEnchantLevel(leggings, SurvivorKey);
        if(level <= 0)
            return;
        // 计算本次伤害后的生命值
        double finalHealth = player.getHealth() - event.getFinalDamage();

        // 检查是否受到致命伤害
        if(finalHealth > 0)
            return;

        // 计算复活概率
        double chance = level * 0.1; // 每级10%几率
        if(random.nextDouble() < chance) {
            // 阻止死亡
            event.setCancelled(true);

            // 完全恢复玩家
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.setFireTicks(0);

            // 通知玩家
            player.sendMessage(ChatColor.GOLD + "护腿上的幸存效果触发！成功规避死亡");

            // 如果攻击者是玩家，也通知攻击者
            if(event instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
                if(damager instanceof Player) {
                    Player attacker = (Player) damager;
                    attacker.sendMessage(ChatColor.YELLOW + player.getName() + " 的幸存附魔触发，规避了致命伤害");
                }
            }

            // 添加特效
            player.getWorld().spawnParticle(Particle.TOTEM, player.getLocation().add(0, 1, 0),
                    30, 0.5, 0.5, 0.5, 0.5);
            player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.8f, 1.2f);

            // 添加3秒无敌
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.DAMAGE_RESISTANCE,
                    60, // 3秒(20tick/秒 * 3)
                    10, // 等级10提供100%伤害抗性
                    false, false
            ));
        }
    }
    @EventHandler
    public void StonePlace(BlockPlaceEvent event)//基础强化石监听器-防放置_包括一二级强化石
    {
        int x = ArmsorEnchant.getEnchantLevel(event.getItemInHand(),BasicStone) +
                ArmsorEnchant.getEnchantLevel(event.getItemInHand(),Armskey) +
                ArmsorEnchant.getEnchantLevel(event.getItemInHand(),Armorkey);
        if(x==0)
            return;
        event.setCancelled(true);
    }
    @EventHandler
    public void IfUSEBasicStone(PlayerInteractEvent event)//获得强化石
    {
        ItemStack item = event.getItem();
        if(ArmsorEnchant.getEnchantLevel(item,BasicStone)==0)
            return;
        item.setAmount(item.getAmount()-1);
        event.setCancelled(true);
        Random r = new Random();
        int i =r.nextInt(6);
        if(i==1 || i==0) {
            event.getPlayer().getInventory().addItem(ArmorPlusCreate(1));
            event.getPlayer().sendMessage("已获得一级护甲强化石");
        }
        else if(i==2){
            event.getPlayer().getInventory().addItem(ArmorPlusCreateII(1));
            event.getPlayer().sendMessage("已获得二级护甲强化石");
        }
        else if(i==3) {
            event.getPlayer().getInventory().addItem(ArmsPlusCreateI(1));
            event.getPlayer().sendMessage("已获得一级武器强化石");
        }
        else if(i==4){
            event.getPlayer().getInventory().addItem(ArmsPlusCreateII(1));
            event.getPlayer().sendMessage("已获得二级武器强化石");
        }
        else if(i==6){
            event.getPlayer().getInventory().addItem(BowPlusCreate(1));
            event.getPlayer().sendMessage("已获得一级弓箭强化石");
        }
    }
    @Override
    public void onEnable()  {
        regkey(this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ArmsorPlusMenu(),this);
        this.getCommand("ArmsorPlus").setExecutor(new ArmsorPlusCommand());//注册命令
        int RecipeAmount = 0;
        getLogger().info("ArmsorPlus 配方注册中...");
        //注册精炼金刚石的合成配方
        NamespacedKey DiamondPlusKey = new NamespacedKey(this,"ArmsorPlus_DiamondPlus");
        ItemStack DiamondPlusItem = DIAMONDPLUSCreate(1);
        ShapelessRecipe DiamondPlusRecipe =new ShapelessRecipe(DiamondPlusKey,DiamondPlusItem);
        DiamondPlusRecipe.addIngredient(9, DIAMOND_BLOCK);
        //注册血祭之剑的合成配方
        NamespacedKey BloodSwordkey = new NamespacedKey(this,"ArmsorPlus_BloodSword");
        ItemStack BloodSwordItem = BloodSword(1);
        ShapedRecipe BloodSwordRecipe =new ShapedRecipe(BloodSwordkey,BloodSwordItem);
        BloodSwordRecipe.shape(" A ","ABA"," A ");
        BloodSwordRecipe.setIngredient('A', REDSTONE);
        BloodSwordRecipe.setIngredient('B', SKELETON_SKULL);
        //注册向导书的合成配方
        NamespacedKey GuideRecipeKey = new NamespacedKey(this,"GuideRecipe");
        ItemStack GuideBook = GuideBook(1);
        ShapelessRecipe GuideRecipe = new ShapelessRecipe(GuideRecipeKey,GuideBook);
        GuideRecipe.addIngredient(1, COBBLESTONE);
        //注册基础强化石的合成配方
        NamespacedKey BasicStoneRecipeKey = new NamespacedKey(this,"StoneRecipe");
        ShapelessRecipe StoneRecipe = new ShapelessRecipe(BasicStoneRecipeKey,BasicStone(1));
        StoneRecipe.addIngredient(4, DIAMOND_BLOCK);

        getServer().addRecipe(StoneRecipe);
        RecipeAmount++;
        getServer().addRecipe(GuideRecipe);//添加合成配方
        RecipeAmount++;
        getServer().addRecipe(BloodSwordRecipe);//添加合成配方
        RecipeAmount++;
        getServer().addRecipe(DiamondPlusRecipe);//添加合成配方
        RecipeAmount++;
        getLogger().info("ArmsorPlus 配方注册完成 数量:" + RecipeAmount);
        //注册附魔
        getLogger().info("服务端类型" + Bukkit.getServer().getName());
        getLogger().info("Bukkit API 版本: " + Bukkit.getBukkitVersion());
    }

    @Override
    public void onDisable() {
        getLogger().info("ArmsorPlus 插件已禁用");
        HandlerList.unregisterAll();
    }

    private ArmsorType GetArmsorType(ItemStack item){
        if (item.getType().equals(DIAMOND_SWORD))
            return ArmsorType.Diamond_Sword;
        else if(item.getType().equals(IRON_SWORD))
            return ArmsorType.Iron_Sword;
        else if(item.getType().equals(GOLDEN_SWORD))
            return ArmsorType.Golden_Sword;
        else if(item.getType().equals(NETHERITE_SWORD))
            return ArmsorType.Netherite_Sword;
        else if(item.getType().equals(STONE_SWORD))
            return ArmsorType.Stone_Sword;
        else if(item.getType().equals(WOODEN_SWORD))
            return ArmsorType.Wooden_Sword;
        return ArmsorType.ERROR;
    }

    private int GetArmsorLevel(ItemStack item){//获得武器装备伤害，护甲值
        Material mate = item.getType();
        return switch (mate) {
            case DIAMOND_SWORD -> 7;
            case DIAMOND_AXE -> 9;
            case DIAMOND_HELMET -> 3;
            case DIAMOND_CHESTPLATE -> 8;
            case DIAMOND_LEGGINGS -> 6;
            case DIAMOND_BOOTS -> 3;
            case IRON_SWORD -> 6;
            case IRON_AXE -> 9;
            case IRON_HELMET -> 2;
            case IRON_CHESTPLATE -> 6;
            case IRON_LEGGINGS -> 5;
            case IRON_BOOTS -> 2;
            case GOLDEN_SWORD -> 6;
            case GOLDEN_AXE -> 9;
            case GOLDEN_HELMET -> 2;
            case GOLDEN_CHESTPLATE -> 6;
            case GOLDEN_LEGGINGS -> 5;
            case GOLDEN_BOOTS -> 2;
            case NETHERITE_SWORD -> 8;
            case NETHERITE_AXE -> 9;
            case NETHERITE_HELMET -> 3;
            case NETHERITE_CHESTPLATE -> 8;
            case NETHERITE_LEGGINGS -> 6;
            case NETHERITE_BOOTS -> 3;
            case WOODEN_SWORD -> 4;
            case WOODEN_AXE -> 6;
            case LEATHER_HELMET -> 1;
            case LEATHER_CHESTPLATE -> 4;
            case LEATHER_LEGGINGS -> 3;
            case LEATHER_BOOTS -> 2;
            case STONE_SWORD -> 5;
            case STONE_AXE -> 9;
            case CHAINMAIL_HELMET -> 2;
            case CHAINMAIL_CHESTPLATE -> 5;
            case CHAINMAIL_LEGGINGS -> 4;
            case CHAINMAIL_BOOTS -> 3;
            default -> 0;
        };
    }

    enum ArmsorType {
        ERROR,Diamond_Sword,Iron_Sword,Netherite_Sword,Golden_Sword,Stone_Sword,Wooden_Sword
    }

    private boolean percent(int x)//百分率随机器
    {
        Random r =new Random();
        int rand = r.nextInt(101);
        if(x >= rand)
            return true;
        else return false;
    }
    public boolean removeLoreLine(ItemStack item, String keyword)
    {
        if(item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if(!meta.hasLore()) return false;

        List<String> lore = meta.getLore();
        List<String> newLore = new ArrayList<>();
        boolean removed = false;

        String strippedKeyword = ChatColor.stripColor(keyword);

        for(String line : lore) {
            String strippedLine = ChatColor.stripColor(line);
            if(strippedLine.contains(strippedKeyword)) {
                removed = true;
            } else {
                newLore.add(line);
            }
        }

        if(removed) {
            meta.setLore(newLore);
            item.setItemMeta(meta);
            return true;
        }
        return false;
    }
    public void removeEnchantLore(ItemStack item, NamespacedKey key)
    {
        String displayName = getEnchantDisplayName(key);
        if(displayName != null) {
            removeLoreLine(item, displayName);
        }
    }
    private String getEnchantDisplayName(NamespacedKey key)
    {
        if(key.equals(FreezeKey))//1
            return "寒冻";
        else if(key.equals(Faminekey))//2
            return "饥荒";
        else if(key.equals(RevengeKey))
            return "复仇";
        else if(key.equals(WitheringKey))//3
            return "凋零";
        else if(key.equals(RipplesProtectkey))//4
            return "涟漪";
        else if(key.equals(Dodgekey))//5
            return "闪避";
        else if(key.equals(HealthBoostKey))//6
            return "生命提升";
        else if(key.equals(SurvivorKey))//7
            return "幸存";
        else if(key.equals(BloodSacrificekey))//8
            return "血祭";
        else if(key.equals(ExplosiveArrowKey))//9
            return "蓄爆";
        else if(key.equals(EffectClear))//10
            return "涤魂";
        else if(key.equals(BlockingKey))//11
            return "格挡";
        else if(key.equals(ShadowDodge))//12
            return "影避";
        else if(key.equals(ArrowSpeed))//13
            return "弹道";
        else if(key.equals(Sniping))//14
            return "狙击";
        else if(key.equals(DoubleHitkey))//15
            return "双重打击";
        else return null;
    }
    private ItemStack addEnchantLore(ItemStack item, String lore, int level,NamespacedKey key )//附魔添加lore
    {
        if (item == null || item.getType() == Material.AIR) return item;
        removeEnchantLore(item,key);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        // 生成新的 Lore 文本
        String newLore = generateEnchantLore( lore,level);
        // 获取当前 Lore
        List<String> loreList = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        loreList.add(newLore);
        // 设置回物品
        meta.setLore(loreList);
        item.setItemMeta(meta);
        return item;
    }
    private String generateEnchantLore(String baseLore, int level)
    {
        String levelStr;
        switch (level) {
            case 1: levelStr = "I"; break;
            case 2: levelStr = "II"; break;
            case 3: levelStr = "III"; break;
            case 4: levelStr = "IV"; break;
            case 5: levelStr = "V"; break;
            case 6: levelStr = "VI"; break;
            case 7: levelStr = "VII"; break;
            case 8: levelStr = "VIII"; break;
            case 9: levelStr = "IX"; break;
            case 10: levelStr = "X"; break;
            default: levelStr = String.valueOf(level);
        }
        return baseLore + levelStr;
    }
    // 定义魔法伤害类型
    private static final EntityDamageEvent.DamageCause[] MAGIC_DAMAGE_TYPES = {
            EntityDamageEvent.DamageCause.MAGIC,        // 魔法伤害（中毒、瞬间伤害等）
            EntityDamageEvent.DamageCause.POISON,       // 中毒
            EntityDamageEvent.DamageCause.WITHER,       // 凋零
            EntityDamageEvent.DamageCause.DRAGON_BREATH // 龙息
    };

    // 冷却时间记录（玩家UUID -> 上次免疫时间）
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    // 判断是否是魔法伤害
    private boolean isMagicDamage(EntityDamageEvent.DamageCause cause)
    {
        for (EntityDamageEvent.DamageCause magicType : MAGIC_DAMAGE_TYPES) {
            if (cause == magicType) {
                return true;
            }
        }
        return false;
    }


    // 辅助方法 - 将整数转换为罗马数字
    public static String romanNumeral(int num) {
        switch(num) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            case 6: return "VI";
            case 7: return "VII";
            case 8: return "VIII";
            case 9: return "IX";
            case 10: return "X";
            default: return String.valueOf(num);
        }
    }
    // 辅助方法 - 获取实体的可显示名称
    private String getEntityName(Entity entity) {
        if (entity instanceof Player) {
            return ((Player) entity).getName();
        } else {
            // 尝试获取自定义名称或默认名称
            return entity.getCustomName() != null ? entity.getCustomName() : entity.getName();
        }
    }
}