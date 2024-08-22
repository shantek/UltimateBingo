package io.shantek.listeners;

import io.shantek.UltimateBingo;
import io.shantek.managers.BingoPlayerGUIManager;
import io.shantek.tools.MaterialList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BingoPlayerGUIListener implements Listener {
    MaterialList materialList;
    public BingoPlayerGUIManager bingoPlayerGUIManager;
    public UltimateBingo ultimateBingo;
    private boolean sentWarning;

    public BingoPlayerGUIListener(MaterialList materialList, BingoPlayerGUIManager bingoPlayerGUIManager, UltimateBingo ultimateBingo) {
        this.materialList = materialList;
        this.bingoPlayerGUIManager = bingoPlayerGUIManager;
        this.ultimateBingo = ultimateBingo;
        sentWarning = false;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player)) return;
        Player player = (Player) e.getWhoClicked();

        // Check if multi world bingo is enabled and they're in the bingo world
        if (ultimateBingo.multiWorldServer && player.getWorld().getName().equalsIgnoreCase(ultimateBingo.bingoWorld.toLowerCase())) {


            // Ensure the event was triggered in the Bingo configuration GUI
            if (e.getView().getTitle().equals(ChatColor.GOLD.toString() + ChatColor.BOLD + "Welcome to Ultimate Bingo")) {
                e.setCancelled(true);  // Prevent dragging items

                int slot = e.getRawSlot();
                // Ensure clicks are within the inventory size
                if (slot >= 0 && slot < 9) {
                    switch (slot) {
                        case 0:
                            ultimateBingo.bingoFunctions.giveBingoCard(player);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                            break;

                        case 1:
                            player.openInventory(ultimateBingo.bingoPlayerGUIManager.setupPlayersBingoCardsInventory());
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                            break;
                    }
                }

            } else if (e.getView().getTitle().contains("Player Bingo Cards")) {

                // Prevent any items from being dragged
                e.setCancelled(true);

                // Deal with the back to menu
                int slot = e.getRawSlot();

                if (slot == 53) {
                    ItemStack clickedItem = e.getCurrentItem();
                    if (clickedItem != null && clickedItem.getType() == Material.CHEST) {
                        // Check the display name of the item to confirm it's the "Back to Menu" chest
                        if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()
                                && ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()).equals("Back to menu")) {

                            player.closeInventory();
                            player.openInventory(ultimateBingo.bingoPlayerGUIManager.createPlayerGUI(player));
                        }
                    }
                } else {

                    // Ensure the event was triggered in the list of player bingo cards
                    ItemStack clickedItem = e.getCurrentItem();
                    if (clickedItem == null || clickedItem.getType() != Material.PLAYER_HEAD) {
                        return; // Not a valid item, ignore the click
                    }

                    // Get the item's display name and strip color codes to retrieve the player's name
                    String displayName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

                    // Check if a player with this name exists and is online
                    Player targetPlayer = Bukkit.getPlayerExact(displayName);
                    if (targetPlayer == null) {
                        e.getWhoClicked().sendMessage(ChatColor.RED + "The player's bingo card you are trying to access does not exist or they are not online.");
                        return; // No such player found or not online, ignore the click
                    }

                    // Close the current inventory
                    e.getWhoClicked().closeInventory();

                    // Call a method to open the bingo card of the target player
                    ultimateBingo.bingoCommand.openBingoOtherPlayer(player, targetPlayer);

                }
            }
        }
    }
}



