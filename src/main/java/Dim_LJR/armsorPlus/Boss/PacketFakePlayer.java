package Dim_LJR.armsorPlus.Boss;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// 纯发包渲染的"假玩家"实体 —— 用于玩家模型Boss。
// 服务端没有对应的真实实体，只向客户端发送 Spawn/移动/旋转/装备/动画 等数据包，
// 使客户端渲染出一个带自定义皮肤的完整玩家模型。
// 注意：客户端不会对它进行重力模拟，必须由调用方持续发送位置/旋转包来驱动它。
public class PacketFakePlayer {

    private final int entityId;
    private final UUID uuid;
    private final UserProfile profile;
    private final Component displayName;
    private final String worldName;

    private double x, y, z;
    private float yaw, pitch;

    public PacketFakePlayer(int entityId, UUID uuid, UserProfile profile, String legacyDisplayName,
                      String worldName, double x, double y, double z, float yaw, float pitch) {
        this.entityId = entityId;
        this.uuid = uuid;
        this.profile = profile;
        this.displayName = LegacyComponentSerializer.legacySection().deserialize(legacyDisplayName);
        this.worldName = worldName;
        this.x = x; this.y = y; this.z = z;
        this.yaw = yaw; this.pitch = pitch;
    }

    public int getEntityId() { return entityId; }
    public UUID getUuid() { return uuid; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    // ========================================================================
    // 生成
    // ========================================================================

    public void spawn(Player viewer) {
        // 1. 加入玩家列表 (带皮肤属性) — 手动按 1.21.11 协议编码:
        //    1.21.9+ 的 PlayerInfoUpdate 改为"每字段一个 Action", ADD_PLAYER 只写 profile,
        //    其余字段(gameMode/listed/latency/displayName/showHat/listOrder/chatSession)是独立 Action。
        //    PacketEvents 2.13.0 仍写旧的合并格式, 会在包内留下多余字节 → 客户端解码器直接抛
        //    IOException("Packet was larger than I expected") 断开连接。因此这里用 PacketWrapper
        //    手写严格格式: actions=[ADD_PLAYER], 每条目只写 UUID + profile。
        sendPlayerInfoAdd(viewer);

        // 2. 生成玩家实体 — 1.21.2+ "drop"版本移除了独立的 SpawnPlayer 包,
        //    玩家必须用通用 AddEntity(实体类型=player) 生成, 客户端才会渲染出玩家模型。
        //    (旧版 WrapperPlayServerSpawnPlayer 在 1.21.11 会被客户端直接忽略 → 实体不生成 → 隐形)
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer,
                new WrapperPlayServerSpawnEntity(entityId, Optional.of(uuid), EntityTypes.PLAYER,
                        new Vector3d(x, y, z), yaw, pitch, yaw, 0, Optional.empty()));

        // 3. 单独发送实体元数据 (自定义名称/名称可见/无重力) — 1.21.11 的 AddEntity 包不含元数据
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer,
                new WrapperPlayServerEntityMetadata(entityId, spawnMetadata()));

