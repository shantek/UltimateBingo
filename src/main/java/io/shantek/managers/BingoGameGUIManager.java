package io.shantek.managers;

import io.shantek.UltimateBingo;
import io.shantek.tools.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BingoGameGUIManager {
    UltimateBingo ultimateBingo;

    private Map<String, String[]> optionsMap;

    public BingoGameGUIManager(UltimateBingo ultimateBingo) {
        this.ultimateBingo = ultimateBingo;

        optionsMap = new HashMap<>();
        optionsMap.put("difficulty", new String[]{"easy", "normal", "hard"});
        optionsMap.put("cardSize", new String[]{"small", "medium", "large"});
        optionsMap.put("gameMode", new String[]{"speedrun", "traditional", "brewdash", "group", "teams"});
        optionsMap.put("uniqueCard", new String[]{"unique", "identical"});
        optionsMap.put("fullCard", new String[]{"full card", "single row"});
        optionsMap.put("revealCards", new String[]{"enabled", "disabled"});
    }


    public Inventory createGameGUI(Player player) {
        Inventory gameConfigInventory = Bukkit.createInventory(player, 9, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Bingo Configuration");
        gameConfigInventory.setItem(0, createItem(setGUIIcon("gamemode"), "Game mode", ultimateBingo.gameMode));
        gameConfigInventory.setItem(1, createItem(setGUIIcon("difficulty"), "Difficulty", ultimateBingo.difficulty));
        gameConfigInventory.setItem(2, createItem(setGUIIcon("cardsize"), "Card Size", ultimateBingo.cardSize));
        if (!ultimateBingo.gameMode.equalsIgnoreCase("group")) {
            gameConfigInventory.setItem(3, createItem(setGUIIcon("uniqueCard"), "Card Type", ultimateBingo.uniqueCard.toUpperCase()));
        } else {
            gameConfigInventory.setItem(3,null);
        }
        gameConfigInventory.setItem(4, createItem(setGUIIcon("wincondition"), "Win Condition", ultimateBingo.fullCard.toUpperCase()));
        gameConfigInventory.setItem(5, createItem(setGUIIcon("reveal"), "Reveal Cards", ultimateBingo.revealCards.toUpperCase()));

        // Work out the game time to display
        String gameTimeString;
        if (ultimateBingo.gameTime == 0) {
            gameTimeString = "Unlimited Time";
        } else {
            gameTimeString = ultimateBingo.gameTime + " minutes";
        }
        gameConfigInventory.setItem(6, createItem(Material.CLOCK, "Time Limit", gameTimeString));

        // Work out the game loadout to give the player
        String gameLoadoutString = "Naked Kit";
        if (ultimateBingo.loadoutType == 1) {
            gameLoadoutString = "Basic Kit";
        } else if (ultimateBingo.loadoutType == 2) {
            gameLoadoutString = "Boat Kit";
        } else if (ultimateBingo.loadoutType == 3) {
            gameLoadoutString = "Flying Kit";
        } else if (ultimateBingo.loadoutType == 4) {
            gameLoadoutString = "Archer Kit";
        } else if (ultimateBingo.loadoutType == 50) {
            gameLoadoutString = "Random Kit";
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
                ultimateBingo.gameMode = "brewdash";
                break;
            case "brewdash":
                ultimateBingo.gameMode = "group";
                break;
            case "group":
                ultimateBingo.gameMode = "teams";
                break;
            case "teams":
                ultimateBingo.gameMode = "random";
                break;
            case "random":
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
                ultimateBingo.difficulty = "random";
                break;
            case "random":
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
                ultimateBingo.cardSize = "random";
                break;
            case "random":
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
                ultimateBingo.loadoutType = 4;
                break;
            case 4:
                ultimateBingo.loadoutType = 50;
                break;
            case 50:
                ultimateBingo.loadoutType = 0;
                break;

        }
        updateGUI(player);
    }

    public void toggleCardType(Player player) {
        switch (ultimateBingo.uniqueCard) {
            case "unique":
                ultimateBingo.uniqueCard = "identical";
                break;
            case "identical":
                ultimateBingo.uniqueCard = "random";
                break;
            case "random":
                ultimateBingo.uniqueCard = "unique";
                break;
        }
        updateGUI(player);
    }

    public void toggleWinCondition(Player player) {
        switch (ultimateBingo.fullCard) {
            case "full card":
                ultimateBingo.fullCard = "single row";
                break;
            case "single row":
                ultimateBingo.fullCard = "random";
                break;
            case "random":
                ultimateBingo.fullCard = "full card";
                break;
        }
        updateGUI(player);
    }

    public void toggleRevealCards(Player player) {
        switch (ultimateBingo.revealCards) {
            case "enabled":
                ultimateBingo.revealCards = "disabled";
                break;
            case "disabled":
                ultimateBingo.revealCards = "random";
                break;
            case "random":
                ultimateBingo.revealCards = "enabled";
                break;
        }
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
            if (!ultimateBingo.gameMode.equalsIgnoreCase("group")) {
                currentInventory.setItem(3, createItem(setGUIIcon("uniqueCard"), "Card Type", ultimateBingo.uniqueCard.toUpperCase()));
            } else {
                currentInventory.setItem(3,null);
            }
            currentInventory.setItem(4, createItem(setGUIIcon("wincondition"), "Win Condition", ultimateBingo.fullCard.toUpperCase()));
            currentInventory.setItem(5, createItem(setGUIIcon("reveal"), "Reveal Cards", ultimateBingo.revealCards.toUpperCase()));

            // Work out the game time to display
            String gameTimeString;
            if (ultimateBingo.gameTime == 0) {
                gameTimeString = "Unlimited Time";
            } else {
                gameTimeString = ultimateBingo.gameTime + " minutes";
            }
            currentInventory.setItem(6, createItem(Material.CLOCK, "Time Limit", gameTimeString));

            // Work out the game loadout to give the player
            String gameLoadoutString = "Naked Kit";
            if (ultimateBingo.loadoutType == 1) {
                gameLoadoutString = "Basic Kit";
            } else if (ultimateBingo.loadoutType == 2) {
                gameLoadoutString = "Boat Kit";
            } else if (ultimateBingo.loadoutType == 3) {
                gameLoadoutString = "Flying Kit";
            } else if (ultimateBingo.loadoutType == 4) {
                gameLoadoutString = "Archer Kit";
            } else if (ultimateBingo.loadoutType == 50) {
                gameLoadoutString = "Random Kit";
            }


            currentInventory.setItem(7, createItem(setGUIIcon("loadout"), "Player Loadout", gameLoadoutString));


        } else {
            // If not viewing the Bingo configuration, open it
            player.openInventory(createGameGUI(player));
        }
    }

    //endregion

    public void setGameConfiguration() {

        ultimateBingo.currentLoadoutType = ultimateBingo.bingoFunctions.validateOrDefaultInt(ultimateBingo.loadoutType, 5, 0);
        ultimateBingo.currentDifficulty = ultimateBingo.bingoFunctions.validateOrDefault(ultimateBingo.difficulty, optionsMap.get("difficulty"), "normal");
        ultimateBingo.currentCardSize = ultimateBingo.bingoFunctions.validateOrDefault(ultimateBingo.cardSize, optionsMap.get("cardSize"), "medium");
        ultimateBingo.currentGameMode = ultimateBingo.bingoFunctions.validateOrDefault(ultimateBingo.gameMode, optionsMap.get("gameMode"), "traditional");
        ultimateBingo.currentUniqueCard = ultimateBingo.bingoFunctions.validateOrDefaultBoolean(ultimateBingo.uniqueCard, optionsMap.get("uniqueCard"), true);
        ultimateBingo.currentFullCard = ultimateBingo.bingoFunctions.validateOrDefaultBoolean(ultimateBingo.fullCard, optionsMap.get("fullCard"), true);
        ultimateBingo.currentRevealCards = ultimateBingo.bingoFunctions.validateOrDefaultBoolean(ultimateBingo.revealCards, optionsMap.get("revealCards"), true);

    }

    private Material setGUIIcon(String type) {

        Material materialToDisplay = Material.AIR;

        if (type.equalsIgnoreCase("loadout")) {
            return switch (ultimateBingo.loadoutType) {
                case 1 -> materialToDisplay = Material.WOODEN_PICKAXE; // Starter kit
                case 2 -> materialToDisplay = Material.OAK_BOAT; // Boat kit
                case 3 -> materialToDisplay = Material.FIREWORK_ROCKET; // Rocket kit
                case 4 -> materialToDisplay = Material.BOW; // Rocket kit
                case 50 -> materialToDisplay = Material.SHULKER_BOX; // Random kit
                default -> materialToDisplay = Material.CRAFTING_TABLE; // Naked kit
            };

        } else if (type.equalsIgnoreCase("difficulty")) {
            return switch (ultimateBingo.difficulty) {
                case "easy" -> materialToDisplay = Material.COPPER_INGOT;
                case "hard" -> materialToDisplay = Material.NETHERITE_INGOT;
                case "random" -> materialToDisplay = Material.DIAMOND;
                default -> materialToDisplay = Material.IRON_INGOT;

            };
        } else if (type.equalsIgnoreCase("cardsize")) {
            return switch (ultimateBingo.cardSize) {
                case "small" -> materialToDisplay = Material.PAPER;
                case "medium" -> materialToDisplay = Material.BOOK;
                case "random" -> materialToDisplay = Material.SUGAR_CANE;
                default -> materialToDisplay = Material.WRITABLE_BOOK;

            };

        } else if (type.equalsIgnoreCase("gamemode")) {
            if (ultimateBingo.gameMode.equalsIgnoreCase("speedrun")) {
                materialToDisplay = Material.DIAMOND_BOOTS;
            } else if (ultimateBingo.gameMode.equalsIgnoreCase("group")) {
                materialToDisplay = Material.ENDER_CHEST;
            } else if (ultimateBingo.gameMode.equalsIgnoreCase("teams")) {
                materialToDisplay = Material.RED_WOOL;
            } else if (ultimateBingo.gameMode.equalsIgnoreCase("brewdash")) {
                    materialToDisplay = Material.POTION;
            } else if (ultimateBingo.gameMode.equalsIgnoreCase("random")) {
                materialToDisplay = Material.LADDER;
            } else {
                materialToDisplay = Material.FURNACE;
            }
        } else if (type.equalsIgnoreCase("uniquecard")) {
            if (ultimateBingo.uniqueCard.equalsIgnoreCase("unique")) {
                materialToDisplay = Material.FILLED_MAP;
            } else if (ultimateBingo.uniqueCard.equalsIgnoreCase("random")) {
                materialToDisplay = Material.SUGAR;
            } else {
                materialToDisplay = Material.MAP;
            }
        } else if (type.equalsIgnoreCase("wincondition")) {
            if (ultimateBingo.fullCard.equalsIgnoreCase("full card")) {
                materialToDisplay = ultimateBingo.tickedItemMaterial;
            } else if (ultimateBingo.fullCard.equalsIgnoreCase("random")) {
                materialToDisplay = Material.BEACON;
            } else {
                materialToDisplay = Material.BLAZE_ROD;
            }
        } else if (type.equalsIgnoreCase("reveal")) {
            if (ultimateBingo.revealCards.equalsIgnoreCase("enabled")) {
                materialToDisplay = Material.SPYGLASS;
            } else if (ultimateBingo.revealCards.equalsIgnoreCase("random")) {
                    materialToDisplay = Material.MINECART;
            } else {
                materialToDisplay = Material.BLACK_CONCRETE;
            }
        }


        return materialToDisplay;


    }
}

