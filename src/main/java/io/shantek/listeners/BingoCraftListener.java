package io.shantek.listeners;

import io.shantek.managers.BingoManager;
import io.shantek.UltimateBingo;
import io.shantek.tools.MaterialList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BingoCraftListener implements Listener {

    UltimateBingo ultimateBingo;

    public BingoCraftListener(UltimateBingo ultimateBingo) {
        this.ultimateBingo = ultimateBingo;
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if (e.getWhoClicked() instanceof Player && ultimateBingo.getBingoManager().isStarted()) {
            BingoManager bingoManager = ultimateBingo.getBingoManager();
            Material craftedItem = e.getRecipe().getResult().getType();
            Player player = (Player) e.getWhoClicked();

            UUID uuid = player.getUniqueId();
            Map<UUID, Inventory> bingoGUIs = bingoManager.getBingoGUIs();

            MaterialList materialListObject = ultimateBingo.getMaterialList();

            List<Material> allMaterials = new ArrayList<>();
            allMaterials.addAll(materialListObject.easy);
            allMaterials.addAll(materialListObject.normal);
            allMaterials.addAll(materialListObject.hard);
            allMaterials.addAll(materialListObject.extreme);
            allMaterials.addAll(materialListObject.impossible);

            if (allMaterials.contains(craftedItem) && bingoGUIs.containsKey(uuid)) {
                Inventory bingoGUI = bingoGUIs.get(uuid);
                for (int i : bingoManager.getSlots()) {
                    ItemStack item = bingoGUI.getItem(i);
                    if (item != null && item.getType().equals(craftedItem)) {
                        bingoManager.markItemAsComplete(player, craftedItem);
                    }
                }
            }
        }
    }
}
