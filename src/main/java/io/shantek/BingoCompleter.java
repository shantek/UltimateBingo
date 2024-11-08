package io.shantek;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class BingoCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1 && commandSender instanceof Player player) {
            List<String> complete = new ArrayList<>();
            if (player.hasPermission("shantek.ultimatebingo.start")) {
                complete.add("gui");
            }
            if (player.hasPermission("shantek.ultimatebingo.stop")) {
                complete.add("stop");
            }
            if (player.hasPermission("shantek.ultimatebingo.settings")) {
                complete.add("settings");
                complete.add("reload");
            }

            complete.add("info");
            complete.add("card");
            complete.add("leaderboard");

            return StringUtil.copyPartialMatches(args[0], complete, new ArrayList<>());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("card")) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            return StringUtil.copyPartialMatches(args[1], playerNames, new ArrayList<>());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("leaderboard")) {
            List<String> cardSizes = new ArrayList<>();
            cardSizes.add("small");
            cardSizes.add("medium");
            cardSizes.add("large");
            return StringUtil.copyPartialMatches(args[1], cardSizes, new ArrayList<>());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("leaderboard")) {
            List<String> winConditions = new ArrayList<>();
            winConditions.add("single");
            winConditions.add("full");
            return StringUtil.copyPartialMatches(args[2], winConditions, new ArrayList<>());
        } else if (args.length == 4 && args[0].equalsIgnoreCase("leaderboard")) {
            List<String> difficulties = new ArrayList<>();
            difficulties.add("easy");
            difficulties.add("normal");
            difficulties.add("hard");
            return StringUtil.copyPartialMatches(args[3], difficulties, new ArrayList<>());
        } else if (args.length == 5 && args[0].equalsIgnoreCase("leaderboard")) {
            List<String> gameModes = new ArrayList<>();
            gameModes.add("traditional");
            gameModes.add("speedrun");
            gameModes.add("brewdash");
            gameModes.add("group");
            return StringUtil.copyPartialMatches(args[4], gameModes, new ArrayList<>());
        }

        return null;
    }
}
