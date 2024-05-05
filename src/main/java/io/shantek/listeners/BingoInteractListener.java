package io.shantek.listeners;

import io.shantek.UltimateBingo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
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
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        ItemStack item = event.getItemDrop().getItemStack();

        if (item.getType() == ultimateBingo.bingoCardMaterial && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Bingo Card")) {
            event.setCancelled(true);

        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();


        ItemStack itemInHand = event.getItem();

        // Check if the player is holding the "Bingo card"
        if (itemInHand != null && itemInHand.getType() == ultimateBingo.bingoCardMaterial && itemInHand.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Bingo Card")) {

            // Run the "/bingo" command for the player
            // If this is false, the bingo countdown is still running or they have an old card
            if (ultimateBingo.bingoCardActive) {
                ultimateBingo.bingoCommand.openBingo(player);
            } else {
                player.sendMessage(ChatColor.RED + "Bingo hasn't started yet!");
            }

            event.setCancelled(true);
        }
    }
}
