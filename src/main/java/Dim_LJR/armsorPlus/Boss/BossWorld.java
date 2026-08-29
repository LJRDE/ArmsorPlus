package Dim_LJR.armsorPlus.Boss;

import io.papermc.paper.registry.keys.GameRuleKeys;
import org.bukkit.*;

// BOSS世界 —— 专用的BOSS战斗世界。
// 一个平坦的虚空世界，用于BOSS战斗，防止破坏主世界地形。
// 通过主菜单的「前往BOSS世界」传送进入。
public class BossWorld {

    public static World world;

    // 加载/创建BOSS世界
    @SuppressWarnings("unchecked")
    public static void loadWorld() {
        WorldCreator creator = new WorldCreator("BossWorld");
        creator.type(WorldType.FLAT);
        creator.generateStructures(false);
        creator.environment(World.Environment.NORMAL);
        world = creator.createWorld();

        if (world != null) {
            world.setAutoSave(true);
            // 保留: 死亡不掉物品栏、世界时间清晨600、难度困难。其余世界规则(PVP/生物生成等)恢复默认
            setRule(world, GameRuleKeys.KEEP_INVENTORY, true);
            world.setTime(600);
            world.setDifficulty(Difficulty.HARD);

            Bukkit.getLogger().info("BOSS世界加载完成");
        } else {
            Bukkit.getLogger().warning("BOSS世界加载失败");
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void setRule(World world, io.papermc.paper.registry.TypedKey<GameRule<?>> key, T value) {
        world.setGameRule((GameRule<T>) (GameRule<?>) Registry.GAME_RULE.get(key), value);
    }
}
