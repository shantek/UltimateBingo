package io.shantek.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BingoGUIListener implements Listener {
    @EventHandler
    public void onInventoryClick (InventoryClickEvent e){
        if (ChatColor.translateAlternateColorCodes('&',
                e.getView().getTitle()).equals(ChatColor.GOLD.toString() + ChatColor.BOLD + "Ultimate Bingo") && e.getCurrentItem() != null){
            e.setCancelled(true);
        }
    }
}
