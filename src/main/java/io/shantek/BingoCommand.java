package io.shantek;

import io.shantek.managers.BingoManager;
import io.shantek.managers.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

public class BingoCommand implements CommandExecutor {
    UltimateBingo ultimateBingo;
    SettingsManager settingsManager;
    BingoManager bingoManager;
    private boolean bingoStarted;
    public BingoCommand(UltimateBingo ultimateBingo, SettingsManager settingsManager, BingoManager bingoManager){
        this.ultimateBingo = ultimateBingo;
        this.settingsManager = settingsManager;
        this.bingoManager = bingoManager;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender instanceof Player player){

            if (args.length > 0){
                if (args[0].equalsIgnoreCase("stop") && player.hasPermission("shantek.ultimatebingo.stop")){
                    stopBingo(player, false);
                }

                else if (args[0].equalsIgnoreCase("start") && player.hasPermission("shantek.ultimatebingo.start")){
                    if (bingoStarted) {
                        player.sendMessage(ChatColor.RED + "Bingo has already started!");
                    } else {

                        ultimateBingo.bingoSpawnLocation = player.getLocation();
                        startBingo(player);
                    }
                }
                else if (args[0].equalsIgnoreCase("reload") && player.hasPermission("shantek.ultimatebingo.settings")) {
                    ultimateBingo.configFile.reloadConfigFile();
                    player.sendMessage(ChatColor.GREEN + "Bingo config file reloaded.");

                }
                else if (args[0].equalsIgnoreCase("cardsize") && player.hasPermission("shantek.ultimatebingo.configure")) {

                    // Check if size argument is provided
                    String cardSize = null;

                    if (args.length > 1) {
                        // Parse the third argument to determine the size
                        cardSize = args[1].toLowerCase(); // Convert to lowercase for case-insensitive comparison

                        // Update and save the card size
                        boolean cardSizeUpdated = false;

                        switch (cardSize) {
                            case "small":
                            case "medium":
                            case "large":
                                cardSizeUpdated = true;
                                ultimateBingo.cardSize = cardSize;
                                player.sendMessage(ChatColor.GREEN + "Bingo card size has been set to " + ChatColor.GREEN + cardSize.toUpperCase());
                                break;
                        }
                        ultimateBingo.configFile.saveConfig();

                        if (!cardSizeUpdated) {
                            player.sendMessage(ChatColor.RED + "Invalid Bingo card size. Please use SMALL, MEDIUM or LARGE.");
                        }
                    } else {
                        player.sendMessage(ChatColor.GREEN + "Bingo card size is currently set to " + ChatColor.YELLOW + ultimateBingo.cardSize.toUpperCase());
                    }

                }
                else if (args[0].equalsIgnoreCase("condition") && player.hasPermission("shantek.ultimatebingo.configure")){

                    // Check if additional arguments are provided
                    if (args.length > 1) {

                        switch (args[1]) {
                            case "fullcard":
                                ultimateBingo.fullCard = true;
                                player.sendMessage(ChatColor.GREEN + "Win condition set to " + ChatColor.YELLOW + "FULL CARD");
                                break;
                            case "bingo":
                                ultimateBingo.fullCard = false;
                                player.sendMessage(ChatColor.GREEN + "Win condition set to " + ChatColor.YELLOW + "BINGO");
                                break;
                            default:
                                player.sendMessage(ChatColor.RED + "Invalid game type. You can set this to full card or bingo.");
                        }


                    } else {
                        player.sendMessage(ChatColor.GREEN + "Card type is currently set to " + ChatColor.YELLOW + (ultimateBingo.fullCard ? "FULL CARD" : "BINGO"));
                    }
                }
                else if (args[0].equalsIgnoreCase("cardtype") && player.hasPermission("shantek.ultimatebingo.configure")) {

                    // Check if additional arguments are provided
                    if (args.length > 1) {

                        switch (args[1]) {
                            case "unique":
                                ultimateBingo.uniqueCard = true;
                                player.sendMessage(ChatColor.GREEN + "Card type has been set to " + ChatColor.YELLOW + "UNIQUE");
                                break;
                            case "identical":
                                ultimateBingo.uniqueCard = false;
                                player.sendMessage(ChatColor.GREEN + "Card type has been set to " + ChatColor.YELLOW + "IDENTICAL");
                                break;
                            default:
                                player.sendMessage(ChatColor.RED + "Invalid card type. Set this to UNIQUE if you want all players to have different cards or IDENTICAL for all players to have the same card.");
                        }

                    } else {
                        player.sendMessage(ChatColor.GREEN + "Card type is currently set to " + ChatColor.YELLOW + (ultimateBingo.fullCard ? "UNIQUE" : "IDENTICAL"));
                    }
                }
                else if (args[0].equalsIgnoreCase("difficulty") && player.hasPermission("shantek.ultimatebingo.configure")) {

                    // Check if additional arguments are provided
                    if (args.length > 1) {

                        switch (args[1]) {
                            case "easy":
                                ultimateBingo.difficulty = "easy";
                                player.sendMessage(ChatColor.GREEN + "Difficulty has been set to " + ChatColor.YELLOW + "EASY");
                                break;
                            case "normal":
                                ultimateBingo.difficulty = "normal";
                                player.sendMessage(ChatColor.GREEN + "Difficulty has been set to " + ChatColor.YELLOW + "NORMAL");
                                break;
                            case "hard":
                                ultimateBingo.difficulty = "hard";
                                player.sendMessage(ChatColor.GREEN + "Difficulty has been set to " + ChatColor.YELLOW + "HARD");
                                break;
                            default:
                                player.sendMessage(ChatColor.RED + "Invalid difficulty. You can set this to easy, normal or hard.");
                        }

                        ultimateBingo.configFile.saveConfig();
                    } else {
                        player.sendMessage(ChatColor.GREEN + "Difficulty is currently set to " + ChatColor.YELLOW + ultimateBingo.difficulty);
                    }
                }
                else if (args[0].equalsIgnoreCase("info")){
                    player.sendMessage(ChatColor.WHITE + "Bingo is currently set up with the following configuration:");
                    player.sendMessage(ChatColor.GREEN + "Difficulty: " + ChatColor.YELLOW + ultimateBingo.difficulty.toUpperCase());
                    player.sendMessage(ChatColor.GREEN + "Card type: " +  ChatColor.YELLOW + ultimateBingo.cardSize.toUpperCase() + "/" + (ultimateBingo.uniqueCard ? "UNIQUE" : "IDENTICAL"));
                    player.sendMessage(ChatColor.GREEN + "Win condition: " + ChatColor.YELLOW + (ultimateBingo.fullCard ? "FULL CARD" : "BINGO"));
                }

                else if (args[0].equalsIgnoreCase("settings") && player.hasPermission("shantek.ultimatebingo.settings")){
                    ultimateBingo.getMaterialList().createMaterials();
                    Inventory settingsGUI = settingsManager.createSettingsGUI(player);

                    player.openInventory(settingsGUI);
                }

                else if (!player.hasPermission("shantek.ultimatebingo.start") && args[0].equalsIgnoreCase("start")
                        || !player.hasPermission("shantek.ultimatebingo.stop") && args[0].equalsIgnoreCase("stop")
                        || args[0].equalsIgnoreCase("settings") && !player.hasPermission("shantek.ultimatebingo.settings")){
                    player.sendMessage(ChatColor.RED + "You do not have permission to do that!");
                }

            } else {
                if (bingoStarted){
                    openBingo(player);
                }
                if (!bingoStarted){
                    player.sendMessage(ChatColor.RED + "Bingo hasn't started yet!");
                }
            }
        }

