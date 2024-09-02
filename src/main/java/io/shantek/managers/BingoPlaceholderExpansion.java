package io.shantek.managers;

import io.shantek.UltimateBingo;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class BingoPlaceholderExpansion extends PlaceholderExpansion {

    private final UltimateBingo plugin;

    public BingoPlaceholderExpansion(UltimateBingo plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ultimatebingo";
    }

    @Override
    public @NotNull String getAuthor() {
        return "YourName";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        List<PlayerStats> topPlayersOverall = plugin.getLeaderboard().getTopPlayersOverall();

        // Check if params match one of the expected placeholders
        for (int i = 1; i <= 10; i++) {
            if (params.equalsIgnoreCase("overall_" + i + "_name")) {
                if (topPlayersOverall.size() >= i) {
                    UUID playerUUID = topPlayersOverall.get(i - 1).getPlayerUUID();
                    OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(playerUUID);
                    return offlinePlayer.getName();
                } else {
                    return "N/A";
                }
            }

            if (params.equalsIgnoreCase("overall_" + i + "_score")) {
                if (topPlayersOverall.size() >= i) {
                    return String.valueOf(topPlayersOverall.get(i - 1).getTotalWins());
                } else {
                    return "0";
                }
            }
        }

        return null; // Placeholder is unknown by the expansion
    }
}
