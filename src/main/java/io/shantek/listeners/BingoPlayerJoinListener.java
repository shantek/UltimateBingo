package io.shantek.listeners;

import io.shantek.UltimateBingo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class BingoPlayerJoinListener implements Listener {

    UltimateBingo ultimateBingo;

    public BingoPlayerJoinListener(UltimateBingo ultimateBingo) {
        this.ultimateBingo = ultimateBingo;
    }

    public void onJoin(PlayerJoinEvent e) {

        // Get the player who just joined
        Player player = e.getPlayer();

        // Check if bingo is active and if they have a card
        // If they don't, prompt them on how to join the game
        // Delay the message by 5 seconds

        if (ultimateBingo.bingoStarted && !ultimateBingo.bingoManager.checkHasBingoCard(player)) {

            // Delay the message by 5 seconds (100 ticks since 1 second = 20 ticks in Minecraft)
            Bukkit.getScheduler().scheduleSyncDelayedTask(ultimateBingo, new Runnable() {
                @Override
                public void run() {
                    player.sendMessage(ChatColor.GREEN + "A bingo game is currently in progress. Type /bingo to join in!");
                }
            }, 100L); // 100 ticks delay
        }

    }

}
