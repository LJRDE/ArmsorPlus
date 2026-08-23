package Dim_LJR.armsorPlus.Boss;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

// 玩家模型Boss的"生物句柄"接口 —— 两种后端统一实现:
//   ① 附属插件(ArmsorPlusFakePlayer)通过 ServicesManager 注册 FakePlayerFactory, create() 得到此句柄;
//   ② 主插件本地兜底: ModelBoss(隐形盔甲架物理本体) + BossRenderer(PacketEvents 发包渲染)。
// 主插件只面向本接口编程, 不关心后端是谁。详见 docs/发包附属插件交接文档.md。
public interface FakePlayer {

    void remove();                                // 死亡/消失: 清实体+发包+取消任务+卸处理器
    boolean isAlive();                            // 血量 > 0
    boolean isValid();                            // 物理本体实体仍然存在(可能已空血但未清理)

    ArmorStand getBody();                         // 物理本体(盔甲架), 主插件读位置/BossMenu追踪用
    Location getLocation();

    void setTarget(Player target);                // 设追击目标; 内部每tick chase(绕墙/跳跃), null=原地待命
    void teleport(Location loc);                  // 技能瞬移(本体+视觉一起)
    void rotateTo(Player target);                 // 面向目标(攻击前保证挥砍方向与朝向一致)

    void swing();                                 // 挥手动画(主手)
    void hurt();                                  // 受击动画(红闪)

    double getHealth();
    double getMaxHealth();
    void applyDamage(Player attacker, double amount, boolean melee); // 手动扣血(攻击包拦截兜底用; melee=true 顺带近战击退)

    void spawnTo(Player p);                       // 后加入/换世界的玩家单独补发模型
    void despawnFrom(Player p);                   // 对单个玩家移除模型

    // 回调注册(主插件挂游戏逻辑: 算伤害/同步血条/掉落清理)
    void setOnAttack(Consumer<Player> cb);              // 玩家挥砍命中(拦截后主线程回调), 主插件算伤害并调 applyDamage()
    void setOnDamage(BiConsumer<Player, Double> cb);    // (attacker, 剩余血量) 主插件同步血条/menu
    void setOnDeath(Consumer<Player> cb);               // killer → 主插件掉落+清理
}
