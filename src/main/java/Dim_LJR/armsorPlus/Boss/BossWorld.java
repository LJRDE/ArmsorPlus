package Dim_LJR.armsorPlus.Boss;

import io.papermc.paper.registry.keys.GameRuleKeys;
import org.bukkit.*;
import org.bukkit.generator.ChunkGenerator;

/**
 * BOSS世界 —— 专用的BOSS战斗世界。
 * <p>
 * 一个平坦的虚空世界，用于BOSS战斗，防止破坏主世界地形。
 * 通过主菜单的「前往BOSS世界」传送进入。
 */
public class BossWorld {

    public static World world;

    /** 加载/创建BOSS世界 */
    @SuppressWarnings("unchecked")
    public static void loadWorld() {
        WorldCreator creator = new WorldCreator("BossWorld");
        creator.type(WorldType.FLAT);
        creator.generateStructures(false);
        creator.environment(World.Environment.NORMAL);
        world = creator.createWorld();

        if (world != null) {
            world.setAutoSave(true);
            world.setPVP(false);
            setRule(world, GameRuleKeys.KEEP_INVENTORY, true);
            setRule(world, GameRuleKeys.MOB_GRIEFING, false);
            setRule(world, GameRuleKeys.FIRE_DAMAGE, false);
            setRule(world, GameRuleKeys.SPAWN_MOBS, false);
            setRule(world, GameRuleKeys.SPAWN_MONSTERS, false);
            setRule(world, GameRuleKeys.SPAWN_PHANTOMS, false);
            setRule(world, GameRuleKeys.ADVANCE_TIME, false);
            setRule(world, GameRuleKeys.ADVANCE_WEATHER, false);
            world.setDifficulty(Difficulty.EASY);
            world.setTime(6000);

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
