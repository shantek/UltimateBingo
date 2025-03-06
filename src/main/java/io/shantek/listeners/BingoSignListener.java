package io.shantek.listeners;

import io.shantek.UltimateBingo;
import io.shantek.managers.InGameConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class BingoSignListener implements Listener {
    private final UltimateBingo plugin;
    private final InGameConfigManager inGameConfigManager;
    private final File configFile;
    private final FileConfiguration config;
    private final Map<String, Location> signLocations = new HashMap<>();
    private Location startButtonLocation;

    public BingoSignListener(UltimateBingo plugin, InGameConfigManager inGameConfigManager) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "ingameconfig.yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
        this.inGameConfigManager = inGameConfigManager;
        loadSignData();
    }

    private void loadSignData() {
        if (!configFile.exists()) {
            saveSignData();
            return;
        }

        if (config.contains("signs")) {
            for (String key : config.getConfigurationSection("signs").getKeys(false)) {
                Location loc = parseLocation(config.getString("signs." + key));
                if (loc != null) {
                    signLocations.put(key, loc);
                }
            }
        }

        // Load button location correctly
        if (config.contains("button.startbutton")) {
            startButtonLocation = parseLocation(config.getString("button.startbutton"));
            plugin.getLogger().info("Loaded start button at: " + startButtonLocation);
        } else {
            plugin.getLogger().warning("No start button found in config!");
        }
    }


    private void saveSignData() {
        for (Map.Entry<String, Location> entry : signLocations.entrySet()) {
            config.set("signs." + entry.getKey(), locationToString(entry.getValue()));
        }
        config.set("startbutton", locationToString(startButtonLocation));
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save ingameconfig.yml");
        }
    }

    private Location parseLocation(String locString) {
        if (locString == null || locString.isEmpty()) return null;
        String[] parts = locString.split(",");
        if (parts.length != 4) return null;
        return new Location(Bukkit.getWorld(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]));
    }

    private String locationToString(Location loc) {
        return loc == null ? "" : loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) return;


        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {

            if (block.getState() instanceof Sign sign) {

                for (Map.Entry<String, Location> entry : signLocations.entrySet()) {
                    if (block.getLocation().equals(entry.getValue())) {

                        if (player.hasPermission("shantek.bingo.signs")) {
                            updateSetting(entry.getKey(), player);
                        } else {
                            player.sendMessage(ChatColor.RED + "You do not have permission to change settings!");
                        }
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        if (player.hasPermission("shantek.bingo.signs")) {
            if (block.getType().name().endsWith("_BUTTON") && startButtonLocation != null) {
                Location clickedLocation = block.getLocation();

                // Debugging: Print locations for checking
                player.sendMessage(ChatColor.YELLOW + "DEBUG: Clicked Button Location: " + clickedLocation);
                player.sendMessage(ChatColor.YELLOW + "DEBUG: Config Button Location: " + startButtonLocation);

                // Ensure startButtonLocation has a valid world before comparing
                if (startButtonLocation.getWorld() == null || clickedLocation.getWorld() == null) {
                    return;
                }

                // Normalize locations to block coordinates (ignore decimal precision)
                if (Objects.equals(clickedLocation.getWorld(), startButtonLocation.getWorld()) &&
                        clickedLocation.getBlockX() == startButtonLocation.getBlockX() &&
                        clickedLocation.getBlockY() == startButtonLocation.getBlockY() &&
                        clickedLocation.getBlockZ() == startButtonLocation.getBlockZ()) {

                    if (!plugin.bingoButtonActive) {
                        player.sendMessage(ChatColor.RED + "A bingo game is already active!");
                    } else {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.YELLOW + "Game will start in 5 seconds...");

                        plugin.bingoButtonActive = false;

                        // Start game with delay
                        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.bingoCommand.startBingo(player), 100L);
                    }
                }
            }



        }
    }

    private void updateSetting(String setting, Player player) {

        switch (setting.toLowerCase()) {

            case "gamemode":
                updateSign(setting, plugin.bingoFunctions.toggleGameMode());
                break;
            case "difficulty":
                updateSign(setting, plugin.bingoFunctions.toggleDifficulty());
                break;
            case "cardsize":
                updateSign(setting, plugin.bingoFunctions.toggleCardSize());
                break;
            case "loadout":
                updateLoadoutSign(setting, String.valueOf(plugin.bingoFunctions.toggleLoadout()));
                break;
            case "revealcards":
                updateSign(setting, plugin.bingoFunctions.toggleReveal());
                break;
            case "wincondition":
                updateSign(setting, plugin.bingoFunctions.toggleFullCard());
                break;
            case "cardtype":
                updateSign(setting, plugin.bingoFunctions.toggleUnique());
                break;
            case "timelimit":
                updateSign(setting, String.valueOf(plugin.bingoFunctions.toggleTimeLimit()));
                break;
        }

    }

    private void updateLoadoutSign(String setting, String textToUpdate) {
        if (!signLocations.containsKey(setting)) return;
        Location loc = signLocations.get(setting);
        Block block = loc.getBlock();
        if (!(block.getState() instanceof Sign)) return;

        String textMode = null;
        if (textToUpdate.equals("0")) {
            textMode = "NAKED KIT";
        } else if (textToUpdate.equals("1")) {
            textMode = "STARTER KIT";
        } else if (textToUpdate.equals("2")) {
            textMode = "BOAT KIT";
        } else if (textToUpdate.equals("3")) {
            textMode = "FLYING KIT";
        } else if (textToUpdate.equals("4")) {
            textMode = "ARCHER KIT";
        } else if (textToUpdate.equals("50")) {
            textMode = "RANDOM";
        }

        Sign sign = (Sign) block.getState();
        sign.setLine(1, ChatColor.BOLD + ChatColor.GOLD.toString() + setting.toUpperCase());
        sign.setLine(2, ChatColor.WHITE + textMode.toUpperCase());

        sign.setGlowingText(false);
        sign.update();
    }


    private void updateSign(String setting, String textToUpdate) {
        if (!signLocations.containsKey(setting)) return;
        Location loc = signLocations.get(setting);
        Block block = loc.getBlock();
        if (!(block.getState() instanceof Sign)) return;

        Sign sign = (Sign) block.getState();
        sign.setLine(1, ChatColor.BOLD + ChatColor.GOLD.toString() + setting.toUpperCase());
        sign.setLine(2, ChatColor.WHITE + textToUpdate.toUpperCase());

        sign.setGlowingText(false);
        sign.update();
    }

    private String getSettingValue(String setting) {
        return switch (setting.toLowerCase()) {
            case "gamemode" -> plugin.currentGameMode;
            case "difficulty" -> plugin.currentDifficulty;
            case "cardsize" -> plugin.currentCardSize;
            case "loadout" -> "Loadout " + plugin.currentLoadoutType;
            case "revealcards" -> plugin.currentRevealCards ? "Enabled" : "Disabled";
            case "wincondition" -> plugin.currentFullCard ? "Full Card" : "Bingo";
            case "cardtype" -> plugin.currentUniqueCard ? "Unique" : "Identical";
            case "timelimit" -> plugin.gameTime == 0 ? "Unlimited" : plugin.gameTime + " min";
            default -> "";
        };
    }
}