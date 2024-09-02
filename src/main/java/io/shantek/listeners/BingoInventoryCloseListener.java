package io.shantek.listeners;

import io.shantek.UltimateBingo;
import org.bukkit.Material;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import java.util.*;

public class BingoInventoryCloseListener implements Listener {

    UltimateBingo ultimateBingo;
    private final Set<InventoryType> validInventoryTypes;

    // All the inventory types listed here will be included in the close listener.
    // Use this to refine what you want to check, should you decide to change it.
    public BingoInventoryCloseListener(UltimateBingo ultimateBingo) {
        this.ultimateBingo = ultimateBingo;
        // Initialize the set of valid inventory types
        this.validInventoryTypes = EnumSet.of(
                InventoryType.FURNACE,
                InventoryType.BLAST_FURNACE,
                InventoryType.MERCHANT,
                InventoryType.CRAFTING,
                InventoryType.CHEST,
                InventoryType.BARREL,
                InventoryType.ANVIL,
                InventoryType.BREWING,
                InventoryType.CARTOGRAPHY,
                InventoryType.ENCHANTING,
                InventoryType.LOOM,
                InventoryType.SMITHING,
                InventoryType.STONECUTTER,
                InventoryType.SMOKER,
                InventoryType.PLAYER,
                InventoryType.WORKBENCH);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        if (ultimateBingo.bingoStarted) {
            Player player = (Player) event.getPlayer();

            if (ultimateBingo.bingoFunctions.isActivePlayer(player)) {

                UUID uuid = player.getUniqueId();
                Inventory inventory = event.getInventory();
                InventoryHolder holder = inventory.getHolder();

                boolean isValidType = validInventoryTypes.contains(inventory.getType());
                boolean isSpecialChest = (holder instanceof StorageMinecart);

                // Check if the inventory type is valid or if it's a special chest
                if (isValidType || isSpecialChest) {
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
    }
}
