package io.shantek.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.entity.Player;
import io.shantek.UltimateBingo;

public class RespawnListener implements Listener {

    UltimateBingo ultimateBingo;

    public RespawnListener(UltimateBingo ultimateBingo) {
        this.ultimateBingo = ultimateBingo;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        // Only teleport the player back to bingo spawn if enabled in the settings
        // Enabled by default

        if (ultimateBingo.bingoManager.isStarted() && ultimateBingo.respawnTeleport) {
            Player player = event.getPlayer();

            // Send title and subtitle immediately after respawn
            player.sendTitle(ChatColor.YELLOW + "TELEPORTING", ChatColor.WHITE + "One Moment", 10, 40, 10);

            // Delayed teleport to handle any asynchronous issues, increased delay to 2 seconds
            Bukkit.getScheduler().runTaskLater(ultimateBingo, () -> {
                player.teleport(ultimateBingo.bingoSpawnLocation);
                ultimateBingo.bingoFunctions.giveBingoCard(player);
            }, 60L); // Delay teleportation by 3 seconds (60 ticks)
        }
    }
}
