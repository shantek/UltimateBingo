package io.shantek.managers;

import io.shantek.UltimateBingo;
import io.shantek.tools.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BingoPlayerGUIManager {
    UltimateBingo ultimateBingo;

    public BingoPlayerGUIManager(UltimateBingo ultimateBingo) {
        this.ultimateBingo = ultimateBingo;
    }

    public Inventory createPlayerGUI(Player player) {
        Inventory gameConfigInventory = Bukkit.createInventory(player, 9, ChatColor.GOLD.toString() + ChatColor.DARK_GRAY + "Welcome to Ultimate Bingo");

        gameConfigInventory.setItem(0, createItem(ultimateBingo.bingoCardMaterial, "Replacement Bingo Card", "Win condition: " + ultimateBingo.fullCard.toUpperCase()));

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
        Inventory inventory = null;

        // Do team cards and use wool

        if (ultimateBingo.currentGameMode.equalsIgnoreCase("teams")) {
            inventory = Bukkit.createInventory(null, 54, ChatColor.GOLD.toString() + ChatColor.DARK_GRAY + "Team Bingo Cards");


            if (ultimateBingo.bingoFunctions.isRedTeamNotEmpty()) {
                ItemStack redTeam = new ItemStack(Material.RED_WOOL);
                ItemMeta redMeta = redTeam.getItemMeta();
                if (redMeta != null) {
                    redMeta.setDisplayName(ChatColor.RED + "Red Team");
                    redMeta.setLore(Arrays.asList(ultimateBingo.bingoFunctions.getRedTeamPlayerNames().split(", ")));
                    redTeam.setItemMeta(redMeta);

                    inventory.addItem(redTeam);
                }
            }

            if (ultimateBingo.bingoFunctions.isBlueTeamNotEmpty()) {
                ItemStack blueTeam = new ItemStack(Material.BLUE_WOOL);
                ItemMeta blueMeta = blueTeam.getItemMeta();
                if (blueMeta != null) {
                    blueMeta.setDisplayName(ChatColor.BLUE + "Blue Team");
                    blueMeta.setLore(Arrays.asList(ultimateBingo.bingoFunctions.getBlueTeamPlayerNames().split(", ")));
                    blueTeam.setItemMeta(blueMeta);
                    inventory.addItem(blueTeam);
                }
            }

            if (ultimateBingo.bingoFunctions.isYellowTeamNotEmpty()) {
                ItemStack yellowTeam = new ItemStack(Material.YELLOW_WOOL);
                ItemMeta yellowMeta = yellowTeam.getItemMeta();
                if (yellowMeta != null) {
                    yellowMeta.setDisplayName(ChatColor.YELLOW + "Yellow Team");
                    yellowMeta.setLore(Arrays.asList(ultimateBingo.bingoFunctions.getYellowTeamPlayerNames().split(", ")));
                    yellowTeam.setItemMeta(yellowMeta);
                    inventory.addItem(yellowTeam);
                }
            }

        } else {

            inventory = Bukkit.createInventory(null, 54, ChatColor.GOLD.toString() + ChatColor.DARK_GRAY + "Player Bingo Cards");

            // Get all online players and populate the inventory
            for (Player player : Bukkit.getOnlinePlayers()) {

                if (ultimateBingo.bingoFunctions.isActivePlayer(player)) {

                    if (ultimateBingo.bingoManager.bingoGUIs.containsKey(player.getUniqueId())) {  // Check if the player has a bingo card
                        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
                        meta.setOwningPlayer(player);

                        if (meta != null) {
                            meta.setDisplayName(ChatColor.GREEN + player.getName());
                            List<String> lore = new ArrayList<>();
                            lore.add(ChatColor.GRAY + "Click to view " + player.getName() + "'s Bingo Card");
                            meta.setLore(lore);  // Set lore
                            playerHead.setItemMeta(meta);

                            int countCompleted = ultimateBingo.bingoFunctions.countCompleted(ultimateBingo.bingoManager.getBingoGUIs().get(player.getUniqueId()));
                            if (countCompleted > 0) {
                                playerHead.setAmount(countCompleted);
                            }

                            inventory.addItem(playerHead);  // Add the item to the inventory
                        }
                    }
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
