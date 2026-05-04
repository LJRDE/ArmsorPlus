package Dim_LJR.armsorPlus.OpenSea;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * 公海世界 (OpenSea) 的地形保护。
 * <p>
 * 防止非创造模式玩家在公海世界破坏或放置方块，
 * 保护公海地图的完整性。
 */
public class OpenSeaDig implements Listener {

    /** 禁止在公海世界放置方块 (创造模式除外) */
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!player.getLocation().getWorld().getName().equals("OpenSea")) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }

    /** 禁止在公海世界破坏方块 (创造模式除外) */
    @EventHandler
    public void onDig(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.getLocation().getWorld().getName().equals("OpenSea")) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }
}
