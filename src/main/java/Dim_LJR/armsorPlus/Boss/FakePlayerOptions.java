package Dim_LJR.armsorPlus.Boss;

import org.bukkit.Location;
import org.bukkit.World;

// 创建 FakePlayer 的配置(主插件在召唤 Boss 时构造)。
public class FakePlayerOptions {

    public final World world;
    public final Location spawnLocation; // 初始位置+朝向
    public final double maxHealth;
    public final String bossName;        // 英文名(合法 Minecraft 用户名, 用于 PlayerInfo)
    public final String displayName;     // 显示名(含 § 颜色码)
    public final String skinValue;       // textures 的 value(base64), 可为空
    public final String skinSignature;   // textures 的 signature, 可为空

    public FakePlayerOptions(World world, Location spawnLocation, double maxHealth,
                             String bossName, String displayName,
                             String skinValue, String skinSignature) {
        this.world = world;
        this.spawnLocation = spawnLocation;
        this.maxHealth = maxHealth;
        this.bossName = bossName;
        this.displayName = displayName;
        this.skinValue = skinValue;
        this.skinSignature = skinSignature;
    }
}
