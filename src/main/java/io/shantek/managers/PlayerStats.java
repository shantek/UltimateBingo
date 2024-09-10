package io.shantek.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStats {

    private final UUID playerUUID;
    private final Map<String, Integer> winsByCategory;
    private final Map<String, Integer> lossesByCategory;

    public PlayerStats(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.winsByCategory = new HashMap<>();
        this.lossesByCategory = new HashMap<>();
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void addResult(String cardSize, boolean fullCard, String difficulty, String gameMode, boolean won) {
        String key = cardSize + (fullCard ? "_fullCard" : "_singleRow") + "_" + difficulty + "_" + gameMode;

        if (won) {
            winsByCategory.put(key, winsByCategory.getOrDefault(key, 0) + 1);
        } else {
            lossesByCategory.put(key, lossesByCategory.getOrDefault(key, 0) + 1);
        }
    }

    public int getWins(String cardSize, boolean fullCard, String difficulty, String gameMode) {
        String key = cardSize + (fullCard ? "_fullCard" : "_singleRow") + "_" + difficulty + "_" + gameMode;
        return winsByCategory.getOrDefault(key, 0);
    }

    public int getLosses(String cardSize, boolean fullCard, String difficulty, String gameMode) {
        String key = cardSize + (fullCard ? "_fullCard" : "_singleRow") + "_" + difficulty + "_" + gameMode;
        return lossesByCategory.getOrDefault(key, 0);
    }


    public void setWins(String category, int value) {
        winsByCategory.put(category, value);
    }

    public void setLosses(String category, int value) {
        lossesByCategory.put(category, value);
    }

    public Map<String, Integer> getWinsByCategory() {
        return winsByCategory;
    }

    public Map<String, Integer> getLossesByCategory() {
        return lossesByCategory;
    }

    public int getTotalWins() {
        return winsByCategory.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getTotalLosses() {
        return lossesByCategory.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getTotalPlayed() {
        return winsByCategory.values().stream().mapToInt(Integer::intValue).sum() + lossesByCategory.values().stream().mapToInt(Integer::intValue).sum();
    }
}
