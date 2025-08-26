package Dim_LJR.armsorPlus.OpenSea;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;


public class OpenSeaDig implements Listener {
    @EventHandler
    public void OnPlace(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();
        if(!player.getLocation().getWorld().getName().equals("OpenSea"))
            return;
        if(player.getGameMode().equals(GameMode.CREATIVE))
            return;
        event.setCancelled(true);
    }
    @EventHandler
    public void OnDig(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        if(!player.getLocation().getWorld().getName().equals("OpenSea"))
            return;
        if(player.getGameMode().equals(GameMode.CREATIVE))
            return;
        event.setCancelled(true);
    }
}
