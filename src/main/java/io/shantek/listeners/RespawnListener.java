package io.shantek.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
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

            // Delay the sendTitle message by 10 ticks
            Bukkit.getScheduler().runTaskLater(ultimateBingo, () -> {
                player.sendTitle(ChatColor.YELLOW + "TELEPORTING", ChatColor.WHITE + "One Moment", 10, 40, 10);
            }, 10L); // Delay showing the title by 10 ticks

            // Delayed teleport to handle any asynchronous issues, total delay 3.5 seconds (70 ticks from respawn)
            Bukkit.getScheduler().runTaskLater(ultimateBingo, () -> {
                player.teleport(ultimateBingo.bingoSpawnLocation);
                ultimateBingo.bingoFunctions.giveBingoCard(player);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            }, 70L); // Delay teleportation by 3.5 seconds (70 ticks) after respawn
        }

    }
}
