package Dim_LJR.armsorPlus.Boss;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

// 玩家模型Boss的"后端发现 + 统一创建"入口。
// 附属插件 softdepend 主插件、必然后加载, 其 FakePlayerFactory 服务注册晚于主插件 onEnable。
// 因此这里不缓存 factory, 而是每次创建时实时 load(), 保证附属启用后能立刻被识别。
// 两种后端:
//   - 附属插件 FakePlayerFactory (优先): 有则用它 create 一个自包含的 FakePlayer 生物;
//   - 本地兜底: ModelBoss(隐形盔甲架物理本体) + BossRenderer(PacketEvents 发包渲染)。
// 调用方(PlayerBoss/VillageCaptain)只拿到 FakePlayer 句柄, 不关心具体后端。
public final class FakePlayerProvider {

    private static boolean packetEvents;

    private FakePlayerProvider() {}

    public static void init(boolean pe) {
        packetEvents = pe;
    }

    // 实时查询附属插件服务 (懒发现)
    public static FakePlayerFactory companion() {
        return Bukkit.getServicesManager().load(FakePlayerFactory.class);
    }

    public static boolean hasCompanion() { return companion() != null; }
    public static boolean hasPacketEvents() { return packetEvents; }
    public static boolean hasBackend() { return companion() != null || packetEvents; }

    // 影武者: 用 skinSource 的皮肤渲染
    public static FakePlayer createShadowWarrior(World world, Location loc, Player skinSource,
                                                 String bossName, String displayName,
                                                 float yaw, double maxHealth) {
        BossSkin skin = BossSkin.shadowWarrior(UUID.randomUUID(), bossName, skinSource);
        return create(world, loc, maxHealth, bossName, displayName, yaw, skin);
    }

    // 村民队长: 配置烘焙皮肤
    public static FakePlayer createVillageCaptain(World world, Location loc,
                                                  String bossName, String displayName,
                                                  float yaw, double maxHealth) {
        return create(world, loc, maxHealth, bossName, displayName, yaw, BossSkin.villageCaptain());
    }

    // 村民卫兵: 复用村民队长皮肤
    public static FakePlayer createVillageGuard(World world, Location loc,
                                                String bossName, String displayName,
                                                float yaw, double maxHealth) {
        return create(world, loc, maxHealth, bossName, displayName, yaw, BossSkin.villageCaptain());
    }

    private static FakePlayer create(World world, Location loc, double maxHealth,
                                     String bossName, String displayName, float yaw, BossSkin skin) {
        FakePlayerFactory factory = companion();
        if (factory != null) {
            return factory.create(new FakePlayerOptions(world, loc, maxHealth, bossName, displayName,
                    skin == null ? null : skin.value, skin == null ? null : skin.signature));
        }

        // 本地兜底: 物理本体 + (可选)PE发包渲染
        ModelBoss body = ModelBoss.spawn(world, loc, new ModelBoss.Options(maxHealth));
        if (body == null) return null;
        if (packetEvents) {
            BossRenderer.createFor(body, bossName, displayName, skin, yaw);
        }
        return body;
    }
}
