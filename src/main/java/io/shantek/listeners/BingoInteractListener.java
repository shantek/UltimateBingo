package io.shantek.listeners;

import io.shantek.UltimateBingo;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BingoInteractListener implements Listener {

    UltimateBingo ultimateBingo;
    public BingoInteractListener(UltimateBingo ultimateBingo){
        this.ultimateBingo = ultimateBingo;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (item.getType() == ultimateBingo.bingoCardMaterial && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Bingo Card")) {
                event.setCancelled(true);
        }
    }

    // Used to catch the player swiping with the bingo card item to open the bingo gui
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack itemInHand = event.getItem();

        if (ultimateBingo.bingoFunctions.isActivePlayer(player)) {

            if (ultimateBingo.playedSinceReboot) {
                // Check if the player is holding the "Bingo card"
                if (itemInHand != null && itemInHand.getType() == ultimateBingo.bingoCardMaterial && itemInHand.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Bingo Card")) {

                    if (ultimateBingo.currentGameMode.equalsIgnoreCase("group") || ultimateBingo.currentGameMode.equalsIgnoreCase("teams")) {
                        if (!ultimateBingo.bingoFunctions.isPlayerInGame(player.getUniqueId())) {
                            player.sendMessage(ChatColor.RED + "Type /bingo to join the game.");
                        } else {
                            ultimateBingo.bingoCommand.openBingo(player);
                        }

                    } else if (ultimateBingo.bingoCardActive || (!ultimateBingo.bingoManager.getBingoGUIs().isEmpty() && ultimateBingo.bingoManager.checkHasBingoCard(player))) {
                        ultimateBingo.bingoCommand.openBingo(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "Bingo hasn't started yet!");
                    }

                    event.setCancelled(true);
                }
            }
        }
    }
}
