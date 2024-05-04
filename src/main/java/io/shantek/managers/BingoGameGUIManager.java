package io.shantek.managers;

import io.shantek.UltimateBingo;
import io.shantek.tools.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BingoGameGUIManager {
    UltimateBingo ultimateBingo;

    public BingoGameGUIManager(UltimateBingo ultimateBingo) {
        this.ultimateBingo = ultimateBingo;
    }

    public Inventory createGameGUI(Player player) {
        Inventory gameConfigInventory = Bukkit.createInventory(player, 9, ChatColor.GOLD.toString() + ChatColor.BOLD + "Bingo Configuration");

        gameConfigInventory.setItem(0, createItem(Material.PAPER, "Game mode", ultimateBingo.gameMode));
        gameConfigInventory.setItem(1, createItem(Material.IRON_SWORD, "Difficulty", ultimateBingo.difficulty));
        gameConfigInventory.setItem(2, createItem(Material.MAP, "Card Size", ultimateBingo.cardSize));
        gameConfigInventory.setItem(3, createItem(Material.NAME_TAG, "Card Type", ultimateBingo.uniqueCard ? "UNIQUE" : "IDENTICAL"));
        gameConfigInventory.setItem(4, createItem(Material.BEACON, "Win Condition", ultimateBingo.fullCard ? "FULL CARD" : "SINGLE ROW"));
        gameConfigInventory.setItem(8, createStartGameItem());

        return gameConfigInventory;
    }

    private ItemStack createItem(Material material, String prefix, String currentValue) {
        return new ItemBuilder(material)
                .withDisplayName(ChatColor.BLUE + prefix + ": " + currentValue.toUpperCase())
                .withLore(ChatColor.GRAY + "Click to toggle " + prefix.toLowerCase()).build();
    }

    private ItemStack createStartGameItem() {
        return new ItemBuilder(Material.ENDER_PEARL)
                .withDisplayName(ChatColor.GREEN + "Start Game")
                .withLore(ChatColor.GRAY + "Click to start the game").build();
    }

    public void toggleGameMode(Player player) {
        // Assuming there are exactly two game modes.
        ultimateBingo.gameMode = ultimateBingo.gameMode.equals("TRADITIONAL") ? "SPEED RUN" : "TRADITIONAL";;
        updateGUI(player);
    }

    public void toggleDifficulty(Player player) {
        // Cycle through difficulties. Assuming there are three fixed difficulties.
        String current = ultimateBingo.difficulty;
        ultimateBingo.difficulty = current.equals("EASY") ? "NORMAL" : current.equals("NORMAL") ? "HARD" : "EASY";
        updateGUI(player);
    }

    public void toggleCardSize(Player player) {
        // Cycle through card sizes. Assuming there are three fixed sizes.
        String current = ultimateBingo.cardSize;
        ultimateBingo.cardSize = current.equals("SMALL") ? "MEDIUM" : current.equals("MEDIUM") ? "LARGE" : "SMALL";
        updateGUI(player);
    }

    public void toggleCardType(Player player) {
        // Toggle boolean value
        ultimateBingo.uniqueCard = !ultimateBingo.uniqueCard;
        updateGUI(player);
    }

    public void toggleWinCondition(Player player) {
        // Toggle boolean value
        ultimateBingo.fullCard = !ultimateBingo.fullCard;
        updateGUI(player);
    }

    private void updateGUI(Player player) {
        player.closeInventory();
        player.openInventory(createGameGUI(player));
    }
}
