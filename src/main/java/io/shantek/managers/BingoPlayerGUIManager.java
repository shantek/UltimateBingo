package io.shantek.managers;

import io.shantek.UltimateBingo;
import io.shantek.tools.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BingoPlayerGUIManager {
    UltimateBingo ultimateBingo;

    public BingoPlayerGUIManager(UltimateBingo ultimateBingo) {
        this.ultimateBingo = ultimateBingo;
    }

    public Inventory createPlayerGUI(Player player) {
        Inventory gameConfigInventory = Bukkit.createInventory(player, 9, ChatColor.GOLD.toString() + ChatColor.BOLD + "Welcome to Ultimate Bingo");

        gameConfigInventory.setItem(0, createItem(ultimateBingo.bingoCardMaterial, "Replacement Bingo Card", "Win condition: " + ultimateBingo.fullCard1.toUpperCase()));

        // Only show the reveal cards option if this is enabled
        if (ultimateBingo.currentRevealCards) { gameConfigInventory.setItem(1, createItem(Material.SPYGLASS, "View Players Cards", "Get a peak at other players cards!"));}

        return gameConfigInventory;
    }

    private ItemStack createItem(Material material, String displayname, String lore) {
        return new ItemBuilder(material)
                .withDisplayName(ChatColor.BLUE + displayname)
                .withLore(ChatColor.GRAY + lore).build();
    }

    public Inventory setupPlayersBingoCardsInventory() {
        // Create a 54-slot inventory with a custom title
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.GOLD.toString() + ChatColor.BOLD + "Player Bingo Cards");

        // Get all online players and populate the inventory
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (ultimateBingo.bingoManager.bingoGUIs.containsKey(player.getUniqueId())) {  // Check if the player has a bingo card
                ItemStack item = new ItemStack(ultimateBingo.bingoCardMaterial);
                ItemMeta meta = item.getItemMeta();

                if (meta != null) {
                    meta.setDisplayName(ChatColor.GREEN + player.getName());
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Click to view " + player.getName() + "'s Bingo Card");
                    meta.setLore(lore);  // Set lore
                    item.setItemMeta(meta);  // Apply the meta back to the item

                    int countCompleted = ultimateBingo.bingoFunctions.countCompleted(ultimateBingo.bingoManager.getBingoGUIs().get(player.getUniqueId()));
                    if (countCompleted > 0) {
                        item.setAmount(countCompleted);
                    }

                    inventory.addItem(item);  // Add the item to the inventory
                }
            }
        }

        // Add a 'Back to menu' chest in the last slot
        ItemStack backToMenu = new ItemStack(Material.CHEST);
        ItemMeta backToMenuMeta = backToMenu.getItemMeta();
        if (backToMenuMeta != null) {
            backToMenuMeta.setDisplayName(ChatColor.RED + "Back to menu");
            backToMenu.setItemMeta(backToMenuMeta);
        }
        inventory.setItem(53, backToMenu);  // Set the chest in the last slot

        return inventory;
    }
}
