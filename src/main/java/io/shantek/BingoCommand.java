package io.shantek;

import io.shantek.managers.BingoManager;
import io.shantek.managers.InGameConfigManager;
import io.shantek.managers.PlayerStats;
import io.shantek.managers.SettingsManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import java.util.*;

public class BingoCommand implements CommandExecutor {
    private final UltimateBingo ultimateBingo;
    private final SettingsManager settingsManager;
    private final BingoManager bingoManager;
    private final InGameConfigManager inGameConfigManager;

    private final Map<String, List<String>> settingOptions = Map.of(
            "GameMode", List.of("traditional", "speedrun", "brewdash", "group", "teams"),
            "Difficulty", List.of("easy", "normal", "hard"),
            "CardSize", List.of("small", "medium", "large"),
            "Loadout", List.of("Naked Kit", "Starter Kit", "Boat Kit", "Flying Kit", "Archer Kit"),
            "RevealCards", List.of("Enabled", "Disabled"),
            "WinCondition", List.of("Single Row", "Full Card"),
            "CardType", List.of("Identical", "Unique"),
            "TimeLimit", List.of("0", "5", "10", "15", "30", "60")
    );

    public BingoCommand(UltimateBingo ultimateBingo, SettingsManager settingsManager, BingoManager bingoManager, InGameConfigManager inGameConfigManager) {
        this.ultimateBingo = ultimateBingo;
        this.settingsManager = settingsManager;
        this.bingoManager = bingoManager;
        this.inGameConfigManager = inGameConfigManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("stop") && player.hasPermission("shantek.ultimatebingo.stop")) {
                if (ultimateBingo.multiWorldServer && !player.getWorld().getName().equalsIgnoreCase(ultimateBingo.bingoWorld.toLowerCase())) {
                    player.sendMessage(ChatColor.RED + "This command can only be run while in the bingo world.");
                } else {
                    stopBingo(player, false);
                }
                return true;
            } else if (args[0].equalsIgnoreCase("set") && player.isOp()) {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /bingo set <settingname|startbutton>");
                    return true;
                }

                Block targetBlock = player.getTargetBlockExact(5);
                if (targetBlock == null || (!targetBlock.getType().name().contains("SIGN") && targetBlock.getType() != Material.STONE_BUTTON)) {
                    player.sendMessage(ChatColor.RED + "You must be looking at a valid sign or button!");
                    return true;
                }

                String settingName = args[1];
                Location targetLocation = targetBlock.getLocation();

