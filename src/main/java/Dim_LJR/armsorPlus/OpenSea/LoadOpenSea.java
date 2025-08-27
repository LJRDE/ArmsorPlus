package Dim_LJR.armsorPlus.OpenSea;

import org.bukkit.*;
import org.codehaus.plexus.util.FileUtils;

import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LoadOpenSea {
    public static World world;
    static InputStream rezip;
    static Path repath;
    static String GetMapName;
    static String GetMapZipName;
    public static void loadMap(Path folderPath, InputStream zip,boolean AutoReset,String OpenSeaName,String MapZipName) {
        if (!isValidWorldFolder(folderPath.resolve(OpenSeaName))) {
            Bukkit.getLogger().warning("无法找到世界文件夹,准备解压");
            try {
                unzipFromStream(zip, folderPath.resolve(OpenSeaName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if(AutoReset)
        {
            try {
                unzipFromStream(zip, folderPath.resolve(OpenSeaName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        rezip = zip;
        repath = folderPath;
        GetMapName = OpenSeaName;
        GetMapZipName = MapZipName;
        Bukkit.getLogger().info("加载世界中...");
        WorldCreator OpenSea = new WorldCreator(OpenSeaName);
        OpenSea.environment(World.Environment.NORMAL);
        OpenSea.generateStructures(false);
        OpenSea.type(WorldType.NORMAL);
        world =  OpenSea.createWorld();
        world.setAutoSave(true);
        world.setPVP(true);
        world.setGameRule(GameRule.KEEP_INVENTORY,true);//死亡不掉落
        world.setGameRule(GameRule.MOB_GRIEFING,false);//禁止苦力怕破坏
        world.setGameRule(GameRule.DO_FIRE_TICK,false);//禁止火焰蔓延
        world.setDifficulty(Difficulty.HARD);
    }

    public static void reloadmap()
    {
        try {
            unzipFromStream(rezip, repath.resolve(GetMapName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static boolean isValidWorldFolder(Path folderPath) {
        return Files.exists(folderPath) &&
                Files.isDirectory(folderPath) &&
                Files.exists(folderPath.resolve("level.dat")) &&
                Files.exists(folderPath.resolve("region"));
    }

    private static void deleteFolder(File folder)
    {
        File[] list = folder.listFiles();
        for(File file : list){
            if(file.isFile())
                file.delete();
            else deleteFolder(file);
        }
    }
    public static void unzipFromStream(InputStream zipInputStream, Path targetPath) throws IOException {

        if(targetPath.toFile().exists())
            deleteFolder(targetPath.toFile());
        // 确保目标目录存在
        if (!Files.exists(targetPath)) {
            Files.createDirectories(targetPath);
        }
        try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path entryPath = targetPath.resolve(entry.getName());

                // 防止路径遍历攻击
                if (!entryPath.normalize().startsWith(targetPath.normalize())) {
                    throw new IOException("非法路径: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    // 创建目录
                    Files.createDirectories(entryPath);
                } else {
                    // 确保父目录存在
                    Path parent = entryPath.getParent();
                    if (parent != null && !Files.exists(parent)) {
                        Files.createDirectories(parent);
                    }

                    // 复制文件内容
                    try (OutputStream os = Files.newOutputStream(entryPath)) {
                        byte[] buffer = new byte[8192];
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