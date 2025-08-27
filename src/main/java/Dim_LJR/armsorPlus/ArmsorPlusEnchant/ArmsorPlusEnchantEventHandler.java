package Dim_LJR.armsorPlus.ArmsorPlusEnchant;


import Dim_LJR.armsorPlus.ArmsorPlus;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static Dim_LJR.armsorPlus.ArmsorItem.*;
import static Dim_LJR.armsorPlus.ArmsorItem.ArrowSpeed_EnchantdeBook;
import static Dim_LJR.armsorPlus.ArmsorItem.Blocking_EnchantedBook;
import static Dim_LJR.armsorPlus.ArmsorItem.BloodSacrifice_EnchantdeBook;
import static Dim_LJR.armsorPlus.ArmsorItem.DoubleHit_EnchantdeBook;
import static Dim_LJR.armsorPlus.ArmsorItem.EffectClear_EnchantdeBook;
import static Dim_LJR.armsorPlus.ArmsorItem.ExplosiveArrow_EnchantedBook;
import static Dim_LJR.armsorPlus.ArmsorItem.Freeze_EnchantedBook;
import static Dim_LJR.armsorPlus.ArmsorItem.HealthBoost_EnchantedBook;
import static Dim_LJR.armsorPlus.ArmsorItem.Revenge_EnchantedBook;
import static Dim_LJR.armsorPlus.ArmsorItem.Ripples_EnchantdeBook;
import static Dim_LJR.armsorPlus.ArmsorItem.ShadowDodge_EnchantdeBook;
import static Dim_LJR.armsorPlus.ArmsorItem.Survivor_EnchantedBook;
import static Dim_LJR.armsorPlus.ArmsorItem.Withering_EnchantedBook;
import static Dim_LJR.armsorPlus.ArmsorPlusEnchant.ArmsorEnchant.addEnchantLore;
import static Dim_LJR.armsorPlus.NamespaceKey.Keys.*;
import static org.bukkit.Material.*;
import static org.bukkit.Material.BOOK;

public class ArmsorPlusEnchantEventHandler implements Listener {
    public boolean percent(int x)//百分率随机器
    {
        Random r =new Random();
        int rand = r.nextInt(101);
        if(x >= rand)
            return true;
        else return false;
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
            velocity.multiply(5 * ArmsorEnchant.getEnchantLevel(livingEntity.getActiveItem(),Sniping));
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
            firework.setMetadata("ExplosiveArrow", new FixedMetadataValue(new ArmsorPlus(),level));

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
                false, // 不产生火焰
                false  // 不破坏方块
        );

