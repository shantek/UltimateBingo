package io.shantek.listeners;

import io.shantek.UltimateBingo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BingoPlayerJoinListener implements Listener {

    UltimateBingo ultimateBingo;

    public BingoPlayerJoinListener(UltimateBingo ultimateBingo) {
        this.ultimateBingo = ultimateBingo;
    }

    public void onJoin(PlayerJoinEvent e) {

        // Get the player who just joined
        Player player = e.getPlayer();

        if (!ultimateBingo.bingoStarted) {
            // If they joined and a game isn't active, reset their inventory in case they
            // carried anything over from a prior game
            ultimateBingo.bingoFunctions.resetIndividualPlayer(player, true);

            // Give them a replacement card so they can view results from a prior game
            if (ultimateBingo.bingoManager.checkHasBingoCard(player)) {
                ultimateBingo.bingoFunctions.giveBingoCard(player);
            }

        } else if (ultimateBingo.bingoStarted && !ultimateBingo.bingoManager.checkHasBingoCard(player)) {

            // Check if bingo is active and if they have a card. If they don't,
            // prompt them on how to join the game. Delay the message by 5 seconds

            Bukkit.getScheduler().scheduleSyncDelayedTask(ultimateBingo, () -> {
                player.sendMessage(ChatColor.GREEN + "A bingo game is currently in progress. Type /bingo to join in!");
            }, 100);
        }

        if (ultimateBingo.bingoStarted && ultimateBingo.bingoManager.checkHasBingoCard(player) && ultimateBingo.gameMode.equals("speedrun")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false, true));
        }
    }
}
