package io.shantek.listeners;

import io.shantek.UltimateBingo;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BingoGUIListener implements Listener {

    public UltimateBingo ultimateBingo;
    public BingoGUIListener(UltimateBingo ultimateBingo) {
        this.ultimateBingo = ultimateBingo;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        // Cancel all interactions in inventories with "Bingo" in the title
        if (ChatColor.translateAlternateColorCodes('&', e.getView().getTitle()).contains("Bingo")) {

            // Get the player who clicked in the inventory
            Player player = (Player) e.getWhoClicked();
            int slot = e.getRawSlot();

            // Spyglass item was clicked - Check if the option is enabled and open the player cards menu
            if (slot == 17) {
                if (ultimateBingo.revealCards) {
                    e.setCancelled(true);
                    player.closeInventory();
                    player.openInventory(ultimateBingo.bingoPlayerGUIManager.setupPlayersBingoCardsInventory());
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                }
            }
            // Cancel all other events
            e.setCancelled(true);
        }
    }
}

