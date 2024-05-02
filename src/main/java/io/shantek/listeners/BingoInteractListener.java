package io.shantek.listeners;

import io.shantek.UltimateBingo;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class BingoInteractListener implements Listener {

    UltimateBingo ultimateBingo;
    public BingoInteractListener(UltimateBingo ultimateBingo){
        this.ultimateBingo = ultimateBingo;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = event.getItem();

        // Check if the player is holding the "Bingo" stick
        if (itemInHand != null && itemInHand.getType() == Material.PAPER && "Bingo".equals(itemInHand.getItemMeta().getDisplayName())) {
            // Run the "/bingo" command for the player

            // If this is false, the bingo countdown is still running or they have an old card
            if (ultimateBingo.bingoCardActive) {
                player.performCommand("bingo");
            } else {
                player.sendMessage(ChatColor.RED + "Bingo hasn't started yet!");

            }

            // Optionally, cancel the event to prevent the stick from being used as a normal stick
            event.setCancelled(true);
        }
    }
}
