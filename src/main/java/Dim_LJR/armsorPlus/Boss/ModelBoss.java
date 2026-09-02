package Dim_LJR.armsorPlus.Boss;

import Dim_LJR.armsorPlus.NamespaceKey;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

// 玩家模型BOSS的"真实实体 + 视觉模型"载体 + 统一伤害系统。
//  - 隐形盔甲架: 真实服务端实体, 用作命中盒/物理本体。不引入完整A*寻路,
//    每tick由本类 setVelocity 朝目标推进(走原生物理/摩擦/碰撞), 附带简单避障:
//    被方块挡住时沿墙滑动、被围死或遇1格台阶时跳跃翻越。被玩家命中时不取消
//    伤害事件 → 原版近战/弓箭/爆炸击退全部生效, 盔甲架被击退/击飞;
//    原生血量由每tick重置吸收(永远不死)。
//  - 视觉模型(假玩家): 纯发包渲染, 全部封装在 BossRenderer(PacketEvents)里。
//    本类不直接依赖 PacketEvents —— 只持有一个可空的 BossRenderer 引用,
//    通过惰性方法调用委托 swing/hurt/rotate/teleport/despawn 等视觉操作。
//    PacketEvents 未安装时 renderer 为 null, Boss 退化为纯盔甲架本体(隐形),
//    伤害/击退仍走盔甲架命中盒, 完整兜底。
// 伤害检测两条路径都汇入本类统一的 health 字段 + applyDamage():
//   ① 近战/弓箭/爆炸/火焰 → 盔甲架真实命中盒触发 EntityDamage 事件 (RealEntityDamageListener)
//   ② 近战兜底 → BossRenderer 拦截玩家挥砍假玩家的包 (仅 PacketEvents 可用时)
public class ModelBoss implements FakePlayer {

    // 盔甲架实体UUID → BOSS载体 (真实伤害事件查表)
    private static final Map<UUID, ModelBoss> BY_BODY = new HashMap<>();

    // 攻击冷却跟踪 (computeDamage 用, 快速连点伤害衰减)
    private static final Map<UUID, Long> LAST_ATTACK = new HashMap<>();

    // 追击参数
    private static final double CHASE_SPEED = 0.15;              // 格/tick ≈ 3格/秒
    private static final double CHASE_STOP_DIST = 2.2;           // 贴到目标这么近就停
    private static final double KNOCKBACK_SPEED_THRESHOLD = 0.08; // 水平速度高于此=正在被击退, 不覆盖
    private static final double BODY_SCALE = 1.4;                // 放大命中盒, 严格包含玩家模型 → 射线优先命中盔甲架

    // 简单避障参数 (沿墙滑动 + 跳跃, 不引入完整A*)
    private static final double JUMP_VELOCITY = 0.5;        // 跳跃垂直速度(翻越1格台阶)
    private static final int JUMP_COOLDOWN_TICKS = 8;       // 跳跃冷却(防连跳刷屏)
    private static final int STUCK_TICKS = 4;               // 连续N tick位移过小=判定卡住
    private static final int[] SLIDE_ANGLES = {0, 45, -45, 90, -90, 135, -135, 180}; // 试探方向(相对目标, 度)

    public final ArmorStand body;        // 真实物理本体 (隐形, 玩家不可见, 可被命中/击退)
    public final World world;

    private BossRenderer renderer;       // 视觉渲染层 (PacketEvents可用时非空, 由BossRenderer.create回填)
    private final BukkitRunnable syncTask;
    private LivingEntity target;         // 追击目标 (由调用方 setTarget 设置, 任意生物)
    private int stuckTicks = 0;          // 连续卡住的tick计数
    private int jumpCooldown = 0;        // 跳跃冷却tick
    private Vector lastPos = null;       // 上一tick位置 (卡住检测用)

    // ========================================================================
    // 统一血量系统 (所有玩家模型BOSS共用)
    // ========================================================================

