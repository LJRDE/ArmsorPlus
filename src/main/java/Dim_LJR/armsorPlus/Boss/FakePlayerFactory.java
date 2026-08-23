package Dim_LJR.armsorPlus.Boss;

// 工厂: 附属插件(ArmsorPlusFakePlayer)实现并注册到 Bukkit ServicesManager。
// 主插件 onEnable 时 getRegistration(FakePlayerFactory.class) 发现它, 用于创建 FakePlayer。
public interface FakePlayerFactory {

    // 创建并启动一个假玩家生物。失败返回 null(附属插件内部应记录日志)。
    FakePlayer create(FakePlayerOptions opt);
}
