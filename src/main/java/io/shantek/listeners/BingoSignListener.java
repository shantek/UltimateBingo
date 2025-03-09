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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BingoSignListener implements Listener {
    private final UltimateBingo plugin;
    private final InGameConfigManager inGameConfigManager;

    public BingoSignListener(UltimateBingo plugin, InGameConfigManager inGameConfigManager) {
        this.plugin = plugin;
        this.inGameConfigManager = inGameConfigManager;

    }



    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) return;


        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {

            if (block.getState() instanceof Sign sign) {

                for (Map.Entry<String, Location> entry : plugin.bingoFunctions.signLocations.entrySet()) {
                    if (block.getLocation().equals(entry.getValue())) {

                        if (player.hasPermission("shantek.ultimatebingo.signs")) {

                            if (!plugin.bingoButtonActive) {
                                player.sendMessage(ChatColor.RED + "A bingo game is already active!");
                            } else {
                                plugin.bingoFunctions.updateSetting(entry.getKey(), player);
                            }
                            event.setCancelled(true);
                        } else {
                            player.sendMessage(ChatColor.RED + "You do not have permission to change settings!");
                            event.setCancelled(true);
                        }

                    }
                }
            }
        }

        if (player.hasPermission("shantek.ultimatebingo.signs")) {
            if (block.getType().name().endsWith("_BUTTON") && plugin.bingoFunctions.startButtonLocation != null) {
                Location clickedLocation = block.getLocation();

                // Ensure startButtonLocation has a valid world before comparing
                if (plugin.bingoFunctions.startButtonLocation.getWorld() == null || clickedLocation.getWorld() == null) {
                    return;
                }

                // Normalize locations to block coordinates (ignore decimal precision)
                if (Objects.equals(clickedLocation.getWorld(), plugin.bingoFunctions.startButtonLocation.getWorld()) &&
                        clickedLocation.getBlockX() == plugin.bingoFunctions.startButtonLocation.getBlockX() &&
                        clickedLocation.getBlockY() == plugin.bingoFunctions.startButtonLocation.getBlockY() &&
                        clickedLocation.getBlockZ() == plugin.bingoFunctions.startButtonLocation.getBlockZ()) {

                    if (!plugin.bingoButtonActive) {
                        player.sendMessage(ChatColor.RED + "A bingo game is already active!");
                    } else {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.YELLOW + "Game will start in 5 seconds...");

                        plugin.bingoButtonActive = false;

                        // Set all the game config ready to play
                        plugin.bingoGameGUIManager.setGameConfiguration();

                        plugin.bingoSpawnLocation = player.getLocation();

                        // Start game with delay
                        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.bingoCommand.startBingo(player), 100L);
                    }
                }
            }

        }
    }

}