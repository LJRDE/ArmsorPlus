package Dim_LJR.armsorPlus.Boss;

import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

// 本地皮肤文件服务器 (离线服皮肤) — 默认已关闭, 不再占用端口。
// 影武者皮肤现在优先从 Mojang 在线拉取 (SkinRestorer式, 原版客户端可显示);
// 本地HTTP服务仅作为可选方案, 供装了 CustomSkinLoader 等皮肤mod的客户端使用。
public final class SkinServer {

    private static HttpServer server;
    private static Path skinsDir;
    private static String host = "127.0.0.1";
    private static String bossSkinFile = "Village_master.png";

    private SkinServer() {}

    public static void init(Plugin plugin) {
        bossSkinFile = plugin.getConfig().getString("BossSkinFile", "Village_master.png");
        host = plugin.getConfig().getString("SkinServerHost", ""); // 仅配置值, 留空则自动解析
        skinsDir = Paths.get(plugin.getDataFolder().getAbsolutePath(), "skins");
        try {
            Files.createDirectories(skinsDir);
        } catch (IOException e) {
            plugin.getLogger().warning("无法创建皮肤目录: " + e.getMessage());
            return;
        }

        // 若 skins 目录缺少配置的皮肤文件, 从插件资源提取 (data-url兜底会用到)
        Path skinFile = skinsDir.resolve(bossSkinFile).normalize();
        if (!Files.isRegularFile(skinFile)) {
            try (InputStream in = plugin.getResource("skin/" + bossSkinFile)) {
                if (in != null) {
                    Files.copy(in, skinFile);
                    plugin.getLogger().info("已从插件资源提取默认皮肤: skins/" + bossSkinFile);
                }
            } catch (IOException ignored) { }
        }

        // 默认不启动HTTP服务器(不占端口): 影武者皮肤走 Mojang 在线拉取。
        // 仅当 EnableSkinServer: true 时才绑定端口 (需要客户端装有皮肤mod)。
        if (!plugin.getConfig().getBoolean("EnableSkinServer", false)) {
            plugin.getLogger().info("影武者皮肤: Mojang在线拉取模式 (未占用端口); 如客户端装有皮肤mod, 可设 EnableSkinServer: true 启用本地服务器");
            return;
        }
        try {
            int port = plugin.getConfig().getInt("SkinServerPort", 26666);
            server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
            server.createContext("/skins/", exchange -> {
                try {
                    String fileName = exchange.getRequestURI().getPath().substring("/skins/".length());
                    Path file = skinsDir.resolve(fileName).normalize();
                    if (!file.startsWith(skinsDir) || !Files.isRegularFile(file)) {
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }
                    byte[] bytes = Files.readAllBytes(file);
                    exchange.getResponseHeaders().set("Content-Type", "image/png");
                    exchange.sendResponseHeaders(200, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                    }
                } catch (IOException ex) {
                    exchange.close();
                }
            });
            server.start();
            plugin.getLogger().info("皮肤文件服务器已启动于端口 " + port
                    + " (同机客户端自动使用127.0.0.1绕过防火墙; 配置SkinServerHost可指定IP) BOSS皮肤: " + bossSkinFile);
        } catch (IOException e) {
            plugin.getLogger().warning("皮肤文件服务器启动失败: " + e.getMessage());
        }
    }

    public static void shutdown() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    // BOSS 使用的本地皮肤URL, 文件不存在则返回 null
    // viewer: 用于判断客户端是否与服务端同机 (回环连接时用 127.0.0.1 绕过防火墙)
    public static String getBossSkinUrl(Player viewer) {
        if (server == null || skinsDir == null) return null;
        Path file = skinsDir.resolve(bossSkinFile).normalize();
        if (!Files.isRegularFile(file)) return null;
        return "http://" + resolveHostFor(viewer) + ":" + server.getAddress().getPort() + "/skins/" + bossSkinFile;
    }

    // data:image/png;base64 形式的本地BOSS皮肤 (无端口依赖)。
    // 注意: 原版客户端不会加载 data-url, 只有支持 data-url 的 modded 客户端可见;
    // 作为Mojang在线拉取失败时的兜底, 保证BOSS不至于完全没有皮肤。
    public static String getBossSkinDataUrl() {
        if (skinsDir == null) return null;
        Path file = skinsDir.resolve(bossSkinFile).normalize();
        if (!Files.isRegularFile(file)) return null;
        try {
            byte[] bytes = Files.readAllBytes(file);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    private static String resolveHostFor(Player viewer) {
        // 1. 配置显式指定
        if (host != null && !host.isEmpty()) {
            return host;
        }
        // 2. 客户端通过回环地址连接 (同机测试) → 用 127.0.0.1, 绕过Windows防火墙
        if (viewer != null && viewer.getAddress() != null
                && viewer.getAddress().getAddress() != null
                && viewer.getAddress().getAddress().isLoopbackAddress()) {
            return "127.0.0.1";
        }
        // 3. 远程客户端 → 用服务器配置的IP, 否则本机IP (联机时可能需玩家自行配置 SkinServerHost)
        String serverIp = Bukkit.getServer().getIp();
        if (serverIp != null && !serverIp.isEmpty()) return serverIp;
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
}
