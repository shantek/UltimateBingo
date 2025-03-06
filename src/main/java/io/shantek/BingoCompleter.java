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

    private static final List<String> SETTINGS_OPTIONS = List.of(
            "GameMode", "Difficulty", "CardSize", "Loadout",
            "RevealCards", "WinCondition", "CardType", "TimeLimit", "StartButton"
    );

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player)) {
            return null;
        }

        if (args.length == 1) {
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
            if (player.isOp()) {
                complete.add("set");
            }

            complete.add("info");
            complete.add("card");
            complete.add("leaderboard");

            return StringUtil.copyPartialMatches(args[0], complete, new ArrayList<>());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("card")) {
            List<String> playerNames = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                playerNames.add(onlinePlayer.getName());
            }
            return StringUtil.copyPartialMatches(args[1], playerNames, new ArrayList<>());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("leaderboard")) {
            List<String> cardSizes = List.of("small", "medium", "large");
            return StringUtil.copyPartialMatches(args[1], cardSizes, new ArrayList<>());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("leaderboard")) {
            List<String> winConditions = List.of("single", "full");
            return StringUtil.copyPartialMatches(args[2], winConditions, new ArrayList<>());
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("leaderboard")) {
            List<String> difficulties = List.of("easy", "normal", "hard");
            return StringUtil.copyPartialMatches(args[3], difficulties, new ArrayList<>());
        }

        if (args.length == 5 && args[0].equalsIgnoreCase("leaderboard")) {
            List<String> gameModes = List.of("traditional", "speedrun", "brewdash", "group", "teams");
            return StringUtil.copyPartialMatches(args[4], gameModes, new ArrayList<>());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("set") && player.isOp()) {
            return StringUtil.copyPartialMatches(args[1], SETTINGS_OPTIONS, new ArrayList<>());
        }

        return null;
    }
}
