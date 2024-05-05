package io.shantek.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BingoGUIListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        // Cancel all interactions in inventories with "Bingo" in the title
        if (ChatColor.translateAlternateColorCodes('&', e.getView().getTitle()).contains("Bingo")) {
            e.setCancelled(true);
      }

    }


}

