package io.shantek.listeners;

import io.shantek.managers.BingoManager;
import io.shantek.UltimateBingo;
import io.shantek.tools.MaterialList;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BingoFurnaceRemoveListener implements Listener {
    UltimateBingo ultimateBingo;
    public BingoFurnaceRemoveListener(UltimateBingo ultimateBingo){
        this.ultimateBingo = ultimateBingo;
    }

    @EventHandler
    public void onFurnaceItemRemove(InventoryMoveItemEvent e){
        // Check if the source of the event is a furnace
        if (e.getSource().getHolder() instanceof Furnace) {

            // Get the item mover
            InventoryHolder destinationHolder = e.getDestination().getHolder();
            // Ensure it's a player
            if (destinationHolder instanceof Player) {
                Player player = (Player) destinationHolder;
                // Get the destination inventory
                Inventory destinationInventory = e.getDestination();
                // Get the removed item
                ItemStack removedItem = e.getItem();

                // Check if player, item, and bingo are all valid and started
                if (player != null && removedItem != null && ultimateBingo.bingoManager.isStarted()) {
                    // Get the player's UUID
                    UUID uuid = player.getUniqueId();

                    BingoManager bingoManager = ultimateBingo.getBingoManager();

                    Map<UUID, Inventory> bingoGUIs = bingoManager.getBingoGUIs();
                    MaterialList materialListObject = ultimateBingo.getMaterialList();

                    List<Material> allMaterials = new ArrayList<>();
                    allMaterials.addAll(materialListObject.easy);
                    allMaterials.addAll(materialListObject.normal);
                    allMaterials.addAll(materialListObject.hard);
                    allMaterials.addAll(materialListObject.extreme);
                    allMaterials.addAll(materialListObject.impossible);

                    Inventory bingoGUI = bingoGUIs.get(uuid);
                    if (allMaterials.contains(removedItem.getType())) {
                        for (int slot : bingoManager.getSlots()) {
                            ItemStack bingoItem = bingoGUI.getItem(slot);
                            if (bingoItem != null && bingoItem.getType() == removedItem.getType()) {
                                bingoManager.markItemAsComplete(player, removedItem.getType());
                                break;
                            }
                        }
                    }


                }
            }
        }
    }
    }