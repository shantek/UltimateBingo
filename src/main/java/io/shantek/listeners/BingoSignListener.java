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

        for (String key : config.getConfigurationSection("signs").getKeys(false)) {
            Location loc = parseLocation(config.getString("signs." + key));
            if (loc != null) {
                signLocations.put(key, loc);
            }
        }
        startButtonLocation = parseLocation(config.getString("startbutton"));
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

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getLocation().equals(startButtonLocation)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.YELLOW + "Game will start in 5 seconds...");
            Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.bingoCommand.startBingo(player), 100L);
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
                updateSign(setting, String.valueOf(plugin.bingoFunctions.toggleLoadout()));
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

    private void updateSign(String setting, String textToUpdate) {
        if (!signLocations.containsKey(setting)) return;
        Location loc = signLocations.get(setting);
        Block block = loc.getBlock();
        if (!(block.getState() instanceof Sign)) return;

        Sign sign = (Sign) block.getState();
        sign.setLine(1, ChatColor.BOLD + ChatColor.GOLD.toString() + setting.toUpperCase());
        sign.setLine(2, ChatColor.WHITE + textToUpdate.toUpperCase());
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