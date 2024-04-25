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

            if (args.length == 1){
                if (args[0].equalsIgnoreCase("stop") && player.hasPermission("shantek.ultimatebingo.stop")){
                    stopBingo(player);
                }

                if (args[0].equalsIgnoreCase("start") && player.hasPermission("shantek.ultimatebingo.start")){
                    if (bingoStarted) {
                        player.sendMessage(ChatColor.RED + "Bingo has already started!");
                    } else {
                        ultimateBingo.bingoSpawnLocation = player.getLocation();
                        startBingo(false);
                    }
                }
                if (args[0].equalsIgnoreCase("startunique") && player.hasPermission("shantek.ultimatebingo.start")){
                    if (bingoStarted) {
                        player.sendMessage(ChatColor.RED + "Bingo has already started!");
                    } else {
                        ultimateBingo.bingoSpawnLocation = player.getLocation();
                        startBingo(true);
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

    public void startBingo(boolean uniquecard) {

        // Let's remove all items from the ground for a clean slate
        ultimateBingo.bingoFunctions.despawnAllItems();

        bingoStarted = true;
        bingoManager.setBingoCards(16);
        ultimateBingo.getMaterialList().createMaterials();

        // Reset player stats, inventory and the time of day
        ultimateBingo.bingoFunctions.resetPlayers();
        ultimateBingo.bingoFunctions.resetTimeAndWeather();

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
                player.sendMessage(ChatColor.GREEN + "Bingo has started using a " + ChatColor.YELLOW + "unique card" + ChatColor.YELLOW + "!");
                player.sendMessage(ChatColor.WHITE + "Use your Compass or type " + ChatColor.YELLOW + "/bingo" + ChatColor.WHITE + " to open your bingo card.");
            } else {
                player.sendMessage(ChatColor.GREEN + "Bingo has started using a " + ChatColor.YELLOW + "shared card" + ChatColor.YELLOW + "!");
                player.sendMessage(ChatColor.WHITE + "Use your Compass or type " + ChatColor.YELLOW + "/bingo" + ChatColor.WHITE + " to open your bingo card.");

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
        ultimateBingo.bingoManager.clearData();
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
