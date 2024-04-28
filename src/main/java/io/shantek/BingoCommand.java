package io.shantek;

import io.shantek.managers.BingoManager;
import io.shantek.managers.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

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
                    stopBingo(player);
                }

                if (args[0].equalsIgnoreCase("start") && player.hasPermission("shantek.ultimatebingo.start")){
                    if (bingoStarted) {
                        player.sendMessage(ChatColor.RED + "Bingo has already started!");
                    } else {
                        boolean isUnique = false;
                        String size = "medium"; // Default size to medium
                        // Check if additional arguments are provided
                        if (args.length > 1) {
                            // Parse the second argument to determine if it's "unique" or "same"
                            if (args[1].equalsIgnoreCase("unique")) {
                                isUnique = true;
                            }
                        }

                        // Check if size argument is provided
                        if (args.length > 2) {
                            // Parse the third argument to determine the size
                            size = args[2].toLowerCase(); // Convert to lowercase for case-insensitive comparison
                            // Check if the parsed size is not valid, default it to medium

                        }

                        if (!size.equals("small") && !size.equals("medium") && !size.equals("large")) {
                            size = "medium";
                            ultimateBingo.cardSize = "medium";
                        } else {
                            ultimateBingo.cardSize = size;
                        }

                        ultimateBingo.bingoSpawnLocation = player.getLocation();
                        startBingo(isUnique, size);
                    }
                }

                if (args[0].equalsIgnoreCase("settings") && player.hasPermission("shantek.ultimatebingo.settings")){
                    ultimateBingo.getMaterialList().createMaterials();
                    Inventory settingsGUI = settingsManager.createSettingsGUI(player);

                    player.openInventory(settingsGUI);
                }

                if (!player.hasPermission("shantek.ultimatebingo.start") && args[0].equalsIgnoreCase("start")
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

    public void startBingo(boolean uniquecard, String size) {

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

        if (uniquecard) {
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

            if (uniquecard) {
                player.sendMessage(ChatColor.GREEN + "Bingo has started using a " + ChatColor.YELLOW + cardSize + " unique card" + ChatColor.YELLOW + "!");
                player.sendMessage(ChatColor.WHITE + "Interact with your bingo card to open it (or type " + ChatColor.YELLOW + "/bingo" + ChatColor.WHITE + ").");
            } else {
                player.sendMessage(ChatColor.GREEN + "Bingo has started using a " + ChatColor.YELLOW + size + " shared card" + ChatColor.YELLOW + "!");
                player.sendMessage(ChatColor.WHITE + "Interact with your bingo card to open it (or type " + ChatColor.YELLOW + "/bingo" + ChatColor.WHITE + ").");

            }
        }

    }

    public void stopBingo(Player sender){
        bingoStarted = false;
        if (bingoManager.getPlayerBingoCards() != null && bingoManager.getBingoGUIs() != null){
            bingoManager.clearData();
            sender.sendMessage(ChatColor.RED + "Bingo has been stopped!");
        } else {
            sender.sendMessage(ChatColor.RED + "Bingo hasn't started yet! Start with /bingo start");
        }

        // Bring everyone back to the bingo spawn, reset their inventory and state
        // and despawn everything off the ground
        teleportPlayers();
        ultimateBingo.bingoFunctions.resetPlayers();
        ultimateBingo.bingoSpawnLocation = null;
        ultimateBingo.bingoFunctions.despawnAllItems();
    }

    public void endGame() {
        bingoStarted = false;
        bingoManager.clearData();
        Bukkit.broadcastMessage(ChatColor.GREEN + " Bingo has ended. Thanks for playing!");

        // Bring everyone back to the bingo spawn, reset their inventory and state
        // and despawn everything off the ground
        teleportPlayers();
        ultimateBingo.bingoFunctions.resetPlayers();
        ultimateBingo.bingoSpawnLocation = null;
        ultimateBingo.bingoFunctions.despawnAllItems();
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
