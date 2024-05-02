package io.shantek.listeners;

import io.shantek.UltimateBingo;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import java.util.*;

public class BingoInventoryCloseListener implements Listener {

    UltimateBingo ultimateBingo;

    public BingoInventoryCloseListener(UltimateBingo ultimateBingo) {
        this.ultimateBingo = ultimateBingo;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();
        Inventory inventory = event.getInventory();

        // Check if the inventory is a furnace or a blast furnace
        if (inventory.getType() == InventoryType.FURNACE || inventory.getType() == InventoryType.BLAST_FURNACE) {
            // Retrieve the player's Bingo GUI
            Inventory bingoGUI = ultimateBingo.getBingoManager().getBingoGUIs().get(uuid);
            Inventory playerInventory = player.getInventory();

            if (bingoGUI != null) {
                // Check each item in the player's Bingo GUI against items in the player's inventory
                for (int slot : ultimateBingo.getBingoManager().getSlots()) {
                    ItemStack bingoItem = bingoGUI.getItem(slot);
                    if (bingoItem != null && bingoItem.getType() != Material.AIR) {
                        // Check if this item type is present in the player's inventory
                        for (ItemStack item : playerInventory.getContents()) {
                            if (item != null && item.getType() == bingoItem.getType()) {
                                ultimateBingo.getBingoManager().markItemAsComplete(player, item.getType());
                                break; // Once matched, no need to check further for this bingo item
                            }
                        }
                    }
                }
            }
        }
    }


}