        // 4. 装备主手钻石剑
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer,
                new WrapperPlayServerEntityEquipment(entityId, List.of(
                        new Equipment(EquipmentSlot.MAIN_HAND,
                                ItemStack.builder().type(ItemTypes.DIAMOND_SWORD).amount(1).build()))));
    }

    public void spawnToWorld(World world) {
        for (Player p : world.getPlayers()) spawn(p);
    }

    // 向指定客户端发送 PlayerInfoUpdate(ADD_PLAYER) 包, 写入带皮肤纹理的 profile。
    // 1.21.11 的条目格式: writeEnumSet(actions) → varint 条目数 → 每条目: UUID + 各 Action 字段。
    // ADD_PLAYER 只含 profile(name + textures 属性), 无任何多余字节。
    private void sendPlayerInfoAdd(Player viewer) {
        PacketWrapper wrapper = new PacketWrapper(PacketType.Play.Server.PLAYER_INFO_UPDATE) {
            @Override
            public void write() {
                writeEnumSet(EnumSet.of(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER),
                        WrapperPlayServerPlayerInfoUpdate.Action.class);
                writeVarInt(1);
                writeUUID(uuid);
                writeString(profile.getName(), 16);
                List<TextureProperty> props = profile.getTextureProperties();
                writeVarInt(props.size());
                for (TextureProperty prop : props) {
                    writeString(prop.getName());
                    writeString(prop.getValue());
                    String signature = prop.getSignature();
                    writeBoolean(signature != null);
                    if (signature != null) writeString(signature);
                }
            }
        };
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, wrapper);
    }

    // ========================================================================
    // 移动 / 旋转
    // ========================================================================

    // 相对移动 (平滑插值)。若单次位移超过 8 格会退回绝对传送。
    public void move(Player viewer, double dx, double dy, double dz, float yaw, float pitch, float headYaw) {
        if (!Double.isFinite(dx) || !Double.isFinite(dy) || !Double.isFinite(dz)) {
            // NaN会污染服务端跟踪位置并让后续相对位移永远变成0 → 假玩家冻结, 防御性原地传送
            teleport(viewer, x, y, z, yaw, pitch, headYaw);
            return;
        }
        if (Math.abs(dx) > 8 || Math.abs(dy) > 8 || Math.abs(dz) > 8) {
            teleport(viewer, x + dx, y + dy, z + dz, yaw, pitch, headYaw);
            return;
        }
        this.x += dx; this.y += dy; this.z += dz;
        this.yaw = yaw; this.pitch = pitch;

        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer,
                new WrapperPlayServerEntityRelativeMove(entityId, dx, dy, dz, true));
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer,
                new WrapperPlayServerEntityRotation(entityId, yaw, pitch, true));
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer,
                new WrapperPlayServerEntityHeadLook(entityId, headYaw));
    }

    public void teleport(Player viewer, double x, double y, double z, float yaw, float pitch, float headYaw) {
        this.x = x; this.y = y; this.z = z;
        this.yaw = yaw; this.pitch = pitch;

        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer,
                new WrapperPlayServerEntityTeleport(entityId, new Vector3d(x, y, z), yaw, pitch, true));
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer,
                new WrapperPlayServerEntityHeadLook(entityId, headYaw));
    }

    public void moveToWorld(World world, double dx, double dy, double dz, float yaw, float pitch, float headYaw) {
        for (Player p : world.getPlayers()) move(p, dx, dy, dz, yaw, pitch, headYaw);
    }

    public void teleportToWorld(World world, double x, double y, double z, float yaw, float pitch, float headYaw) {
        for (Player p : world.getPlayers()) teleport(p, x, y, z, yaw, pitch, headYaw);
    }

    // 原地转向 (不移动位置) —— 攻击前让假玩家面向目标, 保证朝向与挥砍方向一致
    public void rotate(Player viewer, float yaw, float pitch, float headYaw) {
        this.yaw = yaw; this.pitch = pitch;

        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer,
                new WrapperPlayServerEntityRotation(entityId, yaw, pitch, true));
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer,
                new WrapperPlayServerEntityHeadLook(entityId, headYaw));
    }

    public void rotateToWorld(World world, float yaw, float pitch, float headYaw) {
        for (Player p : world.getPlayers()) rotate(p, yaw, pitch, headYaw);
    }

    // ========================================================================
    // 动画
    // ========================================================================

    public void swing(Player viewer) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer,
                new WrapperPlayServerEntityAnimation(entityId,
                        WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM));
    }

    public void swingToWorld(World world) {
        for (Player p : world.getPlayers()) swing(p);
    }

    public void hurt(Player viewer) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer,
                new WrapperPlayServerEntityAnimation(entityId,
                        WrapperPlayServerEntityAnimation.EntityAnimationType.HURT));
    }

    public void hurtToWorld(World world) {
        for (Player p : world.getPlayers()) hurt(p);
    }

    // ========================================================================
    // 移除
    // ========================================================================

    public void despawn(Player viewer) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer,
                new WrapperPlayServerDestroyEntities(entityId));
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer,
                new WrapperPlayServerPlayerInfoRemove(uuid));
    }

    public void despawnToWorld(World world) {
        for (Player p : world.getPlayers()) despawn(p);
    }

    // ========================================================================
    // 元数据
    // ========================================================================

    private List<EntityData<?>> spawnMetadata() {
        return List.of(
                new EntityData<>(2, EntityDataTypes.OPTIONAL_ADV_COMPONENT, Optional.of(displayName)),
                new EntityData<>(3, EntityDataTypes.BOOLEAN, true),
                new EntityData<>(5, EntityDataTypes.BOOLEAN, true),
                // 皮肤层(0x7F = 帽子/外套/左右袖/左右裤腿/披风全开) → 显示3D外层。
                // 索引16 是 1.21.11 玩家基类 Avatar 的 DATA_PLAYER_MODE_CUSTOMISATION (BYTE)。
                // 注意: 索引15 是 DATA_PLAYER_MAIN_HAND, 但1.21.11里它的类型是 HumanoidArm 枚举,
                // 不是BYTE —— 曾因用BYTE写索引15导致客户端类型不匹配被踢, 所以这里不碰它。
                new EntityData<>(16, EntityDataTypes.BYTE, (byte) 0x7F));
    }

    public String getWorldName() { return worldName; }
}