        // 对周围生物造成伤害
        double damage = 30.0 * level;
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
                Victims.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * level, 1));
                Victims.sendActionBar(ChatColor.AQUA + "你被敌人施加了寒冻");
                if (event.getDamager() instanceof Player)
                    ((Player) event.getDamager()).sendActionBar(ChatColor.AQUA + "你对敌人施加了寒冻");
                return;
            }
            else {
                ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * level, 1));
                if (event.getDamager() instanceof Player)
                    ((Player) event.getDamager()).sendActionBar(ChatColor.AQUA + "你对敌人施加了寒冻");
                return;
            }
        }
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
        double maxhealth = ((LivingEntity) event.getEntity()).getMaxHealth();
        double health = ((LivingEntity) event.getEntity()).getHealth() + level - event.getDamage();
        if(health >= maxhealth)
        {
            health = maxhealth;
            event.setDamage(0);
            ((LivingEntity) event.getEntity()).setHealth(health);
        }
        else if(level > event.getDamage())
        {
            event.setDamage(0);
            ((LivingEntity) event.getEntity()).setHealth(health);
        }
        else {
            event.setDamage(event.getDamage() - level);
        }
        if(event.getEntity() instanceof Player)
        {
            event.getEntity().sendMessage(ChatColor.BLUE + "涟漪恢复了" + level + "点生命值");
        }
    }
    @EventHandler
    public void DoubleHit(EntityDamageByEntityEvent event)//双重打击监听器
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
    public void FeedingEnchant(EntityDamageByEntityEvent event)//吸血监听器
    {
        if(event.getEntity().equals(event.getDamager()))
            return;
        if(event.getEntity() instanceof LivingEntity && event.getDamager() instanceof LivingEntity)//如果二者不是生命体直接退出
        {
            int level=ArmsorEnchant.getEnchantLevel(((LivingEntity) event.getDamager()).getEquipment().getItemInMainHand(),Feedingkey);
            if(level==0)
                return;
            if(!percent(20 * level))
                return;
            double Entity = ((LivingEntity) event.getEntity()).getHealth()-0.5;
            if(Entity<0)
                ((LivingEntity) event.getEntity()).setHealth(0);
            else
                ((LivingEntity) event.getEntity()).setHealth(((LivingEntity) event.getEntity()).getHealth()-0.5);
            if((((LivingEntity) event.getDamager()).getHealth()+0.5) > ((LivingEntity) event.getDamager()).getMaxHealth())
                ((LivingEntity) event.getDamager()).setHealth(((LivingEntity) event.getDamager()).getMaxHealth());
            else
                ((LivingEntity) event.getDamager()).setHealth(((LivingEntity) event.getDamager()).getHealth()+0.5);
            if (event.getDamager() instanceof Player)
                event.getDamager().sendMessage(ChatColor.RED + "你对敌人施加了吸血");
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
        int goal = Amount;
        while (c <= goal) {
            if(goal >= 3 && percent(10)) {
                player.getInventory().addItem(Sniping_EnchantdeBook(1, r.nextInt(3) + 1));
                player.sendMessage(ChatColor.LIGHT_PURPLE + "获得狙击附魔书");
                c++;
            }
            if (percent(10)) {
                player.getInventory().addItem(Famine_EnchantdeBook(1, r.nextInt(3) + 1));
                player.sendMessage(ChatColor.GREEN + "获得饥荒附魔书");
                c++;
            } if (percent(10)) {
                player.getInventory().addItem(Dodge_EnchantdeBook(1, r.nextInt(4) + 1));
                player.sendMessage(ChatColor.GOLD + "获得闪避附魔书");
                c++;
            } if (percent(10)) {
                player.getInventory().addItem(Ripples_EnchantdeBook(1, r.nextInt(3) + 1));
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
                player.getInventory().addItem(Blocking_EnchantedBook(1, r.nextInt(5) + 1));
                player.sendMessage(ChatColor.AQUA + "获得格挡附魔书");
                c++;
            } if (percent(10)) {
                player.getInventory().addItem(Survivor_EnchantedBook(1, level_m5));
                player.sendMessage(ChatColor.GOLD + "获得幸存附魔书");
                c++;
            }if (percent(10)) {
                player.getInventory().addItem(ShadowDodge_EnchantdeBook(1, r.nextInt(5) + 1));
                player.sendMessage(ChatColor.DARK_PURPLE + "获得影避附魔书");
                c++;
            }
            if (percent(10)) {
                player.getInventory().addItem(ArrowSpeed_EnchantdeBook(1, r.nextInt(5) + 1));
                player.sendMessage(ChatColor.GOLD + "获得弹道附魔书");
                c++;
            }
            if (percent(10)) {
                player.getInventory().addItem(DoubleHit_EnchantdeBook(1, r.nextInt(5) + 1));
                player.sendMessage(ChatColor.GOLD + "获得双重打击附魔书");
                c++;
            }
            if(percent(10)) {
                player.getInventory().addItem(Feeding_EnchantdeBook(1, r.nextInt(5) + 1));
                player.sendMessage(ChatColor.GOLD + "获得吸血附魔书");
                c++;
            }
            Amount--;
        }
        player.sendMessage("获得数量:" + c);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 30, 0.5, 1, 0.5, 0.2);
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
}
