package io.shantek.listeners;

import io.shantek.managers.BingoManager;
import io.shantek.UltimateBingo;
import io.shantek.tools.MaterialList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BingoPickupListener implements Listener {
    UltimateBingo ultimateBingo;
    public BingoPickupListener(UltimateBingo ultimateBingo){
        this.ultimateBingo = ultimateBingo;
    }

    @EventHandler
    public void onPickup (PlayerPickupItemEvent e){
        if (ultimateBingo.getBingoManager().isStarted()){
            BingoManager bingoManager = ultimateBingo.getBingoManager();
            Material pickedItem = e.getItem().getItemStack().getType();
            Player player = (Player) e.getPlayer();


            UUID uuid = player.getUniqueId();

            Map<UUID, Inventory> bingoGUIs = bingoManager.getBingoGUIs();

            MaterialList materialListObject = ultimateBingo.getMaterialList();

            List<Material> allMaterials = new ArrayList<>();
            allMaterials.addAll(materialListObject.easy);
            allMaterials.addAll(materialListObject.normal);
            allMaterials.addAll(materialListObject.hard);
            allMaterials.addAll(materialListObject.extreme);
            allMaterials.addAll(materialListObject.impossible);

            if (allMaterials.contains(pickedItem)){
                for (int i : bingoManager.getSlots()) {
                    Inventory bingoGUI = bingoGUIs.get(uuid);
                    if (bingoGUI != null) {
                        ItemStack bingoItem = bingoGUI.getItem(i);
                        if (bingoItem != null && bingoItem.getType() == pickedItem) {
                            bingoManager.markItemAsComplete(player, pickedItem);
                        }
                    }
                }
            }

        }


    }
}
