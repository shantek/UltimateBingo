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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class BingoCommand implements CommandExecutor {
    private final UltimateBingo ultimateBingo;
    private final SettingsManager settingsManager;
    private final BingoManager bingoManager;
    private final InGameConfigManager inGameConfigManager;
    String loadoutType = "Empty Inventory";

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
                if (targetBlock == null || (!targetBlock.getType().name().contains("SIGN") && !targetBlock.getType().name().contains("BUTTON"))) {
                    player.sendMessage(ChatColor.RED + "You must be looking at a valid sign or button!");
                    return true;
                }

                String settingName = args[1];
                Location targetLocation = targetBlock.getLocation();

                boolean validSign = true;
                for (Map.Entry<String, Location> entry : ultimateBingo.bingoFunctions.signLocations.entrySet()) {
                    if (targetBlock.getLocation().equals(entry.getValue())) {

                        player.sendMessage(ChatColor.YELLOW + "Sign already used for " + entry.getKey());
                        validSign = false;
                    }
                }
                if (validSign) {
                    if (settingOptions.containsKey(settingName)) {
                        ultimateBingo.inGameConfigManager.saveSignLocation(settingName, targetLocation);
                        player.sendMessage(ChatColor.GREEN + "Sign for " + settingName + " set successfully!");
                        ultimateBingo.inGameConfigManager.loadSignLocations();
                    } else if (settingName.equalsIgnoreCase("startbutton")) {
                        ultimateBingo.inGameConfigManager.saveButtonLocation(targetLocation);
                        player.sendMessage(ChatColor.GREEN + "Start button set successfully!");
                        ultimateBingo.inGameConfigManager.loadSignLocations();
                    } else {
                        player.sendMessage(ChatColor.RED + "Invalid setting name.");
                    }
                }
                return true;

            } else if (args[0].equalsIgnoreCase("remove") && player.hasPermission("shantek.ultimatebingo.settings")) {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /bingo remove <signType|startbutton>");
                    return true;
                }

                String settingName = args[1];

                if (settingName.equalsIgnoreCase("startbutton")) {
                    if (ultimateBingo.bingoFunctions.startButtonLocation == null) {
                        player.sendMessage(ChatColor.RED + "The start button hasn't been set up.");
                    } else {
                        ultimateBingo.bingoFunctions.removeButton();
                        player.sendMessage(ChatColor.GREEN + "The start button has been removed.");
                    }
                } else if (ultimateBingo.bingoFunctions.signLocations.containsKey(settingName)) {
                    ultimateBingo.bingoFunctions.removeSign(settingName);
                    player.sendMessage(ChatColor.GREEN + "The sign for " + settingName + " has been removed.");
                } else {
                    player.sendMessage(ChatColor.RED + "The sign for " + settingName + " hasn't been set up.");
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
            } else if (args[0].equalsIgnoreCase("info")) {

                if (ultimateBingo.bingoStarted) {

                    // Work out the game time to display
                    String timeLimitString;
                    if (ultimateBingo.gameTime == 0) {
                        timeLimitString = "Unlimited Time";
                    } else {

                        // Calculate remaining time
                        long elapsedTime = System.currentTimeMillis() - ultimateBingo.gameStartTime;
                        long remainingTimeMillis = (long) ultimateBingo.gameTime * 60 * 1000 - elapsedTime;
                        long remainingMinutes = remainingTimeMillis / (60 * 1000);

                        // Work out how long is left and display it here if the game is active
                        timeLimitString = ultimateBingo.gameTime + " minutes (" + remainingMinutes + " remaining)";

                    }

                    // This may be removed in the near future and implemented in to the bingo card?
                    player.sendMessage(ChatColor.WHITE + "Bingo is currently set up with the following configuration:");
                    if (ultimateBingo.currentDifficulty == null) {
                        player.sendMessage(ChatColor.GREEN + "Difficulty: " + ChatColor.YELLOW + "N/A");
                    } else {
                        player.sendMessage(ChatColor.GREEN + "Difficulty: " + ChatColor.YELLOW + ultimateBingo.currentDifficulty.toUpperCase());
                    }
                    player.sendMessage(ChatColor.GREEN + "Card type: " + ChatColor.YELLOW + ultimateBingo.currentCardSize.toUpperCase() + "/" + (ultimateBingo.currentUniqueCard ? "UNIQUE" : "IDENTICAL"));
                    player.sendMessage(ChatColor.GREEN + "Game mode: " + ChatColor.YELLOW + ultimateBingo.currentGameMode.toUpperCase());
                    player.sendMessage(ChatColor.GREEN + "Win condition: " + ChatColor.YELLOW + (ultimateBingo.currentFullCard ? "FULL CARD" : "BINGO"));
                    player.sendMessage(ChatColor.GREEN + "Time limit: " + ChatColor.YELLOW + (timeLimitString));
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Bingo isn't currently running!");
                }
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
        UltimateBingo plugin = UltimateBingo.getInstance();

        if (ultimateBingo.bingoStarted) {

            commandPlayer.closeInventory();
            commandPlayer.sendMessage(ChatColor.RED + "Bingo is already running!");

        } else {

            // Clear the player list
            ultimateBingo.bingoFunctions.clearPlayers();

            // Clear any data prior to the new game
            bingoManager.clearData();

            // Set the time that the game started
            ultimateBingo.gameStartTime = System.currentTimeMillis();

            // Clean up and reset game environment
            ultimateBingo.bingoFunctions.despawnAllItems();
            ultimateBingo.bingoStarted = true;
            ultimateBingo.bingoFunctions.resetPlayers();
            ultimateBingo.bingoFunctions.resetTimeAndWeather();

            // Configure game based on card size
            String cardSize = ultimateBingo.currentCardSize;
            switch (cardSize) {
                case "small":
                    ultimateBingo.bingoManager.slots = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30, 37, 38, 39};
                    ultimateBingo.bingoManager.setBingoCards(9);
                    break;
                case "medium":
                    ultimateBingo.bingoManager.slots = new int[]{10, 11, 12, 13, 19, 20, 21, 22, 28, 29, 30, 31, 37, 38, 39, 40};
                    ultimateBingo.bingoManager.setBingoCards(16);
                    break;
                case "large":
                    ultimateBingo.bingoManager.slots = new int[]{10, 11, 12, 13, 14, 19, 20, 21, 22, 23, 28, 29, 30, 31, 32, 37, 38, 39, 40, 41, 46, 47, 48, 49, 50};
                    ultimateBingo.bingoManager.setBingoCards(25);
                    break;
            }
            ultimateBingo.getMaterialList().createMaterials();

            if (ultimateBingo.currentGameMode.equalsIgnoreCase("group")) {
                ultimateBingo.bingoManager.createGroupBingoCard();
            } else if (ultimateBingo.currentGameMode.equalsIgnoreCase("teams")) {
                ultimateBingo.bingoManager.createTeamBingoCards();
                ultimateBingo.bingoFunctions.assignTeams();
            } else if (ultimateBingo.currentUniqueCard) {
                ultimateBingo.bingoManager.createUniqueBingoCards();
            } else {
                ultimateBingo.bingoManager.createBingoCards();
            }

            // Set game strings for countdown
            String cardType;
            if (ultimateBingo.currentGameMode.equalsIgnoreCase("group")) {
                // Always print shared for a group game
                cardType = "SHARED";
            } else {
                cardType = ultimateBingo.currentUniqueCard ? "UNIQUE" : "IDENTICAL";
            }
            String bingoType = ultimateBingo.currentFullCard ? "FULL CARD" : "SINGLE ROW";
            String revealType = ultimateBingo.currentRevealCards ? "ENABLED" : "DISABLED";

            if (ultimateBingo.currentLoadoutType == 0) {
                loadoutType = "Naked Kit";
            } else if (ultimateBingo.currentLoadoutType == 1) {
                loadoutType = "Starter Kit";
            } else if (ultimateBingo.currentLoadoutType == 2) {
                loadoutType = "Boat Kit";
            } else if (ultimateBingo.currentLoadoutType == 3) {
                loadoutType = "Flying Kit";
            } else if (ultimateBingo.currentLoadoutType == 4) {
                loadoutType = "Archer Kit";
            }

            // Store a reference to all online players
            List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

            // Display initial messages
            onlinePlayers.forEach(player -> {

                boolean activePlayer = true;

                // Check if multi world bingo is enabled and they're in the bingo world
                if (ultimateBingo.multiWorldServer && !player.getWorld().getName().equalsIgnoreCase(ultimateBingo.bingoWorld.toLowerCase())) {
                    activePlayer = false;

                }

                if (activePlayer) {

                    // Freeze players
                    player.setWalkSpeed(0);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        player.sendTitle(ChatColor.YELLOW + cardType, ChatColor.WHITE + ultimateBingo.currentCardSize.toUpperCase() + ", " + ultimateBingo.currentDifficulty.toUpperCase(), 10, 40, 10);
                    }, 20);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        player.sendTitle(ChatColor.YELLOW + bingoType, ChatColor.WHITE + "REVEAL MODE " + revealType, 10, 40, 10);
                    }, 80);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        player.sendTitle(ChatColor.YELLOW + ultimateBingo.currentGameMode.toUpperCase(), ChatColor.WHITE + loadoutType.toUpperCase(), 10, 40, 10);
                    }, 140);

                    // Countdown with chimes, with bold and colorful text
                    for (int i = 3; i > 0; i--) {
                        final int count = i;


                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            player.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + String.valueOf(count), "", 10, 20, 10);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                        }, 200 + 30 * (3 - count)); // Countdown starts at 5 seconds

                    }
                    // Final "GO!" message and chime, bold and green
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

                        if (ultimateBingo.currentGameMode.equalsIgnoreCase("teams") && ultimateBingo.bingoFunctions.getTeam(player).equalsIgnoreCase("yellow")) {
                            player.sendTitle(ChatColor.YELLOW + "" + ChatColor.BOLD + "GO YELLOW!", "", 10, 20, 10);
                        } else if (ultimateBingo.currentGameMode.equalsIgnoreCase("teams") && ultimateBingo.bingoFunctions.getTeam(player).equalsIgnoreCase("red")) {
                            player.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "GO RED!", "", 10, 20, 10);
                        } else if (ultimateBingo.currentGameMode.equalsIgnoreCase("teams") && ultimateBingo.bingoFunctions.getTeam(player).equalsIgnoreCase("blue")) {
                            player.sendTitle(ChatColor.BLUE + "" + ChatColor.BOLD + "GO BLUE!", "", 10, 20, 10);
                        } else {
                            player.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "GO!", "", 10, 20, 10);
                        }

                        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);

                        // Unfreeze players
                        player.removePotionEffect(PotionEffectType.SLOW);
                        player.setWalkSpeed(0.2f); // Default walk speed

                        if (ultimateBingo.currentGameMode.equalsIgnoreCase("teams")) {
                            ultimateBingo.bingoFunctions.notifyActivePlayers(player);
                        }
                    }, 290); // 1.5 seconds after "1"

                }
            });

            // Set the game timer for ending the game and game perks if enabled
            ultimateBingo.bingoFunctions.setGameTimer();

            // Delayed broadcast with the win condition
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

                String timeLimitString;
                if (ultimateBingo.gameTime == 0) {
                    timeLimitString = "Time Limit: Unlimited Time";
                } else {
                    timeLimitString = "Time Limit: " + ultimateBingo.gameTime + " minutes";
                }

                if (ultimateBingo.currentGameMode.equalsIgnoreCase("traditional")) {

                    ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Traditional bingo - collect items to mark them off your card!");

                    if (ultimateBingo.currentFullCard) {
                        ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Get a full card to win! " + timeLimitString);
                    } else {
                        ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Get a single row to win! " + timeLimitString);
                    }
                } else if (ultimateBingo.currentGameMode.equalsIgnoreCase("speedrun")) {

                    ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Speed run - Hunger/health resets with each item you tick off!");

                    if (ultimateBingo.currentFullCard) {
                        ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Get a full card to win! " + timeLimitString);
                    } else {
                        ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Get a single row to win! " + timeLimitString);
                    }
                } else if (ultimateBingo.currentGameMode.equalsIgnoreCase("group")) {

                    ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Group mode - Work as a team to get bingo!");

                    if (ultimateBingo.currentFullCard) {
                        ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Get a full card to win! " + timeLimitString);
                    } else {
                        ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Get a single row to win! " + timeLimitString);
                    }
                } else if (ultimateBingo.currentGameMode.equalsIgnoreCase("teams")) {

                    ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Team mode - Work as a team to get bingo!");

                    if (ultimateBingo.currentFullCard) {
                        ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Get a full card to win! " + timeLimitString);
                    } else {
                        ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Get a single row to win! " + timeLimitString);
                    }
                } else if (ultimateBingo.currentGameMode.equalsIgnoreCase("brewdash")) {

                    ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Brew dash - Hit players with a random potion for each item you tick off!");

                    if (ultimateBingo.currentFullCard) {
                        ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Get a full card to win! " + timeLimitString);
                    } else {
                        ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Get a single row to win! " + timeLimitString);
                    }
                }

            }, 350);

            // Game still active? If so, let's start it
            ultimateBingo.playedSinceReboot = true;

            // Get all online players as a List and scatter/teleport them all close together
            // Don't do this for a group game

            /*
            if (!ultimateBingo.currentGameMode.equalsIgnoreCase("teams")) {
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                ultimateBingo.bingoFunctions.safeScatterPlayers(players, ultimateBingo.bingoSpawnLocation, 5);
            }
            */

            // Will change this to safe scatter in teams in an update

            if (!Objects.equals(ultimateBingo.currentGameMode, "teams")) {
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                ultimateBingo.bingoFunctions.safeScatterPlayers(players, ultimateBingo.bingoSpawnLocation, 5);
            }


            // Handle player teleportation and give bingo cards after the countdown
            onlinePlayers.forEach(player -> {

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

                    if (ultimateBingo.bingoFunctions.isActivePlayer(player)) {
                        ultimateBingo.bingoFunctions.giveBingoCard(player);
                        ultimateBingo.bingoCardActive = true;

                        // Equip the player loadout inventory
                        if (ultimateBingo.currentLoadoutType > 0) {
                            ultimateBingo.bingoFunctions.equipLoadoutGear(player, ultimateBingo.currentLoadoutType);
                        }

                        // Also give them night vision
                        if (ultimateBingo.currentGameMode.equals("speedrun") || ultimateBingo.currentGameMode.equals("group") || ultimateBingo.currentGameMode.equalsIgnoreCase("teams")) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false, true));
                        }

                        // Add them to the player list
                        ultimateBingo.bingoFunctions.addPlayer(player.getUniqueId());
                    }
                }, 310); // 210 ticks = 10.5 seconds, just after the "GO!"

            });
        }
    }

    public void bingoGameOver() {

        // Cancel any tasks that are currently scheduled
        Bukkit.getScheduler().cancelTasks(ultimateBingo);

        // Show how long the game ran for
        Bukkit.getScheduler().runTaskLater(ultimateBingo, () -> {

            long duration = System.currentTimeMillis() - ultimateBingo.gameStartTime;

            // Calculate and display the game duration
            String gameDuration = ultimateBingo.bingoFunctions.formatAndShowGameDuration(duration);
            ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Game duration: " + gameDuration);

        }, 80L);  // Delay specified in ticks (80 ticks = 4 seconds)


        // Unfreeze the player - Run in case the game was stopped mid-countdown
        // This should still be run for players who aren't in the bingo world
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        onlinePlayers.forEach(player -> {
            player.removePotionEffect(PotionEffectType.SLOW);
            player.setWalkSpeed(0.2f); // Default walk speed
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        });

        ultimateBingo.bingoCardActive = false;
        ultimateBingo.bingoStarted = false;

        // Get all online players as a List and scatter/teleport them all close together
        // reset their inventory and state and despawn everything off the ground
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        ultimateBingo.bingoFunctions.safeScatterPlayers(players, ultimateBingo.bingoSpawnLocation, 5);
        ultimateBingo.bingoSpawnLocation = null;

        // Schedule a delayed task to run after 2 seconds (40 ticks)
        Bukkit.getScheduler().runTaskLater(ultimateBingo, () -> {
            ultimateBingo.bingoFunctions.resetPlayers();
            ultimateBingo.bingoFunctions.despawnAllItems();

            // Give them a new bingo card to check the results, only if there are results to see
            if (ultimateBingo.currentGameMode.equalsIgnoreCase("teams") || ultimateBingo.currentGameMode.equalsIgnoreCase("group")) {
                ultimateBingo.bingoFunctions.giveBingoCardToAllPlayers();
            } else {
                if (!bingoManager.getBingoGUIs().isEmpty()) {

                    ultimateBingo.bingoFunctions.giveBingoCardToAllPlayers();
                }
            }

        }, 40L);  // Delay specified in ticks (40 ticks = 2 seconds)

    }

    public void stopBingo(Player sender, boolean gameCompleted) {

        if (!ultimateBingo.bingoStarted && !gameCompleted) {

            sender.sendMessage(ChatColor.RED + "Bingo hasn't started yet! Start with /bingo start");

        } else {

            // Cancel any tasks that are currently scheduled
            Bukkit.getScheduler().cancelTasks(ultimateBingo);

            ultimateBingo.bingoButtonActive = true;
            if (!gameCompleted) {
                sender.sendMessage(ChatColor.RED + "Bingo has been stopped!");
            } else {

                // Show how long the game ran for
                Bukkit.getScheduler().runTaskLater(ultimateBingo, () -> {

                    long duration = System.currentTimeMillis() - ultimateBingo.gameStartTime;

                    // Calculate and display the game duration
                    String gameDuration = ultimateBingo.bingoFunctions.formatAndShowGameDuration(duration);
                    ultimateBingo.bingoFunctions.broadcastMessageToBingoPlayers(ChatColor.GREEN + "Game duration: " + gameDuration);

                }, 80L);  // Delay specified in ticks (80 ticks = 4 seconds)
            }
        }

        // Unfreeze the player - Run in case the game was stopped mid-countdown
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        onlinePlayers.forEach(player -> {

            player.removePotionEffect(PotionEffectType.SLOW);
            player.setWalkSpeed(0.2f); // Default walk speed
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);

        });

        ultimateBingo.bingoCardActive = false;
        ultimateBingo.bingoStarted = false;

        // Get all online players as a List and scatter/teleport them all close together
        // reset their inventory and state and despawn everything off the ground
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        ultimateBingo.bingoFunctions.safeScatterPlayers(players, ultimateBingo.bingoSpawnLocation, 5);


        // Schedule a delayed task to run after 2 seconds (40 ticks)
        Bukkit.getScheduler().runTaskLater(ultimateBingo, () -> {
            ultimateBingo.bingoFunctions.resetPlayers();
            ultimateBingo.bingoFunctions.despawnAllItems();

            // Give them a new bingo card to check the results, only if there are results to see
            if (ultimateBingo.currentGameMode.equalsIgnoreCase("group") || ultimateBingo.currentGameMode.equalsIgnoreCase("teams")) {

                ultimateBingo.bingoFunctions.giveBingoCardToAllPlayers();

            } else if (!bingoManager.getBingoGUIs().isEmpty()) {

                ultimateBingo.bingoFunctions.giveBingoCardToAllPlayers();

            }

            // Clear the previous spawn location
            ultimateBingo.bingoSpawnLocation = null;

        }, 40L);  // Delay specified in ticks (40 ticks = 2 seconds)
    }

    //endregion

    //region Opening bingo cards

    public void openBingo(Player sender) {
        if (ultimateBingo.currentGameMode.equalsIgnoreCase("group") && ultimateBingo.groupInventory != null) {
            sender.openInventory(ultimateBingo.groupInventory);

        } else if (ultimateBingo.currentGameMode.equalsIgnoreCase("teams")) {

            Inventory teamInventory = ultimateBingo.bingoFunctions.getTeamInventory(sender);

            if (teamInventory != null) {
                sender.openInventory(teamInventory);
            } else {
                sender.sendMessage(ChatColor.RED + "No team inventory found. Are you in an active game?");

            }


        } else if (bingoManager.getBingoGUIs() != null && !bingoManager.getBingoGUIs().isEmpty()) {
            if (bingoManager.getBingoGUIs().containsKey(sender.getUniqueId())) {
                sender.openInventory(bingoManager.getBingoGUIs().get(sender.getUniqueId()));
            } else {
                if (sender.hasPermission("shantek.ultimatebingo.start")) {
                    sender.sendMessage(ChatColor.RED + "You missed the opportunity to join Bingo! Use /bingo start if you want to create a new game.");
                }

                if (!sender.hasPermission("shantek.ultimatebingo.start")) {
                    sender.sendMessage(ChatColor.RED + "You missed the opportunity to join Bingo!");
                }
            }
        } else {
            if (sender.hasPermission("shantek.ultimatebingo.start")) {
                sender.sendMessage(ChatColor.RED + "Bingo hasn't started yet! Use /bingo start to start");
            }

            if (!sender.hasPermission("shantek.ultimatebingo.start")) {
                sender.sendMessage(ChatColor.RED + "Bingo hasn't started yet!");
            }

        }
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
