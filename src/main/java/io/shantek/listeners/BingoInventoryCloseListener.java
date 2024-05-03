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
    private final Set<InventoryType> validInventoryTypes;

    public BingoInventoryCloseListener(UltimateBingo ultimateBingo) {
        this.ultimateBingo = ultimateBingo;
        // Initialize the set of valid inventory types
        this.validInventoryTypes = EnumSet.of(
                InventoryType.FURNACE,
                InventoryType.BLAST_FURNACE,
                InventoryType.MERCHANT,
                InventoryType.SMOKER);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();
        Inventory inventory = event.getInventory();

        // Check if the inventory is one of the valid types
        if (validInventoryTypes.contains(inventory.getType())) {
            Inventory bingoGUI = ultimateBingo.getBingoManager().getBingoGUIs().get(uuid);
            Inventory playerInventory = player.getInventory();

            if (bingoGUI != null) {
                for (int slot : ultimateBingo.getBingoManager().getSlots()) {
                    ItemStack bingoItem = bingoGUI.getItem(slot);
                    if (bingoItem != null && bingoItem.getType() != Material.AIR) {
                        for (ItemStack item : playerInventory.getContents()) {
                            if (item != null && item.getType() == bingoItem.getType()) {
                                ultimateBingo.getBingoManager().markItemAsComplete(player, item.getType());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}