        return false;
    }

    public void startBingo(Player commandPlayer) {

        // Let's remove all items from the ground for a clean slate
        ultimateBingo.bingoFunctions.despawnAllItems();
        bingoStarted = true;

        // Reset player stats, inventory and the time of day
        ultimateBingo.bingoFunctions.resetPlayers();
        ultimateBingo.bingoFunctions.resetTimeAndWeather();

        // Set the amount of slots we're using based on the card type
        String cardSize = ultimateBingo.cardSize;

        switch (cardSize) {
            case "small":
                bingoManager.slots = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30, 37, 38, 39};
                bingoManager.setBingoCards(9);
                ultimateBingo.getMaterialList().createMaterials();
                break;
            case "medium":
                bingoManager.slots = new int[]{10, 11, 12, 13, 19, 20, 21, 22, 28, 29, 30, 31, 37, 38, 39, 40};
                bingoManager.setBingoCards(16);
                ultimateBingo.getMaterialList().createMaterials();
                break;
            case "large":
                bingoManager.slots = new int[]{10, 11, 12, 13, 14, 19, 20, 21, 22, 23, 28, 29, 30, 31, 32, 37, 38, 39, 40, 41, 46, 47, 48, 49, 50};
                bingoManager.setBingoCards(25);
                ultimateBingo.getMaterialList().createMaterials();
                break;
        }

        // Set the new world spawn
        Location location = commandPlayer.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        // Execute the command as the player
        String commandString = "/setspawn " + x + " " + y + " " + z;
        Bukkit.dispatchCommand(commandPlayer, commandString);

        if (ultimateBingo.uniqueCard) {
            bingoManager.createUniqueBingoCards();
        } else {
            bingoManager.createBingoCards();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {

            // If we have a bingo start location, teleport the players here
            if (ultimateBingo.bingoSpawnLocation != null) {
                player.teleport(ultimateBingo.bingoSpawnLocation);
            }

            // Give them a bingo card
            ultimateBingo.bingoFunctions.giveBingoCard(player);

            // Work out the win condition
            String bingoType = "SINGLE ROW";
            if (ultimateBingo.fullCard) {
                bingoType = "FULL CARD";
            }

            // Work out the card type
            String cardType = "IDENTICAL";
            if (ultimateBingo.uniqueCard) {
                cardType = "UNIQUE";
            }

            player.sendMessage(ChatColor.GREEN + "Bingo has started with a " + ChatColor.YELLOW + cardSize.toUpperCase() + " " + ultimateBingo.difficulty.toUpperCase() + " " + cardType + ChatColor.GREEN + " card. Get a " + ChatColor.YELLOW + bingoType + ChatColor.GREEN + " to win!");
            player.sendMessage(ChatColor.WHITE + "Interact with your bingo card to open it (or type " + ChatColor.YELLOW + "/bingo" + ChatColor.WHITE + ").");

        }
    }

    public void stopBingo(Player sender, boolean gameCompleted){

        bingoManager.clearData();

        if (bingoStarted){
            if (!gameCompleted) { sender.sendMessage(ChatColor.RED + "Bingo has been stopped!"); }
        } else {
            if (!gameCompleted) {
                sender.sendMessage(ChatColor.RED + "Bingo hasn't started yet! Start with /bingo start");
            }
        }

        // Bring everyone back to the bingo spawn, reset their inventory and state
        // and despawn everything off the ground
        teleportPlayers();
        ultimateBingo.bingoFunctions.resetPlayers();
        ultimateBingo.bingoSpawnLocation = null;
        ultimateBingo.bingoFunctions.despawnAllItems();
        bingoStarted = false;
    }

    public void teleportPlayers()
    {
        for (Player target : Bukkit.getOnlinePlayers()) {

            // If we have a bingo start location, teleport the players here
            if (ultimateBingo.bingoSpawnLocation != null) {
                target.teleport(ultimateBingo.bingoSpawnLocation);
            }

        }
    }

    public void openBingo(Player sender){
        if (bingoManager.getBingoGUIs() != null  && !bingoManager.getBingoGUIs().isEmpty()){
            if (bingoManager.getBingoGUIs().containsKey(sender.getUniqueId())){
                sender.openInventory(bingoManager.getBingoGUIs().get(sender.getUniqueId()));
            } else {
                if (sender.hasPermission("shantek.ultimatebingo.start")){
                    sender.sendMessage(ChatColor.RED + "You missed the opportunity to join Bingo! Use /bingo start if you want to create a new game.");
                }

                if (!sender.hasPermission("shantek.ultimatebingo.start")){
                    sender.sendMessage(ChatColor.RED + "You missed the opportunity to join Bingo!");
                }
            }
        } else {
            if (sender.hasPermission("shantek.ultimatebingo.start")){
                sender.sendMessage(ChatColor.RED + "Bingo hasn't started yet! Use /bingo start to start");
            }

            if (!sender.hasPermission("shantek.ultimatebingo.start")){
                sender.sendMessage(ChatColor.RED + "Bingo hasn't started yet!");
            }

        }
    }
}
