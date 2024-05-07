package io.shantek.listeners;

import io.shantek.UltimateBingo;
import io.shantek.managers.BingoPlayerGUIManager;
import io.shantek.tools.MaterialList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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


            // Check if they clicked any settings items
            int slot = e.getRawSlot();

            switch (slot) {
                case 17:
                    // Spyglass item was clicked - Check if the option is enabled and open the player cards menu
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