    private final double maxHealth;
    private double health;
    private boolean alive = true;

    // 回调(主插件通过 FakePlayer.setOn* 注入游戏逻辑: 算伤害/同步血条/掉落清理)
    private Consumer<Player> attackCallback;
    private BiConsumer<Player, Double> damageCallback;
    private Consumer<Player> deathCallback;

    // 生成配置
    public static class Options {
        public final double maxHealth;
        public Options(double maxHealth) { this.maxHealth = maxHealth; }
    }

    private ModelBoss(World world, Location loc, Options opt) {
        this.world = world;
        this.maxHealth = opt.maxHealth;
        this.health = opt.maxHealth;

        // ---- 隐形盔甲架 (真实物理本体: 命中盒 + 击退载体) ----
        // 不设 invulnerable: 必须让伤害事件触发, 才能拿到原版击退。
        body = world.spawn(loc, ArmorStand.class, a -> {
            a.setInvisible(true);
            a.setSilent(true);
            a.setSmall(false);      // 全尺寸
            a.setMarker(false);     // 必须非marker: marker没有命中盒
            a.setBasePlate(false);
            a.setArms(false);       // 无手臂: 攻击不会触发切换手臂
            a.setGravity(true);
            a.setInvulnerable(false); // 必须可被伤害: 命中触发原生击退
            a.setPersistent(true);
            a.setRemoveWhenFarAway(false);
            AttributeInstance mh = a.getAttribute(Attribute.MAX_HEALTH);
            if (mh != null) { mh.setBaseValue(1024); a.setHealth(1024); } // 原生血量缓冲(每tick重置)
            AttributeInstance scale = a.getAttribute(Attribute.SCALE);
            if (scale != null) scale.setBaseValue(BODY_SCALE);
        });
        BY_BODY.put(body.getUniqueId(), this);

        // ---- 每tick任务(实体tick前运行): 追击 + 血量重置 + 无敌帧 ----
        // 追击必须在实体tick前 setVelocity, 速度才会在本tick生效;
        // 假玩家位置同步移到 BossRenderer.TickEndSyncListener (tick末尾, 每2tick一次)。
        syncTask = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (body == null || body.isDead() || !body.isValid()) {
                        cancel();
                        return;
                    }

                    // 每次tick重置原生血量 + 无敌帧:
                    // 让每一击都注册(击退/受击反馈), 但盔甲架原生永远不死。
                    AttributeInstance mh = body.getAttribute(Attribute.MAX_HEALTH);
                    if (mh != null && body.getHealth() < mh.getValue()) {
                        body.setHealth(mh.getValue());
                    }
                    body.setNoDamageTicks(0);

                    // 追击目标 (setVelocity推进, 不覆盖正在播放的击退)
                    chase();
                } catch (Throwable t) {
                    // 任务绝不能被异常打死, 否则盔甲架停摆
                    NamespaceKey.Keys.getplugin.getLogger().warning("[玩家模型Boss] tick任务异常(已忽略): " + t);
                }
            }
        };
        syncTask.runTaskTimer(NamespaceKey.Keys.getplugin, 1L, 1L);
    }

    // 由 BossRenderer.create 回填视觉渲染层 (仅 PacketEvents 可用时调用)
    public void setRenderer(BossRenderer r) { this.renderer = r; }

    // 生成载体: 失败返回null (已记录日志)
    public static ModelBoss spawn(World world, Location loc, Options opt) {
        try {
            return new ModelBoss(world, loc, opt);
        } catch (Throwable t) {
            NamespaceKey.Keys.getplugin.getLogger().warning("[玩家模型Boss] 生成失败: " + t);
            t.printStackTrace();
            return null;
        }
    }

    // ========================================================================
    // 追击 (setVelocity 驱动, 原版物理/摩擦/击退 + 简单避障)
    // ========================================================================

    private void chase() {
        LivingEntity t = target;
        if (t == null || t.isDead() || !t.isValid() || !t.getWorld().equals(world)) {
            stuckTicks = 0;
            lastPos = null;
            return;
        }

        Location bl = body.getLocation();
        Location tl = t.getLocation();
        double dx = tl.getX() - bl.getX();
        double dz = tl.getZ() - bl.getZ();
        double hDist = Math.sqrt(dx * dx + dz * dz);

        Vector vel = body.getVelocity();
        double hSpeed = Math.sqrt(vel.getX() * vel.getX() + vel.getZ() * vel.getZ());

        if (jumpCooldown > 0) jumpCooldown--;

        // 正在被击退: 不覆盖, 让原版击退自然衰减到阈值以下再恢复追击
        if (hSpeed >= KNOCKBACK_SPEED_THRESHOLD) {
            stuckTicks = 0;
            lastPos = bl.toVector();
            return;
        }

        if (hDist <= CHASE_STOP_DIST) {
            // 已贴近目标: 停住(保留垂直速度, 让落地/击飞动画自然播放)
            body.setVelocity(new Vector(0, vel.getY(), 0));
            stuckTicks = 0;
            lastPos = bl.toVector();
            return;
        }

        double len = Math.max(1.0E-4, hDist);
        double nx = dx / len;   // 朝目标的水平单位向量
        double nz = dz / len;

        // ---- 卡住检测: 贴地持续朝目标推进, 水平位移却接近0 → 判定被方块挡住 ----
        boolean stuck = false;
        if (lastPos != null && body.isOnGround()) {
            double mx = bl.getX() - lastPos.getX();
            double mz = bl.getZ() - lastPos.getZ();
            double moved = Math.sqrt(mx * mx + mz * mz);
            if (moved < 0.02) {
                if (++stuckTicks >= STUCK_TICKS) stuck = true;
            } else {
                stuckTicks = 0;
            }
        }
        lastPos = bl.toVector();

        double vx = nx * CHASE_SPEED;
        double vz = nz * CHASE_SPEED;
        double vy = vel.getY();

        if (stuck) {
            stuckTicks = 0;
            // 沿墙滑动: 从目标方向出发试探两侧, 取第一个不被挡的方向
            Vector dir = findPassableDir(nx, nz, bl);
            if (dir != null) {
                vx = dir.getX() * CHASE_SPEED;
                vz = dir.getZ() * CHASE_SPEED;
            } else {
                vx = 0;
                vz = 0;
            }

            // 跳跃翻越: 被围死(dir==null), 或正前方只有1格台阶(脚部挡、头顶空)
            boolean stepAhead = isFeetBlocked(bl, nx, nz) && !isHeadBlocked(bl, nx, nz);
            if (body.isOnGround() && jumpCooldown <= 0 && (dir == null || stepAhead)) {
                vy = JUMP_VELOCITY;
                jumpCooldown = JUMP_COOLDOWN_TICKS;
            }
        }

        body.setVelocity(new Vector(vx, vy, vz));
        body.setRotation((float) Math.toDegrees(Math.atan2(-dx, dz)), 0);
    }

    // 从目标方向出发, 按 SLIDE_ANGLES 顺序旋转试探, 返回第一个不被方块挡住的前进方向(水平单位向量)。
    // 全被挡(被围住)返回 null。
    private Vector findPassableDir(double nx, double nz, Location bl) {
        for (int deg : SLIDE_ANGLES) {
            double rad = Math.toRadians(deg);
            double cos = Math.cos(rad), sin = Math.sin(rad);
            double rx = nx * cos - nz * sin;
            double rz = nx * sin + nz * cos;
            if (!isFeetBlocked(bl, rx, rz) && !isHeadBlocked(bl, rx, rz)) {
                return new Vector(rx, 0, rz);
            }
        }
        return null;
    }

    // 正前方一格的脚部方块是否实心(挡路)
    private boolean isFeetBlocked(Location from, double sx, double sz) {
        return from.getWorld().getBlockAt(
                (int) Math.floor(from.getX() + sx),
                (int) Math.floor(from.getY()),
                (int) Math.floor(from.getZ() + sz)).getType().isSolid();
    }

    // 正前方一格的身体(头顶)方块是否实心(挡路)
    private boolean isHeadBlocked(Location from, double sx, double sz) {
        return from.getWorld().getBlockAt(
                (int) Math.floor(from.getX() + sx),
                (int) Math.floor(from.getY() + 1),
                (int) Math.floor(from.getZ() + sz)).getType().isSolid();
    }

    // ========================================================================
    // 统一伤害入口
    // ========================================================================

    @Override public double getHealth() { return health; }
    @Override public double getMaxHealth() { return maxHealth; }
    @Override public boolean isAlive() { return alive; }

    // 所有玩家模型BOSS伤害统一走这里:
    // 发包拦截(近战兜底)和真实实体伤害事件(近战/弓箭/爆炸/火焰)都汇入此方法。
    // 攻击者可能为null(爆炸/火焰等无直接来源)。
    public void applyDamage(Player attacker, double dmg) {
        applyDamage(attacker, dmg, false);
    }

    // melee=true 时手动施加近战击退:
    // 原版近战击退对盔甲架不生效 (ArmorStand.hurt 对 PLAYER_ATTACK 返回 false,
    // Player.attack 不会调用 target.knockback()), 必须手动 setVelocity。
    // 弓箭(Projectile)的击退由原版弹射物逻辑施加, 不传true, 避免重复击退。
    @Override
    public void applyDamage(Player attacker, double dmg, boolean melee) {
        if (!alive || dmg <= 0) return;
        health = Math.max(0, health - dmg);

        // 受击反馈 (假玩家受击动画 + 音效 + 粒子)
        hurt();
        Location loc = body.getLocation();
        world.playSound(loc, Sound.ENTITY_PLAYER_HURT, 0.6f, 0.9f);
        world.spawnParticle(Particle.DAMAGE_INDICATOR, loc.clone().add(0, 1.5, 0), 10, 0.5, 0.8, 0.5, 0.2);

        // 近战击退 (仅在击打后仍存活时施加, 死亡/消失的盔甲架不再受力)
        if (melee && attacker != null && attacker.isOnline() && !attacker.isDead() && isValid()) {
            body.setVelocity(safeKnockback(attacker.getLocation(), body.getLocation(), 0.45, 0.15));
        }

        // 通知主插件同步血条/menu (attacker, 剩余血量)
        if (damageCallback != null) damageCallback.accept(attacker, health);

        if (health <= 0) {
            alive = false;
            if (deathCallback != null) deathCallback.accept(attacker);
        }
    }

    // ========================================================================
    // 攻击检测 ②: 盔甲架真实命中盒触发的事件 (近战/弓箭/爆炸/火焰等)
    // ========================================================================

    public static class RealEntityDamageListener implements Listener {
        // 非实体来源中, 属于"玩家间接造成"并应扣BOSS血的伤害类型
        private static final EnumSet<EntityDamageEvent.DamageCause> CONVERT_NON_ENTITY = EnumSet.of(
                EntityDamageEvent.DamageCause.FIRE,
                EntityDamageEvent.DamageCause.FIRE_TICK,
                EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,
                EntityDamageEvent.DamageCause.BLOCK_EXPLOSION);

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onBossBodyDamage(EntityDamageEvent event) {
            ModelBoss boss = BY_BODY.get(event.getEntity().getUniqueId());
            if (boss == null || !boss.isAlive()) return;

            if (event instanceof EntityDamageByEntityEvent bye) {
                Entity damager = bye.getDamager();
                if (damager.equals(boss.body)) return; // BOSS自伤(技能波及)不扣自己

                Player attacker = resolveAttacker(damager);
                if (attacker == null) {
                    // 非玩家来源(生物近战/生物弹射物/TNT爆炸等): 结算伤害, 让怪物能反击玩家模型Boss。
                    boolean projectile = damager instanceof Projectile;
                    boss.applyDamage(null, Math.max(event.getDamage(), 1.0));
                    if (!projectile) event.setCancelled(true);
                    return;
                }
                // 玩家近战命中 → 取消事件: 原生血量永不掉, 盔甲架不会原生死亡/掉落盔甲架。
                // 近战(Player直击)原版击退对盔甲架不生效, 由 applyDamage 手动 setVelocity。
                // 弓箭保留事件: 走原版弹射物击退。
                boolean melee = damager instanceof Player;
                boss.applyDamage(attacker, Math.max(event.getDamage(), 1.0), melee);
                if (melee) event.setCancelled(true);
                return;
            }

            // 非实体伤害
            // 放行 /kill (generic_kill): 让盔甲架原生死亡, 由BOSS的AI检测 body 失效后 cleanup
            DamageSource ds = event.getDamageSource();
            if (ds != null && ds.getDamageType() == DamageType.GENERIC_KILL) return;
            event.setCancelled(true);
            if (CONVERT_NON_ENTITY.contains(event.getCause())) {
                // 爆炸击退在原版独立施加, 取消伤害也不影响击退
                boss.applyDamage(null, Math.max(event.getDamage(), 1.0));
            }
            // 坠落/溺水等其他环境伤害: 只取消, 不扣BOSS血
        }

        private static Player resolveAttacker(Entity damager) {
            if (damager instanceof Player p) return p;
            if (damager instanceof Projectile proj) {
                ProjectileSource src = proj.getShooter();
                if (src instanceof Player p) return p;
            }
            return null;
        }
    }

    // ========================================================================
    // 伤害计算 & 击退工具 (两个玩家模型BOSS共用)
    // ========================================================================

    // 近似原版伤害计算 (武器基础伤害 + 锋利 + 力量 + 暴击 + 攻击冷却)
    public static double computeDamage(Player p) {
        AttributeInstance atk = p.getAttribute(Attribute.ATTACK_DAMAGE);
        double base = atk != null ? atk.getValue() : 1.0;

        PotionEffect strength = p.getPotionEffect(PotionEffectType.STRENGTH);
        if (strength != null) base += 3.0 * (strength.getAmplifier() + 1);

        int sharp = p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.SHARPNESS);
        if (sharp > 0) base += 0.5 + 0.5 * sharp;

        if (isCriticalHit(p)) base *= 1.5;

        // 攻击冷却: 快速连点伤害衰减 (与原版 0.2 + t^2 * 0.8 一致)
        long now = System.currentTimeMillis();
        long last = LAST_ATTACK.getOrDefault(p.getUniqueId(), 0L);
        LAST_ATTACK.put(p.getUniqueId(), now);
        float progress = Math.min(1.0f, (now - last) / 1500.0f);
        base *= 0.2 + progress * progress * 0.8;

        return Math.max(1.0, base);
    }

    private static boolean isCriticalHit(Player p) {
        return p.getFallDistance() > 0 && !p.isOnGround() && !p.isInWater()
                && !p.isClimbing() && !p.isInsideVehicle();
    }

    // 安全的击退向量: 当玩家与Boss位置完全重合时向量长度为0, normalize() 会产生 NaN,
    // 导致 setVelocity 抛 IllegalArgumentException。重合时只保留垂直向上的击飞分量。
    public static Vector safeKnockback(Location from, Location to, double strength, double up) {
        Vector v = to.toVector().subtract(from.toVector());
        if (v.lengthSquared() < 1.0E-4) {
            return new Vector(0, up, 0);
        }
        return v.normalize().multiply(strength).setY(up);
    }

    // ========================================================================
    // 移动 / 目标 / 动画 (对盔甲架/假玩家的统一操作)
    // ========================================================================

    @Override public boolean isValid() {
        return body != null && !body.isDead() && body.isValid();
    }

    @Override public ArmorStand getBody() { return body; }

    // 设置/覆盖装备 (null=空槽, 全量覆盖)。ModelBoss 用盔甲架实体真实装备槽,
    // 视觉 + 伤害(attribute)都直接生效。
    @Override
    public void setEquipment(ItemStack helmet, ItemStack chestplate,
                             ItemStack leggings, ItemStack boots, ItemStack mainHand) {
        if (body == null || !body.isValid()) return;
        EntityEquipment eq = body.getEquipment();
        if (eq == null) return;
        eq.setItem(EquipmentSlot.HEAD, helmet);
        eq.setItem(EquipmentSlot.CHEST, chestplate);
        eq.setItem(EquipmentSlot.LEGS, leggings);
        eq.setItem(EquipmentSlot.FEET, boots);
        eq.setItem(EquipmentSlot.HAND, mainHand);
    }

    @Override public Location getLocation() { return body.getLocation(); }

    // 设置/清除追击目标 (由本类每tick setVelocity 推进)
    @Override public void setTarget(LivingEntity p) { this.target = p; }

    // AI 兜底: ModelBoss 固定为 WALK + NORMAL 现状行为, 不实现类型/等级分派。
    @Override public void setAiType(AiType type) { }
    @Override public AiType getAiType() { return AiType.WALK; }
    @Override public void setAiLevel(AiLevel level) { }
    @Override public AiLevel getAiLevel() { return AiLevel.NORMAL; }

    @Override public void swing() { if (renderer != null) renderer.swing(); }
    @Override public void hurt() { if (renderer != null) renderer.hurt(); }

    // 假玩家面向目标 (攻击前保证挥砍方向与朝向一致)
    @Override
    public void rotateTo(LivingEntity target) {
        Location bl = body.getLocation();
        Vector dir = target.getLocation().toVector().subtract(bl.toVector());
        float yaw = bl.getYaw();
        if (dir.lengthSquared() > 1.0E-4) {
            yaw = (float) Math.toDegrees(Math.atan2(-dir.getX(), dir.getZ()));
        }
        body.setRotation(yaw, 0); // 同步盔甲架朝向, 避免下次tick同步把朝向拉回追击方向
        if (renderer != null) renderer.rotate(yaw);
    }

    // 传送 (技能用): 真实体 + 视觉模型一起搬走
    @Override
    public void teleport(Location loc) {
        body.teleport(loc);
        body.setVelocity(new Vector(0, 0, 0)); // 清掉残余击退速度, 避免传送后继续滑动
        if (renderer != null) renderer.teleport(loc);
    }

    // 后加入玩家/换世界的玩家: 单独为TA生成/移除假玩家视觉
    @Override public void spawnTo(Player p) { if (renderer != null) renderer.spawnTo(p); }
    @Override public void despawnFrom(Player p) { if (renderer != null) renderer.despawnFrom(p); }

    // ========================================================================
    // 回调 (BossRenderer 拦截到玩家挥砍时回调; 主插件 setOn* 注入游戏逻辑)
    // ========================================================================

    @Override public void setOnAttack(Consumer<Player> cb) { this.attackCallback = cb; }
    @Override public void setOnDamage(BiConsumer<Player, Double> cb) { this.damageCallback = cb; }
    @Override public void setOnDeath(Consumer<Player> cb) { this.deathCallback = cb; }

    // BossRenderer.FakePlayerHitListener 拦截到玩家挥砍后调用 (主线程)
    public void notifyAttack(Player p) {
        if (attackCallback != null) attackCallback.accept(p);
    }

    @Override
    public void remove() {
        alive = false;
        if (syncTask != null) syncTask.cancel();
        if (body != null && !body.isDead()) {
            BY_BODY.remove(body.getUniqueId());
            body.remove();
        }
        if (renderer != null) renderer.despawn();
    }
}
