package Dim_LJR.armsorPlus.Boss;

import Dim_LJR.armsorPlus.NamespaceKey;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

// 玩家模型Boss的"视觉渲染 + 发包拦截"层, 全部依赖 PacketEvents。
// 本类只在 PacketEvents 已安装时才会被加载(经由 ArmsorPlus.initPacketEvents 里的调用)。
// 未安装时 ModelBoss 只会通过惰性的方法引用本类, 不会触发本类(及其 PacketEvents 依赖)的类加载。
// 皮肤构建已抽取到 BossSkin(纯逻辑, 无 PE 依赖)。
public class BossRenderer {

    // 假玩家实体ID → 渲染器 (发包攻击拦截查表)
    private static final Map<Integer, BossRenderer> BY_FAKE_ENTITY = new HashMap<>();
    // 所有活跃渲染器 (tick末尾同步遍历)
    private static final List<BossRenderer> ACTIVE = new ArrayList<>();

    private final ModelBoss boss;
    private final PacketFakePlayer fakePlayer;
    private final World world;
    private float lastSentYaw;
    private int drift;

    private BossRenderer(ModelBoss boss, PacketFakePlayer fakePlayer, World world, float yaw) {
        this.boss = boss;
        this.fakePlayer = fakePlayer;
        this.world = world;
        this.lastSentYaw = yaw;
    }

    // ========================================================================
    // 创建 (由 FakePlayerProvider 在 PacketEvents 可用时调用)
    // ========================================================================

    // skin 为已解析好的 BossSkin; 这里只负责转成 UserProfile 并发包渲染。
    public static BossRenderer createFor(ModelBoss boss, String bossName, String displayName,
                                         BossSkin skin, float yaw) {
        UUID fakeUuid = UUID.randomUUID();
        return create(boss, toProfile(fakeUuid, bossName, skin), displayName, yaw);
    }

    private static UserProfile toProfile(UUID fakeUuid, String bossName, BossSkin skin) {
        List<TextureProperty> textures = new ArrayList<>();
        if (skin != null && !skin.isEmpty()) {
            textures.add(new TextureProperty("textures", skin.value, skin.signature));
        }
        return new UserProfile(fakeUuid, bossName, textures);
    }

    private static BossRenderer create(ModelBoss boss, UserProfile profile, String displayName, float yaw) {
        Location loc = boss.getLocation();
        PacketFakePlayer fp = new PacketFakePlayer(100000 + new Random().nextInt(900000),
                UUID.randomUUID(), profile, displayName, boss.world.getName(),
                loc.getX(), loc.getY(), loc.getZ(), yaw, 0);
        BossRenderer renderer = new BossRenderer(boss, fp, boss.world, yaw);
        BY_FAKE_ENTITY.put(fp.getEntityId(), renderer);
        ACTIVE.add(renderer);
        boss.setRenderer(renderer); // 回填到 ModelBoss, 让 swing/hurt/rotate/teleport/despawn 委托到渲染层
        fp.spawnToWorld(boss.world);
        return renderer;
    }

    // ========================================================================
    // 实例渲染 (ModelBoss 委托到这里)
    // ========================================================================

    public void swing() { fakePlayer.swingToWorld(world); }
    public void hurt() { fakePlayer.hurtToWorld(world); }

    public void rotate(float yaw) {
        fakePlayer.rotateToWorld(world, yaw, 0, yaw);
    }

    public void teleport(Location loc) {
        float yaw = loc.getYaw();
        fakePlayer.teleportToWorld(world, loc.getX(), loc.getY(), loc.getZ(), yaw, 0, yaw);
        lastSentYaw = yaw;
    }

    public void spawnTo(Player p) { fakePlayer.spawn(p); }
    public void despawnFrom(Player p) { fakePlayer.despawn(p); }

    public void despawn() {
        ACTIVE.remove(this);
        BY_FAKE_ENTITY.remove(fakePlayer.getEntityId());
        fakePlayer.despawnToWorld(world);
    }

