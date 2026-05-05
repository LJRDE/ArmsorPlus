package Dim_LJR.armsorPlus.Boss;

import org.bukkit.*;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

/**
 * BOSS世界 —— 专用的BOSS战斗世界。
 * <p>
 * 一个平坦的虚空世界，用于BOSS战斗，防止破坏主世界地形。
 * 通过主菜单的「前往BOSS世界」传送进入。
 */
public class BossWorld {

    public static World world;

    /** 加载/创建BOSS世界 */
    public static void loadWorld() {
        WorldCreator creator = new WorldCreator("BossWorld");
        creator.type(WorldType.FLAT);
        creator.generateStructures(false);
        creator.environment(World.Environment.NORMAL);
        world = creator.createWorld();

        if (world != null) {
            world.setAutoSave(true);
            world.setPVP(false);
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
            world.setGameRule(GameRule.MOB_GRIEFING, false);
            world.setGameRule(GameRule.DO_FIRE_TICK, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setDifficulty(Difficulty.EASY);
            world.setTime(6000); // 永远白天

            Bukkit.getLogger().info("BOSS世界加载完成");
        } else {
            Bukkit.getLogger().warning("BOSS世界加载失败");
        }
    }
}
