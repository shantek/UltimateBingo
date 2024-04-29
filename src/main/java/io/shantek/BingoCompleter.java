package io.shantek;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BingoCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1 && commandSender instanceof Player player) {
            List<String> complete = new ArrayList<>();
            if (player.hasPermission("shantek.ultimatebingo.start")) {
                complete.add("start");
            }
            if (player.hasPermission("shantek.ultimatebingo.stop")) {
                complete.add("stop");
            }
            if (player.hasPermission("shantek.ultimatebingo.settings")) {
                complete.add("settings");
            }

            return StringUtil.copyPartialMatches(args[0], complete, new ArrayList<>());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
            List<String> complete = new ArrayList<>();
            complete.add("identical");
            complete.add("unique");

            return StringUtil.copyPartialMatches(args[1], complete, new ArrayList<>());

        } else if (args.length == 3 && args[0].equalsIgnoreCase("start") && (args[1].equalsIgnoreCase("identical") || args[1].equalsIgnoreCase("unique"))) {
            List<String> complete = new ArrayList<>();
            complete.add("small");
            complete.add("medium");
            complete.add("large");

            return StringUtil.copyPartialMatches(args[2], complete, new ArrayList<>());

        } else if (args.length == 4 && args[0].equalsIgnoreCase("start") && (args[2].equalsIgnoreCase("small") || args[2].equalsIgnoreCase("medium") || args[2].equalsIgnoreCase("large"))) {
            List<String> complete = new ArrayList<>();
            complete.add("fullcard");
            complete.add("bingo");

            return StringUtil.copyPartialMatches(args[3], complete, new ArrayList<>());
        }

        return null;
    }

}