    // 实体tick结束后把假玩家同步到盔甲架位置/朝向 (与追踪器位置流对齐)
    private void sync() {
        Location bl = boss.getLocation();
        float byaw = bl.getYaw();

        double dx = bl.getX() - fakePlayer.getX();
        double dy = bl.getY() - fakePlayer.getY();
        double dz = bl.getZ() - fakePlayer.getZ();

        boolean bad = !Double.isFinite(dx) || !Double.isFinite(dy) || !Double.isFinite(dz);
        double distSq = dx * dx + dy * dy + dz * dz;
        if (bad || distSq > 16.0) {
            fakePlayer.teleportToWorld(world, bl.getX(), bl.getY(), bl.getZ(), byaw, 0, byaw);
            lastSentYaw = byaw;
            return;
        }

        if (++drift >= 50) {
            drift = 0;
            fakePlayer.teleportToWorld(world, bl.getX(), bl.getY(), bl.getZ(), byaw, 0, byaw);
            return;
        }

        boolean moved = Math.abs(dx) > 0.0005 || Math.abs(dy) > 0.0005 || Math.abs(dz) > 0.0005;
        boolean turned = Math.abs(byaw - lastSentYaw) > 0.1;

        if (moved) {
            fakePlayer.moveToWorld(world, dx, dy, dz, byaw, 0, byaw);
            lastSentYaw = byaw;
        } else if (turned) {
            fakePlayer.rotateToWorld(world, byaw, 0, byaw);
            lastSentYaw = byaw;
        }
    }

    // ========================================================================
    // 静态: 注册监听器 (PacketEvents 可用时由 ArmsorPlus 调用一次)
    // ========================================================================

    public static void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new TickEndSyncListener(), NamespaceKey.Keys.getplugin);
        PacketEvents.getAPI().getEventManager().registerListener(new FakePlayerHitListener());
    }

    // ========================================================================
    // 攻击检测: 拦截玩家挥砍假玩家的包 (近战兜底)
    // ========================================================================

    public static class FakePlayerHitListener extends SimplePacketListenerAbstract {
        public FakePlayerHitListener() {
            super(PacketListenerPriority.LOW);
        }

        @Override
        public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
            if (event.getPacketType() != PacketType.Play.Client.INTERACT_ENTITY) return;

            WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
            if (wrapper.getAction() != WrapperPlayClientInteractEntity.InteractAction.ATTACK) return;

            BossRenderer renderer = BY_FAKE_ENTITY.get(wrapper.getEntityId());
            if (renderer == null || !renderer.boss.isAlive()) return;

            event.setCancelled(true); // 服务端没有此实体, 必须取消避免异常

            Player player = event.getPlayer();
            if (player == null || player.isDead()) return;

            // Netty线程, 不能直接调用 Bukkit 主线程 API, 调度到主线程结算
            Bukkit.getScheduler().runTask(NamespaceKey.Keys.getplugin, () -> {
                ModelBoss b = renderer.boss;
                if (b.isAlive() && b.isValid() && player.isOnline() && !player.isDead()) {
                    b.notifyAttack(player); // 主插件在 setOnAttack 回调里算伤害并调 applyDamage()
                }
            });
        }
    }

    // ========================================================================
    // 实体tick全部结束后, 每2tick同步一次所有假玩家到盔甲架位置
    // ========================================================================

    public static class TickEndSyncListener implements Listener {
        private static int tickCounter = 0;

        @EventHandler
        public void onServerTickEnd(ServerTickEndEvent event) {
            tickCounter++;
            if ((tickCounter & 1) != 0) return; // 奇数tick跳过 → 每2tick同步一次
            try {
                for (BossRenderer r : new ArrayList<>(ACTIVE)) {
                    if (r.boss.isAlive() && r.boss.isValid()) {
                        r.sync();
                    }
                }
            } catch (Throwable t) {
                NamespaceKey.Keys.getplugin.getLogger().warning("[玩家模型Boss] tick末尾同步异常(已忽略): " + t);
            }
        }
    }
}
