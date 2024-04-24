package io.shantek.listeners;

import io.shantek.UltimateBingo;
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
        if (itemInHand != null && itemInHand.getType() == Material.COMPASS && "Bingo".equals(itemInHand.getItemMeta().getDisplayName())) {
            // Run the "/bingo" command for the player
            player.performCommand("bingo");

            // Optionally, cancel the event to prevent the stick from being used as a normal stick
            event.setCancelled(true);
        }
    }
}
