package Dim_LJR.armsorPlus.Boss;

import Dim_LJR.armsorPlus.NamespaceKey;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

// Boss 皮肤(value + signature)的构建与缓存。从 BossRenderer 抽取出来的纯皮肤逻辑,
// 不依赖 PacketEvents 类型, 因此本地PE后端(转 UserProfile)和附属插件接口(FakePlayerOptions)都能复用。
public final class BossSkin {

    public final String value;       // textures base64
    public final String signature;   // 可空

    public BossSkin(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    public boolean isEmpty() { return value == null || value.isEmpty(); }

    // ---- 影武者皮肤 (SkinRestorer式: 配置烘焙 > Mojang在线缓存 > data-url兜底 > skinSource纸皮) ----
    public static BossSkin shadowWarrior(UUID fakeUuid, String bossName, Player skinSource) {
        String bakedValue = NamespaceKey.Keys.getplugin.getConfig().getString("BossSkinValue", "");
        if (!bakedValue.isEmpty()) {
            String bakedSignature = NamespaceKey.Keys.getplugin.getConfig().getString("BossSkinSignature", "");
            return new BossSkin(bakedValue, bakedSignature.isEmpty() ? null : bakedSignature);
        }

        BossSkin cached = getCachedBossSkin();
        if (cached != null) return cached;

        String dataUrl = SkinServer.getBossSkinDataUrl();
        if (dataUrl != null) {
            String payload = "{\"timestamp\":" + System.currentTimeMillis()
                    + ",\"profileId\":\"" + fakeUuid.toString().replace("-", "") + "\""
                    + ",\"profileName\":\"" + bossName + "\""
                    + ",\"textures\":{\"SKIN\":{\"url\":\"" + dataUrl + "\"}}}";
            String value = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
            return new BossSkin(value, null);
        }

        com.destroystokyo.paper.profile.PlayerProfile paperProfile = skinSource.getPlayerProfile();
        if (paperProfile != null) {
            for (ProfileProperty prop : paperProfile.getProperties()) {
                if ("textures".equalsIgnoreCase(prop.getName())) {
                    return new BossSkin(prop.getValue(), prop.getSignature());
                }
            }
            try {
                org.bukkit.profile.PlayerTextures tex = paperProfile.getTextures();
                java.net.URL skinUrl = tex.getSkin();
                if (skinUrl != null) {
                    boolean slim = tex.getSkinModel() == org.bukkit.profile.PlayerTextures.SkinModel.SLIM;
                    String payload = "{\"timestamp\":" + System.currentTimeMillis()
                            + ",\"profileId\":\"" + fakeUuid + "\""
                            + ",\"profileName\":\"" + bossName + "\""
                            + ",\"textures\":{\"SKIN\":{\"url\":\"" + skinUrl + "\""
                            + (slim ? ",\"metadata\":{\"model\":\"slim\"}" : "") + "}}}";
                    String value = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
                    return new BossSkin(value, null);
                }
            } catch (Exception ignored) {
            }
        }

        return new BossSkin(null, null);
    }

    // ---- 村民队长皮肤 (配置烘焙 value+signature) ----
    public static BossSkin villageCaptain() {
        String value = NamespaceKey.Keys.getplugin.getConfig().getString("CaptainSkinValue", "");
        if (value.isEmpty()) {
            NamespaceKey.Keys.getplugin.getLogger().warning("[村民队长] 未配置 CaptainSkinValue, 使用默认皮肤");
            return new BossSkin(null, null);
        }
        String signature = NamespaceKey.Keys.getplugin.getConfig().getString("CaptainSkinSignature", "");
        return new BossSkin(value, signature.isEmpty() ? null : signature);
    }

    // ---- Mojang 皮肤拉取 (SkinRestorer式) ----
    private static final Gson GSON = new Gson();
    private static volatile BossSkin cachedBossSkin;
    private static volatile long lastSkinFetch = 0L;
    private static final long SKIN_RETRY_INTERVAL = 60_000L;

    public static void prefetchShadowWarrior() {
        Bukkit.getScheduler().runTaskAsynchronously(NamespaceKey.Keys.getplugin, () -> {
            try {
                getCachedBossSkin();
            } catch (Throwable ignored) {
            }
        });
    }

    private static BossSkin getCachedBossSkin() {
        if (cachedBossSkin != null) return cachedBossSkin;
        long now = System.currentTimeMillis();
        if (now - lastSkinFetch < SKIN_RETRY_INTERVAL) return null;

        String playerName = NamespaceKey.Keys.getplugin.getConfig()
                .getString("BossSkinPlayerName", "Village_master");
        lastSkinFetch = now;
        try {
            cachedBossSkin = fetchSignedBossSkin(playerName);
        } catch (Throwable t) {
            NamespaceKey.Keys.getplugin.getLogger().warning("[影武者] Mojang皮肤拉取异常: " + t.getMessage());
        }
        return cachedBossSkin;
    }

    private static BossSkin fetchSignedBossSkin(String playerName) {
        if (playerName == null || playerName.isBlank()) return null;
        try {
            String uuidBody = httpGet("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            if (uuidBody == null) return null;
            JsonObject uuidObj = GSON.fromJson(uuidBody, JsonObject.class);
            if (uuidObj == null || !uuidObj.has("id")) return null;
            String uuid = uuidObj.get("id").getAsString();

            String profileBody = httpGet("https://sessionserver.mojang.com/session/minecraft/profile/"
                    + uuid + "?unsigned=false");
            if (profileBody == null) return null;
            JsonObject profileObj = GSON.fromJson(profileBody, JsonObject.class);
            if (profileObj == null || !profileObj.has("properties")) return null;
            JsonArray props = profileObj.getAsJsonArray("properties");
            for (JsonElement el : props) {
                JsonObject p = el.getAsJsonObject();
                if (p.has("name") && "textures".equalsIgnoreCase(p.get("name").getAsString())) {
                    String value = p.get("value").getAsString();
                    String signature = p.has("signature") ? p.get("signature").getAsString() : null;
                    return new BossSkin(value, signature);
                }
            }
        } catch (Exception e) {
            NamespaceKey.Keys.getplugin.getLogger().warning("[影武者] 拉取皮肤失败(" + playerName + "): " + e.getMessage());
        }
        return null;
    }

    private static String httpGet(String urlString) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        try {
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            conn.setRequestProperty("User-Agent", "ArmsorPlus");
            if (conn.getResponseCode() != 200) return null;
            try (InputStream in = conn.getInputStream()) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        } finally {
            conn.disconnect();
        }
    }
}
