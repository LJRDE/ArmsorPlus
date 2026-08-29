package Dim_LJR.armsorPlus.Boss;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

// 通用BOSS生成点工具 —— 供所有BOSS的findSpawnLocation复用。
// 下界的顶盖是基岩, getHighestBlockYAt 会取到屋顶, 导致BOSS生成在屋顶之上无法到达;
// 因此在任意世界都用 groundY 计算可站立Y坐标: 主世界/末地用最高方块+1, 下界在玩家高度附近找地面。
public final class BossSpawn {
    private BossSpawn() {}

    // 环形搜索生成点: 绕玩家一圈 (startR起始半径, 每5格一档到20格, 共8个方向)。
    public static Location ringSpawn(Player summoner, int startR) {
        Location base = summoner.getLocation();
        World world = base.getWorld();

        for (int r = startR; r <= 20; r += 5) {
            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI / 4;
                Location loc = base.clone().add(Math.cos(angle) * r, 0, Math.sin(angle) * r);
                loc.setY(groundY(world, loc.getBlockX(), loc.getBlockZ(), base.getBlockY()));
                if (loc.getBlock().isEmpty() && loc.clone().add(0, 1, 0).getBlock().isEmpty()) {
                    return loc;
                }
            }
        }
        return null;
    }

    // 计算可站立Y坐标: 主世界/末地用最高方块+1; 下界在玩家所在高度上下各6格内找"脚下固体+上方两格空气",
    // 找不到则以玩家高度兜底。
    public static int groundY(World world, int x, int z, int playerY) {
        if (world.getEnvironment() == World.Environment.NETHER) {
            for (int offset = -6; offset <= 6; offset++) {
                int y = playerY + offset;
                if (y <= world.getMinHeight() || y + 1 >= world.getMaxHeight()) continue;
                if (world.getBlockAt(x, y, z).getType().isAir()
                        && world.getBlockAt(x, y + 1, z).getType().isAir()
                        && world.getBlockAt(x, y - 1, z).getType().isSolid()) {
                    return y;
                }
            }
            return playerY;
        }
        return world.getHighestBlockYAt(x, z) + 1;
    }
}
