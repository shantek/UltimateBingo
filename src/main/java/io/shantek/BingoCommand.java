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
                        startBingo(false);
                    }
                }
                if (args[0].equalsIgnoreCase("startunique") && player.hasPermission("shantek.ultimatebingo.start")){
                    if (bingoStarted) {
                        player.sendMessage(ChatColor.RED + "Bingo has already started!");
                    } else {
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

        bingoStarted = true;
        bingoManager.setBingoCards(16);
        ultimateBingo.getMaterialList().createMaterials();

        bingoManager.resetPlayers();

        if (uniquecard) {
            bingoManager.createUniqueBingoCards();
        } else {
            bingoManager.createBingoCards();
        }
        for (Player target : Bukkit.getOnlinePlayers()) {
            target.sendMessage(ChatColor.GREEN + "Bingo has started! Check your bingo cards with /bingo!");
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
    }

    public void endGame() {
        bingoStarted = false;
        ultimateBingo.bingoManager.clearData();
        Bukkit.broadcastMessage(ChatColor.GREEN + " Bingo has ended. Thanks for playing!");
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
