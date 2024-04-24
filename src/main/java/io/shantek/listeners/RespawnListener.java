package io.shantek.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.entity.Player;
import io.shantek.UltimateBingo;

public class RespawnListener implements Listener {

    UltimateBingo ultimateBingo;
    public RespawnListener(UltimateBingo ultimateBingo){
        this.ultimateBingo = ultimateBingo;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        // Spawn them back at the bingo spawn
        event.setRespawnLocation(ultimateBingo.bingoSpawnLocation);

        // Give them a new bingo compass
        ultimateBingo.bingoFunctions.giveBingoCompass(player);
    }
}
