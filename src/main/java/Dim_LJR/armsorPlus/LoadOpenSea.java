package Dim_LJR.armsorPlus;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LoadOpenSea {
    private final JavaPlugin plugin;
    private final String worldName;
    private final String zipResourceName;

    public LoadOpenSea(JavaPlugin plugin) {
        this(plugin, "OpenSea", "OpenSea.zip");
    }

    public LoadOpenSea(JavaPlugin plugin, String worldName, String zipResourceName) {
        this.plugin = plugin;
        this.worldName = worldName;
        this.zipResourceName = zipResourceName;
    }

    public boolean loadMap() {
        Path targetDir = Bukkit.getWorldContainer().toPath().resolve(worldName);

        try {
            // 检查世界是否已存在
            if (!Files.exists(targetDir) || !isValidWorld(targetDir)) {
                // 从插件资源读取ZIP
                InputStream zipStream = plugin.getResource(zipResourceName);
                if (zipStream == null) {
                    plugin.getLogger().severe("地图压缩包未找到: " + zipResourceName);
                    return false;
                }

                // 解压地图
                unzipMap(zipStream, targetDir);
                plugin.getLogger().info("地图解压完成: " + worldName);
            }

            // 加载世界
            World world = Bukkit.createWorld(new WorldCreator(worldName));
            if (world != null) {
                plugin.getLogger().info("世界已加载: " + worldName);
                return true;
            }
        } catch (Exception e) {
            plugin.getLogger().severe("地图加载失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private boolean isValidWorld(Path worldDir) throws IOException {
        return Files.exists(worldDir.resolve("level.dat")) &&
                Files.exists(worldDir.resolve("region"));
    }

    private void unzipMap(InputStream zipStream, Path targetDir) throws IOException {
        // 清理旧目录
        if (Files.exists(targetDir)) {
            deleteDirectory(targetDir);
        }
        Files.createDirectories(targetDir);

        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(zipStream))) {
            ZipEntry entry;
            byte[] buffer = new byte[8192];

            while ((entry = zis.getNextEntry()) != null) {
                Path entryPath = targetDir.resolve(entry.getName()).normalize();

                // 安全验证
                if (!entryPath.startsWith(targetDir)) {
                    throw new IOException("无效的ZIP路径: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    try (OutputStream os = Files.newOutputStream(entryPath)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            os.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }

    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try { Files.delete(p); }
                        catch (IOException e) { /* 忽略删除错误 */ }
                    });
        }
    }
}