package io.shantek.listeners;

import io.shantek.managers.BingoManager;
import io.shantek.UltimateBingo;
import io.shantek.tools.MaterialList;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BingoInventoryOpenListener implements Listener {
    UltimateBingo ultimateBingo;
    public BingoInventoryOpenListener(UltimateBingo megaBingo){
        this.ultimateBingo = megaBingo;
    }

    @EventHandler
    public void onInventoryOpen (InventoryOpenEvent e){
        if (e.getPlayer() instanceof Player && ultimateBingo.getBingoManager().isStarted()){
            BingoManager bingoManager = ultimateBingo.getBingoManager();

            if (e.getInventory().getHolder() instanceof Chest){
                Player player = (Player) e.getPlayer();
                Inventory openedChest = e.getInventory();
                ItemStack[] chestItems = openedChest.getContents();
                List<Material> chestMaterials = new ArrayList<>();

                for(int i = 0; i < chestItems.length; i++) {
                    if(chestItems[i] != null) {
                        chestMaterials.add(chestItems[i].getType());
                    }
                }



                UUID uuid = player.getUniqueId();

                Map<UUID, Inventory> bingoGUIs = bingoManager.getBingoGUIs();

                MaterialList materialListObject = ultimateBingo.getMaterialList();

                List<Material> allMaterials = new ArrayList<>();
                allMaterials.addAll(materialListObject.easy);
                allMaterials.addAll(materialListObject.normal);
                allMaterials.addAll(materialListObject.hard);
                allMaterials.addAll(materialListObject.extreme);
                allMaterials.addAll(materialListObject.impossible);

                for (int i = 0; i < chestMaterials.size(); i++){
                    for (Material material : chestMaterials){
                        if (allMaterials.contains(material)){
                            for (int slot : bingoManager.getSlots()){
                                if (bingoGUIs.get(uuid).getItem(slot).getType().equals(chestMaterials.get(i))){
                                    bingoManager.markItemAsComplete(player, chestMaterials.get(i));
                                }
                            }
                        }
                    }
                }


            }



        }
    }
}
