package io.shantek;

import io.shantek.managers.PlayerStats;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Leaderboard {

    private final UltimateBingo plugin;
    private final File leaderboardFile;
    private FileConfiguration leaderboardConfig;

    private final Map<UUID, PlayerStats> playerStatsMap = new HashMap<>();

    public Leaderboard(UltimateBingo plugin) {
        this.plugin = plugin;
        this.leaderboardFile = new File(plugin.getDataFolder(), "leaderboard.yml");
        loadLeaderboardData();
    }

    public void loadLeaderboardData() {
        if (!leaderboardFile.exists()) {
            try {
                leaderboardFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        leaderboardConfig = YamlConfiguration.loadConfiguration(leaderboardFile);

        for (String key : leaderboardConfig.getKeys(false)) {
            UUID playerUUID = UUID.fromString(key);
            Map<String, Object> wins = leaderboardConfig.getConfigurationSection(key + ".wins").getValues(false);
            Map<String, Object> losses = leaderboardConfig.getConfigurationSection(key + ".losses").getValues(false);

            PlayerStats stats = new PlayerStats(playerUUID);

            // Correctly add the values for wins and losses
            wins.forEach((category, value) -> stats.setWins(category, (Integer) value));
            losses.forEach((category, value) -> stats.setLosses(category, (Integer) value));

            playerStatsMap.put(playerUUID, stats);
        }
    }

    public void saveLeaderboardData() {
        for (Map.Entry<UUID, PlayerStats> entry : playerStatsMap.entrySet()) {
            String key = entry.getKey().toString();
            leaderboardConfig.set(key + ".wins", entry.getValue().getWinsByCategory());
            leaderboardConfig.set(key + ".losses", entry.getValue().getLossesByCategory());
        }
        try {
            leaderboardConfig.save(leaderboardFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<PlayerStats> getTopPlayers(String cardSize, boolean fullCard, String difficulty, String gameMode) {
        List<PlayerStats> sortedStats = new ArrayList<>(playerStatsMap.values());
        sortedStats.sort((a, b) -> Integer.compare(b.getWins(cardSize, fullCard, difficulty, gameMode), a.getWins(cardSize, fullCard, difficulty, gameMode)));
        return sortedStats;
    }

    public List<PlayerStats> getTopPlayersOverall() {
        List<PlayerStats> sortedStats = new ArrayList<>(playerStatsMap.values());
        sortedStats.sort((a, b) -> Integer.compare(b.getTotalWins(), a.getTotalWins()));
        return sortedStats;
    }

    public void addGameResult(UUID playerUUID, String cardSize, boolean fullCard, String difficulty, String gameMode, boolean won) {
        PlayerStats stats = playerStatsMap.getOrDefault(playerUUID, new PlayerStats(playerUUID));
        stats.addResult(cardSize, fullCard, difficulty, gameMode, won);
        playerStatsMap.put(playerUUID, stats); // Correctly add stats to map
        saveLeaderboardData();
    }

    public PlayerStats getPlayerStats(UUID playerUUID) {
        return playerStatsMap.getOrDefault(playerUUID, new PlayerStats(playerUUID));
    }
}
