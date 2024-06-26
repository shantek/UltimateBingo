package io.shantek;

import io.shantek.managers.BingoManager;
import io.shantek.managers.SettingsManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BingoCommand implements CommandExecutor {
    UltimateBingo ultimateBingo;
    SettingsManager settingsManager;
    BingoManager bingoManager;
    String loadoutType = "Empty Inventory";

    public BingoCommand(UltimateBingo ultimateBingo, SettingsManager settingsManager, BingoManager bingoManager) {
        this.ultimateBingo = ultimateBingo;
        this.settingsManager = settingsManager;
        this.bingoManager = bingoManager;
    }

    //region Command functionality

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender instanceof Player player) {

            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("stop") && player.hasPermission("shantek.ultimatebingo.stop")) {
                    stopBingo(player, false);

                } else if (args[0].equalsIgnoreCase("reload") && player.hasPermission("shantek.ultimatebingo.settings")) {
                    ultimateBingo.configFile.reloadConfigFile();
                    player.sendMessage(ChatColor.GREEN + "Bingo config file reloaded.");

                } else if (args[0].equalsIgnoreCase("gui") && player.hasPermission("shantek.ultimatebingo.play")) {

                    // Lets check if the bingoitems.yml file contains enough items in each category to start a game

                    // Define the minimum requirements for each difficulty level
                    int[] minimumRequirements = {15, 15, 10, 10, 5};
                    boolean foundError = false;

                    // Get the materials map
                    Map<Integer, List<Material>> materials = ultimateBingo.getMaterialList().getMaterials();

                    // Check each difficulty level
                    for (int difficulty = 1; difficulty <= 5; difficulty++) {
                        List<Material> difficultyMaterials = materials.get(difficulty);
                        int requiredItems = minimumRequirements[difficulty - 1];

                        if (difficultyMaterials.size() < requiredItems) {
                            player.sendMessage(ChatColor.WHITE + "Group " + difficulty + " requires " + requiredItems + " items, found " + difficultyMaterials.size() + ".");
                            foundError = true;
                        }
                    }

                    // If any error is found, print a final message
                    if (foundError) {
                        player.sendMessage(ChatColor.RED + "The bingoitems.yml file must not be manually modified. Please delete the file and reboot or manually add enough items to each category using /bingo settings for the game to begin.");
                    } else {

                        if (ultimateBingo.bingoStarted) {
                            player.sendMessage(ChatColor.RED + "A bingo game is in progress. Finish the game or use /bingo stop");
                        } else {
                            player.openInventory(ultimateBingo.bingoGameGUIManager.createGameGUI(player));
                        }

                    }

                } else if (args[0].equalsIgnoreCase("info")) {

                    // Work out the game time to display
                    String timeLimitString;
                    if (ultimateBingo.gameTime == 0) {
                        timeLimitString = "Unlimited Time";
                    } else {

                      if (ultimateBingo.bingoStarted) {
                          // Work out how long is left and display it here if the game is active
                          timeLimitString = ultimateBingo.gameTime + " minutes";

                        } else {
                            // Game isn't active, just show the preset time limit
                            timeLimitString = ultimateBingo.gameTime + " minutes";

                        }
                    }


                    // This may be removed in the near future and implemented in to the bingo card?
                    player.sendMessage(ChatColor.WHITE + "Bingo is currently set up with the following configuration:");
                    player.sendMessage(ChatColor.GREEN + "Difficulty: " + ChatColor.YELLOW + ultimateBingo.currentDifficulty.toUpperCase());
                    player.sendMessage(ChatColor.GREEN + "Card type: " + ChatColor.YELLOW + ultimateBingo.currentCardSize.toUpperCase() + "/" + (ultimateBingo.currentUniqueCard ? "UNIQUE" : "IDENTICAL"));
                    player.sendMessage(ChatColor.GREEN + "Game mode: " + ChatColor.YELLOW + ultimateBingo.currentGameMode.toUpperCase());
                    player.sendMessage(ChatColor.GREEN + "Win condition: " + ChatColor.YELLOW + (ultimateBingo.currentFullCard ? "FULL CARD" : "BINGO"));
                    player.sendMessage(ChatColor.GREEN + "Time limit: " + ChatColor.YELLOW + (timeLimitString));
                } else if (args[0].equalsIgnoreCase("settings") && player.hasPermission("shantek.ultimatebingo.settings")) {
                    ultimateBingo.getMaterialList().createMaterials();
                    Inventory settingsGUI = settingsManager.createSettingsGUI(player);

                    player.openInventory(settingsGUI);

                } else if (!player.hasPermission("shantek.ultimatebingo.start") && args[0].equalsIgnoreCase("start")
                        || !player.hasPermission("shantek.ultimatebingo.stop") && args[0].equalsIgnoreCase("stop")
                        || args[0].equalsIgnoreCase("settings") && !player.hasPermission("shantek.ultimatebingo.settings")) {
                    player.sendMessage(ChatColor.RED + "You do not have permission to do that!");
                }

            } else {
                if (ultimateBingo.bingoStarted && ultimateBingo.bingoCardActive) {

                    if (!ultimateBingo.bingoManager.checkHasBingoCard(player)) {

                        // They aren't in the game, let's give them a card and let them join
                        ultimateBingo.bingoFunctions.resetIndividualPlayer(player, true);
                        ultimateBingo.bingoManager.joinGameInProgress(player);

                    }

                    player.openInventory(ultimateBingo.bingoPlayerGUIManager.createPlayerGUI(player));

                } else if (!ultimateBingo.bingoStarted) {
                    player.sendMessage(ChatColor.RED + "Bingo hasn't started yet!");
                }
            }
        }

        return false;
    }

    //endregion

    //region Start and stop the game

    public void startBingo(Player commandPlayer) {
        UltimateBingo plugin = UltimateBingo.getInstance();

        if (ultimateBingo.bingoStarted) {

            commandPlayer.closeInventory();
            commandPlayer.sendMessage(ChatColor.RED + "Bingo is already running!");

        } else {

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

            if (ultimateBingo.currentUniqueCard) {
                ultimateBingo.bingoManager.createUniqueBingoCards();
            } else {
                ultimateBingo.bingoManager.createBingoCards();
            }

            // Set game strings for countdown
            String cardType = ultimateBingo.currentUniqueCard ? "UNIQUE" : "IDENTICAL";
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
            }

            // Store a reference to all online players
            List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

            // Display initial messages
            onlinePlayers.forEach(player -> {

                // Freeze players
                player.setWalkSpeed(0);

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    player.sendTitle(ChatColor.YELLOW + cardType, ChatColor.WHITE + ultimateBingo.currentCardSize.toUpperCase() + ", " + ultimateBingo.currentDifficulty.toUpperCase(), 10, 40, 10);
                }, 20);

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    player.sendTitle(ChatColor.YELLOW + bingoType, ChatColor.WHITE + "REVEAL MODE " + revealType, 10, 40, 10);
                }, 80);

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    player.sendTitle(ChatColor.YELLOW + ultimateBingo.currentGameMode.toUpperCase(), ChatColor.WHITE + loadoutType.toUpperCase() , 10, 40, 10);
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
                    player.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "GO!", "", 10, 20, 10);
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);

                    // Unfreeze players
                    player.removePotionEffect(PotionEffectType.SLOW);
                    player.setWalkSpeed(0.2f); // Default walk speed
                }, 290); // 1.5 seconds after "1"


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

                    Bukkit.broadcastMessage(ChatColor.GREEN + "Traditional bingo - collect items to mark them off your card!");

                    if (ultimateBingo.currentFullCard) {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Get a full card to win! " + timeLimitString);
                    } else {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Get a single row to win! " + timeLimitString);
                    }
                } else if (ultimateBingo.currentGameMode.equalsIgnoreCase("speedrun")) {

                    Bukkit.broadcastMessage(ChatColor.GREEN + "Speed run - Hunger/health resets with each item you tick off!");


                    if (ultimateBingo.currentFullCard) {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Get a full card to win! " + timeLimitString);
                    } else {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Get a single row to win! " + timeLimitString);
                    }
                }

            }, 350);

            // Game still active? If so, let's start it
            ultimateBingo.playedSinceReboot = true;

            // Get all online players as a List and scatter/teleport them all close together
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            ultimateBingo.bingoFunctions.safeScatterPlayers(players, ultimateBingo.bingoSpawnLocation, 5);

            // Handle player teleportation and give bingo cards after the countdown
            onlinePlayers.forEach(player -> {

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    ultimateBingo.bingoFunctions.giveBingoCard(player);
                    ultimateBingo.bingoCardActive = true;

                    // Equip the player loadout inventory
                    if (ultimateBingo.currentLoadoutType > 0) {
                        ultimateBingo.bingoFunctions.equipLoadoutGear(player, ultimateBingo.currentLoadoutType);
                    }

                    // Also give them night vision
                    if (ultimateBingo.currentGameMode.equals("speedrun")) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false, true));
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
            Bukkit.broadcastMessage(ChatColor.GREEN + "Game duration: " + gameDuration);

        }, 80L);  // Delay specified in ticks (80 ticks = 4 seconds)


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
        ultimateBingo.bingoSpawnLocation = null;

        // Schedule a delayed task to run after 2 seconds (40 ticks)
        Bukkit.getScheduler().runTaskLater(ultimateBingo, () -> {
            ultimateBingo.bingoFunctions.resetPlayers();
            ultimateBingo.bingoFunctions.despawnAllItems();

            // Give them a new bingo card to check the results, only if there are results to see
            if (!bingoManager.getBingoGUIs().isEmpty()) {

                ultimateBingo.bingoFunctions.giveBingoCardToAllPlayers();

            }

        }, 40L);  // Delay specified in ticks (40 ticks = 2 seconds)
    }

    public void stopBingo(Player sender, boolean gameCompleted) {

        if (!ultimateBingo.bingoStarted && !gameCompleted) {

            sender.sendMessage(ChatColor.RED + "Bingo hasn't started yet! Start with /bingo start");

        } else {

            // Cancel any tasks that are currently scheduled
            Bukkit.getScheduler().cancelTasks(ultimateBingo);


            if (!gameCompleted) {
                sender.sendMessage(ChatColor.RED + "Bingo has been stopped!");
            } else {

                // Show how long the game ran for
                Bukkit.getScheduler().runTaskLater(ultimateBingo, () -> {

                    long duration = System.currentTimeMillis() - ultimateBingo.gameStartTime;

                    // Calculate and display the game duration
                    String gameDuration = ultimateBingo.bingoFunctions.formatAndShowGameDuration(duration);
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Game duration: " + gameDuration);

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
        ultimateBingo.bingoSpawnLocation = null;

        // Schedule a delayed task to run after 2 seconds (40 ticks)
        Bukkit.getScheduler().runTaskLater(ultimateBingo, () -> {
            ultimateBingo.bingoFunctions.resetPlayers();
            ultimateBingo.bingoFunctions.despawnAllItems();

            // Give them a new bingo card to check the results, only if there are results to see
            if (!bingoManager.getBingoGUIs().isEmpty()) {

                ultimateBingo.bingoFunctions.giveBingoCardToAllPlayers();

            }

        }, 40L);  // Delay specified in ticks (40 ticks = 2 seconds)
    }

    //endregion

    //region Opening bingo cards

    public void openBingo(Player sender) {
        if (bingoManager.getBingoGUIs() != null && !bingoManager.getBingoGUIs().isEmpty()) {
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

    //endregion

}
