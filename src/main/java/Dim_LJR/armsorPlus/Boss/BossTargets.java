package Dim_LJR.armsorPlus.Boss;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

// Boss 目标判定工具 —— 统一过滤条件, 一处改动全 Boss 生效。
// 排除: 死亡 / 无敌 / 创造(不可被攻击) / 旁观(无实体)。
public final class BossTargets {

    private BossTargets() {}

    public static boolean isCombatPlayer(Player p) {
        if (p == null || p.isDead() || p.isInvulnerable()) return false;
        GameMode mode = p.getGameMode();
        return mode != GameMode.CREATIVE && mode != GameMode.SPECTATOR;
    }
}
