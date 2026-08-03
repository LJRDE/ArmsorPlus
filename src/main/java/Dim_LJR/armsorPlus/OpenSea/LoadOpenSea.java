package Dim_LJR.armsorPlus.OpenSea;

import org.bukkit.*;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

// 公海世界 (OpenSea) 的地图加载与解压。
// 插件启动时从 jar 包内读取 OpenSea.zip 并解压到服务端世界目录下，
// 然后创建 / 加载该世界。支持自动重置模式 (AutoResetOpenSeaMap)。
public class LoadOpenSea {

    // 公海世界的 World 实例
    public static World world;

    private static InputStream rezip;
    private static Path repath;
    private static String getMapName;
    private static String getMapZipName;

    // 加载公海世界: 验证/解压世界文件 → 创建/加载世界。
    //
    // @param folderPath  服务端世界目录路径
    // @param zip         地图压缩包输入流 (从jar资源读取)
    // @param autoReset   是否每次重启自动重置地图
    // @param worldName   世界文件夹名
    // @param zipFileName 压缩包文件名
    @SuppressWarnings("removal")
    public static void loadMap(Path folderPath, InputStream zip, boolean autoReset,
                                String worldName, String zipFileName) {
        Path worldPath = folderPath.resolve(worldName);

        if (!isValidWorldFolder(worldPath) || autoReset) {
            Bukkit.getLogger().warning("无法找到世界文件夹或开启了自动重置，准备解压");
            try {
                deleteFolder(worldPath.toFile());
                unzipFromStream(zip, worldPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 保存参数以便 reloadmap 使用
        rezip = zip;
        repath = folderPath;
        getMapName = worldName;
        getMapZipName = zipFileName;

        Bukkit.getLogger().info("加载世界中...");

        WorldCreator creator = new WorldCreator(worldName);
        creator.environment(World.Environment.NORMAL);
        creator.generateStructures(false);
        creator.type(WorldType.NORMAL);
        world = creator.createWorld();

        if (world != null) {
            world.setAutoSave(true);
            world.setPVP(true);
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
            world.setGameRule(GameRule.MOB_GRIEFING, false);
            world.setGameRule(GameRule.DO_FIRE_TICK, false);
            world.setDifficulty(Difficulty.HARD);
        }
    }

    // 重新解压覆盖公海世界 (用于 /ArmsorPlus reloadmap)
    public static void reloadmap() {
        try {
            unzipFromStream(rezip, repath.resolve(getMapName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 验证指定路径是否为有效的 Minecraft 世界文件夹 (含 level.dat 和 region 目录)
    private static boolean isValidWorldFolder(Path folderPath) {
        return Files.exists(folderPath)
                && Files.isDirectory(folderPath)
                && Files.exists(folderPath.resolve("level.dat"))
                && Files.exists(folderPath.resolve("region"));
    }

    // 递归删除文件夹
    private static void deleteFolder(File folder) {
        if (!folder.exists()) return;
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isFile()) file.delete();
            else deleteFolder(file);
        }
        folder.delete();
    }

    // 将压缩包流解压到目标路径
    public static void unzipFromStream(InputStream zipInputStream, Path targetPath) throws IOException {
        // 删除已存在的目标
        if (targetPath.toFile().exists()) {
            deleteFolder(targetPath.toFile());
        }
        Files.createDirectories(targetPath);

        try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
            ZipEntry entry;
            byte[] buffer = new byte[8192];

            while ((entry = zis.getNextEntry()) != null) {
                Path entryPath = targetPath.resolve(entry.getName()).normalize();

                // 防止路径遍历攻击
                if (!entryPath.startsWith(targetPath.normalize())) {
                    throw new IOException("非法路径: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    try (OutputStream os = Files.newOutputStream(entryPath)) {
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
}