                if (settingOptions.containsKey(settingName)) {
                    inGameConfigManager.saveSignLocation(settingName, targetLocation);
                    player.sendMessage(ChatColor.GREEN + "Sign for " + settingName + " set successfully!");
                } else if (settingName.equalsIgnoreCase("startbutton")) {
                    inGameConfigManager.saveButtonLocation(targetLocation);
                    player.sendMessage(ChatColor.GREEN + "Start button set successfully!");
                } else {
                    player.sendMessage(ChatColor.RED + "Invalid setting name.");
                }
                return true;
            } else if (args[0].equalsIgnoreCase("reload") && player.hasPermission("shantek.ultimatebingo.settings")) {
                ultimateBingo.configFile.reloadConfigFile();
                player.sendMessage(ChatColor.GREEN + "Bingo config file reloaded.");
                return true;
            } else if (args[0].equalsIgnoreCase("leaderboard")) {
                if (args.length == 1) {
                    List<PlayerStats> topPlayersOverall = ultimateBingo.getLeaderboard().getTopPlayersOverall();
                    player.sendMessage(ChatColor.GREEN + "Top Players Overall:");
                    int rank = 1;
                    for (PlayerStats stats : topPlayersOverall) {
                        UUID playerUUID = stats.getPlayerUUID();
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
                        String playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : playerUUID.toString();
                        player.sendMessage(ChatColor.YELLOW + "#" + rank + ": " + playerName + " - " + stats.getTotalWins() + " wins, " + stats.getTotalPlayed() + " played");
                        rank++;
                        if (rank > 10) break;
                    }
                    if (rank == 1) player.sendMessage(ChatColor.YELLOW + "No players found for this category.");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("gui") && player.hasPermission("shantek.ultimatebingo.play")) {
                if (ultimateBingo.multiWorldServer && !player.getWorld().getName().equalsIgnoreCase(ultimateBingo.bingoWorld.toLowerCase())) {
                    player.sendMessage(ChatColor.RED + "This command can only be run while in the bingo world.");
                } else {
                    if (ultimateBingo.bingoStarted) {
                        player.sendMessage(ChatColor.RED + "A bingo game is in progress. Finish the game or use /bingo stop");
                    } else {
                        player.openInventory(ultimateBingo.bingoGameGUIManager.createGameGUI(player));
                    }
                }
                return true;
            } else if (args[0].equalsIgnoreCase("settings") && player.hasPermission("shantek.ultimatebingo.settings")) {
                ultimateBingo.getMaterialList().createMaterials();
                Inventory settingsGUI = settingsManager.createSettingsGUI(player);
                player.openInventory(settingsGUI);
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "You do not have permission to do that!");
                return true;
            }
        }

        if (ultimateBingo.bingoStarted && ultimateBingo.bingoCardActive) {
            if (ultimateBingo.multiWorldServer && !player.getWorld().getName().equalsIgnoreCase(ultimateBingo.bingoWorld.toLowerCase())) {
                player.sendMessage(ChatColor.RED + "This command can only be run while in the bingo world.");
            } else {
                if (ultimateBingo.currentGameMode.equalsIgnoreCase("group") || ultimateBingo.currentGameMode.equalsIgnoreCase("teams")) {
                    ultimateBingo.bingoFunctions.resetIndividualPlayer(player, true);
                    ultimateBingo.bingoManager.joinGameInProgress(player);
                } else if (!ultimateBingo.bingoManager.checkHasBingoCard(player)) {
                    ultimateBingo.bingoFunctions.resetIndividualPlayer(player, true);
                    ultimateBingo.bingoManager.joinGameInProgress(player);
                }
                player.openInventory(ultimateBingo.bingoPlayerGUIManager.createPlayerGUI(player));
            }
        } else if (!ultimateBingo.bingoStarted) {
            player.sendMessage(ChatColor.RED + "Bingo hasn't started yet!");
        }
        return false;
    }

//region Start and stop the game

    public void startBingo(Player commandPlayer) {
    }

    public void bingoGameOver() {
    }

    public void stopBingo(Player sender, boolean gameCompleted) {
    }

    //endregion

    //region Opening bingo cards

    public void openBingo(Player sender) {
    }


    public void openBingoOtherPlayer(Player sender, Player otherPlayer) {
        // Playing the reveal mode, all good to allow this functionality
        if (ultimateBingo.bingoManager.getBingoGUIs() != null && !ultimateBingo.bingoManager.getBingoGUIs().isEmpty()) {
            if (ultimateBingo.bingoManager.getBingoGUIs().containsKey(otherPlayer.getUniqueId())) {
                sender.openInventory(ultimateBingo.bingoManager.getBingoGUIs().get(otherPlayer.getUniqueId()));
            } else {
                // Couldn't find that players name in the list
                sender.sendMessage(ChatColor.RED + "Unable to find a bingo card for " + otherPlayer.getName());

            }
        } else {

            sender.sendMessage(ChatColor.RED + "Bingo hasn't started yet!");


        }

    }

    public void openBingoTeamCard(Player sender, Inventory inventory) {

        if (inventory == null) {
            sender.sendMessage(ChatColor.RED + "No team inventory found. Are you in an active game?");
        } else {
            // Close their existing inventory
            sender.closeInventory();

            // Open the desired team inventory
            sender.openInventory(inventory);
        }
    }

    //endregion
}
