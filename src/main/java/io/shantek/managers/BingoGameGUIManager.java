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
        gameConfigInventory.setItem(0, createItem(setGUIIcon("gamemode"), "Game mode", ultimateBingo.gameMode));
        gameConfigInventory.setItem(1, createItem(setGUIIcon("difficulty"), "Difficulty", ultimateBingo.difficulty));
        gameConfigInventory.setItem(2, createItem(setGUIIcon("cardsize"), "Card Size", ultimateBingo.cardSize));
        gameConfigInventory.setItem(3, createItem(setGUIIcon("uniqueCard"), "Card Type", ultimateBingo.uniqueCard ? "UNIQUE" : "IDENTICAL"));
        gameConfigInventory.setItem(4, createItem(setGUIIcon("wincondition"), "Win Condition", ultimateBingo.fullCard ? "FULL CARD" : "SINGLE ROW"));
        gameConfigInventory.setItem(5, createItem(setGUIIcon("reveal"), "Reveal Cards", ultimateBingo.revealCards ? "ENABLED" : "DISABLED"));

        // Work out the game time to display
        String gameTimeString;
        if (ultimateBingo.gameTime == 0) {
            gameTimeString = "Unlimited Time";
        } else {
            gameTimeString = ultimateBingo.gameTime + " minutes";
        }
        gameConfigInventory.setItem(6, createItem(Material.CLOCK, "Time Limit", gameTimeString));

        // Work out the game loadout to give the player
        String gameLoadoutString = "Empty Inventory";
        if (ultimateBingo.loadoutType == 1) {
            gameLoadoutString = "Basic Kit";
        } else if (ultimateBingo.loadoutType == 2) {
            gameLoadoutString = "Boat Kit";
        } else if (ultimateBingo.loadoutType == 3) {
            gameLoadoutString = "Flying Kit";
        }
        gameConfigInventory.setItem(7, createItem(setGUIIcon("loadout"), "Player Loadout", gameLoadoutString));
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

    //region Toggle and update the GUI

    public void toggleGameMode(Player player) {
        switch (ultimateBingo.gameMode.toLowerCase()) {
            case "traditional":
                ultimateBingo.gameMode = "speedrun";
                break;
            case "speedrun":
                ultimateBingo.gameMode = "traditional";
                break;

        }

        updateGUI(player);
    }

    public void toggleDifficulty(Player player) {
        // Cycle through difficulties. Assuming there are three fixed difficulties.
        switch (ultimateBingo.difficulty.toLowerCase()) {
            case "easy":
                ultimateBingo.difficulty = "normal";
                break;
            case "normal":
                ultimateBingo.difficulty = "hard";
                break;
            case "hard":
                ultimateBingo.difficulty = "easy";
                break;
        }

        updateGUI(player);
    }

    public void toggleCardSize(Player player) {
        switch (ultimateBingo.cardSize.toLowerCase()) {
            case "small":
                ultimateBingo.cardSize = "medium";
                break;
            case "medium":
                ultimateBingo.cardSize = "large";
                break;
            case "large":
                ultimateBingo.cardSize = "small";
                break;
        }
        updateGUI(player);
    }

    public void toggleGameTime(Player player) {
        switch (ultimateBingo.gameTime) {
            case 0:
                ultimateBingo.gameTime = 5;
                break;
            case 5:
                ultimateBingo.gameTime = 10;
                break;
            case 10:
                ultimateBingo.gameTime = 15;
                break;
            case 15:
                ultimateBingo.gameTime = 20;
                break;
            case 20:
                ultimateBingo.gameTime = 30;
                break;
            case 30:
                ultimateBingo.gameTime = 40;
                break;
            case 40:
                ultimateBingo.gameTime = 50;
                break;
            case 50:
                ultimateBingo.gameTime = 60;
                break;
            case 60:
                ultimateBingo.gameTime = 0;
                break;
        }
        updateGUI(player);
    }

    public void toggleLoadout(Player player) {
        switch (ultimateBingo.loadoutType) {
            case 0:
                ultimateBingo.loadoutType = 1;
                break;
            case 1:
                ultimateBingo.loadoutType = 2;
                break;
            case 2:
                ultimateBingo.loadoutType = 3;
                break;
            case 3:
                ultimateBingo.loadoutType = 0;
                break;

        }
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

    public void toggleRevealCards(Player player) {
        // Toggle boolean value
        ultimateBingo.revealCards = !ultimateBingo.revealCards;
        updateGUI(player);
    }

    private void updateGUI(Player player) {
        Inventory currentInventory = player.getOpenInventory().getTopInventory();
        ItemStack startGameItem = currentInventory.getItem(8);  // This is the "Start Game" item in slot 8

        if (startGameItem != null && startGameItem.hasItemMeta() && startGameItem.getItemMeta().hasDisplayName() &&
                ChatColor.stripColor(startGameItem.getItemMeta().getDisplayName()).equals("Start Game")) {
            // The inventory is confirmed to be the Bingo Configuration GUI
            // Update existing inventory directly
            currentInventory.setItem(0, createItem(setGUIIcon("gamemode"), "Game mode", ultimateBingo.gameMode));
            currentInventory.setItem(1, createItem(setGUIIcon("difficulty"), "Difficulty", ultimateBingo.difficulty));
            currentInventory.setItem(2, createItem(setGUIIcon("cardsize"), "Card Size", ultimateBingo.cardSize));
            currentInventory.setItem(3, createItem(setGUIIcon("uniqueCard"), "Card Type", ultimateBingo.uniqueCard ? "UNIQUE" : "IDENTICAL"));
            currentInventory.setItem(4, createItem(setGUIIcon("wincondition"), "Win Condition", ultimateBingo.fullCard ? "FULL CARD" : "SINGLE ROW"));
            currentInventory.setItem(5, createItem(setGUIIcon("reveal"), "Reveal Cards", ultimateBingo.revealCards ? "ENABLED" : "DISABLED"));

            // Work out the game time to display
            String gameTimeString;
            if (ultimateBingo.gameTime == 0) {
                gameTimeString = "Unlimited Time";
            } else {
                gameTimeString = ultimateBingo.gameTime + " minutes";
            }
            currentInventory.setItem(6, createItem(Material.CLOCK, "Time Limit", gameTimeString));

            // Work out the game loadout to give the player
            String gameLoadoutString = "Empty Inventory";
            if (ultimateBingo.loadoutType == 1) {
                gameLoadoutString = "Basic Kit";
            } else if (ultimateBingo.loadoutType == 2) {
                gameLoadoutString = "Boat Kit";
            } else if (ultimateBingo.loadoutType == 3) {
                gameLoadoutString = "Flying Kit";
            }
            currentInventory.setItem(7, createItem(setGUIIcon("loadout"), "Player Loadout", gameLoadoutString));


        } else {
            // If not viewing the Bingo configuration, open it
            player.openInventory(createGameGUI(player));
        }
    }

    //endregion

    private Material setGUIIcon(String type) {

        Material materialToDisplay = Material.AIR;

        if (type.equalsIgnoreCase("loadout")) {
            return switch (ultimateBingo.loadoutType) {
                case 1 -> materialToDisplay = Material.WOODEN_PICKAXE; // Starter kit
                case 2 -> materialToDisplay = Material.OAK_BOAT; // Boat kit
                case 3 -> materialToDisplay = Material.FIREWORK_ROCKET; // Rocket kit
                default -> materialToDisplay = Material.CRAFTING_TABLE; // Empty inventory loadout
            };

        } else if (type.equalsIgnoreCase("difficulty")) {
            return switch (ultimateBingo.difficulty) {
                case "easy" -> materialToDisplay = Material.COPPER_INGOT;
                case "hard" -> materialToDisplay = Material.NETHERITE_INGOT;
                default -> materialToDisplay = Material.IRON_INGOT;

            };
        } else if (type.equalsIgnoreCase("cardsize")) {
            return switch (ultimateBingo.cardSize) {
                case "small" -> materialToDisplay = Material.PAPER;
                case "medium" -> materialToDisplay = Material.BOOK;
                default -> materialToDisplay = Material.WRITABLE_BOOK;

            };

        } else if (type.equalsIgnoreCase("gamemode")) {
            if (ultimateBingo.gameMode.equalsIgnoreCase("speedrun")) {
                materialToDisplay = Material.DIAMOND_BOOTS;
            } else {
                materialToDisplay = Material.FURNACE;
            }
        } else if (type.equalsIgnoreCase("uniquecard")) {
            if (ultimateBingo.uniqueCard) {
                materialToDisplay = Material.FILLED_MAP;
            } else {
                materialToDisplay = Material.MAP;
            }
        } else if (type.equalsIgnoreCase("wincondition")) {
            if (ultimateBingo.fullCard) {
                materialToDisplay = ultimateBingo.tickedItemMaterial;
            } else {
                materialToDisplay = Material.BLAZE_ROD;
            }
        } else if (type.equalsIgnoreCase("reveal")) {
            if (ultimateBingo.revealCards) {
                materialToDisplay = Material.SPYGLASS;
            } else {
                materialToDisplay = Material.BLACK_CONCRETE;
            }
        }


        return materialToDisplay;


    }
}